package com.google.common.collect;

import com.google.common.collect.ImmutableCollection;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.DoNotMock;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
@DoNotMock("Use ImmutableMap.of or another implementation")
/* loaded from: classes2.dex */
public abstract class ImmutableMap<K, V> implements Map<K, V>, Serializable {
    static final Map.Entry<?, ?>[] EMPTY_ENTRY_ARRAY = new Map.Entry[0];
    @LazyInit
    private transient ImmutableSet<Map.Entry<K, V>> entrySet;
    @LazyInit
    private transient ImmutableSet<K> keySet;
    @LazyInit
    private transient ImmutableCollection<V> values;

    abstract ImmutableSet<Map.Entry<K, V>> createEntrySet();

    abstract ImmutableSet<K> createKeySet();

    abstract ImmutableCollection<V> createValues();

    @Override // java.util.Map
    public abstract V get(Object obj);

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHashCodeFast() {
        return false;
    }

    public static <K, V> ImmutableMap<K, V> of(K k, V v, K k2, V v2, K k3, V v3) {
        CollectPreconditions.checkEntryNotNull(k, v);
        CollectPreconditions.checkEntryNotNull(k2, v2);
        CollectPreconditions.checkEntryNotNull(k3, v3);
        return RegularImmutableMap.create(3, new Object[]{k, v, k2, v2, k3, v3});
    }

    public static <K, V> ImmutableMap<K, V> of(K k, V v, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        CollectPreconditions.checkEntryNotNull(k, v);
        CollectPreconditions.checkEntryNotNull(k2, v2);
        CollectPreconditions.checkEntryNotNull(k3, v3);
        CollectPreconditions.checkEntryNotNull(k4, v4);
        CollectPreconditions.checkEntryNotNull(k5, v5);
        return RegularImmutableMap.create(5, new Object[]{k, v, k2, v2, k3, v3, k4, v4, k5, v5});
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    @DoNotMock
    /* loaded from: classes2.dex */
    public static class Builder<K, V> {
        Object[] alternatingKeysAndValues;
        boolean entriesUsed;
        int size;
        Comparator<? super V> valueComparator;

        public Builder() {
            this(4);
        }

        Builder(int i) {
            this.alternatingKeysAndValues = new Object[i * 2];
            this.size = 0;
            this.entriesUsed = false;
        }

        private void ensureCapacity(int i) {
            int i2 = i * 2;
            Object[] objArr = this.alternatingKeysAndValues;
            if (i2 > objArr.length) {
                this.alternatingKeysAndValues = Arrays.copyOf(objArr, ImmutableCollection.Builder.expandedCapacity(objArr.length, i2));
                this.entriesUsed = false;
            }
        }

        @CanIgnoreReturnValue
        public Builder<K, V> put(K k, V v) {
            ensureCapacity(this.size + 1);
            CollectPreconditions.checkEntryNotNull(k, v);
            Object[] objArr = this.alternatingKeysAndValues;
            int i = this.size;
            objArr[i * 2] = k;
            objArr[(i * 2) + 1] = v;
            this.size = i + 1;
            return this;
        }

        public ImmutableMap<K, V> build() {
            return buildOrThrow();
        }

        public ImmutableMap<K, V> buildOrThrow() {
            sortEntries();
            this.entriesUsed = true;
            return RegularImmutableMap.create(this.size, this.alternatingKeysAndValues);
        }

        void sortEntries() {
            int i;
            if (this.valueComparator != null) {
                if (this.entriesUsed) {
                    this.alternatingKeysAndValues = Arrays.copyOf(this.alternatingKeysAndValues, this.size * 2);
                }
                Map.Entry[] entryArr = new Map.Entry[this.size];
                int i2 = 0;
                while (true) {
                    i = this.size;
                    if (i2 >= i) {
                        break;
                    }
                    int i3 = i2 * 2;
                    Object obj = this.alternatingKeysAndValues[i3];
                    Objects.requireNonNull(obj);
                    Object obj2 = this.alternatingKeysAndValues[i3 + 1];
                    Objects.requireNonNull(obj2);
                    entryArr[i2] = new AbstractMap.SimpleImmutableEntry(obj, obj2);
                    i2++;
                }
                Arrays.sort(entryArr, 0, i, Ordering.from(this.valueComparator).onResultOf(Maps.valueFunction()));
                for (int i4 = 0; i4 < this.size; i4++) {
                    int i5 = i4 * 2;
                    this.alternatingKeysAndValues[i5] = entryArr[i4].getKey();
                    this.alternatingKeysAndValues[i5 + 1] = entryArr[i4].getValue();
                }
            }
        }
    }

    @Override // java.util.Map
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    @Deprecated
    public final V put(K k, V v) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    @CanIgnoreReturnValue
    @Deprecated
    public final V remove(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    @DoNotCall("Always throws UnsupportedOperationException")
    @Deprecated
    public final void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    @DoNotCall("Always throws UnsupportedOperationException")
    @Deprecated
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object obj) {
        return get(obj) != null;
    }

    @Override // java.util.Map
    public boolean containsValue(Object obj) {
        return values().contains(obj);
    }

    @Override // java.util.Map
    public final V getOrDefault(Object obj, V v) {
        V v2 = get(obj);
        return v2 != null ? v2 : v;
    }

    @Override // java.util.Map
    public ImmutableSet<Map.Entry<K, V>> entrySet() {
        ImmutableSet<Map.Entry<K, V>> immutableSet = this.entrySet;
        if (immutableSet != null) {
            return immutableSet;
        }
        ImmutableSet<Map.Entry<K, V>> createEntrySet = createEntrySet();
        this.entrySet = createEntrySet;
        return createEntrySet;
    }

    @Override // java.util.Map
    public ImmutableSet<K> keySet() {
        ImmutableSet<K> immutableSet = this.keySet;
        if (immutableSet != null) {
            return immutableSet;
        }
        ImmutableSet<K> createKeySet = createKeySet();
        this.keySet = createKeySet;
        return createKeySet;
    }

    @Override // java.util.Map
    public ImmutableCollection<V> values() {
        ImmutableCollection<V> immutableCollection = this.values;
        if (immutableCollection != null) {
            return immutableCollection;
        }
        ImmutableCollection<V> createValues = createValues();
        this.values = createValues;
        return createValues;
    }

    @Override // java.util.Map
    public boolean equals(Object obj) {
        return Maps.equalsImpl(this, obj);
    }

    @Override // java.util.Map
    public int hashCode() {
        return Sets.hashCodeImpl(entrySet());
    }

    public String toString() {
        return Maps.toStringImpl(this);
    }

    /* loaded from: classes2.dex */
    static class SerializedForm<K, V> implements Serializable {
        private static final long serialVersionUID = 0;
        private final Object keys;
        private final Object values;

        /* JADX INFO: Access modifiers changed from: package-private */
        public SerializedForm(ImmutableMap<K, V> immutableMap) {
            Object[] objArr = new Object[immutableMap.size()];
            Object[] objArr2 = new Object[immutableMap.size()];
            UnmodifiableIterator<Map.Entry<K, V>> it = immutableMap.entrySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                Map.Entry<K, V> next = it.next();
                objArr[i] = next.getKey();
                objArr2[i] = next.getValue();
                i++;
            }
            this.keys = objArr;
            this.values = objArr2;
        }

        final Object readResolve() {
            Object obj = this.keys;
            if (!(obj instanceof ImmutableSet)) {
                return legacyReadResolve();
            }
            ImmutableSet immutableSet = (ImmutableSet) obj;
            Builder<K, V> makeBuilder = makeBuilder(immutableSet.size());
            UnmodifiableIterator it = immutableSet.iterator();
            UnmodifiableIterator it2 = ((ImmutableCollection) this.values).iterator();
            while (it.hasNext()) {
                makeBuilder.put((K) it.next(), (V) it2.next());
            }
            return makeBuilder.build();
        }

        final Object legacyReadResolve() {
            Object[] objArr = (Object[]) this.keys;
            Object[] objArr2 = (Object[]) this.values;
            Builder<K, V> makeBuilder = makeBuilder(objArr.length);
            for (int i = 0; i < objArr.length; i++) {
                makeBuilder.put((K) objArr[i], (V) objArr2[i]);
            }
            return makeBuilder.build();
        }

        Builder<K, V> makeBuilder(int i) {
            return new Builder<>(i);
        }
    }

    Object writeReplace() {
        return new SerializedForm(this);
    }
}
