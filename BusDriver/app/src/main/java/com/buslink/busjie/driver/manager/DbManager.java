package com.buslink.busjie.driver.manager;

import com.lidroid.xutils.DbUtils;

/**
 * Created by yanlong.luo on 2015/9/7.
 */
public class DbManager {

    private static DbManager instance;
    private static DbUtils db;


    private DbManager() {}

    public static DbManager getInstance(){
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    public static DbUtils getDb() {
        if (db == null) {
            db = DbUtils.create(MyApplication.getContext());
        }
        return db;
    }
}
