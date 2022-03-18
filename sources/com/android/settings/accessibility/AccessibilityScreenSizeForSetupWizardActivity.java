package com.android.settings.accessibility;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.window.R;
import com.android.settings.core.InstrumentedActivity;
import com.android.settings.display.FontSizePreferenceFragmentForSetupWizard;
import com.android.settings.display.ScreenZoomPreferenceFragmentForSetupWizard;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes.dex */
public class AccessibilityScreenSizeForSetupWizardActivity extends InstrumentedActivity {
    private int mLastScrollViewHeight;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Fragment fragment;
        super.onCreate(bundle);
        setTheme(ThemeHelper.trySetDynamicColor(this) ? 2131952218 : 2131952290);
        setContentView(R.layout.accessibility_screen_size_setup_wizard);
        updateHeaderLayout();
        scrollToBottom();
        initFooterButton();
        if (bundle == null) {
            if (getFragmentType(getIntent()) == 1) {
                fragment = new FontSizePreferenceFragmentForSetupWizard();
            } else {
                fragment = new ScreenZoomPreferenceFragmentForSetupWizard();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        if (getTransitionType(getIntent()) == 2) {
            overridePendingTransition(R.anim.sud_stay, 17432577);
        }
        super.onPause();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return getFragmentType(getIntent()) == 1 ? 369 : 370;
    }

    void updateHeaderLayout() {
        if (ThemeHelper.shouldApplyExtendedPartnerConfig(this) && isSuwSupportedTwoPanes()) {
            GlifLayout glifLayout = (GlifLayout) findViewById(R.id.setup_wizard_layout);
            LinearLayout linearLayout = (LinearLayout) glifLayout.findManagedViewById(R.id.sud_layout_header);
            if (linearLayout != null) {
                linearLayout.setPadding(0, glifLayout.getPaddingTop(), 0, glifLayout.getPaddingBottom());
            }
        }
        ((TextView) findViewById(R.id.suc_layout_title)).setText(getFragmentType(getIntent()) == 1 ? R.string.title_font_size : R.string.screen_zoom_title);
        ((TextView) findViewById(R.id.sud_layout_subtitle)).setText(getFragmentType(getIntent()) == 1 ? R.string.font_size_summary : R.string.screen_zoom_summary);
    }

    private boolean isSuwSupportedTwoPanes() {
        return getResources().getBoolean(R.bool.config_suw_supported_two_panes);
    }

    private void initFooterButton() {
        ((FooterBarMixin) ((GlifLayout) findViewById(R.id.setup_wizard_layout)).getMixin(FooterBarMixin.class)).setPrimaryButton(new FooterButton.Builder(this).setText(R.string.done).setListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.AccessibilityScreenSizeForSetupWizardActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AccessibilityScreenSizeForSetupWizardActivity.this.lambda$initFooterButton$0(view);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initFooterButton$0(View view) {
        onBackPressed();
    }

    private void scrollToBottom() {
        this.mLastScrollViewHeight = 0;
        final ScrollView scrollView = ((GlifLayout) findViewById(R.id.setup_wizard_layout)).getScrollView();
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.settings.accessibility.AccessibilityScreenSizeForSetupWizardActivity$$ExternalSyntheticLambda1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public final void onGlobalLayout() {
                AccessibilityScreenSizeForSetupWizardActivity.this.lambda$scrollToBottom$2(scrollView);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scrollToBottom$2(final ScrollView scrollView) {
        int height = scrollView.getHeight();
        if (height > 0 && height != this.mLastScrollViewHeight) {
            this.mLastScrollViewHeight = height;
            scrollView.post(new Runnable() { // from class: com.android.settings.accessibility.AccessibilityScreenSizeForSetupWizardActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityScreenSizeForSetupWizardActivity.lambda$scrollToBottom$1(scrollView);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$scrollToBottom$1(ScrollView scrollView) {
        scrollView.setSmoothScrollingEnabled(false);
        scrollView.fullScroll(130);
        scrollView.setSmoothScrollingEnabled(true);
    }

    private int getTransitionType(Intent intent) {
        return intent.getIntExtra("page_transition_type", -1);
    }

    private int getFragmentType(Intent intent) {
        return intent.getIntExtra("vision_fragment_no", 1);
    }
}
