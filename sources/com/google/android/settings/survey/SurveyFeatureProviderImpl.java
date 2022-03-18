package com.google.android.settings.survey;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.overlay.SurveyFeatureProvider;
import com.android.settingslib.utils.AsyncLoader;
import com.google.android.libraries.hats20.HatsClient;
import com.google.android.libraries.hats20.HatsDownloadRequest;
import com.google.android.libraries.hats20.HatsShowRequest;
import com.google.android.settings.experiments.GServicesProxy;
import com.google.android.settings.security.SecurityContentManager;
import com.google.android.settings.support.PsdValuesLoader;
import com.google.android.settings.survey.SurveyFeatureProviderImpl;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class SurveyFeatureProviderImpl implements SurveyFeatureProvider, LoaderManager.LoaderCallbacks<HatsDownloadRequest> {
    private Context mContext;

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<HatsDownloadRequest> loader) {
    }

    public SurveyFeatureProviderImpl(Context context) {
        HatsClient.installCookieHandlerIfNeeded();
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.overlay.SurveyFeatureProvider
    public void downloadSurvey(Activity activity, String str, String str2) {
        Bundle bundle = new Bundle(2);
        bundle.putString("survey_id", str);
        bundle.putString("data", str2);
        if (activity != null && str != null) {
            activity.getLoaderManager().initLoader(20, bundle, this);
        }
    }

    @Override // com.android.settings.overlay.SurveyFeatureProvider
    public boolean showSurveyIfAvailable(Activity activity, String str) {
        if (activity != null) {
            return HatsClient.showSurveyIfAvailable(HatsShowRequest.builder(activity).forSiteId(str).build());
        }
        return false;
    }

    @Override // com.android.settings.overlay.SurveyFeatureProvider
    public String getSurveyId(Context context, String str) {
        return GServicesProxy.getString(context.getContentResolver(), String.format("settingsgoogle:%s_site_id", str), null);
    }

    @Override // com.android.settings.overlay.SurveyFeatureProvider
    public long getSurveyExpirationDate(Context context, String str) {
        return HatsClient.getSurveyExpirationDate(str, context);
    }

    @Override // com.android.settings.overlay.SurveyFeatureProvider
    public BroadcastReceiver createAndRegisterReceiver(Activity activity) {
        if (activity != null) {
            SurveyBroadcastReceiver surveyBroadcastReceiver = new SurveyBroadcastReceiver();
            surveyBroadcastReceiver.setActivity(activity);
            LocalBroadcastManager.getInstance(activity).registerReceiver(surveyBroadcastReceiver, new IntentFilter("com.google.android.libraries.hats20.SURVEY_DOWNLOADED"));
            return surveyBroadcastReceiver;
        }
        throw new IllegalStateException("Cannot register receiver if activity is null.");
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public Loader<HatsDownloadRequest> onCreateLoader(int i, Bundle bundle) {
        return new SurveyProviderLoader(this.mContext, bundle);
    }

    public void onLoadFinished(Loader<HatsDownloadRequest> loader, HatsDownloadRequest hatsDownloadRequest) {
        if (hatsDownloadRequest != null) {
            HatsClient.downloadSurvey(hatsDownloadRequest);
        }
    }

    /* loaded from: classes2.dex */
    public static class SurveyProviderLoader extends AsyncLoader<HatsDownloadRequest> {
        private static final Uri PROXY_AUTHORITY = new Uri.Builder().scheme("content").authority("com.google.android.settings.intelligence.provider.adsclientid").build();
        private String mData;
        private String mSurveyId;

        /* JADX INFO: Access modifiers changed from: protected */
        public void onDiscardResult(HatsDownloadRequest hatsDownloadRequest) {
        }

        public SurveyProviderLoader(Context context, Bundle bundle) {
            super(context);
            this.mSurveyId = bundle.getString("survey_id", null);
            this.mData = bundle.getString("data", null);
        }

        @VisibleForTesting
        String getPayload() {
            StringBuilder sb = new StringBuilder();
            Context context = getContext();
            if (GServicesProxy.getBoolean(context.getContentResolver(), "settingsgoogle:survey_payloads_enabled", false)) {
                for (String str : PsdValuesLoader.makePsdBundle(context, 1).getValues()) {
                    sb.append(str);
                    sb.append(",");
                }
                String str2 = this.mData;
                if (str2 != null) {
                    sb.append(str2);
                }
                if (sb.length() > 1000) {
                    sb.setLength(SecurityContentManager.DEFAULT_ORDER);
                }
            }
            return sb.toString();
        }

        @Override // android.content.AsyncTaskLoader
        public HatsDownloadRequest loadInBackground() {
            String adsId = getAdsId();
            if (adsId == null || this.mSurveyId == null) {
                return null;
            }
            return HatsDownloadRequest.builder(getContext().getApplicationContext()).forSiteId(this.mSurveyId).withAdvertisingId(adsId).withSiteContext(getPayload()).build();
        }

        private String getAdsId() {
            try {
                FutureTask futureTask = new FutureTask(new Callable() { // from class: com.google.android.settings.survey.SurveyFeatureProviderImpl$SurveyProviderLoader$$ExternalSyntheticLambda0
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        Bundle lambda$getAdsId$0;
                        lambda$getAdsId$0 = SurveyFeatureProviderImpl.SurveyProviderLoader.this.lambda$getAdsId$0();
                        return lambda$getAdsId$0;
                    }
                });
                Executors.newSingleThreadExecutor().submit(futureTask);
                return ((Bundle) futureTask.get(100L, TimeUnit.MILLISECONDS)).getString("value", null);
            } catch (Exception e) {
                Log.e("SurveyFeatureProvider", "Failed to query ads id provider", e);
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Bundle lambda$getAdsId$0() throws Exception {
            ContentProviderClient acquireUnstableContentProviderClient = getContext().getContentResolver().acquireUnstableContentProviderClient(PROXY_AUTHORITY);
            try {
                Bundle call = acquireUnstableContentProviderClient.call("getAdsClientId", null, null);
                acquireUnstableContentProviderClient.close();
                return call;
            } catch (Throwable th) {
                if (acquireUnstableContentProviderClient != null) {
                    try {
                        acquireUnstableContentProviderClient.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }
}
