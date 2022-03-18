package com.google.android.settings.external.specialcase;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.wifi.WifiManager;
import androidx.window.R;
import com.android.settings.slices.SliceData;
import com.android.settings.wifi.WifiSettings;
import com.google.android.settings.external.ExternalSettingsContract;
import com.google.android.settings.external.Queryable;
/* loaded from: classes2.dex */
public class WifiSetting implements Queryable {
    private int getIconResource() {
        return R.drawable.ic_settings_wireless;
    }

    public Cursor getAccessCursor(Context context, SliceData sliceData) {
        boolean isWifiEnabled = ((WifiManager) context.getSystemService("wifi")).isWifiEnabled();
        String intentString = getIntentString(context, "master_wifi_toggle", WifiSettings.class, getScreenTitle(context), R.string.menu_key_network);
        int iconResource = getIconResource();
        MatrixCursor matrixCursor = new MatrixCursor(ExternalSettingsContract.EXTERNAL_SETTINGS_QUERY_COLUMNS);
        matrixCursor.newRow().add("existing_value", Integer.valueOf(isWifiEnabled ? 1 : 0)).add("availability", 0).add("intent", intentString).add("icon", Integer.valueOf(iconResource));
        return matrixCursor;
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0031, code lost:
        if (r9.setWifiEnabled(r7) != false) goto L_0x0035;
     */
    /* JADX WARN: Type inference failed for: r0v0, types: [int, boolean] */
    /* JADX WARN: Unknown variable types count: 1 */
    @Override // com.google.android.settings.external.Queryable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.database.Cursor getUpdateCursor(android.content.Context r8, com.android.settings.slices.SliceData r9, int r10) {
        /*
            r7 = this;
            java.lang.String r9 = "wifi"
            java.lang.Object r9 = r8.getSystemService(r9)
            android.net.wifi.WifiManager r9 = (android.net.wifi.WifiManager) r9
            boolean r0 = r9.isWifiEnabled()
            java.lang.Class<com.android.settings.wifi.WifiSettings> r4 = com.android.settings.wifi.WifiSettings.class
            java.lang.String r5 = r7.getScreenTitle(r8)
            r6 = 2130971988(0x7f040d54, float:1.755273E38)
            java.lang.String r3 = "master_wifi_toggle"
            r1 = r7
            r2 = r8
            java.lang.String r8 = r1.getIntentString(r2, r3, r4, r5, r6)
            int r1 = r7.getIconResource()
            r2 = 0
            boolean r7 = r7.shouldChangeValue(r2, r0, r10)
            if (r7 == 0) goto L_0x0034
            r7 = 1
            if (r10 != r7) goto L_0x002c
            goto L_0x002d
        L_0x002c:
            r7 = r2
        L_0x002d:
            boolean r7 = r9.setWifiEnabled(r7)
            if (r7 == 0) goto L_0x0034
            goto L_0x0035
        L_0x0034:
            r10 = r0
        L_0x0035:
            android.database.MatrixCursor r7 = new android.database.MatrixCursor
            java.lang.String[] r9 = com.google.android.settings.external.ExternalSettingsContract.EXTERNAL_SETTINGS_UPDATE_COLUMNS
            r7.<init>(r9)
            android.database.MatrixCursor$RowBuilder r9 = r7.newRow()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            java.lang.String r3 = "newValue"
            android.database.MatrixCursor$RowBuilder r9 = r9.add(r3, r10)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r0)
            java.lang.String r0 = "existing_value"
            android.database.MatrixCursor$RowBuilder r9 = r9.add(r0, r10)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r2)
            java.lang.String r0 = "availability"
            android.database.MatrixCursor$RowBuilder r9 = r9.add(r0, r10)
            java.lang.String r10 = "intent"
            android.database.MatrixCursor$RowBuilder r8 = r9.add(r10, r8)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r1)
            java.lang.String r10 = "icon"
            r8.add(r10, r9)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.settings.external.specialcase.WifiSetting.getUpdateCursor(android.content.Context, com.android.settings.slices.SliceData, int):android.database.Cursor");
    }

    private String getScreenTitle(Context context) {
        return context.getString(R.string.wifi_settings);
    }
}
