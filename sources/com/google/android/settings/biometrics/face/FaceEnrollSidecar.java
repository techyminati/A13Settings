package com.google.android.settings.biometrics.face;

import android.app.Activity;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Surface;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes2.dex */
public class FaceEnrollSidecar extends Fragment {
    private boolean mDebugConsent;
    private int[] mDisabledFeatures;
    private boolean mDone;
    private boolean mEnrolling;
    private CancellationSignal mEnrollmentCancel;
    private FaceManager mFaceManager;
    private Listener mListener;
    private PreviewSurfaceProvider mPreviewSurfaceProvider;
    private boolean mShouldManagePreview;
    private boolean mSingleFromMulti;
    private boolean mTalkbackEnabled;
    private byte[] mToken;
    private int mUserId;
    private int mEnrollmentSteps = -1;
    private int mEnrollmentRemaining = 0;
    private Handler mHandler = new Handler();
    private int mEnrollmentTypeVendorCode = 0;
    private final Runnable mTimeoutRunnable = new Runnable() { // from class: com.google.android.settings.biometrics.face.FaceEnrollSidecar.1
        @Override // java.lang.Runnable
        public void run() {
            FaceEnrollSidecar.this.cancelEnrollment();
        }
    };
    private final Runnable mStartEnrollRunnable = new Runnable() { // from class: com.google.android.settings.biometrics.face.FaceEnrollSidecar.2
        @Override // java.lang.Runnable
        public void run() {
            if (!FaceEnrollSidecar.this.mEnrolling) {
                FaceEnrollSidecar.this.startEnrollment();
            }
        }
    };
    private final FaceManager.EnrollmentCallback mEnrollmentCallback = new FaceManager.EnrollmentCallback() { // from class: com.google.android.settings.biometrics.face.FaceEnrollSidecar.3
        public void onEnrollmentProgress(int i) {
            FaceEnrollSidecar.this.onEnrollmentProgress(i);
        }

        public void onEnrollmentHelp(int i, CharSequence charSequence) {
            FaceEnrollSidecar.this.onEnrollmentHelp(i, charSequence);
        }

        public void onEnrollmentError(int i, CharSequence charSequence) {
            FaceEnrollSidecar.this.onEnrollmentError(i, charSequence);
        }
    };
    private ArrayList<QueuedEvent> mQueuedEvents = new ArrayList<>();

    /* loaded from: classes2.dex */
    public interface Listener {
        void onEnrollmentError(int i, CharSequence charSequence);

        void onEnrollmentHelp(int i, CharSequence charSequence);

        void onEnrollmentProgressChange(int i, int i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface PreviewSurfaceProvider {
        Surface getPreviewSurface();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public abstract class QueuedEvent {
        public abstract void send(Listener listener);

        private QueuedEvent() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class QueuedEnrollmentProgress extends QueuedEvent {
        int enrollmentSteps;
        int remaining;

        public QueuedEnrollmentProgress(int i, int i2) {
            super();
            this.enrollmentSteps = i;
            this.remaining = i2;
        }

        @Override // com.google.android.settings.biometrics.face.FaceEnrollSidecar.QueuedEvent
        public void send(Listener listener) {
            listener.onEnrollmentProgressChange(this.enrollmentSteps, this.remaining);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class QueuedEnrollmentHelp extends QueuedEvent {
        int helpMsgId;
        CharSequence helpString;

        public QueuedEnrollmentHelp(int i, CharSequence charSequence) {
            super();
            this.helpMsgId = i;
            this.helpString = charSequence;
        }

        @Override // com.google.android.settings.biometrics.face.FaceEnrollSidecar.QueuedEvent
        public void send(Listener listener) {
            listener.onEnrollmentHelp(this.helpMsgId, this.helpString);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class QueuedEnrollmentError extends QueuedEvent {
        int errMsgId;
        CharSequence errString;

        public QueuedEnrollmentError(int i, CharSequence charSequence) {
            super();
            this.errMsgId = i;
            this.errString = charSequence;
        }

        @Override // com.google.android.settings.biometrics.face.FaceEnrollSidecar.QueuedEvent
        public void send(Listener listener) {
            listener.onEnrollmentError(this.errMsgId, this.errString);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(int[] iArr, boolean z, boolean z2, boolean z3, boolean z4) {
        this.mDisabledFeatures = Arrays.copyOf(iArr, iArr.length);
        this.mSingleFromMulti = z;
        this.mTalkbackEnabled = z2;
        this.mShouldManagePreview = z3;
        this.mDebugConsent = z4;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mToken = activity.getIntent().getByteArrayExtra("hw_auth_token");
        this.mUserId = activity.getIntent().getIntExtra("android.intent.extra.USER_ID", -10000);
        this.mFaceManager = (FaceManager) activity.getSystemService(FaceManager.class);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mHandler.postDelayed(this.mStartEnrollRunnable, 750L);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (!getActivity().isChangingConfigurations()) {
            this.mHandler.removeCallbacks(this.mStartEnrollRunnable);
            cancelEnrollment();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startEnrollment() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        this.mEnrollmentSteps = -1;
        this.mEnrollmentCancel = new CancellationSignal();
        this.mEnrolling = true;
        boolean z = true;
        int i = 0;
        while (true) {
            int[] iArr = this.mDisabledFeatures;
            if (i >= iArr.length) {
                break;
            }
            if (iArr[i] == 2) {
                z = false;
            }
            i++;
        }
        if (this.mSingleFromMulti) {
            this.mEnrollmentTypeVendorCode = 2;
        } else if (z) {
            this.mEnrollmentTypeVendorCode = 0;
        } else {
            this.mEnrollmentTypeVendorCode = 1;
        }
        updateSettingsCache();
        Surface surface = null;
        if (!this.mShouldManagePreview) {
            PreviewSurfaceProvider previewSurfaceProvider = this.mPreviewSurfaceProvider;
            if (previewSurfaceProvider == null) {
                Log.e("FaceEnrollSidecar", "Preview surface provider is null");
            } else {
                surface = previewSurfaceProvider.getPreviewSurface();
                if (surface == null) {
                    Log.e("FaceEnrollSidecar", "Preview surface is null");
                }
            }
        }
        this.mFaceManager.enroll(this.mUserId, this.mToken, this.mEnrollmentCancel, this.mEnrollmentCallback, this.mDisabledFeatures, surface, this.mDebugConsent);
    }

    private void updateSettingsCache() {
        int i = 0;
        int i2 = 1;
        int i3 = 1;
        while (true) {
            int[] iArr = this.mDisabledFeatures;
            if (i < iArr.length) {
                if (iArr[i] == 1) {
                    i2 = 0;
                } else if (iArr[i] == 2) {
                    i3 = 0;
                }
                i++;
            } else {
                Settings.Secure.putIntForUser(getActivity().getContentResolver(), "face_unlock_attention_required", i2, this.mUserId);
                Settings.Secure.putIntForUser(getActivity().getContentResolver(), "face_unlock_diversity_required", i3, this.mUserId);
                return;
            }
        }
    }

    public boolean cancelEnrollment() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        if (!this.mEnrolling) {
            return false;
        }
        this.mEnrollmentCancel.cancel();
        this.mEnrolling = false;
        this.mEnrollmentSteps = -1;
        return true;
    }

    protected void onEnrollmentProgress(int i) {
        if (this.mEnrollmentSteps == -1) {
            this.mEnrollmentSteps = i;
        }
        this.mEnrollmentRemaining = i;
        this.mDone = i == 0;
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onEnrollmentProgressChange(this.mEnrollmentSteps, i);
        } else {
            this.mQueuedEvents.add(new QueuedEnrollmentProgress(this.mEnrollmentSteps, i));
        }
    }

    protected void onEnrollmentHelp(int i, CharSequence charSequence) {
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onEnrollmentHelp(i, charSequence);
        } else {
            this.mQueuedEvents.add(new QueuedEnrollmentHelp(i, charSequence));
        }
    }

    protected void onEnrollmentError(int i, CharSequence charSequence) {
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onEnrollmentError(i, charSequence);
        } else {
            this.mQueuedEvents.add(new QueuedEnrollmentError(i, charSequence));
        }
        this.mEnrolling = false;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
        if (listener != null) {
            for (int i = 0; i < this.mQueuedEvents.size(); i++) {
                this.mQueuedEvents.get(i).send(this.mListener);
            }
            this.mQueuedEvents.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPreviewSurfaceProvider(PreviewSurfaceProvider previewSurfaceProvider) {
        this.mPreviewSurfaceProvider = previewSurfaceProvider;
    }

    public boolean isEnrolling() {
        return this.mEnrolling;
    }

    public void logEnrollmentEnded(int i, boolean z) {
        if (i == 0) {
            logEnrollmentTimeout();
        } else if (i == 1) {
            logEnrollmentSuccess(z);
        } else if (i == 2) {
            logEnrollmentError();
        }
    }

    private void logEnrollmentTimeout() {
        int i = this.mEnrollmentTypeVendorCode;
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    if (this.mTalkbackEnabled) {
                        FaceUtils.writeVendorLog(this.mUserId, 1159);
                    } else {
                        FaceUtils.writeVendorLog(this.mUserId, 1156);
                    }
                }
            } else if (this.mTalkbackEnabled) {
                FaceUtils.writeVendorLog(this.mUserId, 1153);
            } else {
                FaceUtils.writeVendorLog(this.mUserId, 1146);
            }
        } else if (this.mTalkbackEnabled) {
            FaceUtils.writeVendorLog(this.mUserId, 1149);
        } else {
            FaceUtils.writeVendorLog(this.mUserId, 1142);
        }
    }

    private void logEnrollmentSuccess(boolean z) {
        int i = this.mEnrollmentTypeVendorCode;
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    if (this.mTalkbackEnabled) {
                        FaceUtils.writeVendorLog(this.mUserId, 1160);
                    } else {
                        FaceUtils.writeVendorLog(this.mUserId, 1157);
                    }
                }
            } else if (this.mTalkbackEnabled) {
                FaceUtils.writeVendorLog(this.mUserId, 1154);
            } else {
                FaceUtils.writeVendorLog(this.mUserId, 1147);
            }
        } else if (z) {
            if (this.mTalkbackEnabled) {
                FaceUtils.writeVendorLog(this.mUserId, 1150);
            } else {
                FaceUtils.writeVendorLog(this.mUserId, 1143);
            }
        } else if (this.mTalkbackEnabled) {
            FaceUtils.writeVendorLog(this.mUserId, 1151);
        } else {
            FaceUtils.writeVendorLog(this.mUserId, 1144);
        }
    }

    private void logEnrollmentError() {
        int i = this.mEnrollmentTypeVendorCode;
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    if (this.mTalkbackEnabled) {
                        FaceUtils.writeVendorLog(this.mUserId, 1161);
                    } else {
                        FaceUtils.writeVendorLog(this.mUserId, 1158);
                    }
                }
            } else if (this.mTalkbackEnabled) {
                FaceUtils.writeVendorLog(this.mUserId, 1155);
            } else {
                FaceUtils.writeVendorLog(this.mUserId, 1148);
            }
        } else if (this.mTalkbackEnabled) {
            FaceUtils.writeVendorLog(this.mUserId, 1152);
        } else {
            FaceUtils.writeVendorLog(this.mUserId, 1145);
        }
    }
}
