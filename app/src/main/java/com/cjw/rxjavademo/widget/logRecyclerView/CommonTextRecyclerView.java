package com.cjw.rxjavademo.widget.logRecyclerView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.cjw.rxjavademo.widget.logRecyclerView.adapter.CommonTextRecyclerViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.List;

public class CommonTextRecyclerView extends UltimateRecyclerView {

    private CommonTextRecyclerViewAdapter mCommonAdapter;

    public CommonTextRecyclerView(Context context) {
        super(context);
        initView();
    }

    public CommonTextRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CommonTextRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setLayoutManager(manager);

        addItemDividerDecoration(getContext());

        mCommonAdapter = new CommonTextRecyclerViewAdapter(getContext());
        setAdapter(mCommonAdapter);
    }

    public void addText(String text) {
        mCommonAdapter.addText(text);
    }

    public void addNewTextList(List<String> logList) {
        mCommonAdapter.addNewTextList(logList);
    }

    public void clearTextList() {
        mCommonAdapter.clearTextList();
    }

    public void setOnTextItemClickListener(CommonTextRecyclerViewAdapter.OnTextItemClickListener listener) {
        mCommonAdapter.setItemClickListener(this, listener);
    }
}
