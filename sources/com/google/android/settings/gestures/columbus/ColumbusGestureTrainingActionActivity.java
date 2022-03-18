package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.window.R;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
/* loaded from: classes2.dex */
public class ColumbusGestureTrainingActionActivity extends ColumbusGestureTrainingBase {
    private RadioGroup mRadioGroup;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1758;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureTrainingBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        setContentView(R.layout.columbus_gesture_training_action_activity);
        super.onCreate(bundle);
        this.mRadioGroup = (RadioGroup) findViewById(R.id.actions);
        GlifLayout glifLayout = (GlifLayout) findViewById(R.id.layout);
        glifLayout.setDescriptionText(R.string.columbus_gesture_training_action_text);
        FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R.string.wizard_next).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingActionActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingActionActivity.this.onNextButtonClicked(view);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build());
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R.string.columbus_gesture_enrollment_do_it_later).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingActionActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingActionActivity.this.onCancelButtonClicked(view);
            }
        }).setButtonType(2).setTheme(R.style.SudGlifButton_Secondary).build());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 || i == 2) {
            setResult(i2, intent);
            finishAndRemoveTask();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNextButtonClicked(View view) {
        int checkedRadioButtonId = this.mRadioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == 0) {
            Toast.makeText(this, (int) R.string.columbus_gesture_training_action_no_selection_error, 0).show();
        } else if (checkedRadioButtonId == R.id.launch) {
            Settings.Secure.putStringForUser(getContentResolver(), "columbus_action", getString(R.string.columbus_setting_action_launch_value), ActivityManager.getCurrentUser());
            startLaunchActivity();
        } else {
            Settings.Secure.putStringForUser(getContentResolver(), "columbus_action", ((ColumbusRadioButton) findViewById(checkedRadioButtonId)).getSecureValue(), ActivityManager.getCurrentUser());
            startFinishedActivity();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCancelButtonClicked(View view) {
        setResult(101);
        finishAndRemoveTask();
    }

    private void startLaunchActivity() {
        Intent intent = new Intent(this, ColumbusGestureTrainingLaunchActivity.class);
        intent.putExtra("launched_from", getIntent().getStringExtra("launched_from"));
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        startActivityForResult(intent, 1);
    }

    private void startFinishedActivity() {
        Intent intent = new Intent(this, ColumbusGestureTrainingFinishedActivity.class);
        intent.putExtra("launched_from", getIntent().getStringExtra("launched_from"));
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        startActivityForResult(intent, 2);
    }
}
