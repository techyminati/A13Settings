package com.google.zxing.oned;

import androidx.window.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Map;
/* loaded from: classes2.dex */
public final class Code39Reader extends OneDReader {
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".toCharArray();
    private static final int ASTERISK_ENCODING;
    static final int[] CHARACTER_ENCODINGS;
    private final int[] counters;
    private final StringBuilder decodeRowResult;
    private final boolean extendedMode;
    private final boolean usingCheckDigit;

    static {
        int[] iArr = {52, 289, 97, 352, 49, 304, R.styleable.AppCompatTheme_toolbarNavigationButtonStyle, 37, 292, 100, 265, 73, 328, 25, 280, 88, 13, 268, 76, 28, 259, 67, 322, 19, 274, 82, 7, 262, 70, 22, 385, 193, 448, 145, 400, 208, 133, 388, 196, 148, 168, 162, 138, 42};
        CHARACTER_ENCODINGS = iArr;
        ASTERISK_ENCODING = iArr[39];
    }

    public Code39Reader() {
        this(false);
    }

    public Code39Reader(boolean z) {
        this(z, false);
    }

    public Code39Reader(boolean z, boolean z2) {
        this.usingCheckDigit = z;
        this.extendedMode = z2;
        this.decodeRowResult = new StringBuilder(20);
        this.counters = new int[9];
    }

    @Override // com.google.zxing.oned.OneDReader
    public Result decodeRow(int i, BitArray bitArray, Map<DecodeHintType, ?> map) throws NotFoundException, ChecksumException, FormatException {
        int[] findAsteriskPattern;
        String str;
        int[] iArr = this.counters;
        Arrays.fill(iArr, 0);
        StringBuilder sb = this.decodeRowResult;
        sb.setLength(0);
        int nextSet = bitArray.getNextSet(findAsteriskPattern(bitArray, iArr)[1]);
        int size = bitArray.getSize();
        while (true) {
            OneDReader.recordPattern(bitArray, nextSet, iArr);
            int narrowWidePattern = toNarrowWidePattern(iArr);
            if (narrowWidePattern >= 0) {
                char patternToChar = patternToChar(narrowWidePattern);
                sb.append(patternToChar);
                int i2 = nextSet;
                for (int i3 : iArr) {
                    i2 += i3;
                }
                int nextSet2 = bitArray.getNextSet(i2);
                if (patternToChar == '*') {
                    sb.setLength(sb.length() - 1);
                    int i4 = 0;
                    for (int i5 : iArr) {
                        i4 += i5;
                    }
                    int i6 = (nextSet2 - nextSet) - i4;
                    if (nextSet2 == size || (i6 >> 1) >= i4) {
                        if (this.usingCheckDigit) {
                            int length = sb.length() - 1;
                            int i7 = 0;
                            for (int i8 = 0; i8 < length; i8++) {
                                i7 += "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(this.decodeRowResult.charAt(i8));
                            }
                            if (sb.charAt(length) == ALPHABET[i7 % 43]) {
                                sb.setLength(length);
                            } else {
                                throw ChecksumException.getChecksumInstance();
                            }
                        }
                        if (sb.length() != 0) {
                            if (this.extendedMode) {
                                str = decodeExtended(sb);
                            } else {
                                str = sb.toString();
                            }
                            float f = i;
                            return new Result(str, null, new ResultPoint[]{new ResultPoint((findAsteriskPattern[1] + findAsteriskPattern[0]) / 2.0f, f), new ResultPoint((nextSet2 + nextSet) / 2.0f, f)}, BarcodeFormat.CODE_39);
                        }
                        throw NotFoundException.getNotFoundInstance();
                    }
                    throw NotFoundException.getNotFoundInstance();
                }
                nextSet = nextSet2;
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        }
    }

    private static int[] findAsteriskPattern(BitArray bitArray, int[] iArr) throws NotFoundException {
        int size = bitArray.getSize();
        int nextSet = bitArray.getNextSet(0);
        int length = iArr.length;
        boolean z = false;
        int i = 0;
        int i2 = nextSet;
        while (nextSet < size) {
            if (bitArray.get(nextSet) ^ z) {
                iArr[i] = iArr[i] + 1;
            } else {
                int i3 = length - 1;
                if (i != i3) {
                    i++;
                } else if (toNarrowWidePattern(iArr) == ASTERISK_ENCODING && bitArray.isRange(Math.max(0, i2 - ((nextSet - i2) >> 1)), i2, false)) {
                    return new int[]{i2, nextSet};
                } else {
                    i2 += iArr[0] + iArr[1];
                    int i4 = length - 2;
                    System.arraycopy(iArr, 2, iArr, 0, i4);
                    iArr[i4] = 0;
                    iArr[i3] = 0;
                    i--;
                }
                iArr[i] = 1;
                z = !z;
            }
            nextSet++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x002f, code lost:
        if (r1 >= r0) goto L_0x0041;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0031, code lost:
        if (r4 <= 0) goto L_0x0041;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0033, code lost:
        r2 = r10[r1];
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0035, code lost:
        if (r2 <= r3) goto L_0x003e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0037, code lost:
        r4 = r4 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x003b, code lost:
        if ((r2 << 1) < r6) goto L_0x003e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x003d, code lost:
        return -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x003e, code lost:
        r1 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0041, code lost:
        return r5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int toNarrowWidePattern(int[] r10) {
        /*
            int r0 = r10.length
            r1 = 0
            r2 = r1
        L_0x0003:
            r3 = 2147483647(0x7fffffff, float:NaN)
            int r4 = r10.length
            r5 = r1
        L_0x0008:
            if (r5 >= r4) goto L_0x0014
            r6 = r10[r5]
            if (r6 >= r3) goto L_0x0011
            if (r6 <= r2) goto L_0x0011
            r3 = r6
        L_0x0011:
            int r5 = r5 + 1
            goto L_0x0008
        L_0x0014:
            r2 = r1
            r4 = r2
            r5 = r4
            r6 = r5
        L_0x0018:
            if (r2 >= r0) goto L_0x002b
            r7 = r10[r2]
            if (r7 <= r3) goto L_0x0028
            int r8 = r0 + (-1)
            int r8 = r8 - r2
            r9 = 1
            int r8 = r9 << r8
            r5 = r5 | r8
            int r4 = r4 + 1
            int r6 = r6 + r7
        L_0x0028:
            int r2 = r2 + 1
            goto L_0x0018
        L_0x002b:
            r2 = 3
            r7 = -1
            if (r4 != r2) goto L_0x0042
        L_0x002f:
            if (r1 >= r0) goto L_0x0041
            if (r4 <= 0) goto L_0x0041
            r2 = r10[r1]
            if (r2 <= r3) goto L_0x003e
            int r4 = r4 + (-1)
            int r2 = r2 << 1
            if (r2 < r6) goto L_0x003e
            return r7
        L_0x003e:
            int r1 = r1 + 1
            goto L_0x002f
        L_0x0041:
            return r5
        L_0x0042:
            if (r4 > r2) goto L_0x0045
            return r7
        L_0x0045:
            r2 = r3
            goto L_0x0003
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.Code39Reader.toNarrowWidePattern(int[]):int");
    }

    private static char patternToChar(int i) throws NotFoundException {
        int i2 = 0;
        while (true) {
            int[] iArr = CHARACTER_ENCODINGS;
            if (i2 >= iArr.length) {
                throw NotFoundException.getNotFoundInstance();
            } else if (iArr[i2] == i) {
                return ALPHABET[i2];
            } else {
                i2++;
            }
        }
    }

    private static String decodeExtended(CharSequence charSequence) throws FormatException {
        char c;
        int i;
        int length = charSequence.length();
        StringBuilder sb = new StringBuilder(length);
        int i2 = 0;
        while (i2 < length) {
            char charAt = charSequence.charAt(i2);
            if (charAt == '+' || charAt == '$' || charAt == '%' || charAt == '/') {
                i2++;
                char charAt2 = charSequence.charAt(i2);
                if (charAt != '$') {
                    if (charAt != '%') {
                        if (charAt != '+') {
                            if (charAt != '/') {
                                c = 0;
                            } else if (charAt2 >= 'A' && charAt2 <= 'O') {
                                i = charAt2 - ' ';
                            } else if (charAt2 == 'Z') {
                                c = ':';
                            } else {
                                throw FormatException.getFormatInstance();
                            }
                            sb.append(c);
                        } else if (charAt2 < 'A' || charAt2 > 'Z') {
                            throw FormatException.getFormatInstance();
                        } else {
                            i = charAt2 + ' ';
                        }
                    } else if (charAt2 >= 'A' && charAt2 <= 'E') {
                        i = charAt2 - '&';
                    } else if (charAt2 < 'F' || charAt2 > 'W') {
                        throw FormatException.getFormatInstance();
                    } else {
                        i = charAt2 - 11;
                    }
                } else if (charAt2 < 'A' || charAt2 > 'Z') {
                    throw FormatException.getFormatInstance();
                } else {
                    i = charAt2 - '@';
                }
                c = (char) i;
                sb.append(c);
            } else {
                sb.append(charAt);
            }
            i2++;
        }
        return sb.toString();
    }
}
