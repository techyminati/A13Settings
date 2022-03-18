package com.android.settings.applications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.window.R;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.password.ChooseLockSettingsHelper;
/* loaded from: classes.dex */
public class ConvertToFbe extends InstrumentedFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 402;
    }

    private boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(i).setTitle(getActivity().getResources().getText(R.string.convert_to_file_encryption)).show();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R.string.convert_to_file_encryption);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.convert_fbe, (ViewGroup) null);
        ((Button) inflate.findViewById(R.id.button_convert_fbe)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.ConvertToFbe$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ConvertToFbe.this.lambda$onCreateView$0(view);
            }
        });
        return inflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(View view) {
        if (!runKeyguardConfirmation(55)) {
            convert();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 55 && i2 == -1) {
            convert();
        }
    }

    private void convert() {
        new SubSettingLauncher(getContext()).setDestination(ConfirmConvertToFbe.class.getName()).setTitleRes(R.string.convert_to_file_encryption).setSourceMetricsCategory(getMetricsCategory()).launch();
    }
}
