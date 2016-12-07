package com.cjw.rxjavademo.ui.demo2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cjw.rxjavademo.R;
import com.cjw.rxjavademo.ui.base.AppBarActivity;
import com.cjw.rxjavademo.utils.DateUtils;
import com.cjw.rxjavademo.widget.logRecyclerView.CommonTextRecyclerView;
import com.cjw.rxjavademo.widget.logRecyclerView.adapter.CommonTextRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Demo2Activity extends AppBarActivity implements CommonTextRecyclerViewAdapter.OnTextItemClickListener {

    private static final String TAG = "Demo2Activity";

    private CommonTextRecyclerView mOperatorRv;
    private CommonTextRecyclerView mLogRv;

    public static void navigateTo(Context context) {
        Intent intent = new Intent(context, Demo2Activity.class);
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
        mTitleTv.setText("创建操作符");

        List<String> operatorList = new ArrayList<>();
        Collections.addAll(operatorList, "create", "defer", "never", "empty", "error");
        Collections.addAll(operatorList, "from", "interval", "just", "range", "repeat");
        Collections.addAll(operatorList, "timer");

        mOperatorRv.addNewTextList(operatorList);
        mOperatorRv.setOnTextItemClickListener(this);
    }

    @Override
    public void onTextItemClick(UltimateRecyclerView rv, int position) {
        mLogRv.clearTextList();
        switch (position) {
            case 0:
                create();
                break;

            case 1:
                defer();
                break;

            case 2:
                never();
                break;

            case 3:
                empty();
                break;

            case 4:
                error();
                break;

            case 5:
                from();
                break;

            case 6:
                interval();
                break;

            case 7:
                just();
                break;

            case 8:
                range();
                break;

            case 9:
                repeat();
                break;

            case 10:
                timer();
                break;

            default:
                break;
        }
    }

    private void timer() {
        // 参考timer.png

        // 会在指定时间后发射一个Long类型数字

        Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
                .repeat(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mLogRv.addText(DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis()));
                    }
                });

        // 输出结果
        // 18:43:20
        // 18:43:21
        // 18:43:22
    }

    private void repeat() {
        // 参考repeat.png

        // 会将一个Observable对象重复发射,接收值是发射的次数

        Observable.just(DateUtils.getSimpleHourMinuteSecond(System.currentTimeMillis()))
                .repeat(3, AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mLogRv.addText(s);
                    }
                });

        // 输出结果:
        // 18:00:00
        // 18:00:00
        // 18:00:00
    }

    private void range() {
        // 参考range.png

        // 根据输入的初始值和指定数量次,发射大于等于初始值的值

        Observable.range(10, 3, AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // 10
        // 11
        // 12
    }

    private void just() {
        // 参考just.png

        Observable.just(1, 2, 3)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        mLogRv.addText("next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        mLogRv.addText("error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        mLogRv.addText("completed");
                    }
                });

        // 输出结果:
        // next: 1
        // next: 2
        // next: 3
        // completed

        // just与from的区别:
        List<String> dataList = new ArrayList<>();
        Collections.addAll(dataList, "str1", "str2", "str3");
        Observable.from(dataList)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        // 获取的是每个字符串
                    }
                });

        Observable.just(dataList)
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        // 获取的时字符串集合
                    }
                });
    }

    private int mTestCount;

    private void interval() {
        // 参考interval.png
        // 所创建的Observable对象会从0开始,每隔固定的时间发射一个数字

        mTestCount = 0;
        mLogRv.addText("Start Time : " + DateUtils.getSimpleHourMinuteSecond(System.currentTimeMillis()));
        // 先等待2秒,后每1秒调用一次,直到结束为止
        Observable.interval(2, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMap(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long num) {
                        mTestCount++;
                        if (mTestCount >= 5) {
                            return Observable.empty();
                        } else {
                            return Observable.just(num);
                        }
                    }
                }).subscribe(new Action1<Long>() {
            @Override
            public void call(Long num) {
                String text = "num = " + num + ", time = " + DateUtils.getSimpleHourMinuteSecond(System.currentTimeMillis());
                mLogRv.addText(text);
            }
        });

        // 输出结果:
        // Start Time : 10:12:06
        // num = 0, time = 10:12:08
        // num = 1, time = 10:12:09
        // num = 2, time = 10:12:10
        // num = 3, time = 10:12:11
    }

    private void from() {
        // 参考from.png

        // 用来将某个对象转化为Observable对象,并且依次将其内容发射出去,from的接收值可以是集合或者数组

        Integer[] items = {0, 1, 2, 3, 4, 5};
        Observable<Integer> myObservable = Observable.from(items);
        myObservable.subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer item) {
                        mLogRv.addText(String.valueOf(item));
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                        mLogRv.addText("error : " + error.getMessage());
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        mLogRv.addText("complete");
                    }
                }
        );

        // 输出结果:
        // 0
        // 1
        // 2
        // 3
        // 4
        // 5
        // complete
    }

    private void error() {
        // 参考pic_throw.png

        // 返回一个Observable,当有Observer订阅它时直接调用Observer的onError方法终止

        Observable.error(new Exception("my Exception")).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                mLogRv.addText("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                mLogRv.addText("onError : " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {
                mLogRv.addText("onNext");
            }
        });

        // 输出结果:
        // onError : my Exception
    }

    private void empty() {
        // 参考empty.png

        // 创建一个Observable不发射任何数据,而是立即调用onCompleted方法终止

        Observable<Object> observable = Observable.empty();
        observable.subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                mLogRv.addText("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                mLogRv.addText("onError : " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {
                mLogRv.addText("onNext");
            }
        });

        // 输出结果:
        // onCompleted
    }

    private void never() {
        // 参考never.png

        // 创建一个Observable不发射任何数据,也不给订阅它的Observer发出任何通知

        Observable.never().subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                mLogRv.addText("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                mLogRv.addText("onError : " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {
                mLogRv.addText("onNext");
            }
        });

        // 不会发送任何内容
    }

    private void defer() {
        // 参考 defer.png

        // 只有当有Subscriber来订阅的时候才会创建一个新的Observable对象,也就是说每次订阅都会得到一个刚创建的最新的Observable对象
        // 这可以确保Observable对象里的数据是最新的

        // 而just则没有创建新的Observable对象
        Observable<String> defer = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                Object obj = new Object();
                return Observable.just("defer : hashcode = " + obj.hashCode());
            }
        });

        Observable<String> create = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Object obj = new Object();
                subscriber.onNext("create : hashcode = " + obj.hashCode());
            }
        });

        Observable<String> just = Observable.just("just : hashcode = " + new Object().hashCode());

        Action1<String> act1 = new Action1<String>() {
            @Override
            public void call(String s) {
                mLogRv.addText(s);
                Log.d(TAG, "call: " + s);
            }
        };

        defer.subscribe(act1);
        defer.subscribe(act1);
        defer.subscribe(act1);

        create.subscribe(act1);
        create.subscribe(act1);
        create.subscribe(act1);

        just.subscribe(act1);
        just.subscribe(act1);
        just.subscribe(act1);

        // 输出结果:
        // call: defer : hashcode = 49629951
        // call: defer : hashcode = 260362444
        // call: defer : hashcode = 217267989
        // call: create : hashcode = 553514
        // call: create : hashcode = 215848987
        // call: create : hashcode = 244592056
        // call: just : hashcode = 123556382
        // call: just : hashcode = 123556382
        // call: just : hashcode = 123556382
    }

    private void create() {
        // 参考 create.png

        // create       创建被观察者
        // subscribe    观察者订阅被观察者
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> observer) {
                try {
                    // 观察者observer是否被取消订阅
                    for (int i = 1; i < 5; i++) {
                        if (!observer.isUnsubscribed()) {
                            observer.onNext(i);
                            if (i == 2) {
                                // 取消订阅
                                observer.unsubscribe();
                            }
                        }
                    }
                } catch (Exception e) {
                    observer.onError(e);
                } finally {
                    observer.onCompleted();
                }
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
                mLogRv.addText("Sequence complete.");
            }
        });

        // 输出结果:
        // Next: 1
        // Next: 2
        // Sequence complete.
    }
}
