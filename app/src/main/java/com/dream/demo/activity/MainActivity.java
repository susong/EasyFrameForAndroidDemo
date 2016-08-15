package com.dream.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dream.demo.R;
import com.dream.demo.data.TitleParser;
import com.dream.demo.entitiy.TitleEntity;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;

import java.lang.reflect.Constructor;

/**
 * Author:      SuSong
 * Email:       751971697@qq.com | susong0618@163.com
 * GitHub:      https://github.com/susong0618
 * Date:        16/7/20 下午6:33
 * Description: EasyFrameForAndroidDemo
 */
public class MainActivity extends AppCompatActivity {

    private ListView    mLvTitle;
    private TitleEntity mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        try {
            initData();
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        mTitle = (TitleEntity) getIntent().getSerializableExtra("title");
    }

    private void initView() throws Exception {
        if (mTitle != null) {
            if (mTitle.getLayoutResId() == 0) {
                //适用于传入的是自定义View的class，使用反射来生成对象。
                @SuppressWarnings("unchecked")
                Constructor constructor = mTitle.getContentViewClazz().getConstructor(Context.class);
                View view = (View) constructor.newInstance(this);
                setContentView(view);
            } else {
                //适用于传入的是指定布局Id。
                setContentView(mTitle.getLayoutResId());
            }
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mTitle.getTitle());
            }
        } else {
            //适用于首页启动时，没有数据传入，用默认数据。
            mTitle = TitleParser.title;
            setContentView(R.layout.view_listview);
        }
        mLvTitle = (ListView) findViewById(R.id.lv_title);
        if (mLvTitle != null) {
            mLvTitle.setAdapter(new Adapter<TitleEntity>(this, mTitle.getList(), R.layout.item_lv_title) {
                @Override
                protected void convert(AdapterHelper helper, TitleEntity item) {
                    helper.setText(R.id.tv_title, helper.getPosition() + 1 + "、" + item.getTitle());
                }
            });

            mLvTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, mTitle.getList().get(position).getActivityClazz());
                    intent.putExtra("title", mTitle.getList().get(position));
                    startActivity(intent);
                }
            });
        }
    }
}
