package com.cjw.rxjavademo.ui.demo1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cjw.rxjavademo.R;
import com.cjw.rxjavademo.ui.base.AppBarActivity;
import com.cjw.rxjavademo.widget.logRecyclerView.CommonTextRecyclerView;

import java.util.Observable;
import java.util.Observer;

/**
 * 简单的观察者设计模式
 */
public class Demo1Activity extends AppBarActivity {

    private CommonTextRecyclerView mLogRv;

    public static void navigateTo(Context context) {
        Intent intent = new Intent(context, Demo1Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        findWidget();
        initWidget();
    }

    @Override
    protected void findWidget() {
        super.findWidget();
        mLogRv = (CommonTextRecyclerView) findViewById(R.id.log_rv);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleTv.setText("简单的观察者设计模式");

        // 创建观察者及被观察者
        MyObservable observable = new MyObservable();
        MyObserver observer1 = new MyObserver();
        MyObserver observer2 = new MyObserver();

        // 添加观察者对象
        observable.addObserver(observer1);
        observable.addObserver(observer2);

        // 被观察的数据发生变化,通知观察者数据更新
        observable.updateData(1);
        observable.updateData(2);
        observable.updateData(3);

        // 最后显示的结果为:
        // 显示了6条数据
    }

    /**
     * 在观察者模式中充当被观察者<br/>
     * 被观察者对象需要继承Observable
     */
    private class MyObservable extends Observable {

        void updateData(int data) {
            setChanged();
            notifyObservers(data);
        }

    }

    /**
     * 在观察者模式中充当观察者<br/>
     * 观察者对象需要实现Observer对象
     */
    private class MyObserver implements Observer {

        // 有被观察者发生变化,自动调用对应观察者的update方法
        @Override
        public void update(Observable o, Object arg) {
            mLogRv.addText(String.valueOf((int) arg));
        }
    }
}
