package okio;

import kotlin.collections.ArraysKt___ArraysJvmKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: Segment.kt */
/* loaded from: classes2.dex */
public final class Segment {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    public final byte[] data;
    public int limit;
    @Nullable
    public Segment next;
    public boolean owner;
    public int pos;
    @Nullable
    public Segment prev;
    public boolean shared;

    public Segment() {
        this.data = new byte[8192];
        this.owner = true;
        this.shared = false;
    }

    public Segment(@NotNull byte[] data, int i, int i2, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(data, "data");
        this.data = data;
        this.pos = i;
        this.limit = i2;
        this.shared = z;
        this.owner = z2;
    }

    @NotNull
    public final Segment sharedCopy() {
        this.shared = true;
        return new Segment(this.data, this.pos, this.limit, true, false);
    }

    @Nullable
    public final Segment pop() {
        Segment segment = this.next;
        if (segment == this) {
            segment = null;
        }
        Segment segment2 = this.prev;
        Intrinsics.checkNotNull(segment2);
        segment2.next = this.next;
        Segment segment3 = this.next;
        Intrinsics.checkNotNull(segment3);
        segment3.prev = this.prev;
        this.next = null;
        this.prev = null;
        return segment;
    }

    @NotNull
    public final Segment push(@NotNull Segment segment) {
        Intrinsics.checkNotNullParameter(segment, "segment");
        segment.prev = this;
        segment.next = this.next;
        Segment segment2 = this.next;
        Intrinsics.checkNotNull(segment2);
        segment2.prev = segment;
        this.next = segment;
        return segment;
    }

    @NotNull
    public final Segment split(int i) {
        Segment segment;
        if (i > 0 && i <= this.limit - this.pos) {
            if (i >= 1024) {
                segment = sharedCopy();
            } else {
                segment = SegmentPool.take();
                byte[] bArr = this.data;
                byte[] bArr2 = segment.data;
                int i2 = this.pos;
                ArraysKt___ArraysJvmKt.copyInto$default(bArr, bArr2, 0, i2, i2 + i, 2, null);
            }
            segment.limit = segment.pos + i;
            this.pos += i;
            Segment segment2 = this.prev;
            Intrinsics.checkNotNull(segment2);
            segment2.push(segment);
            return segment;
        }
        throw new IllegalArgumentException("byteCount out of range".toString());
    }

    public final void compact() {
        Segment segment = this.prev;
        int i = 0;
        if (segment != this) {
            Intrinsics.checkNotNull(segment);
            if (segment.owner) {
                int i2 = this.limit - this.pos;
                Segment segment2 = this.prev;
                Intrinsics.checkNotNull(segment2);
                int i3 = 8192 - segment2.limit;
                Segment segment3 = this.prev;
                Intrinsics.checkNotNull(segment3);
                if (!segment3.shared) {
                    Segment segment4 = this.prev;
                    Intrinsics.checkNotNull(segment4);
                    i = segment4.pos;
                }
                if (i2 <= i3 + i) {
                    Segment segment5 = this.prev;
                    Intrinsics.checkNotNull(segment5);
                    writeTo(segment5, i2);
                    pop();
                    SegmentPool.recycle(this);
                    return;
                }
                return;
            }
            return;
        }
        throw new IllegalStateException("cannot compact".toString());
    }

    public final void writeTo(@NotNull Segment sink, int i) {
        Intrinsics.checkNotNullParameter(sink, "sink");
        if (sink.owner) {
            int i2 = sink.limit;
            if (i2 + i > 8192) {
                if (!sink.shared) {
                    int i3 = sink.pos;
                    if ((i2 + i) - i3 <= 8192) {
                        byte[] bArr = sink.data;
                        ArraysKt___ArraysJvmKt.copyInto$default(bArr, bArr, 0, i3, i2, 2, null);
                        sink.limit -= sink.pos;
                        sink.pos = 0;
                    } else {
                        throw new IllegalArgumentException();
                    }
                } else {
                    throw new IllegalArgumentException();
                }
            }
            byte[] bArr2 = this.data;
            byte[] bArr3 = sink.data;
            int i4 = sink.limit;
            int i5 = this.pos;
            ArraysKt___ArraysJvmKt.copyInto(bArr2, bArr3, i4, i5, i5 + i);
            sink.limit += i;
            this.pos += i;
            return;
        }
        throw new IllegalStateException("only owner can write".toString());
    }

    /* compiled from: Segment.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
