package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.Map;
/* loaded from: classes2.dex */
public final class Code128Reader extends OneDReader {
    static final int[][] CODE_PATTERNS = {new int[]{2, 1, 2, 2, 2, 2}, new int[]{2, 2, 2, 1, 2, 2}, new int[]{2, 2, 2, 2, 2, 1}, new int[]{1, 2, 1, 2, 2, 3}, new int[]{1, 2, 1, 3, 2, 2}, new int[]{1, 3, 1, 2, 2, 2}, new int[]{1, 2, 2, 2, 1, 3}, new int[]{1, 2, 2, 3, 1, 2}, new int[]{1, 3, 2, 2, 1, 2}, new int[]{2, 2, 1, 2, 1, 3}, new int[]{2, 2, 1, 3, 1, 2}, new int[]{2, 3, 1, 2, 1, 2}, new int[]{1, 1, 2, 2, 3, 2}, new int[]{1, 2, 2, 1, 3, 2}, new int[]{1, 2, 2, 2, 3, 1}, new int[]{1, 1, 3, 2, 2, 2}, new int[]{1, 2, 3, 1, 2, 2}, new int[]{1, 2, 3, 2, 2, 1}, new int[]{2, 2, 3, 2, 1, 1}, new int[]{2, 2, 1, 1, 3, 2}, new int[]{2, 2, 1, 2, 3, 1}, new int[]{2, 1, 3, 2, 1, 2}, new int[]{2, 2, 3, 1, 1, 2}, new int[]{3, 1, 2, 1, 3, 1}, new int[]{3, 1, 1, 2, 2, 2}, new int[]{3, 2, 1, 1, 2, 2}, new int[]{3, 2, 1, 2, 2, 1}, new int[]{3, 1, 2, 2, 1, 2}, new int[]{3, 2, 2, 1, 1, 2}, new int[]{3, 2, 2, 2, 1, 1}, new int[]{2, 1, 2, 1, 2, 3}, new int[]{2, 1, 2, 3, 2, 1}, new int[]{2, 3, 2, 1, 2, 1}, new int[]{1, 1, 1, 3, 2, 3}, new int[]{1, 3, 1, 1, 2, 3}, new int[]{1, 3, 1, 3, 2, 1}, new int[]{1, 1, 2, 3, 1, 3}, new int[]{1, 3, 2, 1, 1, 3}, new int[]{1, 3, 2, 3, 1, 1}, new int[]{2, 1, 1, 3, 1, 3}, new int[]{2, 3, 1, 1, 1, 3}, new int[]{2, 3, 1, 3, 1, 1}, new int[]{1, 1, 2, 1, 3, 3}, new int[]{1, 1, 2, 3, 3, 1}, new int[]{1, 3, 2, 1, 3, 1}, new int[]{1, 1, 3, 1, 2, 3}, new int[]{1, 1, 3, 3, 2, 1}, new int[]{1, 3, 3, 1, 2, 1}, new int[]{3, 1, 3, 1, 2, 1}, new int[]{2, 1, 1, 3, 3, 1}, new int[]{2, 3, 1, 1, 3, 1}, new int[]{2, 1, 3, 1, 1, 3}, new int[]{2, 1, 3, 3, 1, 1}, new int[]{2, 1, 3, 1, 3, 1}, new int[]{3, 1, 1, 1, 2, 3}, new int[]{3, 1, 1, 3, 2, 1}, new int[]{3, 3, 1, 1, 2, 1}, new int[]{3, 1, 2, 1, 1, 3}, new int[]{3, 1, 2, 3, 1, 1}, new int[]{3, 3, 2, 1, 1, 1}, new int[]{3, 1, 4, 1, 1, 1}, new int[]{2, 2, 1, 4, 1, 1}, new int[]{4, 3, 1, 1, 1, 1}, new int[]{1, 1, 1, 2, 2, 4}, new int[]{1, 1, 1, 4, 2, 2}, new int[]{1, 2, 1, 1, 2, 4}, new int[]{1, 2, 1, 4, 2, 1}, new int[]{1, 4, 1, 1, 2, 2}, new int[]{1, 4, 1, 2, 2, 1}, new int[]{1, 1, 2, 2, 1, 4}, new int[]{1, 1, 2, 4, 1, 2}, new int[]{1, 2, 2, 1, 1, 4}, new int[]{1, 2, 2, 4, 1, 1}, new int[]{1, 4, 2, 1, 1, 2}, new int[]{1, 4, 2, 2, 1, 1}, new int[]{2, 4, 1, 2, 1, 1}, new int[]{2, 2, 1, 1, 1, 4}, new int[]{4, 1, 3, 1, 1, 1}, new int[]{2, 4, 1, 1, 1, 2}, new int[]{1, 3, 4, 1, 1, 1}, new int[]{1, 1, 1, 2, 4, 2}, new int[]{1, 2, 1, 1, 4, 2}, new int[]{1, 2, 1, 2, 4, 1}, new int[]{1, 1, 4, 2, 1, 2}, new int[]{1, 2, 4, 1, 1, 2}, new int[]{1, 2, 4, 2, 1, 1}, new int[]{4, 1, 1, 2, 1, 2}, new int[]{4, 2, 1, 1, 1, 2}, new int[]{4, 2, 1, 2, 1, 1}, new int[]{2, 1, 2, 1, 4, 1}, new int[]{2, 1, 4, 1, 2, 1}, new int[]{4, 1, 2, 1, 2, 1}, new int[]{1, 1, 1, 1, 4, 3}, new int[]{1, 1, 1, 3, 4, 1}, new int[]{1, 3, 1, 1, 4, 1}, new int[]{1, 1, 4, 1, 1, 3}, new int[]{1, 1, 4, 3, 1, 1}, new int[]{4, 1, 1, 1, 1, 3}, new int[]{4, 1, 1, 3, 1, 1}, new int[]{1, 1, 3, 1, 4, 1}, new int[]{1, 1, 4, 1, 3, 1}, new int[]{3, 1, 1, 1, 4, 1}, new int[]{4, 1, 1, 1, 3, 1}, new int[]{2, 1, 1, 4, 1, 2}, new int[]{2, 1, 1, 2, 1, 4}, new int[]{2, 1, 1, 2, 3, 2}, new int[]{2, 3, 3, 1, 1, 1, 2}};

    private static int[] findStartPattern(BitArray bitArray) throws NotFoundException {
        int size = bitArray.getSize();
        int nextSet = bitArray.getNextSet(0);
        int[] iArr = new int[6];
        boolean z = false;
        int i = 0;
        int i2 = nextSet;
        while (nextSet < size) {
            if (bitArray.get(nextSet) ^ z) {
                iArr[i] = iArr[i] + 1;
            } else {
                if (i == 5) {
                    int i3 = 64;
                    int i4 = -1;
                    for (int i5 = 103; i5 <= 105; i5++) {
                        int patternMatchVariance = OneDReader.patternMatchVariance(iArr, CODE_PATTERNS[i5], 179);
                        if (patternMatchVariance < i3) {
                            i4 = i5;
                            i3 = patternMatchVariance;
                        }
                    }
                    if (i4 >= 0 && bitArray.isRange(Math.max(0, i2 - ((nextSet - i2) / 2)), i2, false)) {
                        return new int[]{i2, nextSet, i4};
                    }
                    i2 += iArr[0] + iArr[1];
                    System.arraycopy(iArr, 2, iArr, 0, 4);
                    iArr[4] = 0;
                    iArr[5] = 0;
                    i--;
                } else {
                    i++;
                }
                iArr[i] = 1;
                z = !z;
            }
            nextSet++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeCode(BitArray bitArray, int[] iArr, int i) throws NotFoundException {
        OneDReader.recordPattern(bitArray, i, iArr);
        int i2 = 64;
        int i3 = -1;
        int i4 = 0;
        while (true) {
            int[][] iArr2 = CODE_PATTERNS;
            if (i4 >= iArr2.length) {
                break;
            }
            int patternMatchVariance = OneDReader.patternMatchVariance(iArr, iArr2[i4], 179);
            if (patternMatchVariance < i2) {
                i3 = i4;
                i2 = patternMatchVariance;
            }
            i4++;
        }
        if (i3 >= 0) {
            return i3;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.zxing.oned.OneDReader
    public Result decodeRow(int i, BitArray bitArray, Map<DecodeHintType, ?> map) throws NotFoundException, FormatException, ChecksumException {
        char c;
        boolean z = map != null && map.containsKey(DecodeHintType.ASSUME_GS1);
        int[] findStartPattern = findStartPattern(bitArray);
        int i2 = findStartPattern[2];
        switch (i2) {
            case 103:
                c = 'e';
                break;
            case 104:
                c = 'd';
                break;
            case 105:
                c = 'c';
                break;
            default:
                throw FormatException.getFormatInstance();
        }
        StringBuilder sb = new StringBuilder(20);
        ArrayList arrayList = new ArrayList(20);
        int i3 = 6;
        int[] iArr = new int[6];
        boolean z2 = false;
        int i4 = 0;
        int i5 = 0;
        i4 = 0;
        int i6 = findStartPattern[0];
        int i7 = i2;
        int i8 = findStartPattern[1];
        boolean z3 = false;
        boolean z4 = true;
        while (!z3) {
            int decodeCode = decodeCode(bitArray, iArr, i8);
            arrayList.add(Byte.valueOf((byte) decodeCode));
            if (decodeCode != 106) {
                z4 = true;
            }
            if (decodeCode != 106) {
                i5++;
                i7 += i5 * decodeCode;
            }
            int i9 = i8;
            for (int i10 = 0; i10 < i3; i10++) {
                i9 += iArr[i10];
            }
            switch (decodeCode) {
                case 103:
                case 104:
                case 105:
                    throw FormatException.getFormatInstance();
                default:
                    switch (c) {
                        case 'c':
                            c = 'd';
                            if (decodeCode < 100) {
                                if (decodeCode < 10) {
                                    sb.append('0');
                                }
                                sb.append(decodeCode);
                            } else {
                                if (decodeCode != 106) {
                                    z4 = false;
                                }
                                if (decodeCode != 106) {
                                    switch (decodeCode) {
                                        case 100:
                                            c = 'd';
                                            break;
                                        case 101:
                                            z2 = false;
                                            c = 'e';
                                            break;
                                        case 102:
                                            if (z) {
                                                if (sb.length() == 0) {
                                                    sb.append("]C1");
                                                    break;
                                                } else {
                                                    sb.append((char) 29);
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                } else {
                                    z3 = true;
                                }
                            }
                            z2 = false;
                            break;
                        case 'd':
                            if (decodeCode < 96) {
                                sb.append((char) (decodeCode + 32));
                                c = 'd';
                                z2 = false;
                                break;
                            } else {
                                if (decodeCode != 106) {
                                    z4 = false;
                                }
                                if (decodeCode != 98) {
                                    if (decodeCode != 99) {
                                        if (decodeCode != 101) {
                                            if (decodeCode != 102) {
                                                if (decodeCode == 106) {
                                                    z3 = true;
                                                }
                                            } else if (z) {
                                                if (sb.length() == 0) {
                                                    sb.append("]C1");
                                                } else {
                                                    sb.append((char) 29);
                                                }
                                            }
                                            z2 = false;
                                            c = 'd';
                                            break;
                                        } else {
                                            z2 = false;
                                        }
                                    }
                                    z2 = false;
                                    c = 'c';
                                    c = 'd';
                                } else {
                                    z2 = true;
                                }
                                c = 'e';
                                c = 'd';
                            }
                        case 'e':
                            if (decodeCode < 64) {
                                sb.append((char) (decodeCode + 32));
                            } else if (decodeCode < 96) {
                                sb.append((char) (decodeCode - 64));
                            } else {
                                if (decodeCode != 106) {
                                    z4 = false;
                                }
                                if (decodeCode != 102) {
                                    if (decodeCode != 106) {
                                        switch (decodeCode) {
                                            case 98:
                                                z2 = true;
                                                c = 'd';
                                                break;
                                            case 99:
                                                z2 = false;
                                                c = 'c';
                                                break;
                                            case 100:
                                                z2 = false;
                                                c = 'd';
                                                break;
                                        }
                                        c = 'd';
                                        break;
                                    } else {
                                        z3 = true;
                                    }
                                } else if (z) {
                                    if (sb.length() == 0) {
                                        sb.append("]C1");
                                    } else {
                                        sb.append((char) 29);
                                    }
                                }
                                z2 = false;
                                c = 'd';
                            }
                            c = 'd';
                            z2 = false;
                            break;
                        default:
                            c = 'd';
                            z2 = false;
                            break;
                    }
                    if (z2 && c != 'e') {
                        c = 'e';
                    }
                    i3 = 6;
                    i4 = decodeCode;
                    i6 = i8;
                    i8 = i9;
                    break;
            }
        }
        int nextUnset = bitArray.getNextUnset(i8);
        if (!bitArray.isRange(nextUnset, Math.min(bitArray.getSize(), ((nextUnset - i6) / 2) + nextUnset), false)) {
            throw NotFoundException.getNotFoundInstance();
        } else if ((i7 - (i5 * i4)) % 103 == i4) {
            int length = sb.length();
            if (length != 0) {
                if (length > 0 && z4) {
                    if (c == 'c') {
                        sb.delete(length - 2, length);
                    } else {
                        sb.delete(length - 1, length);
                    }
                }
                float f = (findStartPattern[1] + findStartPattern[0]) / 2.0f;
                float f2 = (nextUnset + i6) / 2.0f;
                int size = arrayList.size();
                byte[] bArr = new byte[size];
                for (int i11 = 0; i11 < size; i11++) {
                    bArr[i11] = ((Byte) arrayList.get(i11)).byteValue();
                }
                float f3 = i;
                return new Result(sb.toString(), bArr, new ResultPoint[]{new ResultPoint(f, f3), new ResultPoint(f2, f3)}, BarcodeFormat.CODE_128);
            }
            throw NotFoundException.getNotFoundInstance();
        } else {
            throw ChecksumException.getChecksumInstance();
        }
    }
}
