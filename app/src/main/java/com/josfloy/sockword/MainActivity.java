package com.josfloy.sockword;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.josfloy.greendao.entity.greendao.CET4Entity;
import com.josfloy.greendao.entity.greendao.CET4EntityDao;
import com.josfloy.greendao.entity.greendao.DaoMaster;
import com.josfloy.greendao.entity.greendao.DaoSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    SharedPreferences.Editor editor = null;              // 编辑数据库
    int j = 0;                                     // 用于记录答了几道题
    List<Integer> list;                             // 判断题的数目
    List<CET4Entity> datas;                             // 用于从数据库读取相应的词库
    int k;
    /**
     * 手指按下的点为（x1,y1）
     * 手指离开屏幕的点为（x2,y2）
     */
    float x1 = 0;
    float y1 = 0;
    float x2 = 0;
    float y2 = 0;
    //用来显示单词和音标的
    private TextView timeText, dateText, wordText, englishText;
    private ImageView playVoice;                     //播放声音
    private String mMonth, mDay, mWay, mHours, mMinute; // 用来显示时间
    private SpeechSynthesizer speechSynthesizer;         // 合成对象
    //锁屏
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private RadioGroup radioGroup;                      // 加载单词的三个选项
    private RadioButton radioOne, radioTwo, radioThree; // 单词意思的三个选项
    private SharedPreferences sharedPreferences;         // 定义轻量级数据库
    private SQLiteDatabase db;                        // 创建数据库
    private DaoMaster mDaoMaster, dbMaster;            // 管理者
    private DaoSession mDaoSession, dbSession;        // 和数据库进行会话
    // 对应的表,由java代码生成的,对数据库内相应的表操作使用此对象
    private CET4EntityDao questionDao, dbDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这两个flag已经被弃用
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);
        initDatas();
        initViews();
        initSynthesizer();
    }

    @SuppressLint("CommitPrefEdits")
    public void initDatas() {
        sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        list = new ArrayList<>();

        Random r = new Random();
        int i;
        while (list.size() < 10) {
            i = r.nextInt(20);
            if (!list.contains(i)) {
                list.add(i);
            }
        }

        //得到键盘锁 管理对象
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km != null) {
            kl = km.newKeyguardLock("unLock");
        }

        //初始化，只需要调用一次
        AssetsDatabaseManager.initManager(this);
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        //通过管理对象获取数据库
        SQLiteDatabase db1 = mg.getDatabase("word.db");

        //对数据库进行操作
        mDaoMaster = new DaoMaster(db1);
        mDaoSession = mDaoMaster.newSession();
        questionDao = mDaoSession.getCET4EntityDao();

        /*
         此DevOpenHelper类继承自SQLiteOpenHelper，
          第一个参数Context
          第二个参数数据库名字
          第三个参数CursorFactory
         */
        DaoMaster.DevOpenHelper helper = new DaoMaster.
                DevOpenHelper(this, "wrong.db", null);

        //初始化数据库
        db = helper.getWritableDatabase();
        dbMaster = new DaoMaster(db);
        dbSession = dbMaster.newSession();
        dbDao = dbSession.getCET4EntityDao();
    }

    private void initViews() {
        timeText = findViewById(R.id.time_text);            //用于显示分钟绑定id
        dateText = findViewById(R.id.date_text);             //用于显示日期绑定id
        wordText = findViewById(R.id.word_text);             //用于显示单词绑定id
        englishText = findViewById(R.id.english_text);       //用于显示音标绑定id
        playVoice = findViewById(R.id.play_vioce);              //用于播放单词的按钮绑定id
        playVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = wordText.getText().toString();
                speechSynthesizer.startSpeaking(text, new CustomSynthesizerListener());
            }
        });
        radioGroup = findViewById(R.id.choose_group);
        radioOne = findViewById(R.id.choose_btn_one);
        radioTwo = findViewById(R.id.choose_btn_two);
        radioThree = findViewById(R.id.choose_btn_three);
    }


    /*
     * 初始化语音播报及初始化合成器
     */
    public void initSynthesizer() {
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");

        SpeechUser.getUser().login(this, null, null, "appid=59f7c7b6",
                new SpeechListener() {
                    @Override
                    public void onEvent(int i, Bundle bundle) {

                    }

                    @Override
                    public void onData(byte[] bytes) {

                    }

                    @Override
                    public void onCompleted(SpeechError speechError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTime();

    }

    @SuppressLint("SetTextI18n")
    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        mMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);        //获取日期的月
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));      //获取日期的天
        mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));      //获取日期的星期

        if (calendar.get(Calendar.HOUR) < 10) {
            mHours = "0" + calendar.get(Calendar.HOUR);
        } else {
            mHours = String.valueOf(calendar.get(Calendar.HOUR));
        }

        if (calendar.get(Calendar.MINUTE) < 10) {
            mMinute = "0" + calendar.get(Calendar.MINUTE);
        } else {
            mMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        }

        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }

        timeText.setText(mHours + ":" + mMinute);
        dateText.setText(mMonth + "月" + mDay + "日" + "    " + "星期" + mWay);
    }

}
