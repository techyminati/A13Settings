package com.android.settings.biometrics.face;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settings.biometrics.face.FaceSettingsEnrollButtonPreferenceController;
import com.android.settings.biometrics.face.FaceSettingsRemoveButtonPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class FaceSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.security_settings_face) { // from class: com.android.settings.biometrics.face.FaceSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            if (FaceSettings.isFaceHardwareDetected(context)) {
                return FaceSettings.buildPreferenceControllers(context);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            if (FaceSettings.isFaceHardwareDetected(context)) {
                return hasEnrolledBiometrics(context);
            }
            return false;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            boolean isFaceHardwareDetected = FaceSettings.isFaceHardwareDetected(context);
            Log.d("FaceSettings", "Get non indexable keys. isFaceHardwareDetected: " + isFaceHardwareDetected + ", size:" + nonIndexableKeys.size());
            if (isFaceHardwareDetected) {
                nonIndexableKeys.add(hasEnrolledBiometrics(context) ? "security_settings_face_enroll_faces_container" : "security_settings_face_delete_faces_container");
            }
            if (!isAttentionSupported(context)) {
                nonIndexableKeys.add(FaceSettingsAttentionPreferenceController.KEY);
            }
            return nonIndexableKeys;
        }

        private boolean isAttentionSupported(Context context) {
            FaceFeatureProvider faceFeatureProvider = FeatureFactory.getFactory(context).getFaceFeatureProvider();
            if (faceFeatureProvider != null) {
                return faceFeatureProvider.isAttentionSupported(context);
            }
            return false;
        }

        private boolean hasEnrolledBiometrics(Context context) {
            FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(context);
            if (faceManagerOrNull != null) {
                return faceManagerOrNull.hasEnrolledTemplates(UserHandle.myUserId());
            }
            return false;
        }
    };
    private FaceSettingsAttentionPreferenceController mAttentionController;
    private long mChallenge;
    private boolean mConfirmingPassword;
    private List<AbstractPreferenceController> mControllers;
    private DevicePolicyManager mDevicePolicyManager;
    private Preference mEnrollButton;
    private FaceSettingsEnrollButtonPreferenceController mEnrollController;
    private FaceFeatureProvider mFaceFeatureProvider;
    private FaceManager mFaceManager;
    private FaceSettingsLockscreenBypassPreferenceController mLockscreenController;
    private Preference mRemoveButton;
    private FaceSettingsRemoveButtonPreferenceController mRemoveController;
    private int mSensorId;
    private List<Preference> mTogglePreferences;
    private byte[] mToken;
    private int mUserId;
    private UserManager mUserManager;
    private final FaceSettingsRemoveButtonPreferenceController.Listener mRemovalListener = new FaceSettingsRemoveButtonPreferenceController.Listener() { // from class: com.android.settings.biometrics.face.FaceSettings$$ExternalSyntheticLambda2
        @Override // com.android.settings.biometrics.face.FaceSettingsRemoveButtonPreferenceController.Listener
        public final void onRemoved() {
            FaceSettings.this.lambda$new$0();
        }
    };
    private final FaceSettingsEnrollButtonPreferenceController.Listener mEnrollListener = new FaceSettingsEnrollButtonPreferenceController.Listener() { // from class: com.android.settings.biometrics.face.FaceSettings$$ExternalSyntheticLambda1
        @Override // com.android.settings.biometrics.face.FaceSettingsEnrollButtonPreferenceController.Listener
        public final void onStartEnrolling(Intent intent) {
            FaceSettings.this.lambda$new$1(intent);
        }
    };

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_face;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "FaceSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1511;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.security_settings_face;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        for (Preference preference : this.mTogglePreferences) {
            preference.setEnabled(false);
        }
        this.mRemoveButton.setVisible(false);
        this.mEnrollButton.setVisible(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Intent intent) {
        startActivityForResult(intent, 5);
    }

    public static boolean isFaceHardwareDetected(Context context) {
        boolean z;
        FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(context);
        if (faceManagerOrNull == null) {
            Log.d("FaceSettings", "FaceManager is null");
            z = false;
        } else {
            z = faceManagerOrNull.isHardwareDetected();
            Log.d("FaceSettings", "FaceManager is not null. Hardware detected: " + z);
        }
        return faceManagerOrNull != null && z;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putByteArray("hw_auth_token", this.mToken);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        FaceSettingsLockscreenBypassPreferenceController faceSettingsLockscreenBypassPreferenceController;
        super.onCreate(bundle);
        Context prefContext = getPrefContext();
        if (!isFaceHardwareDetected(prefContext)) {
            Log.w("FaceSettings", "no faceManager, finish this");
            finish();
            return;
        }
        this.mUserManager = (UserManager) prefContext.getSystemService(UserManager.class);
        this.mFaceManager = (FaceManager) prefContext.getSystemService(FaceManager.class);
        this.mDevicePolicyManager = (DevicePolicyManager) prefContext.getSystemService(DevicePolicyManager.class);
        this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        this.mSensorId = getIntent().getIntExtra("sensor_id", -1);
        this.mChallenge = getIntent().getLongExtra("challenge", 0L);
        this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        this.mFaceFeatureProvider = FeatureFactory.getFactory(getContext()).getFaceFeatureProvider();
        if (this.mUserManager.getUserInfo(this.mUserId).isManagedProfile()) {
            getActivity().setTitle(this.mDevicePolicyManager.getString("Settings.FACE_SETTINGS_FOR_WORK_TITLE", new Callable() { // from class: com.android.settings.biometrics.face.FaceSettings$$ExternalSyntheticLambda3
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$onCreate$2;
                    lambda$onCreate$2 = FaceSettings.this.lambda$onCreate$2();
                    return lambda$onCreate$2;
                }
            }));
        }
        if (Utils.isMultipleBiometricsSupported(prefContext)) {
            faceSettingsLockscreenBypassPreferenceController = (FaceSettingsLockscreenBypassPreferenceController) use(BiometricLockscreenBypassPreferenceController.class);
        } else {
            faceSettingsLockscreenBypassPreferenceController = (FaceSettingsLockscreenBypassPreferenceController) use(FaceSettingsLockscreenBypassPreferenceController.class);
        }
        this.mLockscreenController = faceSettingsLockscreenBypassPreferenceController;
        faceSettingsLockscreenBypassPreferenceController.setUserId(this.mUserId);
        this.mTogglePreferences = new ArrayList(Arrays.asList(findPreference("security_settings_face_keyguard"), findPreference("security_settings_face_app"), findPreference(FaceSettingsAttentionPreferenceController.KEY), findPreference("security_settings_face_require_confirmation"), findPreference(this.mLockscreenController.getPreferenceKey())));
        this.mRemoveButton = findPreference("security_settings_face_delete_faces_container");
        this.mEnrollButton = findPreference("security_settings_face_enroll_faces_container");
        for (AbstractPreferenceController abstractPreferenceController : this.mControllers) {
            if (abstractPreferenceController instanceof FaceSettingsPreferenceController) {
                ((FaceSettingsPreferenceController) abstractPreferenceController).setUserId(this.mUserId);
            } else if (abstractPreferenceController instanceof FaceSettingsEnrollButtonPreferenceController) {
                ((FaceSettingsEnrollButtonPreferenceController) abstractPreferenceController).setUserId(this.mUserId);
            }
        }
        this.mRemoveController.setUserId(this.mUserId);
        if (this.mUserManager.isManagedProfile(this.mUserId)) {
            removePreference("security_settings_face_keyguard");
            removePreference(this.mLockscreenController.getPreferenceKey());
        }
        if (bundle != null) {
            this.mToken = bundle.getByteArray("hw_auth_token");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$2() throws Exception {
        return getActivity().getResources().getString(R.string.security_settings_face_profile_preference_title);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        byte[] bArr = this.mToken;
        if (bArr != null || this.mConfirmingPassword) {
            this.mAttentionController.setToken(bArr);
            this.mEnrollController.setToken(this.mToken);
        } else {
            boolean show = new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(4).setTitle(getString(R.string.security_settings_face_preference_title)).setRequestGatekeeperPasswordHandle(true).setUserId(this.mUserId).setForegroundOnly(true).setReturnCredentials(true).show();
            this.mConfirmingPassword = true;
            if (!show) {
                Log.e("FaceSettings", "Password not set");
                finish();
            }
        }
        boolean hasEnrolledTemplates = this.mFaceManager.hasEnrolledTemplates(this.mUserId);
        this.mEnrollButton.setVisible(!hasEnrolledTemplates);
        this.mRemoveButton.setVisible(hasEnrolledTemplates);
        if (!this.mFaceFeatureProvider.isAttentionSupported(getContext())) {
            removePreference(FaceSettingsAttentionPreferenceController.KEY);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, final Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.mToken == null && !BiometricUtils.containsGatekeeperPasswordHandle(intent)) {
            Log.e("FaceSettings", "No credential");
            finish();
        }
        if (i == 4) {
            if (i2 == 1 || i2 == -1) {
                this.mFaceManager.generateChallenge(this.mUserId, new FaceManager.GenerateChallengeCallback() { // from class: com.android.settings.biometrics.face.FaceSettings$$ExternalSyntheticLambda0
                    public final void onGenerateChallengeResult(int i3, int i4, long j) {
                        FaceSettings.this.lambda$onActivityResult$3(intent, i3, i4, j);
                    }
                });
            }
        } else if (i == 5 && i2 == 3) {
            setResult(i2, intent);
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$3(Intent intent, int i, int i2, long j) {
        this.mToken = BiometricUtils.requestGatekeeperHat(getPrefContext(), intent, this.mUserId, j);
        this.mSensorId = i;
        this.mChallenge = j;
        BiometricUtils.removeGatekeeperPasswordHandle(getPrefContext(), intent);
        this.mAttentionController.setToken(this.mToken);
        this.mEnrollController.setToken(this.mToken);
        this.mConfirmingPassword = false;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (!this.mEnrollController.isClicked() && !getActivity().isChangingConfigurations() && !this.mConfirmingPassword) {
            if (this.mToken != null) {
                this.mFaceManager.revokeChallenge(this.mSensorId, this.mUserId, this.mChallenge);
                this.mToken = null;
            }
            finish();
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (!isFaceHardwareDetected(context)) {
            return null;
        }
        List<AbstractPreferenceController> buildPreferenceControllers = buildPreferenceControllers(context);
        this.mControllers = buildPreferenceControllers;
        for (AbstractPreferenceController abstractPreferenceController : buildPreferenceControllers) {
            if (abstractPreferenceController instanceof FaceSettingsAttentionPreferenceController) {
                this.mAttentionController = (FaceSettingsAttentionPreferenceController) abstractPreferenceController;
            } else if (abstractPreferenceController instanceof FaceSettingsRemoveButtonPreferenceController) {
                FaceSettingsRemoveButtonPreferenceController faceSettingsRemoveButtonPreferenceController = (FaceSettingsRemoveButtonPreferenceController) abstractPreferenceController;
                this.mRemoveController = faceSettingsRemoveButtonPreferenceController;
                faceSettingsRemoveButtonPreferenceController.setListener(this.mRemovalListener);
                this.mRemoveController.setActivity((SettingsActivity) getActivity());
            } else if (abstractPreferenceController instanceof FaceSettingsEnrollButtonPreferenceController) {
                FaceSettingsEnrollButtonPreferenceController faceSettingsEnrollButtonPreferenceController = (FaceSettingsEnrollButtonPreferenceController) abstractPreferenceController;
                this.mEnrollController = faceSettingsEnrollButtonPreferenceController;
                faceSettingsEnrollButtonPreferenceController.setListener(this.mEnrollListener);
                this.mEnrollController.setActivity((SettingsActivity) getActivity());
            }
        }
        return this.mControllers;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FaceSettingsKeyguardPreferenceController(context));
        arrayList.add(new FaceSettingsAppPreferenceController(context));
        arrayList.add(new FaceSettingsAttentionPreferenceController(context));
        arrayList.add(new FaceSettingsRemoveButtonPreferenceController(context));
        arrayList.add(new FaceSettingsConfirmPreferenceController(context));
        arrayList.add(new FaceSettingsEnrollButtonPreferenceController(context));
        return arrayList;
    }
}
