package com.google.android.settings.dream;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.window.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dream.AutoFitGridLayoutManager;
import com.android.settings.dream.DreamAdapter;
import com.android.settings.dream.IDreamItem;
import com.android.settingslib.dream.DreamBackend;
import com.google.android.settings.dream.DreamSetupFragment;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class DreamSetupFragment extends SettingsPreferenceFragment {
    private DreamBackend.DreamInfo mActiveDream;
    private DreamBackend mBackend;
    private FooterButton mFooterButton;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 47;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.dream_setup_layout, viewGroup, false);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        DreamBackend instance = DreamBackend.getInstance(getContext());
        this.mBackend = instance;
        List<DreamBackend.DreamInfo> dreamInfos = instance.getDreamInfos();
        this.mActiveDream = dreamInfos.stream().filter(DreamSetupFragment$$ExternalSyntheticLambda2.INSTANCE).findFirst().orElse(null);
        DreamAdapter dreamAdapter = new DreamAdapter((List) dreamInfos.stream().map(new Function() { // from class: com.google.android.settings.dream.DreamSetupFragment$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                DreamSetupFragment.DreamItem lambda$onViewCreated$1;
                lambda$onViewCreated$1 = DreamSetupFragment.this.lambda$onViewCreated$1((DreamBackend.DreamInfo) obj);
                return lambda$onViewCreated$1;
            }
        }).collect(Collectors.toList()));
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dream_setup_list);
        recyclerView.setLayoutManager(new AutoFitGridLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(dreamAdapter);
        this.mFooterButton = new FooterButton.Builder(getContext()).setListener(new View.OnClickListener() { // from class: com.google.android.settings.dream.DreamSetupFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                DreamSetupFragment.this.onPrimaryButtonClicked(view2);
            }
        }).setButtonType(5).setTheme(R.style.SudGlifButton_Primary).build();
        updateFooterButtonText();
        ((FooterBarMixin) ((GlifLayout) view.findViewById(R.id.setup_wizard_layout)).getMixin(FooterBarMixin.class)).setPrimaryButton(this.mFooterButton);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ DreamItem lambda$onViewCreated$1(DreamBackend.DreamInfo dreamInfo) {
        return new DreamItem(dreamInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFooterButtonText() {
        this.mFooterButton.setText(getContext().getString(canCustomizeDream() ? R.string.wizard_next : R.string.wizard_finish));
    }

    private boolean canCustomizeDream() {
        DreamBackend.DreamInfo dreamInfo = this.mActiveDream;
        return (dreamInfo == null || dreamInfo.settingsComponentName == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPrimaryButtonClicked(View view) {
        if (canCustomizeDream()) {
            Intent component = new Intent().setComponent(this.mActiveDream.settingsComponentName);
            WizardManagerHelper.copyWizardManagerExtras(getIntent(), component);
            startActivity(component);
        }
        setResult(0);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class DreamItem implements IDreamItem {
        private final DreamBackend.DreamInfo mDreamInfo;

        private DreamItem(DreamBackend.DreamInfo dreamInfo) {
            this.mDreamInfo = dreamInfo;
        }

        @Override // com.android.settings.dream.IDreamItem
        public CharSequence getTitle() {
            return this.mDreamInfo.caption;
        }

        @Override // com.android.settings.dream.IDreamItem
        public Drawable getIcon() {
            return this.mDreamInfo.icon;
        }

        @Override // com.android.settings.dream.IDreamItem
        public void onItemClicked() {
            DreamSetupFragment.this.mActiveDream = this.mDreamInfo;
            DreamSetupFragment.this.mBackend.setActiveDream(this.mDreamInfo.componentName);
            DreamSetupFragment.this.updateFooterButtonText();
        }

        @Override // com.android.settings.dream.IDreamItem
        public Drawable getPreviewImage() {
            return this.mDreamInfo.previewImage;
        }

        @Override // com.android.settings.dream.IDreamItem
        public boolean isActive() {
            if (DreamSetupFragment.this.mActiveDream == null) {
                return false;
            }
            return this.mDreamInfo.componentName.equals(DreamSetupFragment.this.mActiveDream.componentName);
        }
    }
}
