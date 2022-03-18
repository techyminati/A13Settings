package com.google.android.libraries.hats20.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class QuestionOpenText extends Question {
    public static final Parcelable.Creator<QuestionOpenText> CREATOR = new Parcelable.Creator<QuestionOpenText>() { // from class: com.google.android.libraries.hats20.model.QuestionOpenText.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public QuestionOpenText createFromParcel(Parcel parcel) {
            return new QuestionOpenText(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public QuestionOpenText[] newArray(int i) {
            return new QuestionOpenText[i];
        }
    };
    private boolean singleLine;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // com.google.android.libraries.hats20.model.Question
    public int getType() {
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QuestionOpenText(JSONObject jSONObject) throws JSONException {
        this.questionText = jSONObject.optString("question");
        this.singleLine = jSONObject.optBoolean("single_line");
    }

    public boolean isSingleLine() {
        return this.singleLine;
    }

    public String toString() {
        String str = this.questionText;
        boolean z = this.singleLine;
        StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 49);
        sb.append("QuestionOpenText{questionText=");
        sb.append(str);
        sb.append(", singleLine=");
        sb.append(z);
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.singleLine ? (byte) 1 : (byte) 0);
        parcel.writeString(this.questionText);
    }

    private QuestionOpenText(Parcel parcel) {
        this.singleLine = parcel.readByte() != 0;
        this.questionText = parcel.readString();
    }
}
