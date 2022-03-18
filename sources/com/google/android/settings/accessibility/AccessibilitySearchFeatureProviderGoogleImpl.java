package com.google.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityShortcutInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.provider.SearchIndexableData;
import android.view.accessibility.AccessibilityManager;
import androidx.window.R;
import com.android.settings.accessibility.AccessibilitySearchFeatureProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.google.android.settings.accessibility.AccessibilitySearchFeatureProviderGoogleImpl;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class AccessibilitySearchFeatureProviderGoogleImpl implements AccessibilitySearchFeatureProvider {
    @Override // com.android.settings.accessibility.AccessibilitySearchFeatureProvider
    public List<SearchIndexableRaw> getSearchIndexableRawData(Context context) {
        AccessibilityManager instance = AccessibilityManager.getInstance(context);
        List<AccessibilityShortcutInfo> installedAccessibilityShortcutListAsUser = instance.getInstalledAccessibilityShortcutListAsUser(context, UserHandle.myUserId());
        ArrayList arrayList = new ArrayList(instance.getInstalledAccessibilityServiceList());
        SearchIndexableRawHelper searchIndexableRawHelper = new SearchIndexableRawHelper(context);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.addAll(searchIndexableRawHelper.buildSupportedServiceSearchIndex(arrayList));
        arrayList2.addAll(searchIndexableRawHelper.buildSupportedActivitySearchIndex(installedAccessibilityShortcutListAsUser));
        return arrayList2;
    }

    /* loaded from: classes2.dex */
    static class SearchIndexableRawHelper {
        private final Context mContext;
        private final PackageManager mPm;

        SearchIndexableRawHelper(Context context) {
            this.mContext = context;
            this.mPm = context.getPackageManager();
        }

        public List<SearchIndexableRaw> buildSupportedServiceSearchIndex(List<AccessibilityServiceInfo> list) {
            ImmutableMap of = ImmutableMap.of(new ComponentName("com.google.android.marvin.talkback", "com.google.android.marvin.talkback.TalkBackService"), this.mContext.getString(R.string.keywords_talkback), new ComponentName("com.google.android.marvin.talkback", "com.google.android.accessibility.accessibilitymenu.AccessibilityMenuService"), this.mContext.getString(R.string.keywords_accessibility_menu), new ComponentName("com.google.android.marvin.talkback", "com.google.android.accessibility.selecttospeak.SelectToSpeakService"), this.mContext.getString(R.string.keywords_select_to_speak), new ComponentName("com.google.android.marvin.talkback", "com.android.switchaccess.SwitchAccessService"), this.mContext.getString(R.string.keywords_switch_access), new ComponentName("com.google.android.apps.accessibility.voiceaccess", "com.google.android.apps.accessibility.voiceaccess.JustSpeakService"), this.mContext.getString(R.string.keywords_voice_access));
            final ArrayList arrayList = new ArrayList();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                final ResolveInfo resolveInfo = list.get(i).getResolveInfo();
                final ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
                of.entrySet().stream().filter(new Predicate() { // from class: com.google.android.settings.accessibility.AccessibilitySearchFeatureProviderGoogleImpl$SearchIndexableRawHelper$$ExternalSyntheticLambda2
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$buildSupportedServiceSearchIndex$0;
                        lambda$buildSupportedServiceSearchIndex$0 = AccessibilitySearchFeatureProviderGoogleImpl.SearchIndexableRawHelper.lambda$buildSupportedServiceSearchIndex$0(componentName, (Map.Entry) obj);
                        return lambda$buildSupportedServiceSearchIndex$0;
                    }
                }).forEach(new Consumer() { // from class: com.google.android.settings.accessibility.AccessibilitySearchFeatureProviderGoogleImpl$SearchIndexableRawHelper$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AccessibilitySearchFeatureProviderGoogleImpl.SearchIndexableRawHelper.this.lambda$buildSupportedServiceSearchIndex$1(resolveInfo, arrayList, (Map.Entry) obj);
                    }
                });
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$buildSupportedServiceSearchIndex$0(ComponentName componentName, Map.Entry entry) {
            return componentName.equals(entry.getKey());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$buildSupportedServiceSearchIndex$1(ResolveInfo resolveInfo, List list, Map.Entry entry) {
            list.add(getSearchIndexableRaw(((ComponentName) entry.getKey()).flattenToString(), resolveInfo.loadLabel(this.mPm).toString(), (String) entry.getValue()));
        }

        public List<SearchIndexableRaw> buildSupportedActivitySearchIndex(List<AccessibilityShortcutInfo> list) {
            ImmutableMap of = ImmutableMap.of(new ComponentName("com.google.android.accessibility.soundamplifier", "com.google.android.accessibility.soundamplifier.ui.SoundAmplifierSettingActivity"), this.mContext.getString(R.string.keywords_sound_amplifier), new ComponentName("com.google.audio.hearing.visualization.accessibility.scribe", "com.google.audio.hearing.visualization.accessibility.scribe.MainActivity"), this.mContext.getString(R.string.keywords_live_transcribe), new ComponentName("com.google.audio.hearing.visualization.accessibility.scribe", "com.google.audio.hearing.visualization.accessibility.dolphin.ui.visualizer.TimelineActivity"), this.mContext.getString(R.string.keywords_sound_notifications));
            final ArrayList arrayList = new ArrayList();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                AccessibilityShortcutInfo accessibilityShortcutInfo = list.get(i);
                final ActivityInfo activityInfo = accessibilityShortcutInfo.getActivityInfo();
                final ComponentName componentName = accessibilityShortcutInfo.getComponentName();
                of.entrySet().stream().filter(new Predicate() { // from class: com.google.android.settings.accessibility.AccessibilitySearchFeatureProviderGoogleImpl$SearchIndexableRawHelper$$ExternalSyntheticLambda3
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$buildSupportedActivitySearchIndex$2;
                        lambda$buildSupportedActivitySearchIndex$2 = AccessibilitySearchFeatureProviderGoogleImpl.SearchIndexableRawHelper.lambda$buildSupportedActivitySearchIndex$2(componentName, (Map.Entry) obj);
                        return lambda$buildSupportedActivitySearchIndex$2;
                    }
                }).forEach(new Consumer() { // from class: com.google.android.settings.accessibility.AccessibilitySearchFeatureProviderGoogleImpl$SearchIndexableRawHelper$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AccessibilitySearchFeatureProviderGoogleImpl.SearchIndexableRawHelper.this.lambda$buildSupportedActivitySearchIndex$3(activityInfo, arrayList, (Map.Entry) obj);
                    }
                });
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$buildSupportedActivitySearchIndex$2(ComponentName componentName, Map.Entry entry) {
            return componentName.equals(entry.getKey());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$buildSupportedActivitySearchIndex$3(ActivityInfo activityInfo, List list, Map.Entry entry) {
            list.add(getSearchIndexableRaw(((ComponentName) entry.getKey()).flattenToString(), activityInfo.loadLabel(this.mPm).toString(), (String) entry.getValue()));
        }

        private SearchIndexableRaw getSearchIndexableRaw(String str, String str2, String str3) {
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(this.mContext);
            ((SearchIndexableData) searchIndexableRaw).key = str;
            searchIndexableRaw.title = str2;
            searchIndexableRaw.keywords = str3;
            return searchIndexableRaw;
        }
    }
}
