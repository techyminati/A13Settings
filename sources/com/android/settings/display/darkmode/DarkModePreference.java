package com.android.settings.display.darkmode;

import android.app.UiModeManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.AttributeSet;
import androidx.window.R;
import com.android.settingslib.PrimarySwitchPreference;
import java.time.LocalTime;
/* loaded from: classes.dex */
public class DarkModePreference extends PrimarySwitchPreference {
    private Runnable mCallback;
    private DarkModeObserver mDarkModeObserver;
    private TimeFormatter mFormat;
    private PowerManager mPowerManager;
    private UiModeManager mUiModeManager;

    public DarkModePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDarkModeObserver = new DarkModeObserver(context);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mFormat = new TimeFormatter(context);
        Runnable darkModePreference$$ExternalSyntheticLambda0 = new Runnable() { // from class: com.android.settings.display.darkmode.DarkModePreference$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DarkModePreference.this.lambda$new$0();
            }
        };
        this.mCallback = darkModePreference$$ExternalSyntheticLambda0;
        this.mDarkModeObserver.subscribe(darkModePreference$$ExternalSyntheticLambda0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
        boolean z = (getContext().getResources().getConfiguration().uiMode & 32) != 0;
        setSwitchEnabled(!isPowerSaveMode);
        updateSummary(isPowerSaveMode, z);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDarkModeObserver.subscribe(this.mCallback);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        this.mDarkModeObserver.unsubscribe();
    }

    private void updateSummary(boolean z, boolean z2) {
        String str;
        LocalTime localTime;
        if (z) {
            setSummary(getContext().getString(z2 ? R.string.dark_ui_mode_disabled_summary_dark_theme_on : R.string.dark_ui_mode_disabled_summary_dark_theme_off));
            return;
        }
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            str = getContext().getString(z2 ? R.string.dark_ui_summary_on_auto_mode_auto : R.string.dark_ui_summary_off_auto_mode_auto);
        } else if (nightMode != 3) {
            str = getContext().getString(z2 ? R.string.dark_ui_summary_on_auto_mode_never : R.string.dark_ui_summary_off_auto_mode_never);
        } else if (this.mUiModeManager.getNightModeCustomType() == 1) {
            str = getContext().getString(z2 ? R.string.dark_ui_summary_on_auto_mode_custom_bedtime : R.string.dark_ui_summary_off_auto_mode_custom_bedtime);
        } else {
            if (z2) {
                localTime = this.mUiModeManager.getCustomNightModeEnd();
            } else {
                localTime = this.mUiModeManager.getCustomNightModeStart();
            }
            str = getContext().getString(z2 ? R.string.dark_ui_summary_on_auto_mode_custom : R.string.dark_ui_summary_off_auto_mode_custom, this.mFormat.of(localTime));
        }
        setSummary(str);
    }
}
