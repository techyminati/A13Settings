package com.google.android.settings.accessibility;

import android.content.ComponentName;
import com.android.settings.accessibility.AccessibilityMetricsFeatureProvider;
import com.google.common.collect.ImmutableMap;
/* loaded from: classes2.dex */
public class AccessibilityMetricsFeatureProviderGoogleImpl implements AccessibilityMetricsFeatureProvider {
    @Override // com.android.settings.accessibility.AccessibilityMetricsFeatureProvider
    public int getDownloadedFeatureMetricsCategory(ComponentName componentName) {
        if (componentName == null) {
            return 4;
        }
        return setup1PFeaturesEnumsMap().getOrDefault(componentName, 4).intValue();
    }

    private ImmutableMap<ComponentName, Integer> setup1PFeaturesEnumsMap() {
        ComponentName componentName = new ComponentName("com.google.android.marvin.talkback", "com.google.android.marvin.talkback.TalkBackService");
        ComponentName componentName2 = new ComponentName("com.google.android.marvin.talkback", "com.google.android.accessibility.accessibilitymenu.AccessibilityMenuService");
        ComponentName componentName3 = new ComponentName("com.google.android.marvin.talkback", "com.google.android.accessibility.selecttospeak.SelectToSpeakService");
        ComponentName componentName4 = new ComponentName("com.google.android.marvin.talkback", "com.android.switchaccess.SwitchAccessService");
        ComponentName componentName5 = new ComponentName("com.google.android.apps.accessibility.voiceaccess", "com.google.android.apps.accessibility.voiceaccess.JustSpeakService");
        ComponentName componentName6 = new ComponentName("com.google.android.accessibility.soundamplifier", "com.google.android.accessibility.soundamplifier.ui.SoundAmplifierSettingActivity");
        ComponentName componentName7 = new ComponentName("com.google.audio.hearing.visualization.accessibility.scribe", "com.google.audio.hearing.visualization.accessibility.scribe.MainActivity");
        return ImmutableMap.builder().put(componentName, 1899).put(componentName2, 1900).put(componentName3, 1901).put(componentName4, 1902).put(componentName5, 1903).put(componentName6, 1904).put(componentName7, 1905).put(new ComponentName("com.google.audio.hearing.visualization.accessibility.scribe", "com.google.audio.hearing.visualization.accessibility.dolphin.ui.visualizer.TimelineActivity"), 1906).build();
    }
}
