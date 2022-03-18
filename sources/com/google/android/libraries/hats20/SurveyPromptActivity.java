package com.google.android.libraries.hats20;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.libraries.hats20.adapter.SurveyViewPagerAdapter;
import com.google.android.libraries.hats20.answer.AnswerBeacon;
import com.google.android.libraries.hats20.answer.AnswerBeaconTransmitter;
import com.google.android.libraries.hats20.answer.QuestionResponse;
import com.google.android.libraries.hats20.model.Question;
import com.google.android.libraries.hats20.model.SurveyController;
import com.google.android.libraries.hats20.storage.HatsDataStore;
import com.google.android.libraries.hats20.util.LayoutDimensions;
import com.google.android.libraries.hats20.util.LayoutUtils;
import com.google.android.libraries.hats20.view.FragmentViewDelegate;
import com.google.android.libraries.hats20.view.OnQuestionProgressableChangeListener;
import com.google.android.libraries.hats20.view.OpenTextFragment;
import com.google.android.libraries.hats20.view.SurveyViewPager;
/* loaded from: classes.dex */
public class SurveyPromptActivity extends AppCompatActivity implements FragmentViewDelegate.MeasurementSurrogate, OnQuestionProgressableChangeListener {
    private AnswerBeacon answerBeacon;
    private AnswerBeaconTransmitter answerBeaconTransmitter;
    private IdleResourceManager idleResourceManager;
    private boolean isFullWidth;
    private boolean isSubmitting;
    private LayoutDimensions layoutDimensions;
    private FrameLayout overallContainer;
    private String siteId;
    private LinearLayout surveyContainer;
    private SurveyController surveyController;
    private SurveyViewPager surveyViewPager;
    private SurveyViewPagerAdapter surveyViewPagerAdapter;
    private TextView thankYouTextView;
    private final Point surveyPreDrawMeasurements = new Point(0, 0);
    private int itemMeasureCount = 0;
    private final Handler activityFinishHandler = new Handler();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void startSurveyActivity(Activity activity, String str, SurveyController surveyController, AnswerBeacon answerBeacon, Integer num, boolean z) {
        Intent intent = new Intent(activity, SurveyPromptActivity.class);
        intent.putExtra("SiteId", str);
        intent.putExtra("SurveyController", surveyController);
        intent.putExtra("AnswerBeacon", answerBeacon);
        intent.putExtra("IsFullWidth", z);
        Log.d("HatsLibSurveyActivity", String.format("Starting survey for client activity: %s", activity.getClass().getCanonicalName()));
        if (num == null) {
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, num.intValue());
        }
    }

    public IdleResourceManager getIdleResourceManager() {
        return this.idleResourceManager;
    }

    public void setIsMultipleChoiceSelectionAnimating(boolean z) {
        this.idleResourceManager.setIsMultipleChoiceSelectionAnimating(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        AnswerBeacon answerBeacon;
        super.onCreate(bundle);
        setTitle("");
        this.layoutDimensions = new LayoutDimensions(getResources());
        this.siteId = getIntent().getStringExtra("SiteId");
        this.surveyController = (SurveyController) getIntent().getParcelableExtra("SurveyController");
        if (bundle == null) {
            answerBeacon = (AnswerBeacon) getIntent().getParcelableExtra("AnswerBeacon");
        } else {
            answerBeacon = (AnswerBeacon) bundle.getParcelable("AnswerBeacon");
        }
        this.answerBeacon = answerBeacon;
        this.isSubmitting = bundle == null ? false : bundle.getBoolean("IsSubmitting");
        this.isFullWidth = getIntent().getBooleanExtra("IsFullWidth", false);
        if (this.siteId == null || this.surveyController == null || this.answerBeacon == null) {
            Log.e("HatsLibSurveyActivity", "Required EXTRAS not found in the intent, bailing out.");
            finish();
            return;
        }
        HatsClient.markSurveyRunning();
        Object[] objArr = new Object[2];
        objArr[0] = bundle != null ? "created with savedInstanceState" : "created anew";
        objArr[1] = this.siteId;
        Log.d("HatsLibSurveyActivity", String.format("Activity %s with site ID: %s", objArr));
        this.answerBeaconTransmitter = new AnswerBeaconTransmitter(this.surveyController.getAnswerUrl(), HatsDataStore.buildFromContext(this));
        setContentView(R$layout.hats_container);
        this.surveyContainer = (LinearLayout) findViewById(R$id.hats_lib_survey_container);
        this.overallContainer = (FrameLayout) findViewById(R$id.hats_lib_overall_container);
        wireUpCloseButton();
        TextView textView = (TextView) this.overallContainer.findViewById(R$id.hats_lib_thank_you);
        this.thankYouTextView = textView;
        textView.setText(this.surveyController.getThankYouMessage());
        this.thankYouTextView.setContentDescription(this.surveyController.getThankYouMessage());
        if (this.surveyController.shouldIncludeSurveyControls()) {
            getLayoutInflater().inflate(R$layout.hats_survey_controls, this.surveyContainer);
        }
        setUpSurveyPager(this.surveyController.getQuestions(), bundle);
        signalSurveyBegun();
        if (this.surveyController.shouldIncludeSurveyControls()) {
            wireUpSurveyControls();
        }
        this.idleResourceManager = new IdleResourceManager();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPostResume() {
        super.onPostResume();
        if (this.isSubmitting) {
            finish();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            HatsClient.markSurveyFinished();
        }
        this.activityFinishHandler.removeCallbacks(null);
    }

    private void configureSurveyWindowParameters() {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        Point point = new Point(0, 0);
        getWindowManager().getDefaultDisplay().getSize(point);
        attributes.gravity = 85;
        attributes.width = getFinalizedSurveyDimensions().x;
        attributes.height = point.y;
        if (LayoutUtils.isNavigationBarOnRight(this)) {
            attributes.x = LayoutUtils.getNavigationBarDimensionPixelSize(this).x;
        } else {
            attributes.y = LayoutUtils.getNavigationBarDimensionPixelSize(this).y;
        }
        if (this.layoutDimensions.shouldSurveyDisplayScrim()) {
            showWindowScrim();
        }
        window.setAttributes(attributes);
    }

    private void showWindowScrim() {
        Window window = getWindow();
        window.addFlags(2);
        window.clearFlags(32);
        window.addFlags(262144);
        window.setDimAmount(0.4f);
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            Rect rect = new Rect();
            this.overallContainer.getGlobalVisibleRect(rect);
            if (!rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                Log.d("HatsLibSurveyActivity", "User clicked outside of survey root container. Closing.");
                if (!this.answerBeacon.hasBeaconTypeFullAnswer()) {
                    setBeaconTypeAndTransmit("o");
                }
                finish();
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private void wireUpSurveyControls() {
        ((Button) findViewById(R$id.hats_lib_next)).setOnClickListener(new View.OnClickListener() { // from class: com.google.android.libraries.hats20.SurveyPromptActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SurveyPromptActivity.this.nextPageOrSubmit();
            }
        });
    }

    private void wireUpCloseButton() {
        findViewById(R$id.hats_lib_close_button).setOnClickListener(new View.OnClickListener() { // from class: com.google.android.libraries.hats20.SurveyPromptActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SurveyPromptActivity.this.setBeaconTypeAndTransmit("o");
                SurveyPromptActivity.this.finish();
            }
        });
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        setBeaconTypeAndTransmit("o");
        super.onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("CurrentQuestionIndex", this.surveyViewPager.getCurrentItem());
        bundle.putBoolean("IsSubmitting", this.isSubmitting);
        bundle.putParcelable("AnswerBeacon", this.answerBeacon);
    }

    private void setUpSurveyPager(Question[] questionArr, Bundle bundle) {
        this.surveyViewPagerAdapter = new SurveyViewPagerAdapter(getSupportFragmentManager(), questionArr);
        SurveyViewPager surveyViewPager = (SurveyViewPager) findViewById(R$id.hats_lib_survey_viewpager);
        this.surveyViewPager = surveyViewPager;
        surveyViewPager.setAdapter(this.surveyViewPagerAdapter);
        this.surveyViewPager.setImportantForAccessibility(2);
        if (bundle != null) {
            this.surveyViewPager.setCurrentItem(bundle.getInt("CurrentQuestionIndex"));
        }
        if (this.surveyController.shouldIncludeSurveyControls()) {
            switchNextTextToSubmitIfNeeded();
        }
    }

    @Override // com.google.android.libraries.hats20.view.FragmentViewDelegate.MeasurementSurrogate
    public Point getMeasureSpecs() {
        Point usableContentDimensions = LayoutUtils.getUsableContentDimensions(this);
        usableContentDimensions.y = (int) Math.min(usableContentDimensions.y * 0.8f, this.layoutDimensions.getSurveyMaxHeight());
        usableContentDimensions.x = Math.min(usableContentDimensions.x, this.layoutDimensions.getSurveyMaxWidth());
        return new Point(View.MeasureSpec.makeMeasureSpec(usableContentDimensions.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(usableContentDimensions.y, Integer.MIN_VALUE));
    }

    @Override // com.google.android.libraries.hats20.view.FragmentViewDelegate.MeasurementSurrogate
    public void onFragmentContentMeasurement(int i, int i2) {
        this.itemMeasureCount++;
        Point point = this.surveyPreDrawMeasurements;
        point.x = Math.max(point.x, i);
        Point point2 = this.surveyPreDrawMeasurements;
        point2.y = Math.max(point2.y, i2);
        if (this.itemMeasureCount == this.surveyViewPagerAdapter.getCount()) {
            this.itemMeasureCount = 0;
            FrameLayout frameLayout = (FrameLayout) findViewById(R$id.hats_lib_survey_controls_container);
            if (frameLayout != null) {
                this.surveyPreDrawMeasurements.y += frameLayout.getMeasuredHeight();
            }
            transitionToSurveyMode();
        }
    }

    private void transitionToSurveyMode() {
        this.surveyViewPager.fireOnPageScrolledIntoViewListener();
        if (!this.answerBeacon.hasBeaconType()) {
            setBeaconTypeAndTransmit("sv");
        }
        configureSurveyWindowParameters();
        this.surveyContainer.setAlpha(1.0f);
        updateSurveyLayoutParameters();
        updateSurveyFullBleed();
        if (this.layoutDimensions.shouldSurveyDisplayCloseButton()) {
            findViewById(R$id.hats_lib_close_button).setVisibility(0);
        }
        sendWindowStateChangeAccessibilityEvent();
    }

    private void sendWindowStateChangeAccessibilityEvent() {
        this.surveyViewPager.getCurrentItemFragment().getView().sendAccessibilityEvent(32);
    }

    private void updateSurveyLayoutParameters() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.overallContainer.getLayoutParams();
        Point finalizedSurveyDimensions = getFinalizedSurveyDimensions();
        layoutParams.width = finalizedSurveyDimensions.x;
        layoutParams.height = finalizedSurveyDimensions.y;
        this.overallContainer.setLayoutParams(layoutParams);
    }

    public void updateSurveyFullBleed() {
        if (this.layoutDimensions.isSurveyFullBleed()) {
            this.overallContainer.setPadding(0, 0, 0, 0);
            return;
        }
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.hats_lib_container_padding);
        this.overallContainer.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
    }

    private void signalSurveyBegun() {
        this.answerBeacon.setShown(this.surveyViewPager.getCurrentItem());
        this.surveyContainer.setVisibility(0);
        this.surveyContainer.forceLayout();
    }

    @Override // com.google.android.libraries.hats20.view.OnQuestionProgressableChangeListener
    public void onQuestionProgressableChanged(boolean z, Fragment fragment) {
        if (SurveyViewPagerAdapter.getQuestionIndex(fragment) == this.surveyViewPager.getCurrentItem()) {
            setNextButtonEnabled(z);
        }
    }

    private void setNextButtonEnabled(boolean z) {
        Button button = (Button) findViewById(R$id.hats_lib_next);
        if (button != null && button.isEnabled() != z) {
            button.setAlpha(z ? 1.0f : 0.3f);
            button.setEnabled(z);
        }
    }

    private void switchNextTextToSubmitIfNeeded() {
        Button button = (Button) findViewById(R$id.hats_lib_next);
        if (button != null && this.surveyViewPager.isLastQuestion()) {
            button.setText(R$string.hats_lib_submit);
        }
    }

    public void nextPageOrSubmit() {
        if (this.surveyViewPager.getCurrentItemFragment() instanceof OpenTextFragment) {
            ((OpenTextFragment) this.surveyViewPager.getCurrentItemFragment()).closeKeyboard();
        }
        addCurrentItemResponseToAnswerBeacon();
        if (this.surveyViewPager.isLastQuestion()) {
            Log.d("HatsLibSurveyActivity", "Survey completed, submitting.");
            setBeaconTypeAndTransmit("a");
            submit();
            return;
        }
        setBeaconTypeAndTransmit("pa");
        this.surveyViewPager.navigateToNextPage();
        this.answerBeacon.setShown(this.surveyViewPager.getCurrentItem());
        switchNextTextToSubmitIfNeeded();
        sendWindowStateChangeAccessibilityEvent();
        Log.d("HatsLibSurveyActivity", String.format("Showing question: %d", Integer.valueOf(this.surveyViewPager.getCurrentItem() + 1)));
    }

    private void addCurrentItemResponseToAnswerBeacon() {
        QuestionResponse currentItemQuestionResponse = this.surveyViewPager.getCurrentItemQuestionResponse();
        if (currentItemQuestionResponse != null) {
            this.answerBeacon.setQuestionResponse(this.surveyViewPager.getCurrentItem(), currentItemQuestionResponse);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBeaconTypeAndTransmit(String str) {
        this.answerBeacon.setBeaconType(str);
        this.answerBeaconTransmitter.transmit(this.answerBeacon);
    }

    private void submit() {
        this.isSubmitting = true;
        this.idleResourceManager.setIsThankYouAnimating(true);
        findViewById(R$id.hats_lib_close_button).setVisibility(8);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator duration = ObjectAnimator.ofFloat(this.surveyContainer, "alpha", 0.0f).setDuration(350L);
        duration.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.libraries.hats20.SurveyPromptActivity.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SurveyPromptActivity.this.surveyContainer.setVisibility(8);
            }
        });
        ValueAnimator duration2 = ValueAnimator.ofInt(this.overallContainer.getHeight(), getResources().getDimensionPixelSize(R$dimen.hats_lib_thank_you_height)).setDuration(350L);
        duration2.setStartDelay(350L);
        duration2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.libraries.hats20.SurveyPromptActivity.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SurveyPromptActivity.this.overallContainer.getLayoutParams().height = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                SurveyPromptActivity.this.overallContainer.requestLayout();
            }
        });
        ObjectAnimator duration3 = ObjectAnimator.ofFloat(this.thankYouTextView, "alpha", 1.0f).setDuration(350L);
        duration3.setStartDelay(700L);
        this.thankYouTextView.setVisibility(0);
        TextView textView = this.thankYouTextView;
        textView.announceForAccessibility(textView.getContentDescription());
        this.activityFinishHandler.postDelayed(new Runnable() { // from class: com.google.android.libraries.hats20.SurveyPromptActivity.5
            @Override // java.lang.Runnable
            public void run() {
                SurveyPromptActivity.this.idleResourceManager.setIsThankYouAnimating(false);
                SurveyPromptActivity.this.finish();
            }
        }, 2400L);
        animatorSet.playTogether(duration, duration2, duration3);
        animatorSet.start();
    }

    public ViewGroup getSurveyContainer() {
        return this.surveyContainer;
    }

    public Point getFinalizedSurveyDimensions() {
        int i = LayoutUtils.getUsableContentDimensions(this).x;
        if (!this.isFullWidth) {
            i = Math.min(i, this.layoutDimensions.getSurveyMaxWidth());
        }
        return new Point(i, Math.min(this.layoutDimensions.getSurveyMaxHeight(), this.surveyPreDrawMeasurements.y));
    }
}
