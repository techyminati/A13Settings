package com.google.android.settings.gestures.columbus;

import android.content.Context;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import androidx.window.R;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes2.dex */
public class ColumbusRadioButtonPreference extends SelectorWithWidgetPreference {
    private ContextualSummaryProvider mContextualSummaryProvider;
    private ImageView mExtraWidgetView;
    private int mMetric;

    /* loaded from: classes2.dex */
    public interface ContextualSummaryProvider {
        CharSequence getSummary(Context context);
    }

    public ColumbusRadioButtonPreference(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.widget.SelectorWithWidgetPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mExtraWidgetView = (ImageView) preferenceViewHolder.findViewById(R.id.selector_extra_widget);
        updateAccessibilityDescription();
    }

    @Override // androidx.preference.Preference
    public void setTitle(CharSequence charSequence) {
        super.setTitle(charSequence);
        updateAccessibilityDescription();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMetric(int i) {
        this.mMetric = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMetric() {
        return this.mMetric;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContextualSummaryProvider(ContextualSummaryProvider contextualSummaryProvider) {
        this.mContextualSummaryProvider = contextualSummaryProvider;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSummary(Context context) {
        ContextualSummaryProvider contextualSummaryProvider = this.mContextualSummaryProvider;
        if (contextualSummaryProvider == null) {
            setSummary((CharSequence) null);
        } else {
            setSummary(contextualSummaryProvider.getSummary(context));
        }
    }

    private void updateAccessibilityDescription() {
        ImageView imageView = this.mExtraWidgetView;
        if (imageView != null) {
            imageView.setContentDescription(getContext().getString(R.string.columbus_radio_button_extra_widget_a11y_label, getTitle()));
        }
    }
}
