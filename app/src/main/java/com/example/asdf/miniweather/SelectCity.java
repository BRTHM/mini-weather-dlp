package com.example.asdf.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.example.asdf.miniweather.R;

public class SelectCity extends Activity implements OnClickListener{
    private ImageView mBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.title_back)
        {
            Intent i = new Intent();
            i.putExtra("cityCode", "101160101");
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
