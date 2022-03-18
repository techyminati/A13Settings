package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.security.KeyChain;
import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.settings.security.SecurityContentManager;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.crypto.SecretKey;
/* loaded from: classes.dex */
public class UserCredentialsSettings extends SettingsPreferenceFragment implements View.OnClickListener {
    private static final SparseArray<Credential.Type> credentialViewTypes;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 285;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshItems();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Credential credential = (Credential) view.getTag();
        if (credential != null) {
            CredentialDialogFragment.show(this, credential);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R.string.user_credentials);
    }

    protected void announceRemoval(String str) {
        if (isAdded()) {
            getListView().announceForAccessibility(getString(R.string.user_credential_removed, str));
        }
    }

    protected void refreshItems() {
        if (isAdded()) {
            new AliasLoader().execute(new Void[0]);
        }
    }

    /* loaded from: classes.dex */
    public static class CredentialDialogFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 533;
        }

        public static void show(Fragment fragment, Credential credential) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("credential", credential);
            if (fragment.getFragmentManager().findFragmentByTag("CredentialDialogFragment") == null) {
                CredentialDialogFragment credentialDialogFragment = new CredentialDialogFragment();
                credentialDialogFragment.setTargetFragment(fragment, -1);
                credentialDialogFragment.setArguments(bundle);
                credentialDialogFragment.show(fragment.getFragmentManager(), "CredentialDialogFragment");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            final Credential credential = (Credential) getArguments().getParcelable("credential");
            View inflate = getActivity().getLayoutInflater().inflate(R.layout.user_credential_dialog, (ViewGroup) null);
            ViewGroup viewGroup = (ViewGroup) inflate.findViewById(R.id.credential_container);
            viewGroup.addView(UserCredentialsSettings.getCredentialView(credential, R.layout.user_credential, null, viewGroup, true));
            AlertDialog.Builder positiveButton = new AlertDialog.Builder(getActivity()).setView(inflate).setTitle(R.string.user_credential_title).setPositiveButton(R.string.done, (DialogInterface.OnClickListener) null);
            final int myUserId = UserHandle.myUserId();
            if (!RestrictedLockUtilsInternal.hasBaseUserRestriction(getContext(), "no_config_credentials", myUserId)) {
                positiveButton.setNegativeButton(R.string.trusted_credentials_remove_label, new DialogInterface.OnClickListener() { // from class: com.android.settings.UserCredentialsSettings.CredentialDialogFragment.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(CredentialDialogFragment.this.getContext(), "no_config_credentials", myUserId);
                        if (checkIfRestrictionEnforced != null) {
                            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(CredentialDialogFragment.this.getContext(), checkIfRestrictionEnforced);
                        } else {
                            CredentialDialogFragment credentialDialogFragment = CredentialDialogFragment.this;
                            new RemoveCredentialsTask(credentialDialogFragment.getContext(), CredentialDialogFragment.this.getTargetFragment()).execute(credential);
                        }
                        dialogInterface.dismiss();
                    }
                });
            }
            return positiveButton.create();
        }

        /* loaded from: classes.dex */
        private class RemoveCredentialsTask extends AsyncTask<Credential, Void, Credential[]> {
            private Context context;
            private Fragment targetFragment;

            public RemoveCredentialsTask(Context context, Fragment fragment) {
                this.context = context;
                this.targetFragment = fragment;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            public Credential[] doInBackground(Credential... credentialArr) {
                for (Credential credential : credentialArr) {
                    if (credential.isSystem()) {
                        removeGrantsAndDelete(credential);
                    } else {
                        deleteWifiCredential(credential);
                    }
                }
                return credentialArr;
            }

            private void deleteWifiCredential(Credential credential) {
                try {
                    KeyStore instance = KeyStore.getInstance("AndroidKeyStore");
                    instance.load(new AndroidKeyStoreLoadStoreParameter(102));
                    instance.deleteEntry(credential.getAlias());
                } catch (Exception unused) {
                    throw new RuntimeException("Failed to delete keys from keystore.");
                }
            }

            private void removeGrantsAndDelete(Credential credential) {
                try {
                    try {
                        try {
                            KeyChain.bind(CredentialDialogFragment.this.getContext()).getService().removeKeyPair(credential.alias);
                        } catch (RemoteException e) {
                            Log.w("CredentialDialogFragment", "Removing credentials", e);
                        }
                    } finally {
                        this.close();
                    }
                } catch (InterruptedException e2) {
                    Log.w("CredentialDialogFragment", "Connecting to KeyChain", e2);
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            public void onPostExecute(Credential... credentialArr) {
                Fragment fragment = this.targetFragment;
                if ((fragment instanceof UserCredentialsSettings) && fragment.isAdded()) {
                    UserCredentialsSettings userCredentialsSettings = (UserCredentialsSettings) this.targetFragment;
                    for (Credential credential : credentialArr) {
                        userCredentialsSettings.announceRemoval(credential.alias);
                    }
                    userCredentialsSettings.refreshItems();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AliasLoader extends AsyncTask<Void, Void, List<Credential>> {
        private AliasLoader() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public List<Credential> doInBackground(Void... voidArr) {
            int myUserId = UserHandle.myUserId();
            int uid = UserHandle.getUid(myUserId, SecurityContentManager.DEFAULT_ORDER);
            int uid2 = UserHandle.getUid(myUserId, 1010);
            try {
                KeyStore instance = KeyStore.getInstance("AndroidKeyStore");
                KeyStore keyStore = null;
                instance.load(null);
                if (myUserId == 0) {
                    keyStore = KeyStore.getInstance("AndroidKeyStore");
                    keyStore.load(new AndroidKeyStoreLoadStoreParameter(102));
                }
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(getCredentialsForUid(instance, uid).values());
                if (keyStore != null) {
                    arrayList.addAll(getCredentialsForUid(keyStore, uid2).values());
                }
                return arrayList;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load credentials from Keystore.", e);
            }
        }

        private SortedMap<String, Credential> getCredentialsForUid(KeyStore keyStore, int i) {
            Key key;
            try {
                TreeMap treeMap = new TreeMap();
                boolean z = UserHandle.getAppId(i) == 1000;
                Enumeration<String> aliases = keyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String nextElement = aliases.nextElement();
                    Credential credential = new Credential(nextElement, i);
                    try {
                        key = keyStore.getKey(nextElement, null);
                    } catch (NoSuchAlgorithmException | UnrecoverableKeyException e) {
                        Log.e("UserCredentialsSettings", "Error tying to retrieve key: " + nextElement, e);
                    }
                    if (key != null) {
                        if (!(key instanceof SecretKey) && (!z || (!nextElement.startsWith("profile_key_name_encrypt_") && !nextElement.startsWith("profile_key_name_decrypt_") && !nextElement.startsWith("synthetic_password_")))) {
                            credential.storedTypes.add(Credential.Type.USER_KEY);
                            Certificate[] certificateChain = keyStore.getCertificateChain(nextElement);
                            if (certificateChain != null) {
                                credential.storedTypes.add(Credential.Type.USER_CERTIFICATE);
                                if (certificateChain.length > 1) {
                                    credential.storedTypes.add(Credential.Type.CA_CERTIFICATE);
                                }
                            }
                        }
                    } else if (keyStore.isCertificateEntry(nextElement)) {
                        credential.storedTypes.add(Credential.Type.CA_CERTIFICATE);
                    } else {
                        credential.storedTypes.add(Credential.Type.USER_CERTIFICATE);
                    }
                    treeMap.put(nextElement, credential);
                }
                return treeMap;
            } catch (KeyStoreException e2) {
                throw new RuntimeException("Failed to load credential from Android Keystore.", e2);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void onPostExecute(List<Credential> list) {
            if (UserCredentialsSettings.this.isAdded()) {
                if (list == null || list.size() == 0) {
                    TextView textView = (TextView) UserCredentialsSettings.this.getActivity().findViewById(16908292);
                    textView.setText(R.string.user_credential_none_installed);
                    UserCredentialsSettings.this.setEmptyView(textView);
                } else {
                    UserCredentialsSettings.this.setEmptyView(null);
                }
                UserCredentialsSettings.this.getListView().setAdapter(new CredentialAdapter(list, UserCredentialsSettings.this));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CredentialAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final List<Credential> mItems;
        private final View.OnClickListener mListener;

        public CredentialAdapter(List<Credential> list, View.OnClickListener onClickListener) {
            this.mItems = list;
            this.mListener = onClickListener;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_credential_preference, viewGroup, false));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            UserCredentialsSettings.getCredentialView(this.mItems.get(i), R.layout.user_credential_preference, viewHolder.itemView, null, false);
            viewHolder.itemView.setTag(this.mItems.get(i));
            viewHolder.itemView.setOnClickListener(this.mListener);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mItems.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    static {
        SparseArray<Credential.Type> sparseArray = new SparseArray<>();
        credentialViewTypes = sparseArray;
        sparseArray.put(R.id.contents_userkey, Credential.Type.USER_KEY);
        sparseArray.put(R.id.contents_usercrt, Credential.Type.USER_CERTIFICATE);
        sparseArray.put(R.id.contents_cacrt, Credential.Type.CA_CERTIFICATE);
    }

    protected static View getCredentialView(Credential credential, int i, View view, ViewGroup viewGroup, boolean z) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false);
        }
        ((TextView) view.findViewById(R.id.alias)).setText(credential.alias);
        ((TextView) view.findViewById(R.id.purpose)).setText(credential.isSystem() ? R.string.credential_for_vpn_and_apps : R.string.credential_for_wifi);
        view.findViewById(R.id.contents).setVisibility(z ? 0 : 8);
        if (z) {
            int i2 = 0;
            while (true) {
                SparseArray<Credential.Type> sparseArray = credentialViewTypes;
                if (i2 >= sparseArray.size()) {
                    break;
                }
                view.findViewById(sparseArray.keyAt(i2)).setVisibility(credential.storedTypes.contains(sparseArray.valueAt(i2)) ? 0 : 8);
                i2++;
            }
        }
        return view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Credential implements Parcelable {
        public static final Parcelable.Creator<Credential> CREATOR = new Parcelable.Creator<Credential>() { // from class: com.android.settings.UserCredentialsSettings.Credential.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Credential createFromParcel(Parcel parcel) {
                return new Credential(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Credential[] newArray(int i) {
                return new Credential[i];
            }
        };
        final String alias;
        final EnumSet<Type> storedTypes;
        final int uid;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public enum Type {
            CA_CERTIFICATE("CACERT_"),
            USER_CERTIFICATE("USRCERT_"),
            USER_KEY("USRPKEY_", "USRSKEY_");
            
            final String[] prefix;

            Type(String... strArr) {
                this.prefix = strArr;
            }
        }

        Credential(String str, int i) {
            this.storedTypes = EnumSet.noneOf(Type.class);
            this.alias = str;
            this.uid = i;
        }

        Credential(Parcel parcel) {
            this(parcel.readString(), parcel.readInt());
            Type[] values;
            long readLong = parcel.readLong();
            for (Type type : Type.values()) {
                if (((1 << type.ordinal()) & readLong) != 0) {
                    this.storedTypes.add(type);
                }
            }
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.alias);
            parcel.writeInt(this.uid);
            Iterator it = this.storedTypes.iterator();
            long j = 0;
            while (it.hasNext()) {
                j |= 1 << ((Type) it.next()).ordinal();
            }
            parcel.writeLong(j);
        }

        public boolean isSystem() {
            return UserHandle.getAppId(this.uid) == 1000;
        }

        public String getAlias() {
            return this.alias;
        }
    }
}
