package com.android.settings.deviceinfo;

import android.content.Intent;
import android.content.pm.IPackageMoveObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IVoldTaskListener;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import android.widget.Toast;
import androidx.window.R;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class StorageWizardFormatProgress extends StorageWizardBase {
    private boolean mFormatPrivate;
    private PartitionTask mTask;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.deviceinfo.StorageWizardBase, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mDisk == null) {
            finish();
            return;
        }
        setContentView(R.layout.storage_wizard_progress);
        setKeepScreenOn(true);
        this.mFormatPrivate = getIntent().getBooleanExtra("format_private", false);
        setHeaderText(R.string.storage_wizard_format_progress_title, getDiskShortDescription());
        setBodyText(R.string.storage_wizard_format_progress_body, getDiskDescription());
        setBackButtonVisibility(4);
        setNextButtonVisibility(4);
        PartitionTask partitionTask = (PartitionTask) getLastCustomNonConfigurationInstance();
        this.mTask = partitionTask;
        if (partitionTask == null) {
            PartitionTask partitionTask2 = new PartitionTask();
            this.mTask = partitionTask2;
            partitionTask2.setActivity(this);
            this.mTask.execute(new Void[0]);
            return;
        }
        partitionTask.setActivity(this);
    }

    @Override // androidx.activity.ComponentActivity
    public Object onRetainCustomNonConfigurationInstance() {
        return this.mTask;
    }

    /* loaded from: classes.dex */
    public static class PartitionTask extends AsyncTask<Void, Integer, Exception> {
        public StorageWizardFormatProgress mActivity;
        private volatile long mPrivateBench;
        private volatile int mProgress = 20;

        /* JADX INFO: Access modifiers changed from: protected */
        public Exception doInBackground(Void... voidArr) {
            StorageWizardFormatProgress storageWizardFormatProgress = this.mActivity;
            StorageManager storageManager = storageWizardFormatProgress.mStorage;
            try {
                if (storageWizardFormatProgress.mFormatPrivate) {
                    storageManager.partitionPrivate(storageWizardFormatProgress.mDisk.getId());
                    publishProgress(40);
                    VolumeInfo findFirstVolume = storageWizardFormatProgress.findFirstVolume(1, 25);
                    final CompletableFuture completableFuture = new CompletableFuture();
                    storageManager.benchmark(findFirstVolume.getId(), new IVoldTaskListener.Stub() { // from class: com.android.settings.deviceinfo.StorageWizardFormatProgress.PartitionTask.1
                        public void onStatus(int i, PersistableBundle persistableBundle) {
                            PartitionTask.this.publishProgress(Integer.valueOf(((i * 40) / 100) + 40));
                        }

                        public void onFinished(int i, PersistableBundle persistableBundle) {
                            completableFuture.complete(persistableBundle);
                        }
                    });
                    this.mPrivateBench = ((PersistableBundle) completableFuture.get(60L, TimeUnit.SECONDS)).getLong("run", Long.MAX_VALUE);
                    if (storageWizardFormatProgress.mDisk.isDefaultPrimary() && Objects.equals(storageManager.getPrimaryStorageUuid(), "primary_physical")) {
                        Log.d("StorageWizardFormatProgress", "Just formatted primary physical; silently moving storage to new emulated volume");
                        storageManager.setPrimaryStorageUuid(findFirstVolume.getFsUuid(), new SilentObserver());
                    }
                } else {
                    storageManager.partitionPublic(storageWizardFormatProgress.mDisk.getId());
                }
                return null;
            } catch (Exception e) {
                return e;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
            this.mProgress = numArr[0].intValue();
            this.mActivity.setCurrentProgress(this.mProgress);
        }

        public void setActivity(StorageWizardFormatProgress storageWizardFormatProgress) {
            this.mActivity = storageWizardFormatProgress;
            storageWizardFormatProgress.setCurrentProgress(this.mProgress);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onPostExecute(Exception exc) {
            StorageWizardFormatProgress storageWizardFormatProgress = this.mActivity;
            if (!storageWizardFormatProgress.isDestroyed()) {
                if (exc != null) {
                    Log.e("StorageWizardFormatProgress", "Failed to partition", exc);
                    Toast.makeText(storageWizardFormatProgress, exc.getMessage(), 1).show();
                    storageWizardFormatProgress.finishAffinity();
                } else if (storageWizardFormatProgress.mFormatPrivate) {
                    Log.d("StorageWizardFormatProgress", "New volume took " + this.mPrivateBench + "ms to run benchmark");
                    if (this.mPrivateBench > 2000 || SystemProperties.getBoolean("sys.debug.storage_slow", false)) {
                        this.mActivity.onFormatFinishedSlow();
                    } else {
                        this.mActivity.onFormatFinished();
                    }
                } else {
                    this.mActivity.onFormatFinished();
                }
            }
        }
    }

    public void onFormatFinished() {
        Intent intent = new Intent(this, StorageWizardFormatSlow.class);
        intent.putExtra("format_slow", false);
        startActivity(intent);
        finishAffinity();
    }

    public void onFormatFinishedSlow() {
        Intent intent = new Intent(this, StorageWizardFormatSlow.class);
        intent.putExtra("format_slow", true);
        startActivity(intent);
        finishAffinity();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SilentObserver extends IPackageMoveObserver.Stub {
        public void onCreated(int i, Bundle bundle) {
        }

        public void onStatusChanged(int i, int i2, long j) {
        }

        private SilentObserver() {
        }
    }
}
