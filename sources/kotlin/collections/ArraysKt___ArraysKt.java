package kotlin.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt___RangesKt;
import org.jetbrains.annotations.NotNull;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: _Arrays.kt */
/* loaded from: classes2.dex */
public class ArraysKt___ArraysKt extends ArraysKt___ArraysJvmKt {
    public static final <T> boolean contains(@NotNull T[] tArr, T t) {
        Intrinsics.checkNotNullParameter(tArr, "<this>");
        return indexOf(tArr, t) >= 0;
    }

    public static final <T> int indexOf(@NotNull T[] tArr, T t) {
        Intrinsics.checkNotNullParameter(tArr, "<this>");
        int i = 0;
        if (t == null) {
            int length = tArr.length;
            while (i < length) {
                i++;
                if (tArr[i] == null) {
                    return i;
                }
            }
            return -1;
        }
        int length2 = tArr.length;
        while (i < length2) {
            i++;
            if (Intrinsics.areEqual(t, tArr[i])) {
                return i;
            }
        }
        return -1;
    }

    public static char single(@NotNull char[] cArr) {
        Intrinsics.checkNotNullParameter(cArr, "<this>");
        int length = cArr.length;
        if (length == 0) {
            throw new NoSuchElementException("Array is empty.");
        } else if (length == 1) {
            return cArr[0];
        } else {
            throw new IllegalArgumentException("Array has more than one element.");
        }
    }

    @NotNull
    public static <T> List<T> drop(@NotNull T[] tArr, int i) {
        int coerceAtLeast;
        Intrinsics.checkNotNullParameter(tArr, "<this>");
        if (i >= 0) {
            coerceAtLeast = RangesKt___RangesKt.coerceAtLeast(tArr.length - i, 0);
            return takeLast(tArr, coerceAtLeast);
        }
        throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
    }

    @NotNull
    public static final <T> List<T> takeLast(@NotNull T[] tArr, int i) {
        List<T> emptyList;
        Intrinsics.checkNotNullParameter(tArr, "<this>");
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("Requested element count " + i + " is less than zero.").toString());
        } else if (i == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        } else {
            int length = tArr.length;
            if (i >= length) {
                return toList(tArr);
            }
            if (i == 1) {
                return CollectionsKt__CollectionsJVMKt.listOf(tArr[length - 1]);
            }
            ArrayList arrayList = new ArrayList(i);
            int i2 = length - i;
            while (i2 < length) {
                i2++;
                arrayList.add(tArr[i2]);
            }
            return arrayList;
        }
    }

    @NotNull
    public static final <T> List<T> toList(@NotNull T[] tArr) {
        List<T> emptyList;
        List<T> mutableList;
        Intrinsics.checkNotNullParameter(tArr, "<this>");
        int length = tArr.length;
        if (length == 0) {
            emptyList = CollectionsKt__CollectionsKt.emptyList();
            return emptyList;
        } else if (length == 1) {
            return CollectionsKt__CollectionsJVMKt.listOf(tArr[0]);
        } else {
            mutableList = toMutableList(tArr);
            return mutableList;
        }
    }

    @NotNull
    public static <T> List<T> toMutableList(@NotNull T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "<this>");
        return new ArrayList(CollectionsKt__CollectionsKt.asCollection(tArr));
    }
}
