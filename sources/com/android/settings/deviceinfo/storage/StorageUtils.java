package com.android.settings.deviceinfo.storage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.window.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.deviceinfo.PrivateVolumeForget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class StorageUtils {
    public static List<StorageEntry> getAllStorageEntries(final Context context, final StorageManager storageManager) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll((Collection) storageManager.getVolumes().stream().filter(StorageUtils$$ExternalSyntheticLambda5.INSTANCE).map(new Function() { // from class: com.android.settings.deviceinfo.storage.StorageUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                StorageEntry lambda$getAllStorageEntries$1;
                lambda$getAllStorageEntries$1 = StorageUtils.lambda$getAllStorageEntries$1(context, (VolumeInfo) obj);
                return lambda$getAllStorageEntries$1;
            }
        }).collect(Collectors.toList()));
        arrayList.addAll((Collection) storageManager.getDisks().stream().filter(StorageUtils$$ExternalSyntheticLambda4.INSTANCE).map(StorageUtils$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toList()));
        arrayList.addAll((Collection) storageManager.getVolumeRecords().stream().filter(new Predicate() { // from class: com.android.settings.deviceinfo.storage.StorageUtils$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isVolumeRecordMissed;
                isVolumeRecordMissed = StorageUtils.isVolumeRecordMissed(storageManager, (VolumeRecord) obj);
                return isVolumeRecordMissed;
            }
        }).map(StorageUtils$$ExternalSyntheticLambda2.INSTANCE).collect(Collectors.toList()));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ StorageEntry lambda$getAllStorageEntries$1(Context context, VolumeInfo volumeInfo) {
        return new StorageEntry(context, volumeInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ StorageEntry lambda$getAllStorageEntries$3(DiskInfo diskInfo) {
        return new StorageEntry(diskInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ StorageEntry lambda$getAllStorageEntries$5(VolumeRecord volumeRecord) {
        return new StorageEntry(volumeRecord);
    }

    public static boolean isStorageSettingsInterestedVolume(VolumeInfo volumeInfo) {
        int type = volumeInfo.getType();
        return type == 0 || type == 1 || type == 5;
    }

    public static boolean isVolumeRecordMissed(StorageManager storageManager, VolumeRecord volumeRecord) {
        return volumeRecord.getType() == 1 && storageManager.findVolumeByUuid(volumeRecord.getFsUuid()) == null;
    }

    public static boolean isDiskUnsupported(DiskInfo diskInfo) {
        return diskInfo.volumeCount == 0 && diskInfo.size > 0;
    }

    public static void launchForgetMissingVolumeRecordFragment(Context context, StorageEntry storageEntry) {
        if (storageEntry != null && storageEntry.isVolumeRecordMissed()) {
            Bundle bundle = new Bundle();
            bundle.putString("android.os.storage.extra.FS_UUID", storageEntry.getFsUuid());
            new SubSettingLauncher(context).setDestination(PrivateVolumeForget.class.getCanonicalName()).setTitleRes(R.string.storage_menu_forget).setSourceMetricsCategory(745).setArguments(bundle).launch();
        }
    }

    public static String getStorageSizeLabel(Context context, long j) {
        Formatter.BytesResult formatBytes = Formatter.formatBytes(context.getResources(), j, 1);
        return TextUtils.expandTemplate(context.getText(R.string.storage_size_large), formatBytes.value, formatBytes.units).toString();
    }

    /* loaded from: classes.dex */
    public static class UnmountTask extends AsyncTask<Void, Void, Exception> {
        private final Context mContext;
        private final String mDescription;
        private final StorageManager mStorageManager;
        private final String mVolumeId;

        public UnmountTask(Context context, VolumeInfo volumeInfo) {
            Context applicationContext = context.getApplicationContext();
            this.mContext = applicationContext;
            StorageManager storageManager = (StorageManager) applicationContext.getSystemService(StorageManager.class);
            this.mStorageManager = storageManager;
            this.mVolumeId = volumeInfo.getId();
            this.mDescription = storageManager.getBestVolumeDescription(volumeInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Exception doInBackground(Void... voidArr) {
            try {
                this.mStorageManager.unmount(this.mVolumeId);
                return null;
            } catch (Exception e) {
                return e;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onPostExecute(Exception exc) {
            if (exc == null) {
                Context context = this.mContext;
                Toast.makeText(context, context.getString(R.string.storage_unmount_success, this.mDescription), 0).show();
                return;
            }
            Log.e("StorageUtils", "Failed to unmount " + this.mVolumeId, exc);
            Context context2 = this.mContext;
            Toast.makeText(context2, context2.getString(R.string.storage_unmount_failure, this.mDescription), 0).show();
        }
    }

    /* loaded from: classes.dex */
    public static class MountTask extends AsyncTask<Void, Void, Exception> {
        private final Context mContext;
        private final String mDescription;
        private final StorageManager mStorageManager;
        private final String mVolumeId;

        public MountTask(Context context, VolumeInfo volumeInfo) {
            Context applicationContext = context.getApplicationContext();
            this.mContext = applicationContext;
            StorageManager storageManager = (StorageManager) applicationContext.getSystemService(StorageManager.class);
            this.mStorageManager = storageManager;
            this.mVolumeId = volumeInfo.getId();
            this.mDescription = storageManager.getBestVolumeDescription(volumeInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Exception doInBackground(Void... voidArr) {
            try {
                this.mStorageManager.mount(this.mVolumeId);
                return null;
            } catch (Exception e) {
                return e;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onPostExecute(Exception exc) {
            if (exc == null) {
                Context context = this.mContext;
                Toast.makeText(context, context.getString(R.string.storage_mount_success, this.mDescription), 0).show();
                return;
            }
            Log.e("StorageUtils", "Failed to mount " + this.mVolumeId, exc);
            Context context2 = this.mContext;
            Toast.makeText(context2, context2.getString(R.string.storage_mount_failure, this.mDescription), 0).show();
        }
    }

    /* loaded from: classes.dex */
    public static class SystemInfoFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 565;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getActivity()).setMessage(getContext().getString(R.string.storage_detail_dialog_system, Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY)).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create();
        }
    }

    public static String getStorageSummary(Context context, int i, long j) {
        Formatter.BytesResult formatBytes = Formatter.formatBytes(context.getResources(), j, 1);
        return context.getString(i, formatBytes.value, formatBytes.units);
    }
}
