package ensharp.ttalawa.TourSpot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapTapi;

import java.util.ArrayList;
import java.util.Locale;

import ensharp.ttalawa.DBAdapter.SpotsDbAdapter;
import ensharp.ttalawa.MainActivity;
import ensharp.ttalawa.R;
import ensharp.ttalawa.SharedPreferences;

public class TourInfoTab1_Intro extends Fragment {

    private TextView nameText;
    public String spotName;
    private View inflatedView;
    private ImageView imageView;
    private Intent callIntent;
    private Intent webIntent;
    private TextView spotText;
    private TextView spotAddr;
    private RelativeLayout mapBtn;
    private RelativeLayout naviBtn;
    private RelativeLayout callBtn;
    private RelativeLayout webBtn;
    //방문체크 버튼
    private Button tourSpotVisitCheck;
    SharedPreferences pref;
    String key;
    TelephonyManager telephonyManager;
    String networkoper;
    TMapTapi tmaptapi;
    private SpotsDbAdapter spotDbAdapter;
    private ImageView tourSpotTag;

    //관광명소 소개 리스트
    private ArrayList<String> spotIntroList;
    //관광명소 주소 리스트
    private ArrayList<String> spotAddrList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.spot_tab1_intro, container, false);
        callBtn = (RelativeLayout) inflatedView.findViewById(R.id.btn_call);
        webBtn = (RelativeLayout) inflatedView.findViewById(R.id.btn_website);
        mapBtn = (RelativeLayout) inflatedView.findViewById(R.id.showmap_btn);
        naviBtn = (RelativeLayout) inflatedView.findViewById(R.id.navi_btn);

        callBtn.setOnClickListener(btnListener);
        webBtn.setOnClickListener(btnListener);
        mapBtn.setOnClickListener(btnListener);
        naviBtn.setOnClickListener(btnListener);
        tourSpotVisitCheck = (Button) inflatedView.findViewById(R.id.visitCheckBtn);
        tourSpotVisitCheck.setOnClickListener(btnListener);
        tourSpotTag = (ImageView) inflatedView.findViewById(R.id.text_spotName);
        if (Locale.getDefault().getLanguage().equals("ko")) {
            getKorIntroDbTourSpot();
            tourSpotTag.setImageResource(R.drawable.tourspot_label);
        } else {
            getEngIntroDbTourSpot();
            tourSpotTag.setImageResource(R.drawable.eng_tourspot_label);
        }

        pref = new SharedPreferences(TourInfoActivity.mContextTourInfo);
        spotName = TourInfoActivity.spotName;
        setContentView(spotName);
        TmapAuthentication();

        spotName = TourInfoActivity.spotName;
        setContentView(spotName);
        return inflatedView;
    }

    public void setTourCheckBtnImage(String key) {
        // 방문한 경우
        if (pref.getValue(key, false, "방문여부")) {
//            tourSpotVisitCheck.setBackground(getResources().getDrawable(R.drawable.visited_btn));
            //   tourSpotVisitCheck.setBackgroundColor(getResources().getColor(R.color.green_light));
        }
        // 방문 안한 경우
        else {
            // tourSpotVisitCheck.setBackgroundColor(getResources().getColor(R.color.grey_dark));
        }
    }

    //한글판 소개 디비 받아오기
    private void getKorIntroDbTourSpot() {
        this.spotDbAdapter = new SpotsDbAdapter(getContext());

        spotDbAdapter.open();
        Cursor result = spotDbAdapter.fetchAllSpots();
        result.moveToFirst();
        String spotKorIntro = "";
        String spotKorAddr = "";

        spotIntroList = new ArrayList();
        spotAddrList = new ArrayList();
        while (!result.isAfterLast()) {
            spotKorIntro = result.getString(7);
            spotKorAddr = result.getString(4);
            spotIntroList.add(spotKorIntro);
            spotAddrList.add(spotKorAddr);
            result.moveToNext();
        }

        result.close();
        spotDbAdapter.close();
    }

    //영문판 소개 디비 받아오기
    private void getEngIntroDbTourSpot() {
        this.spotDbAdapter = new SpotsDbAdapter(getContext());

        spotDbAdapter.open();
        Cursor result = spotDbAdapter.fetchAllSpots();
        result.moveToFirst();
        String spotEngIntro = "";
        String spotEngAddr = "";

        spotIntroList = new ArrayList();
        spotAddrList = new ArrayList();
        while (!result.isAfterLast()) {
            spotEngIntro = result.getString(8);
            spotEngAddr = result.getString(6);
            spotIntroList.add(spotEngIntro);
            spotAddrList.add(spotEngAddr);
            result.moveToNext();
        }

        result.close();
        spotDbAdapter.close();
    }

    public void setContentView(String spotName) {
        nameText = (TextView) inflatedView.findViewById(R.id.tab1_spotname);
        nameText.setText(spotName);
        imageView = (ImageView) inflatedView.findViewById(R.id.spot_img);
        spotText = (TextView) inflatedView.findViewById(R.id.tab1_spottext);
        spotAddr = (TextView) inflatedView.findViewById(R.id.tab1_spotaddr);
        if (spotName.equals(getResources().getString(R.string.spot_deoksu))) {
            imageView.setImageResource(R.drawable.deoksugung_img);
            spotText.setText(spotIntroList.get(0));
            spotAddr.setText(spotAddrList.get(0));
            setTourCheckBtnImage("0");
        } else if (spotName.equals(getResources().getString(R.string.spot_myeongdong))) {
            imageView.setImageResource(R.drawable.myeongdong_img);
            spotText.setText(spotIntroList.get(1));
            spotAddr.setText(spotAddrList.get(1));
            setTourCheckBtnImage("1");
        } else if (spotName.equals(getResources().getString(R.string.spot_namsangol))) {
            imageView.setImageResource(R.drawable.namsangol_img);
            spotText.setText(spotIntroList.get(2));
            spotAddr.setText(spotAddrList.get(2));
            setTourCheckBtnImage("2");
        } else if (spotName.equals(getResources().getString(R.string.spot_sungnyemun))) {
            imageView.setImageResource(R.drawable.sungryemun_img);
            spotText.setText(spotIntroList.get(3));
            spotAddr.setText(spotAddrList.get(3));
            setTourCheckBtnImage("3");
        } else if (spotName.equals(getResources().getString(R.string.spot_namsanpark))) {
            imageView.setImageResource(R.drawable.namsanpark_img);
            spotText.setText(spotIntroList.get(4));
            spotAddr.setText(spotAddrList.get(4));
            setTourCheckBtnImage("4");
        } else if (spotName.equals(getResources().getString(R.string.spot_nseoul))) {
            imageView.setImageResource(R.drawable.nseoultower_img);
            spotText.setText(spotIntroList.get(5));
            spotAddr.setText(spotAddrList.get(5));
            setTourCheckBtnImage("5");
        } else if (spotName.equals(getResources().getString(R.string.spot_gyeongbok))) {
            imageView.setImageResource(R.drawable.gyeongbokgung_img);
            spotText.setText(spotIntroList.get(6));
            spotAddr.setText(spotAddrList.get(6));
            setTourCheckBtnImage("6");
        } else if (spotName.equals(getResources().getString(R.string.spot_gwanghwamun))) {
            imageView.setImageResource(R.drawable.gwanghwamun_img);
            spotText.setText(spotIntroList.get(7));
            spotAddr.setText(spotAddrList.get(7));
            setTourCheckBtnImage("7");
        } else if (spotName.equals(getResources().getString(R.string.spot_jongmyo))) {
            imageView.setImageResource(R.drawable.jongmyo_img);
            spotText.setText(spotIntroList.get(8));
            spotAddr.setText(spotAddrList.get(8));
            setTourCheckBtnImage("8");
        } else if (spotName.equals(getResources().getString(R.string.spot_bosingak))) {
            imageView.setImageResource(R.drawable.bosingak_img);
            spotText.setText(spotIntroList.get(9));
            spotAddr.setText(spotAddrList.get(9));
            setTourCheckBtnImage("9");
        } else if (spotName.equals(getResources().getString(R.string.spot_ssamzie))) {
            imageView.setImageResource(R.drawable.samziegil_img);
            spotText.setText(spotIntroList.get(10));
            spotAddr.setText(spotAddrList.get(10));
            setTourCheckBtnImage("10");
        } else if (spotName.equals(getResources().getString(R.string.spot_insadong))) {
            imageView.setImageResource(R.drawable.insadong_img);
            spotText.setText(spotIntroList.get(11));
            spotAddr.setText(spotAddrList.get(11));
            setTourCheckBtnImage("11");
        } else if (spotName.equals(getResources().getString(R.string.spot_changdeokgung))) {
            imageView.setImageResource(R.drawable.changdeokgung_img);
            spotText.setText(spotIntroList.get(12));
            spotAddr.setText(spotAddrList.get(12));
            setTourCheckBtnImage("12");
        } else if (spotName.equals(getResources().getString(R.string.spot_changgyeong))) {
            imageView.setImageResource(R.drawable.changkyeonggung_img);
            spotText.setText(spotIntroList.get(13));
            spotAddr.setText(spotAddrList.get(13));
            setTourCheckBtnImage("13");
        } else if (spotName.equals(getResources().getString(R.string.spot_bukchon))) {
            imageView.setImageResource(R.drawable.bukchon_img);
            spotText.setText(spotIntroList.get(14));
            spotAddr.setText(spotAddrList.get(14));
            setTourCheckBtnImage("14");
        } else if (spotName.equals(getResources().getString(R.string.spot_heunginjimun))) {
            imageView.setImageResource(R.drawable.hunginjimun_img);
            spotText.setText(spotIntroList.get(15));
            spotAddr.setText(spotAddrList.get(15));
            setTourCheckBtnImage("15");
        } else if (spotName.equals(getResources().getString(R.string.spot_dongfashion))) {
            imageView.setImageResource(R.drawable.ddmfashion_img);
            spotText.setText(spotIntroList.get(16));
            spotAddr.setText(spotAddrList.get(16));
            setTourCheckBtnImage("16");
        } else if (spotName.equals(getResources().getString(R.string.spot_daehakro))) {
            imageView.setImageResource(R.drawable.daehakro_img);
            spotText.setText(spotIntroList.get(17));
            spotAddr.setText(spotAddrList.get(17));
            setTourCheckBtnImage("17");
        } else if (spotName.equals(getResources().getString(R.string.spot_marronnier))) {
            imageView.setImageResource(R.drawable.maroniae_img);
            spotText.setText(spotIntroList.get(18));
            spotAddr.setText(spotAddrList.get(18));
            setTourCheckBtnImage("18");
        } else if (spotName.equals(getResources().getString(R.string.spot_naksan))) {
            imageView.setImageResource(R.drawable.naksan_img);
            spotText.setText(spotIntroList.get(19));
            spotAddr.setText(spotAddrList.get(19));
            setTourCheckBtnImage("19");
        } else if (spotName.equals(getResources().getString(R.string.spot_63square))) {
            imageView.setImageResource(R.drawable.square_img);
            spotText.setText(spotIntroList.get(20));
            spotAddr.setText(spotAddrList.get(20));
            setTourCheckBtnImage("20");
        } else if (spotName.equals(getResources().getString(R.string.spot_yeouido))) {
            imageView.setImageResource(R.drawable.yeouido_img);
            spotText.setText(spotIntroList.get(21));
            spotAddr.setText(spotAddrList.get(21));
            setTourCheckBtnImage("21");
        } else if (spotName.equals(getResources().getString(R.string.spot_mbc))) {
            imageView.setImageResource(R.drawable.mbcpark_img);
            spotText.setText(spotIntroList.get(22));
            spotAddr.setText(spotAddrList.get(22));
            setTourCheckBtnImage("22");
        } else if (spotName.equals(getResources().getString(R.string.spot_pyeongwha))) {
            imageView.setImageResource(R.drawable.pyeonghwapark_img);
            spotText.setText(spotIntroList.get(23));
            spotAddr.setText(spotAddrList.get(23));
            setTourCheckBtnImage("23");
        } else if (spotName.equals(getResources().getString(R.string.spot_skypark))) {
            imageView.setImageResource(R.drawable.hanulpark_img);
            spotText.setText(spotIntroList.get(24));
            spotAddr.setText(spotAddrList.get(24));
            setTourCheckBtnImage("24");
        }
    }

    //case에 따른 전화 연결 / 인터넷 연결 / 맵 연결 / 네비 연결
    Button.OnClickListener btnListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_call:
                    if (spotName.equals(getString(R.string.spot_deoksu))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-771-9951"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_myeongdong))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-778-0333"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_namsangol))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-2261-0500"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_sungnyemun))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:042-481-4650"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-3783-5900"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_nseoul))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-3455-9277"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-3700-3900"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_gwanghwamun))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-3700-3901"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-3672-4332"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_bosingak))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-2148-1114"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_ssamzie))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-736-0088"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_insadong))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-737-7890"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-3668-2300"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-762-4868"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_bukchon))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-2133-5580"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_heunginjimun))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-778-0333"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_dongfashion))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-2278-0500"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_daehakro))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-2148-1812"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_marronnier))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-2148-4158"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_naksan))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-2148-2842"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_63square))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-789-5663"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_yeouido))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-2670-3758"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_mbc))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-789-3705"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_pyeongwha))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-300-5501"));
                        startActivity(callIntent);
                    } else if (spotName.equals(getString(R.string.spot_skypark))) {
                        callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:02-300-5502"));
                        startActivity(callIntent);
                    }
                    break;

                case R.id.btn_website:
                    if (spotName.equals(getString(R.string.spot_deoksu))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.deoksugung.go.kr/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_myeongdong))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/MyeongdongTouristInformationCenter/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_namsangol))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hanokmaeul.or.kr/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_sungnyemun))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?cid=128162/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://parks.seoul.go.kr/namsan/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_nseoul))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nseoultower.com/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.royalpalace.go.kr/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_gwanghwamun))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://terms.naver.com/entry.nhn?docId=1065227&cid=40942&categoryId=33076/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jm.cha.go.kr/n_jm/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_bosingak))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tour.jongno.go.kr/tourMain.do/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_ssamzie))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ssamzigil.co.kr/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_insadong))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hiinsa.com/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cdg.go.kr:9901/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cgg.cha.go.kr/n_cgg/index.html/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_bukchon))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://hanok.seoul.go.kr/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_heunginjimun))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://korean.visitseoul.net/attractions/%ED%9D%A5%EC%9D%B8%EC%A7%80%EB%AC%B8%EB%8F%99%EB%8C%80%EB%AC%B8_/1999"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_dongfashion))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.dft.co.kr/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_daehakro))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://korean.visitkorea.or.kr/kor/bz15/where/where_main_search.jsp?cid=126534/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_marronnier))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://korean.visitkorea.or.kr/kor/bz15/where/where_tour.jsp?cid=126487/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_naksan))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://korean.visitkorea.or.kr/kor/bz15/where/where_tour.jsp?cid=129501/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_63square))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.63.co.kr/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_yeouido))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://korean.visitkorea.or.kr/kor/bz15/where/where_tour.jsp?cid=127955/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_mbc))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mbcworld.imbc.com/guide/index.html/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_pyeongwha))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://worldcuppark.seoul.go.kr/parkinfo/parkinfo2_1.html/"));
                        startActivity(webIntent);
                    } else if (spotName.equals(getString(R.string.spot_skypark))) {
                        webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://worldcuppark.seoul.go.kr/parkinfo/parkinfo3_1.html/"));
                        startActivity(webIntent);
                    }
                    break;

                case R.id.showmap_btn:
                    Intent intent = new Intent();

                    if (spotName.equals(getString(R.string.spot_deoksu))) {
                        intent.putExtra("key", getString(R.string.spot_deoksu));
                    } else if (spotName.equals(getString(R.string.spot_myeongdong))) {
                        intent.putExtra("key", getString(R.string.spot_myeongdong));
                    } else if (spotName.equals(getString(R.string.spot_namsangol))) {
                        intent.putExtra("key", getString(R.string.spot_namsangol));
                    } else if (spotName.equals(getString(R.string.spot_sungnyemun))) {
                        intent.putExtra("key", getString(R.string.spot_sungnyemun));
                    } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
                        intent.putExtra("key", getString(R.string.spot_namsanpark));
                    } else if (spotName.equals(getString(R.string.spot_nseoul))) {
                        intent.putExtra("key", getString(R.string.spot_nseoul));
                    } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
                        intent.putExtra("key",getString(R.string.spot_gyeongbok));
                    } else if (spotName.equals(getString(R.string.spot_gwanghwamun))) {
                        intent.putExtra("key", getString(R.string.spot_gwanghwamun));
                    } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
                        intent.putExtra("key", getString(R.string.spot_jongmyo));
                    } else if (spotName.equals(getString(R.string.spot_bosingak))) {
                        intent.putExtra("key", getString(R.string.spot_bosingak));
                    } else if (spotName.equals(getString(R.string.spot_ssamzie))) {
                        intent.putExtra("key", getString(R.string.spot_ssamzie));
                    } else if (spotName.equals(getString(R.string.spot_insadong))) {
                        intent.putExtra("key", getString(R.string.spot_insadong));
                    } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
                        intent.putExtra("key", getString(R.string.spot_changdeokgung));
                    } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
                        intent.putExtra("key", getString(R.string.spot_changgyeong));
                    } else if (spotName.equals(getString(R.string.spot_bukchon))) {
                        intent.putExtra("key", getString(R.string.spot_bukchon));
                    } else if (spotName.equals(getString(R.string.spot_heunginjimun))) {
                        intent.putExtra("key", getString(R.string.spot_heunginjimun));
                    } else if (spotName.equals(getString(R.string.spot_dongfashion))) {
                        intent.putExtra("key", getString(R.string.spot_dongfashion));
                    } else if (spotName.equals(getString(R.string.spot_daehakro))) {
                        intent.putExtra("key", getString(R.string.spot_daehakro));
                    } else if (spotName.equals(getString(R.string.spot_marronnier))) {
                        intent.putExtra("key", getString(R.string.spot_marronnier));
                    } else if (spotName.equals(getString(R.string.spot_naksan))) {
                        intent.putExtra("key", getString(R.string.spot_naksan));
                    } else if (spotName.equals(getString(R.string.spot_63square))) {
                        intent.putExtra("key", getString(R.string.spot_63square));
                    } else if (spotName.equals(getString(R.string.spot_yeouido))) {
                        intent.putExtra("key", getString(R.string.spot_yeouido));
                    } else if (spotName.equals(getString(R.string.spot_mbc))) {
                        intent.putExtra("key", getString(R.string.spot_mbc));
                    } else if (spotName.equals(getString(R.string.spot_pyeongwha))) {
                        intent.putExtra("key", getString(R.string.spot_pyeongwha));
                    } else if (spotName.equals(getString(R.string.spot_skypark))) {
                        intent.putExtra("key", getString(R.string.spot_skypark));
                    }
                    getActivity().setResult(1, intent);
                    getActivity().finish();
                    break;

                case R.id.navi_btn:
                    if (spotName.equals(getString(R.string.spot_deoksu))) {
                        TmapNavigation(getString(R.string.spot_deoksu));
                    } else if (spotName.equals(getString(R.string.spot_myeongdong))) {
                        TmapNavigation(getString(R.string.spot_myeongdong));
                    } else if (spotName.equals(getString(R.string.spot_namsangol))) {
                        TmapNavigation(getString(R.string.spot_namsangol));
                    } else if (spotName.equals(getString(R.string.spot_sungnyemun))) {
                        TmapNavigation(getString(R.string.spot_sungnyemun));
                    } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
                        TmapNavigation(getString(R.string.spot_namsanpark));
                    } else if (spotName.equals(getString(R.string.spot_nseoul))) {
                        TmapNavigation(getString(R.string.spot_nseoul));
                    } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
                        TmapNavigation(getString(R.string.spot_gyeongbok));
                    } else if (spotName.equals(getString(R.string.spot_gwanghwamun))) {
                        TmapNavigation(getString(R.string.spot_gwanghwamun));
                    } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
                        TmapNavigation(getString(R.string.spot_jongmyo));
                    } else if (spotName.equals(getString(R.string.spot_bosingak))) {
                        TmapNavigation(getString(R.string.spot_bosingak));
                    } else if (spotName.equals(getString(R.string.spot_ssamzie))) {
                        TmapNavigation(getString(R.string.spot_ssamzie));
                    } else if (spotName.equals(getString(R.string.spot_insadong))) {
                        TmapNavigation(getString(R.string.spot_insadong));
                    } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
                        TmapNavigation(getString(R.string.spot_changdeokgung));
                    } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
                        TmapNavigation(getString(R.string.spot_changgyeong));
                    } else if (spotName.equals(getString(R.string.spot_bukchon))) {
                        TmapNavigation(getString(R.string.spot_bukchon));
                    } else if (spotName.equals(getString(R.string.spot_heunginjimun))) {
                        TmapNavigation(getString(R.string.spot_heunginjimun));
                    } else if (spotName.equals(getString(R.string.spot_dongfashion))) {
                        TmapNavigation(getString(R.string.spot_dongfashion));
                    } else if (spotName.equals(getString(R.string.spot_daehakro))) {
                        TmapNavigation(getString(R.string.spot_daehakro));
                    } else if (spotName.equals(getString(R.string.spot_marronnier))) {
                        TmapNavigation(getString(R.string.spot_marronnier));
                    } else if (spotName.equals(getString(R.string.spot_naksan))) {
                        TmapNavigation(getString(R.string.spot_naksan));
                    } else if (spotName.equals(getString(R.string.spot_63square))) {
                        TmapNavigation(getString(R.string.spot_63square));
                    } else if (spotName.equals(getString(R.string.spot_yeouido))) {
                        TmapNavigation(getString(R.string.spot_yeouido));
                    } else if (spotName.equals(getString(R.string.spot_mbc))) {
                        TmapNavigation(getString(R.string.spot_mbc));
                    } else if (spotName.equals(getString(R.string.spot_pyeongwha))) {
                        TmapNavigation(getString(R.string.spot_pyeongwha));
                    } else if (spotName.equals(getString(R.string.spot_skypark))) {
                        TmapNavigation(getString(R.string.spot_skypark));
                    }
                    break;

                case R.id.visitCheckBtn:
                    if (spotName.equals(getString(R.string.spot_deoksu))) {
                        tourVisitCheck("0");
                    } else if (spotName.equals(getString(R.string.spot_myeongdong))) {
                        tourVisitCheck("1");
                    } else if (spotName.equals(getString(R.string.spot_namsangol))) {
                        tourVisitCheck("2");
                    } else if (spotName.equals(getString(R.string.spot_sungnyemun))) {
                        tourVisitCheck("3");
                    } else if (spotName.equals(getString(R.string.spot_namsanpark))) {
                        tourVisitCheck("4");
                    } else if (spotName.equals(getString(R.string.spot_nseoul))) {
                        tourVisitCheck("5");
                    } else if (spotName.equals(getString(R.string.spot_gyeongbok))) {
                        tourVisitCheck("6");
                    } else if (spotName.equals(getString(R.string.spot_gwanghwamun))) {
                        tourVisitCheck("7");
                    } else if (spotName.equals(getString(R.string.spot_jongmyo))) {
                        tourVisitCheck("8");
                    } else if (spotName.equals(getString(R.string.spot_bosingak))) {
                        tourVisitCheck("9");
                    } else if (spotName.equals(getString(R.string.spot_ssamzie))) {
                        tourVisitCheck("10");
                    } else if (spotName.equals(getString(R.string.spot_insadong))) {
                        tourVisitCheck("11");
                    } else if (spotName.equals(getString(R.string.spot_changdeokgung))) {
                        tourVisitCheck("12");
                    } else if (spotName.equals(getString(R.string.spot_changgyeong))) {
                        tourVisitCheck("13");
                    } else if (spotName.equals(getString(R.string.spot_bukchon))) {
                        tourVisitCheck("14");
                    } else if (spotName.equals(getString(R.string.spot_heunginjimun))) {
                        tourVisitCheck("15");
                    } else if (spotName.equals(getString(R.string.spot_dongfashion))) {
                        tourVisitCheck("16");
                    } else if (spotName.equals(getString(R.string.spot_daehakro))) {
                        tourVisitCheck("17");
                    } else if (spotName.equals(getString(R.string.spot_marronnier))) {
                        tourVisitCheck("18");
                    } else if (spotName.equals(getString(R.string.spot_naksan))) {
                        tourVisitCheck("19");
                    } else if (spotName.equals(getString(R.string.spot_63square))) {
                        tourVisitCheck("20");
                    } else if (spotName.equals(getString(R.string.spot_yeouido))) {
                        tourVisitCheck("21");
                    } else if (spotName.equals(getString(R.string.spot_mbc))) {
                        tourVisitCheck("22");
                    } else if (spotName.equals(getString(R.string.spot_pyeongwha))) {
                        tourVisitCheck("23");
                    } else if (spotName.equals(getString(R.string.spot_skypark))) {
                        tourVisitCheck("24");
                    }
                    break;

            }
        }
    };

    public void tourVisitCheck(String position) {
        //이미 방문을 한 경우
        if (pref.getValue(position, false, "방문여부")) {
            buildAlertMessage("이미방문", position);
        }
        // 방문을 안했을 경우
        else {
            if (!MainActivity.isOnGps()) {
                buildAlertMessage("GPS", "");
            } else {
                Location location = MainActivity.getMyLocation();
                if (checkPosition(location, Integer.valueOf(position))) {
                    buildAlertMessage("성공", position);
                } else {
                    buildAlertMessage("실패", "");
                }
            }
        }
    }

    public void buildAlertMessage(String mode, String position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TourInfoActivity.mContextTourInfo);
        key = position;

        if (mode.equals("GPS")) {
            builder.setMessage(getString(R.string.alertVisitGps))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else if (mode.equals("성공")) {
            builder.setMessage(getString(R.string.alertVisitComplete))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                            tourSpotVisitCheck.setBackground(getResources().getDrawable(R.drawable.visited_btn));
                            //tourSpotVisitCheck.setBackgroundColor(getResources().getColor(R.color.green_light));
                            pref.putValue(key, true, "방문여부");
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else if (mode.equals("실패")) {
            builder.setMessage(getString(R.string.alertVisitFail))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else if (mode.equals("이미방문")) {
            builder.setMessage(getString(R.string.alertVisitAlready))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            // tourSpotVisitCheck.setBackgroundColor(getResources().getColor(R.color.grey));
                            pref.putValue(key, false, "방문여부");
                            dialog.cancel();
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
    }

    public boolean checkPosition(Location myLocation, int btnId) {
        boolean result = false;
        switch (btnId) {
            // 아래<내 위도<위  && 왼< 내 경도 < 오
            //덕수궁 돌담길
            case 0:
                if ((37.564624854689285 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.567592826189404)
                        && (126.9731006026268 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.97722818702461))
                    result = true;
                else
                    result = false;
                break;
            //명동
            case 1:
                if ((37.55887221292253 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.56563527516861)
                        && (126.98163840919732 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.98919687420128))
                    result = true;
                else
                    result = false;
                break;
            //남산골 한옥마을
            case 2:
                if ((37.556816622126604 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.560001248750986)
                        && (126.99206583201884 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.99540384113789))
                    result = true;
                else
                    result = false;
                break;
            //숭례문
            case 3:
                if ((37.55900749613088 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.56037679289365)
                        && (126.97447389364243 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.97586327791213))
                    result = true;
                else
                    result = false;
                break;
            //남산 공원 -> 그냥 갔다온 것으로 처리할 계획
            case 4:
                if ((37.54709791204009 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.55615958657706)
                        && (126.97939977049829 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.9946863502264))
                    result = true;
                else
                    result = false;
                break;
                //result = true;
                //break;
            //N 서울타워 -> 그냥 갔다온 것으로 처리할 계획
            case 5:
                if ((37.54709791204009 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.55615958657706)
                        && (126.97939977049829 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.9946863502264))
                    result = true;
                else
                    result = false;
                break;
//                result = true;
//                break;
            // 경복궁
            case 6:
                if ((37.575461045726954 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.58413773751325)
                        && (126.97321258485316 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.98086861521006))
                    result = true;
                else
                    result = false;
                break;
            //광화문 광장 -> 그냥 갔다온 것으로 처리할 계획
            case 7:
                if ((37.57191886867333 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57401788800245)
                        && (126.97461001574993 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.97664212435485))
                    result = true;
                else
                    result = false;
                break;
//                result = true;
//                break;
            //종묘
            case 8:
                if ((37.570641198876515 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57763197419156)
                        && (126.9913285598159 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.99681971222161))
                    result = true;
                else
                    result = false;
                break;
            //보신각터
            case 9:
                if ((37.56954580811005 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57014532528512)
                        && (126.98288295418023 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.9844014197588))
                    result = true;
                else
                    result = false;
                break;
            //쌈지길
            case 10:
                if ((37.57399051795766 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57446377916004)
                        && (126.9845362007618 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.98515411466359))
                    result = true;
                else
                    result = false;
                break;
            //인사동 -> 그냥 갔다온 것으로 처리할 계획
            case 11:
                if ((37.57333894848428 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57639399471106)
                        && (126.98264725506306 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.98661256581543))
                    result = true;
                else
                    result = false;
                break;
//                result = true;
//                break;
            //창덕궁과 후원 -> 그냥 갔다온 것으로 처리할 계획
            case 12:
                if ((37.57736439805643 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57987590899944)
                        && (126.99317190796138 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.99693202972412))
                    result = true;
                else
                    result = false;
                break;
//                result = true;
//                break;
            //창경궁
            case 13:
                if ((37.57736439805643 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57987590899944)
                        && (126.99317190796138 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.99693202972412))
                    result = true;
                else
                    result = false;
                break;
            //북촌 한옥마을 -> 그냥 갔다온 것으로 처리할 계획
            case 14:
                if ((37.57399051795766 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57446377916004)
                        && (126.9845362007618 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.98515411466359))
                    result = true;
                else
                    result = false;
                break;
//                result = true;
//                break;
            //흥인지문
            case 15:
                if ((37.5708729243104 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.571249210864345)
                        && (127.00948141515256 <= myLocation.getLongitude() && myLocation.getLongitude() <= 127.0098227262497))
                    result = true;
                else
                    result = false;
                break;
            //동대문 패션타운
            case 16:
                if ((37.56551462073463 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.56859815720111)
                        && (127.00809840112925 <= myLocation.getLongitude() && myLocation.getLongitude() <= 127.0121143385768))
                    result = true;
                else
                    result = false;
                break;
            //대학로  -> 그냥 갔다온 것으로 처리할 계획
            case 17:
                if ((37.57852131914368 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.5842482654808)
                        && (126.99980132281779 <= myLocation.getLongitude() && myLocation.getLongitude() <= 127.00551409274341))
                    result = true;
                else
                    result = false;
                break;
//                result = true;
//                break;
            //마로니에 공원
            case 18:
                if ((37.57956237332758 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.580887982510106)
                        && (127.00223609805109 <= myLocation.getLongitude() && myLocation.getLongitude() <= 127.00342163443565))
                    result = true;
                else
                    result = false;
                break;
            //낙산 공원
            case 19:
                if ((37.578051803726865 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.58304998477044)
                        && (127.58183760263979 <= myLocation.getLongitude() && myLocation.getLongitude() <= 127.00897280126815))
                    result = true;
                else
                    result = false;
                break;
            //63스퀘어
            case 20:
                if ((37.51858117991359 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.52752666793541)
                        && (126.91632494330406 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.94096840918066))
                    result = true;
                else
                    result = false;
                break;
            //여의도 공원 -> 그냥 갔다온 것으로 처리할 계획GKSEMF
            case 21:
                if ((37.52147785811003 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.532387589995395)
                        && (126.93862482905388 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.93059161305428))
                    result = true;
                else
                    result = false;
                break;
//                result = true;
//                break;
            //MBC 월드 방송 테마 파크
            case 22:
                if ((37.5783669400379 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.58196327893457)
                        && (126.88821807503699 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.8927989527583))
                    result = true;
                else
                    result = false;
                break;

            //평화의 공원 -> 그냥 갔다온 것으로 처리할 계획
            case 23:
                if ((37.56437902438309 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57195713465197)
                        && (126.88073504716156 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.88957259058952))
                    result = true;
                else
                    result = false;
            break;
//                result = true;
//                break;
            //하늘 공원
            case 24:
                if ((37.56437902438309 <= myLocation.getLatitude() && myLocation.getLatitude() <= 37.57195713465197)
                        && (126.88073504716156 <= myLocation.getLongitude() && myLocation.getLongitude() <= 126.88957259058952))
                    result = true;
                else
                    result = false;
                break;

            default:
                break;
        }
        return result;
    }

    private void TmapNavigation(String spotName) {
        boolean isTmapApp_1 = appInstalledOrNot("com.skt.skaf.l001mtm091");
        boolean isTmapApp_2 = appInstalledOrNot("com.skt.tmap.ku");
        boolean isTmapApp_3 = appInstalledOrNot("com.skt.skaf.l001mtm092");
        String latitude = "37.5648988447", longitude = "126.9764934725";
        this.spotDbAdapter = new SpotsDbAdapter(TourInfoActivity.mContextTourInfo);
        spotDbAdapter.open();
        Cursor result = spotDbAdapter.fetchSpotByTitle(spotName);
        result.moveToFirst();

        while (!result.isAfterLast()) {
            latitude = result.getString(1);
            Log.i("위도", latitude);
            longitude = result.getString(2);
            Log.i("위도", longitude);
            result.moveToNext();
        }
        if (isTmapApp_1 || isTmapApp_2 || isTmapApp_3) {
            tmaptapi.invokeRoute(spotName, Float.parseFloat(longitude), Float.parseFloat(latitude));
        } else {
            showTmapInstallDialog();
        }
    }


    private void TmapAuthentication() {
        tmaptapi = new TMapTapi(TourInfoActivity.mContextTourInfo);
        tmaptapi.setSKPMapAuthentication("593851f2-df66-33d7-ae97-52ef7278295f");

        tmaptapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {

            @Override
            public void SKPMapApikeySucceed() {
                Log.i("키인증", "성공");
            }

            @Override
            public void SKPMapApikeyFailed(String errorMsg) {
                Log.i("키인증", "실패");
                Toast.makeText(TourInfoActivity.mContextTourInfo, getString(R.string.navigationError), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTmapInstallDialog() {

        telephonyManager = (TelephonyManager) TourInfoActivity.mContextTourInfo.getSystemService(Context.TELEPHONY_SERVICE);
        networkoper = telephonyManager.getNetworkOperatorName();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TourInfoActivity.mContextTourInfo);

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
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}