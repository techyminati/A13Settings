package com.google.android.settings.security;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.activityembedding.ActivityEmbeddingUtils;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.settings.external.SignatureVerifier;
import com.google.android.settings.security.SecurityContentManager;
import com.google.android.settings.security.SecurityWarning;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class SecurityContentManager implements LifecycleObserver {
    public static final int DEFAULT_ORDER = 1000;
    public static final String EXTRA_IS_SELF_TRIGGERED = "isRefresh";
    static final String SECURITY_HUB_APP_PACKAGE_NAME = "com.google.android.apps.security.securityhub";
    static SecurityContentManager sInstance;
    private Context mApplicationContext;
    private SecurityLevel mBiometricSecurityLevelWhenEnrolled;
    private SecurityLevel mBiometricSecurityLevelWhenNotEnrolled;
    ContentProviderClient mContentProviderClient;
    private OverallStatus mOverallStatus;
    private SecurityLevel mScreenLockSecurityLevelWhenNotSet;
    private SecurityLevel mScreenLockSecurityLevelWhenSet;
    private String mUiDataEtag;
    private static final Object sLock = new Object();
    private static final Uri URI = new Uri.Builder().scheme("content").authority("com.google.android.apps.security.securityhub.settingscontentprovider").build();
    private static final Uri UI_DATA_OBSERVER_URI = new Uri.Builder().scheme("content").authority("com.google.android.apps.security.securityhub.settingscontentprovider").appendPath("getUiData").build();
    private final Set<UiDataSubscriber> mUiDataSubscribers = Collections.synchronizedSet(new HashSet());
    private final SecurityContentObserver mSecurityContentObserver = new SecurityContentObserver();
    private List<Entry> mEntries = new ArrayList();
    private List<SecurityWarning> mSecurityWarnings = new ArrayList();
    private AtomicBoolean mFetchUiDataAsyncRequestEnqueued = new AtomicBoolean(false);
    private int mBiometricOrder = DEFAULT_ORDER;
    private int mScreenLockOrder = DEFAULT_ORDER;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ContentProviderMethod {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface EntrySeverity {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface IntentAction {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface OverallSeverity {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface UiDataSubscriber extends LifecycleOwner {
        void onSecurityHubUiDataChange();
    }

    private SecurityContentManager(Context context) {
        this.mApplicationContext = context.getApplicationContext();
    }

    public static SecurityContentManager getInstance(Context context) {
        SecurityContentManager securityContentManager;
        SecurityContentManager securityContentManager2 = sInstance;
        if (securityContentManager2 != null) {
            return securityContentManager2;
        }
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new SecurityContentManager(context);
            }
            securityContentManager = sInstance;
        }
        return securityContentManager;
    }

    public SecurityContentManager subscribe(UiDataSubscriber uiDataSubscriber) {
        uiDataSubscriber.getLifecycle().addObserver(this);
        return this;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart(LifecycleOwner lifecycleOwner) {
        if (lifecycleOwner instanceof UiDataSubscriber) {
            fetchUiDataAsync((UiDataSubscriber) lifecycleOwner);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop(LifecycleOwner lifecycleOwner) {
        if (lifecycleOwner instanceof UiDataSubscriber) {
            this.mUiDataSubscribers.remove(lifecycleOwner);
            if (this.mUiDataSubscribers.isEmpty()) {
                closePersistentClient();
                unregisterContentObserver();
            }
        }
    }

    public boolean getSecurityHubIsEnabled() {
        Bundle callWithPersistentClient;
        try {
            if (isSecurityHubOnDevice() && (callWithPersistentClient = callWithPersistentClient("isEnabled", null, null, 0)) != null) {
                return callWithPersistentClient.getBoolean("is_enabled", false);
            }
            return false;
        } catch (Exception e) {
            Log.e("SecurityContentManager", "Exception on isEnabled call to Security Hub", e);
            return false;
        }
    }

    boolean fetchUiData() {
        return fetchUiData(extrasBundle(true));
    }

    boolean fetchUiDataAfterContentResolverUpdate() {
        return fetchUiData(extrasBundle(false));
    }

    private boolean fetchUiData(Bundle bundle) {
        Bundle bundle2 = null;
        try {
            bundle2 = callWithPersistentClient("getUiData", null, bundle, 0);
        } catch (RemoteException e) {
            Log.e("SecurityContentManager", "Exception on getUiData call to Security Hub", e);
        }
        if (bundle2 == null) {
            return false;
        }
        String string = bundle2.getString("etag");
        if (string != null && string.equals(this.mUiDataEtag)) {
            return false;
        }
        this.mUiDataEtag = string;
        this.mOverallStatus = getOverallStatusFromUiDataBundle(bundle2);
        this.mEntries = getEntriesFromUiDataBundle(bundle2);
        this.mSecurityWarnings = getSecurityWarningsFromUiDataBundle(bundle2);
        Bundle bundle3 = (Bundle) bundle2.getParcelable("config");
        if (bundle3 != null) {
            int i = DEFAULT_ORDER;
            int i2 = bundle3.getInt("biometric_order_value", DEFAULT_ORDER);
            if (i2 <= 1) {
                i2 = 1000;
            }
            this.mBiometricOrder = i2;
            int i3 = bundle3.getInt("screen_lock_order_value", DEFAULT_ORDER);
            if (i3 > 1) {
                i = i3;
            }
            this.mScreenLockOrder = i;
            this.mBiometricSecurityLevelWhenEnrolled = getSecurityLevelOrNull(bundle3.getString("biometric_severity_level_when_enrolled"));
            this.mBiometricSecurityLevelWhenNotEnrolled = getSecurityLevelOrNull(bundle3.getString("biometric_severity_level_when_not_enrolled"));
            this.mScreenLockSecurityLevelWhenSet = getSecurityLevelOrNull(bundle3.getString("screen_lock_severity_level_when_set"));
            this.mScreenLockSecurityLevelWhenNotSet = getSecurityLevelOrNull(bundle3.getString("screen_lock_severity_level_when_not_set"));
        }
        this.mUiDataSubscribers.forEach(new Consumer() { // from class: com.google.android.settings.security.SecurityContentManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SecurityContentManager.this.notifySubscriberIfStarted((SecurityContentManager.UiDataSubscriber) obj);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchUiDataAsync() {
        fetchUiDataAsync(null);
    }

    private void fetchUiDataAsync(final UiDataSubscriber uiDataSubscriber) {
        final boolean z = uiDataSubscriber != null;
        if (!this.mFetchUiDataAsyncRequestEnqueued.get()) {
            this.mFetchUiDataAsyncRequestEnqueued.set(true);
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.google.android.settings.security.SecurityContentManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SecurityContentManager.this.lambda$fetchUiDataAsync$0(uiDataSubscriber, z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchUiDataAsync$0(UiDataSubscriber uiDataSubscriber, boolean z) {
        boolean z2;
        this.mFetchUiDataAsyncRequestEnqueued.set(false);
        if (uiDataSubscriber != null && isStarted(uiDataSubscriber)) {
            if (this.mUiDataSubscribers.isEmpty()) {
                registerContentObserver();
            }
            this.mUiDataSubscribers.add(uiDataSubscriber);
        }
        if (z) {
            z2 = fetchUiData();
        } else {
            z2 = fetchUiDataAfterContentResolverUpdate();
        }
        if (uiDataSubscriber != null && isStarted(uiDataSubscriber) && !z2) {
            uiDataSubscriber.onSecurityHubUiDataChange();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifySubscriberIfStarted(UiDataSubscriber uiDataSubscriber) {
        if (isStarted(uiDataSubscriber)) {
            uiDataSubscriber.onSecurityHubUiDataChange();
        }
    }

    private boolean isStarted(UiDataSubscriber uiDataSubscriber) {
        return uiDataSubscriber.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    }

    private void registerContentObserver() {
        this.mApplicationContext.getContentResolver().registerContentObserver(UI_DATA_OBSERVER_URI, false, this.mSecurityContentObserver);
    }

    private void unregisterContentObserver() {
        this.mApplicationContext.getContentResolver().unregisterContentObserver(this.mSecurityContentObserver);
    }

    private OverallStatus getOverallStatusFromUiDataBundle(Bundle bundle) {
        Bundle bundle2 = (Bundle) bundle.getParcelable("overall_status");
        if (bundle2 != null) {
            return OverallStatus.builder().setTitle(bundle2.getString("title")).setSummary(bundle2.getString("summary")).setStatusSecurityLevel(getStatusSecurityLevel(bundle2.getString("severity_level"))).build();
        }
        return null;
    }

    private List<Entry> getEntriesFromUiDataBundle(Bundle bundle) {
        final ArrayList arrayList = new ArrayList();
        final HashSet hashSet = new HashSet();
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("settings_entries");
        if (parcelableArrayList != null) {
            parcelableArrayList.forEach(new Consumer() { // from class: com.google.android.settings.security.SecurityContentManager$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SecurityContentManager.lambda$getEntriesFromUiDataBundle$1(hashSet, arrayList, (Bundle) obj);
                }
            });
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getEntriesFromUiDataBundle$1(Set set, List list, Bundle bundle) {
        String string = bundle.getString("security_source_id");
        if (!set.contains(string)) {
            set.add(string);
            list.add(Entry.builder().setTitle(bundle.getString("title")).setSummary(bundle.getString("summary")).setOrder(bundle.getInt("order_value", DEFAULT_ORDER)).setSecurityLevel(getSecurityLevel(bundle.getString("severity_level"))).setOnClickBundle((Bundle) bundle.getParcelable("settings_entry_button")).setSecuritySourceId(string).build());
            return;
        }
        Log.w("SecurityContentManager", "Two entries with the same securitySourceId were found.");
    }

    private List<SecurityWarning> getSecurityWarningsFromUiDataBundle(Bundle bundle) {
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("warning_cards");
        if (parcelableArrayList == null) {
            return new ArrayList();
        }
        final ArrayList arrayList = new ArrayList();
        parcelableArrayList.forEach(new Consumer() { // from class: com.google.android.settings.security.SecurityContentManager$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SecurityContentManager.lambda$getSecurityWarningsFromUiDataBundle$2(arrayList, (Bundle) obj);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getSecurityWarningsFromUiDataBundle$2(List list, Bundle bundle) {
        list.add(getSecurityWarningFromWarningBundle(bundle));
    }

    private static SecurityWarning getSecurityWarningFromWarningBundle(Bundle bundle) {
        SecurityWarning.Builder securityLevel = SecurityWarning.builder().setTitle(bundle.getString("title")).setSubtitle(bundle.getString("subtitle")).setSummary(bundle.getString("summary")).setSecurityLevel(getSecurityLevel(bundle.getString("severity_level")));
        Bundle bundle2 = (Bundle) bundle.getParcelable("primary_button");
        if (bundle2 != null) {
            securityLevel.setPrimaryButtonClickBundle(bundle2);
            securityLevel.setPrimaryButtonText(bundle2.getString("label"));
        }
        Bundle bundle3 = (Bundle) bundle.getParcelable("secondary_button");
        if (bundle3 != null) {
            securityLevel.setSecondaryButtonClickBundle(bundle3);
            securityLevel.setSecondaryButtonText(bundle3.getString("label"));
        }
        Bundle bundle4 = (Bundle) bundle.getParcelable("dismiss_button");
        if (bundle4 != null) {
            securityLevel.setDismissButtonClickBundle(bundle4);
            securityLevel.setShowConfirmationDialogOnDismiss(bundle4.getBoolean("show_confirmation_dialog", true));
        }
        return securityLevel.build();
    }

    public List<Entry> getEntries() {
        return this.mEntries;
    }

    public OverallStatus getOverallStatus() {
        return this.mOverallStatus;
    }

    public List<SecurityWarning> getSecurityWarnings() {
        return this.mSecurityWarnings;
    }

    public SecurityWarning getPrimarySecurityWarning() {
        if (this.mSecurityWarnings.size() > 0) {
            return this.mSecurityWarnings.get(0);
        }
        return null;
    }

    public int getSecurityWarningCount() {
        return getSecurityWarnings().size();
    }

    public SecurityLevel getBiometricSecurityLevel(boolean z) {
        if (z) {
            SecurityLevel securityLevel = this.mBiometricSecurityLevelWhenEnrolled;
            return securityLevel != null ? securityLevel : SecurityLevel.INFORMATION;
        }
        SecurityLevel securityLevel2 = this.mBiometricSecurityLevelWhenNotEnrolled;
        return securityLevel2 != null ? securityLevel2 : SecurityLevel.NONE;
    }

    public SecurityLevel getScreenLockSecurityLevel(boolean z) {
        if (z) {
            SecurityLevel securityLevel = this.mScreenLockSecurityLevelWhenSet;
            return securityLevel != null ? securityLevel : SecurityLevel.INFORMATION;
        }
        SecurityLevel securityLevel2 = this.mScreenLockSecurityLevelWhenNotSet;
        return securityLevel2 != null ? securityLevel2 : SecurityLevel.CRITICAL_WARNING;
    }

    public int getBiometricOrder() {
        return this.mBiometricOrder;
    }

    public int getScreenLockOrder() {
        return this.mScreenLockOrder;
    }

    public boolean handleClick(Bundle bundle, Activity activity) {
        if (!isThisLargeScreenDevice(activity) || !clickBundleRequiresLargeScreenSupport(bundle)) {
            try {
                callWithPersistentClient("handleClick", null, bundle, 0);
                return true;
            } catch (RemoteException e) {
                Log.e("SecurityContentManager", "Exception on handleClick call to Security Hub", e);
                return false;
            }
        } else {
            try {
                activity.startActivity(getLargeScreenClickIntent(bundle));
                return true;
            } catch (ActivityNotFoundException e2) {
                Log.e("SecurityContentManager", "Unable to start activity on a large screen device", e2);
                return false;
            }
        }
    }

    private Bundle callWithPersistentClient(String str, String str2, Bundle bundle, int i) throws RemoteException {
        if (this.mContentProviderClient == null) {
            this.mContentProviderClient = this.mApplicationContext.getContentResolver().acquireUnstableContentProviderClient(URI);
        }
        try {
            return this.mContentProviderClient.call(str, str2, bundle);
        } catch (DeadObjectException e) {
            Log.e("SecurityContentManager", "Security Hub client has died", e);
            this.closePersistentClient();
            if (i < 1) {
                return this.callWithPersistentClient(str, str2, bundle, i + 1);
            }
            return null;
        }
    }

    private boolean isSecurityHubOnDevice() {
        ApplicationInfo applicationInfo;
        String str;
        try {
            PackageManager packageManager = this.mApplicationContext.getPackageManager();
            packageManager.getPackageInfo(SECURITY_HUB_APP_PACKAGE_NAME, 0);
            ProviderInfo resolveContentProvider = packageManager.resolveContentProvider("com.google.android.apps.security.securityhub.settingscontentprovider", 0);
            if (!(resolveContentProvider == null || (applicationInfo = resolveContentProvider.applicationInfo) == null || (str = applicationInfo.packageName) == null)) {
                if (SignatureVerifier.isPackageAllowlisted(this.mApplicationContext, str)) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        return false;
    }

    private void closePersistentClient() {
        ContentProviderClient contentProviderClient = this.mContentProviderClient;
        if (contentProviderClient != null) {
            try {
                contentProviderClient.close();
            } catch (Exception e) {
                Log.e("SecurityContentManager", "Exception closing the Security Hub client", e);
            }
            this.mContentProviderClient = null;
        }
    }

    private Bundle extrasBundle(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_IS_SELF_TRIGGERED, z);
        return bundle;
    }

    private static SecurityLevel getSecurityLevelOrNull(String str) {
        if (str == null) {
            return null;
        }
        return getSecurityLevel(str);
    }

    private static SecurityLevel getSecurityLevel(String str) {
        if (str == null) {
            return SecurityLevel.SECURITY_LEVEL_UNKNOWN;
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -1416873252:
                if (str.equals("CRITICAL_WARNING")) {
                    c = 0;
                    break;
                }
                break;
            case -173405940:
                if (str.equals("INFORMATION")) {
                    c = 1;
                    break;
                }
                break;
            case 2402104:
                if (str.equals("NONE")) {
                    c = 2;
                    break;
                }
                break;
            case 1085612985:
                if (str.equals("RECOMMENDATION")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return SecurityLevel.CRITICAL_WARNING;
            case 1:
                return SecurityLevel.INFORMATION;
            case 2:
                return SecurityLevel.NONE;
            case 3:
                return SecurityLevel.RECOMMENDATION;
            default:
                return SecurityLevel.SECURITY_LEVEL_UNKNOWN;
        }
    }

    private static StatusSecurityLevel getStatusSecurityLevel(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -792742738:
                if (str.equals("OVERALL_CRITICAL_WARNING")) {
                    c = 0;
                    break;
                }
                break;
            case 783420924:
                if (str.equals("OVERALL_INFORMATION_REVIEW_ISSUES")) {
                    c = 1;
                    break;
                }
                break;
            case 1994853619:
                if (str.equals("OVERALL_INFORMATION_NO_ISSUES")) {
                    c = 2;
                    break;
                }
                break;
            case 2042685963:
                if (str.equals("OVERALL_RECOMMENDATION")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return StatusSecurityLevel.CRITICAL_WARNING;
            case 1:
                return StatusSecurityLevel.INFORMATION_REVIEW_ISSUES;
            case 2:
                return StatusSecurityLevel.INFORMATION_NO_ISSUES;
            case 3:
                return StatusSecurityLevel.RECOMMENDATION;
            default:
                return StatusSecurityLevel.STATUS_SECURITY_LEVEL_UNKNOWN;
        }
    }

    private boolean isThisLargeScreenDevice(Context context) {
        return ActivityEmbeddingUtils.isEmbeddingActivityEnabled(context);
    }

    private boolean clickBundleRequiresLargeScreenSupport(Bundle bundle) {
        Intent intent;
        if (!"navigation".equals(bundle.getString("click_type")) || (intent = (Intent) bundle.getParcelable("click_intent")) == null) {
            return false;
        }
        String action = intent.getAction();
        return "com.google.android.gms.settings.FIND_MY_DEVICE_SETTINGS".equals(action) || "com.google.android.gms.settings.ADM_SETTINGS".equals(action) || "com.google.android.apps.security.securityhub.shared.crossprofile.profileselection.PROFILE_SELECTION_ACTIVITY".equals(action) || "android.settings.MODULE_UPDATE_SETTINGS".equals(action) || "android.app.action.SET_NEW_PASSWORD".equals(action) || "android.settings.SYSTEM_UPDATE_SETTINGS".equals(action);
    }

    private Intent getLargeScreenClickIntent(Bundle bundle) {
        Intent intent = new Intent((Intent) bundle.getParcelable("click_intent"));
        intent.removeFlags(268435456);
        intent.removeFlags(32768);
        intent.removeFlags(8388608);
        return intent;
    }

    /* loaded from: classes2.dex */
    public static class Entry {
        private Bundle mOnClickBundle;
        private int mOrder;
        private SecurityLevel mSecurityLevel;
        private String mSecuritySourceId;
        private String mSummary;
        private String mTitle;

        private Entry(Builder builder) {
            this.mTitle = builder.mTitle;
            this.mSummary = builder.mSummary;
            this.mSecurityLevel = builder.mSecurityLevel;
            this.mOnClickBundle = builder.mOnClickBundle;
            this.mOrder = builder.mOrder;
            this.mSecuritySourceId = builder.mSecuritySourceId;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getTitle() {
            return this.mTitle;
        }

        public String getSummary() {
            return this.mSummary;
        }

        public SecurityLevel getSecurityLevel() {
            return this.mSecurityLevel;
        }

        public Bundle getOnClickBundle() {
            return this.mOnClickBundle;
        }

        public int getOrder() {
            return this.mOrder;
        }

        public String getSecuritySourceId() {
            return this.mSecuritySourceId;
        }

        /* loaded from: classes2.dex */
        public static class Builder {
            private Bundle mOnClickBundle;
            private int mOrder = SecurityContentManager.DEFAULT_ORDER;
            private SecurityLevel mSecurityLevel;
            private String mSecuritySourceId;
            private String mSummary;
            private String mTitle;

            public Builder setTitle(String str) {
                this.mTitle = str;
                return this;
            }

            public Builder setSummary(String str) {
                this.mSummary = str;
                return this;
            }

            public Builder setSecurityLevel(SecurityLevel securityLevel) {
                this.mSecurityLevel = securityLevel;
                return this;
            }

            public Builder setOnClickBundle(Bundle bundle) {
                this.mOnClickBundle = bundle;
                return this;
            }

            public Builder setOrder(int i) {
                if (i < 1) {
                    i = SecurityContentManager.DEFAULT_ORDER;
                }
                this.mOrder = i;
                return this;
            }

            public Builder setSecuritySourceId(String str) {
                this.mSecuritySourceId = str;
                return this;
            }

            public Entry build() {
                return new Entry(this);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class OverallStatus {
        private StatusSecurityLevel mStatusSecurityLevel;
        private String mSummary;
        private String mTitle;

        private OverallStatus(Builder builder) {
            this.mTitle = builder.mTitle;
            this.mSummary = builder.mSummary;
            this.mStatusSecurityLevel = builder.mStatusSecurityLevel;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getTitle() {
            return this.mTitle;
        }

        public String getSummary() {
            return this.mSummary;
        }

        public StatusSecurityLevel getStatusSecurityLevel() {
            return this.mStatusSecurityLevel;
        }

        /* loaded from: classes2.dex */
        public static class Builder {
            private StatusSecurityLevel mStatusSecurityLevel;
            private String mSummary;
            private String mTitle;

            public Builder setTitle(String str) {
                this.mTitle = str;
                return this;
            }

            public Builder setSummary(String str) {
                this.mSummary = str;
                return this;
            }

            public Builder setStatusSecurityLevel(StatusSecurityLevel statusSecurityLevel) {
                this.mStatusSecurityLevel = statusSecurityLevel;
                return this;
            }

            public OverallStatus build() {
                return new OverallStatus(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SecurityContentObserver extends ContentObserver {
        SecurityContentObserver() {
            super(null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            SecurityContentManager.this.fetchUiDataAsync();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            onChange(z);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            onChange(z);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Collection<Uri> collection, int i) {
            onChange(z);
        }
    }
}
