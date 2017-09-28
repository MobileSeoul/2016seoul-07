package ensharp.ttalawa;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;

import ensharp.ttalawa.DBAdapter.SpotsDbAdapter;
import ensharp.ttalawa.DBAdapter.StationDbAdapter;
import jxl.Sheet;
import jxl.Workbook;


/**
 * Created by Semin on 2016-09-28.
 */
public class SplashActivity extends Activity {

    /* 스플래쉬 화면이 뜨는 시간 */
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    private StationDbAdapter dbAdapter;
    private SpotsDbAdapter spotDbAdapter;

    private TextView tta;
    private TextView reungi;
    private TextView ra;
    private TextView idingharu;
    private TextView wa;
    private TextView dot1;
    private TextView dot2;
    private TextView dot3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Typeface tf_nanumsquare_bold = Typeface.createFromAsset(getAssets(), "NanumSquareB.ttf");
        Typeface tf_nanumsquare_extrabold = Typeface.createFromAsset(getAssets(), "NanumSquareEB.ttf");
        Typeface tf_samsung=Typeface.createFromAsset(getAssets(),"SamsungKorean-Bold.ttf");
        tta=(TextView)findViewById(R.id.txt_tta);
        reungi=(TextView)findViewById(R.id.txt_reungi);
        ra=(TextView)findViewById(R.id.txt_ra);
        idingharu=(TextView)findViewById(R.id.txt_idingharu);
        wa=(TextView)findViewById(R.id.txt_wa);
        dot1=(TextView)findViewById(R.id.txt_dot1);
        dot2=(TextView)findViewById(R.id.txt_dot2);
        dot3=(TextView)findViewById(R.id.txt_dot3);

        tta.setTypeface(tf_samsung);
        reungi.setTypeface(tf_samsung);
        ra.setTypeface(tf_samsung);
        idingharu.setTypeface(tf_samsung);
        wa.setTypeface(tf_samsung);
        dot1.setTypeface(tf_samsung);
        dot2.setTypeface(tf_samsung);
        dot3.setTypeface(tf_samsung);

        this.dbAdapter = new StationDbAdapter(this);
        //엑셀파일 데이터를 데이터베이스에 저장
        dbAdapter.open();
        Cursor temp = dbAdapter.fetchAllStations();
   //     Log.w("testing!getCount!", String.valueOf(temp.getCount()));

        if (temp.getCount() == 0)
            copyExcelDataToDatabase();
        dbAdapter.close();

        this.spotDbAdapter = new SpotsDbAdapter(this);

        //엑셀파일 데이터를 데이터베이스에 저장
        spotDbAdapter.open();
        temp = spotDbAdapter.fetchAllSpots();
//        Log.w("testing!getCount!", String.valueOf(temp.getCount()));

        if (temp.getCount() == 0)
            copyExcelDataToDbSpot();
        spotDbAdapter.close();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    //대여소 데이터 디비로 받아오기
    private void copyExcelDataToDatabase() {
        Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

        Workbook workbook = null;
        Sheet sheet = null;

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("RentalStationSeoul.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {

                sheet = workbook.getSheet(0);

                if (sheet != null) {

                    int nMaxColumn = 2;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(2).length - 1;

                    dbAdapter.open();
                    for (int nRow = nRowStartIndex + 2; nRow <= nRowEndIndex; nRow++) {
                        String content_name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String addr_gu = sheet.getCell(nColumnStartIndex + 1, nRow).getContents();
                        String new_addr = sheet.getCell(nColumnStartIndex + 2, nRow).getContents();
                        String coordinate_x = sheet.getCell(nColumnStartIndex + 3, nRow).getContents();
                        String coordinate_y = sheet.getCell(nColumnStartIndex + 4, nRow).getContents();
                        String content_num = sheet.getCell(nColumnStartIndex + 5, nRow).getContents();
                        String rack_count = sheet.getCell(nColumnStartIndex + 6, nRow).getContents();
                        String eng_contentName=sheet.getCell(nColumnStartIndex+7,nRow).getContents();
                        String eng_addr_gu=sheet.getCell(nColumnStartIndex+8,nRow).getContents();
                        String eng_new_addr=sheet.getCell(nColumnStartIndex+9,nRow).getContents();

                        dbAdapter.createStation(content_name, addr_gu, new_addr, coordinate_x, coordinate_y, content_num, rack_count,eng_contentName,eng_addr_gu,eng_new_addr);
                    }
                    dbAdapter.close();

                } else {
                    System.out.println("Sheet is null!!");
                }
            } else {
                System.out.println("WorkBook is null!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    //관광지 데이터 디비로 받아오기
    private void copyExcelDataToDbSpot() {
        Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

        Workbook workbook = null;
        Sheet sheet = null;

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("TourSpot_DB.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {

                sheet = workbook.getSheet(0);

                if (sheet != null) {

                    int nMaxColumn = 2;
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                    int nColumnStartIndex = 0;
                    int nColumnEndIndex = sheet.getRow(2).length - 1;

                    spotDbAdapter.open();
                    for (int nRow = nRowStartIndex + 1; nRow <= nRowEndIndex; nRow++) {
                        String spotMapX = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String spotMapY = sheet.getCell(nColumnStartIndex + 1, nRow).getContents();
                        String spotTitle = sheet.getCell(nColumnStartIndex + 2, nRow).getContents();
                        String spotAddress = sheet.getCell(nColumnStartIndex + 3, nRow).getContents();
                        String engSpotTitle=sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                        String engSpotAddress=sheet.getCell(nColumnStartIndex+5,nRow).getContents();
                        String korIntro=sheet.getCell(nColumnStartIndex+6,nRow).getContents();
                        String engIntro=sheet.getCell(nColumnStartIndex+7,nRow).getContents();

                        spotDbAdapter.createSpot(spotMapX, spotMapY, spotTitle, spotAddress,engSpotTitle,engSpotAddress,korIntro,engIntro);
                    }
                    spotDbAdapter.close();

                } else {
                    System.out.println("Sheet is null!!");
                }
            } else {
                System.out.println("WorkBook is null!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

    }

}
