package com.google.android.settings.security;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.PreferenceGroup;
import androidx.window.R;
/* loaded from: classes2.dex */
public class LogicalPreferenceGroup extends PreferenceGroup {
    public LogicalPreferenceGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.security_logical_preference_group);
    }
}
