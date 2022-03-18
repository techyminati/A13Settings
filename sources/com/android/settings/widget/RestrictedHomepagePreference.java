package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.window.R;
import com.android.settingslib.RestrictedTopLevelPreference;
/* loaded from: classes.dex */
public class RestrictedHomepagePreference extends RestrictedTopLevelPreference {
    public RestrictedHomepagePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(R.layout.homepage_preference);
    }

    public RestrictedHomepagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(R.layout.homepage_preference);
    }

    public RestrictedHomepagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.homepage_preference);
    }

    public RestrictedHomepagePreference(Context context) {
        super(context);
        setLayoutResource(R.layout.homepage_preference);
    }
}
