package com.google.android.settings.gestures.assist;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import androidx.window.R;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes2.dex */
public class AssistGestureTrainingEnrollingActivity extends AssistGestureTrainingSliderBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 992;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingSliderBase, com.google.android.settings.gestures.assist.AssistGestureTrainingBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        setContentView(R.layout.assist_gesture_training_enrolling_activity);
        super.onCreate(bundle);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(3, 100L);
        ((LinearLayout) findViewById(R.id.content_container)).setLayoutTransition(layoutTransition);
        ((FooterBarMixin) ((GlifLayout) findViewById(R.id.layout)).getMixin(FooterBarMixin.class)).setSecondaryButton(new FooterButton.Builder(this).setText(R.string.assist_gesture_enrollment_do_it_later).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.assist.AssistGestureTrainingEnrollingActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AssistGestureTrainingEnrollingActivity.this.onCancelButtonClicked(view);
            }
        }).setButtonType(2).setTheme(R.style.SudGlifButton_Secondary).build());
    }

    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingSliderBase
    protected void handleGestureDetected() {
        clearIndicators();
        this.mErrorView.setVisibility(4);
        Settings.Secure.putInt(getContentResolver(), "assist_gesture_setup_complete", 1);
        startFinishedActivity();
        finishAndRemoveTask();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCancelButtonClicked(View view) {
        setResult(101);
        finishAndRemoveTask();
    }

    private void startFinishedActivity() {
        Intent intent = new Intent(this, AssistGestureTrainingFinishedActivity.class);
        intent.putExtra("launched_from", getIntent().getStringExtra("launched_from"));
        intent.addFlags(33554432);
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        startActivity(intent);
    }
}
