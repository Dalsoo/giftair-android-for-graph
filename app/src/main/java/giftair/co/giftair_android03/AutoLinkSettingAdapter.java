package giftair.co.giftair_android03;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by parkdgun on 2015-07-14.
 */
public class AutoLinkSettingAdapter extends ArrayAdapter<DeviceData> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<DeviceData> list = new ArrayList<DeviceData>();

    public AutoLinkSettingAdapter(Context context, int layoutResourceId, ArrayList<DeviceData> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View row = convertView;
        DeviceDataViewHolde deviceDataViewHolde;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            deviceDataViewHolde = new DeviceDataViewHolde();
            deviceDataViewHolde.imageView = (ImageView)row.findViewById(R.id.deviceImage);
            deviceDataViewHolde.DeviceNameView = (TextView)row.findViewById(R.id.deviceName);
            deviceDataViewHolde.MacAddressView = (TextView)row.findViewById(R.id.deviceMacAddress);
            deviceDataViewHolde.AutoCheck = (Switch)row.findViewById(R.id.autoSwitch);

            deviceDataViewHolde.AutoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        Realm realm = Realm.getInstance(context);
                        realm.beginTransaction();
                        RealmResults<Database> query = realm.where(Database.class).equalTo("MacAddr", list.get(position).getMacAddress()).findAll();
                        for (int i = 0; i < query.size(); i++) {
                            query.get(i).setAutoCheck(true);
                        }
                        realm.commitTransaction();
                        realm.close();
                    }else {
                        Realm realm = Realm.getInstance(context);
                        realm.beginTransaction();
                        RealmResults<Database> query = realm.where(Database.class).equalTo("MacAddr", list.get(position).getMacAddress()).findAll();
                        for (int i = 0; i < query.size(); i++) {
                            query.get(i).setAutoCheck(false);
                        }
                        realm.commitTransaction();
                        realm.close();
                    }
                }
            });

            row.setTag(deviceDataViewHolde);
        }else {
            deviceDataViewHolde = (DeviceDataViewHolde)row.getTag();
        }
        DeviceData Data = list.get(position);
        //deviceDataViewHolde.imageView.setImageResource(Integer.parseInt(Data.getImageLocal()));
        deviceDataViewHolde.DeviceNameView.setText(Data.getDeviceName());
        deviceDataViewHolde.MacAddressView.setText(Data.getMacAddress());
        deviceDataViewHolde.AutoCheck.setChecked(Data.isAutoCheck());

        return row;
    }

    static class DeviceDataViewHolde {
        ImageView imageView;
        TextView DeviceNameView;
        TextView MacAddressView;
        Switch AutoCheck;
    }
}
