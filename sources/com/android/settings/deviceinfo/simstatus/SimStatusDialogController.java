package com.android.settings.deviceinfo.simstatus;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.telephony.CarrierConfigManager;
import android.telephony.CellSignalStrength;
import android.telephony.ICellBroadcastService;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.window.R;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class SimStatusDialogController implements LifecycleObserver {
    static final int CELLULAR_NETWORK_STATE = 2131558828;
    static final int CELL_DATA_NETWORK_TYPE_VALUE_ID = 2131558825;
    static final int CELL_VOICE_NETWORK_TYPE_VALUE_ID = 2131560026;
    static final int EID_INFO_LABEL_ID = 2131558941;
    static final int EID_INFO_VALUE_ID = 2131558942;
    static final int ICCID_INFO_LABEL_ID = 2131559100;
    static final int ICCID_INFO_VALUE_ID = 2131559101;
    static final int IMS_REGISTRATION_STATE_LABEL_ID = 2131559142;
    static final int IMS_REGISTRATION_STATE_VALUE_ID = 2131559143;
    static final int MAX_PHONE_COUNT_SINGLE_SIM = 1;
    static final int NETWORK_PROVIDER_VALUE_ID = 2131559455;
    static final int OPERATOR_INFO_LABEL_ID = 2131559224;
    static final int OPERATOR_INFO_VALUE_ID = 2131559225;
    static final int PHONE_NUMBER_VALUE_ID = 2131559441;
    static final int ROAMING_INFO_VALUE_ID = 2131559629;
    static final int SERVICE_STATE_VALUE_ID = 2131559691;
    static final int SIGNAL_STRENGTH_LABEL_ID = 2131559722;
    static final int SIGNAL_STRENGTH_VALUE_ID = 2131559723;
    private final CarrierConfigManager mCarrierConfigManager;
    private CellBroadcastServiceConnection mCellBroadcastServiceConnection;
    private final Context mContext;
    private final SimStatusDialogFragment mDialog;
    private final EuiccManager mEuiccManager;
    private ServiceState mPreviousServiceState;
    private final Resources mRes;
    private boolean mShowLatestAreaInfo;
    private final int mSlotIndex;
    private SubscriptionInfo mSubscriptionInfo;
    private final SubscriptionManager mSubscriptionManager;
    protected SimStatusDialogTelephonyCallback mTelephonyCallback;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    private TelephonyManager mTelephonyManager;
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.1
        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            int i = -1;
            int subscriptionId = SimStatusDialogController.this.mSubscriptionInfo != null ? SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId() : -1;
            SimStatusDialogController simStatusDialogController = SimStatusDialogController.this;
            simStatusDialogController.mSubscriptionInfo = simStatusDialogController.getPhoneSubscriptionInfo(simStatusDialogController.mSlotIndex);
            if (SimStatusDialogController.this.mSubscriptionInfo != null) {
                i = SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId();
            }
            if (subscriptionId != i) {
                if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
                    SimStatusDialogController.this.unregisterImsRegistrationCallback(subscriptionId);
                }
                if (SubscriptionManager.isValidSubscriptionId(i)) {
                    SimStatusDialogController simStatusDialogController2 = SimStatusDialogController.this;
                    simStatusDialogController2.mTelephonyManager = simStatusDialogController2.getTelephonyManager().createForSubscriptionId(i);
                    SimStatusDialogController.this.registerImsRegistrationCallback(i);
                }
            }
            SimStatusDialogController.this.updateSubscriptionStatus();
        }
    };
    private boolean mIsRegisteredListener = false;
    private final BroadcastReceiver mAreaInfoReceiver = new BroadcastReceiver() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.telephony.action.AREA_INFO_UPDATED".equals(intent.getAction()) && intent.getIntExtra("android.telephony.extra.SLOT_INDEX", 0) == SimStatusDialogController.this.mSlotIndex) {
                SimStatusDialogController.this.updateAreaInfoText();
            }
        }
    };
    private ImsMmTelManager.RegistrationCallback mImsRegStateCallback = new ImsMmTelManager.RegistrationCallback() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.3
        public void onRegistered(int i) {
            SimStatusDialogController.this.mDialog.setText(R.id.ims_reg_state_value, SimStatusDialogController.this.mRes.getString(R.string.ims_reg_status_registered));
        }

        public void onRegistering(int i) {
            SimStatusDialogController.this.mDialog.setText(R.id.ims_reg_state_value, SimStatusDialogController.this.mRes.getString(R.string.ims_reg_status_not_registered));
        }

        public void onUnregistered(ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(R.id.ims_reg_state_value, SimStatusDialogController.this.mRes.getString(R.string.ims_reg_status_not_registered));
        }

        public void onTechnologyChangeFailed(int i, ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(R.id.ims_reg_state_value, SimStatusDialogController.this.mRes.getString(R.string.ims_reg_status_not_registered));
        }
    };

    static String getNetworkTypeName(int i) {
        switch (i) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "CDMA - EvDo rev. 0";
            case 6:
                return "CDMA - EvDo rev. A";
            case 7:
                return "CDMA - 1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDEN";
            case 12:
                return "CDMA - EvDo rev. B";
            case 13:
                return "LTE";
            case 14:
                return "CDMA - eHRPD";
            case 15:
                return "HSPA+";
            case 16:
                return "GSM";
            case 17:
                return "TD_SCDMA";
            case 18:
                return "IWLAN";
            case 19:
            default:
                return "UNKNOWN";
            case 20:
                return "NR SA";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CellBroadcastServiceConnection implements ServiceConnection {
        private IBinder mService;

        private CellBroadcastServiceConnection() {
        }

        public IBinder getService() {
            return this.mService;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("SimStatusDialogCtrl", "connected to CellBroadcastService");
            this.mService = iBinder;
            SimStatusDialogController.this.updateAreaInfoText();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "mICellBroadcastService has disconnected unexpectedly");
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Binding died");
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Null binding");
        }
    }

    public SimStatusDialogController(SimStatusDialogFragment simStatusDialogFragment, Lifecycle lifecycle, int i) {
        this.mDialog = simStatusDialogFragment;
        Context context = simStatusDialogFragment.getContext();
        this.mContext = context;
        this.mSlotIndex = i;
        this.mSubscriptionInfo = getPhoneSubscriptionInfo(i);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mEuiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mRes = context.getResources();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    public TelephonyManager getTelephonyManager() {
        return this.mTelephonyManager;
    }

    public void initialize() {
        requestForUpdateEid();
        if (this.mSubscriptionInfo != null) {
            this.mTelephonyManager = getTelephonyManager().createForSubscriptionId(this.mSubscriptionInfo.getSubscriptionId());
            this.mTelephonyCallback = new SimStatusDialogTelephonyCallback();
            updateLatestAreaInfo();
            updateSubscriptionStatus();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSubscriptionStatus() {
        updateNetworkProvider();
        ServiceState serviceState = getTelephonyManager().getServiceState();
        SignalStrength signalStrength = getTelephonyManager().getSignalStrength();
        updatePhoneNumber();
        updateServiceState(serviceState);
        updateSignalStrength(signalStrength);
        updateNetworkType();
        updateRoamingStatus(serviceState);
        updateIccidNumber();
        updateImsRegistrationState();
    }

    public void deinitialize() {
        if (this.mShowLatestAreaInfo) {
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (!(cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() == null)) {
                this.mContext.unbindService(this.mCellBroadcastServiceConnection);
            }
            this.mCellBroadcastServiceConnection = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mSubscriptionInfo != null) {
            this.mTelephonyManager = getTelephonyManager().createForSubscriptionId(this.mSubscriptionInfo.getSubscriptionId());
            getTelephonyManager().registerTelephonyCallback(this.mContext.getMainExecutor(), this.mTelephonyCallback);
            this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mContext.getMainExecutor(), this.mOnSubscriptionsChangedListener);
            registerImsRegistrationCallback(this.mSubscriptionInfo.getSubscriptionId());
            if (this.mShowLatestAreaInfo) {
                updateAreaInfoText();
                this.mContext.registerReceiver(this.mAreaInfoReceiver, new IntentFilter("android.telephony.action.AREA_INFO_UPDATED"));
            }
            this.mIsRegisteredListener = true;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo != null) {
            unregisterImsRegistrationCallback(subscriptionInfo.getSubscriptionId());
            this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
            getTelephonyManager().unregisterTelephonyCallback(this.mTelephonyCallback);
            if (this.mShowLatestAreaInfo) {
                this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
            }
        } else if (this.mIsRegisteredListener) {
            this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
            getTelephonyManager().unregisterTelephonyCallback(this.mTelephonyCallback);
            if (this.mShowLatestAreaInfo) {
                this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
            }
            this.mIsRegisteredListener = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkProvider() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        this.mDialog.setText(R.id.operator_name_value, subscriptionInfo != null ? subscriptionInfo.getCarrierName() : null);
    }

    public void updatePhoneNumber() {
        this.mDialog.setText(R.id.number_value, DeviceInfoUtils.getBidiFormattedPhoneNumber(this.mContext, this.mSubscriptionInfo));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDataState(int i) {
        String str;
        if (i == 0) {
            str = this.mRes.getString(R.string.radioInfo_data_disconnected);
        } else if (i == 1) {
            str = this.mRes.getString(R.string.radioInfo_data_connecting);
        } else if (i == 2) {
            str = this.mRes.getString(R.string.radioInfo_data_connected);
        } else if (i != 3) {
            str = this.mRes.getString(R.string.radioInfo_unknown);
        } else {
            str = this.mRes.getString(R.string.radioInfo_data_suspended);
        }
        this.mDialog.setText(R.id.data_state_value, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAreaInfoText() {
        CellBroadcastServiceConnection cellBroadcastServiceConnection;
        ICellBroadcastService asInterface;
        if (this.mShowLatestAreaInfo && (cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection) != null && (asInterface = ICellBroadcastService.Stub.asInterface(cellBroadcastServiceConnection.getService())) != null) {
            try {
                this.mDialog.setText(R.id.latest_area_info_value, asInterface.getCellBroadcastAreaInfo(this.mSlotIndex));
            } catch (RemoteException e) {
                Log.d("SimStatusDialogCtrl", "Can't get area info. e=" + e);
            }
        }
    }

    private void bindCellBroadcastService() {
        this.mCellBroadcastServiceConnection = new CellBroadcastServiceConnection();
        Intent intent = new Intent("android.telephony.CellBroadcastService");
        String cellBroadcastServicePackage = getCellBroadcastServicePackage();
        if (!TextUtils.isEmpty(cellBroadcastServicePackage)) {
            intent.setPackage(cellBroadcastServicePackage);
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (cellBroadcastServiceConnection == null || cellBroadcastServiceConnection.getService() != null) {
                Log.d("SimStatusDialogCtrl", "skipping bindService because connection already exists");
            } else if (!this.mContext.bindService(intent, this.mCellBroadcastServiceConnection, 1)) {
                Log.e("SimStatusDialogCtrl", "Unable to bind to service");
            }
        }
    }

    private String getCellBroadcastServicePackage() {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.telephony.CellBroadcastService"), 1048576);
        if (queryIntentServices.size() != 1) {
            Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found " + queryIntentServices.size() + " CBS packages");
        }
        for (ResolveInfo resolveInfo : queryIntentServices) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                String str = serviceInfo.packageName;
                if (TextUtils.isEmpty(str)) {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found a CBS package but packageName is null/empty");
                } else if (packageManager.checkPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", str) == 0) {
                    Log.d("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str);
                    return str;
                } else {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str + " does not have READ_PRIVILEGED_PHONE_STATE permission");
                }
            }
        }
        Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: package name not found");
        return null;
    }

    private void updateLatestAreaInfo() {
        boolean z = Resources.getSystem().getBoolean(17891734) && getTelephonyManager().getPhoneType() != 2;
        this.mShowLatestAreaInfo = z;
        if (z) {
            bindCellBroadcastService();
            return;
        }
        this.mDialog.removeSettingFromScreen(R.id.latest_area_info_label);
        this.mDialog.removeSettingFromScreen(R.id.latest_area_info_value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateServiceState(ServiceState serviceState) {
        String str;
        int combinedServiceState = Utils.getCombinedServiceState(serviceState);
        if (!Utils.isInService(serviceState)) {
            resetSignalStrength();
        } else if (!Utils.isInService(this.mPreviousServiceState)) {
            updateSignalStrength(getTelephonyManager().getSignalStrength());
        }
        if (combinedServiceState == 0) {
            str = this.mRes.getString(R.string.radioInfo_service_in);
        } else if (combinedServiceState == 1 || combinedServiceState == 2) {
            str = this.mRes.getString(R.string.radioInfo_service_out);
        } else if (combinedServiceState != 3) {
            str = this.mRes.getString(R.string.radioInfo_unknown);
        } else {
            str = this.mRes.getString(R.string.radioInfo_service_off);
        }
        this.mDialog.setText(R.id.service_state_value, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSignalStrength(SignalStrength signalStrength) {
        PersistableBundle configForSubId;
        if (signalStrength != null) {
            SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
            if (!((subscriptionInfo == null || (configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId())) == null) ? true : configForSubId.getBoolean("show_signal_strength_in_sim_status_bool"))) {
                this.mDialog.removeSettingFromScreen(R.id.signal_strength_label);
                this.mDialog.removeSettingFromScreen(R.id.signal_strength_value);
            } else if (Utils.isInService(getTelephonyManager().getServiceState())) {
                int dbm = getDbm(signalStrength);
                int asuLevel = getAsuLevel(signalStrength);
                if (dbm == -1) {
                    dbm = 0;
                }
                if (asuLevel == -1) {
                    asuLevel = 0;
                }
                this.mDialog.setText(R.id.signal_strength_value, this.mRes.getString(R.string.sim_signal_strength, Integer.valueOf(dbm), Integer.valueOf(asuLevel)));
            }
        }
    }

    private void resetSignalStrength() {
        this.mDialog.setText(R.id.signal_strength_value, "0");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkType() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        boolean z = false;
        if (subscriptionInfo == null) {
            String networkTypeName = getNetworkTypeName(0);
            this.mDialog.setText(R.id.voice_network_type_value, networkTypeName);
            this.mDialog.setText(R.id.data_network_type_value, networkTypeName);
            return;
        }
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        int dataNetworkType = getTelephonyManager().getDataNetworkType();
        int voiceNetworkType = getTelephonyManager().getVoiceNetworkType();
        TelephonyDisplayInfo telephonyDisplayInfo = this.mTelephonyDisplayInfo;
        int overrideNetworkType = telephonyDisplayInfo == null ? 0 : telephonyDisplayInfo.getOverrideNetworkType();
        String str = null;
        String networkTypeName2 = dataNetworkType != 0 ? getNetworkTypeName(dataNetworkType) : null;
        if (voiceNetworkType != 0) {
            str = getNetworkTypeName(voiceNetworkType);
        }
        boolean z2 = overrideNetworkType == 5 || overrideNetworkType == 3;
        if (dataNetworkType == 13 && z2) {
            networkTypeName2 = "NR NSA";
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionId);
        if (configForSubId != null) {
            z = configForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        }
        if (z) {
            if ("LTE".equals(networkTypeName2)) {
                networkTypeName2 = "4G";
            }
            if ("LTE".equals(str)) {
                str = "4G";
            }
        }
        this.mDialog.setText(R.id.voice_network_type_value, str);
        this.mDialog.setText(R.id.data_network_type_value, networkTypeName2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRoamingStatus(ServiceState serviceState) {
        if (serviceState == null) {
            this.mDialog.setText(R.id.roaming_state_value, this.mRes.getString(R.string.radioInfo_unknown));
        } else if (serviceState.getRoaming()) {
            this.mDialog.setText(R.id.roaming_state_value, this.mRes.getString(R.string.radioInfo_roaming_in));
        } else {
            this.mDialog.setText(R.id.roaming_state_value, this.mRes.getString(R.string.radioInfo_roaming_not));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x001e  */
    /* JADX WARN: Removed duplicated region for block: B:11:0x002c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateIccidNumber() {
        /*
            r3 = this;
            android.telephony.SubscriptionInfo r0 = r3.mSubscriptionInfo
            if (r0 == 0) goto L_0x0018
            int r0 = r0.getSubscriptionId()
            android.telephony.CarrierConfigManager r1 = r3.mCarrierConfigManager
            android.os.PersistableBundle r0 = r1.getConfigForSubId(r0)
            if (r0 == 0) goto L_0x0018
            java.lang.String r1 = "show_iccid_in_sim_status_bool"
            boolean r0 = r0.getBoolean(r1)
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            r1 = 2131559101(0x7f0d02bd, float:1.8743537E38)
            if (r0 != 0) goto L_0x002c
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r0 = r3.mDialog
            r2 = 2131559100(0x7f0d02bc, float:1.8743535E38)
            r0.removeSettingFromScreen(r2)
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r3 = r3.mDialog
            r3.removeSettingFromScreen(r1)
            goto L_0x0039
        L_0x002c:
            com.android.settings.deviceinfo.simstatus.SimStatusDialogFragment r0 = r3.mDialog
            android.telephony.TelephonyManager r3 = r3.getTelephonyManager()
            java.lang.String r3 = r3.getSimSerialNumber()
            r0.setText(r1, r3)
        L_0x0039:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.updateIccidNumber():void");
    }

    protected void requestForUpdateEid() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SimStatusDialogController.this.lambda$requestForUpdateEid$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestForUpdateEid$1() {
        final AtomicReference<String> eid = getEid(this.mSlotIndex);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SimStatusDialogController.this.lambda$requestForUpdateEid$0(eid);
            }
        });
    }

    public AtomicReference<String> getEid(int i) {
        String str;
        boolean z = true;
        z = false;
        if (getTelephonyManager().getActiveModemCount() > 1) {
            int intValue = ((Integer) this.mTelephonyManager.getLogicalToPhysicalSlotMapping().getOrDefault(Integer.valueOf(i), -1)).intValue();
            if (intValue != -1) {
                Iterator<UiccCardInfo> it = getTelephonyManager().getUiccCardsInfo().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    UiccCardInfo next = it.next();
                    if (next.getPhysicalSlotIndex() == intValue) {
                        if (next.isEuicc()) {
                            str = next.getEid();
                            if (TextUtils.isEmpty(str)) {
                                str = this.mEuiccManager.createForCardId(next.getCardId()).getEid();
                            }
                        }
                    }
                }
            }
            str = null;
        } else {
            if (this.mEuiccManager.isEnabled()) {
                str = this.mEuiccManager.getEid();
            }
            str = null;
        }
        if (z || str != null) {
            return new AtomicReference<>(str);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: updateEid */
    public void lambda$requestForUpdateEid$0(AtomicReference<String> atomicReference) {
        if (atomicReference == null) {
            this.mDialog.removeSettingFromScreen(R.id.esim_id_label);
            this.mDialog.removeSettingFromScreen(R.id.esim_id_value);
        } else if (atomicReference.get() != null) {
            this.mDialog.setText(R.id.esim_id_value, atomicReference.get());
        }
    }

    private boolean isImsRegistrationStateShowUp() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            return false;
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId());
        if (configForSubId == null) {
            return false;
        }
        return configForSubId.getBoolean("show_ims_registration_status_bool");
    }

    private void updateImsRegistrationState() {
        if (!isImsRegistrationStateShowUp()) {
            this.mDialog.removeSettingFromScreen(R.id.ims_reg_state_label);
            this.mDialog.removeSettingFromScreen(R.id.ims_reg_state_value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerImsRegistrationCallback(int i) {
        if (isImsRegistrationStateShowUp()) {
            try {
                ImsMmTelManager.createForSubscriptionId(i).registerImsRegistrationCallback(this.mDialog.getContext().getMainExecutor(), this.mImsRegStateCallback);
            } catch (ImsException e) {
                Log.w("SimStatusDialogCtrl", "fail to register IMS status for subId=" + i, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterImsRegistrationCallback(int i) {
        if (isImsRegistrationStateShowUp()) {
            ImsMmTelManager.createForSubscriptionId(i).unregisterImsRegistrationCallback(this.mImsRegStateCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubscriptionInfo getPhoneSubscriptionInfo(int i) {
        return SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(i);
    }

    private int getDbm(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
            if (cellSignalStrength.getDbm() != -1) {
                return cellSignalStrength.getDbm();
            }
        }
        return -1;
    }

    private int getAsuLevel(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
            if (cellSignalStrength.getAsuLevel() != -1) {
                return cellSignalStrength.getAsuLevel();
            }
        }
        return -1;
    }

    /* loaded from: classes.dex */
    class SimStatusDialogTelephonyCallback extends TelephonyCallback implements TelephonyCallback.DataConnectionStateListener, TelephonyCallback.SignalStrengthsListener, TelephonyCallback.ServiceStateListener, TelephonyCallback.DisplayInfoListener {
        SimStatusDialogTelephonyCallback() {
        }

        public void onDataConnectionStateChanged(int i, int i2) {
            SimStatusDialogController.this.updateDataState(i);
            SimStatusDialogController.this.updateNetworkType();
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            SimStatusDialogController.this.updateSignalStrength(signalStrength);
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            SimStatusDialogController.this.updateNetworkProvider();
            SimStatusDialogController.this.updateServiceState(serviceState);
            SimStatusDialogController.this.updateRoamingStatus(serviceState);
            SimStatusDialogController.this.mPreviousServiceState = serviceState;
        }

        public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
            SimStatusDialogController.this.mTelephonyDisplayInfo = telephonyDisplayInfo;
            SimStatusDialogController.this.updateNetworkType();
        }
    }
}
