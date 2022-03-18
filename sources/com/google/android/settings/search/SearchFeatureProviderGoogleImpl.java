package com.google.android.settings.search;

import android.content.Context;
import com.android.settings.search.SearchFeatureProviderImpl;
import com.google.android.settings.external.SignatureVerifier;
/* loaded from: classes2.dex */
public class SearchFeatureProviderGoogleImpl extends SearchFeatureProviderImpl {
    @Override // com.android.settings.search.SearchFeatureProviderImpl
    protected boolean isSignatureAllowlisted(Context context, String str) {
        return SignatureVerifier.isPackageAllowlisted(context, str);
    }
}
