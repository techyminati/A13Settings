package com.google.android.settings.gestures.assist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.window.R;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes2.dex */
public class AssistGestureTrainingIntroActivity extends AssistGestureTrainingBase {
    private static final String FROM_ACCIDENTAL_TRIGGER_CLASS = "com.google.android.settings.gestures.assist.AssistGestureTrainingIntroActivity";

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 991;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        super.onCreate(bundle);
        ThemeHelper.trySetDynamicColor(this);
        setContentView(R.layout.assist_gesture_training_intro_activity);
        FooterBarMixin footerBarMixin = (FooterBarMixin) ((GlifLayout) findViewById(R.id.layout)).getMixin(FooterBarMixin.class);
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R.string.assist_gesture_enrollment_do_it_later).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.assist.AssistGestureTrainingIntroActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AssistGestureTrainingIntroActivity.this.onCancelButtonClicked(view);
            }
        }).setButtonType(2).setTheme(R.style.SudGlifButton_Secondary).build());
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R.string.wizard_next).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.assist.AssistGestureTrainingIntroActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AssistGestureTrainingIntroActivity.this.onNextButtonClicked(view);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build());
        FooterButton secondaryButton = footerBarMixin.getSecondaryButton();
        if ("accidental_trigger".contentEquals(getFlowType())) {
            secondaryButton.setText(this, R.string.assist_gesture_enrollment_settings);
        } else {
            secondaryButton.setText(this, R.string.assist_gesture_enrollment_do_it_later);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNextButtonClicked(View view) {
        startEnrollingActivity();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCancelButtonClicked(View view) {
        if ("accidental_trigger".contentEquals(getFlowType())) {
            launchAssistGestureSettings();
            return;
        }
        setResult(101);
        finishAndRemoveTask();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 != 0) {
            setResult(i2, intent);
            finishAndRemoveTask();
        }
    }

    private String getFlowType() {
        Intent intent = getIntent();
        if (WizardManagerHelper.isSetupWizardIntent(intent)) {
            return "setup";
        }
        if (WizardManagerHelper.isDeferredSetupWizard(intent)) {
            return "deferred_setup";
        }
        if ("com.google.android.settings.gestures.AssistGestureSuggestion".contentEquals(intent.getComponent().getClassName())) {
            return "settings_suggestion";
        }
        if (FROM_ACCIDENTAL_TRIGGER_CLASS.contentEquals(intent.getComponent().getClassName())) {
            return "accidental_trigger";
        }
        return null;
    }

    private void startEnrollingActivity() {
        Intent intent = new Intent(this, AssistGestureTrainingEnrollingActivity.class);
        intent.putExtra("launched_from", getFlowType());
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        startActivityForResult(intent, 1);
    }

    @Override // com.google.android.settings.gestures.assist.AssistGestureTrainingBase, com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
    public void onGestureProgress(float f, int i) {
        super.onGestureProgress(f, i);
    }

    @Override // com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
    public void onGestureDetected() {
        clearIndicators();
        startEnrollingActivity();
    }
}
