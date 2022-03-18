package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.BatteryStats;
import android.os.BatteryStatsManager;
import android.os.BatteryUsageStats;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseIntArray;
import androidx.window.R;
import com.android.internal.os.BatteryStatsHistoryIterator;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.UsageView;
import com.android.settingslib.Utils;
import com.android.settingslib.fuelgauge.BatteryStatus;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.settingslib.utils.StringUtil;
/* loaded from: classes.dex */
public class BatteryInfo {
    public int batteryLevel;
    public String batteryPercentString;
    public int batteryStatus;
    public CharSequence chargeLabel;
    public boolean isOverheated;
    private BatteryUsageStats mBatteryUsageStats;
    private boolean mCharging;
    public CharSequence remainingLabel;
    public String statusLabel;
    public String suggestionLabel;
    private long timePeriod;
    public boolean discharging = true;
    public long remainingTimeUs = 0;
    public long averageTimeToDischarge = -1;

    /* loaded from: classes.dex */
    public interface BatteryDataParser {
        void onDataGap();

        void onDataPoint(long j, BatteryStats.HistoryItem historyItem);

        void onParsingDone();

        void onParsingStarted(long j, long j2);
    }

    /* loaded from: classes.dex */
    public interface Callback {
        void onBatteryInfoLoaded(BatteryInfo batteryInfo);
    }

    public void bindHistory(final UsageView usageView, BatteryDataParser... batteryDataParserArr) {
        final Context context = usageView.getContext();
        BatteryDataParser batteryDataParser = new BatteryDataParser() { // from class: com.android.settings.fuelgauge.BatteryInfo.1
            byte lastLevel;
            long startTime;
            SparseIntArray points = new SparseIntArray();
            int lastTime = -1;

            @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
            public void onParsingStarted(long j, long j2) {
                this.startTime = j;
                BatteryInfo.this.timePeriod = j2 - j;
                usageView.clearPaths();
                usageView.configureGraph((int) BatteryInfo.this.timePeriod, 100);
            }

            @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
            public void onDataPoint(long j, BatteryStats.HistoryItem historyItem) {
                int i = (int) j;
                this.lastTime = i;
                byte b = historyItem.batteryLevel;
                this.lastLevel = b;
                this.points.put(i, b);
            }

            @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
            public void onDataGap() {
                if (this.points.size() > 1) {
                    usageView.addPath(this.points);
                }
                this.points.clear();
            }

            @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
            public void onParsingDone() {
                onDataGap();
                if (BatteryInfo.this.remainingTimeUs != 0) {
                    PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
                    if (BatteryInfo.this.mCharging || !powerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(context)) {
                        int i = this.lastTime;
                        if (i >= 0) {
                            this.points.put(i, this.lastLevel);
                            this.points.put((int) (BatteryInfo.this.timePeriod + PowerUtil.convertUsToMs(BatteryInfo.this.remainingTimeUs)), BatteryInfo.this.mCharging ? 100 : 0);
                        }
                    } else {
                        this.points = powerUsageFeatureProvider.getEnhancedBatteryPredictionCurve(context, this.startTime);
                    }
                }
                SparseIntArray sparseIntArray = this.points;
                if (sparseIntArray != null && sparseIntArray.size() > 0) {
                    SparseIntArray sparseIntArray2 = this.points;
                    usageView.configureGraph(sparseIntArray2.keyAt(sparseIntArray2.size() - 1), 100);
                    usageView.addProjectedPath(this.points);
                }
            }
        };
        BatteryDataParser[] batteryDataParserArr2 = new BatteryDataParser[batteryDataParserArr.length + 1];
        for (int i = 0; i < batteryDataParserArr.length; i++) {
            batteryDataParserArr2[i] = batteryDataParserArr[i];
        }
        batteryDataParserArr2[batteryDataParserArr.length] = batteryDataParser;
        parseBatteryHistory(batteryDataParserArr2);
        String string = context.getString(R.string.charge_length_format, Formatter.formatShortElapsedTime(context, this.timePeriod));
        long j = this.remainingTimeUs;
        usageView.setBottomLabels(new CharSequence[]{string, j != 0 ? context.getString(R.string.remaining_length_format, Formatter.formatShortElapsedTime(context, j / 1000)) : ""});
    }

    public static void getBatteryInfo(Context context, Callback callback, boolean z) {
        getBatteryInfo(context, callback, null, z);
    }

    public static void getBatteryInfo(final Context context, final Callback callback, final BatteryUsageStats batteryUsageStats, final boolean z) {
        new AsyncTask<Void, Void, BatteryInfo>() { // from class: com.android.settings.fuelgauge.BatteryInfo.2
            /* JADX INFO: Access modifiers changed from: protected */
            public BatteryInfo doInBackground(Void... voidArr) {
                BatteryUsageStats batteryUsageStats2 = batteryUsageStats;
                if (batteryUsageStats2 == null) {
                    try {
                        batteryUsageStats2 = ((BatteryStatsManager) context.getSystemService(BatteryStatsManager.class)).getBatteryUsageStats();
                    } catch (RuntimeException e) {
                        Log.e("BatteryInfo", "getBatteryInfo() from getBatteryUsageStats()", e);
                        batteryUsageStats2 = new BatteryUsageStats.Builder(new String[0]).build();
                    }
                }
                BatteryInfo batteryInfo = BatteryInfo.getBatteryInfo(context, batteryUsageStats2, z);
                try {
                    batteryUsageStats2.close();
                } catch (Exception e2) {
                    Log.e("BatteryInfo", "BatteryUsageStats.close() failed", e2);
                }
                return batteryInfo;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            public void onPostExecute(BatteryInfo batteryInfo) {
                long currentTimeMillis = System.currentTimeMillis();
                callback.onBatteryInfoLoaded(batteryInfo);
                BatteryUtils.logRuntime("BatteryInfo", "time for callback", currentTimeMillis);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static BatteryInfo getBatteryInfo(Context context, BatteryUsageStats batteryUsageStats, boolean z) {
        Estimate enhancedBatteryPrediction;
        BatteryUtils.logRuntime("BatteryInfo", "time for getStats", System.currentTimeMillis());
        long currentTimeMillis = System.currentTimeMillis();
        PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
        long convertMsToUs = PowerUtil.convertMsToUs(SystemClock.elapsedRealtime());
        Intent registerReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        boolean z2 = registerReceiver.getIntExtra("plugged", -1) == 0;
        if (!z2 || powerUsageFeatureProvider == null || !powerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(context) || (enhancedBatteryPrediction = powerUsageFeatureProvider.getEnhancedBatteryPrediction(context)) == null) {
            Estimate estimate = new Estimate(z2 ? batteryUsageStats.getBatteryTimeRemainingMs() : 0L, false, -1L);
            BatteryUtils.logRuntime("BatteryInfo", "time for regular BatteryInfo", currentTimeMillis);
            return getBatteryInfo(context, registerReceiver, batteryUsageStats, estimate, convertMsToUs, z);
        }
        Estimate.storeCachedEstimate(context, enhancedBatteryPrediction);
        BatteryUtils.logRuntime("BatteryInfo", "time for enhanced BatteryInfo", currentTimeMillis);
        return getBatteryInfo(context, registerReceiver, batteryUsageStats, enhancedBatteryPrediction, convertMsToUs, z);
    }

    public static BatteryInfo getBatteryInfo(Context context, Intent intent, BatteryUsageStats batteryUsageStats, Estimate estimate, long j, boolean z) {
        long currentTimeMillis = System.currentTimeMillis();
        BatteryInfo batteryInfo = new BatteryInfo();
        batteryInfo.mBatteryUsageStats = batteryUsageStats;
        int batteryLevel = Utils.getBatteryLevel(intent);
        batteryInfo.batteryLevel = batteryLevel;
        batteryInfo.batteryPercentString = Utils.formatPercentage(batteryLevel);
        boolean z2 = false;
        batteryInfo.mCharging = intent.getIntExtra("plugged", 0) != 0;
        batteryInfo.averageTimeToDischarge = estimate.getAverageDischargeTime();
        if (intent.getIntExtra("health", 1) == 3) {
            z2 = true;
        }
        batteryInfo.isOverheated = z2;
        batteryInfo.statusLabel = getBatteryStatus(context, intent);
        batteryInfo.batteryStatus = intent.getIntExtra("status", 1);
        if (!batteryInfo.mCharging) {
            updateBatteryInfoDischarging(context, z, estimate, batteryInfo);
        } else {
            updateBatteryInfoCharging(context, intent, batteryUsageStats, batteryInfo);
        }
        BatteryUtils.logRuntime("BatteryInfo", "time for getBatteryInfo", currentTimeMillis);
        return batteryInfo;
    }

    private static void updateBatteryInfoCharging(Context context, Intent intent, BatteryUsageStats batteryUsageStats, BatteryInfo batteryInfo) {
        Resources resources = context.getResources();
        long chargeTimeRemainingMs = batteryUsageStats.getChargeTimeRemainingMs();
        int intExtra = intent.getIntExtra("status", 1);
        batteryInfo.discharging = false;
        batteryInfo.suggestionLabel = null;
        if (batteryInfo.isOverheated && intExtra != 5) {
            batteryInfo.remainingLabel = null;
            batteryInfo.chargeLabel = context.getString(R.string.power_charging_limited, batteryInfo.batteryPercentString);
        } else if (chargeTimeRemainingMs <= 0 || intExtra == 5) {
            String batteryStatus = getBatteryStatus(context, intent);
            batteryInfo.remainingLabel = null;
            batteryInfo.chargeLabel = batteryInfo.batteryLevel == 100 ? batteryInfo.batteryPercentString : resources.getString(R.string.power_charging, batteryInfo.batteryPercentString, batteryStatus.toLowerCase());
        } else {
            long convertMsToUs = PowerUtil.convertMsToUs(chargeTimeRemainingMs);
            batteryInfo.remainingTimeUs = convertMsToUs;
            CharSequence formatElapsedTime = StringUtil.formatElapsedTime(context, PowerUtil.convertUsToMs(convertMsToUs), false, true);
            batteryInfo.remainingLabel = context.getString(R.string.power_remaining_charging_duration_only, formatElapsedTime);
            batteryInfo.chargeLabel = context.getString(R.string.power_charging_duration, batteryInfo.batteryPercentString, formatElapsedTime);
        }
    }

    private static void updateBatteryInfoDischarging(Context context, boolean z, Estimate estimate, BatteryInfo batteryInfo) {
        long convertMsToUs = PowerUtil.convertMsToUs(estimate.getEstimateMillis());
        if (convertMsToUs > 0) {
            batteryInfo.remainingTimeUs = convertMsToUs;
            boolean z2 = false;
            batteryInfo.remainingLabel = PowerUtil.getBatteryRemainingStringFormatted(context, PowerUtil.convertUsToMs(convertMsToUs), null, false);
            long convertUsToMs = PowerUtil.convertUsToMs(convertMsToUs);
            String str = batteryInfo.batteryPercentString;
            if (estimate.isBasedOnUsage() && !z) {
                z2 = true;
            }
            batteryInfo.chargeLabel = PowerUtil.getBatteryRemainingStringFormatted(context, convertUsToMs, str, z2);
            batteryInfo.suggestionLabel = PowerUtil.getBatteryTipStringFormatted(context, PowerUtil.convertUsToMs(convertMsToUs));
            return;
        }
        batteryInfo.remainingLabel = null;
        batteryInfo.suggestionLabel = null;
        batteryInfo.chargeLabel = batteryInfo.batteryPercentString;
    }

    private static String getBatteryStatus(Context context, Intent intent) {
        Resources resources = context.getResources();
        if (!resources.getBoolean(R.bool.config_use_compact_battery_status)) {
            return Utils.getBatteryStatus(context, intent);
        }
        int intExtra = intent.getIntExtra("status", 1);
        BatteryStatus batteryStatus = new BatteryStatus(intent);
        String string = resources.getString(R.string.battery_info_status_unknown);
        if (batteryStatus.isCharged()) {
            return resources.getString(R.string.battery_info_status_full);
        }
        if (intExtra == 2) {
            return resources.getString(R.string.battery_info_status_charging);
        }
        if (intExtra == 3) {
            return resources.getString(R.string.battery_info_status_discharging);
        }
        return intExtra == 4 ? resources.getString(R.string.battery_info_status_not_charging) : string;
    }

    public void parseBatteryHistory(BatteryDataParser... batteryDataParserArr) {
        byte b;
        long j;
        BatteryStatsHistoryIterator iterateBatteryStatsHistory = this.mBatteryUsageStats.iterateBatteryStatsHistory();
        BatteryStats.HistoryItem historyItem = new BatteryStats.HistoryItem();
        int i = 1;
        boolean z = true;
        long j2 = 0;
        long j3 = 0;
        long j4 = 0;
        int i2 = 0;
        int i3 = 0;
        long j5 = 0;
        long j6 = 0;
        while (true) {
            b = 5;
            if (!iterateBatteryStatsHistory.next(historyItem)) {
                break;
            }
            i3 += i;
            if (z) {
                j6 = historyItem.time;
                z = false;
            }
            byte b2 = historyItem.cmd;
            if (b2 == 5 || b2 == 7) {
                long j7 = historyItem.currentTime;
                if (j7 > j2 + 15552000000L || historyItem.time < j6 + 300000) {
                    j5 = 0;
                }
                j4 = historyItem.time;
                if (j5 == 0) {
                    j5 = j7 - (j4 - j6);
                }
                j2 = j7;
            }
            if (historyItem.isDeltaData()) {
                j3 = historyItem.time;
                i2 = i3;
            }
            i = 1;
        }
        long j8 = (j2 + j3) - j4;
        for (BatteryDataParser batteryDataParser : batteryDataParserArr) {
            batteryDataParser.onParsingStarted(j5, j8);
        }
        if (j8 > j5) {
            BatteryStatsHistoryIterator iterateBatteryStatsHistory2 = this.mBatteryUsageStats.iterateBatteryStatsHistory();
            long j9 = 0;
            for (int i4 = 0; iterateBatteryStatsHistory2.next(historyItem) && i4 < i2; i4++) {
                if (historyItem.isDeltaData()) {
                    long j10 = historyItem.time;
                    j9 += j10 - j4;
                    long j11 = j9 - j5;
                    if (j11 < 0) {
                        j11 = 0;
                    }
                    for (BatteryDataParser batteryDataParser2 : batteryDataParserArr) {
                        batteryDataParser2.onDataPoint(j11, historyItem);
                    }
                    j4 = j10;
                    b = b;
                } else {
                    byte b3 = historyItem.cmd;
                    if (b3 == b || b3 == 7) {
                        j = historyItem.currentTime;
                        if (j < j5) {
                            j = (historyItem.time - j6) + j5;
                        }
                        j4 = historyItem.time;
                    } else {
                        j4 = j4;
                        j = j9;
                    }
                    b = 5;
                    if (b3 != 6 && (b3 != 5 || Math.abs(j9 - j) > 3600000)) {
                        for (BatteryDataParser batteryDataParser3 : batteryDataParserArr) {
                            batteryDataParser3.onDataGap();
                        }
                    }
                    j9 = j;
                }
            }
        }
        for (BatteryDataParser batteryDataParser4 : batteryDataParserArr) {
            batteryDataParser4.onParsingDone();
        }
    }
}
