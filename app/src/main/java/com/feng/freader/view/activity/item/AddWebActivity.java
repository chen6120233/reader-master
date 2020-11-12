package com.feng.freader.view.activity.item;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.feng.freader.R;
import com.feng.freader.entity.ReadEntity;
import com.feng.freader.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import data.SharedPreferencesUtils;

import static com.feng.freader.view.activity.item.CustomActivity.custom;

public class AddWebActivity extends AppCompatActivity {

    EditText name,url,condition,titleName,next_url,prev_url,content;
    ReadEntity entity ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_web);
        if(null!=getIntent().getSerializableExtra("item")){
            entity=(ReadEntity) getIntent().getSerializableExtra("item");
        }
        name = findViewById(R.id.name);
        url = findViewById(R.id.url);
        condition = findViewById(R.id.condition);
        titleName = findViewById(R.id.titleName);
        next_url = findViewById(R.id.next_url);
        prev_url = findViewById(R.id.prev_url);
        content = findViewById(R.id.content);
        if(entity!=null){
            name.setText(entity.getName());
            url.setText(entity.getUrl());
            condition.setText(entity.getCondition());
            titleName .setText(entity.getTitleName());
            next_url.setText(entity.getNext_url());
            prev_url.setText(entity.getPrev_url());
            content.setText(entity.getContent());
        }
        findViewById(R.id.baocun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ReadEntity> list = new ArrayList<>();
                if(SharedPreferencesUtils.contains(AddWebActivity.this,custom)){
                    List<ReadEntity> bean = (List<ReadEntity>) SharedPreferencesUtils.getBean(AddWebActivity.this, custom);
                    list.addAll(bean);
                }
                if(entity!=null){
                    for (int i = 0; i <list.size() ; i++) {
                        if(entity.getUrl().equals(list.get(i).getUrl())){

                            list.set(i,new ReadEntity(url.getText().toString(),name.getText().toString(),condition.getText().toString(),
                                    titleName.getText().toString(),next_url.getText().toString(),prev_url.getText().toString(),content.getText().toString()));
                            LogUtil.v(list.get(i).toString());
                        }
                    }
                }else {
                    list.add(new ReadEntity(url.getText().toString(),name.getText().toString(),condition.getText().toString(),
                            titleName.getText().toString(),next_url.getText().toString(),prev_url.getText().toString(),content.getText().toString()));

                }
                SharedPreferencesUtils.putBean(AddWebActivity.this,custom,list);
                finish();
            }
        });
        findViewById(R.id.tuichu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
