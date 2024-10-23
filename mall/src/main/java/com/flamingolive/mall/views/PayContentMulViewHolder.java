package com.flamingolive.mall.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.flamingolive.common.interfaces.OnItemClickListener;
import com.flamingolive.common.views.AbsCommonViewHolder;
import com.flamingolive.mall.R;
import com.flamingolive.mall.activity.PayContentPubActivity;
import com.flamingolive.mall.adapter.PayContentMulAdapter;
import com.flamingolive.mall.bean.PayContentVideoBean;

import java.util.List;

/**
 * 多视频
 */
public class PayContentMulViewHolder extends AbsCommonViewHolder implements OnItemClickListener<PayContentVideoBean> {

    private RecyclerView mRecyclerView;
    private PayContentMulAdapter mAdapter;
    private int mTargetPosition = -1;

    public PayContentMulViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_pay_content_mul;
    }

    @Override
    public void init() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new PayContentMulAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }


    public void setFilePath(String filePath, String duration) {
        if (mTargetPosition >= 0) {
            mAdapter.setFilePath(filePath, duration, mTargetPosition);
        }
    }

    @Override
    public void onItemClick(PayContentVideoBean bean, int position) {
        mTargetPosition = position;
        ((PayContentPubActivity) mContext).chooseVideo();
    }

    public List<PayContentVideoBean> getList() {
        if (mAdapter != null) {
            return mAdapter.getList();
        }
        return null;
    }
}
