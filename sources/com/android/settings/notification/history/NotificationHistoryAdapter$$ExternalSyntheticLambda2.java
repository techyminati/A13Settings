package com.android.settings.notification.history;

import android.app.NotificationHistory;
import java.util.Comparator;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class NotificationHistoryAdapter$$ExternalSyntheticLambda2 implements Comparator {
    public static final /* synthetic */ NotificationHistoryAdapter$$ExternalSyntheticLambda2 INSTANCE = new NotificationHistoryAdapter$$ExternalSyntheticLambda2();

    private /* synthetic */ NotificationHistoryAdapter$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$onRebuildComplete$2;
        lambda$onRebuildComplete$2 = NotificationHistoryAdapter.lambda$onRebuildComplete$2((NotificationHistory.HistoricalNotification) obj, (NotificationHistory.HistoricalNotification) obj2);
        return lambda$onRebuildComplete$2;
    }
}
