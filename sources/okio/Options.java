package okio;

import java.util.List;
import java.util.RandomAccess;
import kotlin.collections.AbstractList;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
/* compiled from: Options.kt */
/* loaded from: classes2.dex */
public final class Options extends AbstractList<ByteString> implements RandomAccess {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ByteString[] byteStrings;
    @NotNull
    private final int[] trie;

    public /* synthetic */ Options(ByteString[] byteStringArr, int[] iArr, DefaultConstructorMarker defaultConstructorMarker) {
        this(byteStringArr, iArr);
    }

    @NotNull
    public static final Options of(@NotNull ByteString... byteStringArr) {
        return Companion.of(byteStringArr);
    }

    @Override // kotlin.collections.AbstractCollection, java.util.Collection
    public final /* bridge */ boolean contains(Object obj) {
        if (!(obj instanceof ByteString)) {
            return false;
        }
        return contains((ByteString) obj);
    }

    public /* bridge */ boolean contains(ByteString byteString) {
        return super.contains((Options) byteString);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int indexOf(Object obj) {
        if (!(obj instanceof ByteString)) {
            return -1;
        }
        return indexOf((ByteString) obj);
    }

    public /* bridge */ int indexOf(ByteString byteString) {
        return super.indexOf((Options) byteString);
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public final /* bridge */ int lastIndexOf(Object obj) {
        if (!(obj instanceof ByteString)) {
            return -1;
        }
        return lastIndexOf((ByteString) obj);
    }

    public /* bridge */ int lastIndexOf(ByteString byteString) {
        return super.lastIndexOf((Options) byteString);
    }

    @NotNull
    public final ByteString[] getByteStrings$external__okio__android_common__okio_lib() {
        return this.byteStrings;
    }

    @NotNull
    public final int[] getTrie$external__okio__android_common__okio_lib() {
        return this.trie;
    }

    private Options(ByteString[] byteStringArr, int[] iArr) {
        this.byteStrings = byteStringArr;
        this.trie = iArr;
    }

    @Override // kotlin.collections.AbstractCollection
    public int getSize() {
        return this.byteStrings.length;
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    @NotNull
    public ByteString get(int i) {
        return this.byteStrings[i];
    }

    /* compiled from: Options.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:54:0x00e5, code lost:
            continue;
         */
        @org.jetbrains.annotations.NotNull
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final okio.Options of(@org.jetbrains.annotations.NotNull okio.ByteString... r17) {
            /*
                Method dump skipped, instructions count: 314
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: okio.Options.Companion.of(okio.ByteString[]):okio.Options");
        }

        static /* synthetic */ void buildTrieRecursive$default(Companion companion, long j, Buffer buffer, int i, List list, int i2, int i3, List list2, int i4, Object obj) {
            companion.buildTrieRecursive((i4 & 1) != 0 ? 0L : j, buffer, (i4 & 4) != 0 ? 0 : i, list, (i4 & 16) != 0 ? 0 : i2, (i4 & 32) != 0 ? list.size() : i3, list2);
        }

        private final void buildTrieRecursive(long j, Buffer buffer, int i, List<? extends ByteString> list, int i2, int i3, List<Integer> list2) {
            int i4;
            int i5;
            int i6;
            int i7 = i;
            int i8 = 1;
            if (i2 < i3) {
                int i9 = i2;
                while (i9 < i3) {
                    i9++;
                    if (!(((ByteString) list.get(i9)).size() >= i7)) {
                        throw new IllegalArgumentException("Failed requirement.".toString());
                    }
                }
                ByteString byteString = (ByteString) list.get(i2);
                ByteString byteString2 = (ByteString) list.get(i3 - 1);
                int i10 = -1;
                if (i7 == byteString.size()) {
                    i5 = list2.get(i2).intValue();
                    int i11 = i2 + 1;
                    byteString = (ByteString) list.get(i11);
                    i4 = i11;
                } else {
                    i4 = i2;
                    i5 = -1;
                }
                if (byteString.getByte(i7) != byteString2.getByte(i7)) {
                    int i12 = i4 + 1;
                    while (i12 < i3) {
                        i12++;
                        if (((ByteString) list.get(i12 - 1)).getByte(i7) != ((ByteString) list.get(i12)).getByte(i7)) {
                            i8++;
                        }
                    }
                    long intCount = j + getIntCount(buffer) + 2 + (i8 * 2);
                    buffer.writeInt(i8);
                    buffer.writeInt(i5);
                    int i13 = i4;
                    while (i13 < i3) {
                        i13++;
                        byte b = ((ByteString) list.get(i13)).getByte(i7);
                        if (i13 == i4 || b != ((ByteString) list.get(i13 - 1)).getByte(i7)) {
                            buffer.writeInt(b & 255);
                        }
                    }
                    Buffer buffer2 = new Buffer();
                    while (i4 < i3) {
                        byte b2 = ((ByteString) list.get(i4)).getByte(i7);
                        int i14 = i4 + 1;
                        int i15 = i14;
                        while (true) {
                            if (i15 >= i3) {
                                i6 = i3;
                                break;
                            }
                            i15++;
                            if (b2 != ((ByteString) list.get(i15)).getByte(i7)) {
                                i6 = i15;
                                break;
                            }
                        }
                        if (i14 == i6 && i7 + 1 == ((ByteString) list.get(i4)).size()) {
                            buffer.writeInt(list2.get(i4).intValue());
                            i4 = i6;
                            buffer2 = buffer2;
                        } else {
                            buffer.writeInt(((int) (intCount + getIntCount(buffer2))) * i10);
                            i4 = i6;
                            buffer2 = buffer2;
                            buildTrieRecursive(intCount, buffer2, i7 + 1, list, i4, i6, list2);
                        }
                        i10 = -1;
                    }
                    buffer.writeAll(buffer2);
                    return;
                }
                int min = Math.min(byteString.size(), byteString2.size());
                int i16 = i7;
                int i17 = 0;
                while (i16 < min) {
                    i16++;
                    if (byteString.getByte(i16) != byteString2.getByte(i16)) {
                        break;
                    }
                    i17++;
                }
                long intCount2 = j + getIntCount(buffer) + 2 + i17 + 1;
                buffer.writeInt(-i17);
                buffer.writeInt(i5);
                int i18 = i7 + i17;
                while (i7 < i18) {
                    i7++;
                    buffer.writeInt(byteString.getByte(i7) & 255);
                }
                if (i4 + 1 == i3) {
                    if (i18 == ((ByteString) list.get(i4)).size()) {
                        buffer.writeInt(list2.get(i4).intValue());
                        return;
                    }
                    throw new IllegalStateException("Check failed.".toString());
                }
                Buffer buffer3 = new Buffer();
                buffer.writeInt(((int) (getIntCount(buffer3) + intCount2)) * (-1));
                buildTrieRecursive(intCount2, buffer3, i18, list, i4, i3, list2);
                buffer.writeAll(buffer3);
                return;
            }
            throw new IllegalArgumentException("Failed requirement.".toString());
        }

        private final long getIntCount(Buffer buffer) {
            return buffer.size() / 4;
        }
    }
}
