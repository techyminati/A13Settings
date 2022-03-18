package com.android.settings.development;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.applications.RecentAppOpsAccess;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class OverlayCategoryPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private static final Comparator<OverlayInfo> OVERLAY_INFO_COMPARATOR = Comparator.comparingInt(OverlayCategoryPreferenceController$$ExternalSyntheticLambda2.INSTANCE);
    static final String PACKAGE_DEVICE_DEFAULT = "package_device_default";
    private final boolean mAvailable;
    private final String mCategory;
    private final IOverlayManager mOverlayManager;
    private final PackageManager mPackageManager;
    private ListPreference mPreference;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OverlayCategoryPreferenceController(Context context, PackageManager packageManager, IOverlayManager iOverlayManager, String str) {
        super(context);
        this.mOverlayManager = iOverlayManager;
        this.mPackageManager = packageManager;
        this.mCategory = str;
        this.mAvailable = iOverlayManager != null && !getOverlayInfos().isEmpty();
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mAvailable;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mCategory;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        setPreference((ListPreference) preferenceScreen.findPreference(getPreferenceKey()));
    }

    void setPreference(ListPreference listPreference) {
        this.mPreference = listPreference;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return setOverlay((String) obj);
    }

    private boolean setOverlay(final String str) {
        final String str2 = (String) getOverlayInfos().stream().filter(OverlayCategoryPreferenceController$$ExternalSyntheticLambda1.INSTANCE).map(OverlayCategoryPreferenceController$$ExternalSyntheticLambda0.INSTANCE).findFirst().orElse(null);
        if ((PACKAGE_DEVICE_DEFAULT.equals(str) && TextUtils.isEmpty(str2)) || TextUtils.equals(str, str2)) {
            return true;
        }
        new AsyncTask<Void, Void, Boolean>() { // from class: com.android.settings.development.OverlayCategoryPreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            public Boolean doInBackground(Void... voidArr) {
                try {
                    if (OverlayCategoryPreferenceController.PACKAGE_DEVICE_DEFAULT.equals(str)) {
                        return Boolean.valueOf(OverlayCategoryPreferenceController.this.mOverlayManager.setEnabled(str2, false, 0));
                    }
                    return Boolean.valueOf(OverlayCategoryPreferenceController.this.mOverlayManager.setEnabledExclusiveInCategory(str, 0));
                } catch (RemoteException | IllegalStateException | SecurityException e) {
                    Log.w("OverlayCategoryPC", "Error enabling overlay.", e);
                    return Boolean.FALSE;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            public void onPostExecute(Boolean bool) {
                OverlayCategoryPreferenceController overlayCategoryPreferenceController = OverlayCategoryPreferenceController.this;
                overlayCategoryPreferenceController.updateState(overlayCategoryPreferenceController.mPreference);
                if (!bool.booleanValue()) {
                    Toast.makeText(((AbstractPreferenceController) OverlayCategoryPreferenceController.this).mContext, (int) R.string.overlay_toast_failed_to_apply, 1).show();
                }
            }
        }.execute(new Void[0]);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        String string = this.mContext.getString(R.string.overlay_option_device_default);
        String str = PACKAGE_DEVICE_DEFAULT;
        arrayList.add(str);
        arrayList2.add(string);
        for (OverlayInfo overlayInfo : getOverlayInfos()) {
            arrayList.add(overlayInfo.packageName);
            try {
                arrayList2.add(this.mPackageManager.getApplicationInfo(overlayInfo.packageName, 0).loadLabel(this.mPackageManager).toString());
            } catch (PackageManager.NameNotFoundException unused) {
                arrayList2.add(overlayInfo.packageName);
            }
            if (overlayInfo.isEnabled()) {
                str = (String) arrayList.get(arrayList.size() - 1);
                string = (String) arrayList2.get(arrayList2.size() - 1);
            }
        }
        this.mPreference.setEntries((CharSequence[]) arrayList2.toArray(new String[arrayList2.size()]));
        this.mPreference.setEntryValues((CharSequence[]) arrayList.toArray(new String[arrayList.size()]));
        this.mPreference.setValue(str);
        this.mPreference.setSummary(string);
    }

    private List<OverlayInfo> getOverlayInfos() {
        ArrayList arrayList = new ArrayList();
        try {
            for (OverlayInfo overlayInfo : this.mOverlayManager.getOverlayInfosForTarget(RecentAppOpsAccess.ANDROID_SYSTEM_PACKAGE_NAME, 0)) {
                if (this.mCategory.equals(overlayInfo.category)) {
                    arrayList.add(overlayInfo);
                }
            }
            arrayList.sort(OVERLAY_INFO_COMPARATOR);
            return arrayList;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        setOverlay(PACKAGE_DEVICE_DEFAULT);
        updateState(this.mPreference);
    }
}
