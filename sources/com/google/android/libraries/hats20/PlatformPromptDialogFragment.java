package com.google.android.libraries.hats20;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.libraries.hats20.PromptDialogDelegate;
import com.google.android.libraries.hats20.answer.AnswerBeacon;
import com.google.android.libraries.hats20.model.SurveyController;
/* loaded from: classes.dex */
public final class PlatformPromptDialogFragment extends DialogFragment implements PromptDialogDelegate.DialogFragmentInterface {
    private final PromptDialogDelegate delegate = new PromptDialogDelegate(this);

    public static PlatformPromptDialogFragment newInstance(String str, SurveyController surveyController, AnswerBeacon answerBeacon, Integer num, Integer num2, boolean z) {
        PlatformPromptDialogFragment platformPromptDialogFragment = new PlatformPromptDialogFragment();
        platformPromptDialogFragment.setArguments(PromptDialogDelegate.createArgs(str, surveyController, answerBeacon, num, num2, z));
        return platformPromptDialogFragment;
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.delegate.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onStart() {
        super.onStart();
        this.delegate.onStart();
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.delegate.onResume();
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        this.delegate.onPause();
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        this.delegate.onDestroy();
        super.onDestroy();
    }
}
