package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class RoamingDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private CarrierConfigManager mCarrierConfigManager;
    private int mSubId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1583;
    }

    public static RoamingDialogFragment newInstance(int i) {
        RoamingDialogFragment roamingDialogFragment = new RoamingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_id_key", i);
        roamingDialogFragment.setArguments(bundle);
        return roamingDialogFragment;
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSubId = getArguments().getInt("sub_id_key");
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
        builder.setMessage(getResources().getString((configForSubId == null || !configForSubId.getBoolean("check_pricing_with_carrier_data_roaming_bool")) ? R.string.roaming_warning : R.string.roaming_check_price_warning)).setTitle(R.string.roaming_alert_title).setIconAttribute(16843605).setPositiveButton(17039379, this).setNegativeButton(17039369, this);
        return builder.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        TelephonyManager createForSubscriptionId;
        if (i == -1 && (createForSubscriptionId = ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId)) != null) {
            createForSubscriptionId.setDataRoamingEnabled(true);
        }
    }
}
