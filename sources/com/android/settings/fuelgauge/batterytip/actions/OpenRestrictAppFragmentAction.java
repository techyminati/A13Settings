package com.android.settings.fuelgauge.batterytip.actions;

import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.fuelgauge.RestrictedAppDetails;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager;
import com.android.settings.fuelgauge.batterytip.tips.RestrictAppTip;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;
/* loaded from: classes.dex */
public class OpenRestrictAppFragmentAction extends BatteryTipAction {
    BatteryDatabaseManager mBatteryDatabaseManager = BatteryDatabaseManager.getInstance(this.mContext);
    private final InstrumentedPreferenceFragment mFragment;
    private final RestrictAppTip mRestrictAppTip;

    public OpenRestrictAppFragmentAction(InstrumentedPreferenceFragment instrumentedPreferenceFragment, RestrictAppTip restrictAppTip) {
        super(instrumentedPreferenceFragment.getContext());
        this.mFragment = instrumentedPreferenceFragment;
        this.mRestrictAppTip = restrictAppTip;
    }

    @Override // com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction
    public void handlePositiveAction(int i) {
        this.mMetricsFeatureProvider.action(this.mContext, 1361, i);
        final List<AppInfo> restrictAppList = this.mRestrictAppTip.getRestrictAppList();
        RestrictedAppDetails.startRestrictedAppDetails(this.mFragment, restrictAppList);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.fuelgauge.batterytip.actions.OpenRestrictAppFragmentAction$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                OpenRestrictAppFragmentAction.this.lambda$handlePositiveAction$0(restrictAppList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handlePositiveAction$0(List list) {
        this.mBatteryDatabaseManager.updateAnomalies(list, 1);
    }
}
