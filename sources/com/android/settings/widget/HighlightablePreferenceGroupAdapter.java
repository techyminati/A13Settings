package com.android.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.material.appbar.AppBarLayout;
/* loaded from: classes.dex */
public class HighlightablePreferenceGroupAdapter extends PreferenceGroupAdapter {
    static final long DELAY_COLLAPSE_DURATION_MILLIS = 300;
    static final long DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
    boolean mFadeInAnimated;
    final int mHighlightColor;
    private final String mHighlightKey;
    private int mHighlightPosition = -1;
    private boolean mHighlightRequested;
    private final int mNormalBackgroundRes;

    public static void adjustInitialExpandedChildCount(SettingsPreferenceFragment settingsPreferenceFragment) {
        PreferenceScreen preferenceScreen;
        if (settingsPreferenceFragment != null && (preferenceScreen = settingsPreferenceFragment.getPreferenceScreen()) != null) {
            Bundle arguments = settingsPreferenceFragment.getArguments();
            if (arguments == null || TextUtils.isEmpty(arguments.getString(":settings:fragment_args_key"))) {
                int initialExpandedChildCount = settingsPreferenceFragment.getInitialExpandedChildCount();
                if (initialExpandedChildCount > 0) {
                    preferenceScreen.setInitialExpandedChildrenCount(initialExpandedChildCount);
                    return;
                }
                return;
            }
            preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
        }
    }

    public HighlightablePreferenceGroupAdapter(PreferenceGroup preferenceGroup, String str, boolean z) {
        super(preferenceGroup);
        this.mHighlightKey = str;
        this.mHighlightRequested = z;
        Context context = preferenceGroup.getContext();
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843534, typedValue, true);
        this.mNormalBackgroundRes = typedValue.resourceId;
        this.mHighlightColor = context.getColor(R.color.preference_highlight_color);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.PreferenceGroupAdapter
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, int i) {
        super.onBindViewHolder(preferenceViewHolder, i);
        updateBackground(preferenceViewHolder, i);
    }

    void updateBackground(PreferenceViewHolder preferenceViewHolder, int i) {
        String str;
        View view = preferenceViewHolder.itemView;
        if (i == this.mHighlightPosition && (str = this.mHighlightKey) != null && TextUtils.equals(str, getItem(i).getKey())) {
            addHighlightBackground(preferenceViewHolder, !this.mFadeInAnimated);
        } else if (Boolean.TRUE.equals(view.getTag(R.id.preference_highlighted))) {
            removeHighlightBackground(preferenceViewHolder, false);
        }
    }

    public void requestHighlight(View view, final RecyclerView recyclerView, final AppBarLayout appBarLayout) {
        final int preferenceAdapterPosition;
        if (!this.mHighlightRequested && recyclerView != null && !TextUtils.isEmpty(this.mHighlightKey) && (preferenceAdapterPosition = getPreferenceAdapterPosition(this.mHighlightKey)) >= 0) {
            if (appBarLayout != null) {
                view.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppBarLayout.this.setExpanded(false, true);
                    }
                }, DELAY_COLLAPSE_DURATION_MILLIS);
            }
            view.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    HighlightablePreferenceGroupAdapter.this.lambda$requestHighlight$1(recyclerView, preferenceAdapterPosition);
                }
            }, DELAY_HIGHLIGHT_DURATION_MILLIS);
            view.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    HighlightablePreferenceGroupAdapter.this.lambda$requestHighlight$2(preferenceAdapterPosition);
                }
            }, 900L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHighlight$1(RecyclerView recyclerView, int i) {
        this.mHighlightRequested = true;
        recyclerView.setItemAnimator(null);
        recyclerView.smoothScrollToPosition(i);
        this.mHighlightPosition = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHighlight$2(int i) {
        notifyItemChanged(i);
    }

    public boolean isHighlightRequested() {
        return this.mHighlightRequested;
    }

    void requestRemoveHighlightDelayed(final PreferenceViewHolder preferenceViewHolder) {
        preferenceViewHolder.itemView.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                HighlightablePreferenceGroupAdapter.this.lambda$requestRemoveHighlightDelayed$3(preferenceViewHolder);
            }
        }, 15000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestRemoveHighlightDelayed$3(PreferenceViewHolder preferenceViewHolder) {
        this.mHighlightPosition = -1;
        removeHighlightBackground(preferenceViewHolder, true);
    }

    private void addHighlightBackground(PreferenceViewHolder preferenceViewHolder, boolean z) {
        final View view = preferenceViewHolder.itemView;
        view.setTag(R.id.preference_highlighted, Boolean.TRUE);
        if (!z) {
            view.setBackgroundColor(this.mHighlightColor);
            Log.d("HighlightableAdapter", "AddHighlight: Not animation requested - setting highlight background");
            requestRemoveHighlightDelayed(preferenceViewHolder);
            return;
        }
        this.mFadeInAnimated = true;
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(this.mNormalBackgroundRes), Integer.valueOf(this.mHighlightColor));
        ofObject.setDuration(200L);
        ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                HighlightablePreferenceGroupAdapter.lambda$addHighlightBackground$4(view, valueAnimator);
            }
        });
        ofObject.setRepeatMode(2);
        ofObject.setRepeatCount(4);
        ofObject.start();
        Log.d("HighlightableAdapter", "AddHighlight: starting fade in animation");
        preferenceViewHolder.setIsRecyclable(false);
        requestRemoveHighlightDelayed(preferenceViewHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$addHighlightBackground$4(View view, ValueAnimator valueAnimator) {
        view.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    private void removeHighlightBackground(final PreferenceViewHolder preferenceViewHolder, boolean z) {
        final View view = preferenceViewHolder.itemView;
        if (!z) {
            view.setTag(R.id.preference_highlighted, Boolean.FALSE);
            view.setBackgroundResource(this.mNormalBackgroundRes);
            Log.d("HighlightableAdapter", "RemoveHighlight: No animation requested - setting normal background");
        } else if (!Boolean.TRUE.equals(view.getTag(R.id.preference_highlighted))) {
            Log.d("HighlightableAdapter", "RemoveHighlight: Not highlighted - skipping");
        } else {
            int i = this.mHighlightColor;
            int i2 = this.mNormalBackgroundRes;
            view.setTag(R.id.preference_highlighted, Boolean.FALSE);
            ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(i), Integer.valueOf(i2));
            ofObject.setDuration(500L);
            ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    HighlightablePreferenceGroupAdapter.lambda$removeHighlightBackground$5(view, valueAnimator);
                }
            });
            ofObject.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    view.setBackgroundResource(HighlightablePreferenceGroupAdapter.this.mNormalBackgroundRes);
                    preferenceViewHolder.setIsRecyclable(true);
                }
            });
            ofObject.start();
            Log.d("HighlightableAdapter", "Starting fade out animation");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$removeHighlightBackground$5(View view, ValueAnimator valueAnimator) {
        view.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
