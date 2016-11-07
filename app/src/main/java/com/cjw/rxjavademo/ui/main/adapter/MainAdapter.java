package com.cjw.rxjavademo.ui.main.adapter;

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

/**
 * Created by cjw on 16-11-7.
 */

public class MainAdapter extends UltimateViewAdapter<MainAdapter.MainHolder> {

    private List<String> mTitleList;
    private Context mContext;
    private LayoutInflater mInflater;

    private OnItemClickListener mItemClickListener;

    public MainAdapter(Context context, List<String> dataList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mTitleList = new ArrayList<>();
        mTitleList.addAll(dataList);
    }

    @Override
    public MainHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public MainHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public MainHolder onCreateViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MainHolder(view);
    }

    @Override
    public int getAdapterItemCount() {
        return mTitleList.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(MainHolder holder, int position) {
        ((TextView) holder.itemView).setText(mTitleList.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    class MainHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MainHolder(View itemView) {
            super(itemView);
            ((TextView) itemView).setGravity(Gravity.CENTER);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onAdapterItemClick(getLayoutPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onAdapterItemClick(int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
