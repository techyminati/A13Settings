package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.widget.SettingsSpinnerAdapter;
import com.android.settingslib.widget.SettingsSpinnerPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class StorageSelectionPreferenceController extends BasePreferenceController implements AdapterView.OnItemSelectedListener {
    private OnItemSelectedListener mOnItemSelectedListener;
    SettingsSpinnerPreference mSpinnerPreference;
    StorageAdapter mStorageAdapter;
    private final List<StorageEntry> mStorageEntries = new ArrayList();

    /* loaded from: classes.dex */
    public interface OnItemSelectedListener {
        void onItemSelected(StorageEntry storageEntry);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public StorageSelectionPreferenceController(Context context, String str) {
        super(context, str);
        this.mStorageAdapter = new StorageAdapter(context);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    public void setStorageEntries(List<StorageEntry> list) {
        this.mStorageAdapter.clear();
        this.mStorageEntries.clear();
        if (list != null && !list.isEmpty()) {
            Collections.sort(this.mStorageEntries);
            this.mStorageEntries.addAll(list);
            this.mStorageAdapter.addAll(list);
            SettingsSpinnerPreference settingsSpinnerPreference = this.mSpinnerPreference;
            if (settingsSpinnerPreference != null) {
                boolean z = true;
                if (this.mStorageAdapter.getCount() <= 1) {
                    z = false;
                }
                settingsSpinnerPreference.setVisible(z);
            }
        }
    }

    public void setSelectedStorageEntry(StorageEntry storageEntry) {
        if (this.mSpinnerPreference != null && this.mStorageEntries.contains(storageEntry)) {
            this.mSpinnerPreference.setSelection(this.mStorageAdapter.getPosition(storageEntry));
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        SettingsSpinnerPreference settingsSpinnerPreference = (SettingsSpinnerPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mSpinnerPreference = settingsSpinnerPreference;
        settingsSpinnerPreference.setAdapter(this.mStorageAdapter);
        this.mSpinnerPreference.setOnItemSelectedListener(this);
        SettingsSpinnerPreference settingsSpinnerPreference2 = this.mSpinnerPreference;
        boolean z = true;
        if (this.mStorageAdapter.getCount() <= 1) {
            z = false;
        }
        settingsSpinnerPreference2.setVisible(z);
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        OnItemSelectedListener onItemSelectedListener = this.mOnItemSelectedListener;
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected((StorageEntry) this.mSpinnerPreference.getSelectedItem());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class StorageAdapter extends SettingsSpinnerAdapter<StorageEntry> {
        StorageAdapter(Context context) {
            super(context);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getDefaultView(i, view, viewGroup);
            }
            try {
                TextView textView = (TextView) view;
                textView.setText(getItem(i).getDescription());
                return textView;
            } catch (ClassCastException e) {
                throw new IllegalStateException("Default view should be a TextView, ", e);
            }
        }

        @Override // android.widget.ArrayAdapter, android.widget.BaseAdapter, android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getDefaultDropDownView(i, view, viewGroup);
            }
            try {
                TextView textView = (TextView) view;
                textView.setText(getItem(i).getDescription());
                return textView;
            } catch (ClassCastException e) {
                throw new IllegalStateException("Default drop down view should be a TextView, ", e);
            }
        }
    }
}
