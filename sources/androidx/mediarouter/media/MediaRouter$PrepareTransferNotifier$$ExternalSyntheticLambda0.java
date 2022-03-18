package androidx.mediarouter.media;

import androidx.mediarouter.media.MediaRouter;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class MediaRouter$PrepareTransferNotifier$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MediaRouter.PrepareTransferNotifier f$0;

    public /* synthetic */ MediaRouter$PrepareTransferNotifier$$ExternalSyntheticLambda0(MediaRouter.PrepareTransferNotifier prepareTransferNotifier) {
        this.f$0 = prepareTransferNotifier;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.finishTransfer();
    }
}
