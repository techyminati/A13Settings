package androidx.window.core;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Lazy;
import kotlin.LazyKt__LazyJVMKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt__StringsJVMKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: Version.kt */
/* loaded from: classes.dex */
public final class Version implements Comparable<Version> {
    @NotNull
    private static final Version CURRENT;
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static final Version UNKNOWN = new Version(0, 0, 0, "");
    @NotNull
    private static final Version VERSION_0_1 = new Version(0, 1, 0, "");
    @NotNull
    private static final Version VERSION_1_0;
    @NotNull
    private static final String VERSION_PATTERN_STRING = "(\\d+)(?:\\.(\\d+))(?:\\.(\\d+))(?:-(.+))?";
    @NotNull
    private final Lazy bigInteger$delegate;
    @NotNull
    private final String description;
    private final int major;
    private final int minor;
    private final int patch;

    public /* synthetic */ Version(int i, int i2, int i3, String str, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, i2, i3, str);
    }

    @Nullable
    public static final Version parse(@Nullable String str) {
        return Companion.parse(str);
    }

    private Version(int i, int i2, int i3, String str) {
        Lazy lazy;
        this.major = i;
        this.minor = i2;
        this.patch = i3;
        this.description = str;
        lazy = LazyKt__LazyJVMKt.lazy(new Version$bigInteger$2(this));
        this.bigInteger$delegate = lazy;
    }

    public final int getMajor() {
        return this.major;
    }

    public final int getMinor() {
        return this.minor;
    }

    public final int getPatch() {
        return this.patch;
    }

    @NotNull
    public final String getDescription() {
        return this.description;
    }

    private final BigInteger getBigInteger() {
        Object value = this.bigInteger$delegate.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "<get-bigInteger>(...)");
        return (BigInteger) value;
    }

    @NotNull
    public String toString() {
        boolean isBlank;
        isBlank = StringsKt__StringsJVMKt.isBlank(this.description);
        String stringPlus = isBlank ^ true ? Intrinsics.stringPlus("-", this.description) : "";
        return this.major + '.' + this.minor + '.' + this.patch + stringPlus;
    }

    public int compareTo(@NotNull Version other) {
        Intrinsics.checkNotNullParameter(other, "other");
        return getBigInteger().compareTo(other.getBigInteger());
    }

    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Version)) {
            return false;
        }
        Version version = (Version) obj;
        return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
    }

    public int hashCode() {
        return ((((527 + this.major) * 31) + this.minor) * 31) + this.patch;
    }

    /* compiled from: Version.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final Version getUNKNOWN() {
            return Version.UNKNOWN;
        }

        @NotNull
        public final Version getVERSION_0_1() {
            return Version.VERSION_0_1;
        }

        @NotNull
        public final Version getVERSION_1_0() {
            return Version.VERSION_1_0;
        }

        @NotNull
        public final Version getCURRENT() {
            return Version.CURRENT;
        }

        @Nullable
        public final Version parse(@Nullable String str) {
            boolean isBlank;
            if (str == null) {
                return null;
            }
            isBlank = StringsKt__StringsJVMKt.isBlank(str);
            if (isBlank) {
                return null;
            }
            Matcher matcher = Pattern.compile(Version.VERSION_PATTERN_STRING).matcher(str);
            if (!matcher.matches()) {
                return null;
            }
            String group = matcher.group(1);
            Integer valueOf = group == null ? null : Integer.valueOf(Integer.parseInt(group));
            if (valueOf == null) {
                return null;
            }
            int intValue = valueOf.intValue();
            String group2 = matcher.group(2);
            Integer valueOf2 = group2 == null ? null : Integer.valueOf(Integer.parseInt(group2));
            if (valueOf2 == null) {
                return null;
            }
            int intValue2 = valueOf2.intValue();
            String group3 = matcher.group(3);
            Integer valueOf3 = group3 == null ? null : Integer.valueOf(Integer.parseInt(group3));
            if (valueOf3 == null) {
                return null;
            }
            int intValue3 = valueOf3.intValue();
            String description = matcher.group(4) != null ? matcher.group(4) : "";
            Intrinsics.checkNotNullExpressionValue(description, "description");
            return new Version(intValue, intValue2, intValue3, description, null);
        }
    }

    static {
        Version version = new Version(1, 0, 0, "");
        VERSION_1_0 = version;
        CURRENT = version;
    }
}
