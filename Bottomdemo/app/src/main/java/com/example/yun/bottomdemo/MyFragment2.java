package com.example.yun.bottomdemo;

import android.app.LauncherActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Jay on 2015/8/28 0028.
 */
public class MyFragment2 extends Fragment {


    // 声明ListView控件
    private ListView mListView;
    // 声明数组链表，其装载的类型是ListItem(封装了一个Drawable和一个String的类)
    private ArrayList<ListItem> mList;
    //当前选中的行
    private int current_index;


    public MyFragment2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_2,container,false);


        // 通过findviewByID获取到ListView对象
        mListView = (ListView) view.findViewById(R.id.list_main);
        // 获取Resources对象
        Resources res = this.getResources();

        mList = new ArrayList<MyFragment2.ListItem>();

        // 初始化data，装载八组数据到数组链表mList中
        ListItem item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.fg2_nanning));
        item.setTitle("南宁");
        item.setLocationinfo("南宁，简称“邕”，别称绿城、邕城，是广西壮族自治区首府。");
        mList.add(item);


        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.fg2_add));
        item.setTitle("待添加目的地");
        mList.add(item);


//        item = new ListItem();
//        item.setImage(res.getDrawable(R.drawable.img2));
//        item.setTitle("青秀山风景区");
//        mList.add(item);
//
//        item = new ListItem();
//        item.setImage(res.getDrawable(R.drawable.img3));
//        item.setTitle("南宁动物园");
//        mList.add(item);
//
//        item = new ListItem();
//        item.setImage(res.getDrawable(R.drawable.img4));
//        item.setTitle("南宁国际会展中心");
//        mList.add(item);
//
//        item = new ListItem();
//        item.setImage(res.getDrawable(R.drawable.img5));
//        item.setTitle("大明山景区");
//        mList.add(item);
//
//        item = new ListItem();
//        item.setImage(res.getDrawable(R.drawable.img6));
//        item.setTitle("南湖公园");
//        mList.add(item);
//
//        item = new ListItem();
//        item.setImage(res.getDrawable(R.drawable.img7));
//        item.setTitle("邕江");
//        mList.add(item);
//
//        item = new ListItem();
//        item.setImage(res.getDrawable(R.drawable.img8));
//        item.setTitle("南宁博物馆");
//        mList.add(item);

        // 获取MainListAdapter对象
        MainListViewAdapter adapter = new MainListViewAdapter();

        // 将MainListAdapter对象传递给ListView视图
        mListView.setAdapter(adapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
                current_index = position;

                System.out.println(position);
                switch (position) {
                    case (0):
                        //广西大学
                        Intent intent = new Intent(getActivity(),com.example.yun.bottomdemo.fg2Activitys.GxuActivity.class);
                        startActivity(intent);
                        break;
                    case (1):
//                        Intent intent1 = new Intent(getActivity(),ui.MJWActivity.class);
//                        startActivity(intent1);
                        Toast.makeText(getContext(), "敬请期待", Toast.LENGTH_LONG).show();
                        break;
//                    case (2):
//                        Intent intent2 = new Intent(MainActivity.this, com.vuforia.VuforiaSamples.ui.ActivityList.ActivityLauncher.class);
//                        startActivity(intent2);
//                        break;
                }
            }
        });
        return view;
    }




//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }

    /**
     * 定义ListView适配器MainListViewAdapter
     */
    class MainListViewAdapter extends BaseAdapter {

        /**
         * 返回item的个数
         */
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }

        /**
         * 返回item的内容
         */
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mList.get(position);
        }

        /**
         * 返回item的id
         */
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        /**
         * 返回item的视图
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItemView listItemView;

            // 初始化item view
            if (convertView == null) {
                // 通过LayoutInflater将xml中定义的视图实例化到一个View中
                convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.item_fg2, null);

//                if (current_index == position){
//                    convertView.setBackgroundColor(getResources().getColor(R.color.light_gray));
//                }
//                else {
//                    convertView.setBackgroundColor(getResources().getColor(R.color.white));
//                }


                // 实例化一个封装类ListItemView，并实例化它的两个域
                listItemView = new ListItemView();
                listItemView.imageView = (ImageView) convertView.findViewById(R.id.image_location);
                listItemView.tv_location = (TextView) convertView .findViewById(R.id.tv_location);
                listItemView.tv_locationinfo = (TextView) convertView.findViewById(R.id.tv_locationinfo);
                // 将ListItemView对象传递给convertView
                convertView.setTag(listItemView);
            }
            else {
                // 从converView中获取ListItemView对象
                listItemView = (ListItemView) convertView.getTag();
            }

            // 获取到mList中指定索引位置的资源
            Drawable img = mList.get(position).getImage();
            String location = mList.get(position).getTitle();
            String localtioninfo = mList.get(position).getLocationinfo();
            // 将资源传递给ListItemView的两个域对象
            listItemView.imageView.setImageDrawable(img);
            listItemView.tv_location.setText(location);
            listItemView.tv_locationinfo.setText(localtioninfo);
            // 返回convertView对象
            return convertView;
        }

    }


    /**
     * 封装两个视图组件的类
     */
    class ListItemView {
        ImageView imageView;
        TextView tv_location;
        TextView tv_locationinfo;
    }

    /**
     * 封装了两个资源的类
     */
    class ListItem {
        private Drawable image;
        private String title;
        private String locationinfo;
        public String getLocationinfo() {
            return locationinfo;
        }

        public void setLocationinfo(String locationinfo) {
            this.locationinfo = locationinfo;
        }

        public Drawable getImage() {
            return image;
        }

        public void setImage(Drawable image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }






}
