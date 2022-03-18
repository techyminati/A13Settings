package com.google.android.settings.gestures.columbus;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.window.R;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes2.dex */
public class ColumbusGestureTrainingLaunchActivity extends ColumbusGestureTrainingBase {
    private RadioGroup mRadioGroup;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1759;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureTrainingBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        setContentView(R.layout.columbus_gesture_training_launch_activity);
        super.onCreate(bundle);
        this.mRadioGroup = (RadioGroup) findViewById(R.id.apps);
        List<LauncherActivityInfo> activityList = ((LauncherApps) getSystemService(LauncherApps.class)).getActivityList(null, UserHandle.of(ActivityManager.getCurrentUser()));
        activityList.sort(Comparator.comparing(ColumbusGestureTrainingLaunchActivity$$ExternalSyntheticLambda2.INSTANCE));
        LayoutInflater from = LayoutInflater.from(this.mRadioGroup.getContext());
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.columbus_app_icon_size);
        for (LauncherActivityInfo launcherActivityInfo : activityList) {
            ColumbusRadioButton columbusRadioButton = (ColumbusRadioButton) from.inflate(R.layout.columbus_app_list_item, (ViewGroup) this.mRadioGroup, false);
            columbusRadioButton.setText(launcherActivityInfo.getLabel());
            Drawable icon = launcherActivityInfo.getIcon(0);
            icon.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
            columbusRadioButton.setCompoundDrawablesRelative(icon, null, null, null);
            columbusRadioButton.setSecureValue(launcherActivityInfo.getComponentName().flattenToString());
            this.mRadioGroup.addView(columbusRadioButton);
        }
        GlifLayout glifLayout = (GlifLayout) findViewById(R.id.layout);
        glifLayout.setDescriptionText(R.string.columbus_gesture_training_launch_text);
        FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R.string.wizard_next).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingLaunchActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingLaunchActivity.this.onNextButtonClicked(view);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build());
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R.string.columbus_gesture_enrollment_do_it_later).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingLaunchActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingLaunchActivity.this.onCancelButtonClicked(view);
            }
        }).setButtonType(2).setTheme(R.style.SudGlifButton_Secondary).build());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$onCreate$0(LauncherActivityInfo launcherActivityInfo) {
        return launcherActivityInfo.getLabel().toString();
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
        ColumbusRadioButton columbusRadioButton = (ColumbusRadioButton) this.mRadioGroup.findViewById(this.mRadioGroup.getCheckedRadioButtonId());
        String secureValue = columbusRadioButton == null ? null : columbusRadioButton.getSecureValue();
        if (secureValue == null) {
            Toast.makeText(this, (int) R.string.columbus_gesture_training_launch_no_selection_error, 0).show();
            return;
        }
        Settings.Secure.putStringForUser(getContentResolver(), "columbus_launch_app", secureValue, ActivityManager.getCurrentUser());
        Settings.Secure.putStringForUser(getContentResolver(), "columbus_launch_app_shortcut", secureValue, ActivityManager.getCurrentUser());
        startFinishedActivity();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCancelButtonClicked(View view) {
        setResult(101);
        finishAndRemoveTask();
    }

    private void startFinishedActivity() {
        Intent intent = new Intent(this, ColumbusGestureTrainingFinishedActivity.class);
        intent.putExtra("launched_from", getIntent().getStringExtra("launched_from"));
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        startActivityForResult(intent, 1);
    }
}
