package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.window.R;
/* loaded from: classes.dex */
public class CardPreference extends Preference {
    public CardPreference(Context context) {
        this(context, null);
    }

    public CardPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R.attr.cardPreferenceStyle);
    }
}
