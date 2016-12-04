package com.cjw.rxjavademo.ui.demo4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

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
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 筛选操作符
 * Created by cjw on 16-11-28.
 */
public class Demo4Activity extends AppBarActivity implements CommonTextRecyclerViewAdapter.OnTextItemClickListener {

    private CommonTextRecyclerView mOperatorRv;
    private CommonTextRecyclerView mLogRv;

    public static void navigateTo(Context context) {
        Intent intent = new Intent(context, Demo4Activity.class);
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
        mTitleTv.setText("筛选操作符");

        List<String> operatorList = new ArrayList<>();
        Collections.addAll(operatorList, "deBounce", "distinct", "elementAt", "filter", "first");
        Collections.addAll(operatorList, "ignoreElements", "last");

        mOperatorRv.addNewTextList(operatorList);
        mOperatorRv.setOnTextItemClickListener(this);
    }

    @Override
    public void onTextItemClick(UltimateRecyclerView rv, int position) {
        mLogRv.clearTextList();
        switch (position) {
            case 0:
                deBounce();
                break;

            case 1:
                distinct();
                break;

            case 2:
                elementAt();
                break;

            case 3:
                filter();
                break;

            case 4:
                first();
                break;

            case 5:
                ignoreElements();
                break;

            case 6:
                last();
                break;

            default:
                break;
        }
    }

    private void last() {
        // 获取最后一个元素
        Integer[] arr = new Integer[]{0, 1, 2, 3, 4, 5};
        Observable.from(arr)
                .last()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // 5
    }

    private void ignoreElements() {
        // 忽略所有元素
        Integer[] arr = new Integer[]{0, 1, 2, 3, 4, 5};
        Observable.from(arr)
                .ignoreElements()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        mLogRv.addText("complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLogRv.addText("error");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // complete
    }

    private void first() {
        // 获取第一个元素
        Integer[] arr = new Integer[]{0, 1, 2, 3, 4, 5};
        Observable.from(arr)
                .first()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // 0
    }

    private void filter() {
        // 筛选满足条件的元素
        Integer[] arr = new Integer[]{0, 1, 2, 3, 4, 5};
        Observable.from(arr)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer % 2 == 0;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                mLogRv.addText(String.valueOf(integer));
            }
        });

        // 输出结果:
        // 0
        // 2
        // 4
    }

    private void elementAt() {
        // 获取指定位置的元素
        String[] arr = new String[]{"s0", "s1", "s2"};
        Observable.from(arr)
                .elementAt(1)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mLogRv.addText(s);
                    }
                });

        // 输出结果:
        // s1
    }

    private void distinct() {
        // 忽略相同的数据
        List<Integer> list = new ArrayList<>();
        Collections.addAll(list, 1, 2, 3, 1, 2, 3);
        Observable.from(list)
                .distinct()
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
    }

    private void deBounce() {
        // 在源Observable产生一个结果后开始计时
        // 如果在规定时间内没有新数据产生或者在调用了onCompleted,则发射数据,否则当前数据取消发射

        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    // 产生结果的间隔时间分别为100、200、300...900毫秒
                    for (int i = 1; i < 10; i++) {
                        subscriber.onNext(i);
                        SystemClock.sleep(i * 100);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // 5
        // 6
        // 7
        // 8
        // 9
    }
}
