package com.flamingolive.mall.views;

import android.content.Context;
import android.view.ViewGroup;

import com.flamingolive.mall.adapter.SellerOrderAllRefundAdapter;
import com.flamingolive.mall.adapter.SellerOrderBaseAdapter;
import com.flamingolive.mall.adapter.SellerOrderRefundAdapter;

/**
 * 卖家 订单列表 全部退款
 */
public class SellerOrderAllRefundViewHolder extends AbsSellerOrderViewHolder {

    public SellerOrderAllRefundViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public String getOrderType() {
        return "all_refund";
    }

    @Override
    public SellerOrderBaseAdapter getSellerOrderAdapter() {
        return new SellerOrderRefundAdapter(mContext);
    }

}
