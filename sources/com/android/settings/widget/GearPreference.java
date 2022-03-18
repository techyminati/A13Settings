package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
import com.android.settingslib.RestrictedPreference;
/* loaded from: classes.dex */
public class GearPreference extends RestrictedPreference implements View.OnClickListener {
    protected boolean mGearState = true;
    private OnGearClickListener mOnGearClickListener;

    /* loaded from: classes.dex */
    public interface OnGearClickListener {
        void onGearClick(GearPreference gearPreference);
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R.layout.preference_widget_gear;
    }

    public GearPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setOnGearClickListener(OnGearClickListener onGearClickListener) {
        this.mOnGearClickListener = onGearClickListener;
        notifyChanged();
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return this.mOnGearClickListener == null;
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R.id.settings_button);
        if (this.mOnGearClickListener != null) {
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(this);
        } else {
            findViewById.setVisibility(8);
            findViewById.setOnClickListener(null);
        }
        findViewById.setEnabled(this.mGearState);
    }

    public void onClick(View view) {
        OnGearClickListener onGearClickListener;
        if (view.getId() == R.id.settings_button && (onGearClickListener = this.mOnGearClickListener) != null) {
            onGearClickListener.onGearClick(this);
        }
    }
}
