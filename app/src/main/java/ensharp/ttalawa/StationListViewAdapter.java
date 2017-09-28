package ensharp.ttalawa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.skp.Tmap.TMapTapi;

import java.util.ArrayList;

/**
 * Created by Semin on 2016-07-25.
 */
public class StationListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();
    TMapTapi tmaptapi;
    TelephonyManager telephonyManager;
    String networkoper;

    // ListViewAdapter의 생성자
    public StationListViewAdapter() {

    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final String stationNumber;

        final Context context = parent.getContext();
        final Activity myActivity = (Activity) StationListActivity.mStationContext;
        String convertString;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_nearbystation, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView numberTextView = (TextView) convertView.findViewById(R.id.placenumber);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.placename);
        TextView distanceView = (TextView) convertView.findViewById(R.id.placedistance);
        //TextView distanceUnitView = (TextView) convertView.findViewById(R.id.placedistanceunit);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        stationNumber = listViewItem.getStationNumber();
        numberTextView.setText(stationNumber);

        nameTextView.setText(listViewItem.getStationName());

        if (listViewItem.getDistance() < 1000) {
            distanceView.setText(String.valueOf(Math.round(listViewItem.getDistance())) + " " + "m");
        } else {
            convertString = String.format("%.1f", listViewItem.getDistance() * 0.001) + " " + "km";
            distanceView.setText(convertString);
        }

        Button directNavigation = (Button) convertView.findViewById(R.id.directNavigationBtn);

         //가장 가까운 대여소
        if(pos == 0) {
            directNavigation.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("key", stationNumber);
                    myActivity.setResult(2, resultIntent);
                    myActivity.finish();
                }
            });
            directNavigation.setVisibility(View.VISIBLE);
            distanceView.setTextColor(Color.parseColor("#F33F4A"));
        } // 그 외
        else {
            directNavigation.setVisibility(View.GONE);
            distanceView.setTextColor(Color.parseColor("#797d7b"));
        }

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String number, String name, String address, float distance) {
        ListViewItem item = new ListViewItem();
        item.setNumber(number);
        item.setName(name);
        item.setAddress(address);
        item.setDistance(distance);
        listViewItemList.add(item);
    }

    public class ListViewItem {

        private String stationNumber;
        private String stationName;
        private String stationAddress;
        private float placeDistance;
        private double stationLatitude;
        private double stationLongitude;

        public void setNumber(String number) {
            stationNumber = number;
        }

        public String getStationNumber() {
            return this.stationNumber;
        }

        public void setName(String name) {
            stationName = name;
        }

        public String getStationName() {
            return this.stationName;
        }

        public void setAddress(String address) {
            stationAddress = address;
        }

        public String getStationAddress() {
            return this.stationAddress;
        }

        public void setDistance(float distance) {
            placeDistance = distance;
        }

        public float getDistance() {
            return this.placeDistance;
        }
    }
}




