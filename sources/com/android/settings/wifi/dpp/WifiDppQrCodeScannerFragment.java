package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.window.R;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settings.wifi.qrcode.QrCamera;
import com.android.settings.wifi.qrcode.QrDecorateView;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;
/* loaded from: classes.dex */
public class WifiDppQrCodeScannerFragment extends WifiDppQrCodeBaseFragment implements TextureView.SurfaceTextureListener, QrCamera.ScannerCallback, WifiManager.ActionListener {
    private QrCamera mCamera;
    private QrDecorateView mDecorateView;
    private WifiConfiguration mEnrolleeWifiConfiguration;
    private TextView mErrorMessage;
    private OnScanWifiDppSuccessListener mScanWifiDppSuccessListener;
    private String mSsid;
    private TextureView mTextureView;
    private WifiPickerTracker mWifiPickerTracker;
    private WifiQrCode mWifiQrCode;
    private HandlerThread mWorkerThread;
    private int mLatestStatusCode = 0;
    private final Handler mHandler = new Handler() { // from class: com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                WifiDppQrCodeScannerFragment.this.mErrorMessage.setVisibility(4);
            } else if (i == 2) {
                WifiDppQrCodeScannerFragment.this.mErrorMessage.setVisibility(0);
                WifiDppQrCodeScannerFragment.this.mErrorMessage.setText((String) message.obj);
                WifiDppQrCodeScannerFragment.this.mErrorMessage.sendAccessibilityEvent(32);
                removeMessages(1);
                sendEmptyMessageDelayed(1, 10000L);
                if (message.arg1 == 1) {
                    WifiDppQrCodeScannerFragment.this.setProgressBarShown(false);
                    WifiDppQrCodeScannerFragment.this.mDecorateView.setFocused(false);
                    WifiDppQrCodeScannerFragment.this.restartCamera();
                }
            } else if (i != 3) {
                if (i == 4) {
                    WifiManager wifiManager = (WifiManager) WifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class);
                    boolean z = false;
                    for (WifiConfiguration wifiConfiguration : ((WifiNetworkConfig) message.obj).getWifiConfigurations()) {
                        int addNetwork = wifiManager.addNetwork(wifiConfiguration);
                        if (addNetwork != -1) {
                            wifiManager.enableNetwork(addNetwork, false);
                            if (wifiConfiguration.hiddenSSID || WifiDppQrCodeScannerFragment.this.isReachableWifiNetwork(wifiConfiguration)) {
                                WifiDppQrCodeScannerFragment.this.mEnrolleeWifiConfiguration = wifiConfiguration;
                                wifiManager.connect(addNetwork, WifiDppQrCodeScannerFragment.this);
                                z = true;
                            }
                        }
                    }
                    if (!z) {
                        WifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(R.string.wifi_dpp_check_connection_try_again);
                        return;
                    }
                    ((InstrumentedFragment) WifiDppQrCodeScannerFragment.this).mMetricsFeatureProvider.action(((InstrumentedFragment) WifiDppQrCodeScannerFragment.this).mMetricsFeatureProvider.getAttribution(WifiDppQrCodeScannerFragment.this.getActivity()), 1711, 1596, null, Integer.MIN_VALUE);
                    WifiDppQrCodeScannerFragment.this.notifyUserForQrCodeRecognition();
                }
            } else if (WifiDppQrCodeScannerFragment.this.mScanWifiDppSuccessListener != null) {
                WifiDppQrCodeScannerFragment.this.mScanWifiDppSuccessListener.onScanWifiDppSuccess((WifiQrCode) message.obj);
                if (!WifiDppQrCodeScannerFragment.this.mIsConfiguratorMode) {
                    WifiDppQrCodeScannerFragment.this.setProgressBarShown(true);
                    WifiDppQrCodeScannerFragment.this.startWifiDppEnrolleeInitiator((WifiQrCode) message.obj);
                    WifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
                    WifiDppQrCodeScannerFragment.this.mSummary.sendAccessibilityEvent(32);
                }
                WifiDppQrCodeScannerFragment.this.notifyUserForQrCodeRecognition();
            }
        }
    };
    private boolean mIsConfiguratorMode = true;

    /* loaded from: classes.dex */
    public interface OnScanWifiDppSuccessListener {
        void onScanWifiDppSuccess(WifiQrCode wifiQrCode);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment
    protected boolean isFooterAvailable() {
        return false;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUserForQrCodeRecognition() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera != null) {
            qrCamera.stop();
        }
        this.mDecorateView.setFocused(true);
        this.mErrorMessage.setVisibility(4);
        WifiDppUtils.triggerVibrationForQrCodeRecognition(getContext());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isReachableWifiNetwork(WifiConfiguration wifiConfiguration) {
        List<WifiEntry> wifiEntries = this.mWifiPickerTracker.getWifiEntries();
        WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
        if (connectedWifiEntry != null) {
            wifiEntries.add(connectedWifiEntry);
        }
        for (WifiEntry wifiEntry : wifiEntries) {
            if (TextUtils.equals(wifiEntry.getSsid(), WifiInfo.sanitizeSsid(wifiConfiguration.SSID))) {
                int securityTypeFromWifiConfiguration = WifiDppUtils.getSecurityTypeFromWifiConfiguration(wifiConfiguration);
                if (securityTypeFromWifiConfiguration == wifiEntry.getSecurity()) {
                    return true;
                }
                if (securityTypeFromWifiConfiguration == 5 && wifiEntry.getSecurity() == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mIsConfiguratorMode = bundle.getBoolean("key_is_configurator_mode");
            this.mLatestStatusCode = bundle.getInt("key_latest_error_code");
            this.mEnrolleeWifiConfiguration = (WifiConfiguration) bundle.getParcelable("key_wifi_configuration");
        }
        final WifiDppInitiatorViewModel wifiDppInitiatorViewModel = (WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class);
        wifiDppInitiatorViewModel.getEnrolleeSuccessNetworkId().observe(this, new Observer() { // from class: com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment$$ExternalSyntheticLambda1
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                WifiDppQrCodeScannerFragment.this.lambda$onCreate$0(wifiDppInitiatorViewModel, (Integer) obj);
            }
        });
        wifiDppInitiatorViewModel.getStatusCode().observe(this, new Observer() { // from class: com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment$$ExternalSyntheticLambda0
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                WifiDppQrCodeScannerFragment.this.lambda$onCreate$1(wifiDppInitiatorViewModel, (Integer) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(WifiDppInitiatorViewModel wifiDppInitiatorViewModel, Integer num) {
        if (!wifiDppInitiatorViewModel.isWifiDppHandshaking()) {
            new EasyConnectEnrolleeStatusCallback().onEnrolleeSuccess(num.intValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(WifiDppInitiatorViewModel wifiDppInitiatorViewModel, Integer num) {
        if (!wifiDppInitiatorViewModel.isWifiDppHandshaking()) {
            int intValue = num.intValue();
            Log.d("WifiDppQrCodeScanner", "Easy connect enrollee callback onFailure " + intValue);
            new EasyConnectEnrolleeStatusCallback().onFailure(intValue);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera != null) {
            qrCamera.stop();
        }
        super.onPause();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (!isWifiDppHandshaking()) {
            restartCamera();
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return this.mIsConfiguratorMode ? 1595 : 1596;
    }

    public WifiDppQrCodeScannerFragment() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiDppQrCodeScannerFragment(String str) {
        this.mSsid = str;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        HandlerThread handlerThread = new HandlerThread("WifiDppQrCodeScanner{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        Clock clock = new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment.2
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        };
        Context context = getContext();
        this.mWifiPickerTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createWifiPickerTracker(getSettingsLifecycle(), context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), clock, 15000L, 10000L, null);
        if (this.mIsConfiguratorMode) {
            getActivity().setTitle(R.string.wifi_dpp_add_device_to_network);
        } else {
            getActivity().setTitle(R.string.wifi_dpp_scan_qr_code);
        }
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mScanWifiDppSuccessListener = (OnScanWifiDppSuccessListener) context;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        this.mScanWifiDppSuccessListener = null;
        super.onDetach();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        this.mWorkerThread.quit();
        super.onDestroyView();
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.wifi_dpp_qrcode_scanner_fragment, viewGroup, false);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mSummary = (TextView) view.findViewById(R.id.sud_layout_subtitle);
        TextureView textureView = (TextureView) view.findViewById(R.id.preview_view);
        this.mTextureView = textureView;
        textureView.setSurfaceTextureListener(this);
        this.mDecorateView = (QrDecorateView) view.findViewById(R.id.decorate_view);
        setProgressBarShown(isWifiDppHandshaking());
        if (this.mIsConfiguratorMode) {
            setHeaderTitle(R.string.wifi_dpp_add_device_to_network, new Object[0]);
            WifiNetworkConfig wifiNetworkConfig = ((WifiNetworkConfig.Retriever) getActivity()).getWifiNetworkConfig();
            if (WifiNetworkConfig.isValidConfig(wifiNetworkConfig)) {
                this.mSummary.setText(getString(R.string.wifi_dpp_center_qr_code, wifiNetworkConfig.getSsid()));
            } else {
                throw new IllegalStateException("Invalid Wi-Fi network for configuring");
            }
        } else {
            setHeaderTitle(R.string.wifi_dpp_scan_qr_code, new Object[0]);
            updateEnrolleeSummary();
        }
        this.mErrorMessage = (TextView) view.findViewById(R.id.error_message);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.removeItem(1);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        initCamera(surfaceTexture);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        destroyCamera();
        return true;
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public Size getViewSize() {
        return new Size(this.mTextureView.getWidth(), this.mTextureView.getHeight());
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public Rect getFramePosition(Size size, int i) {
        return new Rect(0, 0, size.getHeight(), size.getHeight());
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public void setTransform(Matrix matrix) {
        this.mTextureView.setTransform(matrix);
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public boolean isValid(String str) {
        try {
            WifiQrCode wifiQrCode = new WifiQrCode(str);
            this.mWifiQrCode = wifiQrCode;
            String scheme = wifiQrCode.getScheme();
            if (!this.mIsConfiguratorMode || !"WIFI".equals(scheme)) {
                return true;
            }
            showErrorMessage(R.string.wifi_dpp_qr_code_is_not_valid_format);
            return false;
        } catch (IllegalArgumentException unused) {
            showErrorMessage(R.string.wifi_dpp_qr_code_is_not_valid_format);
            return false;
        }
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public void handleSuccessfulResult(String str) {
        String scheme = this.mWifiQrCode.getScheme();
        scheme.hashCode();
        if (scheme.equals("DPP")) {
            handleWifiDpp();
        } else if (scheme.equals("WIFI")) {
            handleZxingWifiFormat();
        }
    }

    private void handleWifiDpp() {
        Message obtainMessage = this.mHandler.obtainMessage(3);
        obtainMessage.obj = new WifiQrCode(this.mWifiQrCode.getQrCode());
        this.mHandler.sendMessageDelayed(obtainMessage, 1000L);
    }

    private void handleZxingWifiFormat() {
        Message obtainMessage = this.mHandler.obtainMessage(4);
        obtainMessage.obj = new WifiQrCode(this.mWifiQrCode.getQrCode()).getWifiNetworkConfig();
        this.mHandler.sendMessageDelayed(obtainMessage, 1000L);
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public void handleCameraFailure() {
        destroyCamera();
    }

    private void initCamera(SurfaceTexture surfaceTexture) {
        if (this.mCamera == null) {
            this.mCamera = new QrCamera(getContext(), this);
            if (isWifiDppHandshaking()) {
                QrDecorateView qrDecorateView = this.mDecorateView;
                if (qrDecorateView != null) {
                    qrDecorateView.setFocused(true);
                    return;
                }
                return;
            }
            this.mCamera.start(surfaceTexture);
        }
    }

    private void destroyCamera() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera != null) {
            qrCamera.stop();
            this.mCamera = null;
        }
    }

    private void showErrorMessage(int i) {
        this.mHandler.obtainMessage(2, getString(i)).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showErrorMessageAndRestartCamera(int i) {
        Message obtainMessage = this.mHandler.obtainMessage(2, getString(i));
        obtainMessage.arg1 = 1;
        obtainMessage.sendToTarget();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("key_is_configurator_mode", this.mIsConfiguratorMode);
        bundle.putInt("key_latest_error_code", this.mLatestStatusCode);
        bundle.putParcelable("key_wifi_configuration", this.mEnrolleeWifiConfiguration);
        super.onSaveInstanceState(bundle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EasyConnectEnrolleeStatusCallback extends EasyConnectStatusCallback {
        public void onConfiguratorSuccess(int i) {
        }

        public void onProgress(int i) {
        }

        private EasyConnectEnrolleeStatusCallback() {
        }

        public void onEnrolleeSuccess(int i) {
            WifiManager wifiManager = (WifiManager) WifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class);
            for (WifiConfiguration wifiConfiguration : wifiManager.getPrivilegedConfiguredNetworks()) {
                if (wifiConfiguration.networkId == i) {
                    WifiDppQrCodeScannerFragment.this.mLatestStatusCode = 1;
                    WifiDppQrCodeScannerFragment.this.mEnrolleeWifiConfiguration = wifiConfiguration;
                    wifiManager.connect(wifiConfiguration, WifiDppQrCodeScannerFragment.this);
                    return;
                }
            }
            Log.e("WifiDppQrCodeScanner", "Invalid networkId " + i);
            WifiDppQrCodeScannerFragment.this.mLatestStatusCode = -7;
            WifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
            WifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(R.string.wifi_dpp_check_connection_try_again);
        }

        public void onFailure(int i) {
            Log.d("WifiDppQrCodeScanner", "EasyConnectEnrolleeStatusCallback.onFailure " + i);
            int i2 = R.string.wifi_dpp_failure_authentication_or_configuration;
            switch (i) {
                case -9:
                    throw new IllegalStateException("EASY_CONNECT_EVENT_FAILURE_INVALID_NETWORK should be a configurator only error");
                case -8:
                    throw new IllegalStateException("EASY_CONNECT_EVENT_FAILURE_NOT_SUPPORTED should be a configurator only error");
                case -7:
                    i2 = R.string.wifi_dpp_failure_generic;
                    break;
                case -6:
                    i2 = R.string.wifi_dpp_failure_timeout;
                    break;
                case -5:
                    if (i != WifiDppQrCodeScannerFragment.this.mLatestStatusCode) {
                        WifiDppQrCodeScannerFragment.this.mLatestStatusCode = i;
                        ((WifiManager) WifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class)).stopEasyConnectSession();
                        WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment = WifiDppQrCodeScannerFragment.this;
                        wifiDppQrCodeScannerFragment.startWifiDppEnrolleeInitiator(wifiDppQrCodeScannerFragment.mWifiQrCode);
                        return;
                    }
                    throw new IllegalStateException("stopEasyConnectSession and try again forEASY_CONNECT_EVENT_FAILURE_BUSY but still failed");
                case -4:
                case -2:
                    break;
                case -3:
                    i2 = R.string.wifi_dpp_failure_not_compatible;
                    break;
                case -1:
                    i2 = R.string.wifi_dpp_qr_code_is_not_valid_format;
                    break;
                default:
                    throw new IllegalStateException("Unexpected Wi-Fi DPP error");
            }
            WifiDppQrCodeScannerFragment.this.mLatestStatusCode = i;
            WifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
            WifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startWifiDppEnrolleeInitiator(WifiQrCode wifiQrCode) {
        ((WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class)).startEasyConnectAsEnrolleeInitiator(wifiQrCode.getQrCode());
    }

    public void onSuccess() {
        Intent intent = new Intent();
        intent.putExtra("key_wifi_configuration", this.mEnrolleeWifiConfiguration);
        FragmentActivity activity = getActivity();
        activity.setResult(-1, intent);
        activity.finish();
    }

    public void onFailure(int i) {
        Log.d("WifiDppQrCodeScanner", "Wi-Fi connect onFailure reason - " + i);
        showErrorMessageAndRestartCamera(R.string.wifi_dpp_check_connection_try_again);
    }

    private boolean isWifiDppHandshaking() {
        return ((WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class)).isWifiDppHandshaking();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartCamera() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera == null) {
            Log.d("WifiDppQrCodeScanner", "mCamera is not available for restarting camera");
            return;
        }
        if (qrCamera.isDecodeTaskAlive()) {
            this.mCamera.stop();
        }
        SurfaceTexture surfaceTexture = this.mTextureView.getSurfaceTexture();
        if (surfaceTexture != null) {
            this.mCamera.start(surfaceTexture);
            return;
        }
        throw new IllegalStateException("SurfaceTexture is not ready for restarting camera");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEnrolleeSummary() {
        if (isWifiDppHandshaking()) {
            this.mSummary.setText(R.string.wifi_dpp_connecting);
        } else {
            this.mSummary.setText(TextUtils.isEmpty(this.mSsid) ? getString(R.string.wifi_dpp_scan_qr_code_join_unknown_network, this.mSsid) : getString(R.string.wifi_dpp_scan_qr_code_join_network, this.mSsid));
        }
    }

    protected boolean isDecodeTaskAlive() {
        QrCamera qrCamera = this.mCamera;
        return qrCamera != null && qrCamera.isDecodeTaskAlive();
    }
}
