package com.google.android.libraries.hats20.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.google.android.libraries.hats20.R$dimen;
import com.google.android.libraries.hats20.R$id;
import com.google.android.libraries.hats20.R$layout;
import com.google.android.libraries.hats20.SurveyPromptActivity;
import com.google.android.libraries.hats20.util.TextFormatUtil;
import com.google.android.libraries.hats20.view.ScrollViewWithSizeCallback;
/* loaded from: classes.dex */
public abstract class ScrollableAnswerFragment extends BaseFragment {
    private TextView questionTextView;
    private ScrollViewWithSizeCallback scrollView;
    private View scrollViewContents;
    private View surveyControlsContainer;
    private ScrollShadowHandler scrollShadowHandler = new ScrollShadowHandler();
    private boolean isOnScrollChangedListenerAttached = false;

    abstract View createScrollViewContents();

    abstract String getQuestionText();

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        stopRespondingToScrollChanges();
        super.onDetach();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.hats_survey_question_with_scrollable_content, viewGroup, false);
        TextView textView = (TextView) inflate.findViewById(R$id.hats_lib_survey_question_text);
        this.questionTextView = textView;
        textView.setText(TextFormatUtil.format(getQuestionText()));
        this.questionTextView.setContentDescription(getQuestionText());
        this.scrollViewContents = createScrollViewContents();
        ScrollViewWithSizeCallback scrollViewWithSizeCallback = (ScrollViewWithSizeCallback) inflate.findViewById(R$id.hats_survey_question_scroll_view);
        this.scrollView = scrollViewWithSizeCallback;
        scrollViewWithSizeCallback.addView(this.scrollViewContents);
        this.scrollView.setOnHeightChangedListener(this.scrollShadowHandler);
        startRespondingToScrollChanges();
        this.surveyControlsContainer = ((SurveyPromptActivity) viewGroup.getContext()).getSurveyContainer().findViewById(R$id.hats_lib_survey_controls_container);
        return inflate;
    }

    private void startRespondingToScrollChanges() {
        ScrollViewWithSizeCallback scrollViewWithSizeCallback;
        if (!this.isOnScrollChangedListenerAttached && (scrollViewWithSizeCallback = this.scrollView) != null) {
            scrollViewWithSizeCallback.getViewTreeObserver().addOnScrollChangedListener(this.scrollShadowHandler);
            this.isOnScrollChangedListenerAttached = true;
        }
    }

    private void stopRespondingToScrollChanges() {
        ScrollViewWithSizeCallback scrollViewWithSizeCallback;
        if (this.isOnScrollChangedListenerAttached && (scrollViewWithSizeCallback = this.scrollView) != null) {
            scrollViewWithSizeCallback.getViewTreeObserver().removeOnScrollChangedListener(this.scrollShadowHandler);
            this.isOnScrollChangedListenerAttached = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ScrollShadowHandler implements ViewTreeObserver.OnScrollChangedListener, ScrollViewWithSizeCallback.OnHeightChangedListener {
        private ScrollShadowHandler() {
        }

        @Override // android.view.ViewTreeObserver.OnScrollChangedListener
        public void onScrollChanged() {
            updateShadowVisibility(ScrollableAnswerFragment.this.scrollView.getHeight());
        }

        @Override // com.google.android.libraries.hats20.view.ScrollViewWithSizeCallback.OnHeightChangedListener
        public void onHeightChanged(int i) {
            if (i != 0) {
                updateShadowVisibility(i);
            }
        }

        private void updateShadowVisibility(int i) {
            if (ScrollableAnswerFragment.this.getUserVisibleHint()) {
                boolean z = true;
                boolean z2 = ScrollableAnswerFragment.this.scrollView.getScrollY() == 0;
                boolean z3 = ScrollableAnswerFragment.this.scrollViewContents.getBottom() == ScrollableAnswerFragment.this.scrollView.getScrollY() + i;
                if (ScrollableAnswerFragment.this.scrollViewContents.getBottom() <= i) {
                    z = false;
                }
                if (!z || z2) {
                    hideTopShadow();
                } else {
                    showTopShadow();
                }
                if (!z || z3) {
                    hideBottomShadow();
                } else {
                    showBottomShadow();
                }
            }
        }

        private void hideBottomShadow() {
            setElevation(ScrollableAnswerFragment.this.surveyControlsContainer, 0.0f);
        }

        private void showTopShadow() {
            setElevation(ScrollableAnswerFragment.this.questionTextView, ScrollableAnswerFragment.this.getResources().getDimensionPixelSize(R$dimen.hats_lib_question_view_elevation));
        }

        private void hideTopShadow() {
            setElevation(ScrollableAnswerFragment.this.questionTextView, 0.0f);
        }

        private void showBottomShadow() {
            setElevation(ScrollableAnswerFragment.this.surveyControlsContainer, ScrollableAnswerFragment.this.getResources().getDimensionPixelSize(R$dimen.hats_lib_survey_controls_view_elevation));
        }

        private void setElevation(View view, float f) {
            view.setElevation(f);
        }
    }
}
