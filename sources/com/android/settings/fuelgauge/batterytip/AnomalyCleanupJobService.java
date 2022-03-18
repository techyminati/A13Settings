package com.android.settings.fuelgauge.batterytip;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import androidx.window.R;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class AnomalyCleanupJobService extends JobService {
    static final long CLEAN_UP_FREQUENCY_MS = TimeUnit.DAYS.toMillis(1);

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public static void scheduleCleanUp(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        JobInfo.Builder persisted = new JobInfo.Builder(R.integer.job_anomaly_clean_up, new ComponentName(context, AnomalyCleanupJobService.class)).setPeriodic(CLEAN_UP_FREQUENCY_MS).setRequiresDeviceIdle(true).setRequiresCharging(true).setPersisted(true);
        if (jobScheduler.getPendingJob(R.integer.job_anomaly_clean_up) == null && jobScheduler.schedule(persisted.build()) != 1) {
            Log.i("AnomalyCleanUpJobService", "Anomaly clean up job service schedule failed.");
        }
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters jobParameters) {
        final BatteryDatabaseManager instance = BatteryDatabaseManager.getInstance(this);
        final BatteryTipPolicy batteryTipPolicy = new BatteryTipPolicy(this);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.fuelgauge.batterytip.AnomalyCleanupJobService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AnomalyCleanupJobService.this.lambda$onStartJob$0(instance, batteryTipPolicy, jobParameters);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartJob$0(BatteryDatabaseManager batteryDatabaseManager, BatteryTipPolicy batteryTipPolicy, JobParameters jobParameters) {
        batteryDatabaseManager.deleteAllAnomaliesBeforeTimeStamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(batteryTipPolicy.dataHistoryRetainDay));
        jobFinished(jobParameters, false);
    }
}
