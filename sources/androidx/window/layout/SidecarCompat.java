package androidx.window.layout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.window.core.Version;
import androidx.window.layout.ExtensionInterfaceCompat;
import androidx.window.sidecar.SidecarDeviceState;
import androidx.window.sidecar.SidecarInterface;
import androidx.window.sidecar.SidecarProvider;
import androidx.window.sidecar.SidecarWindowLayoutInfo;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Unit;
import kotlin.collections.CollectionsKt__CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SidecarCompat.kt */
/* loaded from: classes.dex */
public final class SidecarCompat implements ExtensionInterfaceCompat {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static final String TAG = "SidecarCompat";
    @NotNull
    private final Map<Activity, ComponentCallbacks> componentCallbackMap;
    @Nullable
    private ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallback;
    @Nullable
    private final SidecarInterface sidecar;
    @NotNull
    private final SidecarAdapter sidecarAdapter;
    @NotNull
    private final Map<IBinder, Activity> windowListenerRegisteredContexts;

    public SidecarCompat(@Nullable SidecarInterface sidecarInterface, @NotNull SidecarAdapter sidecarAdapter) {
        Intrinsics.checkNotNullParameter(sidecarAdapter, "sidecarAdapter");
        this.sidecar = sidecarInterface;
        this.sidecarAdapter = sidecarAdapter;
        this.windowListenerRegisteredContexts = new LinkedHashMap();
        this.componentCallbackMap = new LinkedHashMap();
    }

    @Nullable
    public final SidecarInterface getSidecar() {
        return this.sidecar;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public SidecarCompat(@NotNull Context context) {
        this(Companion.getSidecarCompat$window_release(context), new SidecarAdapter(null, 1, null));
        Intrinsics.checkNotNullParameter(context, "context");
    }

    @Override // androidx.window.layout.ExtensionInterfaceCompat
    public void setExtensionCallback(@NotNull ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallback) {
        Intrinsics.checkNotNullParameter(extensionCallback, "extensionCallback");
        this.extensionCallback = new DistinctElementCallback(extensionCallback);
        SidecarInterface sidecarInterface = this.sidecar;
        if (sidecarInterface != null) {
            sidecarInterface.setSidecarCallback(new DistinctSidecarElementCallback(this.sidecarAdapter, new TranslatingCallback(this)));
        }
    }

    @NotNull
    public final WindowLayoutInfo getWindowLayoutInfo(@NotNull Activity activity) {
        List emptyList;
        Intrinsics.checkNotNullParameter(activity, "activity");
        IBinder activityWindowToken$window_release = Companion.getActivityWindowToken$window_release(activity);
        if (activityWindowToken$window_release == null) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return new WindowLayoutInfo(emptyList);
        }
        SidecarInterface sidecarInterface = this.sidecar;
        SidecarDeviceState sidecarDeviceState = null;
        SidecarWindowLayoutInfo windowLayoutInfo = sidecarInterface == null ? null : sidecarInterface.getWindowLayoutInfo(activityWindowToken$window_release);
        SidecarAdapter sidecarAdapter = this.sidecarAdapter;
        SidecarInterface sidecarInterface2 = this.sidecar;
        if (sidecarInterface2 != null) {
            sidecarDeviceState = sidecarInterface2.getDeviceState();
        }
        if (sidecarDeviceState == null) {
            sidecarDeviceState = new SidecarDeviceState();
        }
        return sidecarAdapter.translate(windowLayoutInfo, sidecarDeviceState);
    }

    @Override // androidx.window.layout.ExtensionInterfaceCompat
    public void onWindowLayoutChangeListenerAdded(@NotNull Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        IBinder activityWindowToken$window_release = Companion.getActivityWindowToken$window_release(activity);
        if (activityWindowToken$window_release != null) {
            register(activityWindowToken$window_release, activity);
            return;
        }
        activity.getWindow().getDecorView().addOnAttachStateChangeListener(new FirstAttachAdapter(this, activity));
    }

    public final void register(@NotNull IBinder windowToken, @NotNull Activity activity) {
        SidecarInterface sidecarInterface;
        Intrinsics.checkNotNullParameter(windowToken, "windowToken");
        Intrinsics.checkNotNullParameter(activity, "activity");
        this.windowListenerRegisteredContexts.put(windowToken, activity);
        SidecarInterface sidecarInterface2 = this.sidecar;
        if (sidecarInterface2 != null) {
            sidecarInterface2.onWindowLayoutChangeListenerAdded(windowToken);
        }
        if (this.windowListenerRegisteredContexts.size() == 1 && (sidecarInterface = this.sidecar) != null) {
            sidecarInterface.onDeviceStateListenersChanged(false);
        }
        ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallbackInterface = this.extensionCallback;
        if (extensionCallbackInterface != null) {
            extensionCallbackInterface.onWindowLayoutChanged(activity, getWindowLayoutInfo(activity));
        }
        registerConfigurationChangeListener(activity);
    }

    private final void registerConfigurationChangeListener(final Activity activity) {
        if (this.componentCallbackMap.get(activity) == null) {
            ComponentCallbacks sidecarCompat$registerConfigurationChangeListener$configChangeObserver$1 = new ComponentCallbacks() { // from class: androidx.window.layout.SidecarCompat$registerConfigurationChangeListener$configChangeObserver$1
                @Override // android.content.ComponentCallbacks
                public void onLowMemory() {
                }

                @Override // android.content.ComponentCallbacks
                public void onConfigurationChanged(@NotNull Configuration newConfig) {
                    Intrinsics.checkNotNullParameter(newConfig, "newConfig");
                    ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallbackInterface = SidecarCompat.this.extensionCallback;
                    if (extensionCallbackInterface != null) {
                        Activity activity2 = activity;
                        extensionCallbackInterface.onWindowLayoutChanged(activity2, SidecarCompat.this.getWindowLayoutInfo(activity2));
                    }
                }
            };
            this.componentCallbackMap.put(activity, sidecarCompat$registerConfigurationChangeListener$configChangeObserver$1);
            activity.registerComponentCallbacks(sidecarCompat$registerConfigurationChangeListener$configChangeObserver$1);
        }
    }

    @Override // androidx.window.layout.ExtensionInterfaceCompat
    public void onWindowLayoutChangeListenerRemoved(@NotNull Activity activity) {
        SidecarInterface sidecarInterface;
        Intrinsics.checkNotNullParameter(activity, "activity");
        IBinder activityWindowToken$window_release = Companion.getActivityWindowToken$window_release(activity);
        if (activityWindowToken$window_release != null) {
            SidecarInterface sidecarInterface2 = this.sidecar;
            if (sidecarInterface2 != null) {
                sidecarInterface2.onWindowLayoutChangeListenerRemoved(activityWindowToken$window_release);
            }
            unregisterComponentCallback(activity);
            boolean z = this.windowListenerRegisteredContexts.size() == 1;
            this.windowListenerRegisteredContexts.remove(activityWindowToken$window_release);
            if (z && (sidecarInterface = this.sidecar) != null) {
                sidecarInterface.onDeviceStateListenersChanged(true);
            }
        }
    }

    private final void unregisterComponentCallback(Activity activity) {
        activity.unregisterComponentCallbacks(this.componentCallbackMap.get(activity));
        this.componentCallbackMap.remove(activity);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x001f  */
    /* JADX WARN: Removed duplicated region for block: B:12:0x0021 A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x002d A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x005a A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0066 A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0083 A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x008f A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00aa  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00ab A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00b7 A[Catch: all -> 0x019d, TRY_LEAVE, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:81:0x016d A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0179 A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x0185 A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0191 A[Catch: all -> 0x019d, TryCatch #2 {all -> 0x019d, blocks: (B:3:0x0002, B:6:0x0009, B:9:0x0010, B:12:0x0021, B:13:0x0025, B:15:0x002d, B:18:0x0032, B:19:0x0035, B:22:0x003a, B:23:0x003d, B:26:0x0043, B:29:0x004a, B:32:0x005a, B:33:0x005e, B:35:0x0066, B:38:0x006c, B:41:0x0073, B:44:0x0083, B:45:0x0087, B:47:0x008f, B:50:0x0095, B:53:0x009c, B:56:0x00ab, B:57:0x00af, B:59:0x00b7, B:61:0x00bd, B:62:0x00c0, B:64:0x00ec, B:66:0x00f4, B:67:0x0110, B:68:0x0114, B:70:0x0144, B:73:0x014d, B:74:0x0154, B:75:0x0155, B:76:0x015c, B:77:0x015d, B:78:0x0164, B:79:0x0165, B:80:0x016c, B:81:0x016d, B:82:0x0178, B:83:0x0179, B:84:0x0184, B:85:0x0185, B:86:0x0190, B:87:0x0191, B:88:0x019c), top: B:95:0x0002, inners: #0, #1 }] */
    @Override // androidx.window.layout.ExtensionInterfaceCompat
    @android.annotation.SuppressLint({"BanUncheckedReflection"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean validateExtensionInterface() {
        /*
            Method dump skipped, instructions count: 415
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.window.layout.SidecarCompat.validateExtensionInterface():boolean");
    }

    /* compiled from: SidecarCompat.kt */
    /* loaded from: classes.dex */
    private static final class FirstAttachAdapter implements View.OnAttachStateChangeListener {
        @NotNull
        private final WeakReference<Activity> activityWeakReference;
        @NotNull
        private final SidecarCompat sidecarCompat;

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(@NotNull View view) {
            Intrinsics.checkNotNullParameter(view, "view");
        }

        public FirstAttachAdapter(@NotNull SidecarCompat sidecarCompat, @NotNull Activity activity) {
            Intrinsics.checkNotNullParameter(sidecarCompat, "sidecarCompat");
            Intrinsics.checkNotNullParameter(activity, "activity");
            this.sidecarCompat = sidecarCompat;
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(@NotNull View view) {
            Intrinsics.checkNotNullParameter(view, "view");
            view.removeOnAttachStateChangeListener(this);
            Activity activity = this.activityWeakReference.get();
            IBinder activityWindowToken$window_release = SidecarCompat.Companion.getActivityWindowToken$window_release(activity);
            if (activity != null && activityWindowToken$window_release != null) {
                this.sidecarCompat.register(activityWindowToken$window_release, activity);
            }
        }
    }

    /* compiled from: SidecarCompat.kt */
    /* loaded from: classes.dex */
    public final class TranslatingCallback implements SidecarInterface.SidecarCallback {
        final /* synthetic */ SidecarCompat this$0;

        public TranslatingCallback(SidecarCompat this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this.this$0 = this$0;
        }

        @SuppressLint({"SyntheticAccessor"})
        public void onDeviceStateChanged(@NotNull SidecarDeviceState newDeviceState) {
            SidecarInterface sidecar;
            Intrinsics.checkNotNullParameter(newDeviceState, "newDeviceState");
            Collection<Activity> values = this.this$0.windowListenerRegisteredContexts.values();
            SidecarCompat sidecarCompat = this.this$0;
            for (Activity activity : values) {
                IBinder activityWindowToken$window_release = SidecarCompat.Companion.getActivityWindowToken$window_release(activity);
                SidecarWindowLayoutInfo sidecarWindowLayoutInfo = null;
                if (!(activityWindowToken$window_release == null || (sidecar = sidecarCompat.getSidecar()) == null)) {
                    sidecarWindowLayoutInfo = sidecar.getWindowLayoutInfo(activityWindowToken$window_release);
                }
                ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallbackInterface = sidecarCompat.extensionCallback;
                if (extensionCallbackInterface != null) {
                    extensionCallbackInterface.onWindowLayoutChanged(activity, sidecarCompat.sidecarAdapter.translate(sidecarWindowLayoutInfo, newDeviceState));
                }
            }
        }

        @SuppressLint({"SyntheticAccessor"})
        public void onWindowLayoutChanged(@NotNull IBinder windowToken, @NotNull SidecarWindowLayoutInfo newLayout) {
            Intrinsics.checkNotNullParameter(windowToken, "windowToken");
            Intrinsics.checkNotNullParameter(newLayout, "newLayout");
            Activity activity = (Activity) this.this$0.windowListenerRegisteredContexts.get(windowToken);
            if (activity == null) {
                Log.w(SidecarCompat.TAG, "Unable to resolve activity from window token. Missing a call to #onWindowLayoutChangeListenerAdded()?");
                return;
            }
            SidecarAdapter sidecarAdapter = this.this$0.sidecarAdapter;
            SidecarInterface sidecar = this.this$0.getSidecar();
            SidecarDeviceState deviceState = sidecar == null ? null : sidecar.getDeviceState();
            if (deviceState == null) {
                deviceState = new SidecarDeviceState();
            }
            WindowLayoutInfo translate = sidecarAdapter.translate(newLayout, deviceState);
            ExtensionInterfaceCompat.ExtensionCallbackInterface extensionCallbackInterface = this.this$0.extensionCallback;
            if (extensionCallbackInterface != null) {
                extensionCallbackInterface.onWindowLayoutChanged(activity, translate);
            }
        }
    }

    /* compiled from: SidecarCompat.kt */
    /* loaded from: classes.dex */
    private static final class DistinctElementCallback implements ExtensionInterfaceCompat.ExtensionCallbackInterface {
        @NotNull
        private final ExtensionInterfaceCompat.ExtensionCallbackInterface callbackInterface;
        @NotNull
        private final ReentrantLock lock = new ReentrantLock();
        @NotNull
        private final WeakHashMap<Activity, WindowLayoutInfo> activityWindowLayoutInfo = new WeakHashMap<>();

        public DistinctElementCallback(@NotNull ExtensionInterfaceCompat.ExtensionCallbackInterface callbackInterface) {
            Intrinsics.checkNotNullParameter(callbackInterface, "callbackInterface");
            this.callbackInterface = callbackInterface;
        }

        @Override // androidx.window.layout.ExtensionInterfaceCompat.ExtensionCallbackInterface
        public void onWindowLayoutChanged(@NotNull Activity activity, @NotNull WindowLayoutInfo newLayout) {
            Intrinsics.checkNotNullParameter(activity, "activity");
            Intrinsics.checkNotNullParameter(newLayout, "newLayout");
            ReentrantLock reentrantLock = this.lock;
            reentrantLock.lock();
            try {
                if (!Intrinsics.areEqual(newLayout, this.activityWindowLayoutInfo.get(activity))) {
                    this.activityWindowLayoutInfo.put(activity, newLayout);
                    reentrantLock.unlock();
                    this.callbackInterface.onWindowLayoutChanged(activity, newLayout);
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    /* compiled from: SidecarCompat.kt */
    /* loaded from: classes.dex */
    private static final class DistinctSidecarElementCallback implements SidecarInterface.SidecarCallback {
        @NotNull
        private final SidecarInterface.SidecarCallback callbackInterface;
        @Nullable
        private SidecarDeviceState lastDeviceState;
        @NotNull
        private final ReentrantLock lock = new ReentrantLock();
        @NotNull
        private final WeakHashMap<IBinder, SidecarWindowLayoutInfo> mActivityWindowLayoutInfo = new WeakHashMap<>();
        @NotNull
        private final SidecarAdapter sidecarAdapter;

        public DistinctSidecarElementCallback(@NotNull SidecarAdapter sidecarAdapter, @NotNull SidecarInterface.SidecarCallback callbackInterface) {
            Intrinsics.checkNotNullParameter(sidecarAdapter, "sidecarAdapter");
            Intrinsics.checkNotNullParameter(callbackInterface, "callbackInterface");
            this.sidecarAdapter = sidecarAdapter;
            this.callbackInterface = callbackInterface;
        }

        public void onDeviceStateChanged(@NotNull SidecarDeviceState newDeviceState) {
            Intrinsics.checkNotNullParameter(newDeviceState, "newDeviceState");
            ReentrantLock reentrantLock = this.lock;
            reentrantLock.lock();
            try {
                if (!this.sidecarAdapter.isEqualSidecarDeviceState(this.lastDeviceState, newDeviceState)) {
                    this.lastDeviceState = newDeviceState;
                    this.callbackInterface.onDeviceStateChanged(newDeviceState);
                    Unit unit = Unit.INSTANCE;
                }
            } finally {
                reentrantLock.unlock();
            }
        }

        public void onWindowLayoutChanged(@NotNull IBinder token, @NotNull SidecarWindowLayoutInfo newLayout) {
            Intrinsics.checkNotNullParameter(token, "token");
            Intrinsics.checkNotNullParameter(newLayout, "newLayout");
            synchronized (this.lock) {
                if (!this.sidecarAdapter.isEqualSidecarWindowLayoutInfo(this.mActivityWindowLayoutInfo.get(token), newLayout)) {
                    this.mActivityWindowLayoutInfo.put(token, newLayout);
                    this.callbackInterface.onWindowLayoutChanged(token, newLayout);
                }
            }
        }
    }

    /* compiled from: SidecarCompat.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @Nullable
        public final Version getSidecarVersion() {
            try {
                String apiVersion = SidecarProvider.getApiVersion();
                if (!TextUtils.isEmpty(apiVersion)) {
                    return Version.Companion.parse(apiVersion);
                }
                return null;
            } catch (NoClassDefFoundError | UnsupportedOperationException unused) {
                return null;
            }
        }

        @Nullable
        public final SidecarInterface getSidecarCompat$window_release(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return SidecarProvider.getSidecarImpl(context.getApplicationContext());
        }

        @Nullable
        public final IBinder getActivityWindowToken$window_release(@Nullable Activity activity) {
            Window window;
            WindowManager.LayoutParams attributes;
            if (activity == null || (window = activity.getWindow()) == null || (attributes = window.getAttributes()) == null) {
                return null;
            }
            return attributes.token;
        }
    }
}
