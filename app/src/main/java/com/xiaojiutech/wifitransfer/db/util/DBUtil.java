package com.xiaojiutech.wifitransfer.db.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xiaojiutech.wifitransfer.db.model.DaoMaster;
import com.xiaojiutech.wifitransfer.db.model.DaoSession;

public class DBUtil {

    private static DBUtil sInstance;
    private static DaoSession daoSession;
    private static DaoMaster.DevOpenHelper helper;
    private static SQLiteDatabase sqLiteDatabase;
    private static DaoMaster daoMaster;

    private DBUtil(){

    }

    public static synchronized DBUtil getInstance(Context ctx) {
        if (sInstance == null) {
            sInstance = new DBUtil();
            helper = new DaoMaster.DevOpenHelper(ctx,"db_historyfile",null);
            sqLiteDatabase = helper.getWritableDatabase();
            daoMaster =   new DaoMaster(sqLiteDatabase);
            daoSession = daoMaster.newSession();
        }
        return sInstance;
    }

    public DaoSession getDaoSession(){
        return daoSession;
    }
}
