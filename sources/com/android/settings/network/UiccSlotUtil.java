package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.telephony.UiccSlotMapping;
import android.util.Log;
import com.android.settingslib.utils.ThreadUtils;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class UiccSlotUtil {
    public static ImmutableList<UiccSlotInfo> getSlotInfos(TelephonyManager telephonyManager) {
        UiccSlotInfo[] uiccSlotsInfo = telephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(uiccSlotsInfo);
    }

    public static synchronized void switchToRemovableSlot(int i, Context context) throws UiccSlotsException {
        synchronized (UiccSlotUtil.class) {
            switchToRemovableSlot(context, i, null);
        }
    }

    public static synchronized void switchToRemovableSlot(Context context, int i, SubscriptionInfo subscriptionInfo) throws UiccSlotsException {
        synchronized (UiccSlotUtil.class) {
            if (!ThreadUtils.isMainThread()) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
                performSwitchToSlot(telephonyManager, prepareUiccSlotMappingsForRemovableSlot(telephonyManager.getSimSlotMapping(), getInactiveRemovableSlot(telephonyManager.getUiccSlotsInfo(), i), subscriptionInfo, telephonyManager.isMultiSimEnabled()), context);
            } else {
                throw new IllegalThreadStateException("Do not call switchToRemovableSlot on the main thread.");
            }
        }
    }

    public static synchronized void switchToEuiccSlot(Context context, final int i, final int i2, SubscriptionInfo subscriptionInfo) throws UiccSlotsException {
        synchronized (UiccSlotUtil.class) {
            if (!ThreadUtils.isMainThread()) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
                Collection simSlotMapping = telephonyManager.getSimSlotMapping();
                Log.i("UiccSlotUtil", "The SimSlotMapping: " + simSlotMapping);
                if (isTargetSlotActive(simSlotMapping, i, i2)) {
                    Log.i("UiccSlotUtil", "The slot is active, then the sim can enable directly.");
                    return;
                }
                Collection arrayList = new ArrayList();
                if (!telephonyManager.isMultiSimEnabled()) {
                    arrayList.add(new UiccSlotMapping(i2, i, 0));
                } else {
                    final int simSlotIndex = subscriptionInfo != null ? subscriptionInfo.getSimSlotIndex() : i;
                    final int portIndex = subscriptionInfo != null ? subscriptionInfo.getPortIndex() : 0;
                    Log.i("UiccSlotUtil", String.format("Start to set SimSlotMapping from slot%d-port%d to slot%d-port%d", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(simSlotIndex), Integer.valueOf(portIndex)));
                    arrayList = (Collection) simSlotMapping.stream().map(new Function() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda0
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            UiccSlotMapping lambda$switchToEuiccSlot$0;
                            lambda$switchToEuiccSlot$0 = UiccSlotUtil.lambda$switchToEuiccSlot$0(simSlotIndex, portIndex, i2, i, (UiccSlotMapping) obj);
                            return lambda$switchToEuiccSlot$0;
                        }
                    }).collect(Collectors.toList());
                }
                Log.i("UiccSlotUtil", "The SimSlotMapping: " + arrayList);
                performSwitchToSlot(telephonyManager, arrayList, context);
                return;
            }
            throw new IllegalThreadStateException("Do not call switchToRemovableSlot on the main thread.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ UiccSlotMapping lambda$switchToEuiccSlot$0(int i, int i2, int i3, int i4, UiccSlotMapping uiccSlotMapping) {
        return (uiccSlotMapping.getPhysicalSlotIndex() == i && uiccSlotMapping.getPortIndex() == i2) ? new UiccSlotMapping(i3, i4, uiccSlotMapping.getLogicalSlotIndex()) : uiccSlotMapping;
    }

    public static int getEsimSlotId(Context context) {
        final ImmutableList<UiccSlotInfo> slotInfos = getSlotInfos((TelephonyManager) context.getSystemService(TelephonyManager.class));
        int orElse = IntStream.range(0, slotInfos.size()).filter(new IntPredicate() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda2
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                boolean lambda$getEsimSlotId$1;
                lambda$getEsimSlotId$1 = UiccSlotUtil.lambda$getEsimSlotId$1(ImmutableList.this, i);
                return lambda$getEsimSlotId$1;
            }
        }).findFirst().orElse(-1);
        Log.i("UiccSlotUtil", "firstEsimSlot: " + orElse);
        return orElse;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getEsimSlotId$1(ImmutableList immutableList, int i) {
        UiccSlotInfo uiccSlotInfo = (UiccSlotInfo) immutableList.get(i);
        if (uiccSlotInfo == null) {
            return false;
        }
        return !uiccSlotInfo.isRemovable();
    }

    private static boolean isTargetSlotActive(Collection<UiccSlotMapping> collection, final int i, final int i2) {
        return collection.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isTargetSlotActive$2;
                lambda$isTargetSlotActive$2 = UiccSlotUtil.lambda$isTargetSlotActive$2(i, i2, (UiccSlotMapping) obj);
                return lambda$isTargetSlotActive$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isTargetSlotActive$2(int i, int i2, UiccSlotMapping uiccSlotMapping) {
        return uiccSlotMapping.getPhysicalSlotIndex() == i && uiccSlotMapping.getPortIndex() == i2;
    }

    private static void performSwitchToSlot(TelephonyManager telephonyManager, Collection<UiccSlotMapping> collection, Context context) throws UiccSlotsException {
        Throwable th;
        InterruptedException e;
        CountDownLatch countDownLatch;
        CarrierConfigChangedReceiver carrierConfigChangedReceiver;
        long j = Settings.Global.getLong(context.getContentResolver(), "euicc_switch_slot_timeout_millis", 25000L);
        BroadcastReceiver broadcastReceiver = null;
        try {
            try {
                countDownLatch = new CountDownLatch(1);
                carrierConfigChangedReceiver = new CarrierConfigChangedReceiver(countDownLatch);
            } catch (InterruptedException e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
        try {
            carrierConfigChangedReceiver.registerOn(context);
            telephonyManager.setSimSlotMapping(collection);
            countDownLatch.await(j, TimeUnit.MILLISECONDS);
            context.unregisterReceiver(carrierConfigChangedReceiver);
        } catch (InterruptedException e3) {
            e = e3;
            broadcastReceiver = carrierConfigChangedReceiver;
            Thread.currentThread().interrupt();
            Log.e("UiccSlotUtil", "Failed switching to physical slot.", e);
            if (broadcastReceiver != null) {
                context.unregisterReceiver(broadcastReceiver);
            }
        } catch (Throwable th3) {
            th = th3;
            broadcastReceiver = carrierConfigChangedReceiver;
            if (broadcastReceiver != null) {
                context.unregisterReceiver(broadcastReceiver);
            }
            throw th;
        }
    }

    private static int getInactiveRemovableSlot(UiccSlotInfo[] uiccSlotInfoArr, int i) throws UiccSlotsException {
        if (uiccSlotInfoArr != null) {
            if (i == -1) {
                for (int i2 = 0; i2 < uiccSlotInfoArr.length; i2++) {
                    if (!(!uiccSlotInfoArr[i2].isRemovable() || ((UiccPortInfo) uiccSlotInfoArr[i2].getPorts().stream().findFirst().get()).isActive() || uiccSlotInfoArr[i2].getCardStateInfo() == 3 || uiccSlotInfoArr[i2].getCardStateInfo() == 4)) {
                        return i2;
                    }
                }
            } else if (i >= uiccSlotInfoArr.length || !uiccSlotInfoArr[i].isRemovable()) {
                throw new UiccSlotsException("The given slotId is not a removable slot: " + i);
            } else if (!((UiccPortInfo) uiccSlotInfoArr[i].getPorts().stream().findFirst().get()).isActive()) {
                return i;
            }
            return -1;
        }
        throw new UiccSlotsException("UiccSlotInfo is null");
    }

    private static Collection<UiccSlotMapping> prepareUiccSlotMappingsForRemovableSlot(Collection<UiccSlotMapping> collection, final int i, final SubscriptionInfo subscriptionInfo, boolean z) {
        if (i != -1 && !collection.stream().anyMatch(new Predicate() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$prepareUiccSlotMappingsForRemovableSlot$3;
                lambda$prepareUiccSlotMappingsForRemovableSlot$3 = UiccSlotUtil.lambda$prepareUiccSlotMappingsForRemovableSlot$3(i, (UiccSlotMapping) obj);
                return lambda$prepareUiccSlotMappingsForRemovableSlot$3;
            }
        })) {
            ArrayList arrayList = new ArrayList();
            if (!z) {
                arrayList.add(new UiccSlotMapping(0, i, 0));
                collection = arrayList;
            } else if (subscriptionInfo != null) {
                Log.i("UiccSlotUtil", String.format("Start to set SimSlotMapping from slot%d-port%d to slot%d-port%d", Integer.valueOf(i), 0, Integer.valueOf(subscriptionInfo.getSimSlotIndex()), Integer.valueOf(subscriptionInfo.getPortIndex())));
                collection = (Collection) collection.stream().map(new Function() { // from class: com.android.settings.network.UiccSlotUtil$$ExternalSyntheticLambda1
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        UiccSlotMapping lambda$prepareUiccSlotMappingsForRemovableSlot$4;
                        lambda$prepareUiccSlotMappingsForRemovableSlot$4 = UiccSlotUtil.lambda$prepareUiccSlotMappingsForRemovableSlot$4(subscriptionInfo, i, (UiccSlotMapping) obj);
                        return lambda$prepareUiccSlotMappingsForRemovableSlot$4;
                    }
                }).collect(Collectors.toList());
            } else {
                Log.i("UiccSlotUtil", "The removedSubInfo is null");
            }
            Log.i("UiccSlotUtil", "The SimSlotMapping: " + collection);
        }
        return collection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$prepareUiccSlotMappingsForRemovableSlot$3(int i, UiccSlotMapping uiccSlotMapping) {
        return uiccSlotMapping.getPhysicalSlotIndex() == i && uiccSlotMapping.getPortIndex() == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ UiccSlotMapping lambda$prepareUiccSlotMappingsForRemovableSlot$4(SubscriptionInfo subscriptionInfo, int i, UiccSlotMapping uiccSlotMapping) {
        return (uiccSlotMapping.getPhysicalSlotIndex() == subscriptionInfo.getSimSlotIndex() && uiccSlotMapping.getPortIndex() == subscriptionInfo.getPortIndex()) ? new UiccSlotMapping(0, i, uiccSlotMapping.getLogicalSlotIndex()) : uiccSlotMapping;
    }
}
