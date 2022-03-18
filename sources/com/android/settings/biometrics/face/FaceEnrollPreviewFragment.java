package com.android.settings.biometrics.face;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import androidx.window.R;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.face.ParticleCollection;
import com.android.settings.core.InstrumentedPreferenceFragment;
import java.util.Arrays;
/* loaded from: classes.dex */
public class FaceEnrollPreviewFragment extends InstrumentedPreferenceFragment implements BiometricEnrollSidecar.Listener {
    private FaceEnrollAnimationDrawable mAnimationDrawable;
    private CameraDevice mCameraDevice;
    private String mCameraId;
    private CameraManager mCameraManager;
    private CameraCaptureSession mCaptureSession;
    private ImageView mCircleView;
    private ParticleCollection.Listener mListener;
    private CaptureRequest mPreviewRequest;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private Size mPreviewSize;
    private FaceSquareTextureView mTextureView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final ParticleCollection.Listener mAnimationListener = new ParticleCollection.Listener() { // from class: com.android.settings.biometrics.face.FaceEnrollPreviewFragment.1
        @Override // com.android.settings.biometrics.face.ParticleCollection.Listener
        public void onEnrolled() {
            FaceEnrollPreviewFragment.this.mListener.onEnrolled();
        }
    };
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: com.android.settings.biometrics.face.FaceEnrollPreviewFragment.2
        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            FaceEnrollPreviewFragment.this.openCamera(i, i2);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            FaceEnrollPreviewFragment.this.configureTransform(i, i2);
        }
    };
    private final CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() { // from class: com.android.settings.biometrics.face.FaceEnrollPreviewFragment.3
        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onOpened(CameraDevice cameraDevice) {
            FaceEnrollPreviewFragment.this.mCameraDevice = cameraDevice;
            try {
                SurfaceTexture surfaceTexture = FaceEnrollPreviewFragment.this.mTextureView.getSurfaceTexture();
                surfaceTexture.setDefaultBufferSize(FaceEnrollPreviewFragment.this.mPreviewSize.getWidth(), FaceEnrollPreviewFragment.this.mPreviewSize.getHeight());
                Surface surface = new Surface(surfaceTexture);
                FaceEnrollPreviewFragment faceEnrollPreviewFragment = FaceEnrollPreviewFragment.this;
                faceEnrollPreviewFragment.mPreviewRequestBuilder = faceEnrollPreviewFragment.mCameraDevice.createCaptureRequest(1);
                FaceEnrollPreviewFragment.this.mPreviewRequestBuilder.addTarget(surface);
                FaceEnrollPreviewFragment.this.mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() { // from class: com.android.settings.biometrics.face.FaceEnrollPreviewFragment.3.1
                    @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
                    public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                        if (FaceEnrollPreviewFragment.this.mCameraDevice != null) {
                            FaceEnrollPreviewFragment.this.mCaptureSession = cameraCaptureSession;
                            try {
                                FaceEnrollPreviewFragment.this.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, 4);
                                FaceEnrollPreviewFragment faceEnrollPreviewFragment2 = FaceEnrollPreviewFragment.this;
                                faceEnrollPreviewFragment2.mPreviewRequest = faceEnrollPreviewFragment2.mPreviewRequestBuilder.build();
                                FaceEnrollPreviewFragment.this.mCaptureSession.setRepeatingRequest(FaceEnrollPreviewFragment.this.mPreviewRequest, null, FaceEnrollPreviewFragment.this.mHandler);
                            } catch (CameraAccessException e) {
                                Log.e("FaceEnrollPreviewFragment", "Unable to access camera", e);
                            }
                        }
                    }

                    @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
                    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                        Log.e("FaceEnrollPreviewFragment", "Unable to configure camera");
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            FaceEnrollPreviewFragment.this.mCameraDevice = null;
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onError(CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            FaceEnrollPreviewFragment.this.mCameraDevice = null;
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1554;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mTextureView = (FaceSquareTextureView) getActivity().findViewById(R.id.texture_view);
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.circle_view);
        this.mCircleView = imageView;
        imageView.setLayerType(1, null);
        FaceEnrollAnimationDrawable faceEnrollAnimationDrawable = new FaceEnrollAnimationDrawable(getContext(), this.mAnimationListener);
        this.mAnimationDrawable = faceEnrollAnimationDrawable;
        this.mCircleView.setImageDrawable(faceEnrollAnimationDrawable);
        this.mCameraManager = (CameraManager) getContext().getSystemService("camera");
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mTextureView.isAvailable()) {
            openCamera(this.mTextureView.getWidth(), this.mTextureView.getHeight());
        } else {
            this.mTextureView.setSurfaceTextureListener(this.mSurfaceTextureListener);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        closeCamera();
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        this.mAnimationDrawable.onEnrollmentError(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        this.mAnimationDrawable.onEnrollmentHelp(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        this.mAnimationDrawable.onEnrollmentProgressChange(i, i2);
    }

    public void setListener(ParticleCollection.Listener listener) {
        this.mListener = listener;
    }

    private void setUpCameraOutputs() {
        String[] cameraIdList;
        try {
            for (String str : this.mCameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = this.mCameraManager.getCameraCharacteristics(str);
                Integer num = (Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (num != null && num.intValue() == 0) {
                    this.mCameraId = str;
                    this.mPreviewSize = chooseOptimalSize(((StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceTexture.class));
                    return;
                }
            }
        } catch (CameraAccessException e) {
            Log.e("FaceEnrollPreviewFragment", "Unable to access camera", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openCamera(int i, int i2) {
        try {
            setUpCameraOutputs();
            this.mCameraManager.openCamera(this.mCameraId, this.mCameraStateCallback, this.mHandler);
            configureTransform(i, i2);
        } catch (CameraAccessException e) {
            Log.e("FaceEnrollPreviewFragment", "Unable to open camera", e);
        }
    }

    private Size chooseOptimalSize(Size[] sizeArr) {
        for (int i = 0; i < sizeArr.length; i++) {
            if (sizeArr[i].getHeight() == 1080 && sizeArr[i].getWidth() == 1920) {
                return sizeArr[i];
            }
        }
        Log.w("FaceEnrollPreviewFragment", "Unable to find a good resolution");
        return sizeArr[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configureTransform(int i, int i2) {
        if (this.mTextureView != null) {
            float width = i / this.mPreviewSize.getWidth();
            float height = i2 / this.mPreviewSize.getHeight();
            float min = Math.min(width, height);
            float f = width / min;
            float f2 = height / min;
            TypedValue typedValue = new TypedValue();
            TypedValue typedValue2 = new TypedValue();
            TypedValue typedValue3 = new TypedValue();
            getResources().getValue(R.dimen.face_preview_translate_x, typedValue, true);
            getResources().getValue(R.dimen.face_preview_translate_y, typedValue2, true);
            getResources().getValue(R.dimen.face_preview_scale, typedValue3, true);
            Matrix matrix = new Matrix();
            this.mTextureView.getTransform(matrix);
            matrix.setScale(f * typedValue3.getFloat(), f2 * typedValue3.getFloat());
            matrix.postTranslate(typedValue.getFloat(), typedValue2.getFloat());
            this.mTextureView.setTransform(matrix);
        }
    }

    private void closeCamera() {
        CameraCaptureSession cameraCaptureSession = this.mCaptureSession;
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            this.mCaptureSession = null;
        }
        CameraDevice cameraDevice = this.mCameraDevice;
        if (cameraDevice != null) {
            cameraDevice.close();
            this.mCameraDevice = null;
        }
    }
}
