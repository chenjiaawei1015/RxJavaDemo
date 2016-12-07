package com.cjw.rxjavademo.ui.demo3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cjw.rxjavademo.R;
import com.cjw.rxjavademo.ui.base.AppBarActivity;
import com.cjw.rxjavademo.utils.DateUtils;
import com.cjw.rxjavademo.widget.logrecyclerview.CommonTextRecyclerView;
import com.cjw.rxjavademo.widget.logrecyclerview.adapter.CommonTextRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
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
        Collections.addAll(operatorList, "buffer发送", "buffer收集", "flatMap", "groupBy", "map");
        Collections.addAll(operatorList, "scan", "window");

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

            case 3:
                groupBy();
                break;

            case 4:
                map();
                break;

            case 5:
                scan();
                break;

            case 6:
                window();
                break;

            default:
                break;
        }
    }

    private void window() {
        // 参考window.png
        Observable.range(0, 10)
                .window(2, TimeUnit.SECONDS, 3, AndroidSchedulers.mainThread())
                .subscribe(new Action1<Observable<Integer>>() {
                    @Override
                    public void call(Observable<Integer> integerObservable) {
                        mLogRv.addText(integerObservable.toString());
                        Log.d(TAG, integerObservable.toString());

                        integerObservable.subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                String text = integer + " : " + DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis());
                                mLogRv.addText(text);
                                Log.d(TAG, text);
                            }
                        });
                    }
                });

        // 输出结果:
        // rx.subjects.UnicastSubject@fe6846b
        // 0 : 10:52:29 539
        // 1 : 10:52:29 542
        // 2 : 10:52:29 543
        // rx.subjects.UnicastSubject@7412bc8
        // 3 : 10:52:29 545
        // 4 : 10:52:29 545
        // 5 : 10:52:29 546
        // rx.subjects.UnicastSubject@6c6c461
        // 6 : 10:52:29 547
        // 7 : 10:52:29 548
        // 8 : 10:52:29 548
        // rx.subjects.UnicastSubject@67d5b86
        // 9 : 10:52:29 549
    }

    private void scan() {
        // 参考scan.png
        Observable.just(1, 2, 3, 4, 5)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer sum, Integer item) {
                        // 第一个参数是上次的结算结果
                        // 第二个参数是当此的源observable的输入值
                        return sum + item;
                    }
                }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                mLogRv.addText("Next: " + item);
            }

            @Override
            public void onError(Throwable error) {
                mLogRv.addText("Error: " + error.getMessage());
            }

            @Override
            public void onCompleted() {
                mLogRv.addText("complete");
            }
        });

        // 输出结果:
        // Next: 1
        // Next: 3
        // Next: 6
        // Next: 10
        // Next: 15
        // complete
    }

    private void map() {
        // 参考map.png
        Observable.just(100)
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return String.valueOf(integer);
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                mLogRv.addText(s);
            }
        });

        // 输出结果:
        // 100
    }

    private void groupBy() {
        // 参考groupby.png

        List<String> dataList = new ArrayList<>();
        Collections.addAll(dataList, "10", "str1", "str2", "20", "30");

        Observable.from(dataList)
                .groupBy(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s.matches("^(\\d)+$");
                    }
                }).subscribe(new Action1<GroupedObservable<Boolean, String>>() {
            @Override
            public void call(final GroupedObservable<Boolean, String> booleanStringGroupedObservable) {
                booleanStringGroupedObservable.subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mLogRv.addText("key : " + booleanStringGroupedObservable.getKey() + " , value : " + s);
                    }
                });
            }
        });

        // 输出结果:
        // call: key : true , value : 10
        // call: key : false , value : str1
        // call: key : false , value : str2
        // call: key : true , value : 20
        // call: key : true , value : 30
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
