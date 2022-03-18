package com.android.settings.development.storage;

import android.app.blob.BlobStoreManager;
import android.content.Context;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.io.IOException;
/* loaded from: classes.dex */
public class SharedDataPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private BlobStoreManager mBlobStoreManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "shared_data";
    }

    public SharedDataPreferenceController(Context context) {
        super(context);
        this.mBlobStoreManager = (BlobStoreManager) context.getSystemService(BlobStoreManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        try {
            BlobStoreManager blobStoreManager = this.mBlobStoreManager;
            boolean z = blobStoreManager != null && !blobStoreManager.queryBlobsForUser(UserHandle.CURRENT).isEmpty();
            preference.setEnabled(z);
            preference.setSummary(z ? R.string.shared_data_summary : R.string.shared_data_no_blobs_text);
        } catch (IOException e) {
            Log.e("SharedDataPrefCtrl", "Unable to fetch blobs for current user: " + e.getMessage());
            preference.setEnabled(false);
            preference.setSummary(R.string.shared_data_no_blobs_text);
        }
    }
}
