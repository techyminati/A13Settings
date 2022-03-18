package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkPolicyManager;
import androidx.window.R;
import com.android.settings.Settings;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;
/* loaded from: classes.dex */
public class BackgroundDataConditionController implements ConditionalCardController {
    static final int ID = Objects.hash("BackgroundDataConditionController");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final NetworkPolicyManager mNetworkPolicyManager;

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
    }

    public BackgroundDataConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mNetworkPolicyManager = (NetworkPolicyManager) context.getSystemService("netpolicy");
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mNetworkPolicyManager.getRestrictBackground();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        context.startActivity(new Intent(context, Settings.DataUsageSummaryActivity.class));
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mNetworkPolicyManager.setRestrictBackground(false);
        this.mConditionManager.onConditionChanged();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId(ID).setMetricsConstant(378).setActionText(this.mAppContext.getText(R.string.condition_turn_off));
        return actionText.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(R.string.condition_bg_data_title))).setTitleText(this.mAppContext.getText(R.string.condition_bg_data_title).toString()).setSummaryText(this.mAppContext.getText(R.string.condition_bg_data_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(R.drawable.ic_data_saver)).setViewType(R.layout.conditional_card_half_tile).build();
    }
}
