package com.cjw.rxjavademo.widget.logRecyclerView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.cjw.rxjavademo.widget.logRecyclerView.adapter.LogRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.List;

public class LogRecyclerView extends UltimateRecyclerView {

    private LogRecyclerViewAdapter mLogAdapter;

    public LogRecyclerView(Context context) {
        super(context);
        initView();
    }

    public LogRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LogRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setLayoutManager(manager);

        addItemDividerDecoration(getContext());

        mLogAdapter = new LogRecyclerViewAdapter(getContext());
        setAdapter(mLogAdapter);
    }

    public void addLog(String text) {
        mLogAdapter.addLog(text);
    }

    public void addNewLogList(List<String> logList) {
        mLogAdapter.addNewLogList(logList);
    }

    public void clearLogList() {
        mLogAdapter.clearLogList();
    }
}
