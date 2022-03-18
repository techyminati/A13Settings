package com.google.android.wifitrackerlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.wifitrackerlib.Utils;
import com.google.android.wifitrackerlib.WsuPostProvisioningReceiver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class WsuPostProvisioningReceiver extends BroadcastReceiver {
    WifiManager mWifiManager = null;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.wifi.wsu.action.WSU_POST_PROVISIONING")) {
            Log.d("WsuPostProvisioningReceiver", "process the provisioned profiles, connect to the best available network.");
            final String stringExtra = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
            final ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra("provisioned_profiles");
            final boolean booleanExtra = intent.getBooleanExtra("using_suggestion_api", false);
            if (parcelableArrayListExtra == null || parcelableArrayListExtra.size() == 0 || TextUtils.isEmpty(stringExtra)) {
                Log.e("WsuPostProvisioningReceiver", "Invalid data received, do nothing.");
                return;
            }
            WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
            this.mWifiManager = wifiManager;
            if (wifiManager == null) {
                Log.e("WsuPostProvisioningReceiver", "WifiManger is not available.");
                return;
            }
            final BroadcastReceiver.PendingResult goAsync = goAsync();
            final WifiManager.ScanResultsCallback scanResultsCallback = new WifiManager.ScanResultsCallback() { // from class: com.google.android.wifitrackerlib.WsuPostProvisioningReceiver.1
                @Override // android.net.wifi.WifiManager.ScanResultsCallback
                public void onScanResultsAvailable() {
                    WsuPostProvisioningReceiver wsuPostProvisioningReceiver = WsuPostProvisioningReceiver.this;
                    wsuPostProvisioningReceiver.connectToCandidate(wsuPostProvisioningReceiver.getConnectingCandidate(stringExtra, parcelableArrayListExtra, booleanExtra), stringExtra);
                    WsuPostProvisioningReceiver.this.mWifiManager.unregisterScanResultsCallback(this);
                    goAsync.finish();
                }
            };
            final ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
            newSingleThreadExecutor.execute(new Runnable() { // from class: com.google.android.wifitrackerlib.WsuPostProvisioningReceiver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WsuPostProvisioningReceiver.this.lambda$onReceive$0(newSingleThreadExecutor, scanResultsCallback);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onReceive$0(Executor executor, WifiManager.ScanResultsCallback scanResultsCallback) {
        this.mWifiManager.startScan();
        this.mWifiManager.registerScanResultsCallback(executor, scanResultsCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectToCandidate(Optional<CandidateWifiConfig> optional, String str) {
        optional.ifPresent(new Consumer() { // from class: com.google.android.wifitrackerlib.WsuPostProvisioningReceiver$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WsuPostProvisioningReceiver.this.lambda$connectToCandidate$1((WsuPostProvisioningReceiver.CandidateWifiConfig) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$connectToCandidate$1(CandidateWifiConfig candidateWifiConfig) {
        this.mWifiManager.connect(candidateWifiConfig.wifiConfigFromSystem.networkId, null);
        Log.d("WsuPostProvisioningReceiver", "Trying connect to network" + candidateWifiConfig.wifiConfigFromSystem.getKey() + ", network id: " + candidateWifiConfig.wifiConfigFromSystem.networkId);
    }

    Optional<CandidateWifiConfig> getConnectingCandidate(String str, List<WifiNetworkSuggestion> list, boolean z) {
        List<ScanResult> scanResults = this.mWifiManager.getScanResults();
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(getPasspointCandidates(str, list, scanResults));
        if (z) {
            arrayList.addAll(getSuggestedNonPasspointCandidates(str, list, scanResults));
        } else {
            arrayList.addAll(getSavedNonPasspointCandidates(str, list, scanResults));
        }
        if (arrayList.size() == 0) {
            Log.d("WsuPostProvisioningReceiver", "There is no any matched candidate network for connecting.");
            return Optional.empty();
        }
        arrayList.sort(WsuPostProvisioningReceiver$$ExternalSyntheticLambda1.INSTANCE);
        return Optional.of((CandidateWifiConfig) arrayList.get(0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getConnectingCandidate$2(CandidateWifiConfig candidateWifiConfig, CandidateWifiConfig candidateWifiConfig2) {
        boolean z = candidateWifiConfig.isRoaming;
        if (!z && candidateWifiConfig2.isRoaming) {
            return -1;
        }
        if (z && !candidateWifiConfig2.isRoaming) {
            return 1;
        }
        int i = candidateWifiConfig.priority;
        int i2 = candidateWifiConfig2.priority;
        return i != i2 ? i2 - i : candidateWifiConfig2.bestScanResult.level - candidateWifiConfig.bestScanResult.level;
    }

    List<CandidateWifiConfig> getPasspointCandidates(String str, List<WifiNetworkSuggestion> list, List<ScanResult> list2) {
        List<Pair> allMatchingWifiConfigs = this.mWifiManager.getAllMatchingWifiConfigs(list2);
        ArrayList arrayList = new ArrayList();
        for (WifiNetworkSuggestion wifiNetworkSuggestion : list) {
            PasspointConfiguration passpointConfig = wifiNetworkSuggestion.getPasspointConfig();
            if (passpointConfig != null) {
                String uniqueId = passpointConfig.getUniqueId();
                for (Pair pair : allMatchingWifiConfigs) {
                    WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
                    if (TextUtils.equals(wifiConfiguration.getKey(), uniqueId) && TextUtils.equals(wifiConfiguration.creatorName, str)) {
                        List list3 = (List) ((Map) pair.second).get(0);
                        List list4 = (List) ((Map) pair.second).get(1);
                        if (list3 != null && !list3.isEmpty()) {
                            arrayList.add(new CandidateWifiConfig(wifiConfiguration, Utils.getBestScanResultByLevel(list3), false, wifiNetworkSuggestion.getPriority()));
                        }
                        if (list4 != null && !list4.isEmpty()) {
                            arrayList.add(new CandidateWifiConfig(wifiConfiguration, Utils.getBestScanResultByLevel(list4), true, wifiNetworkSuggestion.getPriority()));
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    List<CandidateWifiConfig> getSavedNonPasspointCandidates(final String str, List<WifiNetworkSuggestion> list, List<ScanResult> list2) {
        ArrayList arrayList = new ArrayList();
        Map matchingScanResults = this.mWifiManager.getMatchingScanResults(list, list2);
        List list3 = (List) this.mWifiManager.getConfiguredNetworks().stream().filter(new Predicate() { // from class: com.google.android.wifitrackerlib.WsuPostProvisioningReceiver$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getSavedNonPasspointCandidates$3;
                lambda$getSavedNonPasspointCandidates$3 = WsuPostProvisioningReceiver.lambda$getSavedNonPasspointCandidates$3(str, (WifiConfiguration) obj);
                return lambda$getSavedNonPasspointCandidates$3;
            }
        }).collect(Collectors.toList());
        for (Map.Entry<WifiNetworkSuggestion, List<ScanResult>> entry : matchingScanResults.entrySet()) {
            if (entry.getKey().getPasspointConfig() == null) {
                Iterator it = list3.iterator();
                while (true) {
                    if (it.hasNext()) {
                        WifiConfiguration wifiConfiguration = (WifiConfiguration) it.next();
                        Optional<CandidateWifiConfig> homeCandidateIfMatched = getHomeCandidateIfMatched(wifiConfiguration, entry);
                        if (homeCandidateIfMatched.isPresent()) {
                            arrayList.add(homeCandidateIfMatched.get());
                            Log.d("WsuPostProvisioningReceiver", "matched a saved network: " + wifiConfiguration.getKey());
                            break;
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSavedNonPasspointCandidates$3(String str, WifiConfiguration wifiConfiguration) {
        return TextUtils.equals(wifiConfiguration.creatorName, str);
    }

    List<CandidateWifiConfig> getSuggestedNonPasspointCandidates(final String str, List<WifiNetworkSuggestion> list, List<ScanResult> list2) {
        ArrayList arrayList = new ArrayList();
        Map matchingScanResults = this.mWifiManager.getMatchingScanResults(list, list2);
        List list3 = (List) this.mWifiManager.getWifiConfigForMatchedNetworkSuggestionsSharedWithUser(list2).stream().filter(new Predicate() { // from class: com.google.android.wifitrackerlib.WsuPostProvisioningReceiver$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getSuggestedNonPasspointCandidates$4;
                lambda$getSuggestedNonPasspointCandidates$4 = WsuPostProvisioningReceiver.lambda$getSuggestedNonPasspointCandidates$4(str, (WifiConfiguration) obj);
                return lambda$getSuggestedNonPasspointCandidates$4;
            }
        }).collect(Collectors.toList());
        Log.d("WsuPostProvisioningReceiver", "matched suggestions size " + list3.size());
        for (Map.Entry<WifiNetworkSuggestion, List<ScanResult>> entry : matchingScanResults.entrySet()) {
            if (entry.getKey().getPasspointConfig() == null) {
                Iterator it = list3.iterator();
                while (true) {
                    if (it.hasNext()) {
                        Optional<CandidateWifiConfig> homeCandidateIfMatched = getHomeCandidateIfMatched((WifiConfiguration) it.next(), entry);
                        if (homeCandidateIfMatched.isPresent()) {
                            arrayList.add(homeCandidateIfMatched.get());
                            break;
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSuggestedNonPasspointCandidates$4(String str, WifiConfiguration wifiConfiguration) {
        return TextUtils.equals(wifiConfiguration.creatorName, str);
    }

    private Optional<CandidateWifiConfig> getHomeCandidateIfMatched(WifiConfiguration wifiConfiguration, Map.Entry<WifiNetworkSuggestion, List<ScanResult>> entry) {
        WifiConfiguration wifiConfiguration2 = entry.getKey().getWifiConfiguration();
        if (wifiConfiguration.carrierId == wifiConfiguration2.carrierId || TextUtils.equals(wifiConfiguration.getKey(), wifiConfiguration2.getKey())) {
            return Optional.of(new CandidateWifiConfig(wifiConfiguration, Utils.getBestScanResultByLevel(entry.getValue()), false, entry.getKey().getPriority()));
        }
        return Optional.empty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class CandidateWifiConfig {
        public ScanResult bestScanResult;
        public boolean isRoaming;
        public int priority;
        public WifiConfiguration wifiConfigFromSystem;

        CandidateWifiConfig(WifiConfiguration wifiConfiguration, ScanResult scanResult, boolean z, int i) {
            this.wifiConfigFromSystem = wifiConfiguration;
            this.bestScanResult = scanResult;
            this.isRoaming = z;
            this.priority = i;
        }
    }
}
