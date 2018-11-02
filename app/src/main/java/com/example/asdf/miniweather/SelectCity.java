package com.example.asdf.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asdf.app.MyApplication;

import com.example.asdf.com.example.asdf.bean.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCity extends Activity implements OnClickListener{
    private List<City> mCityList;
    private ImageView mBackBtn;
    private ListView cityListView;
    private SimpleAdapter simplead;
    private List<Map<String, Object>> cityListems;
    private String cityCode;
    private TextView titleNameTv;
    private EditText mEditText;


    private TextWatcher mTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            filterData(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        cityListView = (ListView)findViewById(R.id.city_list_view);
        initCityListView();
        mEditText=(EditText)findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(mTextWatcher);
    }

    //初始化城市列表
    private void initCityListView() {
        mCityList=MyApplication.getInstance().getmCityList();//调用MyApplication的方法从数据库读取城市列表
        cityListems = new ArrayList<Map<String, Object>>();
        //将城市列表中的数据写入List
        for(int i=0;i<mCityList.size();i++)
        {
            City city=mCityList.get(i);
            Map<String, Object> cityListem= new HashMap<String, Object>();
            cityListem.put("city_name",city.getCity());
            cityListem.put("city_code",city.getNumber());
            cityListems.add(cityListem);
        }
        //设定列表界面
        simplead = new SimpleAdapter(this, cityListems
                , R.layout.city_item, new String[]{"city_name", "city_code"}
                , new int[]{R.id.city_name, R.id.city_code});
        cityListView.setAdapter(simplead);
        //单击选择城市
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this,"你单击了:"+position, Toast.LENGTH_SHORT).show();
//                cityCode=mCityList.get(position).getNumber();
                cityCode = cityListems.get(position).get("city_code").toString();
                Toast.makeText(SelectCity.this,cityCode, Toast.LENGTH_SHORT).show();
                titleNameTv=(TextView)findViewById(R.id.title_name);
                titleNameTv.setText("当前城市："+mCityList.get(position).getCity());
                titleNameTv.setText("当前城市："+cityListems.get(position).get("city_name"));
            }
        });

    }

    private void filterData(String filterStr)
    {
        //将城市列表中的数据写入List
        cityListems.clear();
        for(int i=0;i<mCityList.size();i++)
        {
            City city=mCityList.get(i);
            if(city.getCity().indexOf(filterStr.toString())!=-1) {
                Map<String, Object> cityListem = new HashMap<String, Object>();
                cityListem.put("city_name", city.getCity());
                cityListem.put("city_code", city.getNumber());
                cityListems.add(cityListem);
            }
        }
        //设定列表界面
        simplead.notifyDataSetChanged();

    }

    @Override
    //单击返回按钮关闭页面并传回城市代码
    public void onClick(View v) {
        if(v.getId()==R.id.title_back)
        {
            Intent i = new Intent();
            i.putExtra("cityCode", cityCode);
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
