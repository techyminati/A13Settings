package com.android.settings.applications;

import android.content.Context;
import android.util.Log;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class AppStateLocaleBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_APPS_LOCALE = new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStateLocaleBridge.1
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            Object obj = appEntry.extraInfo;
            if (obj != null) {
                return ((Boolean) obj).booleanValue();
            }
            Log.d(AppStateLocaleBridge.TAG, "No extra info.");
            return false;
        }
    };
    private static final String TAG = "AppStateLocaleBridge";
    private final Context mContext;

    public AppStateLocaleBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mContext = context;
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = AppLocaleUtil.canDisplayLocaleUi(this.mContext, appEntry) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        for (int i = 0; i < allApps.size(); i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            appEntry.extraInfo = AppLocaleUtil.canDisplayLocaleUi(this.mContext, appEntry) ? Boolean.TRUE : Boolean.FALSE;
        }
    }
}
