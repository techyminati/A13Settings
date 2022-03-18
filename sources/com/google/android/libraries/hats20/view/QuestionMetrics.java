package com.google.android.libraries.hats20.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
/* loaded from: classes.dex */
public class QuestionMetrics implements Parcelable {
    public static final Parcelable.Creator<QuestionMetrics> CREATOR = new Parcelable.Creator<QuestionMetrics>() { // from class: com.google.android.libraries.hats20.view.QuestionMetrics.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public QuestionMetrics createFromParcel(Parcel parcel) {
            return new QuestionMetrics(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public QuestionMetrics[] newArray(int i) {
            return new QuestionMetrics[i];
        }
    };
    private long delayEndMs;
    private long delayStartMs;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QuestionMetrics() {
        this.delayStartMs = -1L;
        this.delayEndMs = -1L;
    }

    private QuestionMetrics(Parcel parcel) {
        this.delayStartMs = parcel.readLong();
        this.delayEndMs = parcel.readLong();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void markAsShown() {
        if (!isShown()) {
            this.delayStartMs = SystemClock.elapsedRealtime();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void markAsAnswered() {
        if (!isShown()) {
            Log.e("HatsLibQuestionMetrics", "Question was marked as answered but was never marked as shown.");
        } else if (isAnswered()) {
            Log.d("HatsLibQuestionMetrics", "Question was already marked as answered.");
        } else {
            this.delayEndMs = SystemClock.elapsedRealtime();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isShown() {
        return this.delayStartMs >= 0;
    }

    boolean isAnswered() {
        return this.delayEndMs >= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getDelayMs() {
        if (isAnswered()) {
            return this.delayEndMs - this.delayStartMs;
        }
        return -1L;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.delayStartMs);
        parcel.writeLong(this.delayEndMs);
    }
}
