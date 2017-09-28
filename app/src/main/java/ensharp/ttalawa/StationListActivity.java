package ensharp.ttalawa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import ensharp.ttalawa.DBAdapter.StationDbAdapter;

/**
 * Created by Semin on 2016-09-30.
 */
public class StationListActivity extends Activity implements AdapterView.OnItemClickListener {

    ArrayList<StationData> stationList;
    ArrayList<Float> distanceList;
    HashMap placeDistance;
    StationDbAdapter dbAdapter;
    ListView listview;
    StationListViewAdapter adapter;
    StationData stationData;
    LatLng currentLocation;
    TextView activityNameTxt;
    public static Context mStationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearbystationlist);

        activityNameTxt = (TextView) findViewById(R.id.activityName);
        activityNameTxt.setText(getString(R.string.stationListTitle));
        currentLocation = getIntent().getParcelableExtra("location");
        mStationContext = this;
        getStationData();
        sortingStationList();
    }

    @Override
    public void onItemClick(AdapterView parent, View v, int position, long id) {
        StationListViewAdapter.ListViewItem item = (StationListViewAdapter.ListViewItem) parent.getItemAtPosition(position);
        String stationNumber = item.getStationNumber();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("key", stationNumber);
        setResult(1, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
        super.onBackPressed();
    }

    public void btn_Back_Click(View v) {
        setResult(0);
        finish();
    }

    private void sortingStationList() {

        distanceList = new ArrayList();
        placeDistance = new HashMap();
        adapter = new StationListViewAdapter();
        listview = (ListView) findViewById(R.id.stationlistview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        for (int i = 0; i < stationList.size(); i++) {
            float[] distance = new float[2];
            Location.distanceBetween(stationList.get(i).getLatitude(), stationList.get(i).getLongitude(),
                    currentLocation.latitude, currentLocation.longitude, distance);
            distanceList.add(distance[0]);
            placeDistance.put(distance[0], stationList.get(i));
        }

        Collections.sort(distanceList);
        for (int i = 0; i < distanceList.size(); i++) {
            stationData = (StationData) placeDistance.get(distanceList.get(i));
            adapter.addItem(stationData.getNumber(), stationData.getName(), stationData.getAddress(), distanceList.get(i));
        }
    }

    private void getStationData() {

        this.dbAdapter = new StationDbAdapter(this);

        dbAdapter.open();
        Cursor result = dbAdapter.fetchAllStations();
        result.moveToFirst();
        String content_name = "";
        String content_num = "";
        String address = "";
        String coordinate_x = "";
        String coordinate_y = "";

        stationList = new ArrayList();
        while (!result.isAfterLast()) {
            content_num = result.getString(6);
            coordinate_x = result.getString(4);
            coordinate_y = result.getString(5);
            //한국어
            if (Locale.getDefault().getLanguage().equals("ko")) {
                content_name = result.getString(1);
                address = result.getString(2) + " " + result.getString(3);
            }
            // 영어
            else {
                content_name = result.getString(8);
                address = result.getString(10) + " " + result.getString(9);
            }

            stationList.add(new StationData(Double.parseDouble(coordinate_x), Double.parseDouble(coordinate_y), content_name.substring(4), content_num, address));
            result.moveToNext();
        }

        result.close();
        dbAdapter.close();
    }


    public class StationData {
        Double latitude;
        Double longitude;
        String contentAddress;
        String contentName;
        String contentNum;


        public StationData(Double latitude, Double longitude, String contentName, String contentNum, String contentAddress) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.contentName = contentName;
            this.contentNum = contentNum;
            this.contentAddress = contentAddress;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getName() {
            return contentName;
        }

        public void setName(String contentName) {
            this.contentName = contentName;
        }

        public String getNumber() {
            return contentNum;
        }

        public void setNumber(String contentNum) {
            this.contentNum = contentNum;
        }

        public String getAddress() {
            return contentAddress;
        }

        public void setAddress(String contentAddress) {
            this.contentAddress = contentAddress;
        }
    }


}
