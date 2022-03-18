package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import androidx.window.R;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
/* loaded from: classes2.dex */
public class ColumbusGestureTrainingFinishedActivity extends ColumbusGestureTrainingBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1750;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureTrainingBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        setContentView(R.layout.columbus_gesture_training_finished_activity);
        GlifLayout glifLayout = (GlifLayout) findViewById(R.id.layout);
        super.onCreate(bundle);
        glifLayout.setHeaderText(R.string.columbus_gesture_training_finished_title);
        glifLayout.setDescriptionText(R.string.columbus_gesture_training_finished_text);
        FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R.string.columbus_gesture_enrollment_settings).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingFinishedActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingFinishedActivity.this.onSettingsButtonClicked(view);
            }
        }).setButtonType(0).setTheme(R.style.SudGlifButton_Secondary).build());
        FooterButton secondaryButton = footerBarMixin.getSecondaryButton();
        if (flowTypeDeferredSetup() || flowTypeSetup()) {
            secondaryButton.setVisibility(4);
        }
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R.string.done).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingFinishedActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingFinishedActivity.this.onNextButtonClicked(view);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build());
        FooterButton primaryButton = footerBarMixin.getPrimaryButton();
        if (flowTypeDeferredSetup() || flowTypeSetup()) {
            primaryButton.setText(this, R.string.next_label);
        } else if (flowTypeSettingsSuggestion()) {
            primaryButton.setText(this, R.string.done);
        } else if (flowTypeAccidentalTrigger()) {
            primaryButton.setText(this, R.string.columbus_gesture_enrollment_complete);
        }
        Settings.Secure.putIntForUser(getContentResolver(), "columbus_suw_complete", 1, ActivityManager.getCurrentUser());
        setEnableColumbusOnPause();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNextButtonClicked(View view) {
        handleDone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSettingsButtonClicked(View view) {
        launchColumbusGestureSettings(getMetricsCategory());
    }
}
