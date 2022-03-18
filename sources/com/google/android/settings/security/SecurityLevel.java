package com.google.android.settings.security;

import androidx.window.R;
import com.android.settingslib.widget.BannerMessagePreference;
/* JADX INFO: Access modifiers changed from: package-private */
/* JADX WARN: Init of enum INFORMATION can be incorrect */
/* JADX WARN: Init of enum NONE can be incorrect */
/* JADX WARN: Init of enum SECURITY_LEVEL_UNKNOWN can be incorrect */
/* loaded from: classes2.dex */
public enum SecurityLevel {
    SECURITY_LEVEL_UNKNOWN(R.drawable.ic_security_empty, R.drawable.ic_security_empty, r7),
    NONE(R.drawable.ic_security_null_state, R.drawable.ic_security_null_state, r7),
    INFORMATION(R.drawable.ic_security_info, R.drawable.ic_security_info_outline, r7),
    RECOMMENDATION(R.drawable.ic_security_recommendation, R.drawable.ic_security_recommendation_outline, BannerMessagePreference.AttentionLevel.MEDIUM),
    CRITICAL_WARNING(R.drawable.ic_security_warn, R.drawable.ic_security_warn_outline, BannerMessagePreference.AttentionLevel.HIGH);
    
    private final BannerMessagePreference.AttentionLevel mAttentionLevel;
    private final int mEntryIconResId;
    private final int mWarningCardIconResId;

    static {
        BannerMessagePreference.AttentionLevel attentionLevel = BannerMessagePreference.AttentionLevel.LOW;
    }

    SecurityLevel(int i, int i2, BannerMessagePreference.AttentionLevel attentionLevel) {
        this.mEntryIconResId = i;
        this.mWarningCardIconResId = i2;
        this.mAttentionLevel = attentionLevel;
    }

    public int getEntryIconResId() {
        return this.mEntryIconResId;
    }

    public int getWarningCardIconResId() {
        return this.mWarningCardIconResId;
    }

    public BannerMessagePreference.AttentionLevel getAttentionLevel() {
        return this.mAttentionLevel;
    }
}
