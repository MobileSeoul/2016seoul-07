package ensharp.ttalawa.DBAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StationDbAdapter {

    public static final String KEY_ROWID = "_id";

    //    dbAdapter.createStation(contentName,addr_gu,new_addr,coordinate_x,coordinate_y,content_num,rack_count);

    public static final String KEY_CONTENT_NAME = "content_name";
    public static final String KEY_ADDR_GU = "addr_gu";
    public static final String KEY_NEW_ADDR = "new_addr";
    public static final String KEY_COORDINATE_X = "coordinate_x";
    public static final String KEY_COORDINATE_Y = "coordinate_y";
    public static final String KEY_CONTENT_NUM="content_num";
    public static final String KEY_RACK_COUNT="rack_count";
    public static final String KEY_ENG_CONTENT_NAME="eng_content_name";
    public static final String KEY_ENG_ADDR_GU="eng_addr_gu";
    public static final String KEY_ENG_NEW_ADDR="eng_new_addr";


    private static final String TAG = "StationDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_CREATE = "create table stations (_id integer primary key autoincrement, "
            + "content_name text, addr_gu text, new_addr text, coordinate_x text, coordinate_y text, content_num text, "
            + "rack_count text, eng_content_name text, eng_addr_gu text, eng_new_addr text);";

    private static final String DATABASE_NAME = "rentalStation";
    private static final String DATABASE_TABLE = "stations";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS stations");
            onCreate(db);
        }

        //최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~onCreate되고있습니당~~~~~~~~~~~~~~~~~~~~~~~~~");
            db.execSQL(DATABASE_CREATE);
        }

    }

    public StationDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public StationDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }



    //레코드 생성
    public long createStation(String contentName, String addr_gu, String new_addr, String coordinate_x, String coordinate_y,String content_num,String rack_count,
                              String engContentName, String engAddrGu, String engNewAddr) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CONTENT_NAME, contentName);
        initialValues.put(KEY_ADDR_GU, addr_gu);
        initialValues.put(KEY_NEW_ADDR, new_addr);
        initialValues.put(KEY_COORDINATE_X, coordinate_x);
        initialValues.put(KEY_COORDINATE_Y, coordinate_y);
        initialValues.put(KEY_CONTENT_NUM,content_num);
        initialValues.put(KEY_RACK_COUNT,rack_count);
        initialValues.put(KEY_ENG_CONTENT_NAME,engContentName);
        initialValues.put(KEY_ENG_ADDR_GU,engAddrGu);
        initialValues.put(KEY_ENG_NEW_ADDR,engNewAddr);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

//    //레코드 삭제-NO USE
//    public boolean deleteNote(long rowId) {
//        Log.i("Delete called", "value__" + rowId);
//        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
//    }

    //    dbAdapter.createStation(contentName,addr_gu,new_addr,coordinate_x,coordinate_y,content_num,rack_count);

    //모든 레코드 반환
    public Cursor fetchAllStations() {
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_CONTENT_NAME, KEY_ADDR_GU, KEY_NEW_ADDR, KEY_COORDINATE_X,
                KEY_COORDINATE_Y, KEY_CONTENT_NUM,KEY_RACK_COUNT,KEY_ENG_CONTENT_NAME,KEY_ENG_ADDR_GU,KEY_ENG_NEW_ADDR}, null, null, null, null, null);
    }

    //특정 레코드 반환-rowID
    public Cursor fetchStationByRowID(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_CONTENT_NAME, KEY_ADDR_GU, KEY_NEW_ADDR,
                KEY_COORDINATE_X, KEY_COORDINATE_Y, KEY_CONTENT_NUM,KEY_RACK_COUNT,KEY_ENG_CONTENT_NAME,KEY_ENG_ADDR_GU,KEY_ENG_NEW_ADDR }, KEY_ROWID
                + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchStationByNumber(String contentNumber)throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_CONTENT_NAME, KEY_ADDR_GU, KEY_NEW_ADDR,
                KEY_COORDINATE_X, KEY_COORDINATE_Y, KEY_CONTENT_NUM,KEY_RACK_COUNT,KEY_ENG_CONTENT_NAME,KEY_ENG_ADDR_GU,KEY_ENG_NEW_ADDR },KEY_CONTENT_NUM
                + "= '" + contentNumber+"'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }



//    //레코드 수정- NO USE
//    public boolean updateNote(long rowId, String title, String body) {
//        ContentValues args = new ContentValues();
//        args.put(KEY_CONTENTID, title);
//        args.put(KEY_SUBCATEGORYNM, body);
//        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
//    }


}