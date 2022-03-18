package com.google.protobuf;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class Utf8 {
    private static final Processor processor;

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i) {
        if (i > -12) {
            return -1;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i, int i2) {
        if (i > -12 || i2 > -65) {
            return -1;
        }
        return i ^ (i2 << 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i, int i2, int i3) {
        if (i > -12 || i2 > -65 || i3 > -65) {
            return -1;
        }
        return (i ^ (i2 << 8)) ^ (i3 << 16);
    }

    static {
        Processor processor2;
        if (!UnsafeProcessor.isAvailable() || Android.isOnAndroidDevice()) {
            processor2 = new SafeProcessor();
        } else {
            processor2 = new UnsafeProcessor();
        }
        processor = processor2;
    }

    public static boolean isValidUtf8(byte[] bArr) {
        return processor.isValidUtf8(bArr, 0, bArr.length);
    }

    public static boolean isValidUtf8(byte[] bArr, int i, int i2) {
        return processor.isValidUtf8(bArr, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(byte[] bArr, int i, int i2) {
        byte b = bArr[i - 1];
        int i3 = i2 - i;
        if (i3 == 0) {
            return incompleteStateFor(b);
        }
        if (i3 == 1) {
            return incompleteStateFor(b, bArr[i]);
        }
        if (i3 == 2) {
            return incompleteStateFor(b, bArr[i], bArr[i + 1]);
        }
        throw new AssertionError();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class UnpairedSurrogateException extends IllegalArgumentException {
        UnpairedSurrogateException(int i, int i2) {
            super("Unpaired surrogate at index " + i + " of " + i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int encodedLength(CharSequence charSequence) {
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) < 128) {
            i++;
        }
        int i2 = length;
        while (true) {
            if (i < length) {
                char charAt = charSequence.charAt(i);
                if (charAt >= 2048) {
                    i2 += encodedLengthGeneral(charSequence, i);
                    break;
                }
                i2 += (127 - charAt) >>> 31;
                i++;
            } else {
                break;
            }
        }
        if (i2 >= length) {
            return i2;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (i2 + 4294967296L));
    }

    private static int encodedLengthGeneral(CharSequence charSequence, int i) {
        int length = charSequence.length();
        int i2 = 0;
        while (i < length) {
            char charAt = charSequence.charAt(i);
            if (charAt < 2048) {
                i2 += (127 - charAt) >>> 31;
            } else {
                i2 += 2;
                if (55296 <= charAt && charAt <= 57343) {
                    if (Character.codePointAt(charSequence, i) >= 65536) {
                        i++;
                    } else {
                        throw new UnpairedSurrogateException(i, length);
                    }
                }
            }
            i++;
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int encode(CharSequence charSequence, byte[] bArr, int i, int i2) {
        return processor.encodeUtf8(charSequence, bArr, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String decodeUtf8(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException {
        return processor.decodeUtf8(bArr, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class Processor {
        abstract String decodeUtf8(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException;

        abstract int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2);

        abstract int partialIsValidUtf8(int i, byte[] bArr, int i2, int i3);

        Processor() {
        }

        final boolean isValidUtf8(byte[] bArr, int i, int i2) {
            return partialIsValidUtf8(0, bArr, i, i2) == 0;
        }
    }

    /* loaded from: classes2.dex */
    static final class SafeProcessor extends Processor {
        SafeProcessor() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0015, code lost:
            if (r7[r8] > (-65)) goto L_0x001b;
         */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x0046, code lost:
            if (r7[r8] > (-65)) goto L_0x0048;
         */
        /* JADX WARN: Code restructure failed: missing block: B:51:0x007f, code lost:
            if (r7[r8] > (-65)) goto L_0x0081;
         */
        @Override // com.google.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        int partialIsValidUtf8(int r6, byte[] r7, int r8, int r9) {
            /*
                r5 = this;
                if (r6 == 0) goto L_0x0082
                if (r8 < r9) goto L_0x0005
                return r6
            L_0x0005:
                byte r5 = (byte) r6
                r0 = -32
                r1 = -1
                r2 = -65
                if (r5 >= r0) goto L_0x001c
                r6 = -62
                if (r5 < r6) goto L_0x001b
                int r5 = r8 + 1
                byte r6 = r7[r8]
                if (r6 <= r2) goto L_0x0018
                goto L_0x001b
            L_0x0018:
                r8 = r5
                goto L_0x0082
            L_0x001b:
                return r1
            L_0x001c:
                r3 = -16
                if (r5 >= r3) goto L_0x0049
                int r6 = r6 >> 8
                int r6 = ~r6
                byte r6 = (byte) r6
                if (r6 != 0) goto L_0x0034
                int r6 = r8 + 1
                byte r8 = r7[r8]
                if (r6 < r9) goto L_0x0031
                int r5 = com.google.protobuf.Utf8.access$000(r5, r8)
                return r5
            L_0x0031:
                r4 = r8
                r8 = r6
                r6 = r4
            L_0x0034:
                if (r6 > r2) goto L_0x0048
                r3 = -96
                if (r5 != r0) goto L_0x003c
                if (r6 < r3) goto L_0x0048
            L_0x003c:
                r0 = -19
                if (r5 != r0) goto L_0x0042
                if (r6 >= r3) goto L_0x0048
            L_0x0042:
                int r5 = r8 + 1
                byte r6 = r7[r8]
                if (r6 <= r2) goto L_0x0018
            L_0x0048:
                return r1
            L_0x0049:
                int r0 = r6 >> 8
                int r0 = ~r0
                byte r0 = (byte) r0
                r3 = 0
                if (r0 != 0) goto L_0x005d
                int r6 = r8 + 1
                byte r0 = r7[r8]
                if (r6 < r9) goto L_0x005b
                int r5 = com.google.protobuf.Utf8.access$000(r5, r0)
                return r5
            L_0x005b:
                r8 = r6
                goto L_0x0060
            L_0x005d:
                int r6 = r6 >> 16
                byte r3 = (byte) r6
            L_0x0060:
                if (r3 != 0) goto L_0x006e
                int r6 = r8 + 1
                byte r3 = r7[r8]
                if (r6 < r9) goto L_0x006d
                int r5 = com.google.protobuf.Utf8.access$100(r5, r0, r3)
                return r5
            L_0x006d:
                r8 = r6
            L_0x006e:
                if (r0 > r2) goto L_0x0081
                int r5 = r5 << 28
                int r0 = r0 + 112
                int r5 = r5 + r0
                int r5 = r5 >> 30
                if (r5 != 0) goto L_0x0081
                if (r3 > r2) goto L_0x0081
                int r5 = r8 + 1
                byte r6 = r7[r8]
                if (r6 <= r2) goto L_0x0018
            L_0x0081:
                return r1
            L_0x0082:
                int r5 = partialIsValidUtf8(r7, r8, r9)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.SafeProcessor.partialIsValidUtf8(int, byte[], int, int):int");
        }

        @Override // com.google.protobuf.Utf8.Processor
        String decodeUtf8(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException {
            if ((i | i2 | ((bArr.length - i) - i2)) >= 0) {
                int i3 = i + i2;
                char[] cArr = new char[i2];
                int i4 = 0;
                while (i < i3) {
                    byte b = bArr[i];
                    if (!DecodeUtil.isOneByte(b)) {
                        break;
                    }
                    i++;
                    i4++;
                    DecodeUtil.handleOneByte(b, cArr, i4);
                }
                int i5 = i4;
                while (i < i3) {
                    int i6 = i + 1;
                    byte b2 = bArr[i];
                    if (DecodeUtil.isOneByte(b2)) {
                        int i7 = i5 + 1;
                        DecodeUtil.handleOneByte(b2, cArr, i5);
                        while (i6 < i3) {
                            byte b3 = bArr[i6];
                            if (!DecodeUtil.isOneByte(b3)) {
                                break;
                            }
                            i6++;
                            i7++;
                            DecodeUtil.handleOneByte(b3, cArr, i7);
                        }
                        i = i6;
                        i5 = i7;
                    } else if (DecodeUtil.isTwoBytes(b2)) {
                        if (i6 < i3) {
                            i = i6 + 1;
                            i5++;
                            DecodeUtil.handleTwoBytes(b2, bArr[i6], cArr, i5);
                        } else {
                            throw InvalidProtocolBufferException.invalidUtf8();
                        }
                    } else if (DecodeUtil.isThreeBytes(b2)) {
                        if (i6 < i3 - 1) {
                            int i8 = i6 + 1;
                            i = i8 + 1;
                            i5++;
                            DecodeUtil.handleThreeBytes(b2, bArr[i6], bArr[i8], cArr, i5);
                        } else {
                            throw InvalidProtocolBufferException.invalidUtf8();
                        }
                    } else if (i6 < i3 - 2) {
                        int i9 = i6 + 1;
                        byte b4 = bArr[i6];
                        int i10 = i9 + 1;
                        i = i10 + 1;
                        DecodeUtil.handleFourBytes(b2, b4, bArr[i9], bArr[i10], cArr, i5);
                        i5 = i5 + 1 + 1;
                    } else {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                }
                return new String(cArr, 0, i5);
            }
            throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", Integer.valueOf(bArr.length), Integer.valueOf(i), Integer.valueOf(i2)));
        }

        /* JADX WARN: Code restructure failed: missing block: B:12:0x001d, code lost:
            return r9 + r6;
         */
        @Override // com.google.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        int encodeUtf8(java.lang.CharSequence r7, byte[] r8, int r9, int r10) {
            /*
                Method dump skipped, instructions count: 254
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.SafeProcessor.encodeUtf8(java.lang.CharSequence, byte[], int, int):int");
        }

        private static int partialIsValidUtf8(byte[] bArr, int i, int i2) {
            while (i < i2 && bArr[i] >= 0) {
                i++;
            }
            if (i >= i2) {
                return 0;
            }
            return partialIsValidUtf8NonAscii(bArr, i, i2);
        }

        private static int partialIsValidUtf8NonAscii(byte[] bArr, int i, int i2) {
            while (i < i2) {
                i++;
                byte b = bArr[i];
                if (b < 0) {
                    if (b < -32) {
                        if (i >= i2) {
                            return b;
                        }
                        if (b >= -62) {
                            i++;
                            if (bArr[i] > -65) {
                            }
                        }
                        return -1;
                    } else if (b < -16) {
                        if (i >= i2 - 1) {
                            return Utf8.incompleteStateFor(bArr, i, i2);
                        }
                        int i3 = i + 1;
                        byte b2 = bArr[i];
                        if (b2 <= -65 && ((b != -32 || b2 >= -96) && (b != -19 || b2 < -96))) {
                            i = i3 + 1;
                            if (bArr[i3] > -65) {
                            }
                        }
                        return -1;
                    } else if (i >= i2 - 2) {
                        return Utf8.incompleteStateFor(bArr, i, i2);
                    } else {
                        int i4 = i + 1;
                        byte b3 = bArr[i];
                        if (b3 <= -65 && (((b << 28) + (b3 + 112)) >> 30) == 0) {
                            int i5 = i4 + 1;
                            if (bArr[i4] <= -65) {
                                i = i5 + 1;
                                if (bArr[i5] > -65) {
                                }
                            }
                        }
                        return -1;
                    }
                }
            }
            return 0;
        }
    }

    /* loaded from: classes2.dex */
    static final class UnsafeProcessor extends Processor {
        UnsafeProcessor() {
        }

        static boolean isAvailable() {
            return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
        }

        /* JADX WARN: Code restructure failed: missing block: B:34:0x0059, code lost:
            if (com.google.protobuf.UnsafeUtil.getByte(r12, r1) > (-65)) goto L_0x005e;
         */
        /* JADX WARN: Code restructure failed: missing block: B:56:0x009e, code lost:
            if (com.google.protobuf.UnsafeUtil.getByte(r12, r1) > (-65)) goto L_0x00a0;
         */
        @Override // com.google.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        int partialIsValidUtf8(int r11, byte[] r12, int r13, int r14) {
            /*
                Method dump skipped, instructions count: 204
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(int, byte[], int, int):int");
        }

        @Override // com.google.protobuf.Utf8.Processor
        String decodeUtf8(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException {
            if ((i | i2 | ((bArr.length - i) - i2)) >= 0) {
                int i3 = i + i2;
                char[] cArr = new char[i2];
                int i4 = 0;
                while (i < i3) {
                    byte b = UnsafeUtil.getByte(bArr, i);
                    if (!DecodeUtil.isOneByte(b)) {
                        break;
                    }
                    i++;
                    i4++;
                    DecodeUtil.handleOneByte(b, cArr, i4);
                }
                int i5 = i4;
                while (i < i3) {
                    int i6 = i + 1;
                    byte b2 = UnsafeUtil.getByte(bArr, i);
                    if (DecodeUtil.isOneByte(b2)) {
                        int i7 = i5 + 1;
                        DecodeUtil.handleOneByte(b2, cArr, i5);
                        while (i6 < i3) {
                            byte b3 = UnsafeUtil.getByte(bArr, i6);
                            if (!DecodeUtil.isOneByte(b3)) {
                                break;
                            }
                            i6++;
                            i7++;
                            DecodeUtil.handleOneByte(b3, cArr, i7);
                        }
                        i = i6;
                        i5 = i7;
                    } else if (DecodeUtil.isTwoBytes(b2)) {
                        if (i6 < i3) {
                            i = i6 + 1;
                            i5++;
                            DecodeUtil.handleTwoBytes(b2, UnsafeUtil.getByte(bArr, i6), cArr, i5);
                        } else {
                            throw InvalidProtocolBufferException.invalidUtf8();
                        }
                    } else if (DecodeUtil.isThreeBytes(b2)) {
                        if (i6 < i3 - 1) {
                            int i8 = i6 + 1;
                            i = i8 + 1;
                            i5++;
                            DecodeUtil.handleThreeBytes(b2, UnsafeUtil.getByte(bArr, i6), UnsafeUtil.getByte(bArr, i8), cArr, i5);
                        } else {
                            throw InvalidProtocolBufferException.invalidUtf8();
                        }
                    } else if (i6 < i3 - 2) {
                        int i9 = i6 + 1;
                        int i10 = i9 + 1;
                        i = i10 + 1;
                        DecodeUtil.handleFourBytes(b2, UnsafeUtil.getByte(bArr, i6), UnsafeUtil.getByte(bArr, i9), UnsafeUtil.getByte(bArr, i10), cArr, i5);
                        i5 = i5 + 1 + 1;
                    } else {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                }
                return new String(cArr, 0, i5);
            }
            throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", Integer.valueOf(bArr.length), Integer.valueOf(i), Integer.valueOf(i2)));
        }

        @Override // com.google.protobuf.Utf8.Processor
        int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2) {
            char c;
            long j;
            int i3;
            char charAt;
            long j2 = i;
            long j3 = i2 + j2;
            int length = charSequence.length();
            if (length > i2 || bArr.length - i2 < i) {
                throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(length - 1) + " at index " + (i + i2));
            }
            int i4 = 0;
            while (true) {
                c = 128;
                j = 1;
                if (i4 >= length || (charAt = charSequence.charAt(i4)) >= 128) {
                    break;
                }
                j2 = 1 + j2;
                UnsafeUtil.putByte(bArr, j2, (byte) charAt);
                i4++;
            }
            if (i4 == length) {
                return (int) j2;
            }
            while (i4 < length) {
                char charAt2 = charSequence.charAt(i4);
                if (charAt2 < c && j2 < j3) {
                    j2 += j;
                    UnsafeUtil.putByte(bArr, j2, (byte) charAt2);
                    j = j;
                    c = c;
                } else if (charAt2 < 2048 && j2 <= j3 - 2) {
                    long j4 = j2 + j;
                    UnsafeUtil.putByte(bArr, j2, (byte) ((charAt2 >>> 6) | 960));
                    j2 = j4 + j;
                    UnsafeUtil.putByte(bArr, j4, (byte) ((charAt2 & '?') | 128));
                    c = 128;
                    j = j;
                } else if ((charAt2 < 55296 || 57343 < charAt2) && j2 <= j3 - 3) {
                    long j5 = j2 + j;
                    UnsafeUtil.putByte(bArr, j2, (byte) ((charAt2 >>> '\f') | 480));
                    long j6 = j5 + j;
                    UnsafeUtil.putByte(bArr, j5, (byte) (((charAt2 >>> 6) & 63) | 128));
                    j2 = j6 + 1;
                    UnsafeUtil.putByte(bArr, j6, (byte) ((charAt2 & '?') | 128));
                    j = 1;
                    c = 128;
                } else if (j2 <= j3 - 4) {
                    int i5 = i4 + 1;
                    if (i5 != length) {
                        char charAt3 = charSequence.charAt(i5);
                        if (Character.isSurrogatePair(charAt2, charAt3)) {
                            int codePoint = Character.toCodePoint(charAt2, charAt3);
                            long j7 = j2 + 1;
                            UnsafeUtil.putByte(bArr, j2, (byte) ((codePoint >>> 18) | 240));
                            long j8 = j7 + 1;
                            c = 128;
                            UnsafeUtil.putByte(bArr, j7, (byte) (((codePoint >>> 12) & 63) | 128));
                            long j9 = j8 + 1;
                            UnsafeUtil.putByte(bArr, j8, (byte) (((codePoint >>> 6) & 63) | 128));
                            j = 1;
                            j2 = j9 + 1;
                            UnsafeUtil.putByte(bArr, j9, (byte) ((codePoint & 63) | 128));
                            i4 = i5;
                        } else {
                            i4 = i5;
                        }
                    }
                    throw new UnpairedSurrogateException(i4 - 1, length);
                } else if (55296 > charAt2 || charAt2 > 57343 || ((i3 = i4 + 1) != length && Character.isSurrogatePair(charAt2, charSequence.charAt(i3)))) {
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + charAt2 + " at index " + j2);
                } else {
                    throw new UnpairedSurrogateException(i4, length);
                }
                i4++;
            }
            return (int) j2;
        }

        private static int unsafeEstimateConsecutiveAscii(byte[] bArr, long j, int i) {
            if (i < 16) {
                return 0;
            }
            for (int i2 = 0; i2 < i; i2++) {
                j = 1 + j;
                if (UnsafeUtil.getByte(bArr, j) < 0) {
                    return i2;
                }
            }
            return i;
        }

        /* JADX WARN: Code restructure failed: missing block: B:21:0x0039, code lost:
            return -1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:51:0x008e, code lost:
            return -1;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static int partialIsValidUtf8(byte[] r8, long r9, int r11) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r8, r9, r11)
                int r11 = r11 - r0
                long r0 = (long) r0
                long r9 = r9 + r0
            L_0x0007:
                r0 = 0
                r1 = r0
            L_0x0009:
                r2 = 1
                if (r11 <= 0) goto L_0x001a
                long r4 = r9 + r2
                byte r1 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r1 < 0) goto L_0x0019
                int r11 = r11 + (-1)
                r9 = r4
                goto L_0x0009
            L_0x0019:
                r9 = r4
            L_0x001a:
                if (r11 != 0) goto L_0x001d
                return r0
            L_0x001d:
                int r11 = r11 + (-1)
                r0 = -32
                r4 = -65
                r5 = -1
                if (r1 >= r0) goto L_0x003a
                if (r11 != 0) goto L_0x0029
                return r1
            L_0x0029:
                int r11 = r11 + (-1)
                r0 = -62
                if (r1 < r0) goto L_0x0039
                long r2 = r2 + r9
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r9 <= r4) goto L_0x0037
                goto L_0x0039
            L_0x0037:
                r9 = r2
                goto L_0x0007
            L_0x0039:
                return r5
            L_0x003a:
                r6 = -16
                if (r1 >= r6) goto L_0x0064
                r6 = 2
                if (r11 >= r6) goto L_0x0046
                int r8 = unsafeIncompleteStateFor(r8, r1, r9, r11)
                return r8
            L_0x0046:
                int r11 = r11 + (-2)
                long r6 = r9 + r2
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r9 > r4) goto L_0x0063
                r10 = -96
                if (r1 != r0) goto L_0x0056
                if (r9 < r10) goto L_0x0063
            L_0x0056:
                r0 = -19
                if (r1 != r0) goto L_0x005c
                if (r9 >= r10) goto L_0x0063
            L_0x005c:
                long r2 = r2 + r6
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r6)
                if (r9 <= r4) goto L_0x0037
            L_0x0063:
                return r5
            L_0x0064:
                r0 = 3
                if (r11 >= r0) goto L_0x006c
                int r8 = unsafeIncompleteStateFor(r8, r1, r9, r11)
                return r8
            L_0x006c:
                int r11 = r11 + (-3)
                long r6 = r9 + r2
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r9 > r4) goto L_0x008e
                int r10 = r1 << 28
                int r9 = r9 + 112
                int r10 = r10 + r9
                int r9 = r10 >> 30
                if (r9 != 0) goto L_0x008e
                long r9 = r6 + r2
                byte r0 = com.google.protobuf.UnsafeUtil.getByte(r8, r6)
                if (r0 > r4) goto L_0x008e
                long r2 = r2 + r9
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r9 <= r4) goto L_0x0037
            L_0x008e:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(byte[], long, int):int");
        }

        private static int unsafeIncompleteStateFor(byte[] bArr, int i, long j, int i2) {
            if (i2 == 0) {
                return Utf8.incompleteStateFor(i);
            }
            if (i2 == 1) {
                return Utf8.incompleteStateFor(i, UnsafeUtil.getByte(bArr, j));
            }
            if (i2 == 2) {
                return Utf8.incompleteStateFor(i, UnsafeUtil.getByte(bArr, j), UnsafeUtil.getByte(bArr, j + 1));
            }
            throw new AssertionError();
        }
    }

    /* loaded from: classes2.dex */
    private static class DecodeUtil {
        private static char highSurrogate(int i) {
            return (char) ((i >>> 10) + 55232);
        }

        private static boolean isNotTrailingByte(byte b) {
            return b > -65;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean isOneByte(byte b) {
            return b >= 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean isThreeBytes(byte b) {
            return b < -16;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean isTwoBytes(byte b) {
            return b < -32;
        }

        private static char lowSurrogate(int i) {
            return (char) ((i & 1023) + 56320);
        }

        private static int trailingByteValue(byte b) {
            return b & 63;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void handleOneByte(byte b, char[] cArr, int i) {
            cArr[i] = (char) b;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void handleTwoBytes(byte b, byte b2, char[] cArr, int i) throws InvalidProtocolBufferException {
            if (b < -62 || isNotTrailingByte(b2)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            cArr[i] = (char) (((b & 31) << 6) | trailingByteValue(b2));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void handleThreeBytes(byte b, byte b2, byte b3, char[] cArr, int i) throws InvalidProtocolBufferException {
            if (isNotTrailingByte(b2) || ((b == -32 && b2 < -96) || ((b == -19 && b2 >= -96) || isNotTrailingByte(b3)))) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            cArr[i] = (char) (((b & 15) << 12) | (trailingByteValue(b2) << 6) | trailingByteValue(b3));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void handleFourBytes(byte b, byte b2, byte b3, byte b4, char[] cArr, int i) throws InvalidProtocolBufferException {
            if (isNotTrailingByte(b2) || (((b << 28) + (b2 + 112)) >> 30) != 0 || isNotTrailingByte(b3) || isNotTrailingByte(b4)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            int trailingByteValue = ((b & 7) << 18) | (trailingByteValue(b2) << 12) | (trailingByteValue(b3) << 6) | trailingByteValue(b4);
            cArr[i] = highSurrogate(trailingByteValue);
            cArr[i + 1] = lowSurrogate(trailingByteValue);
        }
    }
}
