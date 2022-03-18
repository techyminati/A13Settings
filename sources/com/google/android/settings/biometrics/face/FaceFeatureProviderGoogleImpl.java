package com.google.android.settings.biometrics.face;

import android.content.Context;
import androidx.window.R;
import com.android.settings.biometrics.face.FaceFeatureProvider;
/* loaded from: classes2.dex */
public class FaceFeatureProviderGoogleImpl implements FaceFeatureProvider {
    @Override // com.android.settings.biometrics.face.FaceFeatureProvider
    public boolean isAttentionSupported(Context context) {
        return context.getResources().getBoolean(R.bool.config_face_settings_attention_supported);
    }
}
