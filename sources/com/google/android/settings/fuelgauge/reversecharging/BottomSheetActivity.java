package com.google.android.settings.fuelgauge.reversecharging;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.window.R;
/* loaded from: classes2.dex */
public class BottomSheetActivity extends FragmentActivity {
    static final String REVERSE_CHARGING_SETTINGS = "android.settings.REVERSE_CHARGING_SETTINGS";
    ReverseChargingManager mReverseChargingManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mReverseChargingManager == null) {
            this.mReverseChargingManager = ReverseChargingManager.getInstance(this);
        }
        if (!this.mReverseChargingManager.isSupportedReverseCharging()) {
            finish();
            return;
        }
        setContentView(R.layout.reverse_charging_bottom_sheet);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new VideoPreferenceFragment()).commit();
        ((Button) findViewById(R.id.ok_btn)).setOnClickListener(new View.OnClickListener() { // from class: com.google.android.settings.fuelgauge.reversecharging.BottomSheetActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BottomSheetActivity.this.lambda$onCreate$0(view);
            }
        });
        ((Button) findViewById(R.id.learn_more_btn)).setOnClickListener(new View.OnClickListener() { // from class: com.google.android.settings.fuelgauge.reversecharging.BottomSheetActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BottomSheetActivity.this.lambda$onCreate$1(view);
            }
        });
        setTitle(getString(R.string.reverse_charging_title));
        ((TextView) findViewById(R.id.toolbar_title)).setText(getString(R.string.reverse_charging_title));
        ((TextView) findViewById(R.id.header_subtitle)).setText(getString(R.string.reverse_charging_instructions_title));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(View view) {
        onOkClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(View view) {
        onLearnMoreClick();
    }

    private void onLearnMoreClick() {
        Intent intent = new Intent(REVERSE_CHARGING_SETTINGS);
        intent.setPackage("com.android.settings");
        startActivity(intent);
        finish();
    }

    private void onOkClick() {
        finish();
    }
}
