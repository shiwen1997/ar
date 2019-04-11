package com.example.yun.bottomdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import android.os.Handler;
import android.os.Message;



import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import ui.ViewPagerAdapter;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment1 extends Fragment {

    private int imageIds[];
    private ArrayList<ImageView> images;
    private ArrayList<View> dots;
    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
//    private BottomNavigationView bottomNavigationView;
    ImageView mImageView1;
    ImageView mImageView2;
    ImageView mImageView3;
    ImageView mImageView4;
    private int oldPosition = 0;//记录上一次点的位置
    private int currentItem; //当前页面
    private ScheduledExecutorService scheduledExecutorService;



    public MyFragment1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_1, container, false);
//        TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
//        txt_content.setText("第一个Fragment");
//        Log.e("HEHE", "1");

//        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        //图片ID
        imageIds = new int[]{
                R.drawable.h1,
                R.drawable.h2,
                R.drawable.h3,
        };


        // BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        mImageView1 = (ImageView) view.findViewById(R.id.IV_server);
        mImageView2 = (ImageView) view.findViewById(R.id.IV_jiaotong);
        mImageView3 = (ImageView) view.findViewById(R.id.IV_jingdian);
        mImageView4 = (ImageView) view.findViewById(R.id.IV_meishi);

        mImageView1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getContext(),com.example.yun.bottomdemo.chatbot.activity.SplashActivity.class);
                startActivity(intent);
            }
        });



        //显示的图片
        images = new ArrayList<ImageView>();
        for(int i =0; i < imageIds.length; i++){
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(imageIds[i]);
            images.add(imageView);
        }
        //显示的点
        dots = new ArrayList<View>();
        dots.add(view.findViewById(R.id.dot_1));
        dots.add(view.findViewById(R.id.dot_2));
        dots.add(view.findViewById(R.id.dot_3));

        mViewPager = (ViewPager) view.findViewById(R.id.vp);

        adapter = new ViewPagerAdapter();
        mViewPager.setAdapter(adapter);

//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                // TODO Auto-generated method stub
//
//                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
//                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
//
//                oldPosition = position;
//                currentItem = position;
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//                // TODO Auto-generated method stub
//            }
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//                // TODO Auto-generated method stub
//            }
//        });
        return view;
    }










    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return images.size();
        }

        //是否是同一张图片
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            // TODO Auto-generated method stub
//            super.destroyItem(container, position, object);
//            view.removeViewAt(position);
            view.removeView(images.get(position));

        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            // TODO Auto-generated method stub
            view.addView(images.get(position));

            return images.get(position);
        }
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        //每隔2秒钟切换一张图片
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 2, 2, TimeUnit.SECONDS);
    }

    //切换图片
    private class ViewPagerTask implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            currentItem = (currentItem +1) % imageIds.length;
            //更新界面
//            handler.sendEmptyMessage(0);
            handler.obtainMessage().sendToTarget();
        }

    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //设置当前页面
            mViewPager.setCurrentItem(currentItem);
        }

    };

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }






}
