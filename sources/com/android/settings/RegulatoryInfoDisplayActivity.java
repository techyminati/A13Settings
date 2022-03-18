package com.android.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.SystemProperties;
import android.text.TextUtils;
import java.util.Locale;
/* loaded from: classes.dex */
public class RegulatoryInfoDisplayActivity extends Activity implements DialogInterface.OnDismissListener {
    private final String REGULATORY_INFO_RESOURCE = "regulatory_info";

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0043, code lost:
        if (r4.getIntrinsicHeight() > 2) goto L_0x0047;
     */
    @Override // android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onCreate(android.os.Bundle r9) {
        /*
            r8 = this;
            super.onCreate(r9)
            androidx.appcompat.app.AlertDialog$Builder r9 = new androidx.appcompat.app.AlertDialog$Builder
            r9.<init>(r8)
            r0 = 2130972814(0x7f04108e, float:1.7554405E38)
            androidx.appcompat.app.AlertDialog$Builder r9 = r9.setTitle(r0)
            androidx.appcompat.app.AlertDialog$Builder r9 = r9.setOnDismissListener(r8)
            r0 = 17039370(0x104000a, float:2.42446E-38)
            r1 = 0
            androidx.appcompat.app.AlertDialog$Builder r9 = r9.setPositiveButton(r0, r1)
            java.lang.String r0 = r8.getRegulatoryInfoImageFileName()
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)
            r2 = 1
            r3 = 0
            if (r0 == 0) goto L_0x0029
            r4 = r2
            goto L_0x002a
        L_0x0029:
            r4 = r3
        L_0x002a:
            if (r4 != 0) goto L_0x0031
            int r5 = r8.getResourceId()
            goto L_0x0032
        L_0x0031:
            r5 = r3
        L_0x0032:
            if (r5 == 0) goto L_0x0049
            android.graphics.drawable.Drawable r4 = r8.getDrawable(r5)     // Catch: NotFoundException -> 0x004a
            int r6 = r4.getIntrinsicWidth()     // Catch: NotFoundException -> 0x004a
            r7 = 2
            if (r6 <= r7) goto L_0x0046
            int r4 = r4.getIntrinsicHeight()     // Catch: NotFoundException -> 0x004a
            if (r4 <= r7) goto L_0x0046
            goto L_0x0047
        L_0x0046:
            r2 = r3
        L_0x0047:
            r3 = r2
            goto L_0x004a
        L_0x0049:
            r3 = r4
        L_0x004a:
            android.content.res.Resources r2 = r8.getResources()
            r4 = 2130972813(0x7f04108d, float:1.7554403E38)
            java.lang.CharSequence r2 = r2.getText(r4)
            if (r3 == 0) goto L_0x007b
            android.view.LayoutInflater r8 = r8.getLayoutInflater()
            r2 = 2131100122(0x7f0601da, float:1.7812617E38)
            android.view.View r8 = r8.inflate(r2, r1)
            r1 = 2131559598(0x7f0d04ae, float:1.8744545E38)
            android.view.View r1 = r8.findViewById(r1)
            android.widget.ImageView r1 = (android.widget.ImageView) r1
            if (r0 == 0) goto L_0x0071
            r1.setImageBitmap(r0)
            goto L_0x0074
        L_0x0071:
            r1.setImageResource(r5)
        L_0x0074:
            r9.setView(r8)
            r9.show()
            goto L_0x009a
        L_0x007b:
            int r0 = r2.length()
            if (r0 <= 0) goto L_0x0097
            r9.setMessage(r2)
            androidx.appcompat.app.AlertDialog r8 = r9.show()
            r9 = 16908299(0x102000b, float:2.387726E-38)
            android.view.View r8 = r8.findViewById(r9)
            android.widget.TextView r8 = (android.widget.TextView) r8
            r9 = 17
            r8.setGravity(r9)
            goto L_0x009a
        L_0x0097:
            r8.finish()
        L_0x009a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.RegulatoryInfoDisplayActivity.onCreate(android.os.Bundle):void");
    }

    int getResourceId() {
        int identifier = getResources().getIdentifier("regulatory_info", "drawable", getPackageName());
        String sku = getSku();
        if (!TextUtils.isEmpty(sku)) {
            int identifier2 = getResources().getIdentifier("regulatory_info_" + sku.toLowerCase(), "drawable", getPackageName());
            if (identifier2 != 0) {
                identifier = identifier2;
            }
        }
        String coo = getCoo();
        if (TextUtils.isEmpty(coo) || TextUtils.isEmpty(sku)) {
            return identifier;
        }
        int identifier3 = getResources().getIdentifier("regulatory_info_" + sku.toLowerCase() + "_" + coo.toLowerCase(), "drawable", getPackageName());
        return identifier3 != 0 ? identifier3 : identifier;
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    private String getCoo() {
        return SystemProperties.get("ro.boot.hardware.coo", "");
    }

    private String getSku() {
        return SystemProperties.get("ro.boot.hardware.sku", "");
    }

    private String getRegulatoryInfoImageFileName() {
        String sku = getSku();
        return TextUtils.isEmpty(sku) ? "/data/misc/elabel/regulatory_info.png" : String.format(Locale.US, "/data/misc/elabel/regulatory_info_%s.png", sku.toLowerCase());
    }
}
