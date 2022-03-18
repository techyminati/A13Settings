package kotlin.coroutines;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: CoroutineContext.kt */
/* loaded from: classes2.dex */
public interface CoroutineContext {

    /* compiled from: CoroutineContext.kt */
    /* loaded from: classes2.dex */
    public interface Element extends CoroutineContext {
    }

    /* compiled from: CoroutineContext.kt */
    /* loaded from: classes2.dex */
    public interface Key<E extends Element> {
    }

    @Nullable
    <E extends Element> E get(@NotNull Key<E> key);
}
