package com.android.settings.network.helper;

import android.os.ParcelUuid;
import android.util.Log;
import androidx.annotation.Keep;
import com.android.settings.dashboard.DashboardFragment$$ExternalSyntheticLambda12;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SubscriptionGrouping implements UnaryOperator<List<SubscriptionAnnotation>> {
    public List<SubscriptionAnnotation> apply(List<SubscriptionAnnotation> list) {
        Log.d("SubscriptionGrouping", "Grouping " + list);
        Map map = (Map) list.stream().filter(SubscriptionGrouping$$ExternalSyntheticLambda3.INSTANCE).collect(Collectors.groupingBy(new Function() { // from class: com.android.settings.network.helper.SubscriptionGrouping$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ParcelUuid lambda$apply$0;
                lambda$apply$0 = SubscriptionGrouping.this.lambda$apply$0((SubscriptionAnnotation) obj);
                return lambda$apply$0;
            }
        }));
        map.replaceAll(new BiFunction() { // from class: com.android.settings.network.helper.SubscriptionGrouping$$ExternalSyntheticLambda1
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                List lambda$apply$1;
                lambda$apply$1 = SubscriptionGrouping.this.lambda$apply$1((ParcelUuid) obj, (List) obj2);
                return lambda$apply$1;
            }
        });
        return (List) map.values().stream().flatMap(DashboardFragment$$ExternalSyntheticLambda12.INSTANCE).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ List lambda$apply$1(ParcelUuid parcelUuid, List list) {
        return (parcelUuid == SubscriptionAnnotation.EMPTY_UUID || list.size() <= 1) ? list : Collections.singletonList(selectBestFromList(list));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Keep
    /* renamed from: getGroupUuid */
    public ParcelUuid lambda$apply$0(SubscriptionAnnotation subscriptionAnnotation) {
        ParcelUuid groupUuid = subscriptionAnnotation.getGroupUuid();
        return groupUuid == null ? SubscriptionAnnotation.EMPTY_UUID : groupUuid;
    }

    protected SubscriptionAnnotation selectBestFromList(final List<SubscriptionAnnotation> list) {
        return list.stream().sorted(SubscriptionGrouping$$ExternalSyntheticLambda0.INSTANCE.thenComparingInt(SubscriptionGrouping$$ExternalSyntheticLambda5.INSTANCE).thenComparingInt(new ToIntFunction() { // from class: com.android.settings.network.helper.SubscriptionGrouping$$ExternalSyntheticLambda4
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int indexOf;
                indexOf = list.indexOf((SubscriptionAnnotation) obj);
                return indexOf;
            }
        })).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$selectBestFromList$2(SubscriptionAnnotation subscriptionAnnotation, SubscriptionAnnotation subscriptionAnnotation2) {
        if (subscriptionAnnotation.isDisplayAllowed() != subscriptionAnnotation2.isDisplayAllowed()) {
            return subscriptionAnnotation.isDisplayAllowed() ? -1 : 1;
        }
        if (subscriptionAnnotation.isActive() != subscriptionAnnotation2.isActive()) {
            return subscriptionAnnotation.isActive() ? -1 : 1;
        }
        if (subscriptionAnnotation.isExisted() != subscriptionAnnotation2.isExisted()) {
            return subscriptionAnnotation.isExisted() ? -1 : 1;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$selectBestFromList$3(SubscriptionAnnotation subscriptionAnnotation) {
        return -subscriptionAnnotation.getType();
    }
}
