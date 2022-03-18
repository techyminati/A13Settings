package com.android.settings.deviceinfo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.window.R;
/* loaded from: classes.dex */
public class StorageWizardMigrateProgress extends StorageWizardBase {
    private final PackageManager.MoveCallback mCallback = new PackageManager.MoveCallback() { // from class: com.android.settings.deviceinfo.StorageWizardMigrateProgress.1
        public void onStatusChanged(int i, int i2, long j) {
            if (StorageWizardMigrateProgress.this.mMoveId == i) {
                StorageWizardMigrateProgress storageWizardMigrateProgress = StorageWizardMigrateProgress.this;
                if (PackageManager.isMoveStatusFinished(i2)) {
                    Log.d("StorageWizardMigrateProgress", "Finished with status " + i2);
                    if (i2 != -100) {
                        Toast.makeText(storageWizardMigrateProgress, StorageWizardMigrateProgress.this.getString(R.string.insufficient_storage), 1).show();
                    } else if (StorageWizardMigrateProgress.this.mDisk != null) {
                        Intent intent = new Intent("com.android.systemui.action.FINISH_WIZARD");
                        intent.addFlags(1073741824);
                        StorageWizardMigrateProgress.this.sendBroadcast(intent);
                        if (!StorageWizardMigrateProgress.this.isFinishing()) {
                            Intent intent2 = new Intent(storageWizardMigrateProgress, StorageWizardReady.class);
                            intent2.putExtra("android.os.storage.extra.DISK_ID", StorageWizardMigrateProgress.this.mDisk.getId());
                            StorageWizardMigrateProgress.this.startActivity(intent2);
                        }
                    }
                    StorageWizardMigrateProgress.this.finishAffinity();
                    return;
                }
                StorageWizardMigrateProgress.this.setCurrentProgress(i2);
            }
        }
    };
    private int mMoveId;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.deviceinfo.StorageWizardBase, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mVolume == null) {
            finish();
            return;
        }
        setContentView(R.layout.storage_wizard_progress);
        this.mMoveId = getIntent().getIntExtra("android.content.pm.extra.MOVE_ID", -1);
        setIcon(R.drawable.ic_swap_horiz);
        setHeaderText(R.string.storage_wizard_migrate_progress_v2_title, new CharSequence[0]);
        setAuxChecklist();
        setBackButtonVisibility(4);
        setNextButtonVisibility(4);
        getPackageManager().registerMoveCallback(this.mCallback, new Handler());
        this.mCallback.onStatusChanged(this.mMoveId, getPackageManager().getMoveStatus(this.mMoveId), -1L);
    }
}
