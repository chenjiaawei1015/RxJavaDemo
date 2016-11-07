package com.cjw.rxjavademo.widget.logRecyclerView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class LogRecyclerViewAdapter extends UltimateViewAdapter<LogRecyclerViewAdapter.LogRecyclerViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mLogList;

    public LogRecyclerViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mLogList = new ArrayList<>();
    }

    public void addLog(String logText) {
        mLogList.add(logText);
        int size = mLogList.size();
        notifyItemChanged(size - 1);
    }

    public void addNewLogList(List<String> logList) {
        mLogList.clear();
        mLogList.addAll(logList);
        notifyItemRangeChanged(0, mLogList.size());
    }

    public void clearLogList() {
        mLogList.clear();
        int beforeLogSize = mLogList.size();
        notifyItemRangeRemoved(0, beforeLogSize);
    }

    @Override
    public LogRecyclerViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public LogRecyclerViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public LogRecyclerViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new LogRecyclerViewHolder(view);
    }

    @Override
    public int getAdapterItemCount() {
        return mLogList.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(LogRecyclerViewHolder holder, int position) {
        ((TextView) holder.itemView).setText(mLogList.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        // do nothing
    }

    public class LogRecyclerViewHolder extends RecyclerView.ViewHolder {

        public LogRecyclerViewHolder(View itemView) {
            super(itemView);
            ((TextView) itemView).setGravity(Gravity.CENTER);
        }
    }
}
