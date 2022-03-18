package com.android.settings.privacy;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.dashboard.profileselector.UserAdapter;
import com.android.settings.privacy.EnableContentCaptureWithServiceSettingsPreferenceController;
import com.android.settings.utils.ContentCaptureUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class EnableContentCaptureWithServiceSettingsPreferenceController extends TogglePreferenceController {
    private static final String TAG = "ContentCaptureController";
    private final UserManager mUserManager;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_privacy;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EnableContentCaptureWithServiceSettingsPreferenceController(Context context, String str) {
        super(context, str);
        this.mUserManager = UserManager.get(context);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return ContentCaptureUtils.isEnabledForUser(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ContentCaptureUtils.setEnabledForUser(this.mContext, z);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ComponentName serviceSettingsComponentName = ContentCaptureUtils.getServiceSettingsComponentName();
        if (serviceSettingsComponentName != null) {
            preference.setIntent(new Intent("android.intent.action.MAIN").setComponent(serviceSettingsComponentName));
        } else {
            Log.w(TAG, "No component name for custom service settings");
            preference.setSelectable(false);
        }
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.privacy.EnableContentCaptureWithServiceSettingsPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$updateState$0;
                lambda$updateState$0 = EnableContentCaptureWithServiceSettingsPreferenceController.this.lambda$updateState$0(preference2);
                return lambda$updateState$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateState$0(Preference preference) {
        ProfileSelectDialog.show(this.mContext, preference);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ContentCaptureUtils.isFeatureAvailable() && ContentCaptureUtils.getServiceSettingsComponentName() != null ? 0 : 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ProfileSelectDialog {
        public static void show(final Context context, final Preference preference) {
            UserManager userManager = UserManager.get(context);
            List<UserInfo> users = userManager.getUsers();
            final ArrayList arrayList = new ArrayList(users.size());
            for (UserInfo userInfo : users) {
                arrayList.add(userInfo.getUserHandle());
            }
            if (arrayList.size() == 1) {
                context.startActivityAsUser(preference.getIntent().addFlags(32768), (UserHandle) arrayList.get(0));
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.choose_profile).setAdapter(UserAdapter.createUserAdapter(userManager, context, arrayList), new DialogInterface.OnClickListener() { // from class: com.android.settings.privacy.EnableContentCaptureWithServiceSettingsPreferenceController$ProfileSelectDialog$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    EnableContentCaptureWithServiceSettingsPreferenceController.ProfileSelectDialog.lambda$show$0(arrayList, preference, context, dialogInterface, i);
                }
            }).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$show$0(ArrayList arrayList, Preference preference, Context context, DialogInterface dialogInterface, int i) {
            context.startActivityAsUser(preference.getIntent().addFlags(32768), (UserHandle) arrayList.get(i));
        }
    }
}
