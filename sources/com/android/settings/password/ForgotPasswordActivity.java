package com.android.settings.password;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.window.R;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class ForgotPasswordActivity extends Activity {
    public static final String TAG = "ForgotPasswordActivity";

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("android.intent.extra.USER_ID", -1);
        if (intExtra < 0) {
            Log.e(TAG, "No valid userId supplied, exiting");
            finish();
            return;
        }
        setContentView(R.layout.forgot_password_activity);
        ((TextView) findViewById(R.id.forgot_password_text)).setText(((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getString("Settings.FORGOT_PASSWORD_TEXT", new Callable() { // from class: com.android.settings.password.ForgotPasswordActivity$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                String lambda$onCreate$0;
                lambda$onCreate$0 = ForgotPasswordActivity.this.lambda$onCreate$0();
                return lambda$onCreate$0;
            }
        }));
        ((FooterBarMixin) ((GlifLayout) findViewById(R.id.setup_wizard_layout)).getMixin(FooterBarMixin.class)).setPrimaryButton(new FooterButton.Builder(this).setText(17039370).setListener(new View.OnClickListener() { // from class: com.android.settings.password.ForgotPasswordActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ForgotPasswordActivity.this.lambda$onCreate$1(view);
            }
        }).setButtonType(4).setTheme(R.style.SudGlifButton_Primary).build());
        UserManager.get(this).requestQuietModeEnabled(false, UserHandle.of(intExtra), 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$0() throws Exception {
        return getString(R.string.forgot_password_text);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        finish();
    }
}
