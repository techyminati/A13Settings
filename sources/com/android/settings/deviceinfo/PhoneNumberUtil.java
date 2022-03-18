package com.android.settings.deviceinfo;

import android.text.SpannableStringBuilder;
import android.text.style.TtsSpan;
/* loaded from: classes.dex */
public class PhoneNumberUtil {
    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isPhoneNumberDigit(int i) {
        return (i >= 48 && i <= 57) || i == 45 || i == 43 || i == 40 || i == 41;
    }

    public static CharSequence expandByTts(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() <= 0 || !isPhoneNumberDigits(charSequence)) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        spannableStringBuilder.setSpan(new TtsSpan.DigitsBuilder(charSequence.toString()).build(), 0, spannableStringBuilder.length(), 33);
        return spannableStringBuilder;
    }

    private static boolean isPhoneNumberDigits(CharSequence charSequence) {
        return ((long) charSequence.length()) == charSequence.chars().filter(PhoneNumberUtil$$ExternalSyntheticLambda0.INSTANCE).count();
    }
}
