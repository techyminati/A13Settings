package com.google.android.settings.widget;

import android.content.Context;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import androidx.window.R;
import com.google.android.setupdesign.view.IllustrationVideoView;
/* loaded from: classes2.dex */
public class MarlinColorFingerprintLocationAnimationVideoView extends IllustrationVideoView {
    public MarlinColorFingerprintLocationAnimationVideoView(Context context, AttributeSet attributeSet) {
        super(getDeviceColorTheme(context), attributeSet);
    }

    private static Context getDeviceColorTheme(Context context) {
        int i;
        String str = SystemProperties.get("ro.boot.hardware.color");
        if ("BLU00".equals(str)) {
            i = R.style.MarlinColors_Blue;
        } else if ("SLV00".equals(str)) {
            i = R.style.MarlinColors_Silver;
        } else {
            i = "GRA00".equals(str) ? R.style.MarlinColors_Graphite : R.style.MarlinColors;
        }
        return new ContextThemeWrapper(context, i);
    }
}
