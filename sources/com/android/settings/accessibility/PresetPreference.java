package com.android.settings.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.TextView;
import androidx.window.R;
import com.android.internal.widget.SubtitleView;
/* loaded from: classes.dex */
public class PresetPreference extends ListDialogPreference {
    private final CaptioningManager mCaptioningManager;

    public PresetPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(R.layout.grid_picker_dialog);
        setListItemLayoutResource(R.layout.preset_picker_item);
        this.mCaptioningManager = (CaptioningManager) context.getSystemService("captioning");
    }

    @Override // androidx.preference.Preference
    public boolean shouldDisableDependents() {
        return getValue() != -1 || super.shouldDisableDependents();
    }

    @Override // com.android.settings.accessibility.ListDialogPreference
    protected void onBindListItem(View view, int i) {
        View findViewById = view.findViewById(R.id.preview_viewport);
        SubtitleView findViewById2 = view.findViewById(R.id.preview);
        CaptionAppearanceFragment.applyCaptionProperties(this.mCaptioningManager, findViewById2, findViewById, getValueAt(i));
        findViewById2.setTextSize(getContext().getResources().getDisplayMetrics().density * 32.0f);
        CharSequence titleAt = getTitleAt(i);
        if (titleAt != null) {
            ((TextView) view.findViewById(R.id.summary)).setText(titleAt);
        }
    }
}
