package com.android.settings.notification.app;

import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class ConversationHeaderPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, LifecycleObserver {
    private final DashboardFragment mFragment;
    private EntityHeaderController mHeaderController;
    private boolean mStarted = false;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "pref_app_header";
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return true;
    }

    public ConversationHeaderPreferenceController(Context context, DashboardFragment dashboardFragment) {
        super(context, null);
        this.mFragment = dashboardFragment;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mAppRow != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DashboardFragment dashboardFragment;
        if (this.mAppRow != null && (dashboardFragment = this.mFragment) != null) {
            FragmentActivity activity = this.mStarted ? dashboardFragment.getActivity() : null;
            if (activity != null) {
                EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this.mFragment, ((LayoutPreference) preference).findViewById(R.id.entity_header));
                this.mHeaderController = newInstance;
                LayoutPreference done = newInstance.setIcon(this.mConversationDrawable).setLabel(getLabel()).setSummary(getSummary()).setPackageName(this.mAppRow.pkg).setUid(this.mAppRow.uid).setButtonActions(1, 0).setHasAppInfoLink(true).setRecyclerView(this.mFragment.getListView(), this.mFragment.getSettingsLifecycle()).done(activity, ((NotificationPreferenceController) this).mContext);
                done.findViewById(R.id.entity_header).setVisibility(0);
                done.findViewById(R.id.entity_header).setBackground(null);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mChannel == null || isDefaultChannel()) {
            return this.mChannelGroup != null ? this.mAppRow.label.toString() : "";
        }
        NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
        if (notificationChannelGroup == null || TextUtils.isEmpty(notificationChannelGroup.getName())) {
            return this.mAppRow.label.toString();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        BidiFormatter instance = BidiFormatter.getInstance();
        spannableStringBuilder.append((CharSequence) instance.unicodeWrap(this.mAppRow.label.toString()));
        spannableStringBuilder.append(instance.unicodeWrap(((NotificationPreferenceController) this).mContext.getText(R.string.notification_header_divider_symbol_with_spaces)));
        spannableStringBuilder.append((CharSequence) instance.unicodeWrap(this.mChannelGroup.getName().toString()));
        return spannableStringBuilder.toString();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mStarted = true;
    }

    CharSequence getLabel() {
        ShortcutInfo shortcutInfo = this.mConversationInfo;
        if (shortcutInfo != null) {
            return shortcutInfo.getLabel();
        }
        return this.mChannel.getName();
    }
}
