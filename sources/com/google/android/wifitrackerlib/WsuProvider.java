package com.google.android.wifitrackerlib;

import android.text.TextUtils;
import java.util.Objects;
/* loaded from: classes2.dex */
public class WsuProvider {
    public final String helpUriString;
    public final String networkGroupIdentity;
    public final String servicePackageName;
    public final String wsuProviderName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsuProvider(String str, String str2, String str3, String str4) {
        this.wsuProviderName = str3;
        this.servicePackageName = str;
        this.networkGroupIdentity = str2;
        this.helpUriString = str4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getWsuIdentity() {
        return this.servicePackageName + "," + this.networkGroupIdentity;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WsuProvider)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        WsuProvider wsuProvider = (WsuProvider) obj;
        return TextUtils.equals(this.servicePackageName, wsuProvider.servicePackageName) && TextUtils.equals(this.networkGroupIdentity, wsuProvider.networkGroupIdentity);
    }

    public int hashCode() {
        return Objects.hash(this.servicePackageName, this.networkGroupIdentity);
    }

    public String toString() {
        return this.servicePackageName + ":" + this.networkGroupIdentity + ":" + this.wsuProviderName;
    }
}
