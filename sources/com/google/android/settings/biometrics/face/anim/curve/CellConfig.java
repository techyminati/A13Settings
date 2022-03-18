package com.google.android.settings.biometrics.face.anim.curve;

import android.graphics.Path;
/* loaded from: classes2.dex */
public class CellConfig {
    final boolean mFlipVertical;
    final Path mPath;
    final int mRotation;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CellConfig(Path path, int i) {
        this(path, i, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CellConfig(Path path, int i, boolean z) {
        this.mPath = path;
        this.mRotation = i;
        this.mFlipVertical = z;
    }
}
