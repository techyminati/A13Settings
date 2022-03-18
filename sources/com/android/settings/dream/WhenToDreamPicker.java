package com.android.settings.dream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.window.R;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.dream.DreamBackend;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class WhenToDreamPicker extends RadioButtonPickerFragment {
    private DreamBackend mBackend;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 47;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.when_to_dream_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mBackend = DreamBackend.getInstance(context);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        String[] entries = entries();
        String[] keys = keys();
        ArrayList arrayList = new ArrayList();
        if (entries == null || entries.length <= 0) {
            return null;
        }
        if (keys == null || keys.length != entries.length) {
            throw new IllegalArgumentException("Entries and values must be of the same length.");
        }
        for (int i = 0; i < entries.length; i++) {
            arrayList.add(new WhenToDreamCandidateInfo(entries[i], keys[i]));
        }
        return arrayList;
    }

    private String[] entries() {
        return getResources().getStringArray(R.array.when_to_start_screensaver_entries);
    }

    private String[] keys() {
        return getResources().getStringArray(R.array.when_to_start_screensaver_values);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        return DreamSettings.getKeyFromSetting(this.mBackend.getWhenToDreamSetting());
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        this.mBackend.setWhenToDream(DreamSettings.getSettingFromPrefKey(str));
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void onSelectionPerformed(boolean z) {
        super.onSelectionPerformed(z);
        getActivity().finish();
    }

    /* loaded from: classes.dex */
    private final class WhenToDreamCandidateInfo extends CandidateInfo {
        private final String key;
        private final String name;

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        WhenToDreamCandidateInfo(String str, String str2) {
            super(true);
            this.name = str;
            this.key = str2;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.name;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.key;
        }
    }
}
