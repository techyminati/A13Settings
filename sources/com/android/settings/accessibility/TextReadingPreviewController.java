package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.view.Choreographer;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.accessibility.PreviewSizeSeekBarController;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.display.PreviewPagerAdapter;
import com.android.settings.widget.LabeledSeekBarPreference;
import java.util.Objects;
/* loaded from: classes.dex */
class TextReadingPreviewController extends BasePreferenceController implements PreviewSizeSeekBarController.ProgressInteractionListener {
    private static final long CHANGE_BY_BUTTON_DELAY_MS = 300;
    private static final long CHANGE_BY_SEEKBAR_DELAY_MS = 100;
    private static final String DISPLAY_SIZE_KEY = "display_size";
    private static final String FONT_SIZE_KEY = "font_size";
    private static final long MIN_COMMIT_INTERVAL_MS = 800;
    private static final String PREVIEW_KEY = "preview";
    static final int[] PREVIEW_SAMPLE_RES_IDS = {R.layout.accessibility_text_reading_preview_app_grid, R.layout.screen_zoom_preview_1, R.layout.accessibility_text_reading_preview_mail_content};
    private final Choreographer.FrameCallback mCommit = new Choreographer.FrameCallback() { // from class: com.android.settings.accessibility.TextReadingPreviewController$$ExternalSyntheticLambda0
        @Override // android.view.Choreographer.FrameCallback
        public final void doFrame(long j) {
            TextReadingPreviewController.this.lambda$new$0(j);
        }
    };
    private final DisplaySizeData mDisplaySizeData;
    private LabeledSeekBarPreference mDisplaySizePreference;
    private final FontSizeData mFontSizeData;
    private LabeledSeekBarPreference mFontSizePreference;
    private long mLastCommitTime;
    private int mLastDisplayProgress;
    private int mLastFontProgress;
    private TextReadingPreviewPreference mPreviewPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(long j) {
        tryCommitFontSizeConfig();
        tryCommitDisplaySizeConfig();
        this.mLastCommitTime = SystemClock.elapsedRealtime();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextReadingPreviewController(Context context, String str, FontSizeData fontSizeData, DisplaySizeData displaySizeData) {
        super(context, str);
        this.mFontSizeData = fontSizeData;
        this.mDisplaySizeData = displaySizeData;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreviewPreference = (TextReadingPreviewPreference) preferenceScreen.findPreference(PREVIEW_KEY);
        this.mFontSizePreference = (LabeledSeekBarPreference) preferenceScreen.findPreference(FONT_SIZE_KEY);
        this.mDisplaySizePreference = (LabeledSeekBarPreference) preferenceScreen.findPreference(DISPLAY_SIZE_KEY);
        Objects.requireNonNull(this.mFontSizePreference, "Font size preference is null, the preview controller couldn't get the info");
        Objects.requireNonNull(this.mDisplaySizePreference, "Display size preference is null, the preview controller couldn't get the info");
        this.mLastFontProgress = this.mFontSizePreference.getProgress();
        this.mLastDisplayProgress = this.mDisplaySizePreference.getProgress();
        Configuration configuration = this.mContext.getResources().getConfiguration();
        boolean z = true;
        if (configuration.getLayoutDirection() != 1) {
            z = false;
        }
        PreviewPagerAdapter previewPagerAdapter = new PreviewPagerAdapter(this.mContext, z, PREVIEW_SAMPLE_RES_IDS, createConfig(configuration));
        this.mPreviewPreference.setPreviewAdapter(previewPagerAdapter);
        previewPagerAdapter.setPreviewLayer(0, 0, 0, false);
    }

    @Override // com.android.settings.accessibility.PreviewSizeSeekBarController.ProgressInteractionListener
    public void notifyPreferenceChanged() {
        int size = this.mDisplaySizeData.getValues().size();
        TextReadingPreviewPreference textReadingPreviewPreference = this.mPreviewPreference;
        textReadingPreviewPreference.notifyPreviewPagerChanged((this.mFontSizePreference.getProgress() * size) + this.mDisplaySizePreference.getProgress());
    }

    @Override // com.android.settings.accessibility.PreviewSizeSeekBarController.ProgressInteractionListener
    public void onProgressChanged() {
        postCommitDelayed(CHANGE_BY_BUTTON_DELAY_MS);
    }

    @Override // com.android.settings.accessibility.PreviewSizeSeekBarController.ProgressInteractionListener
    public void onEndTrackingTouch() {
        postCommitDelayed(CHANGE_BY_SEEKBAR_DELAY_MS);
    }

    void postCommitDelayed(long j) {
        if (SystemClock.elapsedRealtime() - this.mLastCommitTime < MIN_COMMIT_INTERVAL_MS) {
            j += MIN_COMMIT_INTERVAL_MS;
        }
        Choreographer instance = Choreographer.getInstance();
        instance.removeFrameCallback(this.mCommit);
        instance.postFrameCallbackDelayed(this.mCommit, j);
    }

    private void tryCommitFontSizeConfig() {
        int progress = this.mFontSizePreference.getProgress();
        if (progress != this.mLastFontProgress) {
            this.mFontSizeData.commit(progress);
            this.mLastFontProgress = progress;
        }
    }

    private void tryCommitDisplaySizeConfig() {
        int progress = this.mDisplaySizePreference.getProgress();
        if (progress != this.mLastDisplayProgress) {
            this.mDisplaySizeData.commit(progress);
            this.mLastDisplayProgress = progress;
        }
    }

    private Configuration[] createConfig(Configuration configuration) {
        int size = this.mFontSizeData.getValues().size();
        int size2 = this.mDisplaySizeData.getValues().size();
        Configuration[] configurationArr = new Configuration[size * size2];
        for (int i = 0; i < size; i++) {
            for (int i2 = 0; i2 < size2; i2++) {
                Configuration configuration2 = new Configuration(configuration);
                configuration2.fontScale = this.mFontSizeData.getValues().get(i).floatValue();
                configuration2.densityDpi = this.mDisplaySizeData.getValues().get(i2).intValue();
                configurationArr[(i * size2) + i2] = configuration2;
            }
        }
        return configurationArr;
    }
}
