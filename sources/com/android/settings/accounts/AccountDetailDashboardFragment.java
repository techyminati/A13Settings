package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceScreen;
import androidx.window.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.drawer.Tile;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AccountDetailDashboardFragment extends DashboardFragment {
    Account mAccount;
    private String mAccountLabel;
    private AccountSyncPreferenceController mAccountSynController;
    String mAccountType;
    private RemoveAccountPreferenceController mRemoveAccountController;
    UserHandle mUserHandle;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_account_detail;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccountDetailDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 8;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.account_type_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPreferenceManager().setPreferenceComparisonCallback(null);
        Bundle arguments = getArguments();
        FragmentActivity activity = getActivity();
        this.mUserHandle = Utils.getSecureTargetUser(activity.getActivityToken(), (UserManager) getSystemService("user"), arguments, activity.getIntent().getExtras());
        if (arguments != null) {
            if (arguments.containsKey("account")) {
                this.mAccount = (Account) arguments.getParcelable("account");
            }
            if (arguments.containsKey("account_label")) {
                this.mAccountLabel = arguments.getString("account_label");
            }
            if (arguments.containsKey("account_type")) {
                this.mAccountType = arguments.getString("account_type");
            }
        }
        this.mAccountSynController.init(this.mAccount, this.mUserHandle);
        this.mRemoveAccountController.init(this.mAccount, this.mUserHandle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mAccountLabel != null) {
            getActivity().setTitle(this.mAccountLabel);
        }
        updateUi();
    }

    void finishIfAccountMissing() {
        Context context = getContext();
        AccountManager accountManager = (AccountManager) context.getSystemService(AccountManager.class);
        for (UserHandle userHandle : ((UserManager) context.getSystemService(UserManager.class)).getUserProfiles()) {
            for (Account account : accountManager.getAccountsAsUser(userHandle.getIdentifier())) {
                if (account.equals(this.mAccount)) {
                    return;
                }
            }
        }
        finish();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        finishIfAccountMissing();
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        AccountSyncPreferenceController accountSyncPreferenceController = new AccountSyncPreferenceController(context);
        this.mAccountSynController = accountSyncPreferenceController;
        arrayList.add(accountSyncPreferenceController);
        RemoveAccountPreferenceController removeAccountPreferenceController = new RemoveAccountPreferenceController(context, this);
        this.mRemoveAccountController = removeAccountPreferenceController;
        arrayList.add(removeAccountPreferenceController);
        arrayList.add(new AccountHeaderPreferenceController(context, getSettingsLifecycle(), getActivity(), this, getArguments()));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public boolean displayTile(Tile tile) {
        Bundle metaData;
        if (!super.displayTile(tile) || this.mAccountType == null || (metaData = tile.getMetaData()) == null) {
            return false;
        }
        boolean equals = this.mAccountType.equals(metaData.getString("com.android.settings.ia.account"));
        if (equals) {
            Intent intent = tile.getIntent();
            intent.putExtra("extra.accountName", this.mAccount.name);
            intent.putExtra("android.intent.extra.USER", this.mUserHandle);
        }
        return equals;
    }

    void updateUi() {
        Context context = getContext();
        Bundle arguments = getArguments();
        UserHandle userHandle = (arguments == null || !arguments.containsKey("user_handle")) ? null : (UserHandle) arguments.getParcelable("user_handle");
        AccountTypePreferenceLoader accountTypePreferenceLoader = new AccountTypePreferenceLoader(this, new AuthenticatorHelper(context, userHandle, null), userHandle);
        PreferenceScreen addPreferencesForType = accountTypePreferenceLoader.addPreferencesForType(this.mAccountType, getPreferenceScreen());
        if (addPreferencesForType != null) {
            accountTypePreferenceLoader.updatePreferenceIntents(addPreferencesForType, this.mAccountType, this.mAccount);
        }
    }
}
