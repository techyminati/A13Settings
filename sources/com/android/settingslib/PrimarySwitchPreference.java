package com.android.settingslib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import androidx.annotation.Keep;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class PrimarySwitchPreference extends RestrictedPreference {
    private boolean mChecked;
    private boolean mCheckedSet;
    private boolean mEnableSwitch = true;
    private Switch mSwitch;

    public PrimarySwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public PrimarySwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public PrimarySwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PrimarySwitchPreference(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.widget.TwoTargetPreference
    public int getSecondTargetResId() {
        return R$layout.preference_widget_primary_switch;
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        int i = R$id.switchWidget;
        View findViewById = preferenceViewHolder.findViewById(i);
        if (findViewById != null) {
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.PrimarySwitchPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (PrimarySwitchPreference.this.mSwitch == null || PrimarySwitchPreference.this.mSwitch.isEnabled()) {
                        PrimarySwitchPreference primarySwitchPreference = PrimarySwitchPreference.this;
                        primarySwitchPreference.setChecked(!primarySwitchPreference.mChecked);
                        PrimarySwitchPreference primarySwitchPreference2 = PrimarySwitchPreference.this;
                        if (!primarySwitchPreference2.callChangeListener(Boolean.valueOf(primarySwitchPreference2.mChecked))) {
                            PrimarySwitchPreference primarySwitchPreference3 = PrimarySwitchPreference.this;
                            primarySwitchPreference3.setChecked(!primarySwitchPreference3.mChecked);
                            return;
                        }
                        PrimarySwitchPreference primarySwitchPreference4 = PrimarySwitchPreference.this;
                        primarySwitchPreference4.persistBoolean(primarySwitchPreference4.mChecked);
                    }
                }
            });
            findViewById.setOnTouchListener(PrimarySwitchPreference$$ExternalSyntheticLambda0.INSTANCE);
        }
        Switch r4 = (Switch) preferenceViewHolder.findViewById(i);
        this.mSwitch = r4;
        if (r4 != null) {
            r4.setContentDescription(getTitle());
            this.mSwitch.setChecked(this.mChecked);
            this.mSwitch.setEnabled(this.mEnableSwitch);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onBindViewHolder$0(View view, MotionEvent motionEvent) {
        return motionEvent.getActionMasked() == 2;
    }

    public boolean isChecked() {
        return this.mSwitch != null && this.mChecked;
    }

    @Keep
    public Boolean getCheckedState() {
        if (this.mCheckedSet) {
            return Boolean.valueOf(this.mChecked);
        }
        return null;
    }

    public void setChecked(boolean z) {
        if ((this.mChecked != z) || !this.mCheckedSet) {
            this.mChecked = z;
            this.mCheckedSet = true;
            Switch r2 = this.mSwitch;
            if (r2 != null) {
                r2.setChecked(z);
            }
        }
    }

    public void setSwitchEnabled(boolean z) {
        this.mEnableSwitch = z;
        Switch r0 = this.mSwitch;
        if (r0 != null) {
            r0.setEnabled(z);
        }
    }

    @Override // com.android.settingslib.RestrictedPreference
    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        super.setDisabledByAdmin(enforcedAdmin);
        setSwitchEnabled(enforcedAdmin == null);
    }

    public Switch getSwitch() {
        return this.mSwitch;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return getSecondTargetResId() == 0;
    }
}
