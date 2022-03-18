package androidx.window.embedding;

import android.app.Activity;
import android.util.Log;
import androidx.window.core.ExperimentalWindowApi;
import androidx.window.embedding.EmbeddingInterfaceCompat;
import androidx.window.extensions.WindowExtensionsProvider;
import androidx.window.extensions.embedding.ActivityEmbeddingComponent;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: EmbeddingCompat.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class EmbeddingCompat implements EmbeddingInterfaceCompat {
    @NotNull
    public static final Companion Companion = new Companion(null);
    public static final boolean DEBUG = true;
    @NotNull
    private static final String TAG = "EmbeddingCompat";
    @NotNull
    private final EmbeddingAdapter adapter;
    @NotNull
    private final ActivityEmbeddingComponent embeddingExtension;

    public EmbeddingCompat(@NotNull ActivityEmbeddingComponent embeddingExtension, @NotNull EmbeddingAdapter adapter) {
        Intrinsics.checkNotNullParameter(embeddingExtension, "embeddingExtension");
        Intrinsics.checkNotNullParameter(adapter, "adapter");
        this.embeddingExtension = embeddingExtension;
        this.adapter = adapter;
    }

    public EmbeddingCompat() {
        this(Companion.embeddingComponent(), new EmbeddingAdapter());
    }

    @Override // androidx.window.embedding.EmbeddingInterfaceCompat
    public void setSplitRules(@NotNull Set<? extends EmbeddingRule> rules) {
        Intrinsics.checkNotNullParameter(rules, "rules");
        this.embeddingExtension.setEmbeddingRules(this.adapter.translate(rules));
    }

    @Override // androidx.window.embedding.EmbeddingInterfaceCompat
    public void setEmbeddingCallback(@NotNull final EmbeddingInterfaceCompat.EmbeddingCallbackInterface embeddingCallback) {
        Intrinsics.checkNotNullParameter(embeddingCallback, "embeddingCallback");
        try {
            this.embeddingExtension.setSplitInfoCallback(new Consumer() { // from class: androidx.window.embedding.EmbeddingCompat$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    EmbeddingCompat.m12setEmbeddingCallback$lambda0(EmbeddingInterfaceCompat.EmbeddingCallbackInterface.this, this, (List) obj);
                }
            });
        } catch (NoSuchMethodError unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: setEmbeddingCallback$lambda-0  reason: not valid java name */
    public static final void m12setEmbeddingCallback$lambda0(EmbeddingInterfaceCompat.EmbeddingCallbackInterface embeddingCallback, EmbeddingCompat this$0, List splitInfoList) {
        Intrinsics.checkNotNullParameter(embeddingCallback, "$embeddingCallback");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        EmbeddingAdapter embeddingAdapter = this$0.adapter;
        Intrinsics.checkNotNullExpressionValue(splitInfoList, "splitInfoList");
        embeddingCallback.onSplitInfoChanged(embeddingAdapter.translate(splitInfoList));
    }

    @Override // androidx.window.embedding.EmbeddingInterfaceCompat
    public boolean isActivityEmbedded(@NotNull Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        return this.embeddingExtension.isActivityEmbedded(activity);
    }

    /* compiled from: EmbeddingCompat.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @Nullable
        public final Integer getExtensionApiLevel() {
            try {
                return Integer.valueOf(WindowExtensionsProvider.getWindowExtensions().getVendorApiLevel());
            } catch (NoClassDefFoundError unused) {
                Log.d(EmbeddingCompat.TAG, "Embedding extension version not found");
                return null;
            } catch (UnsupportedOperationException unused2) {
                Log.d(EmbeddingCompat.TAG, "Stub Extension");
                return null;
            }
        }

        public final boolean isEmbeddingAvailable() {
            try {
                return WindowExtensionsProvider.getWindowExtensions().getActivityEmbeddingComponent() != null;
            } catch (NoClassDefFoundError unused) {
                Log.d(EmbeddingCompat.TAG, "Embedding extension version not found");
                return false;
            } catch (UnsupportedOperationException unused2) {
                Log.d(EmbeddingCompat.TAG, "Stub Extension");
                return false;
            }
        }

        @NotNull
        public final ActivityEmbeddingComponent embeddingComponent() {
            if (!isEmbeddingAvailable()) {
                return new EmptyEmbeddingComponent();
            }
            ActivityEmbeddingComponent activityEmbeddingComponent = WindowExtensionsProvider.getWindowExtensions().getActivityEmbeddingComponent();
            return activityEmbeddingComponent == null ? new EmptyEmbeddingComponent() : activityEmbeddingComponent;
        }
    }
}
