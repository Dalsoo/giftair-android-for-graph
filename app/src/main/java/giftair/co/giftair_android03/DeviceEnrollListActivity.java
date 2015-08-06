package giftair.co.giftair_android03;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class DeviceEnrollListActivity extends AppCompatActivity {

    private ArrayList<DeviceData> DeviceList;
    private DeviceData Data;
    private DeviceEnrollListAdapter adapter;
    private ListView deviceEnrollList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_enroll_list);

        deviceEnrollList = (ListView)findViewById(R.id.deviceEnrollList);

        DeviceList = new ArrayList<DeviceData>();

        Realm realm = Realm.getInstance(DeviceEnrollListActivity.this);
        realm.beginTransaction();

        RealmResults<Database> query = realm.where(Database.class).findAll();
        for (int i = 0; i < query.size(); i++) {
            Data = new DeviceData("","","", false);
            Data.setImageLocal("");
            Data.setDeviceName(query.get(i).getDeviceName());
            Data.setMacAddress(query.get(i).getMacAddr());
            DeviceList.add(Data);
        }
        realm.commitTransaction();
        realm.close();

        adapter = new DeviceEnrollListAdapter(this, R.layout.deviceitem, DeviceList);
        deviceEnrollList.setAdapter(adapter);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.DeviceEnrollList));
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        deviceEnrollList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Realm realm = Realm.getInstance(DeviceEnrollListActivity.this);
                realm.beginTransaction();

                RealmResults<Database> query = realm.where(Database.class).findAll();

                String dName = query.get(position).getDeviceName();
                String Maddr = query.get(position).getMacAddr();
                String Enroll = query.get(position).getEnrollment();
                realm.commitTransaction();

                realm.close();

                Intent Detail = new Intent(getApplicationContext(), DetailInfoActivity.class);
                Detail.putExtra("DeviceName", dName);
                Detail.putExtra("MacAddress", Maddr);
                Detail.putExtra("Enroll", Enroll);
                startActivity(Detail);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_device_enroll_list, menu);
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
                DeviceEnrollListActivity.this.finish();
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
