package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import androidx.window.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.display.NightDisplaySettings;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;
/* loaded from: classes.dex */
public class NightDisplayConditionController implements ConditionalCardController, NightDisplayListener.Callback {
    static final int ID = Objects.hash("NightDisplayConditionController");
    private final Context mAppContext;
    private final ColorDisplayManager mColorDisplayManager;
    private final ConditionManager mConditionManager;
    private final NightDisplayListener mNightDisplayListener;

    public NightDisplayConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mNightDisplayListener = new NightDisplayListener(context);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mColorDisplayManager.isNightDisplayActivated();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        new SubSettingLauncher(context).setDestination(NightDisplaySettings.class.getName()).setSourceMetricsCategory(1502).setTitleRes(R.string.night_display_title).launch();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mColorDisplayManager.setNightDisplayActivated(false);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId(ID).setMetricsConstant(492).setActionText(this.mAppContext.getText(R.string.condition_turn_off));
        return actionText.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(R.string.condition_night_display_title))).setTitleText(this.mAppContext.getText(R.string.condition_night_display_title).toString()).setSummaryText(this.mAppContext.getText(R.string.condition_night_display_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(R.drawable.ic_settings_night_display)).setViewType(R.layout.conditional_card_half_tile).build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mNightDisplayListener.setCallback(this);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
    }

    public void onActivated(boolean z) {
        this.mConditionManager.onConditionChanged();
    }
}
