package com.android.settings.accessibility;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.settingslib.Utils;
import com.google.android.setupdesign.GlifPreferenceLayout;
/* loaded from: classes.dex */
public class TextReadingPreferenceFragmentForSetupWizard extends TextReadingPreferenceFragment {
    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        String string = getContext().getString(R.string.accessibility_text_reading_options_title);
        Drawable drawable = getContext().getDrawable(R.drawable.ic_font_download);
        drawable.setTintList(Utils.getColorAttr(getContext(), 16843827));
        AccessibilitySetupWizardUtils.updateGlifPreferenceLayout(getContext(), (GlifPreferenceLayout) view, string, null, drawable);
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return ((GlifPreferenceLayout) viewGroup).onCreateRecyclerView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.accessibility.TextReadingPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return super.getMetricsCategory();
    }
}
