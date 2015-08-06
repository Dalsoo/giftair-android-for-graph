package giftair.co.giftair_android03;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by parkdgun on 2015-07-21.
 */
public class HealthFragment extends Fragment {

    private GridView gridView;
    private static HealthViewAdapter gridAdapter;
    private static String MeasurementFalse = null;

    public void onEvent(HealthEvent e) {
        HealthCheck();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View rootView = inflater.inflate(R.layout.healthfragment, container, false);

        MeasurementFalse = getString(R.string.MeasurementFalse);

        gridView = (GridView)rootView.findViewById(R.id.gridView);
        gridAdapter = new HealthViewAdapter(getActivity(), R.layout.health_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        ColdIndices.active = true;
        ColdIndices getColdIndices = new ColdIndices(false, 1);
        getColdIndices.start();

        return rootView;
    }

    public void HealthCheck() {
        ColdIndices.active = true;
        ColdIndices getColdIndices = new ColdIndices(false, 1);
        getColdIndices.start();
    }

    public static void ColdThreadResponse(String sCode, String sAreaNo, String sDate,String sToday,String sTomorrow,String stheDayAfterTomorrow) {

        if(sCode.length() == 0) {
            gridAdapter.getItem(0).setDate(MeasurementFalse);
            gridAdapter.getItem(0).setData(MeasurementFalse);
            gridAdapter.notifyDataSetChanged();
        }else {
            String Year = sDate.substring(0, 4);
            String Month = sDate.substring(4, 6);
            String Day = sDate.substring(6, 8);

            gridAdapter.getItem(0).setDate(Year + " / " + Month + " / " + Day);
            gridAdapter.getItem(0).setData(sToday);
            gridAdapter.notifyDataSetChanged();

            PollenWeedsIndices.active = true;
            PollenWeedsIndices getPollenWeedsIndices = new PollenWeedsIndices(false, 1);
            getPollenWeedsIndices.start();
        }

        ColdIndices.active = false;
        ColdIndices.interrupted();
    }

    public static void PollenWeedsThreadResponse(String sCode, String sAreaNo, String sDate,String sToday,String sTomorrow,String stheDayAfterTomorrow) {

        if(sCode.length() == 0) {
            gridAdapter.getItem(1).setDate(MeasurementFalse);
            gridAdapter.getItem(1).setData(MeasurementFalse);
            gridAdapter.notifyDataSetChanged();
        }else {
            String Year = sDate.substring(0, 4);
            String Month = sDate.substring(4, 6);
            String Day = sDate.substring(6, 8);

            gridAdapter.getItem(1).setDate(Year + " / " + Month + " / " + Day);
            gridAdapter.getItem(1).setData(sToday);
            gridAdapter.notifyDataSetChanged();

            PollenPineIndices.active = true;
            PollenPineIndices getPollenPineIndices = new PollenPineIndices(false, 1);
            getPollenPineIndices.start();
        }

        ColdIndices.active = false;
        ColdIndices.interrupted();
    }

    public static void PollenPineThreadResponse(String sCode, String sAreaNo, String sDate,String sToday,String sTomorrow,String stheDayAfterTomorrow) {

        if(sCode.length() == 0) {
            gridAdapter.getItem(2).setDate(MeasurementFalse);
            gridAdapter.getItem(2).setData(MeasurementFalse);
            gridAdapter.notifyDataSetChanged();
        }else {
            String Year = sDate.substring(0, 4);
            String Month = sDate.substring(4, 6);
            String Day = sDate.substring(6, 8);

            gridAdapter.getItem(2).setDate(Year + " / " + Month + " / " + Day);
            gridAdapter.getItem(2).setData(sToday);
            gridAdapter.notifyDataSetChanged();

            PollenTreesIndices.active = true;
            PollenTreesIndices getPollenTreesIndices = new PollenTreesIndices(false, 1);
            getPollenTreesIndices.start();
        }

        ColdIndices.active = false;
        ColdIndices.interrupted();
    }

    public static void PollenTreesThreadResponse(String sCode, String sAreaNo, String sDate,String sToday,String sTomorrow,String stheDayAfterTomorrow) {

        if(sCode.length() == 0) {
            gridAdapter.getItem(3).setDate(MeasurementFalse);
            gridAdapter.getItem(3).setData(MeasurementFalse);
            gridAdapter.notifyDataSetChanged();
        }else {
            String Year = sDate.substring(0, 4);
            String Month = sDate.substring(4, 6);
            String Day = sDate.substring(6, 8);

            gridAdapter.getItem(3).setDate(Year + " / " + Month + " / " + Day);
            gridAdapter.getItem(3).setData(sToday);
            gridAdapter.notifyDataSetChanged();

            SkinDisordersIndices.active = true;
            SkinDisordersIndices getSkinDisordersIndices = new SkinDisordersIndices(false, 1);
            getSkinDisordersIndices.start();
        }

        ColdIndices.active = false;
        ColdIndices.interrupted();
    }

    public static void SkinDisordersThreadResponse(String sCode, String sAreaNo, String sDate,String sToday,String sTomorrow,String stheDayAfterTomorrow) {

        if(sCode.length() == 0) {
            gridAdapter.getItem(4).setDate(MeasurementFalse);
            gridAdapter.getItem(4).setData(MeasurementFalse);
            gridAdapter.notifyDataSetChanged();
        }else {
            String Year = sDate.substring(0, 4);
            String Month = sDate.substring(4, 6);
            String Day = sDate.substring(6, 8);

            gridAdapter.getItem(4).setDate(Year + " / " + Month + " / " + Day);
            gridAdapter.getItem(4).setData(sToday);
            gridAdapter.notifyDataSetChanged();

            StrokeIndices.active = true;
            StrokeIndices getStrokeIndices = new StrokeIndices(false, 1);
            getStrokeIndices.start();
        }

        ColdIndices.active = false;
        ColdIndices.interrupted();
    }

    public static void AsthmaLungsDiseasesThreadResponse(String sCode, String sAreaNo, String sDate,String sToday,String sTomorrow,String stheDayAfterTomorrow) {

        if(sCode.length() == 0) {
            gridAdapter.getItem(5).setDate(MeasurementFalse);
            gridAdapter.getItem(5).setData(MeasurementFalse);
            gridAdapter.notifyDataSetChanged();
        }else {
            String Year = sDate.substring(0, 4);
            String Month = sDate.substring(4, 6);
            String Day = sDate.substring(6, 8);

            gridAdapter.getItem(5).setDate(Year + " / " + Month + " / " + Day);
            gridAdapter.getItem(5).setData(sToday);
            gridAdapter.notifyDataSetChanged();
        }

        ColdIndices.active = false;
        ColdIndices.interrupted();
    }

    public static void StrokeThreadResponse(String sCode, String sAreaNo, String sDate,String sToday,String sTomorrow,String stheDayAfterTomorrow) {

        if(sCode.length() == 0) {
            gridAdapter.getItem(6).setDate(MeasurementFalse);
            gridAdapter.getItem(6).setData(MeasurementFalse);
            gridAdapter.notifyDataSetChanged();
        }else {
            String Year = sDate.substring(0, 4);
            String Month = sDate.substring(4, 6);
            String Day = sDate.substring(6, 8);

            gridAdapter.getItem(6).setDate(Year + " / " + Month + " / " + Day);
            gridAdapter.getItem(6).setData(sToday);
            gridAdapter.notifyDataSetChanged();

            AsthmaLungsDiseasesIndices.active = true;
            AsthmaLungsDiseasesIndices getAsthmaLungsDiseasesIndices = new AsthmaLungsDiseasesIndices(false, 1);
            getAsthmaLungsDiseasesIndices.start();
        }

        ColdIndices.active = false;
        ColdIndices.interrupted();
    }

    private ArrayList<HealthItem> getData() {
        final ArrayList<HealthItem> HealthItem = new ArrayList<>();
        TypedArray item = getResources().obtainTypedArray(R.array.item_ids2);
        for (int i = 0; i < item.length(); i++) {
            String titleText = item.getString(i);
            HealthItem.add(new HealthItem(titleText, "Date", "0"));
        }
        return HealthItem;
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(HealthEvent.class);
        super.onDestroy();
    }
}
