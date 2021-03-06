package com.android.settings.vpn2;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.internal.net.VpnProfile;
/* loaded from: classes.dex */
public class LegacyVpnPreference extends ManageablePreference {
    private VpnProfile mProfile;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LegacyVpnPreference(Context context) {
        super(context, null);
        setIcon(R.drawable.ic_vpn_key);
        setIconSize(2);
    }

    public VpnProfile getProfile() {
        return this.mProfile;
    }

    public void setProfile(VpnProfile vpnProfile) {
        VpnProfile vpnProfile2 = this.mProfile;
        String str = null;
        String str2 = vpnProfile2 != null ? vpnProfile2.name : null;
        if (vpnProfile != null) {
            str = vpnProfile.name;
        }
        if (!TextUtils.equals(str2, str)) {
            setTitle(str);
            notifyHierarchyChanged();
        }
        this.mProfile = vpnProfile;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.Preference
    public int compareTo(Preference preference) {
        if (preference instanceof LegacyVpnPreference) {
            LegacyVpnPreference legacyVpnPreference = (LegacyVpnPreference) preference;
            int i = legacyVpnPreference.mState - this.mState;
            if (i != 0) {
                return i;
            }
            int compareToIgnoreCase = this.mProfile.name.compareToIgnoreCase(legacyVpnPreference.mProfile.name);
            if (compareToIgnoreCase != 0) {
                return compareToIgnoreCase;
            }
            VpnProfile vpnProfile = this.mProfile;
            int i2 = vpnProfile.type;
            VpnProfile vpnProfile2 = legacyVpnPreference.mProfile;
            int i3 = i2 - vpnProfile2.type;
            return i3 == 0 ? vpnProfile.key.compareTo(vpnProfile2.key) : i3;
        } else if (!(preference instanceof AppPreference)) {
            return super.compareTo(preference);
        } else {
            return (this.mState == 3 || ((AppPreference) preference).getState() != 3) ? -1 : 1;
        }
    }

    @Override // com.android.settings.widget.GearPreference, android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() != R.id.settings_button || !isDisabledByAdmin()) {
            super.onClick(view);
        } else {
            performClick();
        }
    }
}
