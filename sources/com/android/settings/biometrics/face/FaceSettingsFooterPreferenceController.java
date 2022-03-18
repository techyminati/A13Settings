package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.HelpUtils;
/* loaded from: classes.dex */
public class FaceSettingsFooterPreferenceController extends BasePreferenceController {
    private static final String ANNOTATION_URL = "url";
    private FaceFeatureProvider mProvider;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
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

    public FaceSettingsFooterPreferenceController(Context context, String str) {
        super(context, str);
        this.mProvider = FeatureFactory.getFactory(context).getFaceFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        Context context = this.mContext;
        preference.setTitle(AnnotationSpan.linkify(this.mContext.getText(this.mProvider.isAttentionSupported(this.mContext) ? R.string.security_settings_face_settings_footer : R.string.security_settings_face_settings_footer_attention_not_supported), new AnnotationSpan.LinkInfo(this.mContext, ANNOTATION_URL, HelpUtils.getHelpIntent(context, context.getString(R.string.help_url_face), getClass().getName()))));
    }
}
