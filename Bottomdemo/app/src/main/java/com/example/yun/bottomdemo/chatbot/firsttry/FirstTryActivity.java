package com.example.yun.bottomdemo.chatbot.firsttry;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.example.yun.bottomdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FirstTryActivity extends AppCompatActivity implements EventListener {
    //--百度asr的类
    EventManager mEventManager;
    //--用来保存asr的json
    private String mJson;
    //--用来保存asr识别的结果
    String result;
    //--???
    private static final String TEMP_DIR = "/sdcard/baiduTTS";
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";
    private static final String MODEL_FILENAME =TEMP_DIR + "/" + "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
    //--百度TTS的类，有speak和stop方法
    protected SpeechSynthesizer mSpeechSynthesizer;
    //--注册百度语音应用的app秘钥
    protected String appId = "11028185";
    protected String appKey = "6IoQLXu7VnMUgfeGWyf7NjBR";
    protected String secretKey = "0qyfeFZ5395alzfT31Og5rnBC7e8S47p";

    private Robot mRobot = null;
    private Handler mHandler;
    private List<Msg> msgList = new ArrayList<Msg>();
    //--定义识别语音按钮，信息条滚动栏试图，消息适配器
    private Button speakSend;
    private Button speakStop;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_try);

        //设置顶部状态栏透明
        StatusBarUtils.setWindowStatusBarColor(this, android.R.color.darker_gray);

//        getSupportActionBar().hide();//--隐藏顶部动作栏
        //--获得额权限
        initPermission();
        //--创建asr管理，初始化
        mEventManager = EventManagerFactory.create(this, "asr");
        //--注册监听器
        mEventManager.registerListener(this);
        Map<String, Object> params = new LinkedHashMap<>();
        //  基于SDK集成2.1 设置识别参数
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        mJson = new JSONObject(params).toString();
        //--初始化TTS
        new Thread(new Runnable() {
            @Override
            public void run() {
                initTTs();
            }
        }).start();
        // --初始化消息数据
        initMsgs();
        //--消息视图
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        //--asr识别开始按钮
        speakSend = (Button) findViewById(R.id.speechSend);
        //点击后，将asr识别的数据保存在mJson里面
        speakSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result="";
                asrStart();
            }
        });

        //--asr识别结束按钮
        speakStop = (Button) findViewById(R.id.speechStop);
        //点击后，将asr识别的数据保存在mJson里面
        speakStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result="";
                asrStop();
            }
        });


        //--设置机器人回复信息条
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                String s = (String) msg.obj;
                mSpeechSynthesizer.speak(s);
                Msg msgReceive = new Msg(s, Msg.TYPE_RECEIVED);
                msgList.add(msgReceive);
                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
            }
        };
        mRobot = new Robot(mHandler);
    }

    private void initTTs() {
        fileCopy("bd_etts_text.dat", TEXT_FILENAME);
        fileCopy("bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat", MODEL_FILENAME);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(FirstTryActivity.this);
        mSpeechSynthesizer.setAppId(appId);
        mSpeechSynthesizer.setApiKey(appKey, secretKey);
        mSpeechSynthesizer.auth(TtsMode.MIX);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);
        mSpeechSynthesizer.initTts(TtsMode.MIX);
    }

    private void fileCopy(String file, String copy) {
        AssetManager am = getAssets();
        File copyFile = new File(copy);
        try {
            InputStream is = am.open(file);
            FileOutputStream fos = new FileOutputStream(copyFile);
            byte[] bytes = new byte[1024 * 1024];
            int length = 0;
            while ((length = is.read(bytes)) > 0) {
                fos.write(bytes, 0, length);
                fos.flush();
            }
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    //--识别语音点击事件，点击开始识别
//    public void click(View view) {
//        mEventManager.send(SpeechConstant.ASR_START, mJson, null, 0, 0);
//    }

    //--百度语音接口，返回的json中asr.finish代表识别结束，best_result表示识别最好的结果
    @Override
    public void onEvent(String s, String s1, byte[] bytes, int i, int i1) {
        if (s.equals("asr.finish")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Msg msg = new Msg(result, Msg.TYPE_SENT);
                    if(!msg.getContent().equals(""))
                    {
                        msgList.add(msg);
                        adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                        msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    }
                }
            });
            if(!result.equals("")) {

                mRobot.ask(result);
            }
        }
        try {
            if (s1 == null) return;
            JSONObject jsonObject = new JSONObject(s1);
            result = jsonObject.get("best_result").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 语音识别开始和停止
     *  基于SDK集成4.1 发送停止事件
     */
    private void asrStop( ) {
            mEventManager.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }
    private void asrStart() {
        mEventManager.send(SpeechConstant.ASR_START, mJson, null, 0, 0);
    }





    private void initMsgs() {
//        Msg msg1 = new Msg("什么是大熊猫", Msg.TYPE_SENT);
//        msgList.add(msg1);
        Msg msg2 = new Msg("Hi,我是棒棒哒导游助手，有什么问题你就问吧！", Msg.TYPE_RECEIVED);
        msgList.add(msg2);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }
}
