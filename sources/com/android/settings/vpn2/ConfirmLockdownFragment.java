package com.android.settings.vpn2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class ConfirmLockdownFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {

    /* loaded from: classes.dex */
    public interface ConfirmLockdownListener {
        void onConfirmLockdown(Bundle bundle, boolean z, boolean z2);
    }

    public static boolean shouldShow(boolean z, boolean z2, boolean z3) {
        return z || (z3 && !z2);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 548;
    }

    public static void show(Fragment fragment, boolean z, boolean z2, boolean z3, boolean z4, Bundle bundle) {
        if (fragment.getFragmentManager().findFragmentByTag("ConfirmLockdown") == null) {
            Bundle bundle2 = new Bundle();
            bundle2.putBoolean("replacing", z);
            bundle2.putBoolean("always_on", z2);
            bundle2.putBoolean("lockdown_old", z3);
            bundle2.putBoolean("lockdown_new", z4);
            bundle2.putParcelable("options", bundle);
            ConfirmLockdownFragment confirmLockdownFragment = new ConfirmLockdownFragment();
            confirmLockdownFragment.setArguments(bundle2);
            confirmLockdownFragment.setTargetFragment(fragment, 0);
            confirmLockdownFragment.show(fragment.getFragmentManager(), "ConfirmLockdown");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        boolean z = getArguments().getBoolean("replacing");
        getArguments().getBoolean("always_on");
        boolean z2 = getArguments().getBoolean("lockdown_old");
        boolean z3 = getArguments().getBoolean("lockdown_new");
        return new AlertDialog.Builder(getActivity()).setTitle(z3 ? R.string.vpn_require_connection_title : z ? R.string.vpn_replace_vpn_title : R.string.vpn_set_vpn_title).setMessage(z3 ? z ? R.string.vpn_replace_always_on_vpn_enable_message : R.string.vpn_first_always_on_vpn_message : z2 ? R.string.vpn_replace_always_on_vpn_disable_message : R.string.vpn_replace_vpn_message).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(z ? R.string.vpn_replace : z3 ? R.string.vpn_turn_on : R.string.okay, this).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (getTargetFragment() instanceof ConfirmLockdownListener) {
            ((ConfirmLockdownListener) getTargetFragment()).onConfirmLockdown((Bundle) getArguments().getParcelable("options"), getArguments().getBoolean("always_on"), getArguments().getBoolean("lockdown_new"));
        }
    }
}
