package com.android.settings.applications.managedomainurls;

import android.content.Context;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.util.IconDrawableFactory;
import androidx.window.R;
import com.android.settings.applications.intentpicker.IntentPickerUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.widget.AppPreference;
/* loaded from: classes.dex */
public class DomainAppPreference extends AppPreference {
    private final DomainVerificationManager mDomainVerificationManager;
    private final ApplicationsState.AppEntry mEntry;
    private final IconDrawableFactory mIconDrawableFactory;

    public DomainAppPreference(Context context, IconDrawableFactory iconDrawableFactory, ApplicationsState.AppEntry appEntry) {
        super(context);
        this.mIconDrawableFactory = iconDrawableFactory;
        this.mDomainVerificationManager = (DomainVerificationManager) context.getSystemService(DomainVerificationManager.class);
        this.mEntry = appEntry;
        appEntry.ensureLabel(getContext());
        setState();
    }

    public void reuse() {
        setState();
        notifyChanged();
    }

    public ApplicationsState.AppEntry getEntry() {
        return this.mEntry;
    }

    private void setState() {
        setTitle(this.mEntry.label);
        setIcon(this.mIconDrawableFactory.getBadgedIcon(this.mEntry.info));
        setSummary(getDomainsSummary(this.mEntry.info.packageName));
    }

    private CharSequence getDomainsSummary(String str) {
        return getContext().getText(isLinkHandlingAllowed(str) ? R.string.app_link_open_always : R.string.app_link_open_never);
    }

    private boolean isLinkHandlingAllowed(String str) {
        DomainVerificationUserState domainVerificationUserState = IntentPickerUtils.getDomainVerificationUserState(this.mDomainVerificationManager, str);
        if (domainVerificationUserState == null) {
            return false;
        }
        return domainVerificationUserState.isLinkHandlingAllowed();
    }
}
