package com.android.settings.development.autofill;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.autofill.AutofillManager;
import androidx.preference.PreferenceCategory;
import com.android.settings.development.autofill.AutofillPreferenceCategory;
/* loaded from: classes.dex */
public final class AutofillPreferenceCategory extends PreferenceCategory {
    private final ContentResolver mContentResolver;
    private final Handler mHandler;
    private final ContentObserver mSettingsObserver;

    public AutofillPreferenceCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mSettingsObserver = new AnonymousClass1(handler);
        this.mContentResolver = context.getContentResolver();
    }

    /* renamed from: com.android.settings.development.autofill.AutofillPreferenceCategory$1  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass1 extends ContentObserver {
        AnonymousClass1(Handler handler) {
            super(handler);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onChange$0() {
            AutofillPreferenceCategory autofillPreferenceCategory = AutofillPreferenceCategory.this;
            autofillPreferenceCategory.notifyDependencyChange(autofillPreferenceCategory.shouldDisableDependents());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            AutofillPreferenceCategory.this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.development.autofill.AutofillPreferenceCategory$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AutofillPreferenceCategory.AnonymousClass1.this.lambda$onChange$0();
                }
            }, 2000L);
        }
    }

    @Override // androidx.preference.PreferenceGroup, androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("autofill_service"), false, this.mSettingsObserver);
    }

    @Override // androidx.preference.PreferenceGroup, androidx.preference.Preference
    public void onDetached() {
        this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
        super.onDetached();
    }

    private boolean isAutofillEnabled() {
        AutofillManager autofillManager = (AutofillManager) getContext().getSystemService(AutofillManager.class);
        boolean z = autofillManager != null && autofillManager.isEnabled();
        Log.v("AutofillPreferenceCategory", "isAutofillEnabled(): " + z);
        return z;
    }

    @Override // androidx.preference.PreferenceCategory, androidx.preference.Preference
    public boolean shouldDisableDependents() {
        boolean z = !isAutofillEnabled();
        Log.v("AutofillPreferenceCategory", "shouldDisableDependents(): " + z);
        return z;
    }
}
