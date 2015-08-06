package giftair.co.giftair_android03;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by parkdgun on 2015-07-14.
 */
public class DeviceEnrollListAdapter extends ArrayAdapter<DeviceData>{
    private Context context;
    private int layoutResourceId;
    private ArrayList<DeviceData> list = new ArrayList<DeviceData>();

    public DeviceEnrollListAdapter(Context context, int layoutResourceId, ArrayList<DeviceData> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.list = list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DeviceDataViewHolde deviceDataViewHolde;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            deviceDataViewHolde = new DeviceDataViewHolde();
            deviceDataViewHolde.imageView = (ImageView)row.findViewById(R.id.deviceImage);
            deviceDataViewHolde.DeviceNameView = (TextView)row.findViewById(R.id.DeviceName);
            deviceDataViewHolde.MacAddressView = (TextView)row.findViewById(R.id.MacAddress);
            row.setTag(deviceDataViewHolde);

        }else {
            deviceDataViewHolde = (DeviceDataViewHolde)row.getTag();
        }

        DeviceData Data = list.get(position);
        //deviceDataViewHolde.imageView.setImageResource(Integer.parseInt(Data.getImageLocal()));
        deviceDataViewHolde.DeviceNameView.setText(Data.getDeviceName());
        deviceDataViewHolde.MacAddressView.setText(Data.getMacAddress());

        return row;
    }

    static class DeviceDataViewHolde {
        ImageView imageView;
        TextView DeviceNameView;
        TextView MacAddressView;
    }
}
