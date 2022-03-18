package com.google.android.settings.gestures.assist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.gestures.AssistGestureFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.google.android.settings.gestures.assist.AssistGestureHelper;
import com.google.android.settings.gestures.assist.bubble.AssistGestureBubbleActivity;
/* loaded from: classes2.dex */
public class AssistGestureSensitivityPreferenceController extends SliderPreferenceController implements LifecycleObserver, OnPause, OnResume {
    public static final float DEFAULT_SENSITIVITY = 0.5f;
    private static final String PREF_KEY_VIDEO = "gesture_assist_video";
    private static final String PREF_KEY_VIDEO_SILENCE = "gesture_assist_video_silence";
    private static final String TAG = "AssistGesSensePrefCtrl";
    private AssistGestureHelper mAssistGestureHelper;
    private final AssistGestureFeatureProvider mFeatureProvider;
    private RestrictedLockUtils.EnforcedAdmin mFunDisallowedAdmin;
    private boolean mFunDisallowedBySystem;
    private SeekBarPreference mPreference;
    private PreferenceScreen mScreen;
    private final UserManager mUserManager;
    private boolean mWasListening;
    private final WindowManager mWindowManager;
    private long[] mHits = new long[4];
    private final AssistGestureHelper.GestureListener mGestureListener = new AssistGestureHelper.GestureListener() { // from class: com.google.android.settings.gestures.assist.AssistGestureSensitivityPreferenceController.1
        @Override // com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
        public void onGestureProgress(float f, int i) {
            AssistGestureSensitivityPreferenceController.this.mIndicatorView.onGestureProgress(f);
        }

        @Override // com.google.android.settings.gestures.assist.AssistGestureHelper.GestureListener
        public void onGestureDetected() {
            AssistGestureSensitivityPreferenceController.this.mIndicatorView.onGestureDetected();
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final SettingObserver mSettingObserver = new SettingObserver();
    private final AssistGestureIndicatorView mIndicatorView = new AssistGestureIndicatorView(this.mContext);

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMin() {
        return 0;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AssistGestureSensitivityPreferenceController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
        this.mAssistGestureHelper = new AssistGestureHelper(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mWindowManager = (WindowManager) context.getSystemService("window");
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mAssistGestureHelper.bindToElmyraServiceProxy();
        this.mSettingObserver.register();
        updatePreference();
        this.mFunDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_fun", UserHandle.myUserId());
        this.mFunDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_fun", UserHandle.myUserId());
        ((Activity) this.mContext).setRequestedOrientation(1);
        WindowManager windowManager = this.mWindowManager;
        AssistGestureIndicatorView assistGestureIndicatorView = this.mIndicatorView;
        windowManager.addView(assistGestureIndicatorView, assistGestureIndicatorView.getLayoutParams(((Activity) this.mContext).getWindow().getAttributes()));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        updateGestureListenerState(false);
        this.mAssistGestureHelper.unbindFromElmyraServiceProxy();
        this.mSettingObserver.unregister();
        this.mWindowManager.removeView(this.mIndicatorView);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = (SeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mScreen = preferenceScreen;
        if (!this.mFeatureProvider.isSupported(this.mContext)) {
            setVisible(preferenceScreen, PREF_KEY_VIDEO, false);
        } else {
            setVisible(preferenceScreen, PREF_KEY_VIDEO_SILENCE, false);
        }
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updatePreference();
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getSliderPosition() {
        return getSensitivityInt(this.mContext);
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public boolean setSliderPosition(int i) {
        return Settings.Secure.putFloat(this.mContext.getContentResolver(), "assist_gesture_sensitivity", convertSensitivityIntToFloat(this.mContext, i));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isAvailable(this.mContext, this.mFeatureProvider) ? 0 : 3;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "gesture_assist_sensitivity");
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMax() {
        return this.mContext.getResources().getInteger(R.integer.gesture_assist_sensitivity_max);
    }

    private void updateGestureListenerState(boolean z) {
        if (z != this.mWasListening) {
            if (z) {
                this.mAssistGestureHelper.setListener(this.mGestureListener);
            } else {
                this.mAssistGestureHelper.setListener(null);
            }
            this.mWasListening = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreference() {
        if (this.mPreference != null) {
            int sliderPosition = getSliderPosition();
            boolean z = false;
            if (!this.mFeatureProvider.isSupported(this.mContext)) {
                setVisible(this.mScreen, PREF_KEY_VIDEO, false);
                setVisible(this.mScreen, PREF_KEY_VIDEO_SILENCE, true);
            } else {
                setVisible(this.mScreen, PREF_KEY_VIDEO, true);
                setVisible(this.mScreen, PREF_KEY_VIDEO_SILENCE, false);
            }
            this.mPreference.setProgress(sliderPosition);
            boolean z2 = Settings.Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_enabled", 1) != 0;
            boolean z3 = Settings.Secure.getInt(this.mContext.getContentResolver(), "assist_gesture_silence_alerts_enabled", 1) != 0;
            if (this.mFeatureProvider.isSupported(this.mContext) && (z2 || z3)) {
                this.mPreference.setEnabled(true);
            } else if (!this.mFeatureProvider.isSensorAvailable(this.mContext) || !z3) {
                this.mPreference.setEnabled(false);
            } else {
                this.mPreference.setEnabled(true);
            }
            if ((z2 && this.mFeatureProvider.isSupported(this.mContext)) || z3) {
                z = true;
            }
            updateGestureListenerState(z);
        }
    }

    public static boolean isAvailable(Context context, AssistGestureFeatureProvider assistGestureFeatureProvider) {
        return assistGestureFeatureProvider.isSensorAvailable(context);
    }

    public static int getMaxSensitivityResourceInteger(Context context) {
        return context.getResources().getInteger(R.integer.gesture_assist_sensitivity_max);
    }

    public static int convertSensitivityFloatToInt(Context context, float f) {
        return Math.round(f * getMaxSensitivityResourceInteger(context));
    }

    public static float convertSensitivityIntToFloat(Context context, int i) {
        return 1.0f - (i / getMaxSensitivityResourceInteger(context));
    }

    public static float getSensitivity(Context context) {
        float f = 0.5f;
        float f2 = Settings.Secure.getFloat(context.getContentResolver(), "assist_gesture_sensitivity", 0.5f);
        if (f2 >= 0.0f && f2 <= 1.0f) {
            f = f2;
        }
        return 1.0f - f;
    }

    public static int getSensitivityInt(Context context) {
        return convertSensitivityFloatToInt(context, getSensitivity(context));
    }

    /* loaded from: classes2.dex */
    class SettingObserver extends ContentObserver {
        private final Uri ASSIST_GESTURE_ENABLED_URI = Settings.Secure.getUriFor("assist_gesture_enabled");
        private final Uri ASSIST_GESTURE_SILENCE_PHONE_ENABLED_URI = Settings.Secure.getUriFor("assist_gesture_silence_alerts_enabled");
        private final Uri ASSIST_GESTURE_SENSITIVITY_URI = Settings.Secure.getUriFor("assist_gesture_sensitivity");

        SettingObserver() {
            super(AssistGestureSensitivityPreferenceController.this.mHandler);
        }

        public void register() {
            ContentResolver contentResolver = ((AbstractPreferenceController) AssistGestureSensitivityPreferenceController.this).mContext.getContentResolver();
            contentResolver.registerContentObserver(this.ASSIST_GESTURE_ENABLED_URI, false, this);
            contentResolver.registerContentObserver(this.ASSIST_GESTURE_SILENCE_PHONE_ENABLED_URI, false, this);
            contentResolver.registerContentObserver(this.ASSIST_GESTURE_SENSITIVITY_URI, false, this);
        }

        public void unregister() {
            ((AbstractPreferenceController) AssistGestureSensitivityPreferenceController.this).mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            AssistGestureSensitivityPreferenceController.this.updatePreference();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        long[] jArr = this.mHits;
        System.arraycopy(jArr, 1, jArr, 0, jArr.length - 1);
        long[] jArr2 = this.mHits;
        jArr2[jArr2.length - 1] = SystemClock.uptimeMillis();
        if (this.mHits[0] >= SystemClock.uptimeMillis() - 500) {
            if (this.mUserManager.hasUserRestriction("no_fun")) {
                RestrictedLockUtils.EnforcedAdmin enforcedAdmin = this.mFunDisallowedAdmin;
                if (enforcedAdmin != null && !this.mFunDisallowedBySystem) {
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, enforcedAdmin);
                }
                return false;
            }
            Intent intent = new Intent(this.mContext, AssistGestureBubbleActivity.class);
            try {
                this.mContext.startActivity(intent);
                return true;
            } catch (Exception unused) {
                Log.e(TAG, "Unable to start activity " + intent.toString());
            }
        }
        return false;
    }
}
