package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import androidx.window.R;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
/* loaded from: classes2.dex */
public class ColumbusGestureTrainingIntroActivity extends ColumbusGestureTrainingBase {
    private static final String FROM_ACCIDENTAL_TRIGGER_CLASS = "com.google.android.settings.gestures.columbus.ColumbusGestureTrainingIntroActivity";

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1748;
    }

    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureTrainingBase, com.google.android.settings.gestures.columbus.ColumbusGestureHelper.GestureListener
    public void onTrigger() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureTrainingBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        super.onCreate(bundle);
        if (!ColumbusPreferenceController.isColumbusSupported(this)) {
            finish();
            return;
        }
        if (Settings.Secure.getIntForUser(getContentResolver(), "columbus_suw_complete", 0, ActivityManager.getCurrentUser()) != 0) {
            launchColumbusGestureSettings(0);
            finish();
            return;
        }
        setContentView(R.layout.columbus_gesture_training_intro_activity);
        GlifLayout glifLayout = (GlifLayout) findViewById(R.id.layout);
        glifLayout.setDescriptionText(R.string.columbus_gesture_training_intro_text_suw);
        FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R.string.columbus_gesture_enrollment_try_it).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingIntroActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingIntroActivity.this.onNextButtonClicked(view);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build());
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R.string.columbus_gesture_enrollment_do_it_later).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingIntroActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingIntroActivity.this.onCancelButtonClicked(view);
            }
        }).setButtonType(2).setTheme(R.style.SudGlifButton_Secondary).build());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1) {
            setResult(i2, intent);
            finishAndRemoveTask();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNextButtonClicked(View view) {
        startEnrollingActivity();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCancelButtonClicked(View view) {
        setResult(101);
        finishAndRemoveTask();
    }

    private void startEnrollingActivity() {
        Intent intent = new Intent(this, ColumbusGestureTrainingEnrollingActivity.class);
        intent.putExtra("launched_from", getFlowType());
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        startActivityForResult(intent, 1);
    }

    private String getFlowType() {
        Intent intent = getIntent();
        if (WizardManagerHelper.isSetupWizardIntent(intent)) {
            return "setup";
        }
        if (WizardManagerHelper.isDeferredSetupWizard(intent)) {
            return "deferred_setup";
        }
        if ("com.google.android.settings.gestures.ColumbusGestureSuggestion".contentEquals(intent.getComponent().getClassName())) {
            return "settings_suggestion";
        }
        if (FROM_ACCIDENTAL_TRIGGER_CLASS.contentEquals(intent.getComponent().getClassName())) {
            return "accidental_trigger";
        }
        return null;
    }
}
