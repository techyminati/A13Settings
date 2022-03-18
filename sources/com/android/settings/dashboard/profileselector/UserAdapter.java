package com.android.settings.dashboard.profileselector;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.window.R;
import com.android.internal.util.UserIcons;
import com.android.settingslib.drawable.UserIconDrawable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
/* loaded from: classes.dex */
public class UserAdapter implements SpinnerAdapter, ListAdapter {
    private ArrayList<UserDetails> data;
    private final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;
    private final LayoutInflater mInflater;

    @Override // android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override // android.widget.Adapter
    public int getItemViewType(int i) {
        return 0;
    }

    @Override // android.widget.Adapter
    public int getViewTypeCount() {
        return 1;
    }

    @Override // android.widget.Adapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.ListAdapter
    public boolean isEnabled(int i) {
        return true;
    }

    @Override // android.widget.Adapter
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override // android.widget.Adapter
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }

    /* loaded from: classes.dex */
    public static class UserDetails {
        private final Drawable mIcon;
        private final String mName;
        private final UserHandle mUserHandle;

        public UserDetails(UserHandle userHandle, UserManager userManager, final Context context) {
            Drawable drawable;
            this.mUserHandle = userHandle;
            UserInfo userInfo = userManager.getUserInfo(userHandle.getIdentifier());
            if (userInfo.isManagedProfile()) {
                this.mName = ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getString("Settings.WORK_PROFILE_USER_LABEL", new Callable() { // from class: com.android.settings.dashboard.profileselector.UserAdapter$UserDetails$$ExternalSyntheticLambda0
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        String string;
                        string = context.getString(R.string.managed_user_title);
                        return string;
                    }
                });
                drawable = context.getPackageManager().getUserBadgeForDensityNoBackground(userHandle, 0);
            } else {
                this.mName = userInfo.name;
                int i = userInfo.id;
                if (userManager.getUserIcon(i) != null) {
                    drawable = new BitmapDrawable(context.getResources(), userManager.getUserIcon(i));
                } else {
                    drawable = UserIcons.getDefaultUserIcon(context.getResources(), i, false);
                }
            }
            this.mIcon = encircle(context, drawable);
        }

        private static Drawable encircle(Context context, Drawable drawable) {
            return new UserIconDrawable(UserIconDrawable.getDefaultSize(context)).setIconDrawable(drawable).bake();
        }
    }

    public UserAdapter(Context context, ArrayList<UserDetails> arrayList) {
        if (arrayList != null) {
            this.mContext = context;
            this.data = arrayList;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
            return;
        }
        throw new IllegalArgumentException("A list of user details must be provided");
    }

    public UserHandle getUserHandle(int i) {
        if (i < 0 || i >= this.data.size()) {
            return null;
        }
        return this.data.get(i).mUserHandle;
    }

    @Override // android.widget.SpinnerAdapter
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = createUser(viewGroup);
        }
        UserDetails userDetails = this.data.get(i);
        ((ImageView) view.findViewById(16908294)).setImageDrawable(userDetails.mIcon);
        ((TextView) view.findViewById(16908310)).setText(getTitle(userDetails));
        return view;
    }

    private String getTitle(UserDetails userDetails) {
        int identifier = userDetails.mUserHandle.getIdentifier();
        if (identifier == -2 || identifier == ActivityManager.getCurrentUser()) {
            return this.mDevicePolicyManager.getString("Settings.category_personal", new Callable() { // from class: com.android.settings.dashboard.profileselector.UserAdapter$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    String lambda$getTitle$0;
                    lambda$getTitle$0 = UserAdapter.this.lambda$getTitle$0();
                    return lambda$getTitle$0;
                }
            });
        }
        return this.mDevicePolicyManager.getString("Settings.WORK_CATEGORY_HEADER", new Callable() { // from class: com.android.settings.dashboard.profileselector.UserAdapter$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                String lambda$getTitle$1;
                lambda$getTitle$1 = UserAdapter.this.lambda$getTitle$1();
                return lambda$getTitle$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getTitle$0() throws Exception {
        return this.mContext.getString(R.string.category_personal);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getTitle$1() throws Exception {
        return this.mContext.getString(R.string.category_work);
    }

    private View createUser(ViewGroup viewGroup) {
        return this.mInflater.inflate(R.layout.user_preference, viewGroup, false);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.data.size();
    }

    @Override // android.widget.Adapter
    public UserDetails getItem(int i) {
        return this.data.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return this.data.get(i).mUserHandle.getIdentifier();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        return getDropDownView(i, view, viewGroup);
    }

    @Override // android.widget.Adapter
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public static UserAdapter createUserSpinnerAdapter(UserManager userManager, Context context) {
        List<UserHandle> userProfiles = userManager.getUserProfiles();
        if (userProfiles.size() < 2) {
            return null;
        }
        UserHandle userHandle = new UserHandle(UserHandle.myUserId());
        userProfiles.remove(userHandle);
        userProfiles.add(0, userHandle);
        return createUserAdapter(userManager, context, userProfiles);
    }

    public static UserAdapter createUserAdapter(UserManager userManager, Context context, List<UserHandle> list) {
        ArrayList arrayList = new ArrayList(list.size());
        int size = list.size();
        for (int i = 0; i < size; i++) {
            arrayList.add(new UserDetails(list.get(i), userManager, context));
        }
        return new UserAdapter(context, arrayList);
    }
}
