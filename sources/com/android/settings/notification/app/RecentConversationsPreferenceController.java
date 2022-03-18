package com.android.settings.notification.app;

import android.app.people.ConversationChannel;
import android.app.people.IPeopleManager;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.window.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.RecentConversationPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class RecentConversationsPreferenceController extends AbstractPreferenceController {
    private final NotificationBackend mBackend;
    protected Comparator<ConversationChannel> mConversationComparator = new Comparator<ConversationChannel>() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController.1
        private final Collator sCollator = Collator.getInstance();

        public int compare(ConversationChannel conversationChannel, ConversationChannel conversationChannel2) {
            int compare = (conversationChannel.getShortcutInfo().getLabel() == null || conversationChannel2.getShortcutInfo().getLabel() == null) ? 0 : this.sCollator.compare(conversationChannel.getShortcutInfo().getLabel().toString(), conversationChannel2.getShortcutInfo().getLabel().toString());
            return compare == 0 ? conversationChannel.getNotificationChannel().getId().compareTo(conversationChannel2.getNotificationChannel().getId()) : compare;
        }
    };
    private List<ConversationChannel> mConversations;
    private final IPeopleManager mPs;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "recent_conversations";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public RecentConversationsPreferenceController(Context context, NotificationBackend notificationBackend, IPeopleManager iPeopleManager) {
        super(context);
        this.mBackend = notificationBackend;
        this.mPs = iPeopleManager;
    }

    LayoutPreference getClearAll(final PreferenceGroup preferenceGroup) {
        LayoutPreference layoutPreference = new LayoutPreference(this.mContext, (int) R.layout.conversations_clear_recents);
        layoutPreference.setOrder(1);
        final Button button = (Button) layoutPreference.findViewById(R.id.conversation_settings_clear_recents);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RecentConversationsPreferenceController.this.lambda$getClearAll$0(preferenceGroup, button, view);
            }
        });
        return layoutPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getClearAll$0(PreferenceGroup preferenceGroup, Button button, View view) {
        try {
            this.mPs.removeAllRecentConversations();
            for (int preferenceCount = preferenceGroup.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
                Preference preference = preferenceGroup.getPreference(preferenceCount);
                if ((preference instanceof RecentConversationPreference) && ((RecentConversationPreference) preference).hasClearListener()) {
                    preferenceGroup.removePreference(preference);
                }
            }
            button.announceForAccessibility(this.mContext.getString(R.string.recent_convos_removed));
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could not clear recents", e);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
        try {
            this.mConversations = this.mPs.getRecentConversations().getList();
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could get recents", e);
        }
        Collections.sort(this.mConversations, this.mConversationComparator);
        populateList(this.mConversations, preferenceCategory);
    }

    protected void populateList(List<ConversationChannel> list, PreferenceGroup preferenceGroup) {
        LayoutPreference clearAll;
        preferenceGroup.removeAll();
        boolean populateConversations = list != null ? populateConversations(list, preferenceGroup) : false;
        if (preferenceGroup.getPreferenceCount() == 0) {
            preferenceGroup.setVisible(false);
            return;
        }
        preferenceGroup.setVisible(true);
        if (populateConversations && (clearAll = getClearAll(preferenceGroup)) != null) {
            preferenceGroup.addPreference(clearAll);
        }
    }

    protected boolean populateConversations(List<ConversationChannel> list, PreferenceGroup preferenceGroup) {
        int i = 100;
        boolean z = false;
        for (ConversationChannel conversationChannel : list) {
            if (conversationChannel.getNotificationChannel().getImportance() != 0 && (conversationChannel.getNotificationChannelGroup() == null || !conversationChannel.getNotificationChannelGroup().isBlocked())) {
                i++;
                RecentConversationPreference createConversationPref = createConversationPref(preferenceGroup, conversationChannel, i);
                preferenceGroup.addPreference(createConversationPref);
                if (createConversationPref.hasClearListener()) {
                    z = true;
                }
            }
        }
        return z;
    }

    protected RecentConversationPreference createConversationPref(final PreferenceGroup preferenceGroup, final ConversationChannel conversationChannel, int i) {
        final String str = conversationChannel.getShortcutInfo().getPackage();
        final int uid = conversationChannel.getUid();
        final String id = conversationChannel.getShortcutInfo().getId();
        final RecentConversationPreference recentConversationPreference = new RecentConversationPreference(this.mContext);
        if (!conversationChannel.hasActiveNotifications()) {
            recentConversationPreference.setOnClearClickListener(new RecentConversationPreference.OnClearClickListener() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda2
                @Override // com.android.settings.notification.app.RecentConversationPreference.OnClearClickListener
                public final void onClear() {
                    RecentConversationsPreferenceController.this.lambda$createConversationPref$1(str, uid, id, recentConversationPreference, preferenceGroup);
                }
            });
        }
        recentConversationPreference.setOrder(i);
        recentConversationPreference.setTitle(getTitle(conversationChannel));
        recentConversationPreference.setSummary(getSummary(conversationChannel));
        recentConversationPreference.setIcon(this.mBackend.getConversationDrawable(this.mContext, conversationChannel.getShortcutInfo(), str, uid, false));
        recentConversationPreference.setKey(conversationChannel.getNotificationChannel().getId() + ":" + id);
        recentConversationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.app.RecentConversationsPreferenceController$$ExternalSyntheticLambda1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$createConversationPref$2;
                lambda$createConversationPref$2 = RecentConversationsPreferenceController.this.lambda$createConversationPref$2(str, uid, conversationChannel, id, recentConversationPreference, preference);
                return lambda$createConversationPref$2;
            }
        });
        return recentConversationPreference;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createConversationPref$1(String str, int i, String str2, RecentConversationPreference recentConversationPreference, PreferenceGroup preferenceGroup) {
        try {
            this.mPs.removeRecentConversation(str, UserHandle.getUserId(i), str2);
            recentConversationPreference.getClearView().announceForAccessibility(this.mContext.getString(R.string.recent_convo_removed));
            preferenceGroup.removePreference(recentConversationPreference);
        } catch (RemoteException e) {
            Slog.w("RecentConversationsPC", "Could not clear recent", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createConversationPref$2(String str, int i, ConversationChannel conversationChannel, String str2, RecentConversationPreference recentConversationPreference, Preference preference) {
        this.mBackend.createConversationNotificationChannel(str, i, conversationChannel.getNotificationChannel(), str2);
        getSubSettingLauncher(conversationChannel, recentConversationPreference.getTitle()).launch();
        return true;
    }

    CharSequence getSummary(ConversationChannel conversationChannel) {
        if (conversationChannel.getNotificationChannelGroup() == null) {
            return conversationChannel.getNotificationChannel().getName();
        }
        return this.mContext.getString(R.string.notification_conversation_summary, conversationChannel.getNotificationChannel().getName(), conversationChannel.getNotificationChannelGroup().getName());
    }

    CharSequence getTitle(ConversationChannel conversationChannel) {
        return conversationChannel.getShortcutInfo().getLabel();
    }

    SubSettingLauncher getSubSettingLauncher(ConversationChannel conversationChannel, CharSequence charSequence) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", conversationChannel.getUid());
        bundle.putString("package", conversationChannel.getShortcutInfo().getPackage());
        bundle.putString("android.provider.extra.CHANNEL_ID", conversationChannel.getNotificationChannel().getId());
        bundle.putString("android.provider.extra.CONVERSATION_ID", conversationChannel.getShortcutInfo().getId());
        return new SubSettingLauncher(this.mContext).setDestination(ChannelNotificationSettings.class.getName()).setArguments(bundle).setExtras(bundle).setUserHandle(UserHandle.getUserHandleForUid(conversationChannel.getUid())).setTitleText(charSequence).setSourceMetricsCategory(1834);
    }
}
