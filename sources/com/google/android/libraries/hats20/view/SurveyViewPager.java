package com.google.android.libraries.hats20.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.libraries.hats20.R$dimen;
import com.google.android.libraries.hats20.SurveyPromptActivity;
import com.google.android.libraries.hats20.adapter.SurveyViewPagerAdapter;
import com.google.android.libraries.hats20.answer.QuestionResponse;
/* loaded from: classes.dex */
public class SurveyViewPager extends ViewPager {
    @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public SurveyViewPager(Context context) {
        super(context);
        setUpSurveyViewPager();
    }

    public SurveyViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setUpSurveyViewPager();
    }

    private void setUpSurveyViewPager() {
        setPageMargin(getResources().getDimensionPixelSize(R$dimen.hats_lib_survey_page_margin));
        setOffscreenPageLimit(Integer.MAX_VALUE);
        addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() { // from class: com.google.android.libraries.hats20.view.SurveyViewPager.1
            @Override // androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener, androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                SurveyViewPager.this.fireOnPageScrolledIntoViewListener();
            }
        });
    }

    public boolean isLastQuestion() {
        return getCurrentItem() == getAdapter().getCount() - 1;
    }

    public void navigateToNextPage() {
        setCurrentItem(getCurrentItem() + 1, true);
    }

    public void fireOnPageScrolledIntoViewListener() {
        getCurrentItemFragment().onPageScrolledIntoView();
    }

    public QuestionResponse getCurrentItemQuestionResponse() {
        if (getCurrentItemFragment() == null) {
            return null;
        }
        return getCurrentItemFragment().computeQuestionResponse();
    }

    public BaseFragment getCurrentItemFragment() {
        if (!(getContext() instanceof SurveyPromptActivity)) {
            Log.e("HatsLibSurveyViewPager", "Context is not a SurveyPromptActivity, something is very wrong.");
            return null;
        }
        int currentItem = getCurrentItem();
        for (Fragment fragment : ((SurveyPromptActivity) getContext()).getSupportFragmentManager().getFragments()) {
            if (SurveyViewPagerAdapter.getQuestionIndex(fragment) == currentItem && (fragment instanceof BaseFragment)) {
                return (BaseFragment) fragment;
            }
        }
        Log.e("HatsLibSurveyViewPager", "No Fragment found for the current item, something is very wrong.");
        return null;
    }
}
