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
public class PriorityConversationsPreferenceController extends ConversationListPreferenceController {
    private List<ConversationChannelWrapper> mConversations;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "important_conversations";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public PriorityConversationsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    Preference getSummaryPreference() {
        Preference preference = new Preference(this.mContext);
        preference.setOrder(1);
        preference.setSummary(R.string.important_conversations_summary_bubbles);
        preference.setSelectable(false);
        return preference;
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return conversationChannelWrapper.getNotificationChannel().isImportantConversation();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        List<ConversationChannelWrapper> list = this.mBackend.getConversations(true).getList();
        this.mConversations = list;
        Collections.sort(list, this.mConversationComparator);
        populateList(this.mConversations, (PreferenceCategory) preference);
    }
}
