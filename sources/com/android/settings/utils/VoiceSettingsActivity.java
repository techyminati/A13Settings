package com.android.settings.utils;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
/* loaded from: classes.dex */
public abstract class VoiceSettingsActivity extends Activity {
    protected abstract boolean onVoiceSettingInteraction(Intent intent);

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!isVoiceInteractionRoot()) {
            Log.v("VoiceSettingsActivity", "Cannot modify settings without voice interaction");
            finish();
        } else if (onVoiceSettingInteraction(getIntent())) {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifySuccess(CharSequence charSequence) {
        if (getVoiceInteractor() != null) {
            getVoiceInteractor().submitRequest(new VoiceInteractor.CompleteVoiceRequest(charSequence, null) { // from class: com.android.settings.utils.VoiceSettingsActivity.1
                @Override // android.app.VoiceInteractor.CompleteVoiceRequest
                public void onCompleteResult(Bundle bundle) {
                    VoiceSettingsActivity.this.finish();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyFailure(CharSequence charSequence) {
        if (getVoiceInteractor() != null) {
            getVoiceInteractor().submitRequest(new VoiceInteractor.AbortVoiceRequest(charSequence, (Bundle) null));
        }
    }
}
