package com.android.settings.biometrics.face;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;
import androidx.window.R;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.BiometricErrorDialog;
import com.android.settings.biometrics.BiometricsEnrollEnrolling;
import com.android.settings.biometrics.face.ParticleCollection;
import com.android.settings.slices.CustomSliceRegistry;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class FaceEnrollEnrolling extends BiometricsEnrollEnrolling {
    private TextView mErrorText;
    private Interpolator mLinearOutSlowInInterpolator;
    private FaceEnrollPreviewFragment mPreviewFragment;
    private ArrayList<Integer> mDisabledFeatures = new ArrayList<>();
    private ParticleCollection.Listener mListener = new ParticleCollection.Listener() { // from class: com.android.settings.biometrics.face.FaceEnrollEnrolling.1
        @Override // com.android.settings.biometrics.face.ParticleCollection.Listener
        public void onEnrolled() {
            FaceEnrollEnrolling faceEnrollEnrolling = FaceEnrollEnrolling.this;
            faceEnrollEnrolling.launchFinish(((BiometricEnrollBase) faceEnrollEnrolling).mToken);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1507;
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected boolean shouldStartAutomatically() {
        return false;
    }

    /* loaded from: classes.dex */
    public static class FaceErrorDialog extends BiometricErrorDialog {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 1510;
        }

        @Override // com.android.settings.biometrics.BiometricErrorDialog
        public int getOkButtonTextResId() {
            return R.string.security_settings_face_enroll_dialog_ok;
        }

        @Override // com.android.settings.biometrics.BiometricErrorDialog
        public int getTitleResId() {
            return R.string.security_settings_face_enroll_error_dialog_title;
        }

        static FaceErrorDialog newInstance(CharSequence charSequence, int i) {
            FaceErrorDialog faceErrorDialog = new FaceErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("error_msg", charSequence);
            bundle.putInt("error_id", i);
            faceErrorDialog.setArguments(bundle);
            return faceErrorDialog;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.face_enroll_enrolling);
        setHeaderText(R.string.security_settings_face_enroll_repeat_title);
        this.mErrorText = (TextView) findViewById(R.id.error_text);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(this, 17563662);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R.string.security_settings_face_enroll_enrolling_skip).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollEnrolling$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FaceEnrollEnrolling.this.onSkipButtonClick(view);
            }
        }).setButtonType(7).setTheme(R.style.SudGlifButton_Secondary).build());
        if (!getIntent().getBooleanExtra("accessibility_diversity", true)) {
            this.mDisabledFeatures.add(2);
        }
        if (!getIntent().getBooleanExtra("accessibility_vision", true)) {
            this.mDisabledFeatures.add(1);
        }
        startEnrollment();
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    public void startEnrollment() {
        super.startEnrollment();
        FaceEnrollPreviewFragment faceEnrollPreviewFragment = (FaceEnrollPreviewFragment) getSupportFragmentManager().findFragmentByTag("tag_preview");
        this.mPreviewFragment = faceEnrollPreviewFragment;
        if (faceEnrollPreviewFragment == null) {
            this.mPreviewFragment = new FaceEnrollPreviewFragment();
            getSupportFragmentManager().beginTransaction().add(this.mPreviewFragment, "tag_preview").commitAllowingStateLoss();
        }
        this.mPreviewFragment.setListener(this.mListener);
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected Intent getFinishIntent() {
        return new Intent(this, FaceEnrollFinish.class);
    }

    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected BiometricEnrollSidecar getSidecar() {
        int[] iArr = new int[this.mDisabledFeatures.size()];
        for (int i = 0; i < this.mDisabledFeatures.size(); i++) {
            iArr[i] = this.mDisabledFeatures.get(i).intValue();
        }
        return new FaceEnrollSidecar(iArr);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            showError(charSequence);
        }
        this.mPreviewFragment.onEnrollmentHelp(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        int i2 = i != 3 ? R.string.security_settings_face_enroll_error_generic_dialog_message : R.string.security_settings_face_enroll_error_timeout_dialog_message;
        this.mPreviewFragment.onEnrollmentError(i, charSequence);
        showErrorDialog(getText(i2), i);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        this.mPreviewFragment.onEnrollmentProgressChange(i, i2);
        showError("Steps: " + i + " Remaining: " + i2);
        if (i2 == 0) {
            getApplicationContext().getContentResolver().notifyChange(CustomSliceRegistry.FACE_ENROLL_SLICE_URI, null);
            launchFinish(this.mToken);
        }
    }

    private void showErrorDialog(CharSequence charSequence, int i) {
        FaceErrorDialog.newInstance(charSequence, i).show(getSupportFragmentManager(), FaceErrorDialog.class.getName());
    }

    private void showError(CharSequence charSequence) {
        this.mErrorText.setText(charSequence);
        if (this.mErrorText.getVisibility() == 4) {
            this.mErrorText.setVisibility(0);
            this.mErrorText.setTranslationY(getResources().getDimensionPixelSize(R.dimen.fingerprint_error_text_appear_distance));
            this.mErrorText.setAlpha(0.0f);
            this.mErrorText.animate().alpha(1.0f).translationY(0.0f).setDuration(200L).setInterpolator(this.mLinearOutSlowInInterpolator).start();
            return;
        }
        this.mErrorText.animate().cancel();
        this.mErrorText.setAlpha(1.0f);
        this.mErrorText.setTranslationY(0.0f);
    }
}
