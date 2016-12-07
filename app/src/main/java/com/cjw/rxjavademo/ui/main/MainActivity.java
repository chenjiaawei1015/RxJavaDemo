package com.cjw.rxjavademo.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.cjw.rxjavademo.R;
import com.cjw.rxjavademo.ui.base.AppBarActivity;
import com.cjw.rxjavademo.ui.demo1.Demo1Activity;
import com.cjw.rxjavademo.ui.demo2.Demo2Activity;
import com.cjw.rxjavademo.ui.demo3.Demo3Activity;
import com.cjw.rxjavademo.ui.demo4.Demo4Activity;
import com.cjw.rxjavademo.ui.demo5.Demo5Activity;
import com.cjw.rxjavademo.ui.demo6.Demo6Activity;
import com.cjw.rxjavademo.ui.main.adapter.MainAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppBarActivity implements MainAdapter.OnItemClickListener {

    private UltimateRecyclerView mMainRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findWidget();
        initWidget();
    }

    @Override
    public void onAdapterItemClick(int position) {
        switch (position) {
            case 0: // 简单的观察者设计模式
                Demo1Activity.navigateTo(mContext);
                break;

            case 1: // 创建类操作符
                Demo2Activity.navigateTo(mContext);
                break;

            case 2: // 转换类操作符
                Demo3Activity.navigateTo(mContext);
                break;

            case 3: // 筛选操作符
                Demo4Activity.navigateTo(mContext);
                break;

            case 4: // 组合操作符
                Demo5Activity.navigateTo(mContext);
                break;

            case 5: // 错误处理操作符
                Demo6Activity.navigateTo(mContext);
                break;

            default:
                break;
        }
    }

    @Override
    protected void findWidget() {
        super.findWidget();
        mMainRv = (UltimateRecyclerView) findViewById(R.id.main_rv);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleTv.setText("首页");
        hideBackIv();

        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mMainRv.setLayoutManager(manager);
        mMainRv.addItemDividerDecoration(mContext);

        List<String> titleList = new ArrayList<>();
        Collections.addAll(titleList, "简单的观察者设计模式", "创建操作符", "转换类操作符");
        Collections.addAll(titleList, "筛选操作符", "组合操作符", "错误处理操作符");
        MainAdapter adapter = new MainAdapter(mContext, titleList);
        adapter.setItemClickListener(this);
        mMainRv.setAdapter(adapter);
    }
}
