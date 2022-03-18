package com.google.android.settings.gestures.columbus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.window.R;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
/* loaded from: classes2.dex */
public class ColumbusGestureTrainingEnrollingActivity extends ColumbusGestureTrainingBase {
    private LottieAnimationView mAnimation;
    private boolean mFirstGestureDetected;
    private final Handler mHandler = new Handler(Looper.myLooper());
    private ColumbusEnrollingIllustration mIllustration;
    private GlifLayout mLayout;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1749;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureTrainingBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        setContentView(R.layout.columbus_gesture_training_enrolling_activity);
        super.onCreate(bundle);
        GlifLayout glifLayout = (GlifLayout) findViewById(R.id.layout);
        this.mLayout = glifLayout;
        this.mAnimation = (LottieAnimationView) glifLayout.findViewById(R.id.animation);
        this.mIllustration = (ColumbusEnrollingIllustration) this.mLayout.findViewById(R.id.columbus_gesture_illustration);
        this.mLayout.setDescriptionText(R.string.columbus_gesture_training_enrolling_text);
        ((FooterBarMixin) this.mLayout.getMixin(FooterBarMixin.class)).setSecondaryButton(new FooterButton.Builder(this).setText(R.string.columbus_gesture_enrollment_do_it_later).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingEnrollingActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingEnrollingActivity.this.onCancelButtonClicked(view);
            }
        }).setButtonType(2).setTheme(R.style.SudGlifButton_Secondary).build());
    }

    @Override // com.google.android.settings.gestures.columbus.ColumbusGestureTrainingBase, com.google.android.settings.gestures.columbus.ColumbusGestureHelper.GestureListener
    public void onTrigger() {
        if (this.mFirstGestureDetected) {
            this.mHandler.post(new Runnable() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingEnrollingActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ColumbusGestureTrainingEnrollingActivity.this.lambda$onTrigger$1();
                }
            });
            return;
        }
        this.mFirstGestureDetected = true;
        this.mHandler.post(new Runnable() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingEnrollingActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ColumbusGestureTrainingEnrollingActivity.this.lambda$onTrigger$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTrigger$1() {
        this.mLayout.setHeaderText(R.string.columbus_gesture_training_enrolling_second_gesture_title);
        this.mLayout.setDescriptionText(R.string.columbus_gesture_training_enrolling_second_gesture_text);
        ((FooterBarMixin) this.mLayout.getMixin(FooterBarMixin.class)).setPrimaryButton(new FooterButton.Builder(this).setText(R.string.wizard_next).setListener(new View.OnClickListener() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingEnrollingActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColumbusGestureTrainingEnrollingActivity.this.onNextButtonClicked(view);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build());
        this.mIllustration.setGestureCount(2, new Runnable() { // from class: com.google.android.settings.gestures.columbus.ColumbusGestureTrainingEnrollingActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ColumbusGestureTrainingEnrollingActivity.this.lambda$onTrigger$0();
            }
        });
        this.mLayout.requestAccessibilityFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTrigger$0() {
        this.mAnimation.cancelAnimation();
        this.mAnimation.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTrigger$2() {
        this.mLayout.setHeaderText(R.string.columbus_gesture_training_enrolling_first_gesture_title);
        this.mLayout.setDescriptionText(R.string.columbus_gesture_training_enrolling_first_gesture_text);
        this.mIllustration.setGestureCount(1, null);
        this.mLayout.requestAccessibilityFocus();
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
        startActionActivity();
        finishAndRemoveTask();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCancelButtonClicked(View view) {
        setResult(101);
        finishAndRemoveTask();
    }

    private void startActionActivity() {
        Intent intent = new Intent(this, ColumbusGestureTrainingActionActivity.class);
        intent.putExtra("launched_from", getIntent().getStringExtra("launched_from"));
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        startActivityForResult(intent, 1);
    }
}
