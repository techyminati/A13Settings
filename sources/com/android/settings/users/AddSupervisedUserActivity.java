package com.android.settings.users;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.NewUserRequest;
import android.os.NewUserResponse;
import android.os.UserManager;
import android.view.View;
import androidx.window.R;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class AddSupervisedUserActivity extends Activity {
    private ActivityManager mActivityManager;
    private UserManager mUserManager;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mUserManager = (UserManager) getSystemService(UserManager.class);
        this.mActivityManager = (ActivityManager) getSystemService(ActivityManager.class);
        setContentView(R.layout.add_supervised_user);
        findViewById(R.id.createSupervisedUser).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.users.AddSupervisedUserActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AddSupervisedUserActivity.this.lambda$onCreate$0(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View view) {
        createUser();
    }

    private void createUserAsync(final NewUserRequest newUserRequest, final Consumer<NewUserResponse> consumer) {
        Objects.requireNonNull(consumer);
        final Handler handler = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(new Runnable() { // from class: com.android.settings.users.AddSupervisedUserActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AddSupervisedUserActivity.this.lambda$createUserAsync$2(newUserRequest, handler, consumer);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createUserAsync$2(NewUserRequest newUserRequest, Handler handler, final Consumer consumer) {
        final NewUserResponse createUser = this.mUserManager.createUser(newUserRequest);
        handler.post(new Runnable() { // from class: com.android.settings.users.AddSupervisedUserActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                consumer.accept(createUser);
            }
        });
    }

    private void createUser() {
        NewUserRequest build = new NewUserRequest.Builder().setName(getString(R.string.user_new_user_name)).build();
        final AlertDialog create = new AlertDialog.Builder(this).setMessage(getString(R.string.creating_new_user_dialog_message)).setCancelable(false).create();
        create.show();
        createUserAsync(build, new Consumer() { // from class: com.android.settings.users.AddSupervisedUserActivity$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AddSupervisedUserActivity.this.lambda$createUser$3(create, (NewUserResponse) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createUser$3(AlertDialog alertDialog, NewUserResponse newUserResponse) {
        alertDialog.dismiss();
        if (newUserResponse.isSuccessful()) {
            this.mActivityManager.switchUser(newUserResponse.getUser());
            finish();
            return;
        }
        AlertDialog.Builder title = new AlertDialog.Builder(this).setTitle(getString(R.string.add_user_failed));
        title.setMessage(UserManager.UserOperationResult.class.getName() + " = " + newUserResponse.getOperationResult()).setNeutralButton(getString(R.string.okay), (DialogInterface.OnClickListener) null).show();
    }
}
