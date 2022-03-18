package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.AdaptiveOutlineDrawable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public class CachedBluetoothDevice implements Comparable<CachedBluetoothDevice> {
    private long mConnectAttempted;
    private final Context mContext;
    BluetoothDevice mDevice;
    LruCache<String, BitmapDrawable> mDrawableCache;
    boolean mJustDiscovered;
    private boolean mLocalNapRoleConnected;
    private final LocalBluetoothProfileManager mProfileManager;
    short mRssi;
    private CachedBluetoothDevice mSubDevice;
    private final Object mProfileLock = new Object();
    private final Collection<LocalBluetoothProfile> mProfiles = new CopyOnWriteArrayList();
    private final Collection<LocalBluetoothProfile> mRemovedProfiles = new CopyOnWriteArrayList();
    boolean mIsCoordinatedSetMember = false;
    private final Collection<Callback> mCallbacks = new CopyOnWriteArrayList();
    private boolean mIsActiveDeviceA2dp = false;
    private boolean mIsActiveDeviceHeadset = false;
    private boolean mIsActiveDeviceHearingAid = false;
    private boolean mIsActiveDeviceLeAudio = false;
    private boolean mIsA2dpProfileConnectedFail = false;
    private boolean mIsHeadsetProfileConnectedFail = false;
    private boolean mIsHearingAidProfileConnectedFail = false;
    private boolean mIsLeAudioProfileConnectedFail = false;
    private Set<CachedBluetoothDevice> mMemberDevices = new HashSet();
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                CachedBluetoothDevice.this.mIsHeadsetProfileConnectedFail = true;
            } else if (i == 2) {
                CachedBluetoothDevice.this.mIsA2dpProfileConnectedFail = true;
            } else if (i == 21) {
                CachedBluetoothDevice.this.mIsHearingAidProfileConnectedFail = true;
            } else if (i != 22) {
                Log.w("CachedBluetoothDevice", "handleMessage(): unknown message : " + message.what);
            } else {
                CachedBluetoothDevice.this.mIsLeAudioProfileConnectedFail = true;
            }
            Log.w("CachedBluetoothDevice", "Connect to profile : " + message.what + " timeout, show error message !");
            CachedBluetoothDevice.this.refresh();
        }
    };
    private final BluetoothAdapter mLocalAdapter = BluetoothAdapter.getDefaultAdapter();
    private long mHiSyncId = 0;
    private int mGroupId = -1;
    private boolean mUnpairing = false;

    /* loaded from: classes.dex */
    public interface Callback {
        void onDeviceAttributesChanged();
    }

    private boolean isTwsBatteryAvailable(int i, int i2) {
        return i >= 0 && i2 >= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedBluetoothDevice(Context context, LocalBluetoothProfileManager localBluetoothProfileManager, BluetoothDevice bluetoothDevice) {
        this.mContext = context;
        this.mProfileManager = localBluetoothProfileManager;
        this.mDevice = bluetoothDevice;
        fillData();
        initDrawableCache();
    }

    private void initDrawableCache() {
        this.mDrawableCache = new LruCache<String, BitmapDrawable>(((int) (Runtime.getRuntime().maxMemory() / 1024)) / 8) { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice.2
            /* JADX INFO: Access modifiers changed from: protected */
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount() / 1024;
            }
        };
    }

    private String describe(LocalBluetoothProfile localBluetoothProfile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Address:");
        sb.append(this.mDevice);
        if (localBluetoothProfile != null) {
            sb.append(" Profile:");
            sb.append(localBluetoothProfile);
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onProfileStateChanged(LocalBluetoothProfile localBluetoothProfile, int i) {
        Log.d("CachedBluetoothDevice", "onProfileStateChanged: profile " + localBluetoothProfile + ", device " + this.mDevice.getAnonymizedAddress() + ", newProfileState " + i);
        if (this.mLocalAdapter.getState() == 13) {
            Log.d("CachedBluetoothDevice", " BT Turninig Off...Profile conn state change ignored...");
            return;
        }
        synchronized (this.mProfileLock) {
            if ((localBluetoothProfile instanceof A2dpProfile) || (localBluetoothProfile instanceof HeadsetProfile) || (localBluetoothProfile instanceof HearingAidProfile)) {
                setProfileConnectedStatus(localBluetoothProfile.getProfileId(), false);
                if (i != 0) {
                    if (i == 1) {
                        this.mHandler.sendEmptyMessageDelayed(localBluetoothProfile.getProfileId(), 60000L);
                    } else if (i == 2) {
                        this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    } else if (i != 3) {
                        Log.w("CachedBluetoothDevice", "onProfileStateChanged(): unknown profile state : " + i);
                    } else if (this.mHandler.hasMessages(localBluetoothProfile.getProfileId())) {
                        this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    }
                } else if (this.mHandler.hasMessages(localBluetoothProfile.getProfileId())) {
                    this.mHandler.removeMessages(localBluetoothProfile.getProfileId());
                    setProfileConnectedStatus(localBluetoothProfile.getProfileId(), true);
                }
            }
            if (i == 2) {
                if (localBluetoothProfile instanceof MapProfile) {
                    localBluetoothProfile.setEnabled(this.mDevice, true);
                }
                if (!this.mProfiles.contains(localBluetoothProfile)) {
                    this.mRemovedProfiles.remove(localBluetoothProfile);
                    this.mProfiles.add(localBluetoothProfile);
                    if ((localBluetoothProfile instanceof PanProfile) && ((PanProfile) localBluetoothProfile).isLocalRoleNap(this.mDevice)) {
                        this.mLocalNapRoleConnected = true;
                    }
                }
            } else if ((localBluetoothProfile instanceof MapProfile) && i == 0) {
                localBluetoothProfile.setEnabled(this.mDevice, false);
            } else if (this.mLocalNapRoleConnected && (localBluetoothProfile instanceof PanProfile) && ((PanProfile) localBluetoothProfile).isLocalRoleNap(this.mDevice) && i == 0) {
                Log.d("CachedBluetoothDevice", "Removing PanProfile from device after NAP disconnect");
                this.mProfiles.remove(localBluetoothProfile);
                this.mRemovedProfiles.add(localBluetoothProfile);
                this.mLocalNapRoleConnected = false;
            }
        }
        fetchActiveDevices();
    }

    void setProfileConnectedStatus(int i, boolean z) {
        if (i == 1) {
            this.mIsHeadsetProfileConnectedFail = z;
        } else if (i == 2) {
            this.mIsA2dpProfileConnectedFail = z;
        } else if (i == 21) {
            this.mIsHearingAidProfileConnectedFail = z;
        } else if (i != 22) {
            Log.w("CachedBluetoothDevice", "setProfileConnectedStatus(): unknown profile id : " + i);
        } else {
            this.mIsLeAudioProfileConnectedFail = z;
        }
    }

    public void disconnect() {
        synchronized (this.mProfileLock) {
            this.mDevice.disconnect();
        }
        PbapServerProfile pbapProfile = this.mProfileManager.getPbapProfile();
        if (pbapProfile != null && isConnectedProfile(pbapProfile)) {
            pbapProfile.setEnabled(this.mDevice, false);
        }
    }

    public void connect() {
        if (ensurePaired()) {
            this.mConnectAttempted = SystemClock.elapsedRealtime();
            connectDevice();
        }
    }

    public long getHiSyncId() {
        return this.mHiSyncId;
    }

    public void setHiSyncId(long j) {
        this.mHiSyncId = j;
    }

    public boolean isHearingAidDevice() {
        return this.mHiSyncId != 0;
    }

    public void setIsCoordinatedSetMember(boolean z) {
        this.mIsCoordinatedSetMember = z;
    }

    public boolean isCoordinatedSetMemberDevice() {
        return this.mIsCoordinatedSetMember;
    }

    public int getGroupId() {
        return this.mGroupId;
    }

    public void setGroupId(int i) {
        this.mGroupId = i;
    }

    private void connectDevice() {
        synchronized (this.mProfileLock) {
            if (this.mProfiles.isEmpty()) {
                Log.d("CachedBluetoothDevice", "No profiles. Maybe we will connect later for device " + this.mDevice);
                return;
            }
            this.mDevice.connect();
        }
    }

    private boolean ensurePaired() {
        if (getBondState() != 10) {
            return true;
        }
        startPairing();
        return false;
    }

    public boolean startPairing() {
        if (this.mLocalAdapter.isDiscovering()) {
            this.mLocalAdapter.cancelDiscovery();
        }
        return this.mDevice.createBond();
    }

    public void unpair() {
        BluetoothDevice bluetoothDevice;
        int bondState = getBondState();
        if (bondState == 11) {
            this.mDevice.cancelBondProcess();
        }
        if (bondState != 10 && (bluetoothDevice = this.mDevice) != null) {
            this.mUnpairing = true;
            if (bluetoothDevice.removeBond()) {
                releaseLruCache();
                Log.d("CachedBluetoothDevice", "Command sent successfully:REMOVE_BOND " + describe(null));
            }
        }
    }

    public int getProfileConnectionState(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile != null) {
            return localBluetoothProfile.getConnectionStatus(this.mDevice);
        }
        return 0;
    }

    private void fillData() {
        updateProfiles();
        fetchActiveDevices();
        migratePhonebookPermissionChoice();
        migrateMessagePermissionChoice();
        lambda$refresh$0();
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public String getAddress() {
        return this.mDevice.getAddress();
    }

    public String getIdentityAddress() {
        String identityAddress = this.mDevice.getIdentityAddress();
        return TextUtils.isEmpty(identityAddress) ? getAddress() : identityAddress;
    }

    public String getName() {
        String alias = this.mDevice.getAlias();
        return TextUtils.isEmpty(alias) ? getAddress() : alias;
    }

    public void setName(String str) {
        if (str != null && !TextUtils.equals(str, getName())) {
            this.mDevice.setAlias(str);
            lambda$refresh$0();
        }
    }

    public boolean setActive() {
        boolean z;
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile == null || !isConnectedProfile(a2dpProfile) || !a2dpProfile.setActiveDevice(getDevice())) {
            z = false;
        } else {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: A2DP active device=" + this);
            z = true;
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null && isConnectedProfile(headsetProfile) && headsetProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: Headset active device=" + this);
            z = true;
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null && isConnectedProfile(hearingAidProfile) && hearingAidProfile.setActiveDevice(getDevice())) {
            Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: Hearing Aid active device=" + this);
            z = true;
        }
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        if (leAudioProfile == null || !isConnectedProfile(leAudioProfile) || !leAudioProfile.setActiveDevice(getDevice())) {
            return z;
        }
        Log.i("CachedBluetoothDevice", "OnPreferenceClickListener: LeAudio active device=" + this);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refreshName() {
        Log.d("CachedBluetoothDevice", "Device name: " + getName());
        lambda$refresh$0();
    }

    public boolean hasHumanReadableName() {
        return !TextUtils.isEmpty(this.mDevice.getAlias());
    }

    public int getBatteryLevel() {
        return this.mDevice.getBatteryLevel();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refresh() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CachedBluetoothDevice.this.lambda$refresh$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refresh$1() {
        Uri uriMetaData;
        if (BluetoothUtils.isAdvancedDetailsHeader(this.mDevice) && (uriMetaData = BluetoothUtils.getUriMetaData(getDevice(), 5)) != null && this.mDrawableCache.get(uriMetaData.toString()) == null) {
            this.mDrawableCache.put(uriMetaData.toString(), (BitmapDrawable) BluetoothUtils.getBtDrawableWithDescription(this.mContext, this).first);
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.bluetooth.CachedBluetoothDevice$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CachedBluetoothDevice.this.lambda$refresh$0();
            }
        });
    }

    public void setJustDiscovered(boolean z) {
        if (this.mJustDiscovered != z) {
            this.mJustDiscovered = z;
            lambda$refresh$0();
        }
    }

    public int getBondState() {
        return this.mDevice.getBondState();
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0054  */
    /* JADX WARN: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onActiveDeviceChanged(boolean r4, int r5) {
        /*
            r3 = this;
            r0 = 1
            r1 = 0
            if (r5 == r0) goto L_0x0049
            r2 = 2
            if (r5 == r2) goto L_0x0040
            r2 = 21
            if (r5 == r2) goto L_0x0037
            r2 = 22
            if (r5 == r2) goto L_0x002e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "onActiveDeviceChanged: unknown profile "
            r0.append(r2)
            r0.append(r5)
            java.lang.String r5 = " isActive "
            r0.append(r5)
            r0.append(r4)
            java.lang.String r4 = r0.toString()
            java.lang.String r5 = "CachedBluetoothDevice"
            android.util.Log.w(r5, r4)
            goto L_0x0052
        L_0x002e:
            boolean r5 = r3.mIsActiveDeviceLeAudio
            if (r5 == r4) goto L_0x0033
            goto L_0x0034
        L_0x0033:
            r0 = r1
        L_0x0034:
            r3.mIsActiveDeviceLeAudio = r4
            goto L_0x0051
        L_0x0037:
            boolean r5 = r3.mIsActiveDeviceHearingAid
            if (r5 == r4) goto L_0x003c
            goto L_0x003d
        L_0x003c:
            r0 = r1
        L_0x003d:
            r3.mIsActiveDeviceHearingAid = r4
            goto L_0x0051
        L_0x0040:
            boolean r5 = r3.mIsActiveDeviceA2dp
            if (r5 == r4) goto L_0x0045
            goto L_0x0046
        L_0x0045:
            r0 = r1
        L_0x0046:
            r3.mIsActiveDeviceA2dp = r4
            goto L_0x0051
        L_0x0049:
            boolean r5 = r3.mIsActiveDeviceHeadset
            if (r5 == r4) goto L_0x004e
            goto L_0x004f
        L_0x004e:
            r0 = r1
        L_0x004f:
            r3.mIsActiveDeviceHeadset = r4
        L_0x0051:
            r1 = r0
        L_0x0052:
            if (r1 == 0) goto L_0x0057
            r3.lambda$refresh$0()
        L_0x0057:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.bluetooth.CachedBluetoothDevice.onActiveDeviceChanged(boolean, int):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAudioModeChanged() {
        lambda$refresh$0();
    }

    public boolean isActiveDevice(int i) {
        if (i == 1) {
            return this.mIsActiveDeviceHeadset;
        }
        if (i == 2) {
            return this.mIsActiveDeviceA2dp;
        }
        if (i == 21) {
            return this.mIsActiveDeviceHearingAid;
        }
        if (i == 22) {
            return this.mIsActiveDeviceLeAudio;
        }
        Log.w("CachedBluetoothDevice", "getActiveDevice: unknown profile " + i);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRssi(short s) {
        if (this.mRssi != s) {
            this.mRssi = s;
            lambda$refresh$0();
        }
    }

    public boolean isConnected() {
        synchronized (this.mProfileLock) {
            for (LocalBluetoothProfile localBluetoothProfile : this.mProfiles) {
                if (getProfileConnectionState(localBluetoothProfile) == 2) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isConnectedProfile(LocalBluetoothProfile localBluetoothProfile) {
        return getProfileConnectionState(localBluetoothProfile) == 2;
    }

    public boolean isBusy() {
        int profileConnectionState;
        synchronized (this.mProfileLock) {
            Iterator<LocalBluetoothProfile> it = this.mProfiles.iterator();
            do {
                boolean z = true;
                if (it.hasNext()) {
                    profileConnectionState = getProfileConnectionState(it.next());
                    if (profileConnectionState == 1) {
                        break;
                    }
                } else {
                    if (getBondState() != 11) {
                        z = false;
                    }
                    return z;
                }
            } while (profileConnectionState != 3);
            return true;
        }
    }

    private boolean updateProfiles() {
        ParcelUuid[] uuids;
        BluetoothClass bluetoothClass;
        ParcelUuid[] uuids2 = this.mDevice.getUuids();
        if (uuids2 == null || (uuids = this.mLocalAdapter.getUuids()) == null) {
            return false;
        }
        processPhonebookAccess();
        synchronized (this.mProfileLock) {
            this.mProfileManager.updateProfiles(uuids2, uuids, this.mProfiles, this.mRemovedProfiles, this.mLocalNapRoleConnected, this.mDevice);
        }
        Log.d("CachedBluetoothDevice", "updating profiles for " + this.mDevice.getAnonymizedAddress());
        if (this.mDevice.getBluetoothClass() != null) {
            Log.v("CachedBluetoothDevice", "Class: " + bluetoothClass.toString());
        }
        Log.v("CachedBluetoothDevice", "UUID:");
        for (ParcelUuid parcelUuid : uuids2) {
            Log.v("CachedBluetoothDevice", "  " + parcelUuid);
        }
        return true;
    }

    private void fetchActiveDevices() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        if (a2dpProfile != null) {
            this.mIsActiveDeviceA2dp = this.mDevice.equals(a2dpProfile.getActiveDevice());
        }
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        if (headsetProfile != null) {
            this.mIsActiveDeviceHeadset = this.mDevice.equals(headsetProfile.getActiveDevice());
        }
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null) {
            this.mIsActiveDeviceHearingAid = hearingAidProfile.getActiveDevices().contains(this.mDevice);
        }
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        if (leAudioProfile != null) {
            this.mIsActiveDeviceLeAudio = leAudioProfile.getActiveDevices().contains(this.mDevice);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onUuidChanged() {
        updateProfiles();
        ParcelUuid[] uuids = this.mDevice.getUuids();
        long j = 30000;
        if (!ArrayUtils.contains(uuids, BluetoothUuid.HOGP)) {
            if (ArrayUtils.contains(uuids, BluetoothUuid.HEARING_AID)) {
                j = 15000;
            } else if (!ArrayUtils.contains(uuids, BluetoothUuid.LE_AUDIO)) {
                j = 5000;
            }
        }
        Log.d("CachedBluetoothDevice", "onUuidChanged: Time since last connect=" + (SystemClock.elapsedRealtime() - this.mConnectAttempted));
        if (this.mConnectAttempted + j > SystemClock.elapsedRealtime()) {
            Log.d("CachedBluetoothDevice", "onUuidChanged: triggering connectDevice");
            connectDevice();
        }
        lambda$refresh$0();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBondingStateChanged(int i) {
        if (i == 10) {
            synchronized (this.mProfileLock) {
                this.mProfiles.clear();
            }
            this.mDevice.setPhonebookAccessPermission(0);
            this.mDevice.setMessageAccessPermission(0);
            this.mDevice.setSimAccessPermission(0);
        }
        refresh();
        if (i == 12 && this.mDevice.isBondingInitiatedLocally()) {
            connect();
        }
    }

    public BluetoothClass getBtClass() {
        return this.mDevice.getBluetoothClass();
    }

    public List<LocalBluetoothProfile> getProfiles() {
        return new ArrayList(this.mProfiles);
    }

    public List<LocalBluetoothProfile> getConnectableProfiles() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mProfileLock) {
            for (LocalBluetoothProfile localBluetoothProfile : this.mProfiles) {
                if (localBluetoothProfile.accessProfileEnabled()) {
                    arrayList.add(localBluetoothProfile);
                }
            }
        }
        return arrayList;
    }

    public List<LocalBluetoothProfile> getRemovedProfiles() {
        return new ArrayList(this.mRemovedProfiles);
    }

    public void registerCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        this.mCallbacks.remove(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: dispatchAttributesChanged */
    public void lambda$refresh$0() {
        for (Callback callback : this.mCallbacks) {
            callback.onDeviceAttributesChanged();
        }
    }

    public String toString() {
        return this.mDevice.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CachedBluetoothDevice)) {
            return false;
        }
        return this.mDevice.equals(((CachedBluetoothDevice) obj).mDevice);
    }

    public int hashCode() {
        return this.mDevice.getAddress().hashCode();
    }

    public int compareTo(CachedBluetoothDevice cachedBluetoothDevice) {
        int i = (cachedBluetoothDevice.isConnected() ? 1 : 0) - (isConnected() ? 1 : 0);
        if (i != 0) {
            return i;
        }
        int i2 = 1;
        int i3 = cachedBluetoothDevice.getBondState() == 12 ? 1 : 0;
        if (getBondState() != 12) {
            i2 = 0;
        }
        int i4 = i3 - i2;
        if (i4 != 0) {
            return i4;
        }
        int i5 = (cachedBluetoothDevice.mJustDiscovered ? 1 : 0) - (this.mJustDiscovered ? 1 : 0);
        if (i5 != 0) {
            return i5;
        }
        int i6 = cachedBluetoothDevice.mRssi - this.mRssi;
        return i6 != 0 ? i6 : getName().compareTo(cachedBluetoothDevice.getName());
    }

    private void migratePhonebookPermissionChoice() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_phonebook_permission", 0);
        if (sharedPreferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getPhonebookAccessPermission() == 0) {
                int i = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
                if (i == 1) {
                    this.mDevice.setPhonebookAccessPermission(1);
                } else if (i == 2) {
                    this.mDevice.setPhonebookAccessPermission(2);
                }
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(this.mDevice.getAddress());
            edit.commit();
        }
    }

    private void migrateMessagePermissionChoice() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("bluetooth_message_permission", 0);
        if (sharedPreferences.contains(this.mDevice.getAddress())) {
            if (this.mDevice.getMessageAccessPermission() == 0) {
                int i = sharedPreferences.getInt(this.mDevice.getAddress(), 0);
                if (i == 1) {
                    this.mDevice.setMessageAccessPermission(1);
                } else if (i == 2) {
                    this.mDevice.setMessageAccessPermission(2);
                }
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.remove(this.mDevice.getAddress());
            edit.commit();
        }
    }

    private void processPhonebookAccess() {
        if (this.mDevice.getBondState() == 12 && BluetoothUuid.containsAnyUuid(this.mDevice.getUuids(), PbapServerProfile.PBAB_CLIENT_UUIDS)) {
            BluetoothClass bluetoothClass = this.mDevice.getBluetoothClass();
            if (this.mDevice.getPhonebookAccessPermission() == 0) {
                if (bluetoothClass != null && (bluetoothClass.getDeviceClass() == 1032 || bluetoothClass.getDeviceClass() == 1028)) {
                    EventLog.writeEvent(1397638484, "138529441", -1, "");
                }
                this.mDevice.setPhonebookAccessPermission(2);
            }
        }
    }

    public String getConnectionSummary() {
        return getConnectionSummary(false);
    }

    public String getConnectionSummary(boolean z) {
        int i;
        int i2;
        if (isProfileConnectedFail() && isConnected()) {
            return this.mContext.getString(R$string.profile_connect_timeout_subtext);
        }
        synchronized (this.mProfileLock) {
            boolean z2 = false;
            boolean z3 = true;
            boolean z4 = true;
            boolean z5 = true;
            boolean z6 = true;
            for (LocalBluetoothProfile localBluetoothProfile : getProfiles()) {
                int profileConnectionState = getProfileConnectionState(localBluetoothProfile);
                if (profileConnectionState != 0) {
                    if (profileConnectionState != 1) {
                        if (profileConnectionState == 2) {
                            z2 = true;
                        } else if (profileConnectionState != 3) {
                        }
                    }
                    return this.mContext.getString(BluetoothUtils.getConnectionStateSummary(profileConnectionState));
                } else if (localBluetoothProfile.isProfileReady()) {
                    if (!(localBluetoothProfile instanceof A2dpProfile) && !(localBluetoothProfile instanceof A2dpSinkProfile)) {
                        if (!(localBluetoothProfile instanceof HeadsetProfile) && !(localBluetoothProfile instanceof HfpClientProfile)) {
                            if (localBluetoothProfile instanceof HearingAidProfile) {
                                z5 = false;
                            } else if (localBluetoothProfile instanceof LeAudioProfile) {
                                z6 = false;
                            }
                        }
                        z4 = false;
                    }
                    z3 = false;
                }
            }
            int batteryLevel = getBatteryLevel();
            int i3 = -1;
            String formatPercentage = batteryLevel > -1 ? Utils.formatPercentage(batteryLevel) : null;
            int i4 = R$string.bluetooth_pairing;
            if (z2) {
                if (BluetoothUtils.getBooleanMetaData(this.mDevice, 6)) {
                    i3 = BluetoothUtils.getIntMetaData(this.mDevice, 10);
                    i2 = BluetoothUtils.getIntMetaData(this.mDevice, 11);
                } else {
                    i2 = -1;
                }
                if (isTwsBatteryAvailable(i3, i2)) {
                    i = R$string.bluetooth_battery_level_untethered;
                } else {
                    i = formatPercentage != null ? R$string.bluetooth_battery_level : i4;
                }
                if (z3 || z4 || z5 || z6) {
                    boolean isAudioModeOngoingCall = Utils.isAudioModeOngoingCall(this.mContext);
                    if (this.mIsActiveDeviceHearingAid || ((this.mIsActiveDeviceHeadset && isAudioModeOngoingCall) || (this.mIsActiveDeviceA2dp && !isAudioModeOngoingCall))) {
                        if (isTwsBatteryAvailable(i3, i2) && !z) {
                            i = R$string.bluetooth_active_battery_level_untethered;
                        } else if (formatPercentage == null || z) {
                            i = R$string.bluetooth_active_no_battery_level;
                        } else {
                            i = R$string.bluetooth_active_battery_level;
                        }
                    }
                }
            } else {
                i2 = -1;
                i = i4;
            }
            if (i != i4 || getBondState() == 11) {
                return isTwsBatteryAvailable(i3, i2) ? this.mContext.getString(i, Utils.formatPercentage(i3), Utils.formatPercentage(i2)) : this.mContext.getString(i, formatPercentage);
            }
            return null;
        }
    }

    private boolean isProfileConnectedFail() {
        return this.mIsA2dpProfileConnectedFail || this.mIsHearingAidProfileConnectedFail || (!isConnectedSapDevice() && this.mIsHeadsetProfileConnectedFail) || this.mIsLeAudioProfileConnectedFail;
    }

    public boolean isConnectedA2dpDevice() {
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        return a2dpProfile != null && a2dpProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedHfpDevice() {
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        return headsetProfile != null && headsetProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedHearingAidDevice() {
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        return hearingAidProfile != null && hearingAidProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public boolean isConnectedLeAudioDevice() {
        LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        return leAudioProfile != null && leAudioProfile.getConnectionStatus(this.mDevice) == 2;
    }

    private boolean isConnectedSapDevice() {
        SapProfile sapProfile = this.mProfileManager.getSapProfile();
        return sapProfile != null && sapProfile.getConnectionStatus(this.mDevice) == 2;
    }

    public CachedBluetoothDevice getSubDevice() {
        return this.mSubDevice;
    }

    public void setSubDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mSubDevice = cachedBluetoothDevice;
    }

    public void switchSubDeviceContent() {
        BluetoothDevice bluetoothDevice = this.mDevice;
        short s = this.mRssi;
        boolean z = this.mJustDiscovered;
        CachedBluetoothDevice cachedBluetoothDevice = this.mSubDevice;
        this.mDevice = cachedBluetoothDevice.mDevice;
        this.mRssi = cachedBluetoothDevice.mRssi;
        this.mJustDiscovered = cachedBluetoothDevice.mJustDiscovered;
        cachedBluetoothDevice.mDevice = bluetoothDevice;
        cachedBluetoothDevice.mRssi = s;
        cachedBluetoothDevice.mJustDiscovered = z;
        fetchActiveDevices();
    }

    public Set<CachedBluetoothDevice> getMemberDevice() {
        return this.mMemberDevices;
    }

    public void setMemberDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mMemberDevices.add(cachedBluetoothDevice);
    }

    public void removeMemberDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mMemberDevices.remove(cachedBluetoothDevice);
    }

    public void switchMemberDeviceContent(CachedBluetoothDevice cachedBluetoothDevice, CachedBluetoothDevice cachedBluetoothDevice2) {
        BluetoothDevice bluetoothDevice = this.mDevice;
        short s = this.mRssi;
        boolean z = this.mJustDiscovered;
        this.mDevice = cachedBluetoothDevice2.mDevice;
        this.mRssi = cachedBluetoothDevice2.mRssi;
        this.mJustDiscovered = cachedBluetoothDevice2.mJustDiscovered;
        setMemberDevice(cachedBluetoothDevice);
        this.mMemberDevices.remove(cachedBluetoothDevice2);
        cachedBluetoothDevice2.mDevice = bluetoothDevice;
        cachedBluetoothDevice2.mRssi = s;
        cachedBluetoothDevice2.mJustDiscovered = z;
        fetchActiveDevices();
    }

    public Pair<Drawable, String> getDrawableWithDescription() {
        Uri uriMetaData = BluetoothUtils.getUriMetaData(this.mDevice, 5);
        Pair<Drawable, String> btClassDrawableWithDescription = BluetoothUtils.getBtClassDrawableWithDescription(this.mContext, this);
        if (BluetoothUtils.isAdvancedDetailsHeader(this.mDevice) && uriMetaData != null) {
            BitmapDrawable bitmapDrawable = this.mDrawableCache.get(uriMetaData.toString());
            if (bitmapDrawable != null) {
                return new Pair<>(new AdaptiveOutlineDrawable(this.mContext.getResources(), bitmapDrawable.getBitmap()), (String) btClassDrawableWithDescription.second);
            }
            refresh();
        }
        return new Pair<>(BluetoothUtils.buildBtRainbowDrawable(this.mContext, (Drawable) btClassDrawableWithDescription.first, getAddress().hashCode()), (String) btClassDrawableWithDescription.second);
    }

    void releaseLruCache() {
        this.mDrawableCache.evictAll();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getUnpairing() {
        return this.mUnpairing;
    }
}
