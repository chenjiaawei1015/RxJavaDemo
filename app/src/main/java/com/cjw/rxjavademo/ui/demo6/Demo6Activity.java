package com.cjw.rxjavademo.ui.demo6;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cjw.rxjavademo.R;
import com.cjw.rxjavademo.ui.base.AppBarActivity;
import com.cjw.rxjavademo.widget.logrecyclerview.CommonTextRecyclerView;
import com.cjw.rxjavademo.widget.logrecyclerview.adapter.CommonTextRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 错误处理操作符
 */
public class Demo6Activity extends AppBarActivity implements CommonTextRecyclerViewAdapter.OnTextItemClickListener {

    private CommonTextRecyclerView mOperatorRv;
    private CommonTextRecyclerView mLogRv;

    public static void navigateTo(Context context) {
        Intent intent = new Intent(context, Demo6Activity.class);
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
        mTitleTv.setText("错误处理操作符");

        List<String> operatorList = new ArrayList<>();
        Collections.addAll(operatorList, "onErrorReturn", "onErrorResumeNext", "onExceptionResumeNext");

        mOperatorRv.addNewTextList(operatorList);
        mOperatorRv.setOnTextItemClickListener(this);
    }

    @Override
    public void onTextItemClick(UltimateRecyclerView rv, int position) {
        mLogRv.clearTextList();
        switch (position) {
            case 0:
                onErrorReturn();
                break;

            case 1:
                onErrorResumeNext();
                break;

            case 2:
                onExceptionResumeNext();
                break;
        }
    }

    private void onExceptionResumeNext() {
        // 参看 onexceptionresumenext.png
        Observable<Integer> o1 = Observable.just(-100);

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < arr.length; i++) {
                    if (i == 3) {
                        subscriber.onError(new Exception("error"));
                    } else {
                        subscriber.onNext(arr[i]);
                    }
                }
            }
        }).onExceptionResumeNext(o1)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // 1
        // 2
        // 3
        // -100
    }

    private void onErrorResumeNext() {
        // 参考onerrorresumenext.png

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < arr.length; i++) {
                    if (i == 3) {
                        subscriber.onError(new Exception("error"));
                    } else {
                        subscriber.onNext(arr[i]);
                    }
                }
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends Integer>>() {
            @Override
            public Observable<? extends Integer> call(Throwable throwable) {
                return Observable.just(-100);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // 1
        // 2
        // 3
        // -100
    }

    private Integer[] arr = {1, 2, 3, 4, 5};

    private void onErrorReturn() {
        // 参考onerrorreturn.png

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < arr.length; i++) {
                    if (i == 3) {
                        subscriber.onError(new Exception("error"));
                    } else {
                        subscriber.onNext(arr[i]);
                    }
                }
            }
        }).onErrorReturn(new Func1<Throwable, Integer>() {
            @Override
            public Integer call(Throwable throwable) {
                return -100;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // 1
        // 2
        // 3
        // -100
    }
}
