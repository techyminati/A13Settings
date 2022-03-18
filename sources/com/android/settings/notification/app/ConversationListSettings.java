package com.android.settings.notification.app;

import android.app.people.IPeopleManager;
import android.content.Context;
import android.os.ServiceManager;
import android.util.Log;
import androidx.window.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ConversationListSettings extends DashboardFragment {
    private static final boolean DEBUG = Log.isLoggable("ConvoListSettings", 3);
    NotificationBackend mBackend = new NotificationBackend();
    protected List<AbstractPreferenceController> mControllers = new ArrayList();
    IPeopleManager mPs = IPeopleManager.Stub.asInterface(ServiceManager.getService("people"));

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConvoListSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1834;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.conversation_list_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mControllers = arrayList;
        arrayList.add(new NoConversationsPreferenceController(context, this.mBackend, this.mPs));
        this.mControllers.add(new PriorityConversationsPreferenceController(context, this.mBackend));
        this.mControllers.add(new AllConversationsPreferenceController(context, this.mBackend));
        this.mControllers.add(new RecentConversationsPreferenceController(context, this.mBackend, this.mPs));
        return new ArrayList(this.mControllers);
    }
}
