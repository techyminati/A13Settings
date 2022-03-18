package com.android.settings.wifi.addappnetworks;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.window.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;
import com.android.settingslib.wifi.WifiEnterpriseRestrictionUtils;
/* loaded from: classes.dex */
public class AddAppNetworksActivity extends FragmentActivity {
    @VisibleForTesting
    boolean mIsAddWifiConfigAllow;
    @VisibleForTesting
    final Bundle mBundle = new Bundle();
    @VisibleForTesting
    IActivityManager mActivityManager = ActivityManager.getService();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.settings_panel);
        if (!showAddNetworksFragment()) {
            finish();
            return;
        }
        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
        Window window = getWindow();
        window.setGravity(80);
        window.setLayout(-1, -2);
        this.mIsAddWifiConfigAllow = WifiEnterpriseRestrictionUtils.isAddWifiConfigAllowed(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (!showAddNetworksFragment()) {
            finish();
        }
    }

    @VisibleForTesting
    protected boolean showAddNetworksFragment() {
        if (!this.mIsAddWifiConfigAllow) {
            Log.d("AddAppNetworksActivity", "Not allowed by Enterprise Restriction");
            return false;
        }
        String callingAppPackageName = getCallingAppPackageName();
        if (TextUtils.isEmpty(callingAppPackageName)) {
            Log.d("AddAppNetworksActivity", "Package name is null");
            return false;
        }
        this.mBundle.putString("panel_calling_package_name", callingAppPackageName);
        this.mBundle.putParcelableArrayList("android.provider.extra.WIFI_NETWORK_LIST", getIntent().getParcelableArrayListExtra("android.provider.extra.WIFI_NETWORK_LIST"));
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag("AddAppNetworksActivity");
        if (findFragmentByTag == null) {
            AddAppNetworksFragment addAppNetworksFragment = new AddAppNetworksFragment();
            addAppNetworksFragment.setArguments(this.mBundle);
            supportFragmentManager.beginTransaction().add(R.id.main_content, addAppNetworksFragment, "AddAppNetworksActivity").commit();
            return true;
        }
        ((AddAppNetworksFragment) findFragmentByTag).createContent(this.mBundle);
        return true;
    }

    @VisibleForTesting
    protected String getCallingAppPackageName() {
        try {
            return this.mActivityManager.getLaunchedFromPackage(getActivityToken());
        } catch (RemoteException unused) {
            Log.e("AddAppNetworksActivity", "Can not get the package from activity manager");
            return null;
        }
    }
}
