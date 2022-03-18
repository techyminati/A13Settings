package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class Chain {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i) {
        ChainHead[] chainHeadArr;
        int i2;
        int i3;
        if (i == 0) {
            int i4 = constraintWidgetContainer.mHorizontalChainsSize;
            chainHeadArr = constraintWidgetContainer.mHorizontalChainsArray;
            i2 = i4;
            i3 = 0;
        } else {
            i3 = 2;
            i2 = constraintWidgetContainer.mVerticalChainsSize;
            chainHeadArr = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i5 = 0; i5 < i2; i5++) {
            ChainHead chainHead = chainHeadArr[i5];
            chainHead.define();
            applyChainConstraints(constraintWidgetContainer, linearSystem, i, i3, chainHead);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x002d, code lost:
        if (r8 == 2) goto L_0x003e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x003c, code lost:
        if (r8 == 2) goto L_0x003e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x003e, code lost:
        r5 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0040, code lost:
        r5 = false;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:102:0x0195  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x01b1  */
    /* JADX WARN: Removed duplicated region for block: B:115:0x01ce  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x025b A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:153:0x02b4 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:211:0x03a5  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x03b2 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:224:0x03c5  */
    /* JADX WARN: Removed duplicated region for block: B:269:0x048c  */
    /* JADX WARN: Removed duplicated region for block: B:274:0x04c1  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x04d4 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:283:0x04e6  */
    /* JADX WARN: Removed duplicated region for block: B:284:0x04e9  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x04ef  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x04f2  */
    /* JADX WARN: Removed duplicated region for block: B:290:0x04f6  */
    /* JADX WARN: Removed duplicated region for block: B:295:0x0506  */
    /* JADX WARN: Removed duplicated region for block: B:297:0x050c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:308:0x03a6 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:318:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r2v56, types: [androidx.constraintlayout.solver.widgets.ConstraintWidget] */
    /* JADX WARN: Type inference failed for: r7v33 */
    /* JADX WARN: Unknown variable types count: 1 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void applyChainConstraints(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r39, androidx.constraintlayout.solver.LinearSystem r40, int r41, int r42, androidx.constraintlayout.solver.widgets.ChainHead r43) {
        /*
            Method dump skipped, instructions count: 1325
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.Chain.applyChainConstraints(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer, androidx.constraintlayout.solver.LinearSystem, int, int, androidx.constraintlayout.solver.widgets.ChainHead):void");
    }
}
