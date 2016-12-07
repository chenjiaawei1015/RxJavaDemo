package com.cjw.rxjavademo.widget.logrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommonTextRecyclerViewAdapter extends UltimateViewAdapter<CommonTextRecyclerViewAdapter.TextRecyclerViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mTextList;
    private OnTextItemClickListener mItemClickListener;
    private UltimateRecyclerView mRv;

    public CommonTextRecyclerViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mTextList = new ArrayList<>();
    }

    public void addText(String logText) {
        mTextList.add(logText);
        int size = mTextList.size();
        notifyItemChanged(size - 1);
    }

    public void addNewTextList(List<String> logList) {
        mTextList.clear();
        mTextList.addAll(logList);
        notifyItemRangeChanged(0, mTextList.size());
    }

    public void clearTextList() {
        int beforeLogSize = mTextList.size();
        mTextList.clear();
        if (beforeLogSize != 0) {
            notifyItemRangeRemoved(0, beforeLogSize);
        }
    }

    @Override
    public TextRecyclerViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public TextRecyclerViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public TextRecyclerViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new TextRecyclerViewHolder(view);
    }

    @Override
    public int getAdapterItemCount() {
        return mTextList.size();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(TextRecyclerViewHolder holder, int position) {
        ((TextView) holder.itemView).setText(mTextList.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        // do nothing
    }

    class TextRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextRecyclerViewHolder(View itemView) {
            super(itemView);
            ((TextView) itemView).setGravity(Gravity.CENTER);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onTextItemClick(mRv, getLayoutPosition());
            }
        }
    }

    public interface OnTextItemClickListener {
        void onTextItemClick(UltimateRecyclerView rv, int position);
    }

    public void setItemClickListener(UltimateRecyclerView rv, OnTextItemClickListener itemClickListener) {
        mRv = rv;
        mItemClickListener = itemClickListener;
    }
}
