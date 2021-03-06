package com.google.android.settings.experiments;

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
/* loaded from: classes2.dex */
public class PhenotypeProxy {
    private static final Uri PROXY_AUTHORITY = new Uri.Builder().scheme("content").authority("com.google.android.settings.intelligence.provider.experimentflags").build();

    public static boolean getFlagByPackageAndKey(Context context, String str, String str2, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString("package_name", str);
        bundle.putString("key", str2);
        Bundle bundle2 = null;
        try {
            ContentProviderClient acquireUnstableContentProviderClient = context.getContentResolver().acquireUnstableContentProviderClient(PROXY_AUTHORITY);
            bundle2 = acquireUnstableContentProviderClient.call("getBooleanForPackageAndKey", null, bundle);
            acquireUnstableContentProviderClient.close();
        } catch (Exception e) {
            Log.e("PhenotypeProxy", "Failed to query experiment provider", e);
        }
        return bundle2 == null ? z : bundle2.getBoolean("value", z);
    }
}
