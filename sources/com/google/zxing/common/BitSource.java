package com.google.zxing.common;
/* loaded from: classes2.dex */
public final class BitSource {
    private int bitOffset;
    private int byteOffset;
    private final byte[] bytes;

    public BitSource(byte[] bArr) {
        this.bytes = bArr;
    }

    public int getBitOffset() {
        return this.bitOffset;
    }

    public int getByteOffset() {
        return this.byteOffset;
    }

    public int readBits(int i) {
        if (i < 1 || i > 32 || i > available()) {
            throw new IllegalArgumentException(String.valueOf(i));
        }
        int i2 = this.bitOffset;
        int i3 = 0;
        if (i2 > 0) {
            int i4 = 8 - i2;
            int i5 = i < i4 ? i : i4;
            int i6 = i4 - i5;
            byte[] bArr = this.bytes;
            int i7 = this.byteOffset;
            i3 = (((255 >> (8 - i5)) << i6) & bArr[i7]) >> i6;
            i -= i5;
            int i8 = i2 + i5;
            this.bitOffset = i8;
            if (i8 == 8) {
                this.bitOffset = 0;
                this.byteOffset = i7 + 1;
            }
        }
        if (i <= 0) {
            return i3;
        }
        while (i >= 8) {
            int i9 = i3 << 8;
            byte[] bArr2 = this.bytes;
            int i10 = this.byteOffset;
            i3 = (bArr2[i10] & 255) | i9;
            this.byteOffset = i10 + 1;
            i -= 8;
        }
        if (i <= 0) {
            return i3;
        }
        int i11 = 8 - i;
        int i12 = (i3 << i) | ((((255 >> i11) << i11) & this.bytes[this.byteOffset]) >> i11);
        this.bitOffset += i;
        return i12;
    }

    public int available() {
        return ((this.bytes.length - this.byteOffset) * 8) - this.bitOffset;
    }
}
