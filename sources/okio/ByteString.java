package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import kotlin.collections.ArraysKt___ArraysJvmKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt__StringsJVMKt;
import okio.internal.ByteStringKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ByteString.kt */
/* loaded from: classes2.dex */
public class ByteString implements Serializable, Comparable<ByteString> {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    public static final ByteString EMPTY = new ByteString(new byte[0]);
    private static final long serialVersionUID = 1;
    @NotNull
    private final byte[] data;
    private transient int hashCode;
    @Nullable
    private transient String utf8;

    @NotNull
    public static final ByteString encodeUtf8(@NotNull String str) {
        return Companion.encodeUtf8(str);
    }

    @NotNull
    public String utf8() {
        String utf8$external__okio__android_common__okio_lib = getUtf8$external__okio__android_common__okio_lib();
        if (utf8$external__okio__android_common__okio_lib != null) {
            return utf8$external__okio__android_common__okio_lib;
        }
        String utf8String = Platform.toUtf8String(internalArray$external__okio__android_common__okio_lib());
        setUtf8$external__okio__android_common__okio_lib(utf8String);
        return utf8String;
    }

    public ByteString(@NotNull byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        this.data = data;
    }

    @NotNull
    public String hex() {
        char[] cArr = new char[getData$external__okio__android_common__okio_lib().length * 2];
        byte[] data$external__okio__android_common__okio_lib = getData$external__okio__android_common__okio_lib();
        int length = data$external__okio__android_common__okio_lib.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            byte b = data$external__okio__android_common__okio_lib[i];
            i++;
            int i3 = i2 + 1;
            cArr[i2] = ByteStringKt.getHEX_DIGIT_CHARS()[(b >> 4) & 15];
            i2 = i3 + 1;
            cArr[i3] = ByteStringKt.getHEX_DIGIT_CHARS()[b & 15];
        }
        return new String(cArr);
    }

    @NotNull
    public final byte[] getData$external__okio__android_common__okio_lib() {
        return this.data;
    }

    public final int getHashCode$external__okio__android_common__okio_lib() {
        return this.hashCode;
    }

    public final void setHashCode$external__okio__android_common__okio_lib(int i) {
        this.hashCode = i;
    }

    @Nullable
    public final String getUtf8$external__okio__android_common__okio_lib() {
        return this.utf8;
    }

    public final void setUtf8$external__okio__android_common__okio_lib(@Nullable String str) {
        this.utf8 = str;
    }

    public final byte getByte(int i) {
        return internalGet$external__okio__android_common__okio_lib(i);
    }

    public final int size() {
        return getSize$external__okio__android_common__okio_lib();
    }

    public byte internalGet$external__okio__android_common__okio_lib(int i) {
        return getData$external__okio__android_common__okio_lib()[i];
    }

    public int getSize$external__okio__android_common__okio_lib() {
        return getData$external__okio__android_common__okio_lib().length;
    }

    @NotNull
    public byte[] internalArray$external__okio__android_common__okio_lib() {
        return getData$external__okio__android_common__okio_lib();
    }

    public boolean rangeEquals(int i, @NotNull ByteString other, int i2, int i3) {
        Intrinsics.checkNotNullParameter(other, "other");
        return other.rangeEquals(i2, getData$external__okio__android_common__okio_lib(), i, i3);
    }

    public boolean rangeEquals(int i, @NotNull byte[] other, int i2, int i3) {
        Intrinsics.checkNotNullParameter(other, "other");
        return i >= 0 && i <= getData$external__okio__android_common__okio_lib().length - i3 && i2 >= 0 && i2 <= other.length - i3 && Util.arrayRangeEquals(getData$external__okio__android_common__okio_lib(), i, other, i2, i3);
    }

    public final boolean startsWith(@NotNull ByteString prefix) {
        Intrinsics.checkNotNullParameter(prefix, "prefix");
        return rangeEquals(0, prefix, 0, prefix.size());
    }

    private final void readObject(ObjectInputStream objectInputStream) throws IOException {
        ByteString read = Companion.read(objectInputStream, objectInputStream.readInt());
        Field declaredField = ByteString.class.getDeclaredField("data");
        declaredField.setAccessible(true);
        declaredField.set(this, read.data);
    }

    private final void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.data.length);
        objectOutputStream.write(this.data);
    }

    /* compiled from: ByteString.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final ByteString encodeUtf8(@NotNull String str) {
            Intrinsics.checkNotNullParameter(str, "<this>");
            ByteString byteString = new ByteString(Platform.asUtf8ToByteArray(str));
            byteString.setUtf8$external__okio__android_common__okio_lib(str);
            return byteString;
        }

        @NotNull
        public final ByteString read(@NotNull InputStream inputStream, int i) throws IOException {
            Intrinsics.checkNotNullParameter(inputStream, "<this>");
            int i2 = 0;
            if (i >= 0) {
                byte[] bArr = new byte[i];
                while (i2 < i) {
                    int read = inputStream.read(bArr, i2, i - i2);
                    if (read != -1) {
                        i2 += read;
                    } else {
                        throw new EOFException();
                    }
                }
                return new ByteString(bArr);
            }
            throw new IllegalArgumentException(Intrinsics.stringPlus("byteCount < 0: ", Integer.valueOf(i)).toString());
        }
    }

    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ByteString) {
            ByteString byteString = (ByteString) obj;
            if (byteString.size() == getData$external__okio__android_common__okio_lib().length && byteString.rangeEquals(0, getData$external__okio__android_common__okio_lib(), 0, getData$external__okio__android_common__okio_lib().length)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int hashCode$external__okio__android_common__okio_lib = getHashCode$external__okio__android_common__okio_lib();
        if (hashCode$external__okio__android_common__okio_lib != 0) {
            return hashCode$external__okio__android_common__okio_lib;
        }
        int hashCode = Arrays.hashCode(getData$external__okio__android_common__okio_lib());
        setHashCode$external__okio__android_common__okio_lib(hashCode);
        return hashCode;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x002e, code lost:
        if (r0 < r1) goto L_0x0030;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0033, code lost:
        return 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:?, code lost:
        return -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0028, code lost:
        if (r7 < r8) goto L_0x0030;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int compareTo(@org.jetbrains.annotations.NotNull okio.ByteString r10) {
        /*
            r9 = this;
            java.lang.String r0 = "other"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r10, r0)
            int r0 = r9.size()
            int r1 = r10.size()
            int r2 = java.lang.Math.min(r0, r1)
            r3 = 0
            r4 = r3
        L_0x0013:
            r5 = -1
            r6 = 1
            if (r4 >= r2) goto L_0x002b
            byte r7 = r9.getByte(r4)
            r7 = r7 & 255(0xff, float:3.57E-43)
            byte r8 = r10.getByte(r4)
            r8 = r8 & 255(0xff, float:3.57E-43)
            if (r7 != r8) goto L_0x0028
            int r4 = r4 + 1
            goto L_0x0013
        L_0x0028:
            if (r7 >= r8) goto L_0x0032
            goto L_0x0030
        L_0x002b:
            if (r0 != r1) goto L_0x002e
            goto L_0x0033
        L_0x002e:
            if (r0 >= r1) goto L_0x0032
        L_0x0030:
            r3 = r5
            goto L_0x0033
        L_0x0032:
            r3 = r6
        L_0x0033:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.ByteString.compareTo(okio.ByteString):int");
    }

    @NotNull
    public String toString() {
        int codePointIndexToCharIndex;
        String replace$default;
        String replace$default2;
        String replace$default3;
        ByteString byteString;
        byte[] copyOfRange;
        boolean z = true;
        if (getData$external__okio__android_common__okio_lib().length == 0) {
            return "[size=0]";
        }
        codePointIndexToCharIndex = ByteStringKt.codePointIndexToCharIndex(getData$external__okio__android_common__okio_lib(), 64);
        if (codePointIndexToCharIndex != -1) {
            String utf8 = utf8();
            String substring = utf8.substring(0, codePointIndexToCharIndex);
            Intrinsics.checkNotNullExpressionValue(substring, "this as java.lang.String…ing(startIndex, endIndex)");
            replace$default = StringsKt__StringsJVMKt.replace$default(substring, "\\", "\\\\", false, 4, null);
            replace$default2 = StringsKt__StringsJVMKt.replace$default(replace$default, "\n", "\\n", false, 4, null);
            replace$default3 = StringsKt__StringsJVMKt.replace$default(replace$default2, "\r", "\\r", false, 4, null);
            if (codePointIndexToCharIndex < utf8.length()) {
                return "[size=" + getData$external__okio__android_common__okio_lib().length + " text=" + replace$default3 + "…]";
            }
            return "[text=" + replace$default3 + ']';
        } else if (getData$external__okio__android_common__okio_lib().length <= 64) {
            return "[hex=" + hex() + ']';
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[size=");
            sb.append(getData$external__okio__android_common__okio_lib().length);
            sb.append(" hex=");
            if (64 > getData$external__okio__android_common__okio_lib().length) {
                z = false;
            }
            if (z) {
                if (64 == getData$external__okio__android_common__okio_lib().length) {
                    byteString = this;
                } else {
                    copyOfRange = ArraysKt___ArraysJvmKt.copyOfRange(getData$external__okio__android_common__okio_lib(), 0, 64);
                    byteString = new ByteString(copyOfRange);
                }
                sb.append(byteString.hex());
                sb.append("…]");
                return sb.toString();
            }
            throw new IllegalArgumentException(("endIndex > length(" + getData$external__okio__android_common__okio_lib().length + ')').toString());
        }
    }
}
