package androidx.slice.builders.impl;

import androidx.slice.Clock;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.SystemClock;
import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TemplateBuilderImpl {
    private Clock mClock;
    private Slice.Builder mSliceBuilder;
    private final SliceSpec mSpec;

    public abstract void apply(Slice.Builder builder);

    /* JADX INFO: Access modifiers changed from: protected */
    public TemplateBuilderImpl(Slice.Builder builder, SliceSpec sliceSpec) {
        this(builder, sliceSpec, new SystemClock());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TemplateBuilderImpl(Slice.Builder builder, SliceSpec sliceSpec, Clock clock) {
        this.mSliceBuilder = builder;
        this.mSpec = sliceSpec;
        this.mClock = clock;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setBuilder(Slice.Builder builder) {
        this.mSliceBuilder = builder;
    }

    public Slice build() {
        this.mSliceBuilder.setSpec(this.mSpec);
        apply(this.mSliceBuilder);
        return this.mSliceBuilder.build();
    }

    public Slice.Builder getBuilder() {
        return this.mSliceBuilder;
    }

    public Slice.Builder createChildBuilder() {
        return new Slice.Builder(this.mSliceBuilder);
    }

    public Clock getClock() {
        return this.mClock;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ArrayList<String> parseImageMode(int i, boolean z) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (i == 6) {
            arrayList.add("show_label");
        }
        if (i != 0) {
            arrayList.add("no_tint");
        }
        if (i == 2 || i == 4) {
            arrayList.add("large");
        }
        if (i == 3 || i == 4) {
            arrayList.add("raw");
        }
        if (z) {
            arrayList.add("partial");
        }
        return arrayList;
    }
}
