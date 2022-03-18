package com.google.android.settings.experiments;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes2.dex */
public class GServicesProxy {
    private static final Uri PROXY_AUTHORITY = new Uri.Builder().scheme("content").authority("com.google.android.settings.intelligence.provider.serviceflags").build();

    public static boolean getBoolean(ContentResolver contentResolver, String str, boolean z) {
        Bundle buildRequest = buildRequest(str);
        buildRequest.putBoolean("default", z);
        Bundle result = getResult(contentResolver, "getBooleanForKey", buildRequest);
        return result == null ? z : result.getBoolean("value", z);
    }

    public static String getString(ContentResolver contentResolver, String str, String str2) {
        Bundle buildRequest = buildRequest(str);
        Bundle result = getResult(contentResolver, "getStringForKey", buildRequest);
        buildRequest.putString("default", str2);
        return result == null ? str2 : result.getString("value", str2);
    }

    public static long getLong(ContentResolver contentResolver, String str, long j) {
        Bundle buildRequest = buildRequest(str);
        buildRequest.putLong("default", j);
        Bundle result = getResult(contentResolver, "getLongForKey", buildRequest);
        return result == null ? j : result.getLong("value", j);
    }

    private static Bundle buildRequest(String str) {
        Bundle bundle = new Bundle();
        bundle.putString("key", str);
        return bundle;
    }

    private static Bundle getResult(final ContentResolver contentResolver, final String str, final Bundle bundle) {
        try {
            FutureTask futureTask = new FutureTask(new Callable() { // from class: com.google.android.settings.experiments.GServicesProxy$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    Bundle lambda$getResult$0;
                    lambda$getResult$0 = GServicesProxy.lambda$getResult$0(contentResolver, str, bundle);
                    return lambda$getResult$0;
                }
            });
            Executors.newSingleThreadExecutor().submit(futureTask);
            return (Bundle) futureTask.get(100L, TimeUnit.MILLISECONDS);
        } catch (TimeoutException unused) {
            Log.w("GServicesProxy", "Timeout to query service flag provider for method " + str);
            return null;
        } catch (Exception e) {
            Log.e("GServicesProxy", "Failed to query service flag provider for method " + str, e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Bundle lambda$getResult$0(ContentResolver contentResolver, String str, Bundle bundle) throws Exception {
        ContentProviderClient acquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(PROXY_AUTHORITY);
        try {
            Bundle call = acquireUnstableContentProviderClient.call(str, null, bundle);
            acquireUnstableContentProviderClient.close();
            return call;
        } catch (Throwable th) {
            if (acquireUnstableContentProviderClient != null) {
                try {
                    acquireUnstableContentProviderClient.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}
