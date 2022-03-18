package com.google.android.settings.external.specialcase;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.hardware.display.ColorDisplayManager;
import androidx.window.R;
import com.android.settings.display.NightDisplaySettings;
import com.android.settings.slices.SliceData;
import com.google.android.settings.external.ExternalSettingsContract;
import com.google.android.settings.external.Queryable;
/* loaded from: classes2.dex */
public class NightDisplaySetting implements Queryable {
    private int getIconResource() {
        return 17302826;
    }

    public Cursor getAccessCursor(Context context, SliceData sliceData) {
        boolean isNightDisplayActivated = ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).isNightDisplayActivated();
        int i = ColorDisplayManager.isNightDisplayAvailable(context) ? 0 : 2;
        String intentString = getIntentString(context, "night_display", NightDisplaySettings.class, getScreenTitle(context), R.string.menu_key_display);
        int iconResource = getIconResource();
        MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_QUERY_COLUMNS);
        matrixCursor.newRow().add("existing_value", Integer.valueOf(isNightDisplayActivated ? 1 : 0)).add("availability", Integer.valueOf(i)).add("intent", intentString).add("icon", Integer.valueOf(iconResource));
        return matrixCursor;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [int, boolean] */
    /* JADX WARN: Unknown variable types count: 1 */
    @Override // com.google.android.settings.external.Queryable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.database.Cursor getUpdateCursor(android.content.Context r10, com.android.settings.slices.SliceData r11, int r12) {
        /*
            r9 = this;
            java.lang.Class<android.hardware.display.ColorDisplayManager> r11 = android.hardware.display.ColorDisplayManager.class
            java.lang.Object r11 = r10.getSystemService(r11)
            android.hardware.display.ColorDisplayManager r11 = (android.hardware.display.ColorDisplayManager) r11
            boolean r0 = r11.isNightDisplayActivated()
            boolean r1 = android.hardware.display.ColorDisplayManager.isNightDisplayAvailable(r10)
            r2 = 0
            if (r1 == 0) goto L_0x0015
            r1 = r2
            goto L_0x0016
        L_0x0015:
            r1 = 2
        L_0x0016:
            java.lang.Class<com.android.settings.display.NightDisplaySettings> r6 = com.android.settings.display.NightDisplaySettings.class
            java.lang.String r7 = r9.getScreenTitle(r10)
            r8 = 2130971985(0x7f040d51, float:1.7552724E38)
            java.lang.String r5 = "night_display"
            r3 = r9
            r4 = r10
            java.lang.String r10 = r3.getIntentString(r4, r5, r6, r7, r8)
            int r3 = r9.getIconResource()
            r4 = 1
            if (r12 != r4) goto L_0x002f
            r2 = r4
        L_0x002f:
            boolean r9 = r9.shouldChangeValue(r1, r0, r12)
            if (r9 == 0) goto L_0x003c
            boolean r9 = r11.setNightDisplayActivated(r2)
            if (r9 == 0) goto L_0x003c
            goto L_0x003d
        L_0x003c:
            r12 = r0
        L_0x003d:
            android.database.MatrixCursor r9 = new android.database.MatrixCursor
            java.lang.String[] r11 = com.google.android.settings.external.ExternalSettingsContract.EXTERNAL_SETTINGS_UPDATE_COLUMNS
            r9.<init>(r11)
            android.database.MatrixCursor$RowBuilder r11 = r9.newRow()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.String r2 = "newValue"
            android.database.MatrixCursor$RowBuilder r11 = r11.add(r2, r12)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r0)
            java.lang.String r0 = "existing_value"
            android.database.MatrixCursor$RowBuilder r11 = r11.add(r0, r12)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r1)
            java.lang.String r0 = "availability"
            android.database.MatrixCursor$RowBuilder r11 = r11.add(r0, r12)
            java.lang.String r12 = "intent"
            android.database.MatrixCursor$RowBuilder r10 = r11.add(r12, r10)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r3)
            java.lang.String r12 = "icon"
            r10.add(r12, r11)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.settings.external.specialcase.NightDisplaySetting.getUpdateCursor(android.content.Context, com.android.settings.slices.SliceData, int):android.database.Cursor");
    }

    private String getScreenTitle(Context context) {
        return context.getString(R.string.night_display_title);
    }
}
