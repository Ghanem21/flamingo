package com.flamingolive.mall.views;

import android.content.Context;
import android.view.ViewGroup;

import com.flamingolive.mall.adapter.SellerOrderBaseAdapter;
import com.flamingolive.mall.adapter.SellerOrderFinishAdapter;

/**
 * 卖家 订单列表 已关闭
 */
public class SellerOrderClosedViewHolder extends AbsSellerOrderViewHolder {

    public SellerOrderClosedViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public String getOrderType() {
        return "closed";
    }

    @Override
    public SellerOrderBaseAdapter getSellerOrderAdapter() {
        return new SellerOrderFinishAdapter(mContext);
    }

}
