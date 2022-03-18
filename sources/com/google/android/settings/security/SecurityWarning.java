package com.google.android.settings.security;

import android.os.Bundle;
import com.google.common.base.Objects;
/* loaded from: classes2.dex */
public class SecurityWarning {
    private Bundle mDismissButtonClickBundle;
    private Bundle mPrimaryButtonClickBundle;
    private String mPrimaryButtonText;
    private Bundle mSecondaryButtonClickBundle;
    private String mSecondaryButtonText;
    private SecurityLevel mSecurityLevel;
    private boolean mShowConfirmationDialogOnDismiss;
    private String mSubtitle;
    private String mSummary;
    private String mTitle;

    private SecurityWarning(Builder builder) {
        this.mSecurityLevel = builder.mSecurityLevel;
        this.mTitle = builder.mTitle;
        this.mSubtitle = builder.mSubtitle;
        this.mSummary = builder.mSummary;
        this.mPrimaryButtonText = builder.mPrimaryButtonText;
        this.mSecondaryButtonText = builder.mSecondaryButtonText;
        this.mPrimaryButtonClickBundle = builder.mPrimaryButtonClickBundle;
        this.mSecondaryButtonClickBundle = builder.mSecondaryButtonClickBundle;
        this.mDismissButtonClickBundle = builder.mDismissButtonClickBundle;
        this.mShowConfirmationDialogOnDismiss = builder.mShowConfirmationDialogOnDismiss;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SecurityLevel getSecurityLevel() {
        return this.mSecurityLevel;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getSubtitle() {
        return this.mSubtitle;
    }

    public String getSummary() {
        return this.mSummary;
    }

    public String getPrimaryButtonText() {
        return this.mPrimaryButtonText;
    }

    public String getSecondaryButtonText() {
        return this.mSecondaryButtonText;
    }

    public Bundle getPrimaryButtonClickBundle() {
        return this.mPrimaryButtonClickBundle;
    }

    public Bundle getSecondaryButtonClickBundle() {
        return this.mSecondaryButtonClickBundle;
    }

    public Bundle getDismissButtonClickBundle() {
        return this.mDismissButtonClickBundle;
    }

    public boolean showConfirmationDialogOnDismiss() {
        return this.mShowConfirmationDialogOnDismiss;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SecurityWarning)) {
            return false;
        }
        SecurityWarning securityWarning = (SecurityWarning) obj;
        return Objects.equal(this.mSecurityLevel, securityWarning.getSecurityLevel()) && Objects.equal(this.mTitle, securityWarning.getTitle()) && Objects.equal(this.mSubtitle, securityWarning.getSubtitle()) && Objects.equal(this.mSummary, securityWarning.getSummary()) && Objects.equal(this.mPrimaryButtonText, securityWarning.getPrimaryButtonText()) && Objects.equal(this.mPrimaryButtonClickBundle, securityWarning.getPrimaryButtonClickBundle()) && Objects.equal(this.mSecondaryButtonText, securityWarning.getSecondaryButtonText()) && Objects.equal(this.mSecondaryButtonClickBundle, securityWarning.getSecondaryButtonClickBundle()) && Objects.equal(this.mDismissButtonClickBundle, securityWarning.getDismissButtonClickBundle());
    }

    public int hashCode() {
        return Objects.hashCode(this.mSecurityLevel, this.mTitle, this.mSubtitle, this.mSummary, this.mPrimaryButtonText, this.mPrimaryButtonClickBundle, this.mSecondaryButtonText, this.mSecondaryButtonClickBundle, this.mDismissButtonClickBundle);
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        private Bundle mDismissButtonClickBundle;
        private Bundle mPrimaryButtonClickBundle;
        private String mPrimaryButtonText;
        private Bundle mSecondaryButtonClickBundle;
        private String mSecondaryButtonText;
        private SecurityLevel mSecurityLevel;
        private boolean mShowConfirmationDialogOnDismiss;
        private String mSubtitle;
        private String mSummary;
        private String mTitle;

        public Builder setSecurityLevel(SecurityLevel securityLevel) {
            this.mSecurityLevel = securityLevel;
            return this;
        }

        public Builder setTitle(String str) {
            this.mTitle = str;
            return this;
        }

        public Builder setSubtitle(String str) {
            this.mSubtitle = str;
            return this;
        }

        public Builder setSummary(String str) {
            this.mSummary = str;
            return this;
        }

        public Builder setPrimaryButtonText(String str) {
            this.mPrimaryButtonText = str;
            return this;
        }

        public Builder setSecondaryButtonText(String str) {
            this.mSecondaryButtonText = str;
            return this;
        }

        public Builder setPrimaryButtonClickBundle(Bundle bundle) {
            this.mPrimaryButtonClickBundle = bundle;
            return this;
        }

        public Builder setSecondaryButtonClickBundle(Bundle bundle) {
            this.mSecondaryButtonClickBundle = bundle;
            return this;
        }

        public Builder setDismissButtonClickBundle(Bundle bundle) {
            this.mDismissButtonClickBundle = bundle;
            return this;
        }

        public Builder setShowConfirmationDialogOnDismiss(boolean z) {
            this.mShowConfirmationDialogOnDismiss = z;
            return this;
        }

        public SecurityWarning build() {
            return new SecurityWarning(this);
        }
    }
}
