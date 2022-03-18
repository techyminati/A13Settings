package com.google.android.settings.aware;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.View;
import androidx.window.R;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.utils.AnnotationSpan;
/* loaded from: classes2.dex */
abstract class AwareFooterPreferenceController extends BasePreferenceController {
    public static final String TIPS_LINK = "tips_link";
    private final AwareFeatureProvider mFeatureProvider = FeatureFactory.getFactory(this.mContext).getAwareFeatureProvider();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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

    abstract int getText();

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

    public AwareFooterPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mFeatureProvider.isSupported(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        AnnotationSpan.LinkInfo linkInfo = getLinkInfo();
        AnnotationSpan.LinkInfo tipsLinkInfo = getTipsLinkInfo();
        CharSequence text = this.mContext.getText(getText());
        if (linkInfo != null) {
            text = AnnotationSpan.linkify(text, linkInfo);
        }
        return tipsLinkInfo != null ? AnnotationSpan.linkify(text, tipsLinkInfo) : text;
    }

    private AnnotationSpan.LinkInfo getLinkInfo() {
        return new AnnotationSpan.LinkInfo("link", new View.OnClickListener() { // from class: com.google.android.settings.aware.AwareFooterPreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AwareFooterPreferenceController.this.lambda$getLinkInfo$0(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getLinkInfo$0(View view) {
        new SubSettingLauncher(this.mContext).setDestination(AwareSettings.class.getName()).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    protected AnnotationSpan.LinkInfo getTipsLinkInfo() {
        return new AnnotationSpan.LinkInfo(TIPS_LINK, new View.OnClickListener() { // from class: com.google.android.settings.aware.AwareFooterPreferenceController$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AwareFooterPreferenceController.this.lambda$getTipsLinkInfo$1(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getTipsLinkInfo$1(View view) {
        this.mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.mContext.getString(R.string.tips_help_url_gesture))));
    }
}
