package com.google.common.collect;

import com.google.errorprone.annotations.DoNotCall;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class UnmodifiableIterator<E> implements Iterator<E> {
    @Override // java.util.Iterator
    @DoNotCall("Always throws UnsupportedOperationException")
    @Deprecated
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
