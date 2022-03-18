package com.android.settingslib.datetime;

import android.content.Context;
import android.icu.text.TimeZoneFormat;
import android.icu.text.TimeZoneNames;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import androidx.core.text.BidiFormatter;
import androidx.core.text.TextDirectionHeuristicsCompat;
import com.android.i18n.timezone.CountryTimeZones;
import com.android.i18n.timezone.TimeZoneFinder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
/* loaded from: classes.dex */
public class ZoneGetter {
    public static CharSequence getTimeZoneOffsetAndName(Context context, TimeZone timeZone, Date date) {
        Locale locale = context.getResources().getConfiguration().locale;
        CharSequence gmtOffsetText = getGmtOffsetText(TimeZoneFormat.getInstance(locale), locale, timeZone, date);
        String zoneLongName = getZoneLongName(TimeZoneNames.getInstance(locale), timeZone, date);
        return zoneLongName == null ? gmtOffsetText : TextUtils.concat(gmtOffsetText, " ", zoneLongName);
    }

    private static String getZoneLongName(TimeZoneNames timeZoneNames, TimeZone timeZone, Date date) {
        return timeZoneNames.getDisplayName(getCanonicalZoneId(timeZone), timeZone.inDaylightTime(date) ? TimeZoneNames.NameType.LONG_DAYLIGHT : TimeZoneNames.NameType.LONG_STANDARD, date.getTime());
    }

    private static String getCanonicalZoneId(TimeZone timeZone) {
        String id = timeZone.getID();
        String canonicalID = android.icu.util.TimeZone.getCanonicalID(id);
        return canonicalID != null ? canonicalID : id;
    }

    private static void appendWithTtsSpan(SpannableStringBuilder spannableStringBuilder, CharSequence charSequence, TtsSpan ttsSpan) {
        int length = spannableStringBuilder.length();
        spannableStringBuilder.append(charSequence);
        spannableStringBuilder.setSpan(ttsSpan, length, spannableStringBuilder.length(), 0);
    }

    private static String formatDigits(int i, int i2, String str) {
        int i3 = i / 10;
        int i4 = i % 10;
        StringBuilder sb = new StringBuilder(i2);
        if (i >= 10 || i2 == 2) {
            sb.append(str.charAt(i3));
        }
        sb.append(str.charAt(i4));
        return sb.toString();
    }

    public static CharSequence getGmtOffsetText(TimeZoneFormat timeZoneFormat, Locale locale, TimeZone timeZone, Date date) {
        String str;
        String str2;
        TimeZoneFormat.GMTOffsetPatternType gMTOffsetPatternType;
        int i;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String gMTPattern = timeZoneFormat.getGMTPattern();
        int indexOf = gMTPattern.indexOf("{0}");
        boolean z = false;
        if (indexOf == -1) {
            str2 = "GMT";
            str = "";
        } else {
            String substring = gMTPattern.substring(0, indexOf);
            str = gMTPattern.substring(indexOf + 3);
            str2 = substring;
        }
        if (!str2.isEmpty()) {
            appendWithTtsSpan(spannableStringBuilder, str2, new TtsSpan.TextBuilder(str2).build());
        }
        int offset = timeZone.getOffset(date.getTime());
        if (offset < 0) {
            offset = -offset;
            gMTOffsetPatternType = TimeZoneFormat.GMTOffsetPatternType.NEGATIVE_HM;
        } else {
            gMTOffsetPatternType = TimeZoneFormat.GMTOffsetPatternType.POSITIVE_HM;
        }
        String gMTOffsetPattern = timeZoneFormat.getGMTOffsetPattern(gMTOffsetPatternType);
        String gMTOffsetDigits = timeZoneFormat.getGMTOffsetDigits();
        long j = offset;
        r7 = (int) (j / 3600000);
        int i2 = Math.abs((int) (j / 60000)) % 60;
        int i3 = 0;
        while (i3 < gMTOffsetPattern.length()) {
            char charAt = gMTOffsetPattern.charAt(i3);
            if (charAt == '+' || charAt == '-' || charAt == 8722) {
                String valueOf = String.valueOf(charAt);
                appendWithTtsSpan(spannableStringBuilder, valueOf, new TtsSpan.VerbatimBuilder(valueOf).build());
            } else if (charAt == 'H' || charAt == 'm') {
                i3++;
                if (i3 >= gMTOffsetPattern.length() || gMTOffsetPattern.charAt(i3) != charAt) {
                    i3 = i3;
                    i = 1;
                } else {
                    i = 2;
                }
                appendWithTtsSpan(spannableStringBuilder, formatDigits(i2, i, gMTOffsetDigits), new TtsSpan.MeasureBuilder().setNumber(i2).setUnit(charAt == 'H' ? "hour" : "minute").build());
            } else {
                spannableStringBuilder.append(charAt);
            }
            i3++;
        }
        if (!str.isEmpty()) {
            appendWithTtsSpan(spannableStringBuilder, str, new TtsSpan.TextBuilder(str).build());
        }
        SpannableString spannableString = new SpannableString(spannableStringBuilder);
        BidiFormatter instance = BidiFormatter.getInstance();
        if (TextUtils.getLayoutDirectionFromLocale(locale) == 1) {
            z = true;
        }
        return instance.unicodeWrap(spannableString, z ? TextDirectionHeuristicsCompat.RTL : TextDirectionHeuristicsCompat.LTR);
    }

    /* loaded from: classes.dex */
    public static final class ZoneGetterData {
        public List<String> lookupTimeZoneIdsByCountry(String str) {
            CountryTimeZones lookupCountryTimeZones = TimeZoneFinder.getInstance().lookupCountryTimeZones(str);
            if (lookupCountryTimeZones == null) {
                return null;
            }
            return extractTimeZoneIds(lookupCountryTimeZones.getTimeZoneMappings());
        }

        private static List<String> extractTimeZoneIds(List<CountryTimeZones.TimeZoneMapping> list) {
            ArrayList arrayList = new ArrayList(list.size());
            for (CountryTimeZones.TimeZoneMapping timeZoneMapping : list) {
                arrayList.add(timeZoneMapping.getTimeZoneId());
            }
            return Collections.unmodifiableList(arrayList);
        }
    }
}
