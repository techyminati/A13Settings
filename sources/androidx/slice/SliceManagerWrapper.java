package androidx.slice;

import android.app.slice.SliceManager;
import android.content.Context;
import android.net.Uri;
import java.util.List;
import java.util.Set;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SliceManagerWrapper extends SliceManager {
    private final SliceManager mManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SliceManagerWrapper(Context context) {
        this((SliceManager) context.getSystemService(SliceManager.class));
    }

    SliceManagerWrapper(SliceManager sliceManager) {
        this.mManager = sliceManager;
    }

    @Override // androidx.slice.SliceManager
    public Set<SliceSpec> getPinnedSpecs(Uri uri) {
        return SliceConvert.wrap(this.mManager.getPinnedSpecs(uri));
    }

    @Override // androidx.slice.SliceManager
    public List<Uri> getPinnedSlices() {
        return this.mManager.getPinnedSlices();
    }
}
