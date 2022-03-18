package com.android.settings.network.telephony;

import android.telephony.CellInfo;
import com.android.internal.telephony.OperatorInfo;
import java.util.function.Function;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class NetworkScanHelper$NetworkScanSyncTask$$ExternalSyntheticLambda0 implements Function {
    public static final /* synthetic */ NetworkScanHelper$NetworkScanSyncTask$$ExternalSyntheticLambda0 INSTANCE = new NetworkScanHelper$NetworkScanSyncTask$$ExternalSyntheticLambda0();

    private /* synthetic */ NetworkScanHelper$NetworkScanSyncTask$$ExternalSyntheticLambda0() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        CellInfo convertOperatorInfoToCellInfo;
        convertOperatorInfoToCellInfo = CellInfoUtil.convertOperatorInfoToCellInfo((OperatorInfo) obj);
        return convertOperatorInfoToCellInfo;
    }
}
