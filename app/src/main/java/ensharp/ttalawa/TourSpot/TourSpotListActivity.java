package ensharp.ttalawa.TourSpot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ensharp.ttalawa.DBAdapter.SpotsDbAdapter;
import ensharp.ttalawa.R;
import ensharp.ttalawa.SharedPreferences;

public class TourSpotListActivity extends ActionBarActivity implements TourSpotRecyclerAdapter.OnItemClickListener {

    public static Context mmContext;

    SpotsDbAdapter dbAdapter;
    private TextView activityNameTxt;
    private TourSpotRecyclerAdapter adapter;
    private ArrayList<String> spotList;
    SharedPreferences pref;
    RecyclerView mTimeRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    //    요청코드정의
    public static final int REQUEST_TOURSPOT_DETAILS = 1003;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourspotlist);
        activityNameTxt = (TextView) findViewById(R.id.activityName);
        activityNameTxt.setText(R.string.tour_recom);
        pref = new SharedPreferences(this);
        mTimeRecyclerView = (RecyclerView) findViewById(R.id.tourSpotRecyclerView);
        mTimeRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        mTimeRecyclerView.setLayoutManager(layoutManager);

        adapter = new TourSpotRecyclerAdapter(getDataset());
        adapter.setOnItemClickListener(this);
        mTimeRecyclerView.setAdapter(adapter);

        mmContext = this;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getBaseContext(), TourInfoActivity.class);
        intent.putExtra("관광명소", adapter.getItem(position).getSpotName());
        startActivityForResult(intent, REQUEST_TOURSPOT_DETAILS);

//        startActivityForResult(intent, REQUEST_CODE_SPOTINFO);
//        Toast.makeText(this, "다른액티비티로 "+adapter.getItem(position).getSpotName(), Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("결과값", "requestCode : " + requestCode + ", resultCode : " + resultCode);
        if (resultCode == 1) {
            String value = intent.getStringExtra("key");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("key", value);
            setResult(1, resultIntent);
            finish();
        } else {
            mTimeRecyclerView = (RecyclerView) findViewById(R.id.tourSpotRecyclerView);
            mTimeRecyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(this);
            mTimeRecyclerView.setLayoutManager(layoutManager);

            adapter = new TourSpotRecyclerAdapter(getDataset());
            adapter.setOnItemClickListener(this);
            mTimeRecyclerView.setAdapter(adapter);
        }
    }

    private ArrayList<TourData> getDataset() {

        if(Locale.getDefault().getLanguage().equals("ko")) {
            getKorDbTourSpot();
        }else{
            getEngDbTourSpot();
        }

        ArrayList<TourData> dataset = new ArrayList<>();

        for (int i = 0; i < spotList.size(); i++) {

            //인증완료
            if (pref.getValue(String.valueOf(i), false, "방문여부")) {
                if (i <= 5) {
                    dataset.add(new TourData(spotList.get(i).toString(), "도심", "방문완료"));
                } else if (i <= 14) {
                    dataset.add(new TourData(spotList.get(i).toString(), "고궁", "방문완료"));
                } else if (i <= 19) {
                    dataset.add(new TourData(spotList.get(i).toString(), "동대문&대학로", "방문완료"));
                } else if (i <= 21) {
                    dataset.add(new TourData(spotList.get(i).toString(), "여의도", "방문완료"));
                } else if (i <= 24) {
                    dataset.add(new TourData(spotList.get(i).toString(), "상암", "방문완료"));
                }
            }
            //미인증
            else if (pref.getValue(String.valueOf(i), false, "방문여부") == false) {
                if (i <= 5) {
                    dataset.add(new TourData(spotList.get(i).toString(), "도심", "미방문"));
                } else if (i <= 14) {
                    dataset.add(new TourData(spotList.get(i).toString(), "고궁", "미방문"));
                } else if (i <= 19) {
                    dataset.add(new TourData(spotList.get(i).toString(), "동대문&대학로", "미방문"));
                } else if (i <= 21) {
                    dataset.add(new TourData(spotList.get(i).toString(), "여의도", "미방문"));
                } else if (i <= 24) {
                    dataset.add(new TourData(spotList.get(i).toString(), "상암", "미방문"));
                }

            }

        }

        return dataset;
    }

    private void getKorDbTourSpot() {
        this.dbAdapter = new SpotsDbAdapter(this);

        dbAdapter.open();
        Cursor result = dbAdapter.fetchAllSpots();
        result.moveToFirst();
        String spotName = "";

        spotList = new ArrayList();
        while (!result.isAfterLast()) {
            spotName = result.getString(3);
            spotList.add(spotName);
            result.moveToNext();
        }

        result.close();
        dbAdapter.close();
    }

    private void getEngDbTourSpot() {
        this.dbAdapter = new SpotsDbAdapter(this);

        dbAdapter.open();
        Cursor result = dbAdapter.fetchAllSpots();
        result.moveToFirst();
        String spotName = "";

        spotList = new ArrayList();
        while (!result.isAfterLast()) {
            spotName = result.getString(5);
            Log.i("왜안들어오냐",result.getString(5));
            spotList.add(spotName);
            result.moveToNext();
        }

        result.close();
        dbAdapter.close();
    }


    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
    }

    public void btn_Back_Click(View v) {
        setResult(0);
        finish();
    }
}