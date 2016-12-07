package com.cjw.rxjavademo.ui.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.cjw.rxjavademo.R;

public class AppBarActivity extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    
    protected Toolbar mToolbar;
    protected TextView mTitleTv;
    protected Context mContext;
    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
    }

    protected void initWidget() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
            }
        }
    }

    protected void hideBackIv() {
        mToolbar.setNavigationIcon(new ColorDrawable(ContextCompat.getColor(mContext, R.color.transparent)));
        mToolbar.setNavigationOnClickListener(null);
    }

    protected void findWidget() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 返回的监听
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
