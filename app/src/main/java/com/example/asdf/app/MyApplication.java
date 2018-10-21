package com.example.asdf.app;

import android.app.Application;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.asdf.CityDB.CityDB;
import com.example.asdf.com.example.asdf.bean.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MyApplication extends Application {
    private static final String TAG="myapp";
    private static MyApplication mApplication;
    private CityDB mCityDB;
    private List<City> mCityList;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyApplication");
        mApplication=this;
        mCityDB=openCityDB();
        initCityList();
    }

    public static MyApplication getInstance() {
        return mApplication;
    }

    private CityDB openCityDB() {
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG, path);
        if (!db.exists()) {
            String pathfolder = "/data" + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
                Log.i("MyApp", "mkdirs");
            }
            Log.i("MyApp", "db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this,path);
    }

    public List<City> getmCityList() {
        return mCityList;
    }

    private void initCityList()
    {
        mCityList=new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();
        int i = 0;
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG, cityCode + ":" + cityName);
        }
        Log.d(TAG, "i=" + i);
        return true;
    }
}
