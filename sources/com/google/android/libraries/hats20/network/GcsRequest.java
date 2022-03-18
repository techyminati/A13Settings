package com.google.android.libraries.hats20.network;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import com.google.android.libraries.hats20.storage.HatsDataStore;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class GcsRequest {
    public static final String USER_AGENT = String.format(Locale.US, "Mozilla/5.0; Hats App/v%d (Android %s; SDK %d; %s; %s; %s)", 2, Build.VERSION.RELEASE, Integer.valueOf(Build.VERSION.SDK_INT), Build.ID, Build.MODEL, Build.TAGS);
    private final HatsDataStore hatsDataStore;
    private final String postData;
    private final Uri requestUriWithNoParams;
    private final ResponseListener responseListener;

    /* loaded from: classes.dex */
    public interface ResponseListener {
        void onError(Exception exc);

        void onSuccess(GcsResponse gcsResponse);
    }

    public GcsRequest(ResponseListener responseListener, Uri uri, HatsDataStore hatsDataStore) {
        this.responseListener = responseListener;
        this.postData = uri.getEncodedQuery();
        this.requestUriWithNoParams = uri.buildUpon().clearQuery().build();
        this.hatsDataStore = hatsDataStore;
    }

    public void send() {
        Throwable th;
        Exception e;
        long currentTimeMillis;
        HttpURLConnection httpURLConnection;
        HttpURLConnection httpURLConnection2 = null;
        try {
            try {
                currentTimeMillis = System.currentTimeMillis();
                httpURLConnection = (HttpURLConnection) new URL(this.requestUriWithNoParams.toString()).openConnection();
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException | JSONException e2) {
            e = e2;
        }
        try {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] bytes = this.postData.getBytes("utf-8");
            httpURLConnection.setRequestProperty("Content-Length", Integer.toString(bytes.length));
            httpURLConnection.setRequestProperty("charset", "utf-8");
            httpURLConnection.setRequestProperty("Connection", "close");
            httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            httpURLConnection.setUseCaches(false);
            new DataOutputStream(httpURLConnection.getOutputStream()).write(bytes);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuffer.append(readLine);
            }
            bufferedReader.close();
            long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
            String stringBuffer2 = stringBuffer.toString();
            int length = stringBuffer2.length();
            StringBuilder sb = new StringBuilder(55);
            sb.append("Downloaded ");
            sb.append(length);
            sb.append(" bytes in ");
            sb.append(currentTimeMillis2);
            sb.append(" ms");
            Log.d("HatsLibGcsRequest", sb.toString());
            if (stringBuffer2.isEmpty()) {
                this.responseListener.onError(new IOException("GCS responded with no data. The site's publishing state may not be Enabled. Check Site > Advanced settings > Publishing state. For more info, see go/get-hats"));
            }
            this.hatsDataStore.storeSetCookieHeaders(this.requestUriWithNoParams, httpURLConnection.getHeaderFields());
            JSONObject jSONObject = new JSONObject(stringBuffer2).getJSONObject("params");
            int i = jSONObject.getInt("responseCode");
            long j = jSONObject.getLong("expirationDate");
            if (i != 0) {
                stringBuffer2 = "";
            }
            this.responseListener.onSuccess(new GcsResponse(i, j, stringBuffer2));
            httpURLConnection.disconnect();
        } catch (IOException | JSONException e3) {
            e = e3;
            httpURLConnection2 = httpURLConnection;
            this.responseListener.onError(e);
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
        } catch (Throwable th3) {
            th = th3;
            httpURLConnection2 = httpURLConnection;
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
            throw th;
        }
    }
}
