package com.android.settings.deviceinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
import com.android.settings.deviceinfo.storage.StorageUtils;
/* loaded from: classes.dex */
public class StorageItemPreference extends Preference {
    private ProgressBar mProgressBar;
    private int mProgressPercent;
    private long mStorageSize;

    public StorageItemPreference(Context context) {
        this(context, null);
    }

    public StorageItemPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mProgressPercent = -1;
        setLayoutResource(R.layout.storage_item);
    }

    public void setStorageSize(long j, long j2) {
        this.mStorageSize = j;
        setSummary(StorageUtils.getStorageSizeLabel(getContext(), j));
        if (j2 == 0) {
            this.mProgressPercent = 0;
        } else {
            this.mProgressPercent = (int) ((j * 100) / j2);
        }
        updateProgressBar();
    }

    public long getStorageSize() {
        return this.mStorageSize;
    }

    protected void updateProgressBar() {
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null && this.mProgressPercent != -1) {
            progressBar.setMax(100);
            this.mProgressBar.setProgress(this.mProgressPercent);
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        this.mProgressBar = (ProgressBar) preferenceViewHolder.findViewById(16908301);
        updateProgressBar();
        super.onBindViewHolder(preferenceViewHolder);
    }
}
