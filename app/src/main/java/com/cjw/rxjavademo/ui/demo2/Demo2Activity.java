package com.cjw.rxjavademo.ui.demo2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cjw.rxjavademo.R;
import com.cjw.rxjavademo.ui.base.AppBarActivity;
import com.cjw.rxjavademo.widget.logRecyclerView.CommonTextRecyclerView;
import com.cjw.rxjavademo.widget.logRecyclerView.adapter.CommonTextRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;

public class Demo2Activity extends AppBarActivity implements CommonTextRecyclerViewAdapter.OnTextItemClickListener {

    private static final String TAG = "Demo2Activity";

    private CommonTextRecyclerView mOperatorRv;
    private CommonTextRecyclerView mLogRv;
    private List<String> mOperatorList;

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

        mOperatorList = new ArrayList<>();
        Collections.addAll(mOperatorList, "create", "defer", "never");

        mOperatorRv.addNewTextList(mOperatorList);
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

            default:
                break;
        }
    }

    private void never() {
        // never
        // 参考never.png

        // 创建一个Observable不发射任何数据,也不给订阅它的Observer发出任何通知

        Observable.never().subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError : " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {
                Log.d(TAG, "onNext");
            }
        });

        // 不会发送任何内容
    }

    private void defer() {
        // defer
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
        // create
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
