package androidx.slice;

import androidx.versionedparcelable.VersionedParcelable;
/* loaded from: classes.dex */
public final class SliceSpec implements VersionedParcelable {
    int mRevision;
    String mType;

    public SliceSpec() {
        this.mRevision = 1;
    }

    public SliceSpec(String str, int i) {
        this.mType = str;
        this.mRevision = i;
    }

    public String getType() {
        return this.mType;
    }

    public int getRevision() {
        return this.mRevision;
    }

    public boolean canRender(SliceSpec sliceSpec) {
        return this.mType.equals(sliceSpec.mType) && this.mRevision >= sliceSpec.mRevision;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SliceSpec)) {
            return false;
        }
        SliceSpec sliceSpec = (SliceSpec) obj;
        return this.mType.equals(sliceSpec.mType) && this.mRevision == sliceSpec.mRevision;
    }

    public int hashCode() {
        return this.mType.hashCode() + this.mRevision;
    }

    public String toString() {
        return String.format("SliceSpec{%s,%d}", this.mType, Integer.valueOf(this.mRevision));
    }
}
