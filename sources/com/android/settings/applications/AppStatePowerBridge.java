package com.android.settings.applications;

import android.content.Context;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class AppStatePowerBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_POWER_ALLOWLISTED = new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStatePowerBridge.1
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return appEntry.extraInfo == Boolean.TRUE;
        }
    });
    private final PowerAllowlistBackend mBackend;

    public AppStatePowerBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mBackend = PowerAllowlistBackend.getInstance(context);
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            appEntry.extraInfo = this.mBackend.isAllowlisted(appEntry.info.packageName) ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = this.mBackend.isAllowlisted(str) ? Boolean.TRUE : Boolean.FALSE;
    }
}
