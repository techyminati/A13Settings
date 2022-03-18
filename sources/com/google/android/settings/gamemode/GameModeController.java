package com.google.android.settings.gamemode;

import android.app.GameManager;
import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes2.dex */
public class GameModeController extends BasePreferenceController implements SelectorWithWidgetPreference.OnClickListener {
    @VisibleForTesting
    static final String GAME_MODE_BATTERY_PREFERENCE_KEY = "game_mode_battery";
    @VisibleForTesting
    static final String GAME_MODE_PERFORMANCE_PREFERENCE_KEY = "game_mode_performance";
    @VisibleForTesting
    static final String GAME_MODE_STANDARD_PREFERENCE_KEY = "game_mode_standard";
    private static final String TAG = "GameModeController";
    @VisibleForTesting
    SelectorWithWidgetPreference mBatteryRadioButtonPref;
    private GameManager mGameManager;
    private String mPackageName;
    @VisibleForTesting
    SelectorWithWidgetPreference mPerformanceRadioButtonPref;
    @VisibleForTesting
    SelectorWithWidgetPreference mStandardRadioButtonPref;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public GameModeController(Context context, String str) {
        super(context, str);
        this.mGameManager = (GameManager) context.getSystemService(GameManager.class);
    }

    public void init(String str) {
        this.mPackageName = str;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mStandardRadioButtonPref = (SelectorWithWidgetPreference) preferenceScreen.findPreference(GAME_MODE_STANDARD_PREFERENCE_KEY);
        this.mPerformanceRadioButtonPref = (SelectorWithWidgetPreference) preferenceScreen.findPreference(GAME_MODE_PERFORMANCE_PREFERENCE_KEY);
        this.mBatteryRadioButtonPref = (SelectorWithWidgetPreference) preferenceScreen.findPreference(GAME_MODE_BATTERY_PREFERENCE_KEY);
        int[] availableGameModes = this.mGameManager.getAvailableGameModes(this.mPackageName);
        int gameMode = this.mGameManager.getGameMode(this.mPackageName);
        boolean z = false;
        this.mStandardRadioButtonPref.setEnabled(false);
        this.mPerformanceRadioButtonPref.setEnabled(false);
        this.mBatteryRadioButtonPref.setEnabled(false);
        int length = availableGameModes.length;
        int i = 0;
        boolean z2 = false;
        while (true) {
            boolean z3 = true;
            if (i >= length) {
                break;
            }
            int i2 = availableGameModes[i];
            if (i2 == 2) {
                this.mPerformanceRadioButtonPref.setEnabled(true);
                SelectorWithWidgetPreference selectorWithWidgetPreference = this.mPerformanceRadioButtonPref;
                if (gameMode != 2) {
                    z3 = false;
                }
                selectorWithWidgetPreference.setChecked(z3);
            } else if (i2 == 3) {
                this.mBatteryRadioButtonPref.setEnabled(true);
                SelectorWithWidgetPreference selectorWithWidgetPreference2 = this.mBatteryRadioButtonPref;
                if (gameMode != 3) {
                    z3 = false;
                }
                selectorWithWidgetPreference2.setChecked(z3);
            } else if (i2 == 0) {
                z2 = true;
            }
            i++;
        }
        if (!z2) {
            this.mStandardRadioButtonPref.setEnabled(true);
            SelectorWithWidgetPreference selectorWithWidgetPreference3 = this.mStandardRadioButtonPref;
            if (gameMode == 1) {
                z = true;
            }
            selectorWithWidgetPreference3.setChecked(z);
        }
        if (gameMode == 0) {
            this.mStandardRadioButtonPref.setChecked(true);
        }
        this.mStandardRadioButtonPref.setOnClickListener(this);
        this.mPerformanceRadioButtonPref.setOnClickListener(this);
        this.mBatteryRadioButtonPref.setOnClickListener(this);
    }

    @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
    public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
        boolean z = true;
        if (selectorWithWidgetPreference.getKey().equals(GAME_MODE_STANDARD_PREFERENCE_KEY)) {
            this.mGameManager.setGameMode(this.mPackageName, 1);
        } else if (selectorWithWidgetPreference.getKey().equals(GAME_MODE_PERFORMANCE_PREFERENCE_KEY)) {
            this.mGameManager.setGameMode(this.mPackageName, 2);
        } else if (selectorWithWidgetPreference.getKey().equals(GAME_MODE_BATTERY_PREFERENCE_KEY)) {
            this.mGameManager.setGameMode(this.mPackageName, 3);
        }
        int gameMode = this.mGameManager.getGameMode(this.mPackageName);
        this.mStandardRadioButtonPref.setChecked(gameMode == 1);
        this.mPerformanceRadioButtonPref.setChecked(gameMode == 2);
        SelectorWithWidgetPreference selectorWithWidgetPreference2 = this.mBatteryRadioButtonPref;
        if (gameMode != 3) {
            z = false;
        }
        selectorWithWidgetPreference2.setChecked(z);
    }
}
