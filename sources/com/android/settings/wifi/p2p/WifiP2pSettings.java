package com.android.settings.wifi.p2p;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.sysprop.TelephonyProperties;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class WifiP2pSettings extends DashboardFragment implements WifiP2pManager.PersistentGroupInfoListener, WifiP2pManager.PeerListListener, WifiP2pManager.DeviceInfoListener {
    static final int DIALOG_CANCEL_CONNECT = 2;
    static final int DIALOG_DELETE_GROUP = 4;
    static final int DIALOG_DISCONNECT = 1;
    static final int DIALOG_RENAME = 3;
    static final int MENU_ID_RENAME = 2;
    static final int MENU_ID_SEARCH = 1;
    static final String SAVE_DEVICE_NAME = "DEV_NAME";
    static final String SAVE_DIALOG_PEER = "PEER_STATE";
    static final String SAVE_SELECTED_GROUP = "GROUP_NAME";
    DialogInterface.OnClickListener mCancelConnectListener;
    WifiP2pManager.Channel mChannel;
    int mConnectedDevices;
    DialogInterface.OnClickListener mDeleteGroupListener;
    private EditText mDeviceNameText;
    DialogInterface.OnClickListener mDisconnectListener;
    P2pPeerCategoryPreferenceController mPeerCategoryController;
    P2pPersistentCategoryPreferenceController mPersistentCategoryController;
    DialogInterface.OnClickListener mRenameListener;
    String mSavedDeviceName;
    WifiP2pPersistentGroup mSelectedGroup;
    String mSelectedGroupName;
    WifiP2pPeer mSelectedWifiPeer;
    private WifiP2pDevice mThisDevice;
    P2pThisDevicePreferenceController mThisDevicePreferenceController;
    private boolean mWifiP2pEnabled;
    WifiP2pManager mWifiP2pManager;
    boolean mWifiP2pSearching;
    private final IntentFilter mIntentFilter = new IntentFilter();
    boolean mLastGroupFormed = false;
    private boolean mIsIgnoreInitConnectionInfoCallback = false;
    private WifiP2pDeviceList mPeers = new WifiP2pDeviceList();
    final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            WifiP2pManager.PersistentGroupInfoListener persistentGroupInfoListener;
            WifiP2pManager wifiP2pManager;
            WifiP2pManager.Channel channel;
            WifiP2pManager.Channel channel2;
            String action = intent.getAction();
            boolean z = false;
            if ("android.net.wifi.p2p.STATE_CHANGED".equals(action)) {
                WifiP2pSettings wifiP2pSettings = WifiP2pSettings.this;
                if (intent.getIntExtra("wifi_p2p_state", 1) == 2) {
                    z = true;
                }
                wifiP2pSettings.mWifiP2pEnabled = z;
                WifiP2pSettings.this.handleP2pStateChanged();
            } else if ("android.net.wifi.p2p.PEERS_CHANGED".equals(action)) {
                WifiP2pSettings.this.mPeers = (WifiP2pDeviceList) intent.getParcelableExtra("wifiP2pDeviceList");
                WifiP2pSettings.this.handlePeersChanged();
            } else if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(action)) {
                if (WifiP2pSettings.this.mWifiP2pManager != null) {
                    WifiP2pInfo wifiP2pInfo = (WifiP2pInfo) intent.getParcelableExtra("wifiP2pInfo");
                    if (!((NetworkInfo) intent.getParcelableExtra("networkInfo")).isConnected()) {
                        WifiP2pSettings wifiP2pSettings2 = WifiP2pSettings.this;
                        if (!wifiP2pSettings2.mLastGroupFormed) {
                            wifiP2pSettings2.startSearch();
                        }
                    }
                    WifiP2pSettings wifiP2pSettings3 = WifiP2pSettings.this;
                    wifiP2pSettings3.mLastGroupFormed = wifiP2pInfo.groupFormed;
                    wifiP2pSettings3.mIsIgnoreInitConnectionInfoCallback = true;
                }
            } else if ("android.net.wifi.p2p.THIS_DEVICE_CHANGED".equals(action)) {
                WifiP2pSettings wifiP2pSettings4 = WifiP2pSettings.this;
                WifiP2pManager wifiP2pManager2 = wifiP2pSettings4.mWifiP2pManager;
                if (wifiP2pManager2 != null && (channel2 = wifiP2pSettings4.mChannel) != null) {
                    wifiP2pManager2.requestDeviceInfo(channel2, wifiP2pSettings4);
                }
            } else if ("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE".equals(action)) {
                if (intent.getIntExtra("discoveryState", 1) == 2) {
                    WifiP2pSettings.this.updateSearchMenu(true);
                } else {
                    WifiP2pSettings.this.updateSearchMenu(false);
                }
            } else if ("android.net.wifi.p2p.action.WIFI_P2P_PERSISTENT_GROUPS_CHANGED".equals(action) && (wifiP2pManager = (persistentGroupInfoListener = WifiP2pSettings.this).mWifiP2pManager) != null && (channel = persistentGroupInfoListener.mChannel) != null) {
                wifiP2pManager.requestPersistentGroupInfo(channel, persistentGroupInfoListener);
            }
        }
    };

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i == 1) {
            return 575;
        }
        if (i == 2) {
            return 576;
        }
        if (i != 3) {
            return i != 4 ? 0 : 578;
        }
        return 577;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_wifi_p2p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "WifiP2pSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 109;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.wifi_p2p_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mPersistentCategoryController = new P2pPersistentCategoryPreferenceController(context);
        this.mPeerCategoryController = new P2pPeerCategoryPreferenceController(context);
        this.mThisDevicePreferenceController = new P2pThisDevicePreferenceController(context);
        arrayList.add(this.mPersistentCategoryController);
        arrayList.add(this.mPeerCategoryController);
        arrayList.add(this.mThisDevicePreferenceController);
        return arrayList;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        getActivity();
        if (this.mWifiP2pManager == null) {
            this.mWifiP2pManager = (WifiP2pManager) getSystemService("wifip2p");
        }
        if (this.mWifiP2pManager == null) {
            Log.e("WifiP2pSettings", "mWifiP2pManager is null !");
        } else if (!initChannel()) {
            Log.e("WifiP2pSettings", "Failed to set up connection with wifi p2p service");
            this.mWifiP2pManager = null;
        }
        if (bundle != null && bundle.containsKey(SAVE_DIALOG_PEER)) {
            this.mSelectedWifiPeer = new WifiP2pPeer(getPrefContext(), (WifiP2pDevice) bundle.getParcelable(SAVE_DIALOG_PEER));
        }
        if (bundle != null && bundle.containsKey(SAVE_DEVICE_NAME)) {
            this.mSavedDeviceName = bundle.getString(SAVE_DEVICE_NAME);
        }
        if (bundle != null && bundle.containsKey(SAVE_SELECTED_GROUP)) {
            this.mSelectedGroupName = bundle.getString(SAVE_SELECTED_GROUP);
        }
        this.mRenameListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    WifiP2pSettings wifiP2pSettings = WifiP2pSettings.this;
                    if (!(wifiP2pSettings.mWifiP2pManager == null || wifiP2pSettings.mChannel == null)) {
                        String obj = wifiP2pSettings.mDeviceNameText.getText().toString();
                        if (obj != null) {
                            for (int i2 = 0; i2 < obj.length(); i2++) {
                                char charAt = obj.charAt(i2);
                                if (!Character.isDigit(charAt) && !Character.isLetter(charAt) && charAt != '-' && charAt != '_' && charAt != ' ') {
                                    Toast.makeText(WifiP2pSettings.this.getActivity(), (int) R.string.wifi_p2p_failed_rename_message, 1).show();
                                    return;
                                }
                            }
                        }
                        WifiP2pSettings wifiP2pSettings2 = WifiP2pSettings.this;
                        wifiP2pSettings2.mWifiP2pManager.setDeviceName(wifiP2pSettings2.mChannel, wifiP2pSettings2.mDeviceNameText.getText().toString(), new WifiP2pManager.ActionListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.2.1
                            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                            public void onSuccess() {
                            }

                            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                            public void onFailure(int i3) {
                                Toast.makeText(WifiP2pSettings.this.getActivity(), (int) R.string.wifi_p2p_failed_rename_message, 1).show();
                            }
                        });
                    }
                }
            }
        };
        this.mDisconnectListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                WifiP2pSettings wifiP2pSettings;
                WifiP2pManager wifiP2pManager;
                WifiP2pManager.Channel channel;
                if (i == -1 && (wifiP2pManager = (wifiP2pSettings = WifiP2pSettings.this).mWifiP2pManager) != null && (channel = wifiP2pSettings.mChannel) != null) {
                    wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.3.1
                        @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                        public void onFailure(int i2) {
                        }

                        @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                        public void onSuccess() {
                        }
                    });
                }
            }
        };
        this.mCancelConnectListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                WifiP2pSettings wifiP2pSettings;
                WifiP2pManager wifiP2pManager;
                WifiP2pManager.Channel channel;
                if (i == -1 && (wifiP2pManager = (wifiP2pSettings = WifiP2pSettings.this).mWifiP2pManager) != null && (channel = wifiP2pSettings.mChannel) != null) {
                    wifiP2pManager.cancelConnect(channel, new WifiP2pManager.ActionListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.4.1
                        @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                        public void onFailure(int i2) {
                        }

                        @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                        public void onSuccess() {
                        }
                    });
                }
            }
        };
        this.mDeleteGroupListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                WifiP2pManager.Channel channel;
                WifiP2pPersistentGroup wifiP2pPersistentGroup;
                if (i == -1) {
                    WifiP2pSettings wifiP2pSettings = WifiP2pSettings.this;
                    WifiP2pManager wifiP2pManager = wifiP2pSettings.mWifiP2pManager;
                    if (wifiP2pManager != null && (channel = wifiP2pSettings.mChannel) != null && (wifiP2pPersistentGroup = wifiP2pSettings.mSelectedGroup) != null) {
                        wifiP2pManager.deletePersistentGroup(channel, wifiP2pPersistentGroup.getNetworkId(), new WifiP2pManager.ActionListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.5.1
                            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                            public void onFailure(int i2) {
                            }

                            @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                            public void onSuccess() {
                            }
                        });
                        WifiP2pSettings.this.mSelectedGroup = null;
                    }
                } else if (i == -2) {
                    WifiP2pSettings.this.mSelectedGroup = null;
                }
            }
        };
        super.onActivityCreated(bundle);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mIntentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        this.mIntentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE");
        this.mIntentFilter.addAction("android.net.wifi.p2p.action.WIFI_P2P_PERSISTENT_GROUPS_CHANGED");
        getPreferenceScreen();
        if (this.mWifiP2pManager != null && initChannel()) {
            getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
            this.mWifiP2pManager.requestPeers(this.mChannel, this);
            this.mWifiP2pManager.requestDeviceInfo(this.mChannel, this);
            this.mIsIgnoreInitConnectionInfoCallback = false;
            this.mWifiP2pManager.requestNetworkInfo(this.mChannel, new WifiP2pManager.NetworkInfoListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings$$ExternalSyntheticLambda1
                @Override // android.net.wifi.p2p.WifiP2pManager.NetworkInfoListener
                public final void onNetworkInfoAvailable(NetworkInfo networkInfo) {
                    WifiP2pSettings.this.lambda$onResume$1(networkInfo);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$1(final NetworkInfo networkInfo) {
        this.mWifiP2pManager.requestConnectionInfo(this.mChannel, new WifiP2pManager.ConnectionInfoListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings$$ExternalSyntheticLambda0
            @Override // android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
            public final void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                WifiP2pSettings.this.lambda$onResume$0(networkInfo, wifiP2pInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$0(NetworkInfo networkInfo, WifiP2pInfo wifiP2pInfo) {
        if (!this.mIsIgnoreInitConnectionInfoCallback) {
            if (!networkInfo.isConnected() && !this.mLastGroupFormed) {
                startSearch();
            }
            this.mLastGroupFormed = wifiP2pInfo.groupFormed;
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        WifiP2pManager.Channel channel;
        super.onPause();
        WifiP2pManager wifiP2pManager = this.mWifiP2pManager;
        if (!(wifiP2pManager == null || (channel = this.mChannel) == null)) {
            wifiP2pManager.stopPeerDiscovery(channel, null);
            if (!this.mLastGroupFormed) {
                this.mChannel.close();
                this.mChannel = null;
            }
        }
        getActivity().unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 1, 0, this.mWifiP2pSearching ? R.string.wifi_p2p_menu_searching : R.string.wifi_p2p_menu_search).setEnabled(this.mWifiP2pEnabled);
        menu.add(0, 2, 0, R.string.wifi_p2p_menu_rename).setEnabled(this.mWifiP2pEnabled);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(1);
        MenuItem findItem2 = menu.findItem(2);
        if (this.mWifiP2pEnabled) {
            findItem.setEnabled(true);
            findItem2.setEnabled(true);
        } else {
            findItem.setEnabled(false);
            findItem2.setEnabled(false);
        }
        if (this.mWifiP2pSearching) {
            findItem.setTitle(R.string.wifi_p2p_menu_searching);
        } else {
            findItem.setTitle(R.string.wifi_p2p_menu_search);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            startSearch();
            return true;
        } else if (itemId != 2) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            showDialog(3);
            return true;
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        WifiP2pManager.Channel channel;
        if (preference instanceof WifiP2pPeer) {
            WifiP2pPeer wifiP2pPeer = (WifiP2pPeer) preference;
            this.mSelectedWifiPeer = wifiP2pPeer;
            int i = wifiP2pPeer.device.status;
            if (i == 0) {
                showDialog(1);
            } else if (i == 1) {
                showDialog(2);
            } else {
                WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                wifiP2pConfig.deviceAddress = this.mSelectedWifiPeer.device.deviceAddress;
                int intValue = ((Integer) TelephonyProperties.wps_info().orElse(-1)).intValue();
                if (intValue != -1) {
                    wifiP2pConfig.wps.setup = intValue;
                } else if (this.mSelectedWifiPeer.device.wpsPbcSupported()) {
                    wifiP2pConfig.wps.setup = 0;
                } else if (this.mSelectedWifiPeer.device.wpsKeypadSupported()) {
                    wifiP2pConfig.wps.setup = 2;
                } else {
                    wifiP2pConfig.wps.setup = 1;
                }
                WifiP2pManager wifiP2pManager = this.mWifiP2pManager;
                if (!(wifiP2pManager == null || (channel = this.mChannel) == null)) {
                    wifiP2pManager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.6
                        @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                        public void onSuccess() {
                        }

                        @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                        public void onFailure(int i2) {
                            Log.e("WifiP2pSettings", " connect fail " + i2);
                            Toast.makeText(WifiP2pSettings.this.getActivity(), (int) R.string.wifi_p2p_failed_connect_message, 0).show();
                        }
                    });
                }
            }
        } else if (preference instanceof WifiP2pPersistentGroup) {
            this.mSelectedGroup = (WifiP2pPersistentGroup) preference;
            showDialog(4);
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        String str;
        String str2;
        String str3;
        if (i == 1) {
            if (TextUtils.isEmpty(this.mSelectedWifiPeer.device.deviceName)) {
                str2 = this.mSelectedWifiPeer.device.deviceAddress;
            } else {
                str2 = this.mSelectedWifiPeer.device.deviceName;
            }
            if (this.mConnectedDevices > 1) {
                str3 = getActivity().getString(R.string.wifi_p2p_disconnect_multiple_message, new Object[]{str2, Integer.valueOf(this.mConnectedDevices - 1)});
            } else {
                str3 = getActivity().getString(R.string.wifi_p2p_disconnect_message, new Object[]{str2});
            }
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.wifi_p2p_disconnect_title).setMessage(str3).setPositiveButton(getActivity().getString(R.string.dlg_ok), this.mDisconnectListener).setNegativeButton(getActivity().getString(R.string.dlg_cancel), (DialogInterface.OnClickListener) null).create();
        } else if (i == 2) {
            if (TextUtils.isEmpty(this.mSelectedWifiPeer.device.deviceName)) {
                str = this.mSelectedWifiPeer.device.deviceAddress;
            } else {
                str = this.mSelectedWifiPeer.device.deviceName;
            }
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.wifi_p2p_cancel_connect_title).setMessage(getActivity().getString(R.string.wifi_p2p_cancel_connect_message, new Object[]{str})).setPositiveButton(getActivity().getString(R.string.dlg_ok), this.mCancelConnectListener).setNegativeButton(getActivity().getString(R.string.dlg_cancel), (DialogInterface.OnClickListener) null).create();
        } else if (i == 3) {
            View inflate = LayoutInflater.from(getPrefContext()).inflate(R.layout.dialog_edittext, (ViewGroup) null);
            EditText editText = (EditText) inflate.findViewById(R.id.edittext);
            this.mDeviceNameText = editText;
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
            String str4 = this.mSavedDeviceName;
            if (str4 != null) {
                this.mDeviceNameText.setText(str4);
                this.mDeviceNameText.setSelection(this.mSavedDeviceName.length());
            } else {
                WifiP2pDevice wifiP2pDevice = this.mThisDevice;
                if (wifiP2pDevice != null && !TextUtils.isEmpty(wifiP2pDevice.deviceName)) {
                    this.mDeviceNameText.setText(this.mThisDevice.deviceName);
                    this.mDeviceNameText.setSelection(0, this.mThisDevice.deviceName.length());
                }
            }
            this.mSavedDeviceName = null;
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.wifi_p2p_menu_rename).setView(inflate).setPositiveButton(getActivity().getString(R.string.dlg_ok), this.mRenameListener).setNegativeButton(getActivity().getString(R.string.dlg_cancel), (DialogInterface.OnClickListener) null).create();
        } else if (i == 4) {
            return new AlertDialog.Builder(getActivity()).setMessage(getActivity().getString(R.string.wifi_p2p_delete_group_message)).setPositiveButton(getActivity().getString(R.string.dlg_ok), this.mDeleteGroupListener).setNegativeButton(getActivity().getString(R.string.dlg_cancel), this.mDeleteGroupListener).create();
        } else {
            return null;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        WifiP2pPeer wifiP2pPeer = this.mSelectedWifiPeer;
        if (wifiP2pPeer != null) {
            bundle.putParcelable(SAVE_DIALOG_PEER, wifiP2pPeer.device);
        }
        EditText editText = this.mDeviceNameText;
        if (editText != null) {
            bundle.putString(SAVE_DEVICE_NAME, editText.getText().toString());
        }
        WifiP2pPersistentGroup wifiP2pPersistentGroup = this.mSelectedGroup;
        if (wifiP2pPersistentGroup != null) {
            bundle.putString(SAVE_SELECTED_GROUP, wifiP2pPersistentGroup.getGroupName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePeersChanged() {
        this.mPeerCategoryController.removeAllChildren();
        this.mConnectedDevices = 0;
        for (WifiP2pDevice wifiP2pDevice : this.mPeers.getDeviceList()) {
            this.mPeerCategoryController.addChild(new WifiP2pPeer(getPrefContext(), wifiP2pDevice));
            if (wifiP2pDevice.status == 0) {
                this.mConnectedDevices++;
            }
        }
    }

    public void onPersistentGroupInfoAvailable(WifiP2pGroupList wifiP2pGroupList) {
        this.mPersistentCategoryController.removeAllChildren();
        for (WifiP2pGroup wifiP2pGroup : wifiP2pGroupList.getGroupList()) {
            WifiP2pPersistentGroup wifiP2pPersistentGroup = new WifiP2pPersistentGroup(getPrefContext(), wifiP2pGroup);
            this.mPersistentCategoryController.addChild(wifiP2pPersistentGroup);
            if (wifiP2pPersistentGroup.getGroupName().equals(this.mSelectedGroupName)) {
                this.mSelectedGroup = wifiP2pPersistentGroup;
                this.mSelectedGroupName = null;
            }
        }
        if (this.mSelectedGroupName != null) {
            Log.w("WifiP2pSettings", " Selected group " + this.mSelectedGroupName + " disappered on next query ");
        }
    }

    @Override // android.net.wifi.p2p.WifiP2pManager.PeerListListener
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        this.mPeers = wifiP2pDeviceList;
        handlePeersChanged();
    }

    @Override // android.net.wifi.p2p.WifiP2pManager.DeviceInfoListener
    public void onDeviceInfoAvailable(WifiP2pDevice wifiP2pDevice) {
        this.mThisDevice = wifiP2pDevice;
        this.mThisDevicePreferenceController.updateDeviceName(wifiP2pDevice);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleP2pStateChanged() {
        updateSearchMenu(false);
        this.mThisDevicePreferenceController.setEnabled(this.mWifiP2pEnabled);
        this.mPersistentCategoryController.setEnabled(this.mWifiP2pEnabled);
        this.mPeerCategoryController.setEnabled(this.mWifiP2pEnabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSearchMenu(boolean z) {
        this.mWifiP2pSearching = z;
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSearch() {
        WifiP2pManager.Channel channel;
        WifiP2pManager wifiP2pManager = this.mWifiP2pManager;
        if (wifiP2pManager != null && (channel = this.mChannel) != null && !this.mWifiP2pSearching) {
            wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() { // from class: com.android.settings.wifi.p2p.WifiP2pSettings.7
                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onFailure(int i) {
                }

                @Override // android.net.wifi.p2p.WifiP2pManager.ActionListener
                public void onSuccess() {
                }
            });
        }
    }

    private boolean initChannel() {
        if (this.mChannel != null) {
            return true;
        }
        WifiP2pManager wifiP2pManager = this.mWifiP2pManager;
        if (wifiP2pManager != null) {
            this.mChannel = wifiP2pManager.initialize(getActivity().getApplicationContext(), getActivity().getMainLooper(), null);
        }
        if (this.mChannel != null) {
            return true;
        }
        Log.e("WifiP2pSettings", "Failed to set up connection with wifi p2p service");
        return false;
    }
}
