package giftair.co.giftair_android03;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;


public class DetailInfoActivity extends AppCompatActivity {

    private Button Save, DeviceRelease;
    private EditText DeviceName, MacAddress, Enrollment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        DeviceName = (EditText) findViewById(R.id.deviceName);
        MacAddress = (EditText) findViewById(R.id.MacAddress);
        Enrollment = (EditText) findViewById(R.id.Enrollment);
        DeviceRelease = (Button)findViewById(R.id.DeviceRelease);

        Intent getintent = getIntent();
        if(getintent.getExtras() != null) {
            String dName = getintent.getExtras().getString("DeviceName");
            String Mac = getintent.getExtras().getString("MacAddress");
            String Enroll = getintent.getExtras().getString("Enroll");

            DeviceName.setText(dName);
            MacAddress.setText(Mac);
            Enrollment.setText(Enroll);
        }else {
            DeviceName.setText(R.string.UNKNOWN);
            MacAddress.setText(MainActivity.deviceAddress);
            Enrollment.setText(R.string.UNKNOWN);
        }

        Save = (Button) findViewById(R.id.Save);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.DetailInfo));
        setSupportActionBar(toolbar);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setEnabled(false);

        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailInfoActivity.this.finish();
            }
        });

        DeviceRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_detail_info, menu);
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
                DetailInfoActivity.this.finish();
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
