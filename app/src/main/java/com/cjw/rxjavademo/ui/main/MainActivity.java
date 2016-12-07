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
        Collections.addAll(titleList, "筛选操作符", "组合操作符");
        MainAdapter adapter = new MainAdapter(mContext, titleList);
        adapter.setItemClickListener(this);
        mMainRv.setAdapter(adapter);
    }

    //    /**
    //     * 组合操作符
    //     */
    //    class OperatorCombination {
    //
    //        public void zip() {
    //            // zip
    //            // 使用一个指定的函数将多个Observable发射的数据组合在一起
    //            // 然后将这个函数的结果作为单项数据发射,严格周期顺序进行合并,不能单独发射
    //            // 注意:
    //            // 只有每个observable都生成一个数据时,才会统一发射一次
    //            // 当不满足所有observable都有数据可产品时,不再发射数据
    //
    //            Observable<String> observable1 = Observable.just("a1", "a2", "a3");
    //            Observable<String> observable2 = Observable.just("b1", "b2", "b3", "a4");
    //            Observable.zip(observable1, observable2, new Func2<String, String, String>() {
    //                @Override
    //                public String call(String s, String s2) {
    //                    return s + " , " + s2;
    //                }
    //            }).subscribe(new Action1<String>() {
    //                @Override
    //                public void call(String s) {
    //                    Log.d(TAG, "call: " + s);
    //                }
    //            });
    //
    //            // call: a1 , b1
    //            // call: a2 , b2
    //            // call: a3 , b3
    //        }
    //
    //    class OperatorCreate {
    //
    //        public void observerOn() {
    //            // subscribeOn
    //            // 指定subscribe运行的线程
    //            // subscribe
    //
    //            Observable.just("s1", "s2")
    //                    .subscribeOn(AndroidSchedulers.mainThread())
    //                    .map(new Func1<String, String>() {
    //                        @Override
    //                        public String call(String s) {
    //                            long currentThreadID = Thread.currentThread().getId();
    //                            if (currentThreadID == getMainLooper().getThread().getId()) {
    //                                Log.d(TAG, "in map : run on MainThread");
    //                            } else {
    //                                Log.d(TAG, "in map : not run on MainThread, current Thread id : " + currentThreadID);
    //                            }
    //                            return "new " + s;
    //                        }
    //                    })
    //                    .observeOn(Schedulers.newThread())
    //                    .subscribe(new Action1<String>() {
    //                        @Override
    //                        public void call(String s) {
    //                            Log.d(TAG, "call: " + s);
    //
    //                            long currentThreadID = Thread.currentThread().getId();
    //                            if (currentThreadID == getMainLooper().getThread().getId()) {
    //                                Log.d(TAG, "in subscribe : run on MainThread");
    //                            } else {
    //                                Log.d(TAG, "in subscribe : not run on MainThread, current Thread id : " + currentThreadID);
    //                            }
    //                        }
    //                    });
    //
    //            // in map : run on MainThread
    //            // in map : run on MainThread
    //            // call: new s1
    //            // in subscribe : not run on MainThread, current Thread id : 176
    //            // call: new s2
    //            // in subscribe : not run on MainThread, current Thread id : 176
    //        }
}
