package com.android.settings.notification.app;

import android.content.Context;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.window.R;
import com.android.settings.notification.NotificationBackend;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class AllConversationsPreferenceController extends ConversationListPreferenceController {
    private List<ConversationChannelWrapper> mConversations;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "other_conversations";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AllConversationsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    Preference getSummaryPreference() {
        Preference preference = new Preference(this.mContext);
        preference.setOrder(1);
        preference.setSummary(R.string.other_conversations_summary);
        preference.setSelectable(false);
        return preference;
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return !conversationChannelWrapper.getNotificationChannel().isImportantConversation();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        List<ConversationChannelWrapper> list = this.mBackend.getConversations(false).getList();
        this.mConversations = list;
        Collections.sort(list, this.mConversationComparator);
        populateList(this.mConversations, (PreferenceCategory) preference);
    }
}
