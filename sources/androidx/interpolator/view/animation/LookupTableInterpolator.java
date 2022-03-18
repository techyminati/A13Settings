package androidx.interpolator.view.animation;
/* loaded from: classes.dex */
final class LookupTableInterpolator {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static float interpolate(float[] fArr, float f, float f2) {
        if (f2 >= 1.0f) {
            return 1.0f;
        }
        if (f2 <= 0.0f) {
            return 0.0f;
        }
        int min = Math.min((int) ((fArr.length - 1) * f2), fArr.length - 2);
        return fArr[min] + (((f2 - (min * f)) / f) * (fArr[min + 1] - fArr[min]));
    }
}
