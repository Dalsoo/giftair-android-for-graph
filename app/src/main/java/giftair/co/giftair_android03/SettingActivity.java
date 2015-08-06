package giftair.co.giftair_android03;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by parkdgun on 2015-07-27.
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    private Preference UserInfo, DeviceList, DeviceAddList;
    private SwitchPreference AutoLogin, Device;
    public static SwitchPreference AirQuality, WeatherCheck, HealthIndexSwitch, Notifications;
    private EditTextPreference Measurement, wCheckCycle, hCheckCycle;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static TimerTask AirTask, WeatherTask, HealthTask;
    private static Timer AirTimer, WeatherTimer, HealthTimer;

    private static int Frag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.setting_layout);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.setting_toolbar, root, false);
        bar.setTitleTextColor(getResources().getColor(R.color.whitegrey));
        bar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UserInfo = (Preference) findPreference("UserInfo");
        AutoLogin = (SwitchPreference) findPreference("autoLogin");
        Device = (SwitchPreference) findPreference("autoDevice");
        DeviceList = (Preference) findPreference("DeviceList");
        DeviceAddList = (Preference) findPreference("Device");
        AirQuality = (SwitchPreference) findPreference("airQuality");
        Measurement = (EditTextPreference) findPreference("Measurement");
        WeatherCheck = (SwitchPreference) findPreference("WeatherCheck");
        wCheckCycle = (EditTextPreference) findPreference("wCheckCycle");
        HealthIndexSwitch = (SwitchPreference) findPreference("HealthIndexSwitch");
        hCheckCycle = (EditTextPreference) findPreference("hCheckCycle");
        Notifications = (SwitchPreference) findPreference("Notifications");

        if(Frag == 0) {
            if(AirQuality.isChecked()) {
                if(MainActivity.deviceAddress != null) {
                    AirT();
                    Frag += 1;
                }
            }

            if(WeatherCheck.isChecked()) {
                WeatherT();
                Frag += 1;
            }

            if(HealthIndexSwitch.isChecked()) {
                HealthT();
                Frag += 1;
            }
        }

        sharedPreferences = getSharedPreferences("Check", Activity.MODE_PRIVATE);
        Device.setChecked(sharedPreferences.getBoolean("Key", false));

        UserInfo.setOnPreferenceClickListener(this);
        AutoLogin.setOnPreferenceClickListener(this);
        Device.setOnPreferenceClickListener(this);
        DeviceList.setOnPreferenceClickListener(this);
        DeviceAddList.setOnPreferenceClickListener(this);
        AirQuality.setOnPreferenceClickListener(this);
        Measurement.setOnPreferenceClickListener(this);
        WeatherCheck.setOnPreferenceClickListener(this);
        wCheckCycle.setOnPreferenceClickListener(this);
        HealthIndexSwitch.setOnPreferenceClickListener(this);
        hCheckCycle.setOnPreferenceClickListener(this);
        Notifications.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if (preference.getKey().equals("UserInfo")) {
            if (MainActivity.MailPanel.getText().equals(getString(R.string.Guest))) {
                Toast.makeText(getApplicationContext(), R.string.Loginuse, Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
            }
        } else if (preference.getKey().equals("autoLogin")) {
            if (MainActivity.MailPanel.getText().equals(getString(R.string.Guest))) {
                AutoLogin.setChecked(false);
                Toast.makeText(getApplicationContext(), R.string.Loginuse, Toast.LENGTH_SHORT).show();
            } else {
                if (AutoLogin.isChecked()) {
                    MainActivity.pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                    MainActivity.editor = MainActivity.pref.edit();
                    MainActivity.editor.putString("ID", MainActivity.MailPanel.getText().toString());
                    MainActivity.editor.putString("NAME", MainActivity.NamePanel.getText().toString());
                    MainActivity.editor.putBoolean("Check", AutoLogin.isChecked());
                    MainActivity.editor.commit();
                    Toast.makeText(getApplicationContext(), R.string.AutoLoginOk, Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                    MainActivity.editor = MainActivity.pref.edit();
                    MainActivity.editor.remove("ID");
                    MainActivity.editor.remove("NAME");
                    MainActivity.editor.remove("Check");
                    MainActivity.editor.commit();
                    AutoLogin.setChecked(false);
                    Toast.makeText(getApplicationContext(), R.string.AutoLoginCancel, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (preference.getKey().equals("autoDevice")) {
            if (Device.isChecked()) {
                sharedPreferences = getSharedPreferences("Check", Activity.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("Key", Device.isChecked());
                editor.commit();

                MainActivity.Devicepref = getSharedPreferences("devicePref", Activity.MODE_PRIVATE);
                MainActivity.Deviceeditor = MainActivity.Devicepref.edit();

                Realm realm = Realm.getInstance(SettingActivity.this);
                realm.beginTransaction();

                RealmResults<Database> query = realm.where(Database.class).equalTo("AutoCheck", true).findAll();
                for (int i = 0; i < query.size(); i++) {
                    MainActivity.Deviceeditor.remove("Status_" + i);
                    MainActivity.Deviceeditor.putString("Status_" + i, query.get(i).getMacAddr());
                }
                MainActivity.Deviceeditor.putInt("Status_size", query.size());
                MainActivity.Deviceeditor.commit();
                realm.commitTransaction();
                realm.close();

                Toast.makeText(getApplicationContext(), R.string.AppReStart, Toast.LENGTH_LONG).show();
            } else {
                sharedPreferences = getSharedPreferences("Check", Activity.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.remove("Key");
                editor.commit();

                MainActivity.Devicepref = getSharedPreferences("devicePref", Activity.MODE_PRIVATE);
                Realm realm = Realm.getInstance(SettingActivity.this);
                realm.beginTransaction();

                RealmResults<Database> query = realm.where(Database.class).findAll();
                MainActivity.Deviceeditor = MainActivity.Devicepref.edit();
                for (int i = 0; i < query.size(); i++) {
                    MainActivity.Deviceeditor.remove("Status_" + i);
                    MainActivity.Deviceeditor.remove("Status_" + i);
                }
                MainActivity.Deviceeditor.remove("Status_size");
                MainActivity.Deviceeditor.commit();
                realm.commitTransaction();
                realm.close();

                Toast.makeText(getApplicationContext(), R.string.AppReStart, Toast.LENGTH_LONG).show();
            }
        } else if (preference.getKey().equals("DeviceList")) {
            startActivity(new Intent(getApplicationContext(), AutoLinkSettingActivity.class));
        } else if (preference.getKey().equals("Device")) {
            startActivity(new Intent(getApplicationContext(), DeviceActivity.class));
        } else if (preference.getKey().equals("airQuality")) {
            if(MainActivity.deviceAddress != null) {
                if (AirQuality.isChecked()) {
                    AirTask = new TimerTask() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new MeasurementEvent());
                        }
                    };
                    AirTimer = new Timer();
                    AirTimer.schedule(AirTask, 3000, Integer.parseInt(Measurement.getText().toString()) * 1000);
                } else {
                    if (AirTimer != null) {
                        AirTimer.cancel();
                        AirTimer.purge();
                        AirTimer = null;
                        AirTask.cancel();
                        AirTask = null;
                        EventBus.getDefault().removeStickyEvent(MeasurementEvent.class);
                    }
                }
            }
        } else if (preference.getKey().equals("Measurement")) {

        } else if (preference.getKey().equals("WeatherCheck")) {
            if (WeatherCheck.isChecked()) {
                WeatherTask = new TimerTask() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new WeatherEvent());
                    }
                };
                WeatherTimer = new Timer();
                WeatherTimer.schedule(WeatherTask, 3000, Integer.parseInt(wCheckCycle.getText().toString()) * 1000);
            } else {
                WeatherTimer.cancel();
                WeatherTimer.purge();
                WeatherTimer = null;
                WeatherTask.cancel();
                WeatherTask = null;
                EventBus.getDefault().removeStickyEvent(WeatherEvent.class);
            }
        } else if (preference.getKey().equals("wCheckCycle")) {

        } else if (preference.getKey().equals("HealthIndexSwitch")) {
            if (HealthIndexSwitch.isChecked()) {
                HealthTask = new TimerTask() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new HealthEvent());
                    }
                };
                HealthTimer = new Timer();
                HealthTimer.schedule(HealthTask, 3000, Integer.parseInt(hCheckCycle.getText().toString()) * 1000);
            } else {
                HealthTimer.cancel();
                HealthTimer.purge();
                HealthTimer = null;
                HealthTask.cancel();
                HealthTask = null;
                EventBus.getDefault().removeStickyEvent(HealthEvent.class);
            }
        } else if (preference.getKey().equals("hCheckCycle")) {

        } else if (preference.getKey().equals("Notifications")) {

        }

        return true;
    }

    public void AirT() {
        AirTask = new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new MeasurementEvent());
            }
        };
        AirTimer = new Timer();
        AirTimer.schedule(AirTask, 3000, Integer.parseInt(Measurement.getText().toString()) * 1000);
    }

    public void WeatherT() {
        WeatherTask = new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new WeatherEvent());
            }
        };
        WeatherTimer = new Timer();
        WeatherTimer.schedule(WeatherTask, 3000, Integer.parseInt(wCheckCycle.getText().toString()) * 1000);
    }

    public void HealthT() {
        HealthTask = new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new HealthEvent());
            }
        };
        HealthTimer = new Timer();
        HealthTimer.schedule(HealthTask, 3000, Integer.parseInt(hCheckCycle.getText().toString()) * 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(MeasurementEvent.class);
        EventBus.getDefault().removeStickyEvent(WeatherEvent.class);
        EventBus.getDefault().removeStickyEvent(HealthEvent.class);

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
