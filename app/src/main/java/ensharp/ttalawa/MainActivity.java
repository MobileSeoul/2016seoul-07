package ensharp.ttalawa;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapTapi;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ensharp.ttalawa.DBAdapter.SpotsDbAdapter;
import ensharp.ttalawa.DBAdapter.StationDbAdapter;
import ensharp.ttalawa.TourSpot.TourInfoActivity;
import ensharp.ttalawa.TourSpot.TourSpotListActivity;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        PlaceSelectionListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.InfoWindowAdapter,
        LocationListener {


    GoogleMap mGoogleMap;
    MapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private StationDbAdapter dbAdapter;
    private SpotsDbAdapter spotDbAdapter;

    HashMap markerMap;
    HashMap stationMarkerMap;
    HashMap stationRedMarkMap;
    HashMap tourSpotMarkerMap;
    HashMap neartourSpotStationMarkerMap;
    HashMap pathStationMarkerMap;
    HashMap placeDistance;
    boolean isSelected;

    ArrayList<TourSpotMarkerItem> TourSpotList;
    ArrayList<MarkerItem> BackupStationList;
    boolean showTourSpot = true;

    LinearLayout stationInfoLayout;
    LinearLayout tourSpotInfoLayout;
    LinearLayout pathInfoLayout;

    TelephonyManager telephonyManager;
    String networkoper;
    TMapTapi tmaptapi;
    Marker navigationMarker;
    private LinearLayout btn_tourspot;
    LinearLayout rentInfoLayout;
    LinearLayout rentInfoBackLayout;
    RelativeLayout alarmSettingLayout;
    Toolbar alarmSettingToolbar;

    SharedPreferences pref;

    // MMS관련 변수
    public static TextView mainTxtView;

    public static TextView overChargingView;
    public static Context mContext;
    private Switch time_switch, type_switch;
    private Button btn_five, btn_ten, btn_twenty, btn_thirty, btn_sound, btn_vib;

    RelativeLayout time_layout;
    RelativeLayout mode_layout;
    RelativeLayout rion_layout;
    private CheckBox alarm_check;

    //요청코드
    private static final int REQUEST_SELECT_PLACE = 1000; /// 검색
    public static final int REQUEST_TOURSPOT_LIST = 1001; /// 관광지 목록
    public static final int REQUEST_STATION_LIST = 1002; /// 대여소 목록
    public static String mLocationService;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    TextView fab_txt_tour;
    TextView fab_txt_rental;
    TextView tool_txt_recommend;

    private MainPathAsyncTask mainPathAsyncTask;
    private SubPathAsyncTask subPathAsyncTask;
    private PathInfoAsyncTask pathInfoAsyncTask;
    ArrayList<LatLng> routePoints;
    ArrayList<LatLng> rentRoutePoints;
    ArrayList<LatLng> returnRoutePoints;
    ArrayList<LatLng> middleOneRoutePoints;
    ArrayList<LatLng> middleTwoRoutePoints;
    ArrayList<Float> distanceList;
    ArrayList<MarkerItem> sampleList;
    ArrayList<Polyline> polylines;
    ArrayList<Marker> pathmarkers;
    private int polylineWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar_main);
        setSupportActionBar(toolbar);

        // 버전이 6.0이상인 경우 허가사용을 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // 검색 기능 코드
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setBoundsBias(BOUNDS_MOUNTAIN_VIEW);

        stationInfoLayout = (LinearLayout) findViewById(R.id.stationInfoLayout);
        tourSpotInfoLayout = (LinearLayout) findViewById(R.id.tourSpotInfoLayout);
        pathInfoLayout = (LinearLayout) findViewById(R.id.pathInfoLayout);
        alarmSettingLayout = (RelativeLayout) findViewById(R.id.alarmSettingLayout);
        alarmSettingToolbar = (Toolbar) findViewById(R.id.toolBar_alarmsetting);
        rentInfoLayout = (LinearLayout) findViewById(R.id.rentInfoLayout);
        rentInfoBackLayout = (LinearLayout) findViewById(R.id.rentInfoBackLayout);
        time_layout = (RelativeLayout) findViewById(R.id.timeLayout);
        mode_layout = (RelativeLayout) findViewById(R.id.modeLayout);

        alarmButtonSetting();
        pref = new SharedPreferences(this);
        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        //구글맵 인스턴스(지도)가 사용될 준비가 되면 this라는 콜백객체를 발생시킨다.
        mapFrag.getMapAsync(this);

        btn_tourspot = (LinearLayout) findViewById(R.id.btn_tourspot);
        btn_tourspot.setOnClickListener(btnClickListener);

        mContext = this;
        mainTxtView = (TextView) findViewById(R.id.restTimetxtView);
        overChargingView = (TextView) findViewById(R.id.overChargingView);

        alarm_check = (CheckBox) findViewById(R.id.alarm_check);
        alarm_check.setOnClickListener(checkClickListener);
        btn_five = (Button) findViewById(R.id.button_5);
        btn_five.setOnClickListener(btnClickListener);
        btn_ten = (Button) findViewById(R.id.button_10);
        btn_ten.setOnClickListener(btnClickListener);
        btn_twenty = (Button) findViewById(R.id.button_20);
        btn_twenty.setOnClickListener(btnClickListener);
        btn_thirty = (Button) findViewById(R.id.button_30);
        btn_thirty.setOnClickListener(btnClickListener);
        btn_sound = (Button) findViewById(R.id.button_sound);
        btn_sound.setOnClickListener(btnClickListener);
        btn_vib = (Button) findViewById(R.id.button_vib);
        btn_vib.setOnClickListener(btnClickListener);
        initSetting();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Typeface tf_nanumsquare_bold = Typeface.createFromAsset(getAssets(), "NanumSquareB.ttf");
        Typeface tf_nanumsquare_extrabold = Typeface.createFromAsset(getAssets(), "NanumSquareEB.ttf");
        fab_txt_tour = (TextView) findViewById(R.id.fab_txt_tourspot);
        fab_txt_rental = (TextView) findViewById(R.id.fab_txt_rentalstation);
        tool_txt_recommend = (TextView) findViewById(R.id.tool_recommend_txt);
        fab_txt_tour.setTypeface(tf_nanumsquare_bold);
        fab_txt_rental.setTypeface(tf_nanumsquare_bold);
        tool_txt_recommend.setTypeface(tf_nanumsquare_bold);
        mainTxtView.setTypeface(tf_nanumsquare_bold);

        polylines = new ArrayList<>();
        pathmarkers = new ArrayList<>();
    }

    private void removePolyline() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }

    private void removeSearchMarkers() {
        Marker marker;

        if (markerMap.containsKey("searched")) {
            marker = (Marker) markerMap.get("searched");
            marker.remove();
        }
        if (markerMap.containsKey("start")) {
            marker = (Marker) markerMap.get("start");
            marker.remove();
        }
        if (markerMap.containsKey("end")) {
            marker = (Marker) markerMap.get("end");
            marker.remove();
        }
    }

    private void changePathStationMarker(MarkerItem markerItem, boolean isPath, String mode) {
        Marker temp;

        // 경로 마커 추가 시
        if (isPath) {
            temp = (Marker) stationMarkerMap.get(markerItem.getNumber());
            if (mode.equals("rent")) {
                addMarker(temp, "rentStation");
                temp.remove();
            } else if (mode.equals("return")) {
                addMarker(temp, "returnStation");
                temp.remove();
            } else {
                if (!pathStationMarkerMap.containsKey(markerItem.getNumber())) {
                    addMarker(temp, "middleStation");
                    temp.remove();
                }
            }
            // 원래 마커 추가 시
        } else {
            for (Marker marker : pathmarkers) {
                temp = marker;
                addMarker(temp, "green");
                temp.remove();
            }
            pathStationMarkerMap.clear();
            pathmarkers.clear();
        }
    }

    private class MainPathAsyncTask extends AsyncTask<MainPathData, Void, MainPathData> {
        @Override
        protected MainPathData doInBackground(MainPathData... mainPathData) {

            try {
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return mainPathData[0];
        }

        @Override
        protected void onPostExecute(MainPathData mainPathData) {
            PolylineOptions options = new PolylineOptions().width(polylineWidth).color(Color.RED).geodesic(true);

            if (mainPathData.getMode().equals("direct")) {
                changePathStationMarker(mainPathData.getStartMarkerItem(), true, "rent");
                changePathStationMarker(mainPathData.getEndMarkerItem(), true, "return");
            } else if (mainPathData.getMode().equals("one")) {
                changePathStationMarker(mainPathData.getStartMarkerItem(), true, "rent");
                changePathStationMarker(mainPathData.getEndMarkerItem(), true, "middle");
            } else {
                changePathStationMarker(mainPathData.getStartMarkerItem(), true, "middle");
                changePathStationMarker(mainPathData.getEndMarkerItem(), true, "return");
            }

            for (int i = 0; i < mainPathData.getPathPoints().size(); i++) {
                LatLng point = mainPathData.getPathPoints().get(i);
                options.add(point);
            }
            polylines.add(mGoogleMap.addPolyline(options));
        }
    }

    private class SubPathAsyncTask extends AsyncTask<SubPathData, Void, SubPathData> {
        @Override
        protected SubPathData doInBackground(SubPathData... subPathData) {

            try {
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return subPathData[0];
        }

        @Override
        protected void onPostExecute(SubPathData subPathData) {
            PolylineOptions options = new PolylineOptions().width(polylineWidth).color(Color.BLUE).geodesic(true);

            if (subPathData.getMode().equals("rent")) {
                markerMap.put("start", mGoogleMap.addMarker(subPathData.getMarkerOptions()));
            } else {
                markerMap.put("end", mGoogleMap.addMarker(subPathData.getMarkerOptions()));
            }

            for (int i = 0; i < subPathData.getPathPoints().size(); i++) {
                LatLng point = subPathData.getPathPoints().get(i);
                options.add(point);
            }
            polylines.add(mGoogleMap.addPolyline(options));
        }
    }

    private class PathInfoAsyncTask extends AsyncTask<PathInfoData, Void, PathInfoData> {
        @Override
        protected PathInfoData doInBackground(PathInfoData... pathInfoData) {

            try {
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return pathInfoData[0];
        }

        @Override
        protected void onPostExecute(PathInfoData pathInfoData) {

            TextView distanceTxt = (TextView) findViewById(R.id.pathDistanceTxt);
            TextView timeTxt = (TextView) findViewById(R.id.pathTimeTxt);
            int hours, minutes;
            float temp;
            String convertString;
            convertString = String.format("%.1f", pathInfoData.getDistance() * 0.001);
            distanceTxt.setText(convertString + "km");
            Float time = Float.valueOf(convertString) / 15;
            if (time >= 1) {
                hours = (int) Math.floor(time);
                temp = time - (float) Math.floor(time);
                minutes = (int) Math.floor(temp * 60);
                convertString = getString(R.string.about) + " " + hours + getString(R.string.pathInfoHours) + " " + minutes + getString(R.string.minutes);
            } else {
                minutes = (int) Math.floor(time * 60);
                convertString = getString(R.string.about) + " " + minutes + getString(R.string.minutes);
            }
            timeTxt.setText(convertString);
            changePathInfoLayoutVisibility(true);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(pathInfoData.getStartPoint().getPosition().latitude, pathInfoData.getStartPoint().getPosition().longitude));
            builder.include(new LatLng(pathInfoData.getEndPoint().getPosition().latitude, pathInfoData.getEndPoint().getPosition().longitude));
            builder.include(new LatLng(pathInfoData.getRentPoint().getLatitude(), pathInfoData.getRentPoint().getLongitude()));
            builder.include(new LatLng(pathInfoData.getReturnPoint().getLatitude(), pathInfoData.getReturnPoint().getLongitude()));
            LatLngBounds bounds = builder.build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }


    private void findRentPath(MarkerOptions start, MarkerItem rentStation) {

        TMapData tmapdata = new TMapData();
        final MarkerOptions fstart = start;
        final MarkerItem frentStation = rentStation;
        TMapPoint startpoint = new TMapPoint(start.getPosition().latitude, start.getPosition().longitude);
        TMapPoint endpoint = new TMapPoint(rentStation.getLatitude(), rentStation.getLongitude());

        tmapdata.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document doc) {
                rentRoutePoints = getCoordinateArrays(doc);
                SubPathData subPathData = new SubPathData(rentRoutePoints, fstart, frentStation, "rent");
                subPathAsyncTask = new SubPathAsyncTask();
                subPathAsyncTask.execute(subPathData);
            }
        });
    }

    private void findReturnPath(MarkerOptions end, MarkerItem returnStation) {

        TMapData tmapdata = new TMapData();

        final MarkerOptions fend = end;
        final MarkerItem freturnStation = returnStation;

        TMapPoint startpoint = new TMapPoint(returnStation.getLatitude(), returnStation.getLongitude());
        TMapPoint endpoint = new TMapPoint(end.getPosition().latitude, end.getPosition().longitude);

        tmapdata.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document doc) {
                returnRoutePoints = getCoordinateArrays(doc);
                SubPathData subPathData = new SubPathData(returnRoutePoints, fend, freturnStation, "return");
                subPathAsyncTask = new SubPathAsyncTask();
                subPathAsyncTask.execute(subPathData);
            }
        });
    }

    private void findOneMiddlePath(MarkerItem rentStation, MarkerItem middleStation) {

        TMapData tmapdata = new TMapData();

        final MarkerItem fRentStation = rentStation;
        final MarkerItem fMiddleStation = middleStation;

        TMapPoint startpoint = new TMapPoint(rentStation.getLatitude(), rentStation.getLongitude());
        TMapPoint endpoint = new TMapPoint(middleStation.getLatitude(), middleStation.getLongitude());

        tmapdata.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document doc) {
                middleOneRoutePoints = getCoordinateArrays(doc);
                MainPathData mainPathData = new MainPathData(middleOneRoutePoints, fRentStation, fMiddleStation, "one");
                mainPathAsyncTask = new MainPathAsyncTask();
                mainPathAsyncTask.execute(mainPathData);
            }
        });
    }

    private void findTwoMiddlePath(MarkerItem returnStation, MarkerItem middleStation) {

        TMapData tmapdata = new TMapData();

        final MarkerItem fReturnStation = returnStation;
        final MarkerItem fMiddleStation = middleStation;

        TMapPoint startpoint = new TMapPoint(middleStation.getLatitude(), middleStation.getLongitude());
        TMapPoint endpoint = new TMapPoint(returnStation.getLatitude(), returnStation.getLongitude());

        tmapdata.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document doc) {
                middleTwoRoutePoints = getCoordinateArrays(doc);
                MainPathData mainPathData = new MainPathData(middleTwoRoutePoints, fMiddleStation, fReturnStation, "two");
                mainPathAsyncTask = new MainPathAsyncTask();
                mainPathAsyncTask.execute(mainPathData);
            }
        });
    }

    private void findPath(MarkerItem rentStation, MarkerItem returnStation, LatLng startPlace, LatLng endPlace) {

        TMapData tmapdata = new TMapData();
        final MarkerItem fRentStation = rentStation;
        final MarkerItem fReturnStation = returnStation;
        final MarkerOptions startMarkerOptions = new MarkerOptions();
        final MarkerOptions endMarkerOptions = new MarkerOptions();
        startMarkerOptions.position(new LatLng(startPlace.latitude, startPlace.longitude));
        if (Locale.getDefault().getLanguage().equals("ko")) {
            startMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("startmarker")));
        } else {
            startMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("eng_startmarker")));
        }
        endMarkerOptions.position(new LatLng(endPlace.latitude, endPlace.longitude));
        if (Locale.getDefault().getLanguage().equals("ko")) {
            endMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("endmarker")));
        } else {
            endMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("eng_endmarker")));
        }
        LatLng start = startPlace;
        LatLng end = endPlace;
        final TMapPoint startpoint = new TMapPoint(rentStation.latitude, rentStation.longitude);
        final TMapPoint endpoint = new TMapPoint(returnStation.latitude, returnStation.longitude);

        tmapdata.findPathDataAll(startpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document doc) {
                Float distance = Float.valueOf(getDistance(doc));
                MarkerItem middlePoint;
                routePoints = getCoordinateArrays(doc);
                findRentPath(startMarkerOptions, fRentStation);
                findReturnPath(endMarkerOptions, fReturnStation);
                if (distance < 15000) {
                    MainPathData mainPathData = new MainPathData(routePoints, fRentStation, fReturnStation, "direct");
                    mainPathAsyncTask = new MainPathAsyncTask();
                    mainPathAsyncTask.execute(mainPathData);
                    pathInfoAsyncTask = new PathInfoAsyncTask();
                    PathInfoData pathInfoData = new PathInfoData(distance, startMarkerOptions, endMarkerOptions, fRentStation, fReturnStation);
                    pathInfoAsyncTask.execute(pathInfoData);
                } else if (distance >= 15000) {
                    middlePoint = sortingStationList(routePoints.get(routePoints.size() / 2));
                    findOneMiddlePath(fRentStation, middlePoint);
                    findTwoMiddlePath(fReturnStation, middlePoint);
                    pathInfoAsyncTask = new PathInfoAsyncTask();
                    PathInfoData pathInfoData = new PathInfoData(distance, startMarkerOptions, endMarkerOptions, fRentStation, fReturnStation);
                    pathInfoAsyncTask.execute(pathInfoData);
                } else {
                    pathAlertMessage("fail");
                }
            }
        });
    }

    public String getDistance(Document doc) {
        NodeList nodeList = doc.getElementsByTagName("tmap:totalDistance");
        String value = "0";

        for (int i = 0; i < nodeList.getLength(); i++) {
            value = nodeList.item(i).getFirstChild().getNodeValue();
            Log.i("tag", value);//여기변경
        }
        return value;
    }

    public ArrayList<LatLng> getCoordinateArrays(Document doc) {

        ArrayList<LatLng> oneTrack = new ArrayList<LatLng>();
        try {
            ArrayList<String> tracksString = new ArrayList<String>();
            NodeList nodeList = doc.getElementsByTagName("coordinates");

            for (int i = 0; i < nodeList.getLength(); i++) {
                String value = nodeList.item(i).getFirstChild().getNodeValue();
                tracksString.add(value);
                Log.i("tag", value);//여기변경
            }

            for (int i = 0; i < tracksString.size(); i++) {

                ArrayList<String> oneTrackString = new ArrayList<String>(Arrays.asList(tracksString.get(i).split("\\s+")));
                for (int k = 1; k < oneTrackString.size(); k++) {
                    LatLng latLng = new LatLng(Double.parseDouble(oneTrackString.get(k).split(",")[1]),
                            Double.parseDouble(oneTrackString.get(k).split(",")[0]));
                    oneTrack.add(latLng);
                    Log.i("좌표", latLng.latitude + " " + latLng.longitude);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oneTrack;
    }


    private void initSetting() {

        if (pref.getValue("alarm", "off", "alarm").equals("off")) {
            alarm_check.setChecked(false);

            time_layout.setAlpha(0.3f);
            mode_layout.setAlpha(0.3f);
            btn_sound.setClickable(false);
            btn_vib.setClickable(false);
            btn_five.setClickable(false);
            btn_ten.setClickable(false);
            btn_twenty.setClickable(false);
            btn_thirty.setClickable(false);

        } else {
            alarm_check.setChecked(true);
            btn_sound.setClickable(true);
            btn_vib.setClickable(true);
            btn_five.setClickable(true);
            btn_ten.setClickable(true);
            btn_twenty.setClickable(true);
            btn_thirty.setClickable(true);
            time_layout.setAlpha(1.0f);
            mode_layout.setAlpha(1.0f);
            if (pref.getValue("sound", "off", "alarm").equals("on")) {
                btn_sound.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                btn_sound.setSelected(true);
            }
            if (pref.getValue("vib", "off", "alarm").equals("on")) {
                btn_vib.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                btn_vib.setSelected(true);
            }
            if (pref.getValue("5", "off", "alarm").equals("on")) {
                btn_five.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                btn_five.setSelected(true);
            }
            if (pref.getValue("10", "off", "alarm").equals("on")) {
                btn_ten.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                btn_ten.setSelected(true);
            }
            if (pref.getValue("20", "off", "alarm").equals("on")) {
                btn_twenty.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                btn_twenty.setSelected(true);
            }
            if (pref.getValue("30", "off", "alarm").equals("on")) {
                btn_thirty.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                btn_thirty.setSelected(true);
            }
        }

    }

    CheckBox.OnClickListener checkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.alarm_check:
                    if (alarm_check.isChecked()) {
                        alarm_check.setChecked(true);
                        btn_sound.setClickable(true);
                        btn_vib.setClickable(true);
                        btn_five.setClickable(true);
                        btn_ten.setClickable(true);
                        btn_twenty.setClickable(true);
                        btn_thirty.setClickable(true);
                        time_layout.setAlpha(1.0f);
                        mode_layout.setAlpha(1.0f);
                        pref.putValue("alarm", "on", "alarm");
                    } else {
                        btn_sound.setClickable(false);
                        btn_vib.setClickable(false);
                        btn_five.setClickable(false);
                        btn_ten.setClickable(false);
                        btn_twenty.setClickable(false);
                        btn_thirty.setClickable(false);
                        time_layout.setAlpha(0.3f);
                        mode_layout.setAlpha(0.3f);
                        pref.putValue("alarm", "off", "alarm");
                    }
                    break;
            }
        }
    };

    Button.OnClickListener btnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btn_tourspot:
                    Intent intent = new Intent(getBaseContext(), TourSpotListActivity.class);
                    startActivityForResult(intent, REQUEST_TOURSPOT_LIST);
                    break;
                case R.id.button_5:
                    if (btn_five.isSelected()) {//isSelected
                        btn_five.setSelected(false);
                        btn_five.setBackground(getResources().getDrawable(R.drawable.setbtn_unselected));
                        pref.putValue("5", "off", "alarm");
                    } else {
                        btn_five.setSelected(true);
                        btn_five.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                        pref.putValue("5", "on", "alarm");
                    }
                    break;
                case R.id.button_10:
                    if (btn_ten.isSelected()) {//isSelected
                        btn_ten.setSelected(false);
                        btn_ten.setBackground(getResources().getDrawable(R.drawable.setbtn_unselected));
                        pref.putValue("10", "off", "alarm");
                    } else {
                        btn_ten.setSelected(true);
                        btn_ten.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                        pref.putValue("10", "on", "alarm");
                    }
                    break;
                case R.id.button_20:
                    if (btn_twenty.isSelected()) {//isSelected
                        btn_twenty.setSelected(false);
                        btn_twenty.setBackground(getResources().getDrawable(R.drawable.setbtn_unselected));
                        pref.putValue("20", "off", "alarm");
                    } else {
                        btn_twenty.setSelected(true);
                        btn_twenty.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                        pref.putValue("20", "on", "alarm");
                    }
                    break;
                case R.id.button_30:
                    if (btn_thirty.isSelected()) {//isSelected
                        btn_thirty.setSelected(false);
                        btn_thirty.setBackground(getResources().getDrawable(R.drawable.setbtn_unselected));
                        pref.putValue("30", "off", "alarm");
                    } else {
                        btn_thirty.setSelected(true);
                        btn_thirty.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                        pref.putValue("30", "on", "alarm");
                    }
                    break;
                case R.id.button_sound:
                    if (btn_sound.isSelected()) {//isSelected
                        btn_sound.setSelected(false);
                        btn_sound.setBackground(getResources().getDrawable(R.drawable.setbtn_unselected));
                        pref.putValue("sound", "off", "alarm");
                    } else {
                        btn_sound.setSelected(true);
                        btn_sound.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                        pref.putValue("sound", "on", "alarm");
                    }
                    break;
                case R.id.button_vib:
                    if (btn_vib.isSelected()) {//isSelected
                        btn_vib.setSelected(false);
                        btn_vib.setBackground(getResources().getDrawable(R.drawable.setbtn_unselected));
                        pref.putValue("vib", "off", "alarm");
                    } else {
                        btn_vib.setSelected(true);
                        btn_vib.setBackground(getResources().getDrawable(R.drawable.setbtn_selected));
                        pref.putValue("vib", "on", "alarm");
                    }
                    break;
            }
        }
    };


    //액티비티가 중지되거나 다시 시작될 때 현재위치표시를 새로고침 해주는 함수
    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setInfoWindowAdapter(this);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);

        mapButtonSetting();
        TmapAuthentication();

        markerMap = new HashMap();
        stationMarkerMap = new HashMap();
        stationRedMarkMap = new HashMap();
        isSelected = false;
        tourSpotMarkerMap = new HashMap();
        neartourSpotStationMarkerMap = new HashMap();
        pathStationMarkerMap = new HashMap();
        TourSpotList = new ArrayList();
        BackupStationList = new ArrayList();

        getStationMarkerItems();
        getTourSpotMarkerItems();

        LatLng latLng = new LatLng(Double.parseDouble(pref.getValue("위도", "37.57142486", "위치")), Double.parseDouble(pref.getValue("경도", "126.985440", "위치")));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        //구글플레이 서비스를 초기화 시킴
        // 버전이 6.0이상인 경우 허가사용을 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        // 6.0 마시멜로우 버전 이하일 경우 승인요구 없이 위치사용
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int displaywidth = metrics.widthPixels;
        polylineWidth = (int) (displaywidth * 0.022f);
    }

    //Google Play Service API로 위치정보를 처리하는 앱에서는 반드시 필요
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        mLocationService = Context.LOCATION_SERVICE;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alertGpsOn))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.i(TAG, "구글과의 연결에 실패하였습니다.");
    }

    @Override
    public void onLocationChanged(Location location) {

        //현재 위치에 대한 마커를 띄워줌
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mLastLocation = location;
        //카메라를 옮김
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        // 위치 업데이트를 중지
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        //위치 사용 권한 체크(사용 권한이 없을 경우 -1)
        // 사용자가 권한 요청을 거부한 경우, 다시 권한을 요청해야 한다면, 조금 더 자세한 설명이 필요함.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
            // 만약 값이 true라면, 요청하는 권한에 대해 좀 더 자세히 설명 가능
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // 이 쓰레드는 사용자의 응답을 기다린다. 사용자가 설명을 확인하고, 허가를 재요청하는 경우
                // 허가를 요청하는 prompt가 띄어짐
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // 최초로 권한을 요청하는 경우(첫실행)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        //사용 권한이 이미 있을 경우
        else {
            return true;
        }
    }

    private void alarmButtonSetting() {
        Button alarmSetting = (Button) findViewById(R.id.alarmSettingBtn);
        ImageButton alarmSettingBack = (ImageButton) findViewById(R.id.alarmSettingBackBtn);

        alarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInfoLayoutVisibility(stationInfoLayout, false);
                changeInfoLayoutVisibility(tourSpotInfoLayout, false);
                changeAlarmLayoutVisibility(true);
            }
        });

        alarmSettingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAlarmLayoutVisibility(false);
            }
        });
    }

    private void changeAlarmLayoutVisibility(boolean isVisible) {

        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in);

        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_out);

        if (isVisible) {
            alarmSettingToolbar.setVisibility(View.VISIBLE);
            alarmSettingLayout.setVisibility(View.VISIBLE);
            alarmSettingLayout.startAnimation(slide_in);
            rentInfoBackLayout.setVisibility(View.VISIBLE);
            rentInfoBackLayout.startAnimation(slide_in);
        } else {
            alarmSettingToolbar.setVisibility(View.GONE);
            alarmSettingLayout.startAnimation(slide_out);
            alarmSettingLayout.setVisibility(View.GONE);
            rentInfoBackLayout.startAnimation(slide_out);
            rentInfoBackLayout.setVisibility(View.GONE);
        }
    }


    private void mapButtonSetting() {

        FloatingActionButton myLocation = (FloatingActionButton) findViewById(R.id.myLocationBtn);
        FloatingActionButton stationList = (FloatingActionButton) findViewById(R.id.stationListBtn);
        FloatingActionButton tourSpotMarker = (FloatingActionButton) findViewById(R.id.tourSpotMarkerBtn);
        FloatingActionButton zoomIn = (FloatingActionButton) findViewById(R.id.zoomInBtn);
        FloatingActionButton zoomOut = (FloatingActionButton) findViewById(R.id.zoomOutBtn);

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else {
                    Location location = mGoogleMap.getMyLocation();

                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                }
            }
        });

        stationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng center = mGoogleMap.getCameraPosition().target;
                Intent intent = new Intent(MainActivity.this, StationListActivity.class);
                intent.putExtra("location", center);
                startActivityForResult(intent, REQUEST_STATION_LIST);
            }
        });

        tourSpotMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker marker;
                if (!showTourSpot) {
                    //tourSpotMarkerMap = new HashMap();
                    for (TourSpotMarkerItem markerItem : TourSpotList) {
                        addTourSpotMarker(markerItem);
                    }
                    showTourSpot = true;
                } else {
                    for (int i = 0; i < TourSpotList.size(); i++) {
                        marker = (Marker) tourSpotMarkerMap.get("S" + TourSpotList.get(i).tourSpotName);
                        marker.remove();
                    }
                    uncheckNearStationMarker();
                    showTourSpot = false;
                }
            }
        });

        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    //권한이 없을 경우 권한사용 동의창을 띄우고, "동의", "비동의"에 대한 콜백을 받는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            //현재 위치 사용권한에 대한 콜백을 받음
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // 만약 요청창이 그냥 꺼지면 result arrays는 empty상태가 된다.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //만약 동의를 받으면 하고자 하는 코드를 심으면 됨
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                    //사용자가 비동의 했을 경우
                } else {
                    Toast.makeText(this, getString(R.string.GPSdenied), Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Marker selectedMarker;
        if (marker.equals(markerMap.get("searched"))) {
            marker.showInfoWindow();
            uncheckNearStationMarker();
        } else if (marker.equals(tourSpotMarkerMap.get("S" + marker.getTitle()))) {
            changeSelectedMarker(null);
            insertInfoLayoutContent(marker, tourSpotInfoLayout);
            changeInfoLayoutVisibility(tourSpotInfoLayout, true);
            uncheckNearStationMarker();
            marker.showInfoWindow();
            checkNearStationMarker(marker);
        } else if (marker.equals(neartourSpotStationMarkerMap.get(marker.getSnippet()))) {
            insertInfoLayoutContent(marker, stationInfoLayout);
            changeInfoLayoutVisibility(stationInfoLayout, true);
        } else if (marker.equals(pathStationMarkerMap.get(marker.getSnippet()))) {
            uncheckNearStationMarker();
            changeSelectedMarker(null);
            insertInfoLayoutContent(marker, stationInfoLayout);
            changeInfoLayoutVisibility(stationInfoLayout, true);
        } else if (marker.equals(markerMap.get("start")) || marker.equals(markerMap.get("end"))) {

        } else {
            uncheckNearStationMarker();
            changeSelectedMarker(marker);
            insertInfoLayoutContent(marker, stationInfoLayout);
            changeInfoLayoutVisibility(stationInfoLayout, true);
            selectedMarker = (Marker) stationRedMarkMap.get("selected");
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        changeSelectedMarker(null);
        uncheckNearStationMarker();
        changeInfoLayoutVisibility(stationInfoLayout, false);
        changeInfoLayoutVisibility(tourSpotInfoLayout, false);
        removePolyline();
        changePathStationMarker(null, false, "");
        removeSearchMarkers();
        changePathInfoLayoutVisibility(false);
    }


    private void insertInfoLayoutContent(Marker marker, LinearLayout view) {

        // 거치소 정보 레이아웃에 대한 정보
        ImageView stationTag = (ImageView) findViewById(R.id.stationTag);
        TextView stationName = (TextView) findViewById(R.id.stationNameTxt);
        TextView stationNumber = (TextView) findViewById(R.id.stationNumberTxt);
        TextView stationRack = (TextView) findViewById(R.id.stationRackTxt);
        TextView stationAddress = (TextView) findViewById(R.id.stationAddressTxt);
        final LinearLayout stationNavigation = (LinearLayout) findViewById(R.id.stationNavigationLayout);
        final LinearLayout stationRent = (LinearLayout) findViewById(R.id.stationRentLayout);

        // 관광명소 정보 레이아웃에 대한 정보
        ImageView tourSpotTag = (ImageView) findViewById(R.id.tourSpotTag);
        TextView tourSpotName = (TextView) findViewById(R.id.tourSpotNameTxt);
        TextView tourSpotIntro = (TextView) findViewById(R.id.tourSpotIntroTxt);
        TextView tourSpotAddress = (TextView) findViewById(R.id.tourSpotAdressTxt);
        final LinearLayout tourSpotNavigation = (LinearLayout) findViewById(R.id.tourSpotNavigationLayout);
        final LinearLayout tourSpotDetails = (LinearLayout) findViewById(R.id.tourSpotDetailsLayout);

        navigationMarker = marker;

        if (view == stationInfoLayout) {

            dbAdapter.open();
            Cursor result = dbAdapter.fetchStationByNumber(marker.getSnippet());
            result.moveToFirst();

            String addr_gu = "";
            String new_addr = "";
            String rack_count = "";
            String address = "";

            while (!result.isAfterLast()) {
                // 한국어
                if(Locale.getDefault().getLanguage().equals("ko")){
                    address = result.getString(2) + " " +  result.getString(3);
                    stationTag.setImageResource(R.drawable.station_label);
                }
                // 영어
                else {
                    address =  result.getString(10) + " " +  result.getString(9);
                    stationTag.setImageResource(R.drawable.eng_station_label);
                }
                rack_count = result.getString(7);
                Log.i("거치대 수 뭐니",rack_count);
                result.moveToNext();
            }

            stationName.setText(marker.getTitle());
            stationName.setSelected(true);

            stationNumber.setText(marker.getSnippet());
            stationRack.setText(rack_count + getString(R.string.rackCountUnit));
            stationAddress.setText(address);
            stationAddress.setSelected(true);

            stationNavigation.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            stationNavigation.setBackgroundResource(R.drawable.info_btn_pressed);
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            return true;
                        }
                        case MotionEvent.ACTION_UP: {
                            stationNavigation.setBackgroundResource(R.drawable.info_btn_unpressed);
                            TmapNavigation(navigationMarker, true);
                            return true;
                        }
                        default:
                            return false;
                    }
                }
            });

            stationRent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            stationRent.setBackgroundResource(R.drawable.info_btn_pressed);
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            return true;
                        }
                        case MotionEvent.ACTION_UP: {
                            stationRent.setBackgroundResource(R.drawable.info_btn_unpressed);
                            if (getPackageList()) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage("com.dki.spb_android");
                                startActivity(intent);
                            } else {
                                String url = "market://details?id=" + "com.dki.spb_android";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            }
                            return true;
                        }
                        default:
                            return false;
                    }
                }
            });

        } else {
            tourSpotName.setText(marker.getTitle());
            tourSpotName.setSelected(true);

            spotDbAdapter.open();
            Cursor result;
            // 한국어
            if (Locale.getDefault().getLanguage().equals("ko")) {
              result = spotDbAdapter.fetchSpotByTitle(marker.getTitle());
            }
            // 영어
            else {
                result = spotDbAdapter.fetchSpotByEngTitle(marker.getTitle());
            }

            result.moveToFirst();

            String address = "";

            while (!result.isAfterLast()) {
                // 한국어
                if(Locale.getDefault().getLanguage().equals("ko")){
                    tourSpotTag.setImageResource(R.drawable.tourspot_label);
                    address = result.getString(4);
                }
                // 영어
                else {
                    tourSpotTag.setImageResource(R.drawable.eng_tourspot_label);
                    address  = result.getString(6);
                }
                result.moveToNext();
            }

            tourSpotAddress.setText(address);
            tourSpotAddress.setSelected(true);
            tourSpotIntro.setText(getTourSpotIntro(marker.getTitle()));
            tourSpotIntro.setSelected(true);

            tourSpotNavigation.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            tourSpotNavigation.setBackgroundResource(R.drawable.info_btn_pressed);
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            return true;
                        }
                        case MotionEvent.ACTION_UP: {
                            tourSpotNavigation.setBackgroundResource(R.drawable.info_btn_unpressed);
                            TmapNavigation(navigationMarker, true);
                            return true;
                        }
                        default:
                            return false;
                    }
                }
            });

            tourSpotDetails.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            tourSpotDetails.setBackgroundResource(R.drawable.info_btn_pressed);
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            return true;
                        }
                        case MotionEvent.ACTION_UP: {
                            tourSpotDetails.setBackgroundResource(R.drawable.info_btn_unpressed);
                            Intent intent = new Intent(getBaseContext(), TourInfoActivity.class);
                            intent.putExtra("관광명소", navigationMarker.getTitle());
                            startActivityForResult(intent, REQUEST_TOURSPOT_LIST);
                            return true;
                        }
                        default:
                            return false;
                    }
                }
            });

        }
    }


    //해당앱이 설치되어있는지 확인
    public boolean getPackageList() {
        boolean isExist = false;

        PackageManager pkgMgr = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        try {
            for (int i = 0; i < mApps.size(); i++) {
                if (mApps.get(i).activityInfo.packageName.startsWith("com.dki.spb_android")) {
                    isExist = true;
                    break;
                }
            }
        } catch (Exception e) {
            isExist = false;
        }
        return isExist;
    }

    private String getTourSpotIntro(String spotName) {

        String introContent;
        if (Locale.getDefault().getLanguage().equals("ko")) {
            if (spotName.equals(getString(R.string.spot_deoksu))) {
                introContent = "연인과 함께 걷고 싶은 길";
            } else if (spotName.equals(getString(R.string.spot_myeongdong))) {
                introContent = "대한민국 예술과 패션 1번지";
            } else if (spotName.equals(getString(R.string.spot_namsangol))) {
                introContent = "전통 가옥에서 만나는 우리 문화";
            } else if (spotName.equals(getString(R.string.spot_sungnyemun))) {
                introContent = "서울의 정문, 국보 1호";
            } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
                introContent = "도심에 위치한 자연 휴식처";
            } else if (spotName.equals(getString(R.string.spot_nseoul))) {
                introContent = "도심 속 로맨틱 아일랜드";
            } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
                introContent = "조선 왕조의 법궁, 조선의 중심지";
            } else if (spotName.equals(getString(R.string.spot_gwanghwamun))) {
                introContent = "한국을 대표하는 도심 속의 광장";
            } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
                introContent = "조선왕조의 신조를 모신 유교사당";
            } else if (spotName.equals(getString(R.string.spot_bosingak))) {
                introContent = "'제야의 종' 타종행사가 열리는 곳";
            } else if (spotName.equals(getString(R.string.spot_ssamzie))) {
                introContent = "인사동 속의 새로운 인사동";
            } else if (spotName.equals(getString(R.string.spot_insadong))) {
                introContent = "도심에서 느끼는 전통의 멋";
            } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
                introContent = "조선의 왕들, 후원에 취하다";
            } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
                introContent = "조선 왕조 역사의 중요한 무대";
            } else if (spotName.equals(getString(R.string.spot_bukchon))) {
                introContent = "옛집 골목길 따라 걷는 여행";
            } else if (spotName.equals(getString(R.string.spot_heunginjimun))) {
                introContent = "국가지정 보물 1호";
            } else if (spotName.equals(getString(R.string.spot_dongfashion))) {
                introContent = "세계적인 의류패션산업의 메카";
            } else if (spotName.equals(getString(R.string.spot_daehakro))) {
                introContent = "문화 예술의 거리";
            } else if (spotName.equals(getString(R.string.spot_marronnier))) {
                introContent = "문화 예술의 터전";
            } else if (spotName.equals(getString(R.string.spot_naksan))) {
                introContent = "북악의 좌청룡에서 서울을 조망하자";
            } else if (spotName.equals(getString(R.string.spot_63square))) {
                introContent = "여의도의 복합 문화 공간";
            } else if (spotName.equals(getString(R.string.spot_yeouido))) {
                introContent = "서울의 센트럴 파크";
            } else if (spotName.equals(getString(R.string.spot_mbc))) {
                introContent = "TV 속 환상의 세계, MBC월드";
            } else if (spotName.equals(getString(R.string.spot_pyeongwha))) {
                introContent = "사람과 자연의 조화로운 만남";
            } else if (spotName.equals(getString(R.string.spot_skypark))) {
                introContent = "하늘과 맞닿은 초원";
            } else {
                introContent = "없음";
            }
        } else {
            if (spotName.equals(getString(R.string.spot_deoksu))) {
                introContent = "One of Korea’s 100 most beautiful roads";
            } else if (spotName.equals(getString(R.string.spot_myeongdong))) {
                introContent = "Korea’s best-known shopping district";
            } else if (spotName.equals(getString(R.string.spot_namsangol))) {
                introContent = "The ancient village from the Joseon period";
            } else if (spotName.equals(getString(R.string.spot_sungnyemun))) {
                introContent = "National Treasure No. 1 in Korea";
            } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
                introContent = "The home to many popular tourist attractions";
            } else if (spotName.equals(getString(R.string.spot_nseoul))) {
                introContent = "Namsan’s main tourist attraction";
            } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
                introContent = "The First royal palace built in the Joseon Dynasty";
            } else if (spotName.equals(getString(R.string.spot_gwanghwamun))) {
                introContent = "The main gate of Gyeongbokgung";
            } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
                introContent = "The Confucian shrine where the memorial tablets for the kings and queens of the Joseon Dynasty";
            } else if (spotName.equals(getString(R.string.spot_bosingak))) {
                introContent = "Bell rung to announce the time during the Joseon period";
            } else if (spotName.equals(getString(R.string.spot_ssamzie))) {
                introContent = "The Multi-cultural space where visitors can shop";
            } else if (spotName.equals(getString(R.string.spot_insadong))) {
                introContent = "The Street showcasing traditional Korean culture";
            } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
                introContent = "The most well-preserved royal residential palace from the Joseon period";
            } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
                introContent = "The palace that symbolizes the filial piety of King Seongjong of the Joseon Dynasty";
            } else if (spotName.equals(getString(R.string.spot_bukchon))) {
                introContent = "The district packed with hanoks, traditional Korean houses";
            } else if (spotName.equals(getString(R.string.spot_heunginjimun))) {
                introContent = "National Treasure No. 1";
            } else if (spotName.equals(getString(R.string.spot_dongfashion))) {
                introContent = "The fashion town with Shopping malls that offer a wide variety of fashion apparel and accessories";
            } else if (spotName.equals(getString(R.string.spot_daehakro))) {
                introContent = "The center of performance culture";
            } else if (spotName.equals(getString(R.string.spot_marronnier))) {
                introContent = "The small park planted with marronnier trees";
            } else if (spotName.equals(getString(R.string.spot_naksan))) {
                introContent = "The park that offers a spectacular view of Daehangno from its peak";
            } else if (spotName.equals(getString(R.string.spot_63square))) {
                introContent = "One of the tallest buildings in Seoul";
            } else if (spotName.equals(getString(R.string.spot_yeouido))) {
                introContent = "Take a walk among the trees";
            } else if (spotName.equals(getString(R.string.spot_mbc))) {
                introContent = "Fantastic world in TV";
            } else if (spotName.equals(getString(R.string.spot_pyeongwha))) {
                introContent = "Harmony with people and nature";
            } else if (spotName.equals(getString(R.string.spot_skypark))) {
                introContent = "A park where the sky and the meadow meet";
            } else {
                introContent = "None";
            }
        }

        return introContent;
    }

    private void TmapAuthentication() {
        tmaptapi = new TMapTapi(this);
        tmaptapi.setSKPMapAuthentication("593851f2-df66-33d7-ae97-52ef7278295f");

        tmaptapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {

            @Override
            public void SKPMapApikeySucceed() {
                Log.i("키인증", "성공");
            }

            @Override
            public void SKPMapApikeyFailed(String errorMsg) {
                Log.i("키인증", "실패");
                Toast.makeText(MainActivity.this, getString(R.string.navigationError), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void TmapNavigation(Marker marker, boolean isStation) {
        boolean isTmapApp_1 = appInstalledOrNot("com.skt.skaf.l001mtm091");
        boolean isTmapApp_2 = appInstalledOrNot("com.skt.tmap.ku");
        boolean isTmapApp_3 = appInstalledOrNot("com.skt.skaf.l001mtm092");

        if (isTmapApp_1 || isTmapApp_2 || isTmapApp_3) {
            if (isStation) {
                tmaptapi.invokeRoute(marker.getTitle() + " " + getString(R.string.station), (float) marker.getPosition().longitude, (float) marker.getPosition().latitude);
            } else {
                tmaptapi.invokeRoute(marker.getTitle(), (float) marker.getPosition().longitude, (float) marker.getPosition().latitude);
            }
        } else {
            showTmapInstallDialog();
        }
    }

    private void showTmapInstallDialog() {

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        networkoper = telephonyManager.getNetworkOperatorName();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        alertDialogBuilder
                .setMessage(getString(R.string.alertTmapInstall))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.install),
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                if (networkoper.equals("SKTelecom")) {
                                    Log.i("통신사", "skt");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://onesto.re/0000163382"));
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.skt.tmap.ku"));
                                    startActivity(intent);
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void changePathInfoLayoutVisibility(boolean isVisible) {

        if (isVisible) {
            pathInfoLayout.setVisibility(View.VISIBLE);
        } else {
            pathInfoLayout.setVisibility(View.GONE);
        }
    }

    private void changeInfoLayoutVisibility(LinearLayout view, boolean isVisible) {

        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        if (isVisible) {
            // Prepare the View for the animation
            if (stationInfoLayout.getVisibility() == View.VISIBLE || tourSpotInfoLayout.getVisibility() == View.VISIBLE) {
                if (view == stationInfoLayout) {
                    stationInfoLayout.setVisibility(View.VISIBLE);
                    tourSpotInfoLayout.setVisibility(View.GONE);
                } else {
                    stationInfoLayout.setVisibility(View.GONE);
                    tourSpotInfoLayout.setVisibility(View.VISIBLE);
                }
            } else {
                if (view == stationInfoLayout) {
                    stationInfoLayout.setVisibility(View.VISIBLE);
                    stationInfoLayout.startAnimation(slide_up);
                } else {
                    tourSpotInfoLayout.setVisibility(View.VISIBLE);
                    tourSpotInfoLayout.startAnimation(slide_up);
                }
            }
        } else {
            // Prepare the View for the animation
            if (view.getVisibility() == View.VISIBLE) {
                view.startAnimation(slide_down);
                view.setVisibility(View.GONE);
            }
        }
    }

    private void changeSelectedMarker(Marker marker) {
        Marker temp;
        // 선택했던 마커 되돌리기
        if (isSelected) {
            temp = (Marker) stationRedMarkMap.get("selected");
            addMarker(temp, "green");
            temp.remove();
            isSelected = false;
        }
        // 선택한 마커 표시
        if (marker != null) {
            temp = (Marker) stationMarkerMap.get(marker.getSnippet());
            addMarker(temp, "red");
            temp.remove();
            isSelected = true;
        }
    }

    private void uncheckNearStationMarker() {

        Marker removeMarker;

        if (BackupStationList != null) {
            for (int i = 0; i < BackupStationList.size(); i++) {
                if (!pathStationMarkerMap.containsKey(BackupStationList.get(i).getNumber())) {
                    removeMarker = (Marker) neartourSpotStationMarkerMap.get(BackupStationList.get(i).contentNum);
                    removeMarker.remove();
                    addMarker(BackupStationList.get(i), "green");
                }
            }
            BackupStationList.clear();
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        changeSelectedMarker(null);
        uncheckNearStationMarker();
        changeInfoLayoutVisibility(stationInfoLayout, false);
        changeInfoLayoutVisibility(tourSpotInfoLayout, false);
        LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        MarkerItem start, end;
        removePolyline();
        changePathStationMarker(null, false, "");
        removeSearchMarkers();
        changePathInfoLayoutVisibility(false);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            pathAlertMessage("GPS");
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(place.getName().toString());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("searchmarker")));
            markerMap.put("searched", mGoogleMap.addMarker(markerOptions));
            Marker marker = (Marker) markerMap.get("searched");
            marker.showInfoWindow();
        } else {
            Location location = mGoogleMap.getMyLocation();
            if (location != null) {
                LatLng myLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                start = sortingStationList(myLatlng);
                end = sortingStationList(latLng);
                if (!start.getNumber().equals(end.getNumber())) {
                    findPath(start, end, myLatlng, latLng);
                } else {
                    pathAlertMessage("fail");
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName().toString());
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("searchmarker")));
                    markerMap.put("searched", mGoogleMap.addMarker(markerOptions));
                    Marker marker = (Marker) markerMap.get("searched");
                    marker.showInfoWindow();
                }
            }
        }
    }

    public void pathAlertMessage(String mode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (mode.equals("GPS")) {
            builder.setMessage(getString(R.string.alertSearchGPS))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else if (mode.equals("fail")) {
            builder.setMessage(getString(R.string.alertSearchFail))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private MarkerItem sortingStationList(LatLng latLng) {

        distanceList = new ArrayList();
        placeDistance = new HashMap();
        MarkerItem markerItem;

        for (int i = 0; i < sampleList.size(); i++) {
            float[] distance = new float[2];
            Location.distanceBetween(sampleList.get(i).getLatitude(), sampleList.get(i).getLongitude(),
                    latLng.latitude, latLng.longitude, distance);
            distanceList.add(distance[0]);
            placeDistance.put(distance[0], sampleList.get(i));
        }

        Collections.sort(distanceList);
        markerItem = (MarkerItem) placeDistance.get(distanceList.get(0));
        //latLng = new LatLng(markerItem.getLatitude(), markerItem.getLongitude());

        return markerItem;
    }

    // 결과에 대한 함수
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Marker marker;
        String value;
        Log.i("결과값", "requestCode : " + requestCode + ", resultCode : " + resultCode);
        //추천관광명소activity(TourSpotListActivity)에서 돌아온 경우
        if (requestCode == REQUEST_TOURSPOT_LIST) {
            if (resultCode == 1) {
                value = data.getStringExtra("key");
                if (tourSpotMarkerMap.containsKey("S" + value)) {
                    marker = (Marker) tourSpotMarkerMap.get("S" + value);
                    changeSelectedMarker(null);
                    insertInfoLayoutContent(marker, tourSpotInfoLayout);
                    changeInfoLayoutVisibility(tourSpotInfoLayout, true);
                    uncheckNearStationMarker();
                    marker.showInfoWindow();
                    checkNearStationMarker(marker);
                    LatLng latLng = new LatLng(marker.getPosition().latitude, (marker.getPosition().longitude));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                }
            }
            //검색결과에 대한 경우
        } else if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
            // 대여소 목록리스트에서 돌아온 경우
        } else {
            if (resultCode == 1) {
                value = data.getStringExtra("key");
                if (stationMarkerMap.containsKey(value)) {
                    marker = (Marker) stationMarkerMap.get(value);
                    uncheckNearStationMarker();
                    changeSelectedMarker(marker);
                    insertInfoLayoutContent(marker, stationInfoLayout);
                    changeInfoLayoutVisibility(stationInfoLayout, true);
                    marker = (Marker) stationRedMarkMap.get("selected");
                    LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                    Log.i("일반", "일반");
                } else if (neartourSpotStationMarkerMap.containsKey(value)) {
                    marker = (Marker) neartourSpotStationMarkerMap.get(value);
                    uncheckNearStationMarker();
                    changeSelectedMarker(marker);
                    insertInfoLayoutContent(marker, stationInfoLayout);
                    changeInfoLayoutVisibility(stationInfoLayout, true);
                    marker = (Marker) stationRedMarkMap.get("selected");
                    LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                    Log.i("관광지근처", "관광지근처");
                } else {
                    marker = (Marker) stationRedMarkMap.get(value);
                    Log.i("선택됨", "선택됨");
                }

                CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
                mGoogleMap.animateCamera(center);
            } else if (resultCode == 2) {
                value = data.getStringExtra("key");
                if (stationMarkerMap.containsKey(value)) {
                    marker = (Marker) stationMarkerMap.get(value);
                    TmapNavigation(marker, true);
                } else if (neartourSpotStationMarkerMap.containsKey(value)) {
                    marker = (Marker) neartourSpotStationMarkerMap.get(value);
                    TmapNavigation(marker, true);
                } else {
                    marker = (Marker) stationRedMarkMap.get(value);
                    TmapNavigation(marker, true);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private void getStationMarkerItems() {

        this.dbAdapter = new StationDbAdapter(this);

        dbAdapter.open();
        Cursor result = dbAdapter.fetchAllStations();
        result.moveToFirst();
        String content_name = "";
        String coordinate_x = "";
        String coordinate_y = "";
        String content_num = "";

        sampleList = new ArrayList();
        while (!result.isAfterLast()) {

            content_num = result.getString(6);
            coordinate_x = result.getString(4);
            coordinate_y = result.getString(5);

            //한국어
            if (Locale.getDefault().getLanguage().equals("ko")) {
                content_name = result.getString(1);
            }
            //영어
            else {
                content_name = result.getString(8);
            }
            sampleList.add(new MarkerItem(Double.parseDouble(coordinate_x), Double.parseDouble(coordinate_y), content_name, content_num));
            result.moveToNext();
        }


        result.close();
        dbAdapter.close();

        for (MarkerItem markerItem : sampleList) {
            markerItem.setName(markerItem.getName().substring(4));
            addMarker(markerItem, "green");
        }
    }

    private void getTourSpotMarkerItems() {

        this.spotDbAdapter = new SpotsDbAdapter(this);

        //엑셀파일 데이터를 데이터베이스에 저장
        spotDbAdapter.open();
        Cursor temp = spotDbAdapter.fetchAllSpots();
        Log.w("testing!getCount!", String.valueOf(temp.getCount()));
        spotDbAdapter.close();

        spotDbAdapter.open();
        Cursor result = spotDbAdapter.fetchAllSpots();
        result.moveToFirst();
        String resultStr = "";
        while (!result.isAfterLast()) {
            String spotMapX = result.getString(1);
            String spotMapY = result.getString(2);
            String spotTitle;

            if (Locale.getDefault().getLanguage().equals("ko")) {
                spotTitle = result.getString(3);
            }
            //영어
            else {
                spotTitle = result.getString(5);
            }
            TourSpotList.add(new TourSpotMarkerItem(Double.parseDouble(spotMapX), Double.parseDouble(spotMapY), spotTitle));
            result.moveToNext();
        }

        result.close();
        spotDbAdapter.close();

        for (TourSpotMarkerItem markerItem : TourSpotList) {
            addTourSpotMarker(markerItem);
        }
    }

    private Bitmap resizeMapIcons(String iconName) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int displaywidth = metrics.widthPixels;
        int displayheight = metrics.heightPixels;
        int width, height;
        if (iconName.contains("green")) {
            width = (int) (displaywidth * 0.075f);
            height = (int) (width * 1.35f);
        } else if (iconName.contains("search")) {
            width = (int) (displaywidth * 0.093f);
            height = (int) (width * 1.25f);
        } else if (iconName.contains("red") || iconName.contains("yellow")) {
            width = (int) (displaywidth * 0.075f);
            height = (int) (width * 1.6f);
        } else if (iconName.contains("start") || iconName.contains("end")) {
            width = (int) (displaywidth * 0.11f);
            height = (int) (width * 1.22f);
        } else if (iconName.contains("eng_return")) {
            width = (int) (displaywidth * 0.145f);
            height = (int) (width * 1.5f);
        } else if (iconName.contains("rent") || iconName.contains("return") || iconName.contains("middle")) {
            width = (int) (displaywidth * 0.12f);
            height = (int) (width * 1.8f);
        } else {
            width = (int) (displaywidth * 0.08f);
            height = (int) (width * 1.6f);
        }
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, true);
        return resizedBitmap;
    }

    private void addMarker(Marker marker, String markerMode) {
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;
        String stationName = marker.getTitle();
        String stationNumber = marker.getSnippet();
        MarkerItem temp = new MarkerItem(latitude, longitude, stationName, stationNumber);
        addMarker(temp, markerMode);
        return;
    }

    private void addMarker(MarkerItem markerItem, String markerMode) {

        LatLng position = new LatLng(markerItem.getLatitude(), markerItem.getLongitude());
        String stationName = markerItem.getName();
        String stationNumber = markerItem.getNumber();
        Marker marker;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(stationName);
        markerOptions.position(position);
        markerOptions.snippet(stationNumber);

        if (markerMode.equals("red")) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("redmarker")));
            stationRedMarkMap.put("selected", mGoogleMap.addMarker(markerOptions));
        } else if (markerMode.equals("green")) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("greenmarker")));
            stationMarkerMap.put(stationNumber, mGoogleMap.addMarker(markerOptions));
        } else if (markerMode.equals("nearStation")) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("redmarker")));
            neartourSpotStationMarkerMap.put(stationNumber, mGoogleMap.addMarker(markerOptions));
        } else if (markerMode.equals("rentStation")) {
            if (Locale.getDefault().getLanguage().equals("ko")) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("rentmarker")));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("eng_rentmarker")));
            }
            marker = mGoogleMap.addMarker(markerOptions);
            pathStationMarkerMap.put(stationNumber, marker);
            pathmarkers.add(marker);
        } else if (markerMode.equals("returnStation")) {
            if (Locale.getDefault().getLanguage().equals("ko")) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("returnmarker")));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("eng_returnmarker")));
            }
            marker = mGoogleMap.addMarker(markerOptions);
            pathStationMarkerMap.put(stationNumber, marker);
            pathmarkers.add(marker);
        } else if (markerMode.equals("middleStation")) {
            if (Locale.getDefault().getLanguage().equals("ko")) {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("middlemarker")));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("eng_middlemarker")));
            }
            marker = mGoogleMap.addMarker(markerOptions);
            pathStationMarkerMap.put(stationNumber, marker);
            pathmarkers.add(marker);
        }

        return;
    }

    private void addTourSpotMarker(Marker marker) {
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;
        String tourSpotName = marker.getTitle();
        TourSpotMarkerItem temp = new TourSpotMarkerItem(latitude, longitude, tourSpotName);
        addTourSpotMarker(temp);
        return;
    }

    private void addTourSpotMarker(TourSpotMarkerItem markerItem) {
        LatLng position = new LatLng(markerItem.getLatitude(), markerItem.getLongitude());
        String tourSpotName = markerItem.gettourSpotName();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(tourSpotName);
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellowmarker")));
        tourSpotMarkerMap.put("S" + tourSpotName, mGoogleMap.addMarker(markerOptions));
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ensharp.ttalawa/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ensharp.ttalawa/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class MarkerItem {
        Double latitude;
        Double longitude;
        String contentName;
        String contentNum;


        public MarkerItem(Double latitude, Double longitude, String contentName, String contentNum) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.contentName = contentName;
            this.contentNum = contentNum;
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
    }

    public class TourSpotMarkerItem {
        Double latitude;
        Double longitude;
        String tourSpotName;

        public TourSpotMarkerItem(Double latitude, Double longitude, String tourSpotName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.tourSpotName = tourSpotName;
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

        public String gettourSpotName() {
            return tourSpotName;
        }

        public void settourSpotName(String tourSpotName) {
            this.tourSpotName = tourSpotName;
        }
    }

    public class MainPathData {
        ArrayList<LatLng> pathPoints;
        MarkerItem startMarkerItem;
        MarkerItem endMarkerItem;
        String mode;

        public MainPathData(ArrayList<LatLng> pathPoints, MarkerItem startMarkerItem, MarkerItem endMarkerItem, String mode) {
            this.pathPoints = pathPoints;
            this.startMarkerItem = startMarkerItem;
            this.endMarkerItem = endMarkerItem;
            this.mode = mode;
        }

        public ArrayList<LatLng> getPathPoints() {
            return pathPoints;
        }

        public MarkerItem getStartMarkerItem() {
            return startMarkerItem;
        }

        public MarkerItem getEndMarkerItem() {
            return endMarkerItem;
        }

        public String getMode() {
            return mode;
        }
    }

    public class SubPathData {
        ArrayList<LatLng> pathPoints;
        MarkerOptions markerOptions;
        MarkerItem markerItem;
        String mode;

        public SubPathData(ArrayList<LatLng> pathPoints, MarkerOptions markerOptions, MarkerItem markerItem, String mode) {
            this.pathPoints = pathPoints;
            this.markerOptions = markerOptions;
            this.markerItem = markerItem;
            this.mode = mode;
        }

        public ArrayList<LatLng> getPathPoints() {
            return pathPoints;
        }

        public MarkerOptions getMarkerOptions() {
            return markerOptions;
        }

        public MarkerItem getMarkerItem() {
            return markerItem;
        }

        public String getMode() {
            return mode;
        }
    }

    public class PathInfoData {
        float distance;
        MarkerOptions startPoint;
        MarkerOptions endPoint;
        MarkerItem rentPoint;
        MarkerItem returnPoint;

        public PathInfoData(float distance, MarkerOptions startPoint, MarkerOptions endPoint, MarkerItem rentPoint, MarkerItem returnPoint) {
            this.distance = distance;
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.rentPoint = rentPoint;
            this.returnPoint = returnPoint;
        }

        public float getDistance() {
            return distance;
        }

        public MarkerOptions getStartPoint() {
            return startPoint;
        }

        public MarkerOptions getEndPoint() {
            return endPoint;
        }

        public MarkerItem getRentPoint() {
            return rentPoint;
        }

        public MarkerItem getReturnPoint() {
            return returnPoint;
        }
    }


    private void checkNearStationMarker(Marker marker) {

        Marker removeMarker;

        if (Locale.getDefault().getLanguage().equals("ko")) {
            if (marker.getTitle().equals(getString(R.string.spot_deoksu))) {
                BackupStationList.add(new MarkerItem(37.5666321, 126.9774684, "서울광장 옆", "311"));
                BackupStationList.add(new MarkerItem(37.5646485, 126.9767202, "시청역 1번출구 뒤", "312"));
            } else if (marker.getTitle().equals(getString(R.string.spot_myeongdong))) {
                BackupStationList.add(new MarkerItem(37.5651703, 126.9846938, "한국외환은행 본점 앞", "321"));
                BackupStationList.add(new MarkerItem(37.5640689, 126.9863523, "명동성당 앞", "322"));
            } else if (marker.getTitle().equals(getString(R.string.spot_namsangol))) {
                BackupStationList.add(new MarkerItem(37.5625785, 126.9929071, "티마크 호텔 앞", "336"));
            } else if (marker.getTitle().equals(getString(R.string.spot_sungnyemun))) {
                BackupStationList.add(new MarkerItem(37.556828, 126.9719665, "서울역 광장 파출소 옆", "313"));
                BackupStationList.add(new MarkerItem(37.561474, 126.9810088, "신세계백화점 본점 앞", "324"));
            } else if (marker.getTitle().equals(getString(R.string.spot_namsanpark))) {
            } else if (marker.getTitle().equals(getString(R.string.spot_nseoul))) {
            } else if (marker.getTitle().equals(getString(R.string.spot_gyeongbok))) {
                BackupStationList.add(new MarkerItem(37.5759804, 126.9741132, "경복궁역 4번출구 뒤", "302"));
                BackupStationList.add(new MarkerItem(37.5790923, 126.9803112, "국립현대미술관", "314"));
            } else if (marker.getTitle().equals(getString(R.string.spot_gwanghwamun))) {
                BackupStationList.add(new MarkerItem(37.572552, 126.977427, "광화문역 2번출구 앞", "304"));
                BackupStationList.add(new MarkerItem(37.5725488, 126.9783353, "종로구청 옆", "305"));
                BackupStationList.add(new MarkerItem(37.5706864, 126.9764515, "광화문역 7번출구 앞", "306"));
            } else if (marker.getTitle().equals(getString(R.string.spot_jongmyo))) {
                BackupStationList.add(new MarkerItem(37.5775368, 126.9936516, "창덕궁 매표소 앞", "333"));
                BackupStationList.add(new MarkerItem(37.570584, 126.9918076, "종로3가역 2번출구 뒤", "334"));
                BackupStationList.add(new MarkerItem(37.5712826, 126.9974966, "세운스퀘어 앞", "338"));
            } else if (marker.getTitle().equals(getString(R.string.spot_bosingak))) {
                BackupStationList.add(new MarkerItem(37.5703991, 126.9818301, "종각역 1번출구 앞", "316"));
                BackupStationList.add(new MarkerItem(37.5685003, 126.9825137, "광교사거리 남측", "318"));
                BackupStationList.add(new MarkerItem(37.5682135, 126.9849536, "청계천 한빛광장", "330"));
            } else if (marker.getTitle().equals(getString(R.string.spot_ssamzie))) {
                BackupStationList.add(new MarkerItem(37.5758723, 126.9833154, "신한은행 안국역지점 옆", "315"));
                BackupStationList.add(new MarkerItem(37.5762768, 126.9861608, "안국역 5번출구 앞", "326"));
                BackupStationList.add(new MarkerItem(37.5736474, 126.9873623, "낙원상가 옆", "327"));
            } else if (marker.getTitle().equals(getString(R.string.spot_insadong))) {
                BackupStationList.add(new MarkerItem(37.5758723, 126.9833154, "신한은행 안국역지점 옆", "315"));
                BackupStationList.add(new MarkerItem(37.5762768, 126.9861608, "안국역 5번출구 앞", "326"));
            } else if (marker.getTitle().equals(getString(R.string.spot_changdeokgung))) {
                BackupStationList.add(new MarkerItem(37.5775368, 126.9936516, "창덕궁 매표소 앞", "333"));
                BackupStationList.add(new MarkerItem(37.5789955, 126.9964652, "창경궁 입구", "337"));
            } else if (marker.getTitle().equals(getString(R.string.spot_changgyeong))) {
                BackupStationList.add(new MarkerItem(37.5775368, 126.9936516, "창덕궁 매표소 앞", "333"));
                BackupStationList.add(new MarkerItem(37.5789955, 126.9964652, "창경궁 입구", "337"));
            } else if (marker.getTitle().equals(getString(R.string.spot_bukchon))) {
                BackupStationList.add(new MarkerItem(37.580003, 126.9849234, "가회동 주민센터 옆", "325"));
                BackupStationList.add(new MarkerItem(37.5762768, 126.9861608, "안국역 5번출구 앞", "326"));
            } else if (marker.getTitle().equals(getString(R.string.spot_heunginjimun))) {
                BackupStationList.add(new MarkerItem(37.5740319, 127.0067303, "성균관대 E하우스 앞", "344"));
                BackupStationList.add(new MarkerItem(37.5688585, 127.0100563, "맥스타일 앞", "346"));
            } else if (marker.getTitle().equals(getString(R.string.spot_dongfashion))) {
                BackupStationList.add(new MarkerItem(37.5688585, 127.0100563, "맥스타일 앞", "346"));
                BackupStationList.add(new MarkerItem(37.5653792, 127.0078744, "동대문역사문화공원역 9번출구 앞", "347"));
            } else if (marker.getTitle().equals(getString(R.string.spot_daehakro))) {
                BackupStationList.add(new MarkerItem(37.5821752, 127.0017159, "혜화역 3번출구 뒤", "341"));
                BackupStationList.add(new MarkerItem(37.5796835, 127.00248, "대학로 마로니에공원", "342"));
            } else if (marker.getTitle().equals(getString(R.string.spot_marronnier))) {
                BackupStationList.add(new MarkerItem(37.5821752, 127.0017159, "혜화역 3번출구 뒤", "341"));
                BackupStationList.add(new MarkerItem(37.5796835, 127.00248, "대학로 마로니에공원", "342"));
            } else if (marker.getTitle().equals(getString(R.string.spot_naksan))) {
                BackupStationList.add(new MarkerItem(37.5821752, 127.0017159, "혜화역 3번출구 뒤", "341"));
                BackupStationList.add(new MarkerItem(37.5796835, 127.00248, "대학로 마로니에공원", "342"));
            } else if (marker.getTitle().equals(getString(R.string.spot_63square))) {
                BackupStationList.add(new MarkerItem(37.522529, 126.9376243, "여의도초교 앞", "221"));
                BackupStationList.add(new MarkerItem(37.5190648, 126.9375564, "시범아파트버스정류장 옆", "222"));
            } else if (marker.getTitle().equals(getString(R.string.spot_yeouido))) {
                BackupStationList.add(new MarkerItem(37.5287803, 126.924613, "국민일보 앞", "202"));
                BackupStationList.add(new MarkerItem(37.52629, 126.9204017, "산업은행 앞", "205"));
                BackupStationList.add(new MarkerItem(37.5245621, 126.9178234, "KBS 앞", "206"));
                BackupStationList.add(new MarkerItem(37.5250461, 126.9240667, "신한금융투자후문 앞", "210"));
                BackupStationList.add(new MarkerItem(37.5218453, 126.9188913, "KT 앞", "213"));
                BackupStationList.add(new MarkerItem(37.5230357, 126.9208569, "금융감독원 앞", "214"));
            } else if (marker.getTitle().equals(getString(R.string.spot_mbc))) {
                BackupStationList.add(new MarkerItem(37.583542, 126.8867311, "우리금융상암센터 교차로", "404"));
                BackupStationList.add(new MarkerItem(37.5825567, 126.8856689, "DMC빌 앞", "405"));
                BackupStationList.add(new MarkerItem(37.5808287, 126.886682, "LG CNS앞", "408"));
                BackupStationList.add(new MarkerItem(37.579368, 126.8891946, "누리꿈스퀘어 옆", "409"));
                BackupStationList.add(new MarkerItem(37.5779916, 126.8914505, "KT 앞", "411"));
                BackupStationList.add(new MarkerItem(37.5781785, 126.8945011, "상암동주민센터 옆", "414"));
            } else if (marker.getTitle().equals(getString(R.string.spot_pyeongwha))) {
                BackupStationList.add(new MarkerItem(37.5715288, 126.8896663, "상암월드컵파크 3단지 후문", "413"));
                BackupStationList.add(new MarkerItem(37.5662219, 126.8961888, "서울시 공공자전거 운영센터 옆", "420"));
                BackupStationList.add(new MarkerItem(37.5659259, 126.9009017, "마포구청 앞", "421"));
            } else if (marker.getTitle().equals(getString(R.string.spot_skypark))) {
                BackupStationList.add(new MarkerItem(37.5715288, 126.8896663, "상암월드컵파크 3단지 후문", "413"));
                BackupStationList.add(new MarkerItem(37.5662219, 126.8961888, "서울시 공공자전거 운영센터 옆", "420"));
                BackupStationList.add(new MarkerItem(37.5659259, 126.9009017, "마포구청 앞", "421"));
            } else {
            }
        } else {
            if (marker.getTitle().equals(getString(R.string.spot_deoksu))) {
                BackupStationList.add(new MarkerItem(37.5666321, 126.9774684, "Beside Seoul Plaza", "311"));
                BackupStationList.add(new MarkerItem(37.5646485, 126.9767202, "Behind City Hall Station, Exit 1", "312"));
            } else if (marker.getTitle().equals(getString(R.string.spot_myeongdong))) {
                BackupStationList.add(new MarkerItem(37.5651703, 126.9846938, "In front of the Korea Exchange Bank Headquarters", "321"));
                BackupStationList.add(new MarkerItem(37.5640689, 126.9863523, "In front of Myeong-dong Cathedral", "322"));
            } else if (marker.getTitle().equals(getString(R.string.spot_namsangol))) {
                BackupStationList.add(new MarkerItem(37.5625785, 126.9929071, "In front of Tmark Hotel", "336"));
            } else if (marker.getTitle().equals(getString(R.string.spot_sungnyemun))) {
                BackupStationList.add(new MarkerItem(37.556828, 126.9719665, "Beside Seoul Plaza Police Substation", "313"));
                BackupStationList.add(new MarkerItem(37.561474, 126.9810088, "In front of the Shinsegae Department Store Headquarters", "324"));
            } else if (marker.getTitle().equals(getString(R.string.spot_namsanpark))) {
            } else if (marker.getTitle().equals(getString(R.string.spot_nseoul))) {
            } else if (marker.getTitle().equals(getString(R.string.spot_gyeongbok))) {
                BackupStationList.add(new MarkerItem(37.5759804, 126.9741132, "Behind Gyeongbokgung Station, Exit 4", "302"));
                BackupStationList.add(new MarkerItem(37.5790923, 126.9803112, "National Museum of Modern and Contemporary Art", "314"));
            } else if (marker.getTitle().equals(getString(R.string.spot_gwanghwamun))) {
                BackupStationList.add(new MarkerItem(37.572552, 126.977427, "In front of Gwanghwamun Station, Exit 1", "304"));
                BackupStationList.add(new MarkerItem(37.5725488, 126.9783353, "Beside Jongno-gu Office", "305"));
                BackupStationList.add(new MarkerItem(37.5706864, 126.9764515, "In front of Gwanghwamun Station, Exit 7", "306"));
            } else if (marker.getTitle().equals(getString(R.string.spot_jongmyo))) {
                BackupStationList.add(new MarkerItem(37.5775368, 126.9936516, "In front of Changdeokgung Palace Ticket Office", "333"));
                BackupStationList.add(new MarkerItem(37.570584, 126.9918076, "Behind Jongno 3-ga Station, Exit 2", "334"));
                BackupStationList.add(new MarkerItem(37.5712826, 126.9974966, "In front of Sewoon Square", "338"));
            } else if (marker.getTitle().equals(getString(R.string.spot_bosingak))) {
                BackupStationList.add(new MarkerItem(37.5703991, 126.9818301, "In front of Jonggak Station, Exit 1", "316"));
                BackupStationList.add(new MarkerItem(37.5685003, 126.9825137, "South side of Gwanggyo Intersection", "318"));
                BackupStationList.add(new MarkerItem(37.5682135, 126.9849536, "Cheonggyecheon Hanbit Plaza", "330"));
            } else if (marker.getTitle().equals(getString(R.string.spot_ssamzie))) {
                BackupStationList.add(new MarkerItem(37.5758723, 126.9833154, "Beside the Anguk Station Branch of Shinhan Bank", "315"));
                BackupStationList.add(new MarkerItem(37.5762768, 126.9861608, "In front of Anguk Station, Exit 5", "326"));
                BackupStationList.add(new MarkerItem(37.5736474, 126.9873623, "Beside Nakwon Shopping Center", "327"));
            } else if (marker.getTitle().equals(getString(R.string.spot_insadong))) {
                BackupStationList.add(new MarkerItem(37.5758723, 126.9833154, "Beside the Anguk Station Branch of Shinhan Bank", "315"));
                BackupStationList.add(new MarkerItem(37.5762768, 126.9861608, "In front of Anguk Station, Exit 5", "326"));
            } else if (marker.getTitle().equals(getString(R.string.spot_changdeokgung))) {
                BackupStationList.add(new MarkerItem(37.5775368, 126.9936516, "In front of Changdeokgung Palace Ticket Office", "333"));
                BackupStationList.add(new MarkerItem(37.5789955, 126.9964652, "Changgyeonggung Palace Entrance", "337"));
            } else if (marker.getTitle().equals(getString(R.string.spot_changgyeong))) {
                BackupStationList.add(new MarkerItem(37.5775368, 126.9936516, "In front of Changdeokgung Palace Ticket Office", "333"));
                BackupStationList.add(new MarkerItem(37.5789955, 126.9964652, "Changgyeonggung Palace Entrance", "337"));
            } else if (marker.getTitle().equals(getString(R.string.spot_bukchon))) {
                BackupStationList.add(new MarkerItem(37.580003, 126.9849234, "Beside Gahoe-dong Community Service Center", "325"));
                BackupStationList.add(new MarkerItem(37.5762768, 126.9861608, "In front of Anguk Station, Exit 5", "326"));
            } else if (marker.getTitle().equals(getString(R.string.spot_heunginjimun))) {
                BackupStationList.add(new MarkerItem(37.5740319, 127.0067303, "In front of Sungkyunkwan University's E House", "344"));
                BackupStationList.add(new MarkerItem(37.5688585, 127.0100563, "In front of The Maxtyle", "346"));
            } else if (marker.getTitle().equals(getString(R.string.spot_dongfashion))) {
                BackupStationList.add(new MarkerItem(37.5688585, 127.0100563, "In front of The Maxtyle", "346"));
                BackupStationList.add(new MarkerItem(37.5653792, 127.0078744, "In front of Dongdaemun History and Culture Park Station, Exit 9", "347"));
            } else if (marker.getTitle().equals(getString(R.string.spot_daehakro))) {
                BackupStationList.add(new MarkerItem(37.5821752, 127.0017159, "Behind Hyehwa Station, Exit 3", "341"));
                BackupStationList.add(new MarkerItem(37.5796835, 127.00248, "In front of Marronnier Park, Daehak-ro", "342"));
            } else if (marker.getTitle().equals(getString(R.string.spot_marronnier))) {
                BackupStationList.add(new MarkerItem(37.5821752, 127.0017159, "Behind Hyehwa Station, Exit 3", "341"));
                BackupStationList.add(new MarkerItem(37.5796835, 127.00248, "In front of Marronnier Park, Daehak-ro", "342"));
            } else if (marker.getTitle().equals(getString(R.string.spot_naksan))) {
                BackupStationList.add(new MarkerItem(37.5821752, 127.0017159, "Behind Hyehwa Station, Exit 3", "341"));
                BackupStationList.add(new MarkerItem(37.5796835, 127.00248, "In front of Marronnier Park, Daehak-ro", "342"));
            } else if (marker.getTitle().equals(getString(R.string.spot_63square))) {
                BackupStationList.add(new MarkerItem(37.522529, 126.9376243, "In front of Yeouido Elementary School", "221"));
                BackupStationList.add(new MarkerItem(37.5190648, 126.9375564, "Sibum Apt. next to the bus stop", "222"));
            } else if (marker.getTitle().equals(getString(R.string.spot_yeouido))) {
                BackupStationList.add(new MarkerItem(37.5287803, 126.924613, "In front of the Kukmin Daily Building", "202"));
                BackupStationList.add(new MarkerItem(37.52629, 126.9204017, "In front of the KDB Bank Building", "205"));
                BackupStationList.add(new MarkerItem(37.5245621, 126.9178234, "In front of the KBS Building", "206"));
                BackupStationList.add(new MarkerItem(37.5250461, 126.9240667, "In front of the Back Gate of the Shinhan Investment Corp. Building", "210"));
                BackupStationList.add(new MarkerItem(37.5218453, 126.9188913, "In front of the KT Building", "213"));
                BackupStationList.add(new MarkerItem(37.5230357, 126.9208569, "In front of the Financial Supervisory Service Building", "214"));
            } else if (marker.getTitle().equals(getString(R.string.spot_mbc))) {
                BackupStationList.add(new MarkerItem(37.583542, 126.8867311, "Woori Finance Holdings at Sangam Crossroad", "404"));
                BackupStationList.add(new MarkerItem(37.5825567, 126.8856689, "In front of DMC Ville", "405"));
                BackupStationList.add(new MarkerItem(37.5808287, 126.886682, "LG CNS앞", "408"));
                BackupStationList.add(new MarkerItem(37.579368, 126.8891946, "In front of LG CNS", "409"));
                BackupStationList.add(new MarkerItem(37.5779916, 126.8914505, "DMC Gallery", "411"));
                BackupStationList.add(new MarkerItem(37.5781785, 126.8945011, "Beside Sangam-dong Community Service Center", "414"));
            } else if (marker.getTitle().equals(getString(R.string.spot_pyeongwha))) {
                BackupStationList.add(new MarkerItem(37.5715288, 126.8896663, "Rear entrance to Sangam World Cup Park Apt. Complex #3", "413"));
                BackupStationList.add(new MarkerItem(37.5662219, 126.8961888, "Beside Seoul City Public Bicycle Operations Center", "420"));
                BackupStationList.add(new MarkerItem(37.5659259, 126.9009017, "In front of Mapo-Gu Office", "421"));
            } else if (marker.getTitle().equals(getString(R.string.spot_skypark))) {
                BackupStationList.add(new MarkerItem(37.5715288, 126.8896663, "Rear entrance to Sangam World Cup Park Apt. Complex #3", "413"));
                BackupStationList.add(new MarkerItem(37.5662219, 126.8961888, "Beside Seoul City Public Bicycle Operations Center", "420"));
                BackupStationList.add(new MarkerItem(37.5659259, 126.9009017, "In front of Mapo-Gu Office", "421"));
            } else {
            }
        }

        for (int i = 0; i < BackupStationList.size(); i++) {
            if (!pathStationMarkerMap.containsKey(BackupStationList.get(i).getNumber())) {
                removeMarker = (Marker) stationMarkerMap.get(BackupStationList.get(i).contentNum.toString());
                removeMarker.remove();
                addMarker(BackupStationList.get(i), "nearStation");
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file info_window_layout

        if (marker.equals(tourSpotMarkerMap.get("S" + marker.getTitle()))) {
            View v = getLayoutInflater().inflate(R.layout.infowindow_tourspotmarker, null);
            // Getting reference to the TextView to set latitude
            TextView tourSpotName = (TextView) v.findViewById(R.id.tourspot_name);
            tourSpotName.setText(marker.getTitle());
            Log.i("마커이름", marker.getTitle());
            return v;
        } else {
            View v = getLayoutInflater().inflate(R.layout.infowindow_tourspotmarker, null);
            // Getting reference to the TextView to set latitude
            TextView tourSpotName = (TextView) v.findViewById(R.id.tourspot_name);
            tourSpotName.setText(marker.getTitle());
            Log.i("마커이름", marker.getTitle());
            return v;
        }
    }

    public static Location getMyLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location bestLocation = null;

        while (bestLocation == null) {
            LocationManager mLocationManager = (LocationManager) mContext.getSystemService(mLocationService);

            List<String> providers = mLocationManager.getProviders(true);

            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        // }while (bestLocation!=null);
        return bestLocation;
    }

    public static boolean isOnGps() {
        final LocationManager manager = (LocationManager) mContext.getSystemService(mLocationService);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        LatLng center = mGoogleMap.getCameraPosition().target;
        if (exit) {
            pref.putValue("위도", String.valueOf(center.latitude), "위치");
            pref.putValue("경도", String.valueOf(center.longitude), "위치");
            finish(); // finish activity
        } else {
            if (stationInfoLayout.getVisibility() == View.VISIBLE || tourSpotInfoLayout.getVisibility() == View.VISIBLE) {
                changeInfoLayoutVisibility(stationInfoLayout, false);
                changeInfoLayoutVisibility(tourSpotInfoLayout, false);
            } else if (alarmSettingLayout.getVisibility() == View.VISIBLE) {
                changeAlarmLayoutVisibility(false);
            } else {
                Toast.makeText(this,getString(R.string.exit),
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }

    }
}