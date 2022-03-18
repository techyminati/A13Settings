package com.android.settings.deviceinfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.window.R;
import com.android.settings.core.InstrumentedFragment;
/* loaded from: classes.dex */
public class PrivateVolumeFormat extends InstrumentedFragment {
    private final View.OnClickListener mConfirmListener = new View.OnClickListener() { // from class: com.android.settings.deviceinfo.PrivateVolumeFormat.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent(PrivateVolumeFormat.this.getActivity(), StorageWizardFormatProgress.class);
            intent.putExtra("android.os.storage.extra.DISK_ID", PrivateVolumeFormat.this.mDisk.getId());
            intent.putExtra("format_private", false);
            intent.putExtra("format_forget_uuid", PrivateVolumeFormat.this.mVolume.getFsUuid());
            PrivateVolumeFormat.this.startActivity(intent);
            PrivateVolumeFormat.this.getActivity().finish();
        }
    };
    private DiskInfo mDisk;
    private VolumeInfo mVolume;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 42;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        StorageManager storageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
        VolumeInfo findVolumeById = storageManager.findVolumeById(getArguments().getString("android.os.storage.extra.VOLUME_ID"));
        this.mVolume = findVolumeById;
        this.mDisk = storageManager.findDiskById(findVolumeById.getDiskId());
        View inflate = layoutInflater.inflate(R.layout.storage_internal_format, viewGroup, false);
        ((TextView) inflate.findViewById(R.id.body)).setText(TextUtils.expandTemplate(getText(R.string.storage_internal_format_details), this.mDisk.getDescription()));
        ((Button) inflate.findViewById(R.id.confirm)).setOnClickListener(this.mConfirmListener);
        return inflate;
    }
}
