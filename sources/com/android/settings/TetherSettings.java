package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.window.R;
import com.android.settings.TetherSettings;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.tether.WifiTetherPreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.TetherUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class TetherSettings extends RestrictedSettingsFragment implements DataSaverBackend.Listener {
    static final String KEY_ENABLE_BLUETOOTH_TETHERING = "enable_bluetooth_tethering";
    static final String KEY_TETHER_PREFS_SCREEN = "tether_prefs_screen";
    static final String KEY_TETHER_PREFS_TOP_INTRO = "tether_prefs_top_intro";
    static final String KEY_USB_TETHER_SETTINGS = "usb_tether_settings";
    static final String KEY_WIFI_TETHER = "wifi_tether";
    private boolean mBluetoothEnableForTether;
    private String[] mBluetoothRegexs;
    private SwitchPreference mBluetoothTether;
    private ConnectivityManager mCm;
    Context mContext;
    private DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private Preference mDataSaverFooter;
    private EthernetManager mEm;
    private EthernetListener mEthernetListener;
    private String mEthernetRegex;
    private SwitchPreference mEthernetTether;
    private boolean mMassStorageActive;
    private OnStartTetheringCallback mStartTetheringCallback;
    private BroadcastReceiver mTetherChangeReceiver;
    private TetheringEventCallback mTetheringEventCallback;
    TetheringManager mTm;
    private boolean mUnavailable;
    private boolean mUsbConnected;
    String[] mUsbRegexs;
    private RestrictedSwitchPreference mUsbTether;
    private WifiTetherPreferenceController mWifiTetherPreferenceController;
    private static final boolean DEBUG = Log.isLoggable("TetheringSettings", 3);
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.TetherSettings.2
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R.xml.tether_prefs;
            return Arrays.asList(searchIndexableResource);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            TetheringManager tetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
            if (!TetherUtil.isTetherAvailable(context)) {
                nonIndexableKeys.add(TetherSettings.KEY_TETHER_PREFS_SCREEN);
                nonIndexableKeys.add(TetherSettings.KEY_WIFI_TETHER);
            }
            boolean z = false;
            if (!(tetheringManager.getTetherableUsbRegexs().length != 0) || Utils.isMonkeyRunning()) {
                nonIndexableKeys.add(TetherSettings.KEY_USB_TETHER_SETTINGS);
            }
            if (tetheringManager.getTetherableBluetoothRegexs().length != 0) {
                z = true;
            }
            if (!z) {
                nonIndexableKeys.add(TetherSettings.KEY_ENABLE_BLUETOOTH_TETHERING);
            }
            if (!(!TextUtils.isEmpty(context.getResources().getString(17039958)))) {
                nonIndexableKeys.add("enable_ethernet_tethering");
            }
            return nonIndexableKeys;
        }
    };
    private AtomicReference<BluetoothPan> mBluetoothPan = new AtomicReference<>();
    private Handler mHandler = new Handler();
    private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.TetherSettings.1
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            TetherSettings.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            TetherSettings.this.mBluetoothPan.set(null);
        }
    };

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_tether;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 90;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    public TetherSettings() {
        super("no_config_tethering");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWifiTetherPreferenceController = new WifiTetherPreferenceController(context, getSettingsLifecycle());
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.tether_prefs);
        Context context = getContext();
        this.mContext = context;
        DataSaverBackend dataSaverBackend = new DataSaverBackend(context);
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mDataSaverFooter = findPreference("disabled_on_data_saver");
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted()) {
            this.mUnavailable = true;
            getPreferenceScreen().removeAll();
            return;
        }
        FragmentActivity activity = getActivity();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(activity.getApplicationContext(), this.mProfileServiceListener, 5);
        }
        setupTetherPreference();
        setTopIntroPreferenceTitle();
        this.mDataSaverBackend.addListener(this);
        this.mCm = (ConnectivityManager) getSystemService("connectivity");
        this.mEm = (EthernetManager) getSystemService("ethernet");
        TetheringManager tetheringManager = (TetheringManager) getSystemService("tethering");
        this.mTm = tetheringManager;
        this.mUsbRegexs = tetheringManager.getTetherableUsbRegexs();
        this.mBluetoothRegexs = this.mTm.getTetherableBluetoothRegexs();
        String string = this.mContext.getResources().getString(17039958);
        this.mEthernetRegex = string;
        boolean z = this.mUsbRegexs.length != 0;
        boolean z2 = (defaultAdapter == null || this.mBluetoothRegexs.length == 0) ? false : true;
        boolean z3 = !TextUtils.isEmpty(string);
        if (!z || Utils.isMonkeyRunning()) {
            getPreferenceScreen().removePreference(this.mUsbTether);
        }
        this.mWifiTetherPreferenceController.displayPreference(getPreferenceScreen());
        if (!z2) {
            getPreferenceScreen().removePreference(this.mBluetoothTether);
        } else {
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            if (bluetoothPan == null || !bluetoothPan.isTetheringOn()) {
                this.mBluetoothTether.setChecked(false);
            } else {
                this.mBluetoothTether.setChecked(true);
            }
        }
        if (!z3) {
            getPreferenceScreen().removePreference(this.mEthernetTether);
        }
        onDataSaverChanged(this.mDataSaverBackend.isDataSaverEnabled());
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mDataSaverBackend.remListener(this);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothProfile bluetoothProfile = (BluetoothProfile) this.mBluetoothPan.getAndSet(null);
        if (!(bluetoothProfile == null || defaultAdapter == null)) {
            defaultAdapter.closeProfileProxy(5, bluetoothProfile);
        }
        super.onDestroy();
    }

    void setupTetherPreference() {
        this.mUsbTether = (RestrictedSwitchPreference) findPreference(KEY_USB_TETHER_SETTINGS);
        this.mBluetoothTether = (SwitchPreference) findPreference(KEY_ENABLE_BLUETOOTH_TETHERING);
        this.mEthernetTether = (SwitchPreference) findPreference("enable_ethernet_tethering");
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        this.mUsbTether.setEnabled(!z);
        this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
        this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
        this.mDataSaverFooter.setVisible(this.mDataSaverEnabled);
    }

    void setTopIntroPreferenceTitle() {
        Preference findPreference = findPreference(KEY_TETHER_PREFS_TOP_INTRO);
        if (((WifiManager) this.mContext.getSystemService(WifiManager.class)).isStaApConcurrencySupported()) {
            findPreference.setTitle(R.string.tethering_footer_info_sta_ap_concurrency);
        } else {
            findPreference.setTitle(R.string.tethering_footer_info);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TetherChangeReceiver extends BroadcastReceiver {
        private TetherChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TetherSettings.DEBUG) {
                Log.d("TetheringSettings", "onReceive() action : " + action);
            }
            if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {
                ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("availableArray");
                ArrayList<String> stringArrayListExtra2 = intent.getStringArrayListExtra("tetherArray");
                TetherSettings.this.updateBluetoothState();
                TetherSettings.this.updateEthernetState((String[]) stringArrayListExtra.toArray(new String[stringArrayListExtra.size()]), (String[]) stringArrayListExtra2.toArray(new String[stringArrayListExtra2.size()]));
            } else if (action.equals("android.intent.action.MEDIA_SHARED")) {
                TetherSettings.this.mMassStorageActive = true;
                TetherSettings.this.updateBluetoothAndEthernetState();
                TetherSettings.this.updateUsbPreference();
            } else if (action.equals("android.intent.action.MEDIA_UNSHARED")) {
                TetherSettings.this.mMassStorageActive = false;
                TetherSettings.this.updateBluetoothAndEthernetState();
                TetherSettings.this.updateUsbPreference();
            } else if (action.equals("android.hardware.usb.action.USB_STATE")) {
                TetherSettings.this.mUsbConnected = intent.getBooleanExtra("connected", false);
                TetherSettings.this.updateBluetoothAndEthernetState();
                TetherSettings.this.updateUsbPreference();
            } else if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                if (TetherSettings.this.mBluetoothEnableForTether) {
                    int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                    if (intExtra == Integer.MIN_VALUE || intExtra == 10) {
                        TetherSettings.this.mBluetoothEnableForTether = false;
                    } else if (intExtra == 12) {
                        TetherSettings.this.startTethering(2);
                        TetherSettings.this.mBluetoothEnableForTether = false;
                    }
                }
                TetherSettings.this.updateBluetoothAndEthernetState();
            } else if (action.equals("android.bluetooth.action.TETHERING_STATE_CHANGED")) {
                TetherSettings.this.updateBluetoothAndEthernetState();
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mUnavailable) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R.string.tethering_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        this.mStartTetheringCallback = new OnStartTetheringCallback(this);
        this.mTetheringEventCallback = new TetheringEventCallback();
        this.mTm.registerTetheringEventCallback(new Executor() { // from class: com.android.settings.TetherSettings$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                TetherSettings.this.lambda$onStart$0(runnable);
            }
        }, this.mTetheringEventCallback);
        this.mMassStorageActive = "shared".equals(Environment.getExternalStorageState());
        registerReceiver();
        EthernetListener ethernetListener = new EthernetListener();
        this.mEthernetListener = ethernetListener;
        EthernetManager ethernetManager = this.mEm;
        if (ethernetManager != null) {
            ethernetManager.addListener(ethernetListener, new Executor() { // from class: com.android.settings.TetherSettings$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    TetherSettings.this.lambda$onStart$1(runnable);
                }
            });
        }
        updateUsbState();
        updateBluetoothAndEthernetState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$1(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (!this.mUnavailable) {
            getActivity().unregisterReceiver(this.mTetherChangeReceiver);
            this.mTm.unregisterTetheringEventCallback(this.mTetheringEventCallback);
            EthernetManager ethernetManager = this.mEm;
            if (ethernetManager != null) {
                ethernetManager.removeListener(this.mEthernetListener);
            }
            this.mTetherChangeReceiver = null;
            this.mStartTetheringCallback = null;
            this.mTetheringEventCallback = null;
            this.mEthernetListener = null;
        }
    }

    void registerReceiver() {
        FragmentActivity activity = getActivity();
        this.mTetherChangeReceiver = new TetherChangeReceiver();
        Intent registerReceiver = activity.registerReceiver(this.mTetherChangeReceiver, new IntentFilter("android.net.conn.TETHER_STATE_CHANGED"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter2.addAction("android.intent.action.MEDIA_UNSHARED");
        intentFilter2.addDataScheme("file");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter3.addAction("android.bluetooth.action.TETHERING_STATE_CHANGED");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter3);
        if (registerReceiver != null) {
            this.mTetherChangeReceiver.onReceive(activity, registerReceiver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBluetoothAndEthernetState() {
        updateBluetoothAndEthernetState(this.mTm.getTetheredIfaces());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBluetoothAndEthernetState(String[] strArr) {
        String[] tetherableIfaces = this.mTm.getTetherableIfaces();
        updateBluetoothState();
        updateEthernetState(tetherableIfaces, strArr);
    }

    private void updateUsbState() {
        updateUsbState(this.mTm.getTetheredIfaces());
    }

    void updateUsbState(String[] strArr) {
        boolean z = false;
        for (String str : strArr) {
            for (String str2 : this.mUsbRegexs) {
                if (str.matches(str2)) {
                    z = true;
                }
            }
        }
        if (DEBUG) {
            Log.d("TetheringSettings", "updateUsbState() mUsbConnected : " + this.mUsbConnected + ", mMassStorageActive : " + this.mMassStorageActive + ", usbTethered : " + z);
        }
        if (z) {
            this.mUsbTether.setEnabled(!this.mDataSaverEnabled);
            this.mUsbTether.setChecked(true);
            this.mUsbTether.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(this.mContext, UserHandle.myUserId()));
            return;
        }
        this.mUsbTether.setChecked(false);
        updateUsbPreference();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUsbPreference() {
        boolean z = this.mUsbConnected && !this.mMassStorageActive;
        RestrictedLockUtils.EnforcedAdmin checkIfUsbDataSignalingIsDisabled = RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(this.mContext, UserHandle.myUserId());
        if (checkIfUsbDataSignalingIsDisabled != null) {
            this.mUsbTether.setDisabledByAdmin(checkIfUsbDataSignalingIsDisabled);
        } else if (z) {
            this.mUsbTether.setEnabled(!this.mDataSaverEnabled);
        } else {
            this.mUsbTether.setEnabled(false);
        }
    }

    int getBluetoothState() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            return Integer.MIN_VALUE;
        }
        return defaultAdapter.getState();
    }

    boolean isBluetoothTetheringOn() {
        BluetoothPan bluetoothPan = this.mBluetoothPan.get();
        return bluetoothPan != null && bluetoothPan.isTetheringOn();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBluetoothState() {
        int bluetoothState = getBluetoothState();
        if (DEBUG) {
            Log.d("TetheringSettings", "updateBluetoothState() btState : " + bluetoothState);
        }
        if (bluetoothState == Integer.MIN_VALUE) {
            Log.w("TetheringSettings", "updateBluetoothState() Bluetooth state is error!");
        } else if (bluetoothState == 13) {
            this.mBluetoothTether.setEnabled(false);
        } else if (bluetoothState == 11) {
            this.mBluetoothTether.setEnabled(false);
        } else if (bluetoothState != 12 || !isBluetoothTetheringOn()) {
            this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
            this.mBluetoothTether.setChecked(false);
        } else {
            this.mBluetoothTether.setChecked(true);
            this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
        }
    }

    void updateEthernetState(String[] strArr, String[] strArr2) {
        EthernetManager ethernetManager;
        boolean z = false;
        for (String str : strArr) {
            if (str.matches(this.mEthernetRegex)) {
                z = true;
            }
        }
        boolean z2 = false;
        for (String str2 : strArr2) {
            if (str2.matches(this.mEthernetRegex)) {
                z2 = true;
            }
        }
        if (DEBUG) {
            Log.d("TetheringSettings", "updateEthernetState() isAvailable : " + z + ", isTethered : " + z2);
        }
        if (z2) {
            this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
            this.mEthernetTether.setChecked(true);
        } else if (z || ((ethernetManager = this.mEm) != null && ethernetManager.isAvailable())) {
            this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
            this.mEthernetTether.setChecked(false);
        } else {
            this.mEthernetTether.setEnabled(false);
            this.mEthernetTether.setChecked(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTethering(int i) {
        if (i == 2) {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter.getState() == 10) {
                this.mBluetoothEnableForTether = true;
                defaultAdapter.enable();
                this.mBluetoothTether.setEnabled(false);
                return;
            }
        }
        this.mCm.startTethering(i, true, this.mStartTetheringCallback, this.mHandler);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        RestrictedSwitchPreference restrictedSwitchPreference = this.mUsbTether;
        if (preference != restrictedSwitchPreference) {
            SwitchPreference switchPreference = this.mBluetoothTether;
            if (preference != switchPreference) {
                SwitchPreference switchPreference2 = this.mEthernetTether;
                if (preference == switchPreference2) {
                    if (switchPreference2.isChecked()) {
                        startTethering(5);
                    } else {
                        this.mCm.stopTethering(5);
                    }
                }
            } else if (switchPreference.isChecked()) {
                startTethering(2);
            } else {
                this.mCm.stopTethering(2);
            }
        } else if (restrictedSwitchPreference.isChecked()) {
            startTethering(1);
        } else {
            this.mCm.stopTethering(1);
        }
        return super.onPreferenceTreeClick(preference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        final WeakReference<TetherSettings> mTetherSettings;

        OnStartTetheringCallback(TetherSettings tetherSettings) {
            this.mTetherSettings = new WeakReference<>(tetherSettings);
        }

        public void onTetheringStarted() {
            update();
        }

        public void onTetheringFailed() {
            update();
        }

        private void update() {
            TetherSettings tetherSettings = this.mTetherSettings.get();
            if (tetherSettings != null) {
                tetherSettings.updateBluetoothAndEthernetState();
            }
        }
    }

    /* loaded from: classes.dex */
    private final class TetheringEventCallback implements TetheringManager.TetheringEventCallback {
        private TetheringEventCallback() {
        }

        public void onTetheredInterfacesChanged(List<String> list) {
            Log.d("TetheringSettings", "onTetheredInterfacesChanged() interfaces : " + list.toString());
            String[] strArr = (String[]) list.toArray(new String[list.size()]);
            TetherSettings.this.updateUsbState(strArr);
            TetherSettings.this.updateBluetoothAndEthernetState(strArr);
        }
    }

    /* loaded from: classes.dex */
    private final class EthernetListener implements EthernetManager.Listener {
        private EthernetListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAvailabilityChanged$0() {
            TetherSettings.this.updateBluetoothAndEthernetState();
        }

        public void onAvailabilityChanged(String str, boolean z) {
            TetherSettings.this.mHandler.post(new Runnable() { // from class: com.android.settings.TetherSettings$EthernetListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TetherSettings.EthernetListener.this.lambda$onAvailabilityChanged$0();
                }
            });
        }
    }
}
