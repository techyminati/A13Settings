package kotlin.reflect;
/* compiled from: KFunction.kt */
/* loaded from: classes2.dex */
public interface KFunction<R> extends KCallable<R> {
    boolean isExternal();

    boolean isInfix();

    boolean isInline();

    boolean isOperator();

    @Override // kotlin.reflect.KCallable
    boolean isSuspend();
}
