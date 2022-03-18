package androidx.core.app;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
/* loaded from: classes.dex */
public final class AppOpsManagerCompat {
    public static String permissionToOp(String str) {
        return AppOpsManager.permissionToOp(str);
    }

    public static int noteProxyOpNoThrow(Context context, String str, String str2) {
        return ((AppOpsManager) context.getSystemService(AppOpsManager.class)).noteProxyOpNoThrow(str, str2);
    }

    public static int checkOrNoteProxyOp(Context context, int i, String str, String str2) {
        AppOpsManager systemService = Api29Impl.getSystemService(context);
        int checkOpNoThrow = Api29Impl.checkOpNoThrow(systemService, str, Binder.getCallingUid(), str2);
        return checkOpNoThrow != 0 ? checkOpNoThrow : Api29Impl.checkOpNoThrow(systemService, str, i, Api29Impl.getOpPackageName(context));
    }

    /* loaded from: classes.dex */
    static class Api29Impl {
        static AppOpsManager getSystemService(Context context) {
            return (AppOpsManager) context.getSystemService(AppOpsManager.class);
        }

        static int checkOpNoThrow(AppOpsManager appOpsManager, String str, int i, String str2) {
            if (appOpsManager == null) {
                return 1;
            }
            return appOpsManager.checkOpNoThrow(str, i, str2);
        }

        static String getOpPackageName(Context context) {
            return context.getOpPackageName();
        }
    }
}
