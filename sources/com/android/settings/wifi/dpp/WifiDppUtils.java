package com.android.settings.wifi.dpp;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import androidx.window.R;
import com.android.wifitrackerlib.WifiEntry;
import java.time.Duration;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class WifiDppUtils {
    private static final Duration VIBRATE_DURATION_QR_CODE_RECOGNITION = Duration.ofMillis(3);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isWifiDppEnabled(Context context) {
        return ((WifiManager) context.getSystemService(WifiManager.class)).isEasyConnectSupported();
    }

    public static Intent getEnrolleeQrCodeScannerIntent(Context context, String str) {
        Intent intent = new Intent(context, WifiDppEnrolleeActivity.class);
        intent.setAction("android.settings.WIFI_DPP_ENROLLEE_QR_CODE_SCANNER");
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra("ssid", str);
        }
        return intent;
    }

    private static String getPresharedKey(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        for (WifiConfiguration wifiConfiguration2 : wifiManager.getPrivilegedConfiguredNetworks()) {
            if (wifiConfiguration2.networkId == wifiConfiguration.networkId) {
                if (!wifiConfiguration.allowedKeyManagement.get(0) || !wifiConfiguration.allowedAuthAlgorithms.get(1)) {
                    return wifiConfiguration2.preSharedKey;
                }
                return wifiConfiguration2.wepKeys[wifiConfiguration2.wepTxKeyIndex];
            }
        }
        return wifiConfiguration.preSharedKey;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String removeFirstAndLastDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int i = 0;
        int length = str.length() - 1;
        if (str.charAt(0) == '\"') {
            i = 1;
        }
        if (str.charAt(length) == '\"') {
            length--;
        }
        return str.substring(i, length + 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSecurityString(WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.allowedKeyManagement.get(8) ? "SAE" : wifiConfiguration.allowedKeyManagement.get(9) ? "nopass" : (wifiConfiguration.allowedKeyManagement.get(1) || wifiConfiguration.allowedKeyManagement.get(4)) ? "WPA" : wifiConfiguration.wepKeys[0] == null ? "nopass" : "WEP";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getSecurityString(WifiEntry wifiEntry) {
        int security = wifiEntry.getSecurity();
        return security != 1 ? security != 2 ? security != 5 ? "nopass" : "SAE" : "WPA" : "WEP";
    }

    public static Intent getConfiguratorQrCodeGeneratorIntentOrNull(Context context, WifiManager wifiManager, WifiEntry wifiEntry) {
        Intent intent = new Intent(context, WifiDppConfiguratorActivity.class);
        if (!wifiEntry.canShare()) {
            return null;
        }
        intent.setAction("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR");
        setConfiguratorIntentExtra(intent, wifiManager, wifiEntry.getWifiConfiguration());
        return intent;
    }

    public static Intent getConfiguratorQrCodeScannerIntentOrNull(Context context, WifiManager wifiManager, WifiEntry wifiEntry) {
        Intent intent = new Intent(context, WifiDppConfiguratorActivity.class);
        if (!wifiEntry.canEasyConnect()) {
            return null;
        }
        intent.setAction("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_SCANNER");
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        setConfiguratorIntentExtra(intent, wifiManager, wifiConfiguration);
        int i = wifiConfiguration.networkId;
        if (i != -1) {
            intent.putExtra("networkId", i);
            return intent;
        }
        throw new IllegalArgumentException("Invalid network ID");
    }

    public static Intent getHotspotConfiguratorIntentOrNull(Context context, WifiManager wifiManager, SoftApConfiguration softApConfiguration) {
        Intent intent = new Intent(context, WifiDppConfiguratorActivity.class);
        if (!isSupportHotspotConfiguratorQrCodeGenerator(softApConfiguration)) {
            return null;
        }
        intent.setAction("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR");
        String removeFirstAndLastDoubleQuotes = removeFirstAndLastDoubleQuotes(softApConfiguration.getSsid());
        int securityType = softApConfiguration.getSecurityType();
        String str = securityType == 3 ? "SAE" : (securityType == 1 || securityType == 2) ? "WPA" : "nopass";
        String removeFirstAndLastDoubleQuotes2 = removeFirstAndLastDoubleQuotes(softApConfiguration.getPassphrase());
        if (!TextUtils.isEmpty(removeFirstAndLastDoubleQuotes)) {
            intent.putExtra("ssid", removeFirstAndLastDoubleQuotes);
        }
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra("security", str);
        }
        if (!TextUtils.isEmpty(removeFirstAndLastDoubleQuotes2)) {
            intent.putExtra("preSharedKey", removeFirstAndLastDoubleQuotes2);
        }
        intent.putExtra("hiddenSsid", softApConfiguration.isHiddenSsid());
        intent.putExtra("networkId", -1);
        intent.putExtra("isHotspot", true);
        return intent;
    }

    private static void setConfiguratorIntentExtra(Intent intent, WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        String removeFirstAndLastDoubleQuotes = removeFirstAndLastDoubleQuotes(wifiConfiguration.SSID);
        String securityString = getSecurityString(wifiConfiguration);
        String removeFirstAndLastDoubleQuotes2 = removeFirstAndLastDoubleQuotes(getPresharedKey(wifiManager, wifiConfiguration));
        if (!TextUtils.isEmpty(removeFirstAndLastDoubleQuotes)) {
            intent.putExtra("ssid", removeFirstAndLastDoubleQuotes);
        }
        if (!TextUtils.isEmpty(securityString)) {
            intent.putExtra("security", securityString);
        }
        if (!TextUtils.isEmpty(removeFirstAndLastDoubleQuotes2)) {
            intent.putExtra("preSharedKey", removeFirstAndLastDoubleQuotes2);
        }
        intent.putExtra("hiddenSsid", wifiConfiguration.hiddenSSID);
    }

    public static void showLockScreen(Context context, final Runnable runnable) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        if (keyguardManager.isKeyguardSecure()) {
            BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() { // from class: com.android.settings.wifi.dpp.WifiDppUtils.1
                @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationError(int i, CharSequence charSequence) {
                }

                @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
                    runnable.run();
                }
            };
            BiometricPrompt.Builder title = new BiometricPrompt.Builder(context).setTitle(context.getText(R.string.wifi_dpp_lockscreen_title));
            if (keyguardManager.isDeviceSecure()) {
                title.setDeviceCredentialAllowed(true);
            }
            BiometricPrompt build = title.build();
            final Handler handler = new Handler(Looper.getMainLooper());
            build.authenticate(new CancellationSignal(), new Executor() { // from class: com.android.settings.wifi.dpp.WifiDppUtils$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable2) {
                    handler.post(runnable2);
                }
            }, authenticationCallback);
            return;
        }
        runnable.run();
    }

    public static boolean isSupportEnrolleeQrCodeScanner(Context context, int i) {
        return isSupportWifiDpp(context, i) || isSupportZxing(context, i);
    }

    private static boolean isSupportHotspotConfiguratorQrCodeGenerator(SoftApConfiguration softApConfiguration) {
        int securityType = softApConfiguration.getSecurityType();
        return securityType == 3 || securityType == 2 || securityType == 1 || securityType == 0;
    }

    private static boolean isSupportWifiDpp(Context context, int i) {
        if (!isWifiDppEnabled(context)) {
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        if (i != 2) {
            return i == 5 && wifiManager.isWpa3SaeSupported();
        }
        return true;
    }

    private static boolean isSupportZxing(Context context, int i) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        if (i == 0 || i == 1 || i == 2) {
            return true;
        }
        return i != 4 ? i == 5 && wifiManager.isWpa3SaeSupported() : wifiManager.isEnhancedOpenSupported();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void triggerVibrationForQrCodeRecognition(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION_QR_CODE_RECOGNITION.toMillis(), -1));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getSecurityTypeFromWifiConfiguration(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return 5;
        }
        if (wifiConfiguration.allowedKeyManagement.get(1)) {
            return 2;
        }
        if (wifiConfiguration.allowedKeyManagement.get(10)) {
            return 6;
        }
        if (wifiConfiguration.allowedKeyManagement.get(2) || wifiConfiguration.allowedKeyManagement.get(3)) {
            return 3;
        }
        if (wifiConfiguration.allowedKeyManagement.get(9)) {
            return 4;
        }
        return wifiConfiguration.wepKeys[0] != null ? 1 : 0;
    }
}
