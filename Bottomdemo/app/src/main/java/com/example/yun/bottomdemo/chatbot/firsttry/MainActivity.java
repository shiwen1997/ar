//package com.example.yun.bottomdemo.chatbot.firsttry;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//
//import com.example.sw.myrobot0322.R;
//import com.example.sw.myrobot0322.activity.SplashActivity;
//
//public class MainActivity extends AppCompatActivity {
//
//
////    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
////        getSupportActionBar().hide();//--隐藏顶部动作栏
//        Button buttonGoFirst=(Button) findViewById(R.id.go_1);
//        buttonGoFirst.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                Intent intent = new Intent(MainActivity.this,FirstTryActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        Button buttonGoFlash=(Button) findViewById(R.id.go_splash);
//        buttonGoFlash.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//                Intent intent = new Intent(MainActivity.this,SplashActivity.class);
//                startActivity(intent);
//            }
//        });
//
//
//
//        //设置顶部状态栏透明
//        StatusBarUtils.setWindowStatusBarColor(this, android.R.color.darker_gray);
//
//    }
//}
