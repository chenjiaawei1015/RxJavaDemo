package com.cjw.rxjavademo.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.cjw.rxjavademo.R;
import com.cjw.rxjavademo.ui.base.AppBarActivity;
import com.cjw.rxjavademo.ui.demo1.Demo1Activity;
import com.cjw.rxjavademo.ui.demo2.Demo2Activity;
import com.cjw.rxjavademo.ui.demo3.Demo3Activity;
import com.cjw.rxjavademo.ui.demo4.Demo4Activity;
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
        Collections.addAll(titleList, "简单的观察者设计模式", "创建操作符", "转换类操作符", "筛选操作符");
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
    //        public void switchOnNext() {
    //            // switchOnNext
    //            // 把一组Observable转换成一个Observable,如果在同一个时间内产生两个或多个Observable产生的数据,只发射最后一个Observable产生的数据
    //
    //            Log.d(TAG, "startTime: " + System.currentTimeMillis());
    //            Observable<Observable<String>> observable = Observable.interval(2000, 500, TimeUnit.MILLISECONDS).map(new Func1<Long, Observable<String>>() {
    //                @Override
    //                public Observable<String> call(final Long aLongOutside) {
    //                    // 每隔300ms产生一组数据（0,1,2,3,4)
    //                    return Observable.interval(0, 300, TimeUnit.MILLISECONDS).map(new Func1<Long, String>() {
    //                        @Override
    //                        public String call(Long aLongInside) {
    //                            return "aLongOutside = " + aLongOutside + " , aLongInside = " + aLongInside + " , time : " + System.currentTimeMillis();
    //                        }
    //                    }).take(5);
    //                }
    //            }).take(2);
    //
    //            Observable.switchOnNext(observable).subscribe(new Action1<String>() {
    //                @Override
    //                public void call(String s) {
    //                    Log.d(TAG, "call: " + s);
    //                }
    //            });
    //
    //            // startTime: 1476949745501
    //            // call: aLongOutside = 0 , aLongInside = 0 , time : 1476949747516
    //            // call: aLongOutside = 0 , aLongInside = 1 , time : 1476949747765
    //            // call: aLongOutside = 1 , aLongInside = 0 , time : 1476949748015
    //            // call: aLongOutside = 1 , aLongInside = 1 , time : 1476949748263
    //            // call: aLongOutside = 1 , aLongInside = 2 , time : 1476949748513
    //            // call: aLongOutside = 1 , aLongInside = 3 , time : 1476949748764
    //        }
    //
    //        public void startWith() {
    //            // startWith
    //            // 在源Observable发射数据之前,先发射一个指定的数据序列或数据项
    //
    //            Observable.just(4, 5, 6).startWith(1, 2, 3).subscribe(new Action1<Integer>() {
    //                @Override
    //                public void call(Integer integer) {
    //                    Log.d(TAG, "call: " + integer);
    //                }
    //            });
    //
    //            // call: 1
    //            // call: 2
    //            // call: 3
    //            // call: 4
    //            // call: 5
    //            // call: 6
    //        }
    //
    //        public void merge() {
    //            // merge
    //            // 将两个Observable发射的数据按照时间顺序进行组合,合并成一个Observable进行发射
    //
    //            Log.d(TAG, "startTime: " + System.currentTimeMillis());
    //            Observable<String> observable1 = Observable.interval(0, 500, TimeUnit.MILLISECONDS)
    //                    .take(3).flatMap(new Func1<Long, Observable<String>>() {
    //                        @Override
    //                        public Observable<String> call(Long aLong) {
    //                            return Observable.just("o1: " + aLong + " , time : " + System.currentTimeMillis());
    //                        }
    //                    });
    //
    //            Observable<String> observable2 = Observable.interval(500, 500, TimeUnit.MILLISECONDS)
    //                    .take(3).flatMap(new Func1<Long, Observable<String>>() {
    //                        @Override
    //                        public Observable<String> call(Long aLong) {
    //                            return Observable.just("o1: " + aLong + " , time : " + System.currentTimeMillis());
    //                        }
    //                    });
    //
    //            Observable.merge(observable1, observable2)
    //                    .subscribe(new Action1<String>() {
    //                        @Override
    //                        public void call(String s) {
    //                            Log.d(TAG, "call: " + s);
    //                        }
    //                    });
    //
    //            // startTime: 1476946676174
    //            // call: o1: 0 , time : 1476946676177
    //            // call: o1: 0 , time : 1476946676680
    //            // call: o1: 1 , time : 1476946676680
    //            // call: o1: 1 , time : 1476946677175
    //            // call: o1: 2 , time : 1476946677176
    //            // call: o1: 2 , time : 1476946677675
    //        }
    //
    //        public void join() {
    //            // join
    //            // 无论何时,如果一个Observable发射了一个数据项,只要在另一个Observable发射的数据项定义的时间窗口内,就将两个Observable发射的数据合并发射
    //            // 注意:
    //            // 只有当两个observable都发射过第一项数据时,并且符合上述条件,才能进行发射
    //
    //            Observable<Long> observable1 = Observable.interval(2000, 1000, TimeUnit.MILLISECONDS).take(2);
    //            Observable<Long> observable2 = Observable.interval(2500, 1000, TimeUnit.MILLISECONDS).take(2);
    //
    //            Log.d(TAG, "startTime: " + System.currentTimeMillis());
    //            observable1.join(observable2,
    //                    new Func1<Long, Observable<Long>>() {
    //                        @Override
    //                        public Observable<Long> call(Long aLong) {
    //                            Log.d(TAG, "o1: " + aLong + " , time : " + System.currentTimeMillis());
    //                            return Observable.just(aLong).delay(100, TimeUnit.MILLISECONDS);
    //                        }
    //                    },
    //                    new Func1<Long, Observable<Long>>() {
    //                        @Override
    //                        public Observable<Long> call(Long aLong) {
    //                            Log.d(TAG, "o2: " + aLong + " , time : " + System.currentTimeMillis());
    //                            return Observable.just(aLong).delay(100, TimeUnit.MILLISECONDS);
    //                        }
    //                    },
    //                    new Func2<Long, Long, Long>() {
    //                        @Override
    //                        public Long call(Long aLong1, Long aLong2) {
    //                            Log.d(TAG, "aLong1 = [" + aLong1 + "], aLong2 = [" + aLong2 + "]" + " , time : " + System.currentTimeMillis());
    //                            return aLong1 + aLong2;
    //                        }
    //                    }
    //            ).subscribe(new Subscriber<Long>() {
    //                @Override
    //                public void onCompleted() {
    //                    Log.d(TAG, "onCompleted");
    //                }
    //
    //                @Override
    //                public void onError(Throwable e) {
    //                    // do nothing
    //                }
    //
    //                @Override
    //                public void onNext(Long aLong) {
    //                    Log.d(TAG, "onNext: " + aLong + " , time : " + System.currentTimeMillis());
    //                }
    //            });
    //
    //            // startTime: 1476945902410
    //            // o1: 0 , time : 1476945904414
    //            // o2: 0 , time : 1476945904914
    //            // aLong1 = [0], aLong2 = [0] , time : 1476945904914
    //            // onNext: 0 , time : 1476945904914
    //            // o1: 1 , time : 1476945905412
    //            // aLong1 = [1], aLong2 = [0] , time : 1476945905412
    //            // onNext: 1 , time : 1476945905412
    //            // o2: 1 , time : 1476945905914
    //            // aLong1 = [1], aLong2 = [1] , time : 1476945905914
    //            // onNext: 2 , time : 1476945905914
    //            // onCompleted
    //        }
    //
    //        public void combineLatest() {
    //            // combineLatest
    //            // 当两个Observables中的任何一个发射了一个数据时,将两个Observables数据通过指定的规则进行处理,将结果进行发射
    //            // 注意:
    //            // 只有当两个observable都发射过第一项数据时,才能进行组合发射
    //            // 当每一个observable都发射首个数据后,在此发射任何数据都会找相应的另外一个observable的最新数据进行组合发射
    //
    //            Observable<Long> observable1 = Observable.interval(0, 300, TimeUnit.MILLISECONDS).take(4);
    //            Observable<Long> observable2 = Observable.interval(500, 250, TimeUnit.MILLISECONDS).take(3);
    //
    //            Log.d(TAG, "startTime: " + System.currentTimeMillis());
    //            Observable.combineLatest(observable1, observable2, new Func2<Long, Long, Long>() {
    //                @Override
    //                public Long call(Long aLong1, Long aLong2) {
    //                    Log.d(TAG, "aLong1 = [" + aLong1 + "], aLong2 = [" + aLong2 + "], time = [" + System.currentTimeMillis() + "]");
    //                    return aLong1 + aLong2;
    //                }
    //            }).subscribe(new Action1<Long>() {
    //                @Override
    //                public void call(Long aLong) {
    //                    Log.d(TAG, "call: " + aLong);
    //                }
    //            });
    //
    //            // startTime: 1476843262324
    //            // aLong1 = [1], aLong2 = [0], time = [1476843262824]
    //            // call: 1
    //            // aLong1 = [2], aLong2 = [0], time = [1476843262925]
    //            // call: 2
    //            // aLong1 = [2], aLong2 = [1], time = [1476843263075]
    //            // call: 3
    //            // aLong1 = [3], aLong2 = [1], time = [1476843263224]
    //            // call: 4
    //            // aLong1 = [3], aLong2 = [2], time = [1476843263324]
    //            // call: 5
    //
    //            // 分析:
    //            // observable1 打印时间分别为:  0ms    300ms  600ms  900ms
    //            // observable2 打印时间分别为:  500ms  750ms  900ms  1000ms
    //            // 只有当两个observable都发射过第一项数据时,才能进行组合发射
    //            // 第1次发射时间: 500ms   a1 = 1 , a2 = 0
    //            // 第2次发射时间: 600ms   a1 = 2 , a2 = 0
    //            // 第3次发射时间: 750ms   a1 = 2 , a2 = 1
    //            // 第4次发射时间: 900ms   a1 = 3 , a2 = 1
    //            // 第5次发射时间: 1000ms  a1 = 3 , a2 = 2
    //        }
    //
    //    }
    //
    //    /**
    //     * 过滤操作符
    //     */
    //    class OperatorFilter {
    //
    //        public void takeLast() {
    //            // takeLast
    //            // 与skipLast用法相反,只保留后面的n项数据进行发射,而忽略前面的结果
    //
    //            Observable.just(1, 2, 3, 4, 5)
    //                    .takeLast(2)
    //                    .subscribe(new Action1<Integer>() {
    //                        @Override
    //                        public void call(Integer integer) {
    //                            Log.d(TAG, "call: " + integer);
    //                        }
    //                    });
    //
    //            // call: 4
    //            // call: 5
    //        }
    //
    //        public void take() {
    //            // take
    //            // 与skip用法相反,保留前面的n项数据进行发射,而忽略后面的结果
    //
    //            Observable.just(1, 2, 3, 4, 5)
    //                    .take(2)
    //                    .subscribe(new Action1<Integer>() {
    //                        @Override
    //                        public void call(Integer integer) {
    //                            Log.d(TAG, "call: " + integer);
    //                        }
    //                    });
    //
    //            // call: 1
    //            // call: 2
    //        }
    //
    //        public void skipLast() {
    //            // skipLast
    //            // 跳过后面的n项数据不进行处理
    //
    //            Observable.just(1, 2, 3, 4, 5)
    //                    .skipLast(2)
    //                    .subscribe(new Action1<Integer>() {
    //                        @Override
    //                        public void call(Integer integer) {
    //                            Log.d(TAG, "call: " + integer);
    //                        }
    //                    });
    //
    //            // call: 1
    //            // call: 2
    //            // call: 3
    //        }
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
