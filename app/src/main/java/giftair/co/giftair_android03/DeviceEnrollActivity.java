package giftair.co.giftair_android03;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;


public class DeviceEnrollActivity extends AppCompatActivity {

    private Button EnrollDevice;
    private EditText DeviceName, MacAddr, Enrollment;
    private static String Macadr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_enroll);

        Intent getintent = getIntent();

        MacAddr = (EditText) findViewById(R.id.MacAddr);
        EnrollDevice = (Button) findViewById(R.id.EnrollDevice);
        DeviceName = (EditText) findViewById(R.id.deviceName);
        Enrollment = (EditText) findViewById(R.id.Enrollment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.DeviceEnroll));
        setSupportActionBar(toolbar);

        String address = getIntent().getExtras().getString("Address");
        MacAddr.setText(address);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setEnabled(false);

        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        EnrollDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Realm realm = Realm.getInstance(getApplicationContext());
                realm.beginTransaction();

                RealmResults<Database> query = realm.where(Database.class).findAll();

                for (int i = 0; i < query.size(); i++) {
                    String addr = query.get(i).getMacAddr();
                    if(addr.equals(MacAddr.getText().toString())) {
                        Macadr = addr;
                    }
                }

                realm.close();

                if(DeviceName.getText().length() > 0) {
                    if(Enrollment.getText().length() > 0) {
                        if (Macadr == null) {
                            AlertDialog.Builder AutoCheck = new AlertDialog.Builder(DeviceEnrollActivity.this);
                            AutoCheck.setTitle(R.string.AutoDC).
                                    setPositiveButton(R.string.Success, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            // 데이터베이스 호출.
                                            Realm realm = Realm.getInstance(getApplicationContext());
                                            realm.beginTransaction();

                                            Database db = realm.createObject(Database.class);
                                            db.setDeviceName(DeviceName.getText().toString());
                                            db.setMacAddr(MacAddr.getText().toString());
                                            db.setEnrollment(Enrollment.getText().toString());
                                            db.setAutoCheck(true);
                                            realm.commitTransaction();
                                            realm.close();

                                            DeviceEnrollActivity.this.finish();
                                        }
                                    }).
                                    setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            // 데이터베이스 호출.
                                            Realm realm = Realm.getInstance(getApplicationContext());
                                            realm.beginTransaction();

                                            Database db = realm.createObject(Database.class);
                                            db.setDeviceName(DeviceName.getText().toString());
                                            db.setMacAddr(MacAddr.getText().toString());
                                            db.setEnrollment(Enrollment.getText().toString());
                                            db.setAutoCheck(false);
                                            realm.commitTransaction();
                                            realm.close();
                                            dialog.cancel();
                                            DeviceEnrollActivity.this.finish();
                                        }
                                    });
                            AlertDialog dialog = AutoCheck.create();
                            dialog.show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.EnrollFalse, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), R.string.EnrollNumberInput, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), R.string.DeviceNameInput, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_device_enroll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                DeviceEnrollActivity.this.finish();
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
