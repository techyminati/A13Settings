package com.android.settings.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.window.R;
import com.android.settings.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Objects;
/* loaded from: classes.dex */
public class RingVolumePreferenceController extends VolumeSeekBarPreferenceController {
    private static final String KEY_RING_VOLUME = "ring_volume";
    private static final String TAG = "RingVolumeController";
    private final H mHandler;
    private int mMuteIcon;
    private final RingReceiver mReceiver;
    private int mRingerMode;
    private ComponentName mSuppressor;
    private Vibrator mVibrator;

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getAudioStream() {
        return 2;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_RING_VOLUME;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean useDynamicSliceSummary() {
        return true;
    }

    public RingVolumePreferenceController(Context context) {
        this(context, KEY_RING_VOLUME);
    }

    public RingVolumePreferenceController(Context context, String str) {
        super(context, str);
        this.mRingerMode = -1;
        this.mReceiver = new RingReceiver();
        this.mHandler = new H();
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        this.mVibrator = vibrator;
        if (vibrator != null && !vibrator.hasVibrator()) {
            this.mVibrator = null;
        }
        updateRingerMode();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        super.onResume();
        this.mReceiver.register(true);
        updateEffectsSuppressor();
        updatePreferenceIcon();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        super.onPause();
        this.mReceiver.register(false);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!Utils.isVoiceCapable(this.mContext) || ((VolumeSeekBarPreferenceController) this).mHelper.isSingleVolume()) ? 3 : 0;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_RING_VOLUME);
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getMuteIcon() {
        return this.mMuteIcon;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRingerMode() {
        int ringerModeInternal = ((VolumeSeekBarPreferenceController) this).mHelper.getRingerModeInternal();
        if (this.mRingerMode != ringerModeInternal) {
            this.mRingerMode = ringerModeInternal;
            updatePreferenceIcon();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEffectsSuppressor() {
        ComponentName effectsSuppressor = NotificationManager.from(this.mContext).getEffectsSuppressor();
        if (!Objects.equals(effectsSuppressor, this.mSuppressor)) {
            this.mSuppressor = effectsSuppressor;
            if (this.mPreference != null) {
                this.mPreference.setSuppressionText(SuppressorHelper.getSuppressionText(this.mContext, effectsSuppressor));
            }
            updatePreferenceIcon();
        }
    }

    private void updatePreferenceIcon() {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            int i = this.mRingerMode;
            if (i == 1) {
                this.mMuteIcon = R.drawable.ic_volume_ringer_vibrate;
                volumeSeekBarPreference.showIcon(R.drawable.ic_volume_ringer_vibrate);
            } else if (i == 0) {
                this.mMuteIcon = R.drawable.ic_notifications_off_24dp;
                volumeSeekBarPreference.showIcon(R.drawable.ic_notifications_off_24dp);
            } else {
                volumeSeekBarPreference.showIcon(R.drawable.ic_notifications);
            }
        }
    }

    /* loaded from: classes.dex */
    private final class H extends Handler {
        private H() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                RingVolumePreferenceController.this.updateEffectsSuppressor();
            } else if (i == 2) {
                RingVolumePreferenceController.this.updateRingerMode();
            }
        }
    }

    /* loaded from: classes.dex */
    private class RingReceiver extends BroadcastReceiver {
        private boolean mRegistered;

        private RingReceiver() {
        }

        public void register(boolean z) {
            if (this.mRegistered != z) {
                if (z) {
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
                    intentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
                    ((AbstractPreferenceController) RingVolumePreferenceController.this).mContext.registerReceiver(this, intentFilter);
                } else {
                    ((AbstractPreferenceController) RingVolumePreferenceController.this).mContext.unregisterReceiver(this);
                }
                this.mRegistered = z;
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED".equals(action)) {
                RingVolumePreferenceController.this.mHandler.sendEmptyMessage(1);
            } else if ("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION".equals(action)) {
                RingVolumePreferenceController.this.mHandler.sendEmptyMessage(2);
            }
        }
    }
}
