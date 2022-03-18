package com.android.settings.security;

import android.content.Context;
import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.security.KeyStore;
/* loaded from: classes.dex */
public class ResetCredentialsPreferenceController extends RestrictedEncryptionPreferenceController implements LifecycleObserver, OnResume {
    private final KeyStore mKeyStore;
    private RestrictedPreference mPreference;
    private final KeyStore mWifiKeyStore;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "credentials_reset";
    }

    public ResetCredentialsPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, "no_config_credentials");
        KeyStore keyStore;
        KeyStore keyStore2 = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (Exception unused) {
            keyStore = null;
        }
        this.mKeyStore = keyStore;
        if (context.getUser().isSystem()) {
            try {
                KeyStore instance = KeyStore.getInstance("AndroidKeyStore");
                instance.load(new AndroidKeyStoreLoadStoreParameter(102));
                keyStore2 = instance;
            } catch (Exception unused2) {
            }
        }
        this.mWifiKeyStore = keyStore2;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0025, code lost:
        if (r1.aliases().hasMoreElements() != false) goto L_0x0027;
     */
    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onResume() {
        /*
            r2 = this;
            com.android.settingslib.RestrictedPreference r0 = r2.mPreference
            if (r0 == 0) goto L_0x002d
            boolean r0 = r0.isDisabledByAdmin()
            if (r0 != 0) goto L_0x002d
            r0 = 0
            java.security.KeyStore r1 = r2.mKeyStore     // Catch: KeyStoreException -> 0x0028
            if (r1 == 0) goto L_0x0019
            java.util.Enumeration r1 = r1.aliases()     // Catch: KeyStoreException -> 0x0028
            boolean r1 = r1.hasMoreElements()     // Catch: KeyStoreException -> 0x0028
            if (r1 != 0) goto L_0x0027
        L_0x0019:
            java.security.KeyStore r1 = r2.mWifiKeyStore     // Catch: KeyStoreException -> 0x0028
            if (r1 == 0) goto L_0x0028
            java.util.Enumeration r1 = r1.aliases()     // Catch: KeyStoreException -> 0x0028
            boolean r1 = r1.hasMoreElements()     // Catch: KeyStoreException -> 0x0028
            if (r1 == 0) goto L_0x0028
        L_0x0027:
            r0 = 1
        L_0x0028:
            com.android.settingslib.RestrictedPreference r2 = r2.mPreference
            r2.setEnabled(r0)
        L_0x002d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.security.ResetCredentialsPreferenceController.onResume():void");
    }
}
