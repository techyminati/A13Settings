package com.android.settingslib.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.util.Log;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public final class AuthenticatorHelper extends BroadcastReceiver {
    private final Context mContext;
    private final OnAccountsUpdateListener mListener;
    private boolean mListeningToAccountUpdates;
    private final UserHandle mUserHandle;
    private final Map<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();
    private final ArrayList<String> mEnabledAccountTypes = new ArrayList<>();
    private final Map<String, Drawable> mAccTypeIconCache = new HashMap();
    private final HashMap<String, ArrayList<String>> mAccountTypeToAuthorities = new HashMap<>();

    /* loaded from: classes.dex */
    public interface OnAccountsUpdateListener {
        void onAccountsUpdate(UserHandle userHandle);
    }

    public AuthenticatorHelper(Context context, UserHandle userHandle, OnAccountsUpdateListener onAccountsUpdateListener) {
        this.mContext = context;
        this.mUserHandle = userHandle;
        this.mListener = onAccountsUpdateListener;
        onAccountsUpdated(null);
    }

    public String[] getEnabledAccountTypes() {
        ArrayList<String> arrayList = this.mEnabledAccountTypes;
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public void preloadDrawableForType(final Context context, final String str) {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settingslib.accounts.AuthenticatorHelper.1
            /* JADX INFO: Access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                AuthenticatorHelper.this.getDrawableForType(context, str);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    public Drawable getDrawableForType(Context context, String str) {
        synchronized (this.mAccTypeIconCache) {
            if (this.mAccTypeIconCache.containsKey(str)) {
                return this.mAccTypeIconCache.get(str);
            }
            Drawable drawable = null;
            if (this.mTypeToAuthDescription.containsKey(str)) {
                try {
                    AuthenticatorDescription authenticatorDescription = this.mTypeToAuthDescription.get(str);
                    drawable = this.mContext.getPackageManager().getUserBadgedIcon(context.createPackageContextAsUser(authenticatorDescription.packageName, 0, this.mUserHandle).getDrawable(authenticatorDescription.iconId), this.mUserHandle);
                    synchronized (this.mAccTypeIconCache) {
                        this.mAccTypeIconCache.put(str, drawable);
                    }
                } catch (PackageManager.NameNotFoundException | Resources.NotFoundException unused) {
                }
            }
            if (drawable == null) {
                drawable = context.getPackageManager().getDefaultActivityIcon();
            }
            return Utils.getBadgedIcon(this.mContext, drawable, this.mUserHandle);
        }
    }

    public CharSequence getLabelForType(Context context, String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            try {
                AuthenticatorDescription authenticatorDescription = this.mTypeToAuthDescription.get(str);
                return context.createPackageContextAsUser(authenticatorDescription.packageName, 0, this.mUserHandle).getResources().getText(authenticatorDescription.labelId);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("AuthenticatorHelper", "No label name for account type " + str);
            } catch (Resources.NotFoundException unused2) {
                Log.w("AuthenticatorHelper", "No label icon for account type " + str);
            }
        }
        return null;
    }

    public String getPackageForType(String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            return this.mTypeToAuthDescription.get(str).packageName;
        }
        return null;
    }

    public int getLabelIdForType(String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            return this.mTypeToAuthDescription.get(str).labelId;
        }
        return -1;
    }

    public void updateAuthDescriptions(Context context) {
        AuthenticatorDescription[] authenticatorTypesAsUser = AccountManager.get(context).getAuthenticatorTypesAsUser(this.mUserHandle.getIdentifier());
        for (int i = 0; i < authenticatorTypesAsUser.length; i++) {
            this.mTypeToAuthDescription.put(authenticatorTypesAsUser[i].type, authenticatorTypesAsUser[i]);
        }
    }

    public boolean containsAccountType(String str) {
        return this.mTypeToAuthDescription.containsKey(str);
    }

    public AuthenticatorDescription getAccountTypeDescription(String str) {
        return this.mTypeToAuthDescription.get(str);
    }

    void onAccountsUpdated(Account[] accountArr) {
        updateAuthDescriptions(this.mContext);
        if (accountArr == null) {
            accountArr = AccountManager.get(this.mContext).getAccountsAsUser(this.mUserHandle.getIdentifier());
        }
        this.mEnabledAccountTypes.clear();
        this.mAccTypeIconCache.clear();
        for (Account account : accountArr) {
            if (!this.mEnabledAccountTypes.contains(account.type)) {
                this.mEnabledAccountTypes.add(account.type);
            }
        }
        buildAccountTypeToAuthoritiesMap();
        if (this.mListeningToAccountUpdates) {
            this.mListener.onAccountsUpdate(this.mUserHandle);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        onAccountsUpdated(AccountManager.get(this.mContext).getAccountsAsUser(this.mUserHandle.getIdentifier()));
    }

    public void listenToAccountUpdates() {
        if (!this.mListeningToAccountUpdates) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.accounts.LOGIN_ACCOUNTS_CHANGED");
            intentFilter.addAction("android.intent.action.DEVICE_STORAGE_OK");
            this.mContext.registerReceiverAsUser(this, this.mUserHandle, intentFilter, null, null);
            this.mListeningToAccountUpdates = true;
        }
    }

    public void stopListeningToAccountUpdates() {
        if (this.mListeningToAccountUpdates) {
            this.mContext.unregisterReceiver(this);
            this.mListeningToAccountUpdates = false;
        }
    }

    public ArrayList<String> getAuthoritiesForAccountType(String str) {
        return this.mAccountTypeToAuthorities.get(str);
    }

    private void buildAccountTypeToAuthoritiesMap() {
        SyncAdapterType[] syncAdapterTypesAsUser;
        this.mAccountTypeToAuthorities.clear();
        for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier())) {
            ArrayList<String> arrayList = this.mAccountTypeToAuthorities.get(syncAdapterType.accountType);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mAccountTypeToAuthorities.put(syncAdapterType.accountType, arrayList);
            }
            if (Log.isLoggable("AuthenticatorHelper", 2)) {
                Log.v("AuthenticatorHelper", "Added authority " + syncAdapterType.authority + " to accountType " + syncAdapterType.accountType);
            }
            arrayList.add(syncAdapterType.authority);
        }
    }
}
