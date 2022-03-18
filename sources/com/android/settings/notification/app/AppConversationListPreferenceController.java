package com.android.settings.notification.app;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.pm.ShortcutInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.window.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class AppConversationListPreferenceController extends NotificationPreferenceController {
    protected PreferenceCategory mPreference;
    protected List<ConversationChannelWrapper> mConversations = new ArrayList();
    protected Comparator<ConversationChannelWrapper> mConversationComparator = AppConversationListPreferenceController$$ExternalSyntheticLambda0.INSTANCE;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "conversations";
    }

    protected int getTitleResId() {
        return R.string.conversations_category_title;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return false;
    }

    public AppConversationListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        if (this.mChannel != null && (this.mBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid) || "miscellaneous".equals(this.mChannel.getId()))) {
            return false;
        }
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow2 = this.mAppRow;
        if (!notificationBackend.hasSentValidMsg(appRow2.pkg, appRow2.uid)) {
            NotificationBackend notificationBackend2 = this.mBackend;
            NotificationBackend.AppRow appRow3 = this.mAppRow;
            if (!notificationBackend2.isInInvalidMsgState(appRow3.pkg, appRow3.uid)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference = (PreferenceCategory) preference;
        loadConversationsAndPopulate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void loadConversationsAndPopulate() {
        if (this.mAppRow != null) {
            new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.app.AppConversationListPreferenceController.1
                /* JADX INFO: Access modifiers changed from: protected */
                public Void doInBackground(Void... voidArr) {
                    AppConversationListPreferenceController appConversationListPreferenceController = AppConversationListPreferenceController.this;
                    NotificationBackend notificationBackend = appConversationListPreferenceController.mBackend;
                    NotificationBackend.AppRow appRow = appConversationListPreferenceController.mAppRow;
                    ParceledListSlice<ConversationChannelWrapper> conversations = notificationBackend.getConversations(appRow.pkg, appRow.uid);
                    if (conversations == null) {
                        return null;
                    }
                    AppConversationListPreferenceController appConversationListPreferenceController2 = AppConversationListPreferenceController.this;
                    appConversationListPreferenceController2.mConversations = appConversationListPreferenceController2.filterAndSortConversations(conversations.getList());
                    return null;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                public void onPostExecute(Void r1) {
                    AppConversationListPreferenceController appConversationListPreferenceController = AppConversationListPreferenceController.this;
                    if (((NotificationPreferenceController) appConversationListPreferenceController).mContext != null) {
                        appConversationListPreferenceController.populateList();
                    }
                }
            }.execute(new Void[0]);
        }
    }

    protected List<ConversationChannelWrapper> filterAndSortConversations(List<ConversationChannelWrapper> list) {
        Collections.sort(list, this.mConversationComparator);
        return list;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void populateList() {
        if (this.mPreference != null && !this.mConversations.isEmpty()) {
            this.mPreference.removeAll();
            this.mPreference.setTitle(getTitleResId());
            populateConversations();
        }
    }

    private void populateConversations() {
        for (ConversationChannelWrapper conversationChannelWrapper : this.mConversations) {
            if (!conversationChannelWrapper.getNotificationChannel().isDemoted()) {
                this.mPreference.addPreference(createConversationPref(conversationChannelWrapper));
            }
        }
    }

    protected Preference createConversationPref(ConversationChannelWrapper conversationChannelWrapper) {
        AppPreference appPreference = new AppPreference(((NotificationPreferenceController) this).mContext);
        populateConversationPreference(conversationChannelWrapper, appPreference);
        return appPreference;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void populateConversationPreference(ConversationChannelWrapper conversationChannelWrapper, Preference preference) {
        CharSequence charSequence;
        CharSequence charSequence2;
        ShortcutInfo shortcutInfo = conversationChannelWrapper.getShortcutInfo();
        if (shortcutInfo != null) {
            charSequence = shortcutInfo.getLabel();
        } else {
            charSequence = conversationChannelWrapper.getNotificationChannel().getName();
        }
        preference.setTitle(charSequence);
        if (conversationChannelWrapper.getNotificationChannel().getGroup() != null) {
            charSequence2 = ((NotificationPreferenceController) this).mContext.getString(R.string.notification_conversation_summary, conversationChannelWrapper.getParentChannelLabel(), conversationChannelWrapper.getGroupLabel());
        } else {
            charSequence2 = conversationChannelWrapper.getParentChannelLabel();
        }
        preference.setSummary(charSequence2);
        if (shortcutInfo != null) {
            NotificationBackend notificationBackend = this.mBackend;
            Context context = ((NotificationPreferenceController) this).mContext;
            NotificationBackend.AppRow appRow = this.mAppRow;
            preference.setIcon(notificationBackend.getConversationDrawable(context, shortcutInfo, appRow.pkg, appRow.uid, conversationChannelWrapper.getNotificationChannel().isImportantConversation()));
        }
        preference.setKey(conversationChannelWrapper.getNotificationChannel().getId());
        Bundle bundle = new Bundle();
        bundle.putInt("uid", this.mAppRow.uid);
        bundle.putString("package", this.mAppRow.pkg);
        bundle.putString("android.provider.extra.CHANNEL_ID", conversationChannelWrapper.getNotificationChannel().getParentChannelId());
        bundle.putString("android.provider.extra.CONVERSATION_ID", conversationChannelWrapper.getNotificationChannel().getConversationId());
        bundle.putBoolean("fromSettings", true);
        preference.setIntent(new SubSettingLauncher(((NotificationPreferenceController) this).mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setExtras(bundle).setTitleText(preference.getTitle()).setSourceMetricsCategory(72).toIntent());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$new$0(ConversationChannelWrapper conversationChannelWrapper, ConversationChannelWrapper conversationChannelWrapper2) {
        if (conversationChannelWrapper.getNotificationChannel().isImportantConversation() != conversationChannelWrapper2.getNotificationChannel().isImportantConversation()) {
            return Boolean.compare(conversationChannelWrapper2.getNotificationChannel().isImportantConversation(), conversationChannelWrapper.getNotificationChannel().isImportantConversation());
        }
        return conversationChannelWrapper.getNotificationChannel().getId().compareTo(conversationChannelWrapper2.getNotificationChannel().getId());
    }
}
