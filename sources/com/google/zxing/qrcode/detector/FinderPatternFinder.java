package com.google.zxing.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public class FinderPatternFinder {
    private boolean hasSkipped;
    private final BitMatrix image;
    private final ResultPointCallback resultPointCallback;
    private final List<FinderPattern> possibleCenters = new ArrayList();
    private final int[] crossCheckStateCount = new int[5];

    public FinderPatternFinder(BitMatrix bitMatrix, ResultPointCallback resultPointCallback) {
        this.image = bitMatrix;
        this.resultPointCallback = resultPointCallback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final FinderPatternInfo find(Map<DecodeHintType, ?> map) throws NotFoundException {
        boolean z = map != null && map.containsKey(DecodeHintType.TRY_HARDER);
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        int i = (height * 3) / 228;
        if (i < 3 || z) {
            i = 3;
        }
        int[] iArr = new int[5];
        int i2 = i - 1;
        boolean z2 = false;
        while (i2 < height && !z2) {
            iArr[0] = 0;
            iArr[1] = 0;
            iArr[2] = 0;
            iArr[3] = 0;
            iArr[4] = 0;
            int i3 = 0;
            int i4 = 0;
            while (i3 < width) {
                if (this.image.get(i3, i2)) {
                    if ((i4 & 1) == 1) {
                        i4++;
                    }
                    iArr[i4] = iArr[i4] + 1;
                } else if ((i4 & 1) != 0) {
                    iArr[i4] = iArr[i4] + 1;
                } else if (i4 == 4) {
                    if (!foundPatternCross(iArr)) {
                        iArr[0] = iArr[2];
                        iArr[1] = iArr[3];
                        iArr[2] = iArr[4];
                        iArr[3] = 1;
                        iArr[4] = 0;
                    } else if (handlePossibleCenter(iArr, i2, i3)) {
                        if (this.hasSkipped) {
                            z2 = haveMultiplyConfirmedCenters();
                        } else {
                            int findRowSkip = findRowSkip();
                            if (findRowSkip > iArr[2]) {
                                i2 += (findRowSkip - iArr[2]) - 2;
                                i3 = width - 1;
                            }
                        }
                        iArr[0] = 0;
                        iArr[1] = 0;
                        iArr[2] = 0;
                        iArr[3] = 0;
                        iArr[4] = 0;
                        i4 = 0;
                        i = 2;
                    } else {
                        iArr[0] = iArr[2];
                        iArr[1] = iArr[3];
                        iArr[2] = iArr[4];
                        iArr[3] = 1;
                        iArr[4] = 0;
                    }
                    i4 = 3;
                } else {
                    i4++;
                    iArr[i4] = iArr[i4] + 1;
                }
                i3++;
            }
            if (foundPatternCross(iArr) && handlePossibleCenter(iArr, i2, width)) {
                i = iArr[0];
                if (this.hasSkipped) {
                    z2 = haveMultiplyConfirmedCenters();
                }
            }
            i2 += i;
        }
        FinderPattern[] selectBestPatterns = selectBestPatterns();
        ResultPoint.orderBestPatterns(selectBestPatterns);
        return new FinderPatternInfo(selectBestPatterns);
    }

    private static float centerFromEnd(int[] iArr, int i) {
        return ((i - iArr[4]) - iArr[3]) - (iArr[2] / 2.0f);
    }

    protected static boolean foundPatternCross(int[] iArr) {
        int i;
        int i2;
        int i3 = 0;
        for (int i4 = 0; i4 < 5; i4++) {
            int i5 = iArr[i4];
            if (i5 == 0) {
                return false;
            }
            i3 += i5;
        }
        return i3 >= 7 && Math.abs(i - (iArr[0] << 8)) < (i2 = (i = (i3 << 8) / 7) / 2) && Math.abs(i - (iArr[1] << 8)) < i2 && Math.abs((i * 3) - (iArr[2] << 8)) < i2 * 3 && Math.abs(i - (iArr[3] << 8)) < i2 && Math.abs(i - (iArr[4] << 8)) < i2;
    }

    private int[] getCrossCheckStateCount() {
        int[] iArr = this.crossCheckStateCount;
        iArr[0] = 0;
        iArr[1] = 0;
        iArr[2] = 0;
        iArr[3] = 0;
        iArr[4] = 0;
        return iArr;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x003a, code lost:
        if (r9[1] <= r12) goto L_0x003e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x003f, code lost:
        if (r2 < 0) goto L_0x0053;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0045, code lost:
        if (r0.get(r11, r2) == false) goto L_0x0053;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0049, code lost:
        if (r9[0] > r12) goto L_0x0053;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004b, code lost:
        r9[0] = r9[0] + 1;
        r2 = r2 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0055, code lost:
        if (r9[0] <= r12) goto L_0x0058;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0057, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0058, code lost:
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0059, code lost:
        if (r10 >= r1) goto L_0x0069;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x005f, code lost:
        if (r0.get(r11, r10) == false) goto L_0x0069;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0061, code lost:
        r9[2] = r9[2] + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0069, code lost:
        if (r10 != r1) goto L_0x006c;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x006b, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x006d, code lost:
        if (r10 >= r1) goto L_0x0081;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0073, code lost:
        if (r0.get(r11, r10) != false) goto L_0x0081;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0077, code lost:
        if (r9[3] >= r12) goto L_0x0081;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0079, code lost:
        r9[3] = r9[3] + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0081, code lost:
        if (r10 == r1) goto L_?;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0085, code lost:
        if (r9[3] < r12) goto L_0x0088;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0089, code lost:
        if (r10 >= r1) goto L_0x009d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x008f, code lost:
        if (r0.get(r11, r10) == false) goto L_0x009d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0093, code lost:
        if (r9[4] >= r12) goto L_0x009d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0095, code lost:
        r9[4] = r9[4] + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x009f, code lost:
        if (r9[4] < r12) goto L_0x00a2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00a1, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00b8, code lost:
        if ((java.lang.Math.abs(((((r9[0] + r9[1]) + r9[2]) + r9[3]) + r9[4]) - r13) * 5) < (r13 * 2)) goto L_0x00bb;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00ba, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00bf, code lost:
        if (foundPatternCross(r9) == false) goto L_?;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00c5, code lost:
        return centerFromEnd(r9, r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:?, code lost:
        return Float.NaN;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float crossCheckVertical(int r10, int r11, int r12, int r13) {
        /*
            Method dump skipped, instructions count: 198
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.FinderPatternFinder.crossCheckVertical(int, int, int, int):float");
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x003a, code lost:
        if (r9[1] <= r12) goto L_0x003e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x003f, code lost:
        if (r2 < 0) goto L_0x0053;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0045, code lost:
        if (r0.get(r2, r11) == false) goto L_0x0053;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0049, code lost:
        if (r9[0] > r12) goto L_0x0053;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x004b, code lost:
        r9[0] = r9[0] + 1;
        r2 = r2 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0055, code lost:
        if (r9[0] <= r12) goto L_0x0058;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0057, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0058, code lost:
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0059, code lost:
        if (r10 >= r1) goto L_0x0069;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x005f, code lost:
        if (r0.get(r10, r11) == false) goto L_0x0069;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0061, code lost:
        r9[2] = r9[2] + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0069, code lost:
        if (r10 != r1) goto L_0x006c;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x006b, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x006d, code lost:
        if (r10 >= r1) goto L_0x0081;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0073, code lost:
        if (r0.get(r10, r11) != false) goto L_0x0081;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0077, code lost:
        if (r9[3] >= r12) goto L_0x0081;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0079, code lost:
        r9[3] = r9[3] + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0081, code lost:
        if (r10 == r1) goto L_?;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0085, code lost:
        if (r9[3] < r12) goto L_0x0088;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0089, code lost:
        if (r10 >= r1) goto L_0x009d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x008f, code lost:
        if (r0.get(r10, r11) == false) goto L_0x009d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0093, code lost:
        if (r9[4] >= r12) goto L_0x009d;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0095, code lost:
        r9[4] = r9[4] + 1;
        r10 = r10 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x009f, code lost:
        if (r9[4] < r12) goto L_0x00a2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00a1, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00b7, code lost:
        if ((java.lang.Math.abs(((((r9[0] + r9[1]) + r9[2]) + r9[3]) + r9[4]) - r13) * 5) < r13) goto L_0x00ba;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00b9, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00be, code lost:
        if (foundPatternCross(r9) == false) goto L_?;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00c4, code lost:
        return centerFromEnd(r9, r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:?, code lost:
        return Float.NaN;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:?, code lost:
        return Float.NaN;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float crossCheckHorizontal(int r10, int r11, int r12, int r13) {
        /*
            Method dump skipped, instructions count: 197
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.detector.FinderPatternFinder.crossCheckHorizontal(int, int, int, int):float");
    }

    protected final boolean handlePossibleCenter(int[] iArr, int i, int i2) {
        boolean z = false;
        int i3 = iArr[0] + iArr[1] + iArr[2] + iArr[3] + iArr[4];
        int centerFromEnd = (int) centerFromEnd(iArr, i2);
        float crossCheckVertical = crossCheckVertical(i, centerFromEnd, iArr[2], i3);
        if (!Float.isNaN(crossCheckVertical)) {
            float crossCheckHorizontal = crossCheckHorizontal(centerFromEnd, (int) crossCheckVertical, iArr[2], i3);
            if (!Float.isNaN(crossCheckHorizontal)) {
                float f = i3 / 7.0f;
                int i4 = 0;
                while (true) {
                    if (i4 >= this.possibleCenters.size()) {
                        break;
                    }
                    FinderPattern finderPattern = this.possibleCenters.get(i4);
                    if (finderPattern.aboutEquals(f, crossCheckVertical, crossCheckHorizontal)) {
                        this.possibleCenters.set(i4, finderPattern.combineEstimate(crossCheckVertical, crossCheckHorizontal, f));
                        z = true;
                        break;
                    }
                    i4++;
                }
                if (!z) {
                    FinderPattern finderPattern2 = new FinderPattern(crossCheckHorizontal, crossCheckVertical, f);
                    this.possibleCenters.add(finderPattern2);
                    ResultPointCallback resultPointCallback = this.resultPointCallback;
                    if (resultPointCallback != null) {
                        resultPointCallback.foundPossibleResultPoint(finderPattern2);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private int findRowSkip() {
        if (this.possibleCenters.size() <= 1) {
            return 0;
        }
        FinderPattern finderPattern = null;
        for (FinderPattern finderPattern2 : this.possibleCenters) {
            if (finderPattern2.getCount() >= 2) {
                if (finderPattern == null) {
                    finderPattern = finderPattern2;
                } else {
                    this.hasSkipped = true;
                    return ((int) (Math.abs(finderPattern.getX() - finderPattern2.getX()) - Math.abs(finderPattern.getY() - finderPattern2.getY()))) / 2;
                }
            }
        }
        return 0;
    }

    private boolean haveMultiplyConfirmedCenters() {
        int size = this.possibleCenters.size();
        float f = 0.0f;
        float f2 = 0.0f;
        int i = 0;
        for (FinderPattern finderPattern : this.possibleCenters) {
            if (finderPattern.getCount() >= 2) {
                i++;
                f2 += finderPattern.getEstimatedModuleSize();
            }
        }
        if (i < 3) {
            return false;
        }
        float f3 = f2 / size;
        for (FinderPattern finderPattern2 : this.possibleCenters) {
            f += Math.abs(finderPattern2.getEstimatedModuleSize() - f3);
        }
        return f <= f2 * 0.05f;
    }

    private FinderPattern[] selectBestPatterns() throws NotFoundException {
        float f;
        int size = this.possibleCenters.size();
        if (size >= 3) {
            float f2 = 0.0f;
            if (size > 3) {
                float f3 = 0.0f;
                float f4 = 0.0f;
                for (FinderPattern finderPattern : this.possibleCenters) {
                    float estimatedModuleSize = finderPattern.getEstimatedModuleSize();
                    f3 += estimatedModuleSize;
                    f4 += estimatedModuleSize * estimatedModuleSize;
                }
                float f5 = f3 / size;
                float sqrt = (float) Math.sqrt((f4 / f) - (f5 * f5));
                Collections.sort(this.possibleCenters, new FurthestFromAverageComparator(f5));
                float max = Math.max(0.2f * f5, sqrt);
                int i = 0;
                while (i < this.possibleCenters.size() && this.possibleCenters.size() > 3) {
                    if (Math.abs(this.possibleCenters.get(i).getEstimatedModuleSize() - f5) > max) {
                        this.possibleCenters.remove(i);
                        i--;
                    }
                    i++;
                }
            }
            if (this.possibleCenters.size() > 3) {
                for (FinderPattern finderPattern2 : this.possibleCenters) {
                    f2 += finderPattern2.getEstimatedModuleSize();
                }
                Collections.sort(this.possibleCenters, new CenterComparator(f2 / this.possibleCenters.size()));
                List<FinderPattern> list = this.possibleCenters;
                list.subList(3, list.size()).clear();
            }
            return new FinderPattern[]{this.possibleCenters.get(0), this.possibleCenters.get(1), this.possibleCenters.get(2)};
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class FurthestFromAverageComparator implements Comparator<FinderPattern>, Serializable {
        private final float average;

        private FurthestFromAverageComparator(float f) {
            this.average = f;
        }

        public int compare(FinderPattern finderPattern, FinderPattern finderPattern2) {
            float abs = Math.abs(finderPattern2.getEstimatedModuleSize() - this.average);
            float abs2 = Math.abs(finderPattern.getEstimatedModuleSize() - this.average);
            if (abs < abs2) {
                return -1;
            }
            return abs == abs2 ? 0 : 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class CenterComparator implements Comparator<FinderPattern>, Serializable {
        private final float average;

        private CenterComparator(float f) {
            this.average = f;
        }

        public int compare(FinderPattern finderPattern, FinderPattern finderPattern2) {
            if (finderPattern2.getCount() != finderPattern.getCount()) {
                return finderPattern2.getCount() - finderPattern.getCount();
            }
            float abs = Math.abs(finderPattern2.getEstimatedModuleSize() - this.average);
            float abs2 = Math.abs(finderPattern.getEstimatedModuleSize() - this.average);
            if (abs < abs2) {
                return 1;
            }
            return abs == abs2 ? 0 : -1;
        }
    }
}
