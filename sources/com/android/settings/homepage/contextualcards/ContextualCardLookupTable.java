package com.android.settings.homepage.contextualcards;

import android.util.Log;
import androidx.window.R;
import com.android.settings.homepage.contextualcards.ContextualCardLookupTable;
import com.android.settings.homepage.contextualcards.conditional.ConditionContextualCardController;
import com.android.settings.homepage.contextualcards.conditional.ConditionContextualCardRenderer;
import com.android.settings.homepage.contextualcards.conditional.ConditionFooterContextualCardRenderer;
import com.android.settings.homepage.contextualcards.conditional.ConditionHeaderContextualCardRenderer;
import com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardController;
import com.android.settings.homepage.contextualcards.legacysuggestion.LegacySuggestionContextualCardRenderer;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardController;
import com.android.settings.homepage.contextualcards.slices.SliceContextualCardRenderer;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ContextualCardLookupTable {
    static final Set<ControllerRendererMapping> LOOKUP_TABLE = new TreeSet<ControllerRendererMapping>() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLookupTable.1
        {
            add(new ControllerRendererMapping(3, R.layout.conditional_card_half_tile, ConditionContextualCardController.class, ConditionContextualCardRenderer.class));
            add(new ControllerRendererMapping(3, R.layout.conditional_card_full_tile, ConditionContextualCardController.class, ConditionContextualCardRenderer.class));
            add(new ControllerRendererMapping(2, R.layout.legacy_suggestion_tile, LegacySuggestionContextualCardController.class, LegacySuggestionContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, R.layout.contextual_slice_full_tile, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, R.layout.contextual_slice_half_tile, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(1, R.layout.contextual_slice_sticky_tile, SliceContextualCardController.class, SliceContextualCardRenderer.class));
            add(new ControllerRendererMapping(5, R.layout.conditional_card_footer, ConditionContextualCardController.class, ConditionFooterContextualCardRenderer.class));
            add(new ControllerRendererMapping(4, R.layout.conditional_card_header, ConditionContextualCardController.class, ConditionHeaderContextualCardRenderer.class));
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ControllerRendererMapping implements Comparable<ControllerRendererMapping> {
        final int mCardType;
        final Class<? extends ContextualCardController> mControllerClass;
        final Class<? extends ContextualCardRenderer> mRendererClass;
        final int mViewType;

        ControllerRendererMapping(int i, int i2, Class<? extends ContextualCardController> cls, Class<? extends ContextualCardRenderer> cls2) {
            this.mCardType = i;
            this.mViewType = i2;
            this.mControllerClass = cls;
            this.mRendererClass = cls2;
        }

        public int compareTo(ControllerRendererMapping controllerRendererMapping) {
            return Comparator.comparingInt(ContextualCardLookupTable$ControllerRendererMapping$$ExternalSyntheticLambda0.INSTANCE).thenComparingInt(ContextualCardLookupTable$ControllerRendererMapping$$ExternalSyntheticLambda1.INSTANCE).compare(this, controllerRendererMapping);
        }
    }

    public static Class<? extends ContextualCardController> getCardControllerClass(int i) {
        for (ControllerRendererMapping controllerRendererMapping : LOOKUP_TABLE) {
            if (controllerRendererMapping.mCardType == i) {
                return controllerRendererMapping.mControllerClass;
            }
        }
        return null;
    }

    public static Class<? extends ContextualCardRenderer> getCardRendererClassByViewType(final int i) throws IllegalStateException {
        List list = (List) LOOKUP_TABLE.stream().filter(new Predicate() { // from class: com.android.settings.homepage.contextualcards.ContextualCardLookupTable$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getCardRendererClassByViewType$0;
                lambda$getCardRendererClassByViewType$0 = ContextualCardLookupTable.lambda$getCardRendererClassByViewType$0(i, (ContextualCardLookupTable.ControllerRendererMapping) obj);
                return lambda$getCardRendererClassByViewType$0;
            }
        }).collect(Collectors.toList());
        if (list == null || list.isEmpty()) {
            Log.w("ContextualCardLookup", "No matching mapping");
            return null;
        } else if (list.size() == 1) {
            return ((ControllerRendererMapping) list.get(0)).mRendererClass;
        } else {
            throw new IllegalStateException("Have duplicate VIEW_TYPE in lookup table.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getCardRendererClassByViewType$0(int i, ControllerRendererMapping controllerRendererMapping) {
        return controllerRendererMapping.mViewType == i;
    }
}
