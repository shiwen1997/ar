package com.example.yun.bottomdemo.chatbot.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.yun.bottomdemo.R;
import com.example.yun.bottomdemo.chatbot.adapter.ChatAdapter;
import com.example.yun.bottomdemo.chatbot.bean.AudioMsgBody;
import com.example.yun.bottomdemo.chatbot.bean.FileMsgBody;
import com.example.yun.bottomdemo.chatbot.bean.ImageMsgBody;
import com.example.yun.bottomdemo.chatbot.bean.MsgSendStatus;
import com.example.yun.bottomdemo.chatbot.bean.MsgType;
import com.example.yun.bottomdemo.chatbot.bean.MyMessage;
import com.example.yun.bottomdemo.chatbot.bean.TextMsgBody;
import com.example.yun.bottomdemo.chatbot.bean.VideoMsgBody;
import com.example.yun.bottomdemo.chatbot.firsttry.Msg;
import com.example.yun.bottomdemo.chatbot.firsttry.MsgAdapter;
import com.example.yun.bottomdemo.chatbot.firsttry.Robot;
import com.example.yun.bottomdemo.chatbot.util.ChatUiHelper;
import com.example.yun.bottomdemo.chatbot.util.FileUtils;
import com.example.yun.bottomdemo.chatbot.util.LogUtil;
import com.example.yun.bottomdemo.chatbot.util.PictureFileUtil;
import com.example.yun.bottomdemo.chatbot.widget.MediaManager;
import com.example.yun.bottomdemo.chatbot.widget.RecordButton;
import com.example.yun.bottomdemo.chatbot.widget.StateButton;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

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
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//导入FirstTry
//import android.os.MyMessage;


public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,EventListener {

    @BindView(R.id.llContent)
    LinearLayout mLlContent;
    @BindView(R.id.rv_chat_list)
    RecyclerView mRvChat;
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.bottom_layout)
    RelativeLayout mRlBottomLayout;//表情,添加底部布局
    @BindView(R.id.ivAdd)
    ImageView mIvAdd;
    @BindView(R.id.ivEmo)
    ImageView mIvEmo;
    @BindView(R.id.btn_send)
    StateButton mBtnSend;//发送按钮
    @BindView(R.id.ivAudio)
    ImageView mIvAudio;//录音图片
    @BindView(R.id.btnAudio)
    RecordButton mBtnAudio;//录音按钮
    @BindView(R.id.rlEmotion)
    LinearLayout mLlEmotion;//表情布局
    @BindView(R.id.llAdd)
    LinearLayout mLlAdd;//添加布局
     @BindView(R.id.swipe_chat)
     SwipeRefreshLayout mSwipeRefresh;//下拉刷新
     private ChatAdapter mAdapter;
     public static final String 	  mSenderId="right";
     public static final String     mTargetId="left";
     public static final int       REQUEST_CODE_IMAGE=0000;
     public static final int       REQUEST_CODE_VEDIO=1111;
     public static final int       REQUEST_CODE_FILE=2222;


     //--测试
//     private UseChat useChat;
    //

     //
//     List<MyMessage>  mReceiveMsgList=new ArrayList<MyMessage>();

    /**
     * firsttry移植
     */
    //--百度asr的类
    static EventManager mEventManager;
    //--用来保存asr的json
    private static String mJson;
    //--用来保存asr识别的结果


    static String result="";
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
//    private Button speakSend;
//    private Button speakStop;
//    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initContent();

        //--获得额权限
        initPermission();

        //返回上一页面
        Button bt_chatfinish=findViewById(R.id.finish_chat);
        bt_chatfinish.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

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



        //--设置机器人回复信息条
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                String s = (String) msg.obj;
//                mSpeechSynthesizer.speak(s);
//                Msg msgReceive = new Msg(s, Msg.TYPE_RECEIVED);
//                msgList.add(msgReceive);
//                adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
//                msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                //
                List<MyMessage>  mReceiveMsgList=new ArrayList<MyMessage>();
                //构建文本消息
                MyMessage mMessgaeText=getBaseReceiveMessage(MsgType.TEXT);
                TextMsgBody mTextMsgBody=new TextMsgBody();
                mTextMsgBody.setMessage(s);
                mMessgaeText.setBody(mTextMsgBody);
                mReceiveMsgList.add(mMessgaeText);
                mAdapter.addData(mMessgaeText);
                updateMsg(mMessgaeText);
//                mSwipeRefresh.setRefreshing(false);
                //
                Log.i("机器人回复",s);
            }
        };
        mRobot = new Robot(mHandler);

    }


    private ImageView  ivAudio;

    protected void initContent() {
        ButterKnife.bind(this) ;
        mAdapter=new ChatAdapter(this, new ArrayList<MyMessage>());
        LinearLayoutManager mLinearLayout=new LinearLayoutManager(this);
        mRvChat.setLayoutManager(mLinearLayout);
        mRvChat.setAdapter(mAdapter);
         mSwipeRefresh.setOnRefreshListener(this);
        initChatUi();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (ivAudio != null) {
                    ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                    ivAudio = null;
                    MediaManager.reset();
                }else{
                    ivAudio = view.findViewById(R.id.ivAudio);
                      MediaManager.reset();
                      ivAudio.setBackgroundResource(R.drawable.audio_animation_right_list);
                     AnimationDrawable  drawable = (AnimationDrawable) ivAudio.getBackground();
                    drawable.start();
                     MediaManager.playSound(ChatActivity.this,((AudioMsgBody)mAdapter.getData().get(position).getBody()).getLocalPath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            LogUtil.d("开始播放结束");
                            ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                            MediaManager.release();
                         }
                    });
                }
            }
        });
     }


    /**
     * 此处为机器人发送的消息
     */

    //待完善功能，从本地聊天记录中获取，
    @Override
    public void onRefresh() {
          //下拉刷新模拟获取历史消息
         List<MyMessage>  mReceiveMsgList=new ArrayList<MyMessage>();
          //构建文本消息
          MyMessage mMessgaeText=getBaseReceiveMessage(MsgType.TEXT);
          TextMsgBody mTextMsgBody=new TextMsgBody();
          mTextMsgBody.setMessage("你好，我是小西！");
          mMessgaeText.setBody(mTextMsgBody);
          mReceiveMsgList.add(mMessgaeText);
//          //构建图片消息
//          MyMessage mMessgaeImage=getBaseReceiveMessage(MsgType.IMAGE);
//          ImageMsgBody mImageMsgBody=new ImageMsgBody();
//          mImageMsgBody.setThumbUrl("http://pic19.nipic.com/20120323/9248108_173720311160_2.jpg");
//          mMessgaeImage.setBody(mImageMsgBody);
//          mReceiveMsgList.add(mMessgaeImage);
//          //构建文件消息
//          MyMessage mMessgaeFile=getBaseReceiveMessage(MsgType.FILE);
//          FileMsgBody mFileMsgBody=new FileMsgBody();
//          mFileMsgBody.setDisplayName("收到的文件");
//          mFileMsgBody.setSize(12);
//          mMessgaeFile.setBody(mFileMsgBody);
//          mReceiveMsgList.add(mMessgaeFile);

          mAdapter.addData(mReceiveMsgList);
          mSwipeRefresh.setRefreshing(false);
    }




    private void initChatUi(){
        //mBtnAudio
        final ChatUiHelper mUiHelper= ChatUiHelper.with(this);
        mUiHelper.bindContentLayout(mLlContent)
                .bindttToSendButton(mBtnSend)
                .bindEditText(mEtContent)
                .bindBottomLayout(mRlBottomLayout)
                .bindEmojiLayout(mLlEmotion)
                .bindAddLayout(mLlAdd)
                .bindToAddButton(mIvAdd)
                .bindToEmojiButton(mIvEmo)
                .bindAudioBtn(mBtnAudio)
                .bindAudioIv(mIvAudio)
                .bindEmojiData();
        //底部布局弹出,聊天列表上滑
        mRvChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRvChat.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter.getItemCount() > 0) {
                                mRvChat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });
        //点击空白区域关闭键盘
        mRvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUiHelper.hideBottomLayout(false);
                mUiHelper.hideSoftInput();
                mEtContent.clearFocus();
                mIvEmo.setImageResource(R.mipmap.ic_emoji);
                return false;
            }
        });
        //
//        mBtnAudio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                result="";
////                asrStop();
//                Log.i("测试按钮","hahaha");
//            }
//        });



        ((RecordButton) mBtnAudio).setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int time) {
                 LogUtil.d("录音结束回调");
                 File file = new File(audioPath);
                 if (file.exists()) {
                    sendAudioMessage(audioPath,time);
//                    sendTextMsg(result);
//                     mRobot.ask(result);

                }
            }
        });




    }

    //有待添加功能，发送位置
    @OnClick({R.id.btn_send,R.id.rlPhoto,R.id.rlVideo,R.id.rlLocation,R.id.rlFile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String content = mEtContent.getText().toString();
                //
                if (!"".equals(content)) {
                    sendTextMsg(mEtContent.getText().toString());
                    mEtContent.setText("");
                    mRobot.ask(content);
                }
                //
//                sendTextMsg(mEtContent.getText().toString());
//                mEtContent.setText("");
                break;
            case R.id.rlPhoto:
                PictureFileUtil.openGalleryPic(ChatActivity.this,REQUEST_CODE_IMAGE);
                break;
            case R.id.rlVideo:
                PictureFileUtil.openGalleryAudio(ChatActivity.this,REQUEST_CODE_VEDIO);
                break;
            case R.id.rlFile:
                PictureFileUtil.openFile(ChatActivity.this,REQUEST_CODE_FILE);
                break;
            case R.id.rlLocation:
                break;
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FILE:
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    LogUtil.d("获取到的文件路径:"+filePath);
                    sendFileMessage(mSenderId, mTargetId, filePath);
                    break;
                case REQUEST_CODE_IMAGE:
                    // 图片选择结果回调
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListPic) {
                        LogUtil.d("获取图片路径成功:"+  media.getPath());
                        sendImageMessage(media);
                    }
                    break;
                case REQUEST_CODE_VEDIO:
                    // 视频选择结果回调
                    List<LocalMedia> selectListVideo = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListVideo) {
                        LogUtil.d("获取视频路径成功:"+  media.getPath());
                        sendVedioMessage(media);
                    }
                    break;
            }
        }
    }




    //文本消息
    private void sendTextMsg(String hello)  {
        final MyMessage mMessgae=getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody=new TextMsgBody();
        mTextMsgBody.setMessage(hello);
        mMessgae.setBody(mTextMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }



    //图片消息
    private void sendImageMessage(final LocalMedia media) {
        final MyMessage mMessgae=getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody=new ImageMsgBody();
        mImageMsgBody.setThumbUrl(media.getCompressPath());
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }


    //视频消息
    private void sendVedioMessage(final LocalMedia media) {
        final MyMessage mMessgae=getBaseSendMessage(MsgType.VIDEO);
        //生成缩略图路径
        String vedioPath=media.getPath();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(vedioPath);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
        String imgname = System.currentTimeMillis() + ".jpg";
        String urlpath = Environment.getExternalStorageDirectory() + "/" + imgname;
        File f = new File(urlpath);
        try {
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        }catch ( Exception e) {
            LogUtil.d("视频缩略图路径获取失败："+e.toString());
            e.printStackTrace();
        }
        VideoMsgBody mImageMsgBody=new VideoMsgBody();
        mImageMsgBody.setExtra(urlpath);
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }

    //文件消息
    private void sendFileMessage(String from, String to, final String path) {
        final MyMessage mMessgae=getBaseSendMessage(MsgType.FILE);
        FileMsgBody mFileMsgBody=new FileMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDisplayName(FileUtils.getFileName(path));
        mFileMsgBody.setSize(FileUtils.getFileLength(path));
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }

    //语音消息
    private void sendAudioMessage( final String path,int time) {
        final MyMessage mMessgae=getBaseSendMessage(MsgType.AUDIO);
        AudioMsgBody mFileMsgBody=new AudioMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDuration(time);
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }


    private MyMessage getBaseSendMessage(MsgType msgType){
        MyMessage mMessgae=new MyMessage();
        mMessgae.setUuid(UUID.randomUUID()+"");
        mMessgae.setSenderId(mSenderId);
        mMessgae.setTargetId(mTargetId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private MyMessage getBaseReceiveMessage(MsgType msgType){
        MyMessage mMessgae=new MyMessage();
        mMessgae.setUuid(UUID.randomUUID()+"");
        mMessgae.setSenderId(mTargetId);
        mMessgae.setTargetId(mSenderId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private void updateMsg(final MyMessage mMessgae) {
        mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);
         //模拟1秒后发送成功
        new Handler().postDelayed(new Runnable() {
            public void run() {
                int position=0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //更新单个子条目
                for (int i=0;i<mAdapter.getData().size();i++){
                    MyMessage mAdapterMyMessage =mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMyMessage.getUuid())){
                        position=i;
                    }
                }
                mAdapter.notifyItemChanged(position);
            }
        }, 500);

    }



    @Override
    public void onEvent(String s, String s1, byte[] bytes, int i, int i1) {
        if (s.equals("asr.finish")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("result的值","它的值"+result);
//                    Msg msg = new Msg(result, Msg.TYPE_SENT);
                    if((!result.equals(null))&&(!result.equals("")))
                    {
                        Log.i("到了这一步","result不为null也不为》》");
                    }
                }
            });
            if((!result.equals(null))&&(!result.equals(""))) {
                Log.i("到了这一步","向机器人问问题");
                sendTextMsg(result);
                mRobot.ask(result);
            }
        }
        try {
            Log.i("到了这一步","获得s:"+s);
            Log.i("到了这一步","获得s1:"+s1);
            if (s1 == null) return;
            JSONObject jsonObject = new JSONObject(s1);
            result = jsonObject.get("best_result").toString();
            Log.i("到了这一步","给result赋值:"+result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initTTs() {
        fileCopy("bd_etts_text.dat", TEXT_FILENAME);
        fileCopy("bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat", MODEL_FILENAME);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(ChatActivity.this);
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

    /**
     * 语音识别开始和停止
     *  基于SDK集成4.1 发送停止事件
     */
    public static void asrStop() {
        mEventManager.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
    }
    public static void asrStart() {
        mEventManager.send(SpeechConstant.ASR_START, mJson, null, 0, 0);
    }
    public static void cancleSendText(){
        result = "";
    }


    public void testlog() {
        Log.i("测试","chatActivity");
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
