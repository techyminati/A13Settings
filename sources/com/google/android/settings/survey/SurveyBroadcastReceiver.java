package com.google.android.settings.survey;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.overlay.SurveyFeatureProvider;
/* loaded from: classes2.dex */
public class SurveyBroadcastReceiver extends BroadcastReceiver {
    private Activity mActivity;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        SurveyFeatureProvider surveyFeatureProvider = FeatureFactory.getFactory(context).getSurveyFeatureProvider(context);
        if (this.mActivity != null && "com.google.android.libraries.hats20.SURVEY_DOWNLOADED".equals(intent.getAction())) {
            SurveyFeatureProvider.unregisterReceiver(this.mActivity, this);
            if (surveyFeatureProvider != null) {
                surveyFeatureProvider.showSurveyIfAvailable(this.mActivity, intent.getStringExtra("SiteId"));
            }
        }
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }
}
