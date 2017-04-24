package com.woting.ui.music.citylist.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.common.database.SQLiteHelper;
import com.woting.ui.music.citylist.citymodel.secondaryCity;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储地理位置
 */
public class CityInfoDao {

    private SQLiteHelper helper;
    private Context context;

    public CityInfoDao(Context context) {
        helper = new SQLiteHelper(context);
        this.context = context;
    }

    /**
     * 查找本地存储的地理位置
     * @return
     */
    public List<secondaryCity> queryCityInfo() {
        List<secondaryCity> _c = new ArrayList<secondaryCity>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
    /*	String url = cursor.getString(cursor.getColumnIndex("url"));
		String author = cursor.getColumnName(cursor.getColumnIndex("author"));*/
        try {
            cursor = db.rawQuery("Select * from cityInfo", new String[]{});
            while (cursor.moveToNext()) {
                String adCode = cursor.getString(cursor.getColumnIndex("adCode"));
                String cityName = cursor.getString(cursor.getColumnIndex("cityName"));
                secondaryCity _n = new secondaryCity();
                _n.setCatalogId(adCode);
                _n.setCatalogName(cityName);
                _c.add(_n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return _c;
    }

    /**
     * 新添加数据
     * @param list
     */
    public void InsertCityInfo(List<secondaryCity> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        for (int i = 0; i < list.size(); i++) {
            String adCode = list.get(i).getCatalogId();
            String cityName = list.get(i).getCatalogName();
            db.execSQL("insert into cityInfo(adCode,cityName)values(?,?)", new Object[]{adCode , cityName});
        }
        if (db != null) {
            db.close();
        }
    }

    //
    public void DelCityInfo() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete  from cityInfo");
        db.close();
    }


}
