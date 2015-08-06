package giftair.co.giftair_android03;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity {

    String dust;
    String totalvoc;
    String co2;
    String tem;
    String hum;
    String atm;
    String battery;

    public static Boolean Check;
    public static String deviceAddress;

    private static String Mac;
    private static String Id;

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    public static SharedPreferences Devicepref;
    public static SharedPreferences.Editor Deviceeditor;
    private static ArrayList<String> DeviceArrayList = new ArrayList<>();

    public static DataEvent dataEvent;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageView userImage;
    public static Button Login_Button, DeviceSet;
    public static TextView MailPanel, NamePanel;
    public static ViewPager viewPager;
    private TabLayout tabLayout;

    private BLEWrap blewarp;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;

    private ArrayList<String> DeviceList = new ArrayList<>();

    private int mState = UART_PROFILE_DISCONNECTED;

    public void onEvent(MeasurementEvent e) {
        Dust();
    }

    public void onEvent(AnionOn e) {
        on();
    }

    public void onEvent(AnionOff e) {
        off();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        Login_Button = (Button) findViewById(R.id.login_button);
        DeviceSet = (Button) findViewById(R.id.DeviceSet);
        MailPanel = (TextView) findViewById(R.id.MailPan);
        NamePanel = (TextView) findViewById(R.id.NamePan);
        userImage = (ImageView) findViewById(R.id.userImage);

        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle("save_data");
            MailPanel.setText(bundle.getString("UserMail"));
            NamePanel.setText(bundle.getString("UserName"));
            deviceAddress = bundle.getString("MacAddress1");
            Mac = bundle.getString("MacAddress2");
        }

        dataEvent = new DataEvent("", "", "", "", "", "", "");

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        Id = pref.getString("ID", "");
        String Name = pref.getString("NAME", "");
        Check = pref.getBoolean("Check", false);

        Devicepref = getSharedPreferences("devicePref", Activity.MODE_PRIVATE);
        int Size = Devicepref.getInt("Status_size", 0);
        for (int i = 0; i < Size; i++) {
            DeviceArrayList.add(Devicepref.getString("Status_" + i, null));
        }
        blewarp = new BLEWrap();

        if (blewarp.BLEState() == null) {
            Toast.makeText(this, R.string.not_available, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!Id.equals("")) {
            MailPanel.setText(Id);
            NamePanel.setText(Name);
            Login_Button.setText(R.string.Logout);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);

        Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Button.getText().equals(getString(R.string.Login))) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
                    EventBus.getDefault().post(new LogoutEvent());
                    NamePanel.setText("Name");
                    MailPanel.setText("Guest");
                    Login_Button.setText(R.string.Login);
                }
            }
        });

        if (mDevice != null && mService != null) {
            DeviceSet.setText(getString(R.string.DetailInfo));
        } else {
            DeviceSet.setText(getString(R.string.DeviceConnect));
        }

        DeviceSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeviceSet.getText().equals(getString(R.string.DeviceConnect))) {
                    if (!blewarp.BLEState().isEnabled()) {
                        Log.i(TAG, "onResume - BT not enabled yet");
                        DeviceSet.setText(getString(R.string.DeviceConnect));
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    } else if (blewarp.BLEState().isEnabled()) {
                        if (DeviceArrayList.size() != 0) {

                            for (int i = 0; i < DeviceArrayList.size(); i++) {
                                Mac = DeviceArrayList.get(i).toString();
                                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(Mac);
                                mService.connect(Mac);
                            }
                            DeviceSet.setText(getString(R.string.DetailInfo));

                        } else {
                            Intent newIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                        }
                    }
                } else {
                    Intent putintent = new Intent(getApplicationContext(), DetailInfoActivity.class);

                    Realm realm = Realm.getInstance(MainActivity.this);
                    realm.beginTransaction();

                    if (Mac != null) {
                        RealmResults<Database> query = realm.where(Database.class).equalTo("MacAddr", Mac).findAll();
                        for (int i = 0; i < query.size(); i++) {
                            putintent.putExtra("DeviceName", query.get(i).getDeviceName());
                            putintent.putExtra("MacAddress", query.get(i).getMacAddr());
                            putintent.putExtra("Enroll", query.get(i).getEnrollment());
                        }
                    }

                    realm.commitTransaction();
                    realm.close();

                    startActivity(putintent);
                }
            }
        });

        service_init();

        if (mDevice != null) {
            mService.disconnect();
            DeviceSet.setText(getString(R.string.DeviceConnect));
        }

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Button.getText().equals(getString(R.string.Login))) {
                    Toast.makeText(getApplicationContext(), R.string.Loginuse, Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
                }
            }
        });
    }

    public void AllCheck() {
        Dust();
    }

    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        menuItem.getItemId();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_Air:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.nav_Health:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.nav_AirPollution:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.App_Settings:
                                //startActivity(new Intent(getApplicationContext(), AppSettingActivity.class));
                                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                                break;
                            case R.id.App_Info:
                                startActivity(new Intent(getApplicationContext(), AppInfoActivity.class));
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new AirFragment(), getString(R.string.Air));
        adapter.addFragment(new HealthFragment(), getString(R.string.HealthWeatherIndices));
        adapter.addFragment(new WeatherFragment(), getString(R.string.AirPollutionInfomation));
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(GiftairEvent.class);
        EventBus.getDefault().removeStickyEvent(DataEvent.class);
        EventBus.getDefault().removeStickyEvent(MeasurementEvent.class);
        super.onDestroy();

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService = null;
    }

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                    }
                });
            }
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {


                            int size = txValue.length;
                            StringBuffer strBuff = new StringBuffer();
                            StringBuffer chksum = new StringBuffer();
                            StringBuffer confirm = new StringBuffer();

                            for (int i = 0; i < size; i++) {
                                String str = String.format("%02X ", txValue[i]);
                                strBuff.append(str);
                                String con = String.format("%02x", txValue[i]);
                                confirm.append(con);
                                if (i != size - 1) {
                                    String chk = String.format("%02x", txValue[i]);
                                    chksum.append(chk);
                                }
                            }
                            String chk = chksum.toString();
                            String chk2 = checksumcalc(chk, 0);
                            String finalcon = confirm.toString();

                            Log.w(TAG, finalcon.substring(2, 8));

                            if (finalcon.substring(2, 6).equals("0f4e")) {
                                //showMessage("모델 이름: " + getModelName(finalcon));
                            } else if (finalcon.substring(2, 6).equals("0956")) {
                                //showMessage("버전: " + getVersion(finalcon));
                            } else if (finalcon.substring(2, 6).equals("0746")) {
                                //showMessage("배터리 상태: " + getBatteryInfo(finalcon));
                                battery = getBatteryInfo(finalcon);
                                dataEvent.setBattery(battery);

                                EventBus.getDefault().post(dataEvent);
                            } else if (finalcon.substring(2, 6).equals("0d43")) {
                                StringTokenizer token = new StringTokenizer(getTHP(finalcon), "/");
                                //Temperature.setText(token.nextToken());
                                //Humidity.setText(token.nextToken());
                                //Atmospheric.setText(token.nextToken());
                                //showMessage("온도/습도/기압: " + getTHP(finalcon));
                                tem = token.nextToken();
                                hum = token.nextToken();
                                atm = token.nextToken();
                                dataEvent.setTem(tem);
                                dataEvent.setHum(hum);
                                dataEvent.setAtm(atm);
                                battery();
                            } else if (finalcon.substring(2, 6).equals("0750")) {
                                dust = calcdustvalue(finalcon);
                                dataEvent.setFineDust(dust);
                                Totalvoc();
                            } else if (finalcon.substring(2, 6).equals("0951")) {
                                //Totalvocnumber.setText(calctvocvalue(finalcon));
                                //showMessage("Co2 값: " + calcco2value(finalcon) + " TVOC 값: " + calctvocvalue(finalcon));
                                totalvoc = calctvocvalue(finalcon);
                                co2 = calcco2value(finalcon);
                                dataEvent.setTotalvoc(totalvoc);
                                dataEvent.setCo2(co2);
                                THP();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                showMessage(getString(R.string.DeviceDisconnecting));
                mService.disconnect();
                DeviceSet.setText(getString(R.string.DeviceConnect));
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String calctvocvalue(String msg) {
        int a = 0;

        String temp1 = msg.substring(16, 17);
        String temp2 = msg.substring(17, 18);
        String temp3 = msg.substring(14, 15);
        String temp4 = msg.substring(15, 16);

        Log.i(TAG, "temp1=" + temp1);
        Log.i(TAG, "temp2=" + temp2);
        Log.i(TAG, "temp3=" + temp3);
        Log.i(TAG, "temp4=" + temp4);

        a = (int) (
                Integer.parseInt(temp1, 16) * Math.pow(16, 3) + Integer.parseInt(temp2, 16) * Math.pow(16, 2) + Integer.parseInt(temp3, 16) * Math.pow(16, 1) + Integer.parseInt(temp4, 16) * Math.pow(16, 0)
        );

        String res = Integer.toString(a);

        return res;
    }

    private String calcdustvalue(String msg) {
        Log.v(TAG, msg);

        String dust1 = msg.substring(12, 13);
        String dust2 = msg.substring(13, 14);
        String dust3 = msg.substring(10, 11);
        String dust4 = msg.substring(11, 12);

//        String dust = msg.substring(10, 18);
//        Long i = Long.parseLong(dust, 16);
//        Float f = Float.intBitsToFloat(i.intValue());

        Log.i(TAG, "dust1=" + dust1);
        Log.i(TAG, "dust2=" + dust2);
        Log.i(TAG, "dust3=" + dust3);
        Log.i(TAG, "dust4=" + dust4);

        Integer a = (int) ((Integer.parseInt(dust1, 16) * Math.pow(16, 3)
                + Integer.parseInt(dust2, 16) * Math.pow(16, 2)
                + Integer.parseInt(dust3, 16) * Math.pow(16, 1)
                + Integer.parseInt(dust4, 16) * Math.pow(16, 0)));

        return Integer.toString(a);
    }

    private String getTHP(String msg) {
        String res = "";

        Log.v(TAG, msg);

        String temp1 = msg.substring(10, 11);
        String temp2 = msg.substring(11, 12);
        String temp3 = msg.substring(12, 13);
        String temp4 = msg.substring(13, 14);

        String hum1 = msg.substring(14, 15);
        String hum2 = msg.substring(15, 16);
        String hum3 = msg.substring(16, 17);
        String hum4 = msg.substring(17, 18);
        String hum5 = msg.substring(18, 19);
        String hum6 = msg.substring(19, 20);

        String press1 = msg.substring(20, 21);
        String press2 = msg.substring(21, 22);
        String press3 = msg.substring(22, 23);
        String press4 = msg.substring(23, 24);
        String press5 = msg.substring(24, 25);
        String press6 = msg.substring(25, 26);

        Log.i(TAG, "temp1= " + temp1);
        Log.i(TAG, "temp2= " + temp2);
        Log.i(TAG, "temp3= " + temp3);
        Log.i(TAG, "temp4= " + temp4);

        Log.i(TAG, "hum1=" + hum1);
        Log.i(TAG, "hum2=" + hum2);
        Log.i(TAG, "hum3=" + hum3);
        Log.i(TAG, "hum4=" + hum4);
        Log.i(TAG, "hum5=" + hum5);
        Log.i(TAG, "hum6=" + hum6);

        Log.i(TAG, "press1= " + press1);
        Log.i(TAG, "press2= " + press2);
        Log.i(TAG, "press3= " + press3);
        Log.i(TAG, "press4= " + press4);
        Log.i(TAG, "press5= " + press5);
        Log.i(TAG, "press6= " + press6);

        double temp = (float) (
                Integer.parseInt(temp1, 16) * Math.pow(16, 3) + Integer.parseInt(temp2, 16) * Math.pow(16, 2) + Integer.parseInt(temp3, 16) * Math.pow(16, 1) + Integer.parseInt(temp4, 16) * Math.pow(16, 0)
        ) * 0.01;
        double hum = (float) (
                Integer.parseInt(hum1, 16) * Math.pow(16, 5) + Integer.parseInt(hum2, 16) * Math.pow(16, 4) + Integer.parseInt(hum3, 16) * Math.pow(16, 3) + Integer.parseInt(hum4, 16) * Math.pow(16, 2) + Integer.parseInt(hum5, 16) * Math.pow(16, 1) + Integer.parseInt(hum6, 16) * Math.pow(16, 0)
        ) / 1024;
        double press = (float) (
                Integer.parseInt(press1, 16) * Math.pow(16, 5) + Integer.parseInt(press2, 16) * Math.pow(16, 4) + Integer.parseInt(press3, 16) * Math.pow(16, 3) + Integer.parseInt(press4, 16) * Math.pow(16, 2) + Integer.parseInt(press5, 16) * Math.pow(16, 1) + Integer.parseInt(press6, 16) * Math.pow(16, 0)
        ) * 0.01;

        return res = String.format("%.2f", temp) + "/" + String.format("%.2f", hum) + "/" + String.format("%.2f", press);
    }

    private String getBatteryInfo(String msg) {
        // 3v에서 shutdown
        // 0.0022
        String res = "";

        String sub1 = msg.substring(9, 10);
        String sub2 = msg.substring(8, 9);

        Log.i(TAG, "sub1=" + sub1);
        Log.i(TAG, "sub2=" + sub2);

        int state = (int) (Integer.parseInt(sub1, 16) * Math.pow(16, 1) + Integer.parseInt(sub2, 16) * Math.pow(16, 0));
        String s_state = "";
        if (state == 0) {
            Log.i(TAG, "state = 0");
            s_state = "충전 중 아님";
        } else if (state == 1) {
            Log.i(TAG, "state = 1");
            s_state = "충전 중";
        } else if (state == 2) {
            Log.i(TAG, "state = 2");
            s_state = "충전 완료";
        }

        String info1 = msg.substring(12, 13);
        String info2 = msg.substring(13, 14);
        String info3 = msg.substring(10, 11);
        String info4 = msg.substring(11, 12);

        Log.i(TAG, "info1=" + info1);
        Log.i(TAG, "info2=" + info2);
        Log.i(TAG, "info3=" + info3);
        Log.i(TAG, "info4=" + info4);

        double info = (float) (
                Integer.parseInt(info1, 16) * Math.pow(16, 3) + Integer.parseInt(info2, 16) * Math.pow(16, 2) + Integer.parseInt(info3, 16) * Math.pow(16, 1) + Integer.parseInt(info4, 16) * Math.pow(16, 0)
        ) * 0.0022;

        res = String.format("%.2f", info);

        return res;
    }

    public String checksumcalc(String str, int flag) {
        String result = "";

        String sums = str.substring(2, str.length());
        System.out.println(sums);

        int checksumres = 0;
        for (int i = 0; i < sums.length() / 2; i++) {
            checksumres += Integer.parseInt(sums.substring(i * 2, i * 2 + 2), 16);
        }
        System.out.printf("%02X\n", checksumres);

        if (checksumres < 0x100) {
            result = String.format("%02x", checksumres);
            System.out.println("checksum : " + result);
        } else {
            result = String.format("%02x", checksumres);
            String rescarry = result.substring(0, 1);
            String resborrow = result.substring(1, 3);

            int carrysum = Integer.parseInt(rescarry, 16) + Integer.parseInt(resborrow, 16);
            result = String.format("%02x", carrysum);
            System.out.println("checksum : " + result);
        }
        if (flag == 0)
            str = str.concat(result);
        else if (flag == 1)
            str = result;
        return str;
    }

    private String calcco2value(String msg) {
        int a = 0;

        String co21 = msg.substring(12, 13);
        String co22 = msg.substring(13, 14);
        String co23 = msg.substring(10, 11);
        String co24 = msg.substring(11, 12);

        Log.i(TAG, "co21=" + co21);
        Log.i(TAG, "co22=" + co22);
        Log.i(TAG, "co23=" + co23);
        Log.i(TAG, "co24=" + co24);

        a = (int) (
                Integer.parseInt(co21, 16) * Math.pow(16, 3) + Integer.parseInt(co22, 16) * Math.pow(16, 2) + Integer.parseInt(co23, 16) * Math.pow(16, 1) + Integer.parseInt(co24, 16) * Math.pow(16, 0)
        );

        String res = Integer.toString(a);

        return res;
    }

    public void Dust() {
        String message = "020650000000";
        byte[] value;

        message = checksumcalc(message, 0);

        byte[] testv = new byte[message.length() / 2];

        for (int i = 0; i < testv.length; i++) {
            testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }
        value = testv;
        try {
            mService.writeRXCharacteristic(value);
        } catch (Exception e) {
            DeviceSet.setText(getString(R.string.DeviceConnect));
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void Totalvoc() {
        String message = "020651000000";
        byte[] value;

        message = checksumcalc(message, 0);

        byte[] testv = new byte[message.length() / 2];

        for (int i = 0; i < testv.length; i++) {
            testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }
        value = testv;
        try {
            mService.writeRXCharacteristic(value);
        } catch (Exception e) {
            DeviceSet.setText(getString(R.string.DeviceConnect));
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void THP() {
        String message = "020643000000";
        byte[] value;

        message = checksumcalc(message, 0);

        byte[] testv = new byte[message.length() / 2];

        for (int i = 0; i < testv.length; i++) {
            testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }
        value = testv;
        try {
            mService.writeRXCharacteristic(value);
        } catch (Exception e) {
            DeviceSet.setText(getString(R.string.DeviceConnect));
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void on() {
        String message = "020652010000";
        byte[] value;

        message = checksumcalc(message, 0);

        byte[] testv = new byte[message.length() / 2];

        for (int i = 0; i < testv.length; i++) {
            testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }
        value = testv;
        try {
            mService.writeRXCharacteristic(value);
            AirFragment.Switchpref = getSharedPreferences("switchPref", Activity.MODE_PRIVATE);
            AirFragment.Switcheditor = AirFragment.Switchpref.edit();
            AirFragment.Switcheditor.putString("AnionSwitch", "ON");
            AirFragment.Switcheditor.commit();
        } catch (Exception e) {
            DeviceSet.setText(getString(R.string.DeviceConnect));
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void off() {
        String message = "020652000000";
        byte[] value;
        message = checksumcalc(message, 0);

        byte[] testv = new byte[message.length() / 2];

        for (int i = 0; i < testv.length; i++) {
            testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }
        value = testv;
        try {
            mService.writeRXCharacteristic(value);
            AirFragment.Switchpref = getSharedPreferences("switchPref", Activity.MODE_PRIVATE);
            AirFragment.Switcheditor = AirFragment.Switchpref.edit();
            AirFragment.Switcheditor.putString("AnionSwitch", "OFF");
            AirFragment.Switcheditor.commit();
        } catch (Exception e) {
            DeviceSet.setText(getString(R.string.DeviceConnect));
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void battery() {
        String message = "020646000000";
        byte[] value;

        message = checksumcalc(message, 0);

        byte[] testv = new byte[message.length() / 2];

        for (int i = 0; i < testv.length; i++) {
            testv[i] = (byte) Integer.parseInt(message.substring(2 * i, 2 * i + 2), 16);
        }
        value = testv;
        try {
            mService.writeRXCharacteristic(value);
        } catch (Exception e) {
            DeviceSet.setText(getString(R.string.DeviceConnect));
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    Log.d(TAG, "...onActivityResultdevice.address = " + deviceAddress + "mserviceValue" + mService);
                    mService.connect(deviceAddress);

                    Intent dataTrans = new Intent(getApplicationContext(), DeviceEnrollActivity.class);
                    dataTrans.putExtra("Address", deviceAddress);
                    startActivity(dataTrans);
                    DeviceSet.setText(getString(R.string.DetailInfo));
                } else {
                    DeviceSet.setText(getString(R.string.DeviceConnect));
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    //Toast.makeText(this, R.string.bluetoothonString, Toast.LENGTH_SHORT).show();
                    if (DeviceArrayList.size() != 0) {

                        for (int i = 0; i < DeviceArrayList.size(); i++) {
                            Mac = DeviceArrayList.get(i).toString();
                            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(Mac);
                            mService.connect(Mac);
                        }


                        DeviceSet.setText(getString(R.string.DetailInfo));
                    } else {
                        Intent newIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                    }
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bluetoothproString, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                DeviceSet.setText(getString(R.string.DeviceConnect));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        bundle.putString("UserMail", this.MailPanel.getText().toString());
        bundle.putString("UserName", this.NamePanel.getText().toString());
        bundle.putString("MacAddress1", this.deviceAddress);
        bundle.putString("MacAddress2", this.Mac);
        outState.putBundle("save_data", bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
    }
}
