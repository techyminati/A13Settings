package com.airbnb.lottie.utils;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes.dex */
public abstract class BaseLottieAnimator extends ValueAnimator {
    private final Set<ValueAnimator.AnimatorUpdateListener> updateListeners = new CopyOnWriteArraySet();
    private final Set<Animator.AnimatorListener> listeners = new CopyOnWriteArraySet();

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public long getStartDelay() {
        throw new UnsupportedOperationException("LottieAnimator does not support getStartDelay.");
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void setStartDelay(long j) {
        throw new UnsupportedOperationException("LottieAnimator does not support setStartDelay.");
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public ValueAnimator setDuration(long j) {
        throw new UnsupportedOperationException("LottieAnimator does not support setDuration.");
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void setInterpolator(TimeInterpolator timeInterpolator) {
        throw new UnsupportedOperationException("LottieAnimator does not support setInterpolator.");
    }

    @Override // android.animation.ValueAnimator
    public void addUpdateListener(ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        this.updateListeners.add(animatorUpdateListener);
    }

    @Override // android.animation.ValueAnimator
    public void removeUpdateListener(ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        this.updateListeners.remove(animatorUpdateListener);
    }

    @Override // android.animation.ValueAnimator
    public void removeAllUpdateListeners() {
        this.updateListeners.clear();
    }

    @Override // android.animation.Animator
    public void addListener(Animator.AnimatorListener animatorListener) {
        this.listeners.add(animatorListener);
    }

    @Override // android.animation.Animator
    public void removeListener(Animator.AnimatorListener animatorListener) {
        this.listeners.remove(animatorListener);
    }

    @Override // android.animation.Animator
    public void removeAllListeners() {
        this.listeners.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyStart(boolean z) {
        for (Animator.AnimatorListener animatorListener : this.listeners) {
            animatorListener.onAnimationStart(this, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyRepeat() {
        for (Animator.AnimatorListener animatorListener : this.listeners) {
            animatorListener.onAnimationRepeat(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyEnd(boolean z) {
        for (Animator.AnimatorListener animatorListener : this.listeners) {
            animatorListener.onAnimationEnd(this, z);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyCancel() {
        for (Animator.AnimatorListener animatorListener : this.listeners) {
            animatorListener.onAnimationCancel(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyUpdate() {
        for (ValueAnimator.AnimatorUpdateListener animatorUpdateListener : this.updateListeners) {
            animatorUpdateListener.onAnimationUpdate(this);
        }
    }
}
