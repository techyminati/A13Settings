package com.android.settings.activityembedding;

import android.content.Context;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.TypedValue;
import androidx.window.R;
import androidx.window.embedding.SplitController;
/* loaded from: classes.dex */
public class ActivityEmbeddingUtils {
    public static int getMinCurrentScreenSplitWidthPx(Context context) {
        return (int) TypedValue.applyDimension(1, 720.0f, context.getResources().getDisplayMetrics());
    }

    public static int getMinSmallestScreenSplitWidthPx(Context context) {
        return (int) TypedValue.applyDimension(1, 600.0f, context.getResources().getDisplayMetrics());
    }

    public static float getSplitRatio(Context context) {
        return context.getResources().getFloat(R.dimen.config_activity_embed_split_ratio);
    }

    public static boolean isEmbeddingActivityEnabled(Context context) {
        boolean isEnabled = FeatureFlagUtils.isEnabled(context, "settings_support_large_screen");
        boolean isSplitSupported = SplitController.getInstance().isSplitSupported();
        Log.d("ActivityEmbeddingUtils", "isFlagEnabled = " + isEnabled);
        Log.d("ActivityEmbeddingUtils", "isSplitSupported = " + isSplitSupported);
        return isEnabled && isSplitSupported;
    }
}
