package com.google.android.libraries.hats20;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.util.Objects;
/* loaded from: classes.dex */
public class HatsDownloadRequest {
    private final String advertisingId;
    private final String baseDownloadUrl;
    private final Context context;
    private final String siteContext;
    private final String siteId;

    private HatsDownloadRequest(Builder builder) {
        this.context = builder.context;
        this.siteContext = builder.siteContext;
        this.siteId = builder.siteId;
        this.advertisingId = builder.advertisingId;
        this.baseDownloadUrl = builder.baseDownloadUrl;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Context getContext() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getSiteId() {
        return this.siteId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Uri computeDownloadUri() {
        Uri.Builder appendQueryParameter = Uri.parse(this.baseDownloadUrl).buildUpon().appendQueryParameter("lang", "EN").appendQueryParameter("site", this.siteId).appendQueryParameter("adid", this.advertisingId);
        String str = this.siteContext;
        if (str != null) {
            appendQueryParameter.appendQueryParameter("sc", str);
        }
        return appendQueryParameter.build();
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private String advertisingId;
        private boolean alreadyBuilt;
        private String baseDownloadUrl;
        private final Context context;
        private String siteContext;
        private String siteId;

        private Builder(Context context) {
            this.baseDownloadUrl = "https://clients4.google.com/insights/consumersurveys/gk/prompt";
            this.alreadyBuilt = false;
            Objects.requireNonNull(context, "Context was missing.");
            this.context = context;
        }

        public Builder forSiteId(String str) {
            if (this.siteId == null) {
                Objects.requireNonNull(str, "Site ID cannot be set to null.");
                this.siteId = str;
                return this;
            }
            throw new UnsupportedOperationException("Currently don't support multiple site IDs.");
        }

        public Builder withAdvertisingId(String str) {
            Objects.requireNonNull(str, "Advertising ID was missing.");
            this.advertisingId = str;
            return this;
        }

        public Builder withSiteContext(String str) {
            Objects.requireNonNull(str, "Site context was missing.");
            if (str.length() > 1000) {
                Log.w("HatsLibDownloadRequest", "Site context was longer than 1000 chars, please trim it down.");
            }
            this.siteContext = str;
            return this;
        }

        public Builder setBaseDownloadUrlForTesting(String str) {
            Objects.requireNonNull(str, "Base download URL was missing.");
            this.baseDownloadUrl = str;
            return this;
        }

        public HatsDownloadRequest build() {
            if (!this.alreadyBuilt) {
                this.alreadyBuilt = true;
                if (this.siteId == null) {
                    Log.d("HatsLibDownloadRequest", "Site ID was not set, no survey will be downloaded.");
                    this.siteId = "-1";
                }
                Objects.requireNonNull(this.advertisingId, "Advertising ID was missing.");
                return new HatsDownloadRequest(this);
            }
            throw new IllegalStateException("Cannot reuse Builder instance once instantiated");
        }
    }
}
