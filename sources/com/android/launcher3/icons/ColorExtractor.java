package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import java.util.Arrays;
/* loaded from: classes.dex */
public class ColorExtractor {
    private final int NUM_SAMPLES = 20;
    private final float[] mTmpHsv = new float[3];
    private final float[] mTmpHueScoreHistogram = new float[360];
    private final int[] mTmpPixels = new int[20];
    private final SparseArray<Float> mTmpRgbScores = new SparseArray<>();

    public int findDominantColorByHue(Bitmap bitmap) {
        return findDominantColorByHue(bitmap, 20);
    }

    public int findDominantColorByHue(Bitmap bitmap, int i) {
        int i2;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int sqrt = (int) Math.sqrt((height * width) / i);
        if (sqrt < 1) {
            sqrt = 1;
        }
        float[] fArr = this.mTmpHsv;
        Arrays.fill(fArr, 0.0f);
        float[] fArr2 = this.mTmpHueScoreHistogram;
        Arrays.fill(fArr2, 0.0f);
        int i3 = -1;
        int[] iArr = this.mTmpPixels;
        int i4 = 0;
        Arrays.fill(iArr, 0);
        int i5 = 0;
        int i6 = 0;
        float f = -1.0f;
        while (true) {
            i2 = -16777216;
            if (i5 >= height) {
                break;
            }
            while (i4 < width) {
                int pixel = bitmap.getPixel(i4, i5);
                if (((pixel >> 24) & 255) < 128) {
                    height = height;
                } else {
                    int i7 = pixel | (-16777216);
                    Color.colorToHSV(i7, fArr);
                    height = height;
                    int i8 = (int) fArr[0];
                    if (i8 >= 0 && i8 < fArr2.length) {
                        if (i6 < i) {
                            i6++;
                            iArr[i6] = i7;
                        }
                        fArr2[i8] = fArr2[i8] + (fArr[1] * fArr[2]);
                        if (fArr2[i8] > f) {
                            i3 = i8;
                            f = fArr2[i8];
                        }
                    }
                }
                i4 += sqrt;
            }
            i5 += sqrt;
            i4 = 0;
        }
        SparseArray<Float> sparseArray = this.mTmpRgbScores;
        sparseArray.clear();
        float f2 = -1.0f;
        for (int i9 = 0; i9 < i6; i9++) {
            int i10 = iArr[i9];
            Color.colorToHSV(i10, fArr);
            if (((int) fArr[0]) == i3) {
                float f3 = fArr[1];
                float f4 = fArr[2];
                int i11 = ((int) (100.0f * f3)) + ((int) (10000.0f * f4));
                float f5 = f3 * f4;
                Float f6 = sparseArray.get(i11);
                if (f6 != null) {
                    f5 += f6.floatValue();
                }
                sparseArray.put(i11, Float.valueOf(f5));
                if (f5 > f2) {
                    i2 = i10;
                    f2 = f5;
                }
            }
        }
        return i2;
    }
}
