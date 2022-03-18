package com.google.common.collect;

import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
@DoNotMock("Use Maps.difference")
/* loaded from: classes2.dex */
public interface MapDifference<K, V> {

    @DoNotMock("Use Maps.difference")
    /* loaded from: classes2.dex */
    public interface ValueDifference<V> {
        V leftValue();

        V rightValue();
    }

    Map<K, ValueDifference<V>> entriesDiffering();

    Map<K, V> entriesInCommon();

    Map<K, V> entriesOnlyOnLeft();

    Map<K, V> entriesOnlyOnRight();
}
