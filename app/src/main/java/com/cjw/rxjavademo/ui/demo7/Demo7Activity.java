package com.cjw.rxjavademo.ui.demo7;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

/**
 * 效用操作符
 */
public class Demo7Activity extends AppBarActivity implements CommonTextRecyclerViewAdapter.OnTextItemClickListener {

    private CommonTextRecyclerView mOperatorRv;
    private CommonTextRecyclerView mLogRv;

    private List<Integer> mDataList;

    public static void navigateTo(Context context) {
        Intent intent = new Intent(context, Demo7Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);
        findWidget();
        initWidget();
        setListener();
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
        mTitleTv.setText("效用操作符");

        mDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mDataList.add(i);
        }

        List<String> operatorList = new ArrayList<>();
        Collections.addAll(operatorList, "delay", "doOnNext");
        mOperatorRv.addNewTextList(operatorList);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mOperatorRv.setOnTextItemClickListener(this);
    }

    @Override
    public void onTextItemClick(UltimateRecyclerView rv, int position) {
        mLogRv.clearTextList();
        switch (position) {
            case 0:
                delay();
                break;

            case 1:
                doOnNext();
                break;

            default:
                break;
        }
    }

    private void doOnNext() {
        // 参考 do_on_next.png

        Observable.just(1, 2, 3)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer item) {
                        if (item > 1) {
                            throw new RuntimeException("Item exceeds maximum value");
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
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

        // 打印结果:
        // Error: Item exceeds maximum value
    }

    private void delay() {
        // 参考delay.png
        // 延时

        String startTimeStr = "开始时间:" + DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis());
        mLogRv.addText(startTimeStr);

        Observable.just(100)
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        String endTimeStr = "开始时间:" + DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis());
                        mLogRv.addText(endTimeStr);
                    }
                });
    }
}
