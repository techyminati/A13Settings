package com.android.settings.deviceinfo.imei;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.deviceinfo.PhoneNumberUtil;
import java.util.Arrays;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class ImeiInfoDialogFragment extends InstrumentedDialogFragment {
    static final String TAG = "ImeiInfoDialog";
    private static final int[] sViewIdsInDigitFormat = IntStream.of(R.id.meid_number_value, R.id.min_number_value, R.id.imei_value, R.id.imei_sv_value).sorted().toArray();
    private View mRootView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1240;
    }

    public static void show(Fragment fragment, int i, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(TAG) == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("arg_key_slot_id", i);
            bundle.putString("arg_key_dialog_title", str);
            ImeiInfoDialogFragment imeiInfoDialogFragment = new ImeiInfoDialogFragment();
            imeiInfoDialogFragment.setArguments(bundle);
            imeiInfoDialogFragment.show(childFragmentManager, TAG);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        int i = arguments.getInt("arg_key_slot_id");
        String string = arguments.getString("arg_key_dialog_title");
        ImeiInfoDialogController imeiInfoDialogController = new ImeiInfoDialogController(this, i);
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(getActivity()).setTitle(string).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        this.mRootView = LayoutInflater.from(positiveButton.getContext()).inflate(R.layout.dialog_imei_info, (ViewGroup) null);
        imeiInfoDialogController.populateImeiInfo();
        return positiveButton.setView(this.mRootView).create();
    }

    public void removeViewFromScreen(int i) {
        View findViewById = this.mRootView.findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(8);
        }
    }

    public void setText(int i, CharSequence charSequence) {
        TextView textView = (TextView) this.mRootView.findViewById(i);
        if (textView != null) {
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = getResources().getString(R.string.device_info_default);
            } else if (Arrays.binarySearch(sViewIdsInDigitFormat, i) >= 0) {
                charSequence = PhoneNumberUtil.expandByTts(charSequence);
            }
            textView.setText(charSequence);
        }
    }
}
