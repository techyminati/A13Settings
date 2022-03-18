package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.pdf417.PDF417ResultMetadata;
import java.math.BigInteger;
import java.util.Arrays;
/* loaded from: classes2.dex */
final class DecodedBitStreamParser {
    private static final BigInteger[] EXP900;
    private static final char[] PUNCT_CHARS = {';', '<', '>', '@', '[', '\\', '}', '_', '`', '~', '!', '\r', '\t', ',', ':', '\n', '-', '.', '$', '/', '\"', '|', '*', '(', ')', '?', '{', '}', '\''};
    private static final char[] MIXED_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '&', '\r', '\t', ',', ':', '#', '-', '.', '$', '/', '+', '%', '*', '=', '^'};

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum Mode {
        ALPHA,
        LOWER,
        MIXED,
        PUNCT,
        ALPHA_SHIFT,
        PUNCT_SHIFT
    }

    static {
        BigInteger[] bigIntegerArr = new BigInteger[16];
        EXP900 = bigIntegerArr;
        bigIntegerArr[0] = BigInteger.ONE;
        BigInteger valueOf = BigInteger.valueOf(900L);
        bigIntegerArr[1] = valueOf;
        int i = 2;
        while (true) {
            BigInteger[] bigIntegerArr2 = EXP900;
            if (i < bigIntegerArr2.length) {
                bigIntegerArr2[i] = bigIntegerArr2[i - 1].multiply(valueOf);
                i++;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DecoderResult decode(int[] iArr, String str) throws FormatException {
        int i;
        int i2 = 2;
        StringBuilder sb = new StringBuilder(iArr.length * 2);
        int i3 = iArr[1];
        PDF417ResultMetadata pDF417ResultMetadata = new PDF417ResultMetadata();
        while (i2 < iArr[0]) {
            if (i3 == 913) {
                i = byteCompaction(i3, iArr, i2, sb);
            } else if (i3 != 928) {
                switch (i3) {
                    case 900:
                        i = textCompaction(iArr, i2, sb);
                        break;
                    case 901:
                        i = byteCompaction(i3, iArr, i2, sb);
                        break;
                    case 902:
                        i = numericCompaction(iArr, i2, sb);
                        break;
                    default:
                        switch (i3) {
                            case 922:
                            case 923:
                                throw FormatException.getFormatInstance();
                            case 924:
                                i = byteCompaction(i3, iArr, i2, sb);
                                break;
                            default:
                                i = textCompaction(iArr, i2 - 1, sb);
                                break;
                        }
                }
            } else {
                i = decodeMacroBlock(iArr, i2, pDF417ResultMetadata);
            }
            if (i < iArr.length) {
                i2 = i + 1;
                i3 = iArr[i];
            } else {
                throw FormatException.getFormatInstance();
            }
        }
        if (sb.length() != 0) {
            DecoderResult decoderResult = new DecoderResult(null, sb.toString(), null, str);
            decoderResult.setOther(pDF417ResultMetadata);
            return decoderResult;
        }
        throw FormatException.getFormatInstance();
    }

    private static int decodeMacroBlock(int[] iArr, int i, PDF417ResultMetadata pDF417ResultMetadata) throws FormatException {
        if (i + 2 <= iArr[0]) {
            int[] iArr2 = new int[2];
            int i2 = 0;
            while (i2 < 2) {
                iArr2[i2] = iArr[i];
                i2++;
                i++;
            }
            pDF417ResultMetadata.setSegmentIndex(Integer.parseInt(decodeBase900toBase10(iArr2, 2)));
            StringBuilder sb = new StringBuilder();
            int textCompaction = textCompaction(iArr, i, sb);
            pDF417ResultMetadata.setFileId(sb.toString());
            if (iArr[textCompaction] == 923) {
                int i3 = textCompaction + 1;
                int[] iArr3 = new int[iArr[0] - i3];
                boolean z = false;
                int i4 = 0;
                while (i3 < iArr[0] && !z) {
                    int i5 = i3 + 1;
                    int i6 = iArr[i3];
                    if (i6 < 900) {
                        i4++;
                        iArr3[i4] = i6;
                        i3 = i5;
                    } else if (i6 == 922) {
                        pDF417ResultMetadata.setLastSegment(true);
                        i3 = i5 + 1;
                        z = true;
                    } else {
                        throw FormatException.getFormatInstance();
                    }
                }
                pDF417ResultMetadata.setOptionalData(Arrays.copyOf(iArr3, i4));
                return i3;
            } else if (iArr[textCompaction] != 922) {
                return textCompaction;
            } else {
                pDF417ResultMetadata.setLastSegment(true);
                return textCompaction + 1;
            }
        } else {
            throw FormatException.getFormatInstance();
        }
    }

    private static int textCompaction(int[] iArr, int i, StringBuilder sb) {
        int[] iArr2 = new int[(iArr[0] - i) << 1];
        int[] iArr3 = new int[(iArr[0] - i) << 1];
        boolean z = false;
        int i2 = 0;
        while (i < iArr[0] && !z) {
            i++;
            int i3 = iArr[i];
            if (i3 < 900) {
                iArr2[i2] = i3 / 30;
                iArr2[i2 + 1] = i3 % 30;
                i2 += 2;
            } else if (i3 != 913) {
                if (i3 != 928) {
                    switch (i3) {
                        case 900:
                            i2++;
                            iArr2[i2] = 900;
                            break;
                        default:
                            switch (i3) {
                            }
                        case 901:
                        case 902:
                            i--;
                            z = true;
                            break;
                    }
                }
                i--;
                z = true;
            } else {
                iArr2[i2] = 913;
                i++;
                iArr3[i2] = iArr[i];
                i2++;
            }
        }
        decodeTextCompaction(iArr2, iArr3, i2, sb);
        return i;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static void decodeTextCompaction(int[] iArr, int[] iArr2, int i, StringBuilder sb) {
        int i2;
        Mode mode = Mode.ALPHA;
        Mode mode2 = mode;
        for (int i3 = 0; i3 < i; i3++) {
            int i4 = iArr[i3];
            char c = ' ';
            switch (AnonymousClass1.$SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[mode.ordinal()]) {
                case 1:
                    if (i4 >= 26) {
                        if (i4 != 26) {
                            if (i4 == 27) {
                                mode = Mode.LOWER;
                            } else if (i4 == 28) {
                                mode = Mode.MIXED;
                            } else if (i4 == 29) {
                                mode = Mode.PUNCT_SHIFT;
                                c = 0;
                                mode2 = mode;
                                break;
                            } else if (i4 == 913) {
                                sb.append((char) iArr2[i3]);
                            } else if (i4 == 900) {
                                mode = Mode.ALPHA;
                            }
                            c = 0;
                            break;
                        }
                    } else {
                        i2 = i4 + 65;
                        c = (char) i2;
                        break;
                    }
                    break;
                case 2:
                    if (i4 >= 26) {
                        if (i4 != 26) {
                            if (i4 != 27) {
                                if (i4 == 28) {
                                    mode = Mode.MIXED;
                                } else if (i4 == 29) {
                                    mode = Mode.PUNCT_SHIFT;
                                } else if (i4 == 913) {
                                    sb.append((char) iArr2[i3]);
                                } else if (i4 == 900) {
                                    mode = Mode.ALPHA;
                                }
                                c = 0;
                                break;
                            } else {
                                mode = Mode.ALPHA_SHIFT;
                            }
                            c = 0;
                            mode2 = mode;
                            break;
                        }
                    } else {
                        i2 = i4 + 97;
                        c = (char) i2;
                        break;
                    }
                    break;
                case 3:
                    if (i4 < 25) {
                        c = MIXED_CHARS[i4];
                        break;
                    } else {
                        if (i4 == 25) {
                            mode = Mode.PUNCT;
                        } else if (i4 != 26) {
                            if (i4 == 27) {
                                mode = Mode.LOWER;
                            } else if (i4 == 28) {
                                mode = Mode.ALPHA;
                            } else if (i4 == 29) {
                                mode = Mode.PUNCT_SHIFT;
                                c = 0;
                                mode2 = mode;
                                break;
                            } else if (i4 == 913) {
                                sb.append((char) iArr2[i3]);
                            } else if (i4 == 900) {
                                mode = Mode.ALPHA;
                            }
                        }
                        c = 0;
                        break;
                    }
                    break;
                case 4:
                    if (i4 < 29) {
                        c = PUNCT_CHARS[i4];
                        break;
                    } else {
                        if (i4 == 29) {
                            mode = Mode.ALPHA;
                        } else if (i4 == 913) {
                            sb.append((char) iArr2[i3]);
                        } else if (i4 == 900) {
                            mode = Mode.ALPHA;
                        }
                        c = 0;
                        break;
                    }
                case 5:
                    if (i4 < 26) {
                        c = (char) (i4 + 65);
                    } else if (i4 != 26) {
                        if (i4 == 900) {
                            mode = Mode.ALPHA;
                            c = 0;
                            break;
                        }
                        c = 0;
                    }
                    mode = mode2;
                    break;
                case 6:
                    if (i4 < 29) {
                        c = PUNCT_CHARS[i4];
                        mode = mode2;
                        break;
                    } else {
                        if (i4 == 29) {
                            mode = Mode.ALPHA;
                        } else {
                            if (i4 == 913) {
                                sb.append((char) iArr2[i3]);
                            } else if (i4 == 900) {
                                mode = Mode.ALPHA;
                            }
                            c = 0;
                            mode = mode2;
                        }
                        c = 0;
                        break;
                    }
                default:
                    c = 0;
                    break;
            }
            if (c != 0) {
                sb.append(c);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.zxing.pdf417.decoder.DecodedBitStreamParser$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode;

        static {
            int[] iArr = new int[Mode.values().length];
            $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode = iArr;
            try {
                iArr[Mode.ALPHA.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.LOWER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.MIXED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.PUNCT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.ALPHA_SHIFT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.PUNCT_SHIFT.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    private static int byteCompaction(int i, int[] iArr, int i2, StringBuilder sb) {
        int i3;
        int i4 = 922;
        int i5 = 923;
        long j = 900;
        int i6 = 6;
        if (i == 901) {
            char[] cArr = new char[6];
            int[] iArr2 = new int[6];
            int i7 = i2 + 1;
            boolean z = false;
            int i8 = 0;
            int i9 = iArr[i2];
            long j2 = 0;
            while (i7 < iArr[0] && !z) {
                int i10 = i8 + 1;
                iArr2[i8] = i9;
                j2 = (j2 * j) + i9;
                int i11 = i7 + 1;
                i9 = iArr[i7];
                if (i9 == 900 || i9 == 901 || i9 == 902 || i9 == 924 || i9 == 928 || i9 == 923 || i9 == 922) {
                    i7 = i11 - 1;
                    i9 = i9;
                    i8 = i10;
                    j = 900;
                    i6 = 6;
                    z = true;
                } else {
                    if (i10 % 5 != 0 || i10 <= 0) {
                        i9 = i9;
                        i8 = i10;
                        i7 = i11;
                    } else {
                        int i12 = 0;
                        while (i12 < i6) {
                            cArr[5 - i12] = (char) (j2 % 256);
                            j2 >>= 8;
                            i12++;
                            i9 = i9;
                            i6 = 6;
                        }
                        sb.append(cArr);
                        i7 = i11;
                        i8 = 0;
                    }
                    j = 900;
                    i6 = 6;
                }
            }
            if (i7 != iArr[0] || i9 >= 900) {
                i3 = i8;
            } else {
                i3 = i8 + 1;
                iArr2[i8] = i9;
            }
            for (int i13 = 0; i13 < i3; i13++) {
                sb.append((char) iArr2[i13]);
            }
            return i7;
        } else if (i != 924) {
            return i2;
        } else {
            int i14 = i2;
            boolean z2 = false;
            int i15 = 0;
            long j3 = 0;
            while (i14 < iArr[0] && !z2) {
                int i16 = i14 + 1;
                int i17 = iArr[i14];
                if (i17 < 900) {
                    i15++;
                    j3 = (j3 * 900) + i17;
                } else if (i17 == 900 || i17 == 901 || i17 == 902 || i17 == 924 || i17 == 928 || i17 == i5 || i17 == i4) {
                    i14 = i16 - 1;
                    z2 = true;
                    if (i15 % 5 != 0 && i15 > 0) {
                        char[] cArr2 = new char[6];
                        for (int i18 = 0; i18 < 6; i18++) {
                            cArr2[5 - i18] = (char) (j3 & 255);
                            j3 >>= 8;
                        }
                        sb.append(cArr2);
                        i15 = 0;
                    }
                    i4 = 922;
                    i5 = 923;
                }
                i14 = i16;
                if (i15 % 5 != 0) {
                }
                i4 = 922;
                i5 = 923;
            }
            return i14;
        }
    }

    private static int numericCompaction(int[] iArr, int i, StringBuilder sb) throws FormatException {
        int[] iArr2 = new int[15];
        boolean z = false;
        int i2 = 0;
        while (i < iArr[0] && !z) {
            i++;
            int i3 = iArr[i];
            if (i == iArr[0]) {
                z = true;
            }
            if (i3 < 900) {
                iArr2[i2] = i3;
                i2++;
            } else if (i3 == 900 || i3 == 901 || i3 == 924 || i3 == 928 || i3 == 923 || i3 == 922) {
                i--;
                z = true;
            }
            if (i2 % 15 == 0 || i3 == 902 || z) {
                sb.append(decodeBase900toBase10(iArr2, i2));
                i2 = 0;
            }
        }
        return i;
    }

    private static String decodeBase900toBase10(int[] iArr, int i) throws FormatException {
        BigInteger bigInteger = BigInteger.ZERO;
        for (int i2 = 0; i2 < i; i2++) {
            bigInteger = bigInteger.add(EXP900[(i - i2) - 1].multiply(BigInteger.valueOf(iArr[i2])));
        }
        String bigInteger2 = bigInteger.toString();
        if (bigInteger2.charAt(0) == '1') {
            return bigInteger2.substring(1);
        }
        throw FormatException.getFormatInstance();
    }
}
