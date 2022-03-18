package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.window.R;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
/* loaded from: classes.dex */
public class ResetNetwork extends InstrumentedFragment {
    private View mContentView;
    CheckBox mEsimCheckbox;
    View mEsimContainer;
    private Button mInitiateButton;
    private final View.OnClickListener mInitiateListener = new View.OnClickListener() { // from class: com.android.settings.ResetNetwork.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (!ResetNetwork.this.runKeyguardConfirmation(55)) {
                ResetNetwork.this.showFinalConfirmation();
            }
        }
    };
    private Spinner mSubscriptionSpinner;
    private List<SubscriptionInfo> mSubscriptions;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 83;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R.string.reset_network_title);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(i).setTitle(getActivity().getResources().getText(R.string.reset_network_title)).show();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 55) {
            if (i2 == -1) {
                showFinalConfirmation();
            } else {
                establishInitialState(getActiveSubscriptionInfoList());
            }
        }
    }

    void showFinalConfirmation() {
        Bundle bundle = new Bundle();
        List<SubscriptionInfo> list = this.mSubscriptions;
        if (list != null && list.size() > 0) {
            bundle.putInt("android.telephony.extra.SUBSCRIPTION_INDEX", this.mSubscriptions.get(this.mSubscriptionSpinner.getSelectedItemPosition()).getSubscriptionId());
        }
        bundle.putBoolean("erase_esim", this.mEsimContainer.getVisibility() == 0 && this.mEsimCheckbox.isChecked());
        new SubSettingLauncher(getContext()).setDestination(ResetNetworkConfirm.class.getName()).setArguments(bundle).setTitleRes(R.string.reset_network_confirm_title).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    private void establishInitialState(List<SubscriptionInfo> list) {
        this.mSubscriptionSpinner = (Spinner) this.mContentView.findViewById(R.id.reset_network_subscription);
        this.mEsimContainer = this.mContentView.findViewById(R.id.erase_esim_container);
        this.mEsimCheckbox = (CheckBox) this.mContentView.findViewById(R.id.erase_esim);
        this.mSubscriptions = list;
        if (list == null || list.size() <= 0) {
            this.mSubscriptionSpinner.setVisibility(4);
        } else {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
                defaultDataSubscriptionId = SubscriptionManager.getDefaultVoiceSubscriptionId();
            }
            if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
                defaultDataSubscriptionId = SubscriptionManager.getDefaultSmsSubscriptionId();
            }
            if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
                defaultDataSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
            }
            this.mSubscriptions.size();
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (SubscriptionInfo subscriptionInfo : this.mSubscriptions) {
                if (subscriptionInfo.getSubscriptionId() == defaultDataSubscriptionId) {
                    i = arrayList.size();
                }
                String charSequence = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, getContext()).toString();
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = subscriptionInfo.getNumber();
                }
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = subscriptionInfo.getCarrierName().toString();
                }
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = String.format("MCC:%s MNC:%s Slot:%s Id:%s", Integer.valueOf(subscriptionInfo.getMcc()), Integer.valueOf(subscriptionInfo.getMnc()), Integer.valueOf(subscriptionInfo.getSimSlotIndex()), Integer.valueOf(subscriptionInfo.getSubscriptionId()));
                }
                arrayList.add(charSequence);
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), 17367048, arrayList);
            arrayAdapter.setDropDownViewResource(17367049);
            this.mSubscriptionSpinner.setAdapter((SpinnerAdapter) arrayAdapter);
            this.mSubscriptionSpinner.setSelection(i);
            if (this.mSubscriptions.size() > 1) {
                this.mSubscriptionSpinner.setVisibility(0);
            } else {
                this.mSubscriptionSpinner.setVisibility(4);
            }
        }
        Button button = (Button) this.mContentView.findViewById(R.id.initiate_reset_network);
        this.mInitiateButton = button;
        button.setOnClickListener(this.mInitiateListener);
        if (showEuiccSettings(getContext())) {
            this.mEsimContainer.setVisibility(0);
            this.mEsimContainer.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ResetNetwork.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ResetNetwork.this.mEsimCheckbox.toggle();
                }
            });
            return;
        }
        this.mEsimCheckbox.setChecked(false);
    }

    private List<SubscriptionInfo> getActiveSubscriptionInfoList() {
        SubscriptionManager subscriptionManager = (SubscriptionManager) getActivity().getSystemService(SubscriptionManager.class);
        if (subscriptionManager != null) {
            return (List) Optional.ofNullable(subscriptionManager.getActiveSubscriptionInfoList()).orElse(Collections.emptyList());
        }
        Log.w("ResetNetwork", "No SubscriptionManager");
        return Collections.emptyList();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList();
        List<SubscriptionInfo> list = this.mSubscriptions;
        if (list == null || list.size() != activeSubscriptionInfoList.size() || !this.mSubscriptions.containsAll(activeSubscriptionInfoList)) {
            Log.d("ResetNetwork", "subcription list changed");
            establishInitialState(activeSubscriptionInfoList);
        }
    }

    private boolean showEuiccSettings(Context context) {
        if (!((EuiccManager) context.getSystemService("euicc")).isEnabled()) {
            return false;
        }
        return Settings.Global.getInt(context.getContentResolver(), "euicc_provisioned", 0) != 0 || DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        UserManager userManager = UserManager.get(getActivity());
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_network_reset", UserHandle.myUserId());
        if (!userManager.isAdminUser() || RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_network_reset", UserHandle.myUserId())) {
            return layoutInflater.inflate(R.layout.network_reset_disallowed_screen, (ViewGroup) null);
        }
        if (checkIfRestrictionEnforced != null) {
            new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder("no_network_reset", checkIfRestrictionEnforced).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.ResetNetwork$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ResetNetwork.this.lambda$onCreateView$0(dialogInterface);
                }
            }).show();
            return new View(getContext());
        }
        this.mContentView = layoutInflater.inflate(R.layout.reset_network, (ViewGroup) null);
        establishInitialState(getActiveSubscriptionInfoList());
        return this.mContentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(DialogInterface dialogInterface) {
        getActivity().finish();
    }
}
