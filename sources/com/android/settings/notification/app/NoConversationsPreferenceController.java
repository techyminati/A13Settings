package com.android.settings.notification.app;

import android.app.people.IPeopleManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.service.notification.ConversationChannelWrapper;
import android.util.Log;
import androidx.preference.Preference;
import androidx.window.R;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class NoConversationsPreferenceController extends ConversationListPreferenceController {
    private static String TAG = "NoConversationsPC";
    private int mConversationCount = 0;
    private IPeopleManager mPs;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "no_conversations";
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    Preference getSummaryPreference() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return false;
    }

    public NoConversationsPreferenceController(Context context, NotificationBackend notificationBackend, IPeopleManager iPeopleManager) {
        super(context, notificationBackend);
        this.mPs = iPeopleManager;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        final LayoutPreference layoutPreference = (LayoutPreference) preference;
        new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.notification.app.NoConversationsPreferenceController.1
            /* JADX INFO: Access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                NoConversationsPreferenceController noConversationsPreferenceController = NoConversationsPreferenceController.this;
                noConversationsPreferenceController.mConversationCount = noConversationsPreferenceController.mBackend.getConversations(false).getList().size();
                try {
                    NoConversationsPreferenceController.this.mConversationCount += NoConversationsPreferenceController.this.mPs.getRecentConversations().getList().size();
                    return null;
                } catch (RemoteException e) {
                    Log.w(NoConversationsPreferenceController.TAG, "Error calling PS", e);
                    return null;
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            public void onPostExecute(Void r3) {
                if (((AbstractPreferenceController) NoConversationsPreferenceController.this).mContext != null) {
                    boolean z = false;
                    layoutPreference.findViewById(R.id.onboarding).setVisibility(NoConversationsPreferenceController.this.mConversationCount == 0 ? 0 : 8);
                    Preference preference2 = preference;
                    if (NoConversationsPreferenceController.this.mConversationCount == 0) {
                        z = true;
                    }
                    preference2.setVisible(z);
                }
            }
        }.execute(new Void[0]);
    }
}
