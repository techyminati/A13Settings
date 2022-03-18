package com.android.settings.network.telephony;

import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import com.android.internal.telephony.OperatorInfo;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public final class CellInfoUtil {
    public static String getNetworkTitle(CellIdentity cellIdentity, String str) {
        if (cellIdentity != null) {
            String objects = Objects.toString(cellIdentity.getOperatorAlphaLong(), "");
            if (TextUtils.isEmpty(objects)) {
                objects = Objects.toString(cellIdentity.getOperatorAlphaShort(), "");
            }
            if (!TextUtils.isEmpty(objects)) {
                return objects;
            }
        }
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return BidiFormatter.getInstance().unicodeWrap(str, TextDirectionHeuristics.LTR);
    }

    public static CellIdentity getCellIdentity(CellInfo cellInfo) {
        if (cellInfo == null) {
            return null;
        }
        if (cellInfo instanceof CellInfoGsm) {
            return ((CellInfoGsm) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoCdma) {
            return ((CellInfoCdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoWcdma) {
            return ((CellInfoWcdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoTdscdma) {
            return ((CellInfoTdscdma) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoLte) {
            return ((CellInfoLte) cellInfo).getCellIdentity();
        }
        if (cellInfo instanceof CellInfoNr) {
            return ((CellInfoNr) cellInfo).getCellIdentity();
        }
        return null;
    }

    public static CellInfo convertOperatorInfoToCellInfo(OperatorInfo operatorInfo) {
        String str;
        String operatorNumeric = operatorInfo.getOperatorNumeric();
        String str2 = null;
        if (operatorNumeric == null || !operatorNumeric.matches("^[0-9]{5,6}$")) {
            str = null;
        } else {
            String substring = operatorNumeric.substring(0, 3);
            str = operatorNumeric.substring(3);
            str2 = substring;
        }
        CellIdentityGsm cellIdentityGsm = new CellIdentityGsm(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, str2, str, operatorInfo.getOperatorAlphaLong(), operatorInfo.getOperatorAlphaShort(), Collections.emptyList());
        CellInfoGsm cellInfoGsm = new CellInfoGsm();
        cellInfoGsm.setCellIdentity(cellIdentityGsm);
        return cellInfoGsm;
    }

    public static String cellInfoListToString(List<CellInfo> list) {
        return (String) list.stream().map(CellInfoUtil$$ExternalSyntheticLambda0.INSTANCE).collect(Collectors.joining(", "));
    }

    public static String cellInfoToString(CellInfo cellInfo) {
        CharSequence charSequence;
        String simpleName = cellInfo.getClass().getSimpleName();
        CellIdentity cellIdentity = getCellIdentity(cellInfo);
        String cellIdentityMcc = getCellIdentityMcc(cellIdentity);
        String cellIdentityMnc = getCellIdentityMnc(cellIdentity);
        CharSequence charSequence2 = null;
        if (cellIdentity != null) {
            charSequence2 = cellIdentity.getOperatorAlphaLong();
            charSequence = cellIdentity.getOperatorAlphaShort();
        } else {
            charSequence = null;
        }
        return String.format("{CellType = %s, isRegistered = %b, mcc = %s, mnc = %s, alphaL = %s, alphaS = %s}", simpleName, Boolean.valueOf(cellInfo.isRegistered()), cellIdentityMcc, cellIdentityMnc, charSequence2, charSequence);
    }

    public static String getCellIdentityMccMnc(CellIdentity cellIdentity) {
        String cellIdentityMcc = getCellIdentityMcc(cellIdentity);
        String cellIdentityMnc = getCellIdentityMnc(cellIdentity);
        if (cellIdentityMcc == null || cellIdentityMnc == null) {
            return null;
        }
        return cellIdentityMcc + cellIdentityMnc;
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String getCellIdentityMcc(android.telephony.CellIdentity r2) {
        /*
            r0 = 0
            if (r2 == 0) goto L_0x003a
            boolean r1 = r2 instanceof android.telephony.CellIdentityGsm
            if (r1 == 0) goto L_0x000e
            android.telephony.CellIdentityGsm r2 = (android.telephony.CellIdentityGsm) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x000e:
            boolean r1 = r2 instanceof android.telephony.CellIdentityWcdma
            if (r1 == 0) goto L_0x0019
            android.telephony.CellIdentityWcdma r2 = (android.telephony.CellIdentityWcdma) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x0019:
            boolean r1 = r2 instanceof android.telephony.CellIdentityTdscdma
            if (r1 == 0) goto L_0x0024
            android.telephony.CellIdentityTdscdma r2 = (android.telephony.CellIdentityTdscdma) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x0024:
            boolean r1 = r2 instanceof android.telephony.CellIdentityLte
            if (r1 == 0) goto L_0x002f
            android.telephony.CellIdentityLte r2 = (android.telephony.CellIdentityLte) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x002f:
            boolean r1 = r2 instanceof android.telephony.CellIdentityNr
            if (r1 == 0) goto L_0x003a
            android.telephony.CellIdentityNr r2 = (android.telephony.CellIdentityNr) r2
            java.lang.String r2 = r2.getMccString()
            goto L_0x003b
        L_0x003a:
            r2 = r0
        L_0x003b:
            if (r2 != 0) goto L_0x003e
            goto L_0x003f
        L_0x003e:
            r0 = r2
        L_0x003f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.CellInfoUtil.getCellIdentityMcc(android.telephony.CellIdentity):java.lang.String");
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:23:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String getCellIdentityMnc(android.telephony.CellIdentity r2) {
        /*
            r0 = 0
            if (r2 == 0) goto L_0x003a
            boolean r1 = r2 instanceof android.telephony.CellIdentityGsm
            if (r1 == 0) goto L_0x000e
            android.telephony.CellIdentityGsm r2 = (android.telephony.CellIdentityGsm) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x000e:
            boolean r1 = r2 instanceof android.telephony.CellIdentityWcdma
            if (r1 == 0) goto L_0x0019
            android.telephony.CellIdentityWcdma r2 = (android.telephony.CellIdentityWcdma) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x0019:
            boolean r1 = r2 instanceof android.telephony.CellIdentityTdscdma
            if (r1 == 0) goto L_0x0024
            android.telephony.CellIdentityTdscdma r2 = (android.telephony.CellIdentityTdscdma) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x0024:
            boolean r1 = r2 instanceof android.telephony.CellIdentityLte
            if (r1 == 0) goto L_0x002f
            android.telephony.CellIdentityLte r2 = (android.telephony.CellIdentityLte) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x002f:
            boolean r1 = r2 instanceof android.telephony.CellIdentityNr
            if (r1 == 0) goto L_0x003a
            android.telephony.CellIdentityNr r2 = (android.telephony.CellIdentityNr) r2
            java.lang.String r2 = r2.getMncString()
            goto L_0x003b
        L_0x003a:
            r2 = r0
        L_0x003b:
            if (r2 != 0) goto L_0x003e
            goto L_0x003f
        L_0x003e:
            r0 = r2
        L_0x003f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.CellInfoUtil.getCellIdentityMnc(android.telephony.CellIdentity):java.lang.String");
    }
}
