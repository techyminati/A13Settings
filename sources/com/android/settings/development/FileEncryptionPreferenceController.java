package com.android.settings.development;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IStorageManager;
import android.sysprop.CryptoProperties;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class FileEncryptionPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private final IStorageManager mStorageManager = getStorageManager();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "convert_to_file_encryption";
    }

    public FileEncryptionPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        IStorageManager iStorageManager = this.mStorageManager;
        if (iStorageManager == null) {
            return false;
        }
        try {
            return iStorageManager.isConvertibleToFBE();
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (CryptoProperties.type().orElse(CryptoProperties.type_values.NONE) == CryptoProperties.type_values.FILE) {
            this.mPreference.setEnabled(false);
            this.mPreference.setSummary(this.mContext.getResources().getString(R.string.convert_to_file_encryption_done));
        }
    }

    private IStorageManager getStorageManager() {
        try {
            return IStorageManager.Stub.asInterface(ServiceManager.getService("mount"));
        } catch (VerifyError unused) {
            return null;
        }
    }
}
