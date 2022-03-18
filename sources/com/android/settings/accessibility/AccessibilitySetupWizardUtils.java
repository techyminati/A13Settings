package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import androidx.window.R;
import com.google.android.setupdesign.GlifPreferenceLayout;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes.dex */
class AccessibilitySetupWizardUtils {
    public static void updateGlifPreferenceLayout(Context context, GlifPreferenceLayout glifPreferenceLayout, CharSequence charSequence, CharSequence charSequence2, Drawable drawable) {
        LinearLayout linearLayout;
        glifPreferenceLayout.setHeaderText(charSequence);
        glifPreferenceLayout.setDescriptionText(charSequence2);
        glifPreferenceLayout.setIcon(drawable);
        glifPreferenceLayout.setDividerInsets(Integer.MAX_VALUE, 0);
        if (ThemeHelper.shouldApplyExtendedPartnerConfig(context) && (linearLayout = (LinearLayout) glifPreferenceLayout.findManagedViewById(R.id.sud_layout_header)) != null) {
            linearLayout.setPadding(0, glifPreferenceLayout.getPaddingTop(), 0, glifPreferenceLayout.getPaddingBottom());
        }
    }
}
