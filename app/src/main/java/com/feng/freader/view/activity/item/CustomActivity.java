package com.feng.freader.view.activity.item;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.feng.freader.R;
import com.feng.freader.entity.ReadEntity;
import com.feng.freader.util.LogUtil;
import com.feng.freader.view.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import data.SharedPreferencesUtils;
import recycler.MultiItemTypeAdapter;
import recycler.adpter.CustomAdapter;

public class CustomActivity extends BaseActivity {

    RecyclerView recycler;
    List<ReadEntity> list = new ArrayList<>();
    CustomAdapter adapter;
    public static ReadEntity URL = null;
    public static String custom = "custom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        init();
        findViewById(R.id.fan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomActivity.this, AddWebActivity.class));
            }
        });
        /*if(SharedPreferencesUtils.contains(this,custom)){
            List<ReadEntity>  lists = ( List<ReadEntity>)SharedPreferencesUtils.getBean(this,custom);
            list.addAll(lists);
        }else {

        }*/
        addList();
        adapter = new CustomAdapter(this, R.layout.item_novel, list);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                URL = list.get(position);
                startActivity(new Intent(CustomActivity.this, WebActivity.class));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void addList() {
        list.clear();
        list.add(new ReadEntity("https://m.heiyan.la", "黑岩小说网", ".html",
                "h1[class='bookname']", "a[id=\"next_url\"]", "a[id=\"prev_url\"]", "div[data-id]"));


        list.add(new ReadEntity("https://m.05wxw.com", "零五文学", ".html",
                "h1", "a[id=\"next_url\"]", "a[id=\"prev_url\"]", "div[data-id]"));

        list.add(new ReadEntity("https://m.gzc2.com", "无限小说网", ".html",
                "h1[class='headline']", "div[class=\"pager\"]", "div[class=\"pager\"]", "div[class='content']"));

        list.add(new ReadEntity("https://m.haxyuedu.com", "海岸线文学网", "yuedu",
                "div[class='nr_title']", "a[id=\"pb_next\"]", "a[id=\"pb_prev\"]", "div[class='nr_nr']"));

        list.add(new ReadEntity("http://m.ltoooo.com", "新笔趣阁", ".html",
                "span[class=\"title\"]", "a[id=\"pb_next\"]", "a[id=\"pb_prev\"]", "div[id=\"chaptercontent\"]"));
        list.add(new ReadEntity("http://m.ibqg5200.com", "笔趣阁5200", "wapbook",
                "div[class=\"nr_title\"]", "a[id=\"pb_next\"]", "a[id=\"pb_prev\"]", "div[id=\"nr\"]"));
        list.add(new ReadEntity("https://m.biqugetv.com", "笔趣阁", "wapbook",
                "span[class=\"title\"]", "a[id=\"pt_next\"]", "a[id=\"pt_prev\"]", "div[id=\"chaptercontent\"]"));
        list.add(new ReadEntity("https://m.haotxt.com", "笔趣阁", "html",
                "h1[id=\"chaptertitle\"]", "ul[class=\"novelbutton\"]", "ul[class=\"novelbutton\"]", "div[id=\"novelcontent\"]"));

        SharedPreferencesUtils.putBean(this, custom, list);
    }

    private void init() {
        recycler = findViewById(R.id.recycler);
    }


}
