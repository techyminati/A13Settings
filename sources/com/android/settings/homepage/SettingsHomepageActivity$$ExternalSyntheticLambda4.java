package com.android.settings.homepage;

import com.android.settings.homepage.SettingsHomepageActivity;
import java.util.function.Consumer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class SettingsHomepageActivity$$ExternalSyntheticLambda4 implements Consumer {
    public static final /* synthetic */ SettingsHomepageActivity$$ExternalSyntheticLambda4 INSTANCE = new SettingsHomepageActivity$$ExternalSyntheticLambda4();

    private /* synthetic */ SettingsHomepageActivity$$ExternalSyntheticLambda4() {
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        ((SettingsHomepageActivity.HomepageLoadedListener) obj).onHomepageLoaded();
    }
}
