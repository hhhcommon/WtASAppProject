package com.woting.ui.home.program.citylist.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.woting.common.database.SQLiteHelper;
import com.woting.ui.home.common.model.CatalogName;

import java.util.ArrayList;
import java.util.List;

public class CityInfoDao {

    private SQLiteHelper helper;
    private Context context;

    public CityInfoDao(Context context) {
        helper = new SQLiteHelper(context);
        this.context = context;
    }

    //查
    public List<CatalogName> queryCityInfo() {
        List<CatalogName> mylist = new ArrayList<CatalogName>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
    /*	String url = cursor.getString(cursor.getColumnIndex("url"));
		String author = cursor.getColumnName(cursor.getColumnIndex("author"));*/

        try {
            cursor = db.rawQuery("Select * from cityinfo", new String[]{});
            while (cursor.moveToNext()) {
                String Adcode = cursor.getString(cursor.getColumnIndex("adcode"));
                String CityName = cursor.getString(cursor.getColumnIndex("cityname"));
                CatalogName mfFenleiname = new CatalogName();
                mfFenleiname.setCatalogId(Adcode);
                mfFenleiname.setCatalogName(CityName);
                mylist.add(mfFenleiname);
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
        return mylist;
    }

    //增
    public void InsertCityInfo(List<CatalogName> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        for (int i = 0; i < list.size(); i++) {
            String adcode = list.get(i).getCatalogId();
            String cityName = list.get(i).getCatalogName();
            db.execSQL("insert into cityinfo(adcode,cityname)values(?,?)", new Object[]{adcode, cityName});
        }
        if (db != null) {
            db.close();
        }
    }

    //
    public void DelCityInfo() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete  from cityinfo");
        db.close();
    }


}
