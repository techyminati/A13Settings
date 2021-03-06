package com.android.settings.tts;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.util.Pair;
import androidx.lifecycle.AndroidViewModel;
/* loaded from: classes.dex */
public class TextToSpeechViewModel extends AndroidViewModel {
    private final Application mApplication;
    private TextToSpeech mTts;

    public TextToSpeechViewModel(Application application) {
        super(application);
        this.mApplication = application;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.lifecycle.ViewModel
    public void onCleared() {
        shutdownTts();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void shutdownTts() {
        this.mTts.shutdown();
        this.mTts = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Pair<TextToSpeech, Boolean> getTtsAndWhetherNew(TextToSpeech.OnInitListener onInitListener) {
        boolean z;
        if (this.mTts == null) {
            this.mTts = new TextToSpeech(this.mApplication, onInitListener);
            z = true;
        } else {
            z = false;
        }
        return Pair.create(this.mTts, Boolean.valueOf(z));
    }
}
