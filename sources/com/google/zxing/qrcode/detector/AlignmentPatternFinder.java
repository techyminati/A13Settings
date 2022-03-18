package com.google.zxing.qrcode.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
final class AlignmentPatternFinder {
    private final int height;
    private final BitMatrix image;
    private final float moduleSize;
    private final ResultPointCallback resultPointCallback;
    private final int startX;
    private final int startY;
    private final int width;
    private final List<AlignmentPattern> possibleCenters = new ArrayList(5);
    private final int[] crossCheckStateCount = new int[3];

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlignmentPatternFinder(BitMatrix bitMatrix, int i, int i2, int i3, int i4, float f, ResultPointCallback resultPointCallback) {
        this.image = bitMatrix;
        this.startX = i;
        this.startY = i2;
        this.width = i3;
        this.height = i4;
        this.moduleSize = f;
        this.resultPointCallback = resultPointCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlignmentPattern find() throws NotFoundException {
        AlignmentPattern handlePossibleCenter;
        AlignmentPattern handlePossibleCenter2;
        int i = this.startX;
        int i2 = this.height;
        int i3 = this.width + i;
        int i4 = this.startY + (i2 >> 1);
        int[] iArr = new int[3];
        for (int i5 = 0; i5 < i2; i5++) {
            int i6 = ((i5 & 1) == 0 ? (i5 + 1) >> 1 : -((i5 + 1) >> 1)) + i4;
            iArr[0] = 0;
            iArr[1] = 0;
            iArr[2] = 0;
            int i7 = i;
            while (i7 < i3 && !this.image.get(i7, i6)) {
                i7++;
            }
            int i8 = 0;
            while (i7 < i3) {
                if (!this.image.get(i7, i6)) {
                    if (i8 == 1) {
                        i8++;
                    }
                    iArr[i8] = iArr[i8] + 1;
                } else if (i8 == 1) {
                    iArr[i8] = iArr[i8] + 1;
                } else if (i8 != 2) {
                    i8++;
                    iArr[i8] = iArr[i8] + 1;
                } else if (foundPatternCross(iArr) && (handlePossibleCenter2 = handlePossibleCenter(iArr, i6, i7)) != null) {
                    return handlePossibleCenter2;
                } else {
                    iArr[0] = iArr[2];
                    iArr[1] = 1;
                    iArr[2] = 0;
                    i8 = 1;
                }
                i7++;
            }
            if (foundPatternCross(iArr) && (handlePossibleCenter = handlePossibleCenter(iArr, i6, i3)) != null) {
                return handlePossibleCenter;
            }
        }
        if (!this.possibleCenters.isEmpty()) {
            return this.possibleCenters.get(0);
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static float centerFromEnd(int[] iArr, int i) {
        return (i - iArr[2]) - (iArr[1] / 2.0f);
    }

    private boolean foundPatternCross(int[] iArr) {
        float f = this.moduleSize;
        float f2 = f / 2.0f;
        for (int i = 0; i < 3; i++) {
            if (Math.abs(f - iArr[i]) >= f2) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x0062, code lost:
        if (r2[1] <= r12) goto L_0x0065;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0065, code lost:
        if (r10 >= r1) goto L_0x0079;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x006b, code lost:
        if (r0.get(r11, r10) != false) goto L_0x0079;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x006f, code lost:
        if (r2[2] > r12) goto L_0x0079;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0071, code lost:
        r2[2] = r2[2] + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x007b, code lost:
        if (r2[2] <= r12) goto L_0x007e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x007d, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x008e, code lost:
        if ((java.lang.Math.abs(((r2[0] + r2[1]) + r2[2]) - r13) * 5) < (r13 * 2)) goto L_0x0091;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0090, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0095, code lost:
        if (foundPatternCross(r2) == false) goto L_?;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x009b, code lost:
        return centerFromEnd(r2, r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:?, code lost:
        return Float.NaN;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float crossCheckVertical(int r10, int r11, int r12, int r13) {
        /*
            r9 = this;
            com.google.zxing.common.BitMatrix r0 = r9.image
            int r1 = r0.getHeight()
            int[] r2 = r9.crossCheckStateCount
            r3 = 0
            r2[r3] = r3
            r4 = 1
            r2[r4] = r3
            r5 = 2
            r2[r5] = r3
            r6 = r10
        L_0x0012:
            if (r6 < 0) goto L_0x0026
            boolean r7 = r0.get(r11, r6)
            if (r7 == 0) goto L_0x0026
            r7 = r2[r4]
            if (r7 > r12) goto L_0x0026
            r7 = r2[r4]
            int r7 = r7 + r4
            r2[r4] = r7
            int r6 = r6 + (-1)
            goto L_0x0012
        L_0x0026:
            r7 = 2143289344(0x7fc00000, float:NaN)
            if (r6 < 0) goto L_0x009b
            r8 = r2[r4]
            if (r8 <= r12) goto L_0x0030
            goto L_0x009b
        L_0x0030:
            if (r6 < 0) goto L_0x0044
            boolean r8 = r0.get(r11, r6)
            if (r8 != 0) goto L_0x0044
            r8 = r2[r3]
            if (r8 > r12) goto L_0x0044
            r8 = r2[r3]
            int r8 = r8 + r4
            r2[r3] = r8
            int r6 = r6 + (-1)
            goto L_0x0030
        L_0x0044:
            r6 = r2[r3]
            if (r6 <= r12) goto L_0x0049
            return r7
        L_0x0049:
            int r10 = r10 + r4
        L_0x004a:
            if (r10 >= r1) goto L_0x005e
            boolean r6 = r0.get(r11, r10)
            if (r6 == 0) goto L_0x005e
            r6 = r2[r4]
            if (r6 > r12) goto L_0x005e
            r6 = r2[r4]
            int r6 = r6 + r4
            r2[r4] = r6
            int r10 = r10 + 1
            goto L_0x004a
        L_0x005e:
            if (r10 == r1) goto L_0x009b
            r6 = r2[r4]
            if (r6 <= r12) goto L_0x0065
            goto L_0x009b
        L_0x0065:
            if (r10 >= r1) goto L_0x0079
            boolean r6 = r0.get(r11, r10)
            if (r6 != 0) goto L_0x0079
            r6 = r2[r5]
            if (r6 > r12) goto L_0x0079
            r6 = r2[r5]
            int r6 = r6 + r4
            r2[r5] = r6
            int r10 = r10 + 1
            goto L_0x0065
        L_0x0079:
            r11 = r2[r5]
            if (r11 <= r12) goto L_0x007e
            return r7
        L_0x007e:
            r11 = r2[r3]
            r12 = r2[r4]
            int r11 = r11 + r12
            r12 = r2[r5]
            int r11 = r11 + r12
            int r11 = r11 - r13
            int r11 = java.lang.Math.abs(r11)
            int r11 = r11 * 5
            int r13 = r13 * r5
            if (r11 < r13) goto L_0x0091
            return r7
        L_0x0091:
            boolean r9 = r9.foundPatternCross(r2)
            if (r9 == 0) goto L_0x009b
            float r7 = centerFromEnd(r2, r10)
        L_0x009b:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.AlignmentPatternFinder.crossCheckVertical(int, int, int, int):float");
    }

    private AlignmentPattern handlePossibleCenter(int[] iArr, int i, int i2) {
        int i3 = iArr[0] + iArr[1] + iArr[2];
        float centerFromEnd = centerFromEnd(iArr, i2);
        float crossCheckVertical = crossCheckVertical(i, (int) centerFromEnd, iArr[1] * 2, i3);
        if (Float.isNaN(crossCheckVertical)) {
            return null;
        }
        float f = ((iArr[0] + iArr[1]) + iArr[2]) / 3.0f;
        for (AlignmentPattern alignmentPattern : this.possibleCenters) {
            if (alignmentPattern.aboutEquals(f, crossCheckVertical, centerFromEnd)) {
                return alignmentPattern.combineEstimate(crossCheckVertical, centerFromEnd, f);
            }
        }
        AlignmentPattern alignmentPattern2 = new AlignmentPattern(centerFromEnd, crossCheckVertical, f);
        this.possibleCenters.add(alignmentPattern2);
        ResultPointCallback resultPointCallback = this.resultPointCallback;
        if (resultPointCallback == null) {
            return null;
        }
        resultPointCallback.foundPossibleResultPoint(alignmentPattern2);
        return null;
    }
}
