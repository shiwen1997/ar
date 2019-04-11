package com.example.yun.bottomdemo.fg2Activitys;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yun.bottomdemo.R;
import com.example.yun.bottomdemo.TestFragmentActivity;

import java.util.ArrayList;

import ui.TestFragment;


public class GxuActivity extends AppCompatActivity {

    // 声明ListView控件
    private ListView mListView;
    // 声明数组链表，其装载的类型是ListItem(封装了一个Drawable和一个String的类)
    private ArrayList<ListItem> mList;
    //当前选中的行
    private int current_index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gxu);

        //返回上一页面
        Button bt_gofg2=findViewById(R.id.finish_gxu);
        bt_gofg2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });


        // 通过findviewByID获取到ListView对象
        mListView = (ListView) findViewById(R.id.list_gxu);

        // 获取Resources对象
        Resources res = this.getResources();

        mList = new ArrayList<GxuActivity.ListItem>();

        // 初始化data，装载八组数据到数组链表mList中
        ListItem item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.img1));
        item.setScen("广西大学石碑");
        item.setSceninfo("最让西大学子不舍的是这块石碑，上面四个大字由毛泽东主席在1952年亲笔题写，全国只有十六所高校获此殊荣。");
        mList.add(item);

        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.img2));
        item.setScen("马君武雕像");
        item.setSceninfo("中国近代获得德国工学博士第一人，政治活动家、教育家。广西大学的创建人和首任校长。");
        mList.add(item);

        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.img3));
        item.setScen("广西大学图书馆");
        mList.add(item);

        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.img4));
        item.setScen("汇学堂");
        mList.add(item);

        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.img5));
        item.setScen("西体育馆");
        mList.add(item);

        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.img6));
        item.setScen("广西大学正门");
        mList.add(item);

        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.img7));
        item.setScen("大礼堂");
        mList.add(item);

        item = new ListItem();
        item.setImage(res.getDrawable(R.drawable.img8));
        item.setScen("综合楼");
        mList.add(item);

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
                        Intent intent = new Intent(GxuActivity.this,ui.DMSBActivity.class);
                        startActivity(intent);
                        break;
                    case (1):
                        Intent intent1 = new Intent(GxuActivity.this,ui.MJWActivity.class);
                        startActivity(intent1);
                        break;
//                    case (2):
//                        Intent intent2 = new Intent(MainActivity.this, com.vuforia.VuforiaSamples.ui.ActivityList.ActivityLauncher.class);
//                        startActivity(intent2);
//                        break;
                }
            }
        });
    }


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
                convertView = LayoutInflater.from(GxuActivity.this).inflate(R.layout.item_scenery, null);
                listItemView = new ListItemView();
                listItemView.imageView = (ImageView) convertView.findViewById(R.id.image_scenery);
                listItemView.tv_scen = (TextView) convertView.findViewById(R.id.location_scenery);
                listItemView.tv_sceninfo = (TextView) convertView.findViewById(R.id.info_scenerty);
                listItemView.check_ar= (TextView) convertView.findViewById(R.id.check_ar);
                listItemView.check_sound= (TextView) convertView.findViewById(R.id.check_sound);
                // 将ListItemView对象传递给convertView
                convertView.setTag(listItemView);
            }
            else {
                // 从converView中获取ListItemView对象
                listItemView = (ListItemView) convertView.getTag();
            }
            // 获取到mList中指定索引位置的资源
            Drawable img = mList.get(position).getImage();
            String scen = mList.get(position).getScen();
            String sceninfo = mList.get(position).getSceninfo();
            // 将资源传递给ListItemView的两个域对象
            listItemView.imageView.setImageDrawable(img);
            listItemView.tv_scen.setText(scen);
            listItemView.tv_sceninfo.setText(sceninfo);
            // 返回convertView对象
            return convertView;
        }
    }
    /**
     * 封装两个视图组件的类
     */
    class ListItemView {
        ImageView imageView;
        TextView tv_scen;
        TextView tv_sceninfo;
        TextView check_ar;
        TextView check_sound;
    }
    /**
     * 封装了两个资源的类
     */
    class ListItem {
        private Drawable image;

        public String getScen() {
            return scen;
        }

        public void setScen(String scen) {
            this.scen = scen;
        }

        public String getSceninfo() {
            return sceninfo;
        }

        public void setSceninfo(String sceninfo) {
            this.sceninfo = sceninfo;
        }

        private String scen;
        private String sceninfo;


        public Drawable getImage() {
            return image;
        }
        public void setImage(Drawable image) {
            this.image = image;
        }
//        public String getTitle() {
//            return title;
//        }
//        public void setTitle(String title) {
//            this.title = title;
//        }

    }
}

