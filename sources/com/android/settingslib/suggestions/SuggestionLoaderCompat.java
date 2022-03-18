package com.android.settingslib.suggestions;

import android.content.Context;
import android.service.settings.suggestions.Suggestion;
import android.util.Log;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.util.List;
/* loaded from: classes.dex */
public class SuggestionLoaderCompat extends AsyncLoaderCompat<List<Suggestion>> {
    private final SuggestionController mSuggestionController;

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDiscardResult(List<Suggestion> list) {
    }

    public SuggestionLoaderCompat(Context context, SuggestionController suggestionController) {
        super(context);
        this.mSuggestionController = suggestionController;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public List<Suggestion> loadInBackground() {
        List<Suggestion> suggestions = this.mSuggestionController.getSuggestions();
        if (suggestions == null) {
            Log.d("SuggestionLoader", "data is null");
        } else {
            Log.d("SuggestionLoader", "data size " + suggestions.size());
        }
        return suggestions;
    }
}
