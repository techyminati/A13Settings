package okio.internal;

import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.Options;
import okio.Platform;
import okio.Segment;
import org.jetbrains.annotations.NotNull;
/* compiled from: Buffer.kt */
/* loaded from: classes2.dex */
public final class BufferKt {
    @NotNull
    private static final byte[] HEX_DIGIT_BYTES = Platform.asUtf8ToByteArray("0123456789abcdef");

    public static final boolean rangeEquals(@NotNull Segment segment, int i, @NotNull byte[] bytes, int i2, int i3) {
        Intrinsics.checkNotNullParameter(segment, "segment");
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        int i4 = segment.limit;
        byte[] bArr = segment.data;
        while (i2 < i3) {
            if (i == i4) {
                segment = segment.next;
                Intrinsics.checkNotNull(segment);
                byte[] bArr2 = segment.data;
                bArr = bArr2;
                i = segment.pos;
                i4 = segment.limit;
            }
            if (bArr[i] != bytes[i2]) {
                return false;
            }
            i++;
            i2++;
        }
        return true;
    }

    public static /* synthetic */ int selectPrefix$default(Buffer buffer, Options options, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return selectPrefix(buffer, options, z);
    }

    public static final int selectPrefix(@NotNull Buffer buffer, @NotNull Options options, boolean z) {
        int i;
        int i2;
        Segment segment;
        int i3;
        Intrinsics.checkNotNullParameter(buffer, "<this>");
        Intrinsics.checkNotNullParameter(options, "options");
        Segment segment2 = buffer.head;
        if (segment2 == null) {
            return z ? -2 : -1;
        }
        byte[] bArr = segment2.data;
        int i4 = segment2.pos;
        int i5 = segment2.limit;
        int[] trie$external__okio__android_common__okio_lib = options.getTrie$external__okio__android_common__okio_lib();
        Segment segment3 = segment2;
        int i6 = -1;
        int i7 = 0;
        loop0: while (true) {
            int i8 = i7 + 1;
            int i9 = trie$external__okio__android_common__okio_lib[i7];
            int i10 = i8 + 1;
            int i11 = trie$external__okio__android_common__okio_lib[i8];
            if (i11 != -1) {
                i6 = i11;
            }
            if (segment3 == null) {
                break;
            }
            if (i9 < 0) {
                int i12 = i10 + (i9 * (-1));
                while (true) {
                    int i13 = i4 + 1;
                    int i14 = i10 + 1;
                    if ((bArr[i4] & 255) != trie$external__okio__android_common__okio_lib[i10]) {
                        return i6;
                    }
                    boolean z2 = i14 == i12;
                    if (i13 == i5) {
                        Intrinsics.checkNotNull(segment3);
                        Segment segment4 = segment3.next;
                        Intrinsics.checkNotNull(segment4);
                        i3 = segment4.pos;
                        byte[] bArr2 = segment4.data;
                        i2 = segment4.limit;
                        if (segment4 != segment2) {
                            segment = segment4;
                            bArr = bArr2;
                        } else if (!z2) {
                            break loop0;
                        } else {
                            bArr = bArr2;
                            segment = null;
                        }
                    } else {
                        i2 = i5;
                        i3 = i13;
                        segment = segment3;
                    }
                    if (z2) {
                        i = trie$external__okio__android_common__okio_lib[i14];
                        i4 = i3;
                        i5 = i2;
                        segment3 = segment;
                        break;
                    }
                    i4 = i3;
                    i5 = i2;
                    i10 = i14;
                    segment3 = segment;
                }
            } else {
                i4++;
                int i15 = bArr[i4] & 255;
                int i16 = i10 + i9;
                while (i10 != i16) {
                    if (i15 == trie$external__okio__android_common__okio_lib[i10]) {
                        i = trie$external__okio__android_common__okio_lib[i10 + i9];
                        if (i4 == i5) {
                            segment3 = segment3.next;
                            Intrinsics.checkNotNull(segment3);
                            i4 = segment3.pos;
                            bArr = segment3.data;
                            i5 = segment3.limit;
                            if (segment3 == segment2) {
                                segment3 = null;
                            }
                        }
                    } else {
                        i10++;
                    }
                }
                return i6;
            }
            if (i >= 0) {
                return i;
            }
            i7 = -i;
        }
        if (z) {
            return -2;
        }
        return i6;
    }
}
