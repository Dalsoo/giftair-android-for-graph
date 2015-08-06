package giftair.co.giftair_android03;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class AutoLinkSettingActivity extends AppCompatActivity {

    private ListView AutoDeviceList;
    private ArrayList<DeviceData> DeviceList;
    private DeviceData Data;
    private AutoLinkSettingAdapter adapter;
    private Button SuccessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_link_setting);

        AutoDeviceList = (ListView)findViewById(R.id.autoDevicelist);

        DeviceList = new ArrayList<DeviceData>();

        Realm realm = Realm.getInstance(AutoLinkSettingActivity.this);
        realm.beginTransaction();

        RealmResults<Database> query = realm.where(Database.class).findAll();
        for (int i = 0; i < query.size(); i++) {
            Data = new DeviceData("", "", "", false);
            Data.setImageLocal("");
            Data.setDeviceName(query.get(i).getDeviceName());
            Data.setMacAddress(query.get(i).getMacAddr());
            Data.setAutoCheck(query.get(i).isAutoCheck());
            DeviceList.add(Data);
        }
        realm.commitTransaction();

        realm.close();

        adapter = new AutoLinkSettingAdapter(this, R.layout.autodeviceitem, DeviceList);
        AutoDeviceList.setAdapter(adapter);

        SuccessButton = (Button)findViewById(R.id.SuccessButton);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.AutoDeviceSetting));
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        SuccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoLinkSettingActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_auto_link_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home :
                AutoLinkSettingActivity.this.finish();
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
