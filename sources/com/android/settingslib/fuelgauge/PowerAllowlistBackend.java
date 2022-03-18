package com.android.settingslib.fuelgauge;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.IDeviceIdleController;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telecom.DefaultDialerManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.telephony.SmsApplication;
import com.android.internal.util.ArrayUtils;
/* loaded from: classes.dex */
public class PowerAllowlistBackend {
    private static PowerAllowlistBackend sInstance;
    private final ArraySet<String> mAllowlistedApps;
    private final Context mAppContext;
    private final ArraySet<String> mDefaultActiveApps;
    private final IDeviceIdleController mDeviceIdleService;
    private final ArraySet<String> mSysAllowlistedApps;

    public PowerAllowlistBackend(Context context) {
        this(context, IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle")));
    }

    PowerAllowlistBackend(Context context, IDeviceIdleController iDeviceIdleController) {
        this.mAllowlistedApps = new ArraySet<>();
        this.mSysAllowlistedApps = new ArraySet<>();
        this.mDefaultActiveApps = new ArraySet<>();
        this.mAppContext = context.getApplicationContext();
        this.mDeviceIdleService = iDeviceIdleController;
        refreshList();
    }

    public boolean isSysAllowlisted(String str) {
        return this.mSysAllowlistedApps.contains(str);
    }

    public boolean isAllowlisted(String str) {
        return this.mAllowlistedApps.contains(str) || isDefaultActiveApp(str);
    }

    public boolean isDefaultActiveApp(String str) {
        return this.mDefaultActiveApps.contains(str) || ((DevicePolicyManager) this.mAppContext.getSystemService(DevicePolicyManager.class)).packageHasActiveAdmins(str);
    }

    public boolean isAllowlisted(String[] strArr) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        for (String str : strArr) {
            if (isAllowlisted(str)) {
                return true;
            }
        }
        return false;
    }

    public void addApp(String str) {
        try {
            this.mDeviceIdleService.addPowerSaveWhitelistApp(str);
            this.mAllowlistedApps.add(str);
        } catch (RemoteException e) {
            Log.w("PowerAllowlistBackend", "Unable to reach IDeviceIdleController", e);
        }
    }

    public void removeApp(String str) {
        try {
            this.mDeviceIdleService.removePowerSaveWhitelistApp(str);
            this.mAllowlistedApps.remove(str);
        } catch (RemoteException e) {
            Log.w("PowerAllowlistBackend", "Unable to reach IDeviceIdleController", e);
        }
    }

    public void refreshList() {
        this.mSysAllowlistedApps.clear();
        this.mAllowlistedApps.clear();
        this.mDefaultActiveApps.clear();
        IDeviceIdleController iDeviceIdleController = this.mDeviceIdleService;
        if (iDeviceIdleController != null) {
            try {
                for (String str : iDeviceIdleController.getFullPowerWhitelist()) {
                    this.mAllowlistedApps.add(str);
                }
                for (String str2 : this.mDeviceIdleService.getSystemPowerWhitelist()) {
                    this.mSysAllowlistedApps.add(str2);
                }
                boolean hasSystemFeature = this.mAppContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
                ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(this.mAppContext, true);
                String defaultDialerApplication = DefaultDialerManager.getDefaultDialerApplication(this.mAppContext);
                if (hasSystemFeature) {
                    if (defaultSmsApplication != null) {
                        this.mDefaultActiveApps.add(defaultSmsApplication.getPackageName());
                    }
                    if (!TextUtils.isEmpty(defaultDialerApplication)) {
                        this.mDefaultActiveApps.add(defaultDialerApplication);
                    }
                }
            } catch (RemoteException e) {
                Log.w("PowerAllowlistBackend", "Unable to reach IDeviceIdleController", e);
            }
        }
    }

    public static PowerAllowlistBackend getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PowerAllowlistBackend(context);
        }
        return sInstance;
    }
}
