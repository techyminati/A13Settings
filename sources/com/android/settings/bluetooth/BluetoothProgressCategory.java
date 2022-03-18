package com.android.settings.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import androidx.window.R;
import com.android.settings.ProgressCategory;
/* loaded from: classes.dex */
public class BluetoothProgressCategory extends ProgressCategory {
    public BluetoothProgressCategory(Context context) {
        super(context);
        init();
    }

    public BluetoothProgressCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public BluetoothProgressCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public BluetoothProgressCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private void init() {
        setEmptyTextRes(R.string.bluetooth_no_devices_found);
    }
}
