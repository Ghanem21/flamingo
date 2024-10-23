package com.flamingolive.mall.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.flamingolive.common.adapter.RefreshAdapter;
import com.flamingolive.common.custom.CommonRefreshView;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.views.AbsCommonViewHolder;
import com.flamingolive.mall.R;
import com.flamingolive.mall.adapter.PayBuyAdapter;
import com.flamingolive.mall.bean.PayContentBuyBean;
import com.flamingolive.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 我购买的
 */
public class PayBuyViewHolder extends AbsCommonViewHolder {

    private CommonRefreshView mRefreshView;
    private PayBuyAdapter mAdapter;

    public PayBuyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_pay_buy;
    }

    @Override
    public void init() {
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_pay_buy);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<PayContentBuyBean>() {
            @Override
            public RefreshAdapter<PayContentBuyBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new PayBuyAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getBuyPayContentList(p, callback);
            }

            @Override
            public List<PayContentBuyBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), PayContentBuyBean.class);
            }

            @Override
            public void onRefreshSuccess(List<PayContentBuyBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<PayContentBuyBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });

    }

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }
}
