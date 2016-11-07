package com.cjw.rxjavademo.ui.demo2;

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

import rx.Observable;
import rx.Subscriber;

public class Demo2Activity extends AppBarActivity implements CommonTextRecyclerViewAdapter.OnTextItemClickListener {

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
        Collections.addAll(mOperatorList, "create");

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

            default:
                break;
        }
    }

    private void create() {
        // create 操作符

        // create       创建被观察者
        // subscribe    订阅一个观察者
        

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
