package com.google.android.settings.games;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.TogglePreferenceController;
import java.util.Map;
/* loaded from: classes2.dex */
public class GameDashboardDNDController extends TogglePreferenceController implements LifecycleObserver {
    private static final String TAG = "GDDNDController";
    @VisibleForTesting
    String mRuleId;
    private static final String PACKAGE_NAME = "com.android.systemui";
    @VisibleForTesting
    static final Uri CONDITION_ID = new Uri.Builder().scheme("android-app").authority(PACKAGE_NAME).appendPath("game-mode-dnd-controller").build();
    private static final ComponentName COMPONENT_NAME = new ComponentName(PACKAGE_NAME, "com.google.android.systemui.gamedashboard.GameDndConfigActivity");
    private final NotificationManager mNotificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
    private final String mRuleName = this.mContext.getString(R.string.game_dashboard_dnd_rule_name);

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_apps;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public GameDashboardDNDController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        AutomaticZenRule fetchRule;
        return (this.mRuleId == null || (fetchRule = fetchRule()) == null || fetchRule.getInterruptionFilter() == 1) ? false : true;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (this.mRuleId == null && z) {
            this.mRuleId = this.mNotificationManager.addAutomaticZenRule(new AutomaticZenRule(this.mRuleName, null, COMPONENT_NAME, CONDITION_ID, null, 1, true));
        }
        if (this.mRuleId == null && !z) {
            return true;
        }
        AutomaticZenRule fetchRule = fetchRule();
        fetchRule.setInterruptionFilter(z ? 2 : 1);
        this.mNotificationManager.updateAutomaticZenRule(this.mRuleId, fetchRule);
        return true;
    }

    @VisibleForTesting
    String getRuleId() {
        for (Map.Entry<String, AutomaticZenRule> entry : this.mNotificationManager.getAutomaticZenRules().entrySet()) {
            if (entry.getValue().getConditionId().equals(CONDITION_ID)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @VisibleForTesting
    AutomaticZenRule fetchRule() {
        AutomaticZenRule automaticZenRule = this.mNotificationManager.getAutomaticZenRule(this.mRuleId);
        if (automaticZenRule != null) {
            return automaticZenRule;
        }
        return null;
    }

    public void init(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onLifeCycleStartEvent() {
        this.mRuleId = getRuleId();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onLifeCycleStopEvent() {
        this.mRuleId = null;
    }
}
