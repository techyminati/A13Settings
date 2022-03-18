package com.android.settings.dashboard.profileselector;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.window.R;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.drawer.Tile;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ProfileSelectDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private static final boolean DEBUG = Log.isLoggable("ProfileSelectDialog", 3);
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private DialogInterface.OnShowListener mOnShowListener;
    private Tile mSelectedTile;
    private int mSourceMetricCategory;

    public static void show(FragmentManager fragmentManager, Tile tile, int i, DialogInterface.OnShowListener onShowListener, DialogInterface.OnDismissListener onDismissListener, DialogInterface.OnCancelListener onCancelListener) {
        ProfileSelectDialog profileSelectDialog = new ProfileSelectDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("selectedTile", tile);
        bundle.putInt("sourceMetricCategory", i);
        profileSelectDialog.setArguments(bundle);
        profileSelectDialog.mOnShowListener = onShowListener;
        profileSelectDialog.mOnDismissListener = onDismissListener;
        profileSelectDialog.mOnCancelListener = onCancelListener;
        profileSelectDialog.show(fragmentManager, "select_profile");
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSelectedTile = (Tile) getArguments().getParcelable("selectedTile");
        this.mSourceMetricCategory = getArguments().getInt("sourceMetricCategory");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.choose_profile).setAdapter(UserAdapter.createUserAdapter(UserManager.get(activity), activity, this.mSelectedTile.userHandle), this);
        return builder.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        UserHandle userHandle = this.mSelectedTile.userHandle.get(i);
        Intent intent = new Intent(this.mSelectedTile.getIntent());
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        int i2 = this.mSourceMetricCategory;
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        metricsFeatureProvider.logStartedIntentWithProfile(intent, i2, z);
        intent.addFlags(32768);
        getActivity().startActivityAsUser(intent, userHandle);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        DialogInterface.OnShowListener onShowListener = this.mOnShowListener;
        if (onShowListener != null) {
            onShowListener.onShow(getDialog());
        }
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        DialogInterface.OnCancelListener onCancelListener = this.mOnCancelListener;
        if (onCancelListener != null) {
            onCancelListener.onCancel(dialogInterface);
        }
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        DialogInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
    }

    public static void updateUserHandlesIfNeeded(Context context, Tile tile) {
        ArrayList<UserHandle> arrayList = tile.userHandle;
        if (arrayList != null && arrayList.size() > 1) {
            UserManager userManager = UserManager.get(context);
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (userManager.getUserInfo(arrayList.get(size).getIdentifier()) == null) {
                    if (DEBUG) {
                        Log.d("ProfileSelectDialog", "Delete the user: " + arrayList.get(size).getIdentifier());
                    }
                    arrayList.remove(size);
                }
            }
        }
    }
}
