package com.google.zxing.datamatrix.decoder;

import com.google.zxing.datamatrix.decoder.Version;
/* loaded from: classes2.dex */
final class DataBlock {
    private final byte[] codewords;
    private final int numDataCodewords;

    private DataBlock(int i, byte[] bArr) {
        this.numDataCodewords = i;
        this.codewords = bArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DataBlock[] getDataBlocks(byte[] bArr, Version version) {
        Version.ECBlocks eCBlocks = version.getECBlocks();
        Version.ECB[] eCBlocks2 = eCBlocks.getECBlocks();
        int i = 0;
        for (Version.ECB ecb : eCBlocks2) {
            i += ecb.getCount();
        }
        DataBlock[] dataBlockArr = new DataBlock[i];
        int i2 = 0;
        for (Version.ECB ecb2 : eCBlocks2) {
            for (int i3 = 0; i3 < ecb2.getCount(); i3++) {
                int dataCodewords = ecb2.getDataCodewords();
                i2++;
                dataBlockArr[i2] = new DataBlock(dataCodewords, new byte[eCBlocks.getECCodewords() + dataCodewords]);
            }
        }
        int length = dataBlockArr[0].codewords.length - eCBlocks.getECCodewords();
        int i4 = length - 1;
        int i5 = 0;
        for (int i6 = 0; i6 < i4; i6++) {
            for (int i7 = 0; i7 < i2; i7++) {
                i5++;
                dataBlockArr[i7].codewords[i6] = bArr[i5];
            }
        }
        boolean z = version.getVersionNumber() == 24;
        int i8 = z ? 8 : i2;
        for (int i9 = 0; i9 < i8; i9++) {
            i5++;
            dataBlockArr[i9].codewords[i4] = bArr[i5];
        }
        int length2 = dataBlockArr[0].codewords.length;
        while (length < length2) {
            int i10 = 0;
            while (i10 < i2) {
                i5++;
                dataBlockArr[i10].codewords[(!z || i10 <= 7) ? length : length - 1] = bArr[i5];
                i10++;
            }
            length++;
        }
        if (i5 == bArr.length) {
            return dataBlockArr;
        }
        throw new IllegalArgumentException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNumDataCodewords() {
        return this.numDataCodewords;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getCodewords() {
        return this.codewords;
    }
}
