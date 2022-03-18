package com.android.settings.display.darkmode;

import android.app.UiModeManager;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.time.LocalTime;
/* loaded from: classes.dex */
public class DarkModeActivationPreferenceController extends BasePreferenceController implements OnMainSwitchChangeListener {
    private TimeFormatter mFormat;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private MainSwitchPreference mPreference;
    private final UiModeManager mUiModeManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
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

    public DarkModeActivationPreferenceController(Context context, String str) {
        super(context, str);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mFormat = new TimeFormatter(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    public DarkModeActivationPreferenceController(Context context, String str, TimeFormatter timeFormatter) {
        this(context, str);
        this.mFormat = timeFormatter;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public final void updateState(Preference preference) {
        this.mPreference.updateStatus((this.mContext.getResources().getConfiguration().uiMode & 32) != 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        LocalTime localTime;
        boolean z = (this.mContext.getResources().getConfiguration().uiMode & 32) != 0;
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            return this.mContext.getString(z ? R.string.dark_ui_summary_on_auto_mode_auto : R.string.dark_ui_summary_off_auto_mode_auto);
        } else if (nightMode != 3) {
            return this.mContext.getString(z ? R.string.dark_ui_summary_on_auto_mode_never : R.string.dark_ui_summary_off_auto_mode_never);
        } else if (this.mUiModeManager.getNightModeCustomType() == 1) {
            return this.mContext.getString(z ? R.string.dark_ui_summary_on_auto_mode_custom_bedtime : R.string.dark_ui_summary_off_auto_mode_custom_bedtime);
        } else {
            if (z) {
                localTime = this.mUiModeManager.getCustomNightModeEnd();
            } else {
                localTime = this.mUiModeManager.getCustomNightModeStart();
            }
            return this.mContext.getString(z ? R.string.dark_ui_summary_on_auto_mode_custom : R.string.dark_ui_summary_off_auto_mode_custom, this.mFormat.of(localTime));
        }
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        this.mMetricsFeatureProvider.logClickedPreference(this.mPreference, getMetricsCategory());
        this.mUiModeManager.setNightModeActivated(!((this.mContext.getResources().getConfiguration().uiMode & 32) != 0));
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = mainSwitchPreference;
        mainSwitchPreference.addOnSwitchChangeListener(this);
    }
}
