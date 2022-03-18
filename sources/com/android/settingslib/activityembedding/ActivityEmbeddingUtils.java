package com.android.settingslib.activityembedding;

import android.app.Activity;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.core.os.BuildCompat;
import androidx.window.embedding.SplitController;
/* loaded from: classes.dex */
public class ActivityEmbeddingUtils {
    public static boolean isActivityEmbedded(Activity activity) {
        return SplitController.getInstance().isActivityEmbedded(activity);
    }

    public static boolean shouldHideBackButton(Activity activity, boolean z) {
        if (!BuildCompat.isAtLeastT() || !z) {
            return false;
        }
        String string = Settings.Global.getString(activity.getContentResolver(), "settings_hide_secondary_page_back_button_in_two_pane");
        if (TextUtils.isEmpty(string) || TextUtils.equals("true", string)) {
            return isActivityEmbedded(activity);
        }
        return false;
    }
}
