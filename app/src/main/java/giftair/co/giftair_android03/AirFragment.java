package giftair.co.giftair_android03;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by parkdgun on 2015-07-06.
 */
public class AirFragment extends Fragment {

    private Animation animation;

    private GridView gridView;
    private AirViewAdapter gridAdapter;
    private Button Mbutton;

    public static SharedPreferences Switchpref;
    public static SharedPreferences.Editor Switcheditor;

    public void onEvent(DataEvent e) {
        gridAdapter.getItem(0).setData(e.getFineDust());
        gridAdapter.getItem(1).setData(e.getTotalvoc());
        gridAdapter.getItem(2).setData(e.getCo2());
        gridAdapter.getItem(3).setData(e.getTem());
        gridAdapter.getItem(4).setData(e.getHum());
        gridAdapter.getItem(5).setData(e.getAtm());
        gridAdapter.getItem(6).setData(e.getBattery());
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.airfragment, container, false);

        Mbutton = (Button) rootView.findViewById(R.id.Mbutton);
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridAdapter = new AirViewAdapter(getActivity(), R.layout.air_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        //animation = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_test);
        //gridView.startAnimation(animation);

        Mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ObjectAnimator animator = ObjectAnimator.ofFloat(gridView, "rotationY", 0, 720);
                //animator.setDuration(1500);
                //animator.start();
                EventBus.getDefault().post(new MeasurementEvent());
            }
        });

        Switchpref = getActivity().getSharedPreferences("switchPref", Activity.MODE_PRIVATE);
        String switchString = Switchpref.getString("AnionSwitch", "");

        if(switchString.length() !=  0) {
            gridAdapter.getItem(7).setData(switchString);
            gridAdapter.notifyDataSetChanged();
        }

        if(gridAdapter.getItem(7).getData().equals("ON")) {
            EventBus.getDefault().post(new AnionOn());
        }else {
            EventBus.getDefault().post(new AnionOff());
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 7 :
                        if(gridAdapter.getItem(7).getData().equals("OFF")) {
                            gridAdapter.getItem(7).setData("ON");
                            gridAdapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new AnionOn());
                            EventBus.getDefault().removeStickyEvent(AnionOn.class);
                        }else {
                            gridAdapter.getItem(7).setData("OFF");
                            gridAdapter.notifyDataSetChanged();
                            EventBus.getDefault().post(new AnionOff());
                            EventBus.getDefault().removeStickyEvent(AnionOff.class);
                        }
                        break;
                }
            }
        });

        return rootView;
    }

    private ArrayList<GItem> getData() {
        final ArrayList<GItem> GItem = new ArrayList<>();
        TypedArray item = getResources().obtainTypedArray(R.array.item_ids);
        for (int i = 0; i < item.length()-1; i++) {
            String titleText = item.getString(i);
            GItem.add(new GItem(titleText, "0"));
        }
        String titleText = item.getString(7);
        GItem.add(7, new GItem(titleText, "OFF"));
        return GItem;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(DataEvent.class);
        EventBus.getDefault().removeStickyEvent(AnionOn.class);
        EventBus.getDefault().removeStickyEvent(AnionOff.class);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeStickyEvent(DataEvent.class);
        EventBus.getDefault().removeStickyEvent(AnionOn.class);
        EventBus.getDefault().removeStickyEvent(AnionOff.class);
        super.onDestroy();
    }

}
