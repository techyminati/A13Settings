package com.android.settings.development;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class SystemServerHeapDumpPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final UserManager mUserManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "system_server_heap_dump";
    }

    public SystemServerHeapDumpPreferenceController(Context context) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return Build.IS_DEBUGGABLE && !this.mUserManager.hasUserRestriction("no_debugging_features");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(final Preference preference) {
        if (!"system_server_heap_dump".equals(preference.getKey())) {
            return false;
        }
        try {
            preference.setEnabled(false);
            Toast.makeText(this.mContext, (int) R.string.capturing_system_heap_dump_message, 0).show();
            ActivityManager.getService().requestSystemServerHeapDump();
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.development.SystemServerHeapDumpPreferenceController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Preference.this.setEnabled(true);
                }
            }, 5000L);
            return true;
        } catch (RemoteException e) {
            Log.e("PrefControllerMixin", "error taking system heap dump", e);
            Toast.makeText(this.mContext, (int) R.string.error_capturing_system_heap_dump_message, 0).show();
            return false;
        }
    }
}
