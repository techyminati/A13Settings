package com.android.settings.development.tare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.window.R;
/* loaded from: classes.dex */
public class TareHomePage extends Activity {
    private TextView mAlarmManagerView;
    private TextView mJobSchedulerView;
    private Switch mOnSwitch;
    private Button mRevButton;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.tare_homepage);
        this.mOnSwitch = (Switch) findViewById(R.id.on_switch);
        this.mRevButton = (Button) findViewById(R.id.revert_button);
        this.mAlarmManagerView = (TextView) findViewById(R.id.alarmmanager);
        this.mJobSchedulerView = (TextView) findViewById(R.id.jobscheduler);
        boolean z = false;
        if (Settings.Global.getInt(getContentResolver(), "enable_tare", 0) == 1) {
            z = true;
        }
        setEnabled(z);
        this.mOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.development.tare.TareHomePage.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                TareHomePage.this.setEnabled(z2);
                Settings.Global.putInt(TareHomePage.this.getContentResolver(), "enable_tare", z2 ? 1 : 0);
            }
        });
    }

    public void revertSettings(View view) {
        Toast.makeText(this, (int) R.string.tare_settings_reverted_toast, 1).show();
        Settings.Global.putString(getApplicationContext().getContentResolver(), "enable_tare", null);
        setEnabled(false);
    }

    public void launchAlarmManagerPage(View view) {
        Intent intent = new Intent(getApplicationContext(), DropdownActivity.class);
        intent.putExtra("policy", 0);
        startActivity(intent);
    }

    public void launchJobSchedulerPage(View view) {
        Intent intent = new Intent(getApplicationContext(), DropdownActivity.class);
        intent.putExtra("policy", 1);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEnabled(boolean z) {
        this.mRevButton.setEnabled(z);
        this.mAlarmManagerView.setEnabled(z);
        this.mJobSchedulerView.setEnabled(z);
        this.mOnSwitch.setChecked(z);
    }
}
