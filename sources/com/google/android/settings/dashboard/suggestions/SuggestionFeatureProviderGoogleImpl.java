package com.google.android.settings.dashboard.suggestions;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProviderImpl;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.settings.aware.AwareSettingsActivity;
import com.google.android.settings.aware.WakeScreenSuggestionActivity;
/* loaded from: classes2.dex */
public class SuggestionFeatureProviderGoogleImpl extends SuggestionFeatureProviderImpl {
    public SuggestionFeatureProviderGoogleImpl(Context context) {
        super(context);
    }

    @Override // com.android.settings.dashboard.suggestions.SuggestionFeatureProviderImpl, com.android.settings.dashboard.suggestions.SuggestionFeatureProvider
    public ComponentName getSuggestionServiceComponent() {
        return new ComponentName("com.google.android.settings.intelligence", "com.google.android.settings.intelligence.modules.suggestions.SuggestionService");
    }

    @Override // com.android.settings.dashboard.suggestions.SuggestionFeatureProviderImpl, com.android.settings.dashboard.suggestions.SuggestionFeatureProvider
    public boolean isSuggestionComplete(Context context, ComponentName componentName) {
        String className = componentName.getClassName();
        if (className.equals("com.google.android.settings.gestures.AssistGestureSuggestion")) {
            return !FeatureFactory.getFactory(context).getAssistGestureFeatureProvider().isSupported(context) || (Settings.Secure.getInt(context.getContentResolver(), "assist_gesture_setup_complete", 0) != 0) || !(Settings.Secure.getInt(context.getContentResolver(), "assist_gesture_enabled", 1) != 0);
        } else if (className.equals(AwareSettingsActivity.class.getName())) {
            return AwareSettingsActivity.isSuggestionComplete(context);
        } else {
            if (className.equals(WakeScreenSuggestionActivity.class.getName())) {
                return WakeScreenSuggestionActivity.isSuggestionComplete(context);
            }
            return super.isSuggestionComplete(context, componentName);
        }
    }

    @Override // com.android.settings.dashboard.suggestions.SuggestionFeatureProviderImpl, com.android.settings.dashboard.suggestions.SuggestionFeatureProvider
    public Class<? extends Fragment> getContextualSuggestionFragment() {
        return ContextualSuggestionFragment.class;
    }
}
