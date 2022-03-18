package com.android.settings.widget;

import android.media.MediaPlayer;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class MediaAnimationController$$ExternalSyntheticLambda0 implements MediaPlayer.OnPreparedListener {
    public static final /* synthetic */ MediaAnimationController$$ExternalSyntheticLambda0 INSTANCE = new MediaAnimationController$$ExternalSyntheticLambda0();

    private /* synthetic */ MediaAnimationController$$ExternalSyntheticLambda0() {
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    public final void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setLooping(true);
    }
}
