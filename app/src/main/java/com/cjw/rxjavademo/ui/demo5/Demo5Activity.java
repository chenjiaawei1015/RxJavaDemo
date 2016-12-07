package com.cjw.rxjavademo.ui.demo5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 组合操作符
 */
public class Demo5Activity extends AppBarActivity implements CommonTextRecyclerViewAdapter.OnTextItemClickListener {

    private CommonTextRecyclerView mOperatorRv;
    private CommonTextRecyclerView mLogRv;

    public static void navigateTo(Context context) {
        Intent intent = new Intent(context, Demo5Activity.class);
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
        mTitleTv.setText("组合操作符");

        List<String> operatorList = new ArrayList<>();
        Collections.addAll(operatorList, "combineLatest", "join", "merge", "startWith", "switchOnNext", "zip");

        mOperatorRv.addNewTextList(operatorList);
        mOperatorRv.setOnTextItemClickListener(this);
    }

    @Override
    public void onTextItemClick(UltimateRecyclerView rv, int position) {
        mLogRv.clearTextList();
        switch (position) {
            case 0:
                combineLatest();
                break;

            case 1:
                join();
                break;

            case 2:
                merge();
                break;

            case 3:
                startWith();
                break;

            case 4:
                switchOnNext();
                break;

            case 5:
                zip();
                break;

            default:
                break;
        }
    }

    private void zip() {
        // 参考 zip1.png 和 zip2.png

        // 使用一个指定的函数将多个Observable发射的数据组合在一起
        // 然后将这个函数的结果作为单项数据发射,严格周期顺序进行合并,不能单独发射
        
        Observable<String> observable1 = Observable.just("a1", "a2", "a3");
        Observable<String> observable2 = Observable.just("b1", "b2", "b3", "a4");
        Observable.zip(observable1, observable2, new Func2<String, String, String>() {
            @Override
            public String call(String s, String s2) {
                return s + " , " + s2;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "call: " + s);
                        mLogRv.addText(s);
                    }
                });

        // call: a1 , b1
        // call: a2 , b2
        // call: a3 , b3
    }

    private void switchOnNext() {
        // 参考switchonnext.png

        String startTimeStr = DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis());
        Log.d(TAG, "startTime: " + startTimeStr);
        mLogRv.addText(startTimeStr);

        Observable<Observable<String>> observable = Observable.interval(2000, 500, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Observable<String>>() {
                    @Override
                    public Observable<String> call(final Long aLongOutside) {
                        // 每隔300ms产生一组数据（0,1,2,3,4)
                        return Observable.interval(0, 300, TimeUnit.MILLISECONDS)
                                .map(new Func1<Long, String>() {
                                    @Override
                                    public String call(Long aLongInside) {
                                        return "aLongOutside = " + aLongOutside + " , aLongInside = "
                                                + aLongInside + " , time : " + DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis());
                                    }
                                }).take(5);
                    }
                }).take(2);

        Observable.switchOnNext(observable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "call: " + s);
                        mLogRv.addText(s);
                    }
                });

        // 输出结果:
        // startTime: 17:11:58 226
        // call: aLongOutside = 0 , aLongInside = 0 , time : 17:12:00 248
        // call: aLongOutside = 0 , aLongInside = 1 , time : 17:12:00 545
        // call: aLongOutside = 1 , aLongInside = 0 , time : 17:12:00 744
        // call: aLongOutside = 1 , aLongInside = 1 , time : 17:12:01 043
        // call: aLongOutside = 1 , aLongInside = 2 , time : 17:12:01 344
        // call: aLongOutside = 1 , aLongInside = 3 , time : 17:12:01 644
        // call: aLongOutside = 1 , aLongInside = 4 , time : 17:12:01 945
    }

    private void startWith() {
        // 参考 startwith.png
        Observable<Integer> o1 = Observable.just(1, 2, 3);
        Observable<Integer> o2 = Observable.just(4, 5, 6);

        o2.startWith(o1)
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
        // 4
        // 5
        // 6
    }

    private void merge() {
        // 参考 merge.png

        Observable<Integer> o1 = Observable.just(1, 3, 5);
        Observable<Integer> o2 = Observable.just(2, 4, 6);

        Observable.merge(o1, o2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mLogRv.addText(String.valueOf(integer));
                    }
                });

        // 输出结果:
        // 1
        // 3
        // 5
        // 2
        // 4
        // 6
    }

    private void join() {
        // 参考join.png
        // 如果一个Observable发射了一个数据项,只要在另一个Observable发射的数据项定义的时间窗口内,就将两个Observable发射的数据合并发射

        String startTimeStr = "start Time = " + DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis());
        mLogRv.addText(startTimeStr);
        Log.d(TAG, startTimeStr);

        Observable<Long> ob1 = Observable.interval(300, TimeUnit.MILLISECONDS);
        Observable<Long> ob2 = Observable.interval(200, 500, TimeUnit.MILLISECONDS);
        ob1.join(ob2, new Func1<Long, Observable<String>>() {
            @Override
            public Observable<String> call(Long aLong) {
                return Observable.just(String.valueOf(aLong)).delay(100, TimeUnit.MILLISECONDS);
            }
        }, new Func1<Long, Observable<String>>() {
            @Override
            public Observable<String> call(Long aLong) {
                return Observable.just(String.valueOf(aLong)).delay(100, TimeUnit.MILLISECONDS);
            }
        }, new Func2<Long, Long, String>() {
            @Override
            public String call(Long num1, Long num2) {
                return "num1 = " + num1 + ", num2 = " + num2 + ", time = " + DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mLogRv.addText(s);
                        Log.d(TAG, s);
                    }
                });

        // 输出结果:(以前6组为例)
        // start Time = 09:51:46 754
        // num1 = 0, num2 = 0, time = 09:51:47 165
        // num1 = 3, num2 = 2, time = 09:51:48 063
        // num1 = 5, num2 = 3, time = 09:51:48 661
        // num1 = 8, num2 = 5, time = 09:51:49 563
        // num1 = 10, num2 = 6, time = 09:51:50 161
        // num1 = 13, num2 = 8, time = 09:51:51 063
    }

    private void combineLatest() {
        // 参考combineLatest.png

        mLogRv.addText("start Time = " + DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis()));
        Observable<Long> ob1 = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> ob2 = Observable.interval(500, 1000, TimeUnit.MILLISECONDS);
        Observable.combineLatest(ob1, ob2, new Func2<Long, Long, String>() {
            @Override
            public String call(Long num1, Long num2) {
                return "num1 = " + num1 + ", num2 = " + num2 + " , time = " + DateUtils.getSimpleHourMinuteSecondMillis(System.currentTimeMillis());
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mLogRv.addText(String.valueOf(s));
                    }
                });

        // 打印结果:(以前5组为例)
        // start Time = 09:41:56 190
        // num1 = 0, num2 = 0 , time = 09:41:57 275     // 第一个数据的发送必须保证每个Observable至少发送了一个数据
        // num1 = 0, num2 = 1 , time = 09:41:57 777
        // num1 = 1, num2 = 1 , time = 09:41:58 272
        // num1 = 1, num2 = 2 , time = 09:41:58 776
        // num1 = 2, num2 = 2 , time = 09:41:59 272
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clear, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_menu:
                mLogRv.clearTextList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
