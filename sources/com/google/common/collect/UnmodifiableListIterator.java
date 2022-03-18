package com.google.common.collect;

import com.google.errorprone.annotations.DoNotCall;
import java.util.ListIterator;
/* loaded from: classes2.dex */
public abstract class UnmodifiableListIterator<E> extends UnmodifiableIterator<E> implements ListIterator<E> {
    @Override // java.util.ListIterator
    @DoNotCall("Always throws UnsupportedOperationException")
    @Deprecated
    public final void add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ListIterator
    @DoNotCall("Always throws UnsupportedOperationException")
    @Deprecated
    public final void set(E e) {
        throw new UnsupportedOperationException();
    }
}
