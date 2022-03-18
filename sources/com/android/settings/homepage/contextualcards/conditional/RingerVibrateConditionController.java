package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import androidx.window.R;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.util.Objects;
/* loaded from: classes.dex */
public class RingerVibrateConditionController extends AbnormalRingerConditionController {
    static final int ID = Objects.hash("RingerVibrateConditionController");
    private final Context mAppContext;

    public RingerVibrateConditionController(Context context, ConditionManager conditionManager) {
        super(context, conditionManager);
        this.mAppContext = context;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mAudioManager.getRingerModeInternal() == 1;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder actionText = new ConditionalContextualCard.Builder().setConditionId(ID).setMetricsConstant(1369).setActionText(this.mAppContext.getText(R.string.condition_device_muted_action_turn_on_sound));
        return actionText.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(R.string.condition_device_vibrate_title))).setTitleText(this.mAppContext.getText(R.string.condition_device_vibrate_title).toString()).setSummaryText(this.mAppContext.getText(R.string.condition_device_vibrate_summary).toString()).setIconDrawable(this.mAppContext.getDrawable(R.drawable.ic_volume_ringer_vibrate)).setViewType(R.layout.conditional_card_half_tile).build();
    }
}
