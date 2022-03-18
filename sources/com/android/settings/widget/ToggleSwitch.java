package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;
/* loaded from: classes.dex */
public class ToggleSwitch extends Switch {
    private OnBeforeCheckedChangeListener mOnBeforeListener;

    /* loaded from: classes.dex */
    public interface OnBeforeCheckedChangeListener {
        boolean onBeforeCheckedChanged(ToggleSwitch toggleSwitch, boolean z);
    }

    public ToggleSwitch(Context context) {
        super(context);
    }

    public ToggleSwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ToggleSwitch(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public ToggleSwitch(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setOnBeforeCheckedChangeListener(OnBeforeCheckedChangeListener onBeforeCheckedChangeListener) {
        this.mOnBeforeListener = onBeforeCheckedChangeListener;
    }

    @Override // android.widget.Switch, android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean z) {
        OnBeforeCheckedChangeListener onBeforeCheckedChangeListener = this.mOnBeforeListener;
        if (onBeforeCheckedChangeListener == null || !onBeforeCheckedChangeListener.onBeforeCheckedChanged(this, z)) {
            super.setChecked(z);
        }
    }

    public void setCheckedInternal(boolean z) {
        super.setChecked(z);
    }
}
