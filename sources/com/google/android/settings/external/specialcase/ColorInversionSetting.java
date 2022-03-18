package com.google.android.settings.external.specialcase;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.Settings;
import androidx.window.R;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.slices.SliceData;
import com.google.android.settings.external.ExternalSettingsContract;
import com.google.android.settings.external.Queryable;
/* loaded from: classes2.dex */
public class ColorInversionSetting implements Queryable {
    public Cursor getAccessCursor(Context context, SliceData sliceData) {
        int currentValue = getCurrentValue(context.getContentResolver());
        String intentString = getIntentString(context, "color_inversion", AccessibilitySettings.class, getScreenTitle(context), R.string.menu_key_accessibility);
        MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_QUERY_COLUMNS);
        matrixCursor.newRow().add("existing_value", Integer.valueOf(currentValue)).add("availability", 0).add("intent", intentString);
        return matrixCursor;
    }

    @Override // com.google.android.settings.external.Queryable
    public Cursor getUpdateCursor(Context context, SliceData sliceData, int i) {
        validateInput(i);
        ContentResolver contentResolver = context.getContentResolver();
        int currentValue = getCurrentValue(contentResolver);
        String intentString = getIntentString(context, "color_inversion", AccessibilitySettings.class, getScreenTitle(context), R.string.menu_key_accessibility);
        if (!shouldChangeValue(0, currentValue, i) || !Settings.Secure.putInt(contentResolver, "accessibility_display_inversion_enabled", i)) {
            i = currentValue;
        }
        MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_UPDATE_COLUMNS);
        matrixCursor.newRow().add("newValue", Integer.valueOf(i)).add("existing_value", Integer.valueOf(currentValue)).add("availability", 0).add("intent", intentString);
        return matrixCursor;
    }

    private void validateInput(int i) {
        if (i != 1 && i != 0) {
            throw new IllegalArgumentException("Unexpected value for display inversion. Expected 0 or 1, but found: " + i);
        }
    }

    private int getCurrentValue(ContentResolver contentResolver) {
        return Settings.Secure.getInt(contentResolver, "accessibility_display_inversion_enabled", 0);
    }

    private String getScreenTitle(Context context) {
        return context.getString(R.string.accessibility_settings);
    }
}
