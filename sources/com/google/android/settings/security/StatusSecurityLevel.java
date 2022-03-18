package com.google.android.settings.security;

import androidx.window.R;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public enum StatusSecurityLevel {
    STATUS_SECURITY_LEVEL_UNKNOWN(R.drawable.security_status_info),
    INFORMATION_NO_ISSUES(R.drawable.security_status_info),
    INFORMATION_REVIEW_ISSUES(R.drawable.security_status_info_review),
    RECOMMENDATION(R.drawable.security_status_recommendation),
    CRITICAL_WARNING(R.drawable.security_status_warn);
    
    private final int mImageResId;

    StatusSecurityLevel(int i) {
        this.mImageResId = i;
    }

    public int getImageResId() {
        return this.mImageResId;
    }
}
