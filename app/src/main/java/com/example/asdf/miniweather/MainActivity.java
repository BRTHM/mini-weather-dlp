package com.example.asdf.miniweather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asdf.com.example.asdf.bean.TodayWeather;
import com.example.asdf.util.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


public class MainActivity extends Activity implements OnClickListener{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    @SuppressLint("HandlerLeak")
    //接受子线程传回的天气数据
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    protected void onCreate(Bundle  savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        mUpdateBtn=(ImageView)findViewById(R.id.title_updata_btn);
        mUpdateBtn.setOnClickListener(this);
        mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        //检查网络
        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE)
        {
            Log.d("myweather", "net connected");
            Toast.makeText(MainActivity.this,"net connected",Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.d("myweather", "net unconnected");
            Toast.makeText(MainActivity.this,"net unconnected",Toast.LENGTH_LONG).show();
        }
        initView(); //初始化界面
    }

    //初始化控件
    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    @Override
    public void onClick(View v) {
        //选择城市
        if (v.getId()==R.id.title_city_manager)
        {
            Intent i = new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }
        //更新天气信息
        if(v.getId()==R.id.title_updata_btn)
        {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_ city_code", "101010100");
            Log.d("myWeather",cityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
            {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            }
            else
            {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    //根据城市编码更新天气信息
    private void queryWeatherCode(String cityCode) {
        //和风天气
        final String addressWeather = "https://free-api.heweather.com/s6/weather/forecast?location=CN" + cityCode+"&key=6393f5e642b943748dc79a31822f936e";
        final String addressAir = "https://free-api.heweather.com/s6/air/now?location=CN" + cityCode+"&key=6393f5e642b943748dc79a31822f936e";
        Log.d("myWeather", addressWeather);
        Log.d("myWeather", addressAir);
        //获取网络天气信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                TodayWeather todayWeather=null;
                String jsonDataWeather = getResponseStr(addressWeather);//天气信息
                String jsonDataAir = getResponseStr(addressAir);//空气信息
                String jsonData="["+jsonDataWeather+","+jsonDataAir+"]";
                todayWeather=parseJSON(jsonData);//解析JSON文件
                //返回天气信息到主线程
                if (todayWeather != null) {
                    Log.d("myWeather", todayWeather.toString());
                    Message msg = new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj = todayWeather;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    //通过网络地址获取天气信息文件
    private String getResponseStr(String address)
    {
        HttpURLConnection con = null;
        String responseStr=null;
        try {
            URL url = new URL(address);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(8000);
            con.setReadTimeout(8000);
            InputStream in = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                response.append(str);
                Log.d("myWeather", str);
            }
            responseStr = response.toString();
            Log.d("myWeather", responseStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return responseStr;
    }


    //解析JSON文件
    private TodayWeather parseJSON(String jsonData)
    {
        TodayWeather todayWeather = new TodayWeather();
        try {
            JSONArray jsonArray=new JSONArray(jsonData);
            JSONObject jsonObjectWeather=jsonArray.getJSONObject(0).getJSONArray("HeWeather6").getJSONObject(0);
            JSONObject jsonObjectWeatherForecast=jsonObjectWeather.getJSONArray("daily_forecast").getJSONObject(0);
            JSONObject jsonObjectAir=jsonArray.getJSONObject(1).getJSONArray("HeWeather6").getJSONObject(0);
            todayWeather.setCity(jsonObjectWeather.getJSONObject("basic").getString("location"));
            todayWeather.setUpdatetime(jsonObjectWeather.getJSONObject("update").getString("utc"));
            String tempMin=jsonObjectWeatherForecast.getString("tmp_min");
            String tempMax=jsonObjectWeatherForecast.getString("tmp_max");
            todayWeather.setWendu(tempMin+"°C-"+tempMax+"°C");
            todayWeather.setShidu(jsonObjectWeatherForecast.getString("hum"));
            todayWeather.setPm25(jsonObjectAir.getJSONObject("air_now_city").getString("pm25"));
            todayWeather.setQuality(jsonObjectAir.getJSONObject("air_now_city").getString("qlty"));
            todayWeather.setFengxiang(jsonObjectWeatherForecast.getString("wind_dir"));
            todayWeather.setFengli(jsonObjectWeatherForecast.getString("wind_sc"));
            todayWeather.setDate(jsonObjectWeatherForecast.getString("date"));
            todayWeather.setLow(tempMin);
            todayWeather.setHigh(tempMax);
            String typeDay=jsonObjectWeatherForecast.getString("cond_txt_d");
            String typeNight=jsonObjectWeatherForecast.getString("cond_txt_n");
            todayWeather.setType(typeDay.equals(typeNight)?typeDay:typeDay+"转"+typeNight);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    //根据已获得的数据更新天气界面
    void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime().split(" ")[1] + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu()+"%");
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date=null;
        try {
            date = dateFormat.parse(todayWeather.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateFormat=new SimpleDateFormat("MM月dd日 EEEE",Locale.CHINESE);
        weekTv.setText(dateFormat.format(date));
        temperatureTv.setText(todayWeather.getLow() + "°C~" + todayWeather.getHigh()+"°C");
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli()+"级");
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    //接受选择城市界面传回的城市代码
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为" + newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else{
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }
}
