package com.android.settings.fuelgauge;

import android.content.ContentValues;
import android.content.Context;
import android.os.BatteryUsageStats;
import android.os.LocaleList;
import android.os.UserHandle;
import android.text.format.DateFormat;
import com.android.settings.overlay.FeatureFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
/* loaded from: classes.dex */
public final class ConvertUtils {
    private static final Map<String, BatteryHistEntry> EMPTY_BATTERY_MAP = new HashMap();
    private static final BatteryHistEntry EMPTY_BATTERY_HIST_ENTRY = new BatteryHistEntry(new ContentValues());
    static double PERCENTAGE_OF_TOTAL_THRESHOLD = 1.0d;

    private static double getDiffValue(double d, double d2, double d3) {
        double d4 = 0.0d;
        double d5 = d2 > d ? d2 - d : 0.0d;
        if (d3 > d2) {
            d4 = d3 - d2;
        }
        return d5 + d4;
    }

    private static long getDiffValue(long j, long j2, long j3) {
        long j4 = 0;
        long j5 = j2 > j ? j2 - j : 0L;
        if (j3 > j2) {
            j4 = j3 - j2;
        }
        return j5 + j4;
    }

    public static ContentValues convert(BatteryEntry batteryEntry, BatteryUsageStats batteryUsageStats, int i, int i2, int i3, long j, long j2) {
        ContentValues contentValues = new ContentValues();
        if (batteryEntry == null || batteryUsageStats == null) {
            contentValues.put("packageName", "fake_package");
        } else {
            contentValues.put("uid", Long.valueOf(batteryEntry.getUid()));
            contentValues.put("userId", Long.valueOf(UserHandle.getUserId(batteryEntry.getUid())));
            contentValues.put("appLabel", batteryEntry.getLabel());
            contentValues.put("packageName", batteryEntry.getDefaultPackageName());
            contentValues.put("isHidden", Boolean.valueOf(batteryEntry.isHidden()));
            contentValues.put("totalPower", Double.valueOf(batteryUsageStats.getConsumedPower()));
            contentValues.put("consumePower", Double.valueOf(batteryEntry.getConsumedPower()));
            contentValues.put("percentOfTotal", Double.valueOf(batteryEntry.percent));
            contentValues.put("foregroundUsageTimeInMs", Long.valueOf(batteryEntry.getTimeInForegroundMs()));
            contentValues.put("backgroundUsageTimeInMs", Long.valueOf(batteryEntry.getTimeInBackgroundMs()));
            contentValues.put("drainType", Integer.valueOf(batteryEntry.getPowerComponentId()));
            contentValues.put("consumerType", Integer.valueOf(batteryEntry.getConsumerType()));
        }
        contentValues.put("bootTimestamp", Long.valueOf(j));
        contentValues.put("timestamp", Long.valueOf(j2));
        contentValues.put("zoneId", TimeZone.getDefault().getID());
        contentValues.put("batteryLevel", Integer.valueOf(i));
        contentValues.put("batteryStatus", Integer.valueOf(i2));
        contentValues.put("batteryHealth", Integer.valueOf(i3));
        return contentValues;
    }

    public static String utcToLocalTime(Context context, long j) {
        return DateFormat.format(DateFormat.getBestDateTimePattern(getLocale(context), "MMM dd,yyyy HH:mm:ss"), j).toString();
    }

    public static String utcToLocalTimeHour(Context context, long j, boolean z) {
        Locale locale = getLocale(context);
        return DateFormat.format(DateFormat.getBestDateTimePattern(locale, z ? "HHm" : "ha"), j).toString().toLowerCase(locale);
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x00ee, code lost:
        if (r5 == 0.0d) goto L_0x00f9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.Map<java.lang.Integer, java.util.List<com.android.settings.fuelgauge.BatteryDiffEntry>> getIndexedUsageMap(android.content.Context r37, int r38, long[] r39, java.util.Map<java.lang.Long, java.util.Map<java.lang.String, com.android.settings.fuelgauge.BatteryHistEntry>> r40, boolean r41) {
        /*
            Method dump skipped, instructions count: 398
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.fuelgauge.ConvertUtils.getIndexedUsageMap(android.content.Context, int, long[], java.util.Map, boolean):java.util.Map");
    }

    private static void insert24HoursData(int i, Map<Integer, List<BatteryDiffEntry>> map) {
        HashMap hashMap = new HashMap();
        double d = 0.0d;
        for (List<BatteryDiffEntry> list : map.values()) {
            for (BatteryDiffEntry batteryDiffEntry : list) {
                String key = batteryDiffEntry.mBatteryHistEntry.getKey();
                BatteryDiffEntry batteryDiffEntry2 = (BatteryDiffEntry) hashMap.get(key);
                if (batteryDiffEntry2 == null) {
                    hashMap.put(key, batteryDiffEntry.clone());
                } else {
                    batteryDiffEntry2.mForegroundUsageTimeInMs += batteryDiffEntry.mForegroundUsageTimeInMs;
                    batteryDiffEntry2.mBackgroundUsageTimeInMs += batteryDiffEntry.mBackgroundUsageTimeInMs;
                    batteryDiffEntry2.mConsumePower += batteryDiffEntry.mConsumePower;
                }
                d += batteryDiffEntry.mConsumePower;
            }
        }
        ArrayList<BatteryDiffEntry> arrayList = new ArrayList(hashMap.values());
        for (BatteryDiffEntry batteryDiffEntry3 : arrayList) {
            batteryDiffEntry3.setTotalConsumePower(d);
        }
        map.put(Integer.valueOf(i), arrayList);
    }

    private static void purgeLowPercentageAndFakeData(Context context, Map<Integer, List<BatteryDiffEntry>> map) {
        Set<CharSequence> hideBackgroundUsageTimeSet = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context).getHideBackgroundUsageTimeSet(context);
        for (List<BatteryDiffEntry> list : map.values()) {
            Iterator<BatteryDiffEntry> it = list.iterator();
            while (it.hasNext()) {
                BatteryDiffEntry next = it.next();
                if (next.getPercentOfTotal() < PERCENTAGE_OF_TOTAL_THRESHOLD || "fake_package".equals(next.getPackageName())) {
                    it.remove();
                }
                String packageName = next.getPackageName();
                if (packageName != null && !hideBackgroundUsageTimeSet.isEmpty() && hideBackgroundUsageTimeSet.contains(packageName)) {
                    next.mBackgroundUsageTimeInMs = 0L;
                }
            }
        }
    }

    private static BatteryHistEntry selectBatteryHistEntry(BatteryHistEntry batteryHistEntry, BatteryHistEntry batteryHistEntry2, BatteryHistEntry batteryHistEntry3) {
        if (batteryHistEntry != null && batteryHistEntry != EMPTY_BATTERY_HIST_ENTRY) {
            return batteryHistEntry;
        }
        if (batteryHistEntry2 != null && batteryHistEntry2 != EMPTY_BATTERY_HIST_ENTRY) {
            return batteryHistEntry2;
        }
        if (batteryHistEntry3 == null || batteryHistEntry3 == EMPTY_BATTERY_HIST_ENTRY) {
            return null;
        }
        return batteryHistEntry3;
    }

    static Locale getLocale(Context context) {
        if (context == null) {
            return Locale.getDefault();
        }
        LocaleList locales = context.getResources().getConfiguration().getLocales();
        return (locales == null || locales.isEmpty()) ? Locale.getDefault() : locales.get(0);
    }
}
