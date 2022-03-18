package com.google.android.libraries.hats20.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.google.android.libraries.hats20.model.Question;
import com.google.android.libraries.hats20.model.QuestionRating;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class SurveyController implements Parcelable {
    public static final Parcelable.Creator<SurveyController> CREATOR = new Parcelable.Creator<SurveyController>() { // from class: com.google.android.libraries.hats20.model.SurveyController.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SurveyController createFromParcel(Parcel parcel) {
            return new SurveyController(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SurveyController[] newArray(int i) {
            return new SurveyController[i];
        }
    };
    private String answerUrl;
    private String promptMessage;
    private String promptParams;
    private Question[] questions;
    private boolean showInvitation;
    private String thankYouMessage;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private SurveyController() {
        this.showInvitation = true;
    }

    public Question[] getQuestions() {
        return this.questions;
    }

    public boolean showInvitation() {
        return this.showInvitation;
    }

    public boolean shouldIncludeSurveyControls() {
        Question[] questionArr = this.questions;
        return (questionArr.length == 1 && questionArr[0].getType() == 4 && ((QuestionRating) this.questions[0]).getSprite() == QuestionRating.Sprite.SMILEYS) ? false : true;
    }

    public String getPromptMessage() {
        return this.promptMessage;
    }

    public String getThankYouMessage() {
        return this.thankYouMessage;
    }

    public String getPromptParams() {
        return this.promptParams;
    }

    public String getAnswerUrl() {
        return this.answerUrl;
    }

    public static SurveyController initWithSurveyFromJson(String str, Resources resources) throws JSONException, MalformedSurveyException {
        JSONObject jSONObject = new JSONObject(str).getJSONObject("params");
        SurveyController surveyController = new SurveyController();
        retrieveTagDataFromJson(surveyController, jSONObject.getJSONArray("tags"), resources);
        surveyController.questions = Question.getQuestionsFromSurveyDefinition(jSONObject);
        surveyController.promptParams = jSONObject.optString("promptParams");
        surveyController.answerUrl = jSONObject.optString("answerUrl");
        assertSurveyIsValid(surveyController);
        return surveyController;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0070, code lost:
        if (r5.equals("thankYouMessage") == false) goto L_0x003b;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void retrieveTagDataFromJson(com.google.android.libraries.hats20.model.SurveyController r9, org.json.JSONArray r10, android.content.res.Resources r11) throws org.json.JSONException {
        /*
            Method dump skipped, instructions count: 286
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.libraries.hats20.model.SurveyController.retrieveTagDataFromJson(com.google.android.libraries.hats20.model.SurveyController, org.json.JSONArray, android.content.res.Resources):void");
    }

    private static void assertSurveyIsValid(SurveyController surveyController) throws MalformedSurveyException {
        if (surveyController.getQuestions().length == 0) {
            throw new MalformedSurveyException("Survey has no questions.");
        } else if (TextUtils.isEmpty(surveyController.getAnswerUrl())) {
            throw new MalformedSurveyException("Survey did not have an AnswerUrl, this is a GCS issue.");
        } else if (!TextUtils.isEmpty(surveyController.getPromptParams())) {
            for (int i = 0; i < surveyController.getQuestions().length; i++) {
                Question question = surveyController.questions[i];
                if (!TextUtils.isEmpty(question.questionText)) {
                    if (question instanceof Question.QuestionWithSelectableAnswers) {
                        Question.QuestionWithSelectableAnswers questionWithSelectableAnswers = (Question.QuestionWithSelectableAnswers) question;
                        List<String> answers = questionWithSelectableAnswers.getAnswers();
                        List<Integer> ordering = questionWithSelectableAnswers.getOrdering();
                        if (answers.isEmpty()) {
                            StringBuilder sb = new StringBuilder(42);
                            sb.append("Question #");
                            sb.append(i + 1);
                            sb.append(" was missing answers.");
                            throw new MalformedSurveyException(sb.toString());
                        } else if (ordering.isEmpty()) {
                            StringBuilder sb2 = new StringBuilder(74);
                            sb2.append("Question #");
                            sb2.append(i + 1);
                            sb2.append(" was missing an ordering, this likely is a GCS issue.");
                            throw new MalformedSurveyException(sb2.toString());
                        }
                    }
                    if (question.getType() == 4) {
                        QuestionRating questionRating = (QuestionRating) question;
                        if (TextUtils.isEmpty(questionRating.getLowValueText()) || TextUtils.isEmpty(questionRating.getHighValueText())) {
                            throw new MalformedSurveyException("A rating question was missing its high/low text.");
                        }
                        QuestionRating.Sprite sprite = questionRating.getSprite();
                        QuestionRating.Sprite sprite2 = QuestionRating.Sprite.SMILEYS;
                        if (sprite != sprite2 || questionRating.getNumIcons() == 5) {
                            QuestionRating.Sprite sprite3 = questionRating.getSprite();
                            if (!(sprite3 == QuestionRating.Sprite.STARS || sprite3 == sprite2)) {
                                String valueOf = String.valueOf(sprite3);
                                StringBuilder sb3 = new StringBuilder(valueOf.length() + 40);
                                sb3.append("Rating question has unsupported sprite: ");
                                sb3.append(valueOf);
                                throw new MalformedSurveyException(sb3.toString());
                            }
                        } else {
                            throw new MalformedSurveyException("Smiley surveys must have 5 options.");
                        }
                    }
                } else {
                    StringBuilder sb4 = new StringBuilder(43);
                    sb4.append("Question #");
                    sb4.append(i + 1);
                    sb4.append(" had no question text.");
                    throw new MalformedSurveyException(sb4.toString());
                }
            }
        } else {
            throw new MalformedSurveyException("Survey did not have prompt params, this is a GCS issue.");
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.showInvitation ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.questions.length);
        for (Question question : this.questions) {
            parcel.writeParcelable(question, i);
        }
        parcel.writeString(this.promptMessage);
        parcel.writeString(this.thankYouMessage);
        parcel.writeString(this.promptParams);
        parcel.writeString(this.answerUrl);
    }

    private SurveyController(Parcel parcel) {
        boolean z = true;
        this.showInvitation = true;
        this.showInvitation = parcel.readByte() == 0 ? false : z;
        int readInt = parcel.readInt();
        this.questions = new Question[readInt];
        for (int i = 0; i < readInt; i++) {
            this.questions[i] = (Question) parcel.readParcelable(Question.class.getClassLoader());
        }
        this.promptMessage = parcel.readString();
        this.thankYouMessage = parcel.readString();
        this.promptParams = parcel.readString();
        this.answerUrl = parcel.readString();
    }

    /* loaded from: classes.dex */
    public static class MalformedSurveyException extends Exception {
        public MalformedSurveyException(String str) {
            super(str);
        }
    }
}
