package com.dream.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dream.demo.R;
import com.dream.demo.data.TitleParser;
import com.dream.demo.entitiy.TitleEntity;
import com.dream.library.AbLog;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.border.BorderConfiguration;
import com.elvishew.xlog.formatter.log.DefaultLogFormatter;
import com.elvishew.xlog.formatter.message.json.DefaultJsonFormatter;
import com.elvishew.xlog.formatter.message.method.DefaultMethodFormatter;
import com.elvishew.xlog.formatter.message.throwable.DefaultThrowableFormatter;
import com.elvishew.xlog.formatter.message.xml.DefaultXmlFormatter;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
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

    private ListView    mListView;
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

        AbLog.d(Environment.getExternalStorageDirectory().toString());

        XLog.init(LogLevel.ALL,
                  new LogConfiguration                                             // If LogConfiguration not specified, will use new LogConfiguration.Builder().build()
                          .Builder()                                               // The log configuration used when logging
                                                                                   .tag("MY_TAG")                                           // Default: "XLOG"
                                                                                   .jsonFormatter(new DefaultJsonFormatter())               // Default: DefaultJsonFormatter
                                                                                   .xmlFormatter(new DefaultXmlFormatter())                 // Default: DefaultXmlFormatter
                                                                                   .methodFormatter(new DefaultMethodFormatter())           // Default: DefaultMethodFormatter
                                                                                   .throwableFormatter(new DefaultThrowableFormatter())     // Default: DefaultThrowableFormatter
                                                                                   .build(),
                  new AndroidPrinter(                                              // Print the log using android.util.Log, if no printer is specified, AndroidPrinter will be used by default
                                                                                   new BorderConfiguration                                  // If BorderConfiguration not specified, will use new BorderConfiguration.Builder().enable(false).build()
                                                                                           .Builder()                                       // The border configuration used to indicate the message
                                                                                                                                            .enable(true)                                    // Default: false
                                                                                                                                            .horizontalBorderChar('═')                       // Default: '═'
                                                                                                                                            .verticalBorderChar('║')                         // Default: '║'
                                                                                                                                            .borderLength(100)                               // Default: 100
                                                                                                                                            .build()
                  ),
                  new FilePrinter                                                  // Print the log to the file system, if not specified, will not be used
                          .Builder("/sdcard/xlog/")                                // The path to save log file
                                                                                   .fileNameGenerator(new DateFileNameGenerator())          // Default: ChangelessFileNameGenerator("log")
                                                                                   .backupStrategy(new FileSizeBackupStrategy(1024 * 1024)) // Default: FileSizeBackupStrategy(1024 * 1024)
                                                                                   .logFormatter(new DefaultLogFormatter())                 // Default: DefaultLogFormatter
                                                                                   .build());


        String jsonString = "{name:Elvis, age: 18}";
        String xmlString  = "<Person name=\"Elvis\" age=\"18\" />";
        XLog.d("The message");
        XLog.d("The message with argument: age=%s", 18);
        XLog.json(jsonString);
        XLog.xml(xmlString);
        XLog.stack("Here's the call stack");
        XLog.method();


        AbLog.d("The message");
        AbLog.d("The message with argument: age=%s", 18);
        AbLog.json(jsonString);
        AbLog.xml(xmlString);
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
        mListView = (ListView) findViewById(R.id.ListView);
        if (mListView != null) {
            mListView.setAdapter(new Adapter<TitleEntity>(this, mTitle.getList(), R.layout.item_lv_title) {
                @Override
                protected void convert(AdapterHelper helper, TitleEntity item) {
                    helper.setText(R.id.tv_title, helper.getPosition() + 1 + "、" + item.getTitle());
                }
            });

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
