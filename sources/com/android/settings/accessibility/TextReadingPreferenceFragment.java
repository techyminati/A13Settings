package com.android.settings.accessibility;

import android.content.Context;
import androidx.window.R;
import com.android.settings.accessibility.TextReadingResetController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class TextReadingPreferenceFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.accessibility_text_reading_options);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "TextReadingPreferenceFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1912;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.accessibility_text_reading_options;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        FontSizeData fontSizeData = new FontSizeData(context);
        DisplaySizeData displaySizeData = new DisplaySizeData(context);
        TextReadingPreviewController textReadingPreviewController = new TextReadingPreviewController(context, "preview", fontSizeData, displaySizeData);
        arrayList.add(textReadingPreviewController);
        PreviewSizeSeekBarController previewSizeSeekBarController = new PreviewSizeSeekBarController(context, "font_size", fontSizeData);
        previewSizeSeekBarController.setInteractionListener(textReadingPreviewController);
        arrayList.add(previewSizeSeekBarController);
        PreviewSizeSeekBarController previewSizeSeekBarController2 = new PreviewSizeSeekBarController(context, "display_size", displaySizeData);
        previewSizeSeekBarController2.setInteractionListener(textReadingPreviewController);
        arrayList.add(previewSizeSeekBarController2);
        arrayList.add(new FontWeightAdjustmentPreferenceController(context, "toggle_force_bold_text"));
        arrayList.add(new HighTextContrastPreferenceController(context, "toggle_high_text_contrast_preference"));
        arrayList.add(new TextReadingResetController(context, "reset", (List) arrayList.stream().filter(TextReadingPreferenceFragment$$ExternalSyntheticLambda1.INSTANCE).map(TextReadingPreferenceFragment$$ExternalSyntheticLambda0.INSTANCE).collect(Collectors.toList())));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createPreferenceControllers$0(AbstractPreferenceController abstractPreferenceController) {
        return abstractPreferenceController instanceof TextReadingResetController.ResetStateListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ TextReadingResetController.ResetStateListener lambda$createPreferenceControllers$1(AbstractPreferenceController abstractPreferenceController) {
        return (TextReadingResetController.ResetStateListener) abstractPreferenceController;
    }
}
