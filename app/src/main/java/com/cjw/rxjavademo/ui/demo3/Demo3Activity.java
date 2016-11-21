package com.cjw.rxjavademo.ui.demo3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cjw.rxjavademo.R;
import com.cjw.rxjavademo.ui.base.AppBarActivity;
import com.cjw.rxjavademo.widget.logRecyclerView.CommonTextRecyclerView;
import com.cjw.rxjavademo.widget.logRecyclerView.adapter.CommonTextRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Demo3Activity extends AppBarActivity implements CommonTextRecyclerViewAdapter.OnTextItemClickListener {

    private CommonTextRecyclerView mOperatorRv;
    private CommonTextRecyclerView mLogRv;

    public static void navigateTo(Context context) {
        Intent intent = new Intent(context, Demo3Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);

        findWidget();
        initWidget();
    }

    @Override
    protected void findWidget() {
        super.findWidget();
        mOperatorRv = (CommonTextRecyclerView) findViewById(R.id.operator_rv);
        mLogRv = (CommonTextRecyclerView) findViewById(R.id.log_rv);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleTv.setText("转换操作符");

        List<String> operatorList = new ArrayList<>();
        Collections.addAll(operatorList, "buffer发送", "buffer收集", "flatMap");

        mOperatorRv.addNewTextList(operatorList);
        mOperatorRv.setOnTextItemClickListener(this);
    }

    @Override
    public void onTextItemClick(UltimateRecyclerView rv, int position) {
        mLogRv.clearTextList();
        switch (position) {
            case 0: // buffer发送
                bufferDemo1();
                break;

            case 1: // buffer收集
                bufferDemo2();
                break;

            case 2:
                flatMap();
                break;

            default:
                break;
        }
    }

    private void flatMap() {
        // 参考flatmap.png

        Observable.just(100)
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(Integer integer) {
                        return Observable.just(String.valueOf(integer));
                    }
                }).flatMap(new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String s) {
                int num = Integer.parseInt(s);
                return Observable.just(num);
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                mLogRv.addText(String.valueOf(integer));
            }
        });

        // 输出结果:
        // 100
    }

    private void bufferDemo2() {
        // 打包收集

        // 每250ms发送一个数据,每600ms收集一组数据,并将每组数据进行显示
        Observable.interval(250, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .buffer(600, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<List<Long>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<Long> longs) {
                        return Observable.just(longs.toString())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                mLogRv.addText(s);
            }
        });

        // 输出结果:(只以前3组数据为例)
        // [0,1]
        // [2,3]
        // [4,5,6]

        // 分析:
        // 250 500 750 1000 1250 1500 1750 2000
        //       600      1200           1800
    }

    private void bufferDemo1() {
        // 打包发送

        final List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        // 每次打包发送四个数据
        Observable.from(list)
                .buffer(4)
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        mLogRv.addText(integers.toString());
                    }
                });

        // 输出结果:
        // [0,1,2,3]
        // [4,5,6,7]
        // [8,9]
    }
}
