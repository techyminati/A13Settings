package com.google.android.libraries.hats20.view;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.libraries.hats20.R$dimen;
import com.google.android.libraries.hats20.R$id;
import com.google.android.libraries.hats20.R$layout;
import com.google.android.libraries.hats20.SurveyPromptActivity;
import com.google.android.libraries.hats20.answer.QuestionResponse;
import com.google.android.libraries.hats20.model.QuestionRating;
import com.google.android.libraries.hats20.ui.StarRatingBar;
import com.google.android.libraries.hats20.util.LayoutUtils;
import com.google.android.libraries.hats20.util.TextFormatUtil;
import com.google.android.libraries.hats20.view.FragmentViewDelegate;
import com.google.android.libraries.material.autoresizetext.AutoResizeTextView;
/* loaded from: classes.dex */
public class RatingFragment extends BaseFragment {
    private FragmentViewDelegate fragmentViewDelegate = new FragmentViewDelegate();
    private QuestionRating question;
    private QuestionMetrics questionMetrics;
    private String selectedResponse;

    public static RatingFragment newInstance(QuestionRating questionRating) {
        RatingFragment ratingFragment = new RatingFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Question", questionRating);
        ratingFragment.setArguments(bundle);
        return ratingFragment;
    }

    public void updateRatingQuestionTextSize(AutoResizeTextView autoResizeTextView) {
        Resources resources = getResources();
        int size = View.MeasureSpec.getSize(((FragmentViewDelegate.MeasurementSurrogate) getActivity()).getMeasureSpecs().x);
        LayoutUtils.fitTextInTextViewWrapIfNeeded(size - (((resources.getDimensionPixelSize(R$dimen.hats_lib_rating_container_padding) * 2) + TypedValue.applyDimension(1, 24.0f, resources.getDisplayMetrics())) + TypedValue.applyDimension(1, 40.0f, resources.getDisplayMetrics())), 20, 16, this.question.getQuestionText(), autoResizeTextView);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.question = (QuestionRating) getArguments().getParcelable("Question");
        if (bundle != null) {
            this.selectedResponse = bundle.getString("SelectedResponse", null);
            this.questionMetrics = (QuestionMetrics) bundle.getParcelable("QuestionMetrics");
        }
        if (this.questionMetrics == null) {
            this.questionMetrics = new QuestionMetrics();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("SelectedResponse", this.selectedResponse);
        bundle.putParcelable("QuestionMetrics", this.questionMetrics);
    }

    @Override // com.google.android.libraries.hats20.view.BaseFragment
    public void onPageScrolledIntoView() {
        this.questionMetrics.markAsShown();
        ((OnQuestionProgressableChangeListener) getActivity()).onQuestionProgressableChanged(isResponseSatisfactory(), this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        this.fragmentViewDelegate.cleanUp();
        super.onDetach();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R$layout.hats_survey_question_rating, viewGroup, false);
        inflate.setContentDescription(this.question.getQuestionText());
        TextView textView = (TextView) inflate.findViewById(R$id.hats_lib_survey_question_text);
        textView.setText(TextFormatUtil.format(this.question.getQuestionText()));
        textView.setContentDescription(this.question.getQuestionText());
        setTextAndContentDescription((TextView) inflate.findViewById(R$id.hats_lib_survey_rating_low_value_text), this.question.getLowValueText());
        setTextAndContentDescription((TextView) inflate.findViewById(R$id.hats_lib_survey_rating_high_value_text), this.question.getHighValueText());
        final ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R$id.hats_lib_survey_rating_images_container);
        final StarRatingBar starRatingBar = (StarRatingBar) inflate.findViewById(R$id.hats_lib_star_rating_bar);
        int i = AnonymousClass3.$SwitchMap$com$google$android$libraries$hats20$model$QuestionRating$Sprite[this.question.getSprite().ordinal()];
        if (i == 1) {
            viewGroup2.setVisibility(0);
            int i2 = 0;
            while (i2 < 5) {
                View inflate2 = layoutInflater.inflate(R$layout.hats_survey_question_rating_item, viewGroup2, false);
                ((ImageView) inflate2.findViewById(R$id.hats_lib_survey_rating_icon)).setImageResource(QuestionRating.READONLY_SURVEY_RATING_ICON_RESOURCE_MAP.get(Integer.valueOf(i2)).intValue());
                final int i3 = i2 + 1;
                inflate2.setTag(Integer.valueOf(i3));
                setDescriptionForTalkBack(inflate2, i3, 5);
                inflate2.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.libraries.hats20.view.RatingFragment.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        RatingFragment.this.removeOnClickListenersAndDisableClickEvents(viewGroup2);
                        int i4 = i3;
                        StringBuilder sb = new StringBuilder(35);
                        sb.append("Rating selected, value: ");
                        sb.append(i4);
                        Log.d("HatsLibRatingFragment", sb.toString());
                        RatingFragment.this.questionMetrics.markAsAnswered();
                        RatingFragment.this.selectedResponse = Integer.toString(i3);
                        ((SurveyPromptActivity) RatingFragment.this.getActivity()).nextPageOrSubmit();
                    }
                });
                removeMarginIfNeeded(inflate2, i2, 5);
                viewGroup2.addView(inflate2);
                i2 = i3;
            }
        } else if (i == 2) {
            starRatingBar.setVisibility(0);
            starRatingBar.setNumStars(this.question.getNumIcons());
            starRatingBar.setOnRatingChangeListener(new StarRatingBar.OnRatingChangeListener() { // from class: com.google.android.libraries.hats20.view.RatingFragment.2
                @Override // com.google.android.libraries.hats20.ui.StarRatingBar.OnRatingChangeListener
                public void onRatingChanged(int i4) {
                    RatingFragment ratingFragment = RatingFragment.this;
                    ratingFragment.setDescriptionForTalkBack(starRatingBar, i4, ratingFragment.question.getNumIcons());
                    RatingFragment.this.questionMetrics.markAsAnswered();
                    RatingFragment.this.selectedResponse = Integer.toString(i4);
                    ((OnQuestionProgressableChangeListener) RatingFragment.this.getActivity()).onQuestionProgressableChanged(RatingFragment.this.isResponseSatisfactory(), RatingFragment.this);
                }
            });
        } else {
            String valueOf = String.valueOf(this.question.getSprite());
            StringBuilder sb = new StringBuilder(valueOf.length() + 15);
            sb.append("Unknown sprite ");
            sb.append(valueOf);
            throw new IllegalStateException(sb.toString());
        }
        updateRatingQuestionTextSize((AutoResizeTextView) inflate.findViewById(R$id.hats_lib_survey_question_text));
        if (!isDetached()) {
            this.fragmentViewDelegate.watch((FragmentViewDelegate.MeasurementSurrogate) getActivity(), inflate);
        }
        return inflate;
    }

    /* renamed from: com.google.android.libraries.hats20.view.RatingFragment$3  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$google$android$libraries$hats20$model$QuestionRating$Sprite;

        static {
            int[] iArr = new int[QuestionRating.Sprite.values().length];
            $SwitchMap$com$google$android$libraries$hats20$model$QuestionRating$Sprite = iArr;
            try {
                iArr[QuestionRating.Sprite.SMILEYS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$android$libraries$hats20$model$QuestionRating$Sprite[QuestionRating.Sprite.STARS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeOnClickListenersAndDisableClickEvents(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setOnClickListener(null);
            viewGroup.getChildAt(i).setClickable(false);
        }
    }

    private void setTextAndContentDescription(TextView textView, String str) {
        textView.setText(str);
        textView.setContentDescription(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDescriptionForTalkBack(View view, int i, int i2) {
        String format = String.format("%d of %d", Integer.valueOf(i), Integer.valueOf(i2));
        if (i == 1) {
            String valueOf = String.valueOf(format);
            String valueOf2 = String.valueOf(this.question.getLowValueText());
            StringBuilder sb = new StringBuilder(valueOf.length() + 1 + valueOf2.length());
            sb.append(valueOf);
            sb.append(" ");
            sb.append(valueOf2);
            format = sb.toString();
        } else if (i == i2) {
            String valueOf3 = String.valueOf(format);
            String valueOf4 = String.valueOf(this.question.getHighValueText());
            StringBuilder sb2 = new StringBuilder(valueOf3.length() + 1 + valueOf4.length());
            sb2.append(valueOf3);
            sb2.append(" ");
            sb2.append(valueOf4);
            format = sb2.toString();
        }
        view.setContentDescription(format);
    }

    private void removeMarginIfNeeded(View view, int i, int i2) {
        if (i == 0 || i == i2 - 1) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            if (i == 0) {
                layoutParams.setMargins(0, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
            } else if (i == i2 - 1) {
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, 0, layoutParams.bottomMargin);
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @Override // com.google.android.libraries.hats20.view.BaseFragment
    public QuestionResponse computeQuestionResponse() {
        QuestionResponse.Builder builder = QuestionResponse.builder();
        if (this.questionMetrics.isShown()) {
            builder.setDelayMs(this.questionMetrics.getDelayMs());
            String str = this.selectedResponse;
            if (str != null) {
                builder.addResponse(str);
                String valueOf = String.valueOf(this.selectedResponse);
                Log.d("HatsLibRatingFragment", valueOf.length() != 0 ? "Selected response: ".concat(valueOf) : new String("Selected response: "));
            }
        }
        return builder.build();
    }

    public boolean isResponseSatisfactory() {
        return this.selectedResponse != null;
    }
}
