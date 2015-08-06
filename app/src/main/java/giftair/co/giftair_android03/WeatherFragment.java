package giftair.co.giftair_android03;

import android.content.Context;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class WeatherFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GridView gridView;
    private static WeatherViewAdapter adapter;
    private Button Scan;

    private GPSInfo gps;

    private String from = "WGS84";
    private String to = "TM";
    private static String Un;
    private static String greade;
    static int stationCnt = 0;
    private static double Lat;
    private static double Lon;

    public void onEvent(WeatherEvent e) {
        WeatherCheck();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weatherfragment, container, false);

        Un = getString(R.string.UNKNOWN);
        greade = getString(R.string.Greade);

        Scan = (Button) rootView.findViewById(R.id.Select);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        adapter = new WeatherViewAdapter(getActivity(), R.layout.weather_item_layout, getData());
        gridView.setAdapter(adapter);

        gps = new GPSInfo(getActivity());

        if (gps.isGetLocation()) {
            Lat = gps.getLatitude();
            Lon = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(WeatherFragment.this)
                        .addOnConnectionFailedListener(WeatherFragment.this)
                        .addApi(LocationServices.API)
                        .build();

                mGoogleApiClient.connect();

            }
        });

        return rootView;
    }

    public void WeatherCheck() {
        gps = new GPSInfo(getActivity());

        if (gps.isGetLocation()) {
            Lat = gps.getLatitude();
            Lon = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(WeatherFragment.this)
                .addOnConnectionFailedListener(WeatherFragment.this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private ArrayList<WeatherItem> getData() {
        final ArrayList<WeatherItem> WItem = new ArrayList<>();
        TypedArray item = getResources().obtainTypedArray(R.array.item_ids3);
        for (int i = 0; i < item.length(); i++) {
            String titleText = item.getString(i);
            WItem.add(new WeatherItem(titleText, "0"));
        }
        return WItem;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //Log.d("mLastLocation",String.valueOf(mLastLocation.getLatitude()) + "," + mLastLocation.getLongitude());
        if (mLastLocation != null) {
            //totalcnt.setText(String.valueOf(mLastLocation.getLatitude()) + "," + mLastLocation.getLongitude());
            if (mLastLocation.getLatitude() == 0 && mLastLocation.getLongitude() == 0) {
                getStation(String.valueOf(Lat), String.valueOf(Lon));
            } else {
                getStation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
            }
        }
        mGoogleApiClient.disconnect();

    }

    void getStation(String yGrid, String xGrid) {

        if (xGrid != null && yGrid != null) {
            GetTransCoordThread.active = true;
            GetTransCoordThread getCoordthread = new GetTransCoordThread(false, xGrid, yGrid, from, to);        //스레드생성(UI 스레드사용시 system 뻗는다)
            getCoordthread.start();    //스레드 시작
        }
    }

    public static void getNearStation(String yGrid, String xGrid) {    //이건 측정소 정보가져올 스레드

        GetStationListThread.active = true;
        GetStationListThread getstationthread = new GetStationListThread(false, yGrid, xGrid);        //스레드생성(UI 스레드사용시 system 뻗는다)
        getstationthread.start();    //스레드 시작

    }

    public static void TransCoordThreadResponse(String x, String y) {    //대기정보 가져온 결과값
        if (!x.equals("NaN") || !y.equals("NaN")) {
            getNearStation(y, x);
        }
        GetTransCoordThread.active = false;
    }

    public static void NearStationThreadResponse(String[] sStation, String[] sAddr, String[] sTm) {    //측정소 정보를 가져온 결과
        //totalcnt.setText("가까운 측정소 :" + sStation[0] + "\r\n측정소 주소 :" +sAddr[0]+"\r\n측정소까지 거리 :"+sTm[0]+"km");
        GetFindDustThread.active = false;
        GetFindDustThread.interrupted();
        getFindDust(sStation[0]);
    }

    public static void getFindDust(String name) {    //대기정보를 가져오는 스레드

        GetFindDustThread.active = true;
        GetFindDustThread getweatherthread = new GetFindDustThread(false, name);        //스레드생성(UI 스레드사용시 system 뻗는다)
        getweatherthread.start();    //스레드 시작

    }

    public static void FindDustThreadResponse(String getCnt, String sDate, String sSo2Value, String sCoValue,
                                              String sO3Value, String sNo2Value, String sPm10Value, String sKhaiValue, String sKhaiGrade, String sSo2Grade,
                                              String sNo2Grade, String sCoGrade, String sO3Grade, String sPm10Grade) {
        stationCnt = 0;
        stationCnt = Integer.parseInt(getCnt);

        if (stationCnt == 0) {
            adapter.getItem(0).setData(Un);
            adapter.getItem(1).setData(Un);
            adapter.getItem(2).setData(Un);
            adapter.getItem(3).setData(Un);
            adapter.getItem(4).setData(Un);
            adapter.getItem(5).setData(Un);
            adapter.getItem(6).setData(Un);
            adapter.getItem(7).setData(Un);
            adapter.getItem(8).setData(Un);
            adapter.getItem(9).setData(Un);
            adapter.getItem(10).setData(Un);
            adapter.getItem(11).setData(Un);
            adapter.getItem(12).setData(Un);
            adapter.notifyDataSetChanged();
        } else {
            adapter.getItem(0).setData(sDate);
            adapter.getItem(1).setData(sSo2Value + " ppm");
            adapter.getItem(2).setData(sCoValue + " ppm");
            adapter.getItem(3).setData(sO3Value + " ppm");
            adapter.getItem(4).setData(sNo2Value + " ppm");
            adapter.getItem(5).setData(sPm10Value + " ㎍/㎥");
            adapter.getItem(6).setData(sKhaiValue);
            adapter.getItem(7).setData(sKhaiGrade + " " + greade);
            adapter.getItem(8).setData(sSo2Grade + " " + greade);
            adapter.getItem(9).setData(sCoGrade + " " + greade);
            adapter.getItem(10).setData(sO3Grade + " " + greade);
            adapter.getItem(11).setData(sNo2Grade + " " + greade);
            adapter.getItem(12).setData(sPm10Grade + " " + greade);
            adapter.notifyDataSetChanged();
        }

        GetFindDustThread.active = false;
        GetFindDustThread.interrupted();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        //EventBus.getDefault().removeStickyEvent(WeatherEvent.class);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(WeatherEvent.class);
        super.onDestroy();
    }
}
