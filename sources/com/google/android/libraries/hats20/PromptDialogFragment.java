package com.google.android.libraries.hats20;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.DialogFragment;
import com.google.android.libraries.hats20.PromptDialogDelegate;
import com.google.android.libraries.hats20.answer.AnswerBeacon;
import com.google.android.libraries.hats20.model.SurveyController;
/* loaded from: classes.dex */
public final class PromptDialogFragment extends DialogFragment implements PromptDialogDelegate.DialogFragmentInterface {
    private final PromptDialogDelegate delegate = new PromptDialogDelegate(this);

    @Override // androidx.fragment.app.Fragment, com.google.android.libraries.hats20.PromptDialogDelegate.DialogFragmentInterface
    public /* bridge */ /* synthetic */ Activity getActivity() {
        return super.getActivity();
    }

    public static PromptDialogFragment newInstance(String str, SurveyController surveyController, AnswerBeacon answerBeacon, Integer num, Integer num2, boolean z) {
        PromptDialogFragment promptDialogFragment = new PromptDialogFragment();
        promptDialogFragment.setArguments(PromptDialogDelegate.createArgs(str, surveyController, answerBeacon, num, num2, z));
        return promptDialogFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.delegate.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.delegate.onStart();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        this.delegate.onResume();
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.delegate.onPause();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        this.delegate.onDestroy();
        super.onDestroy();
    }
}
