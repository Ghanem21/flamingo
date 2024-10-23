package com.flamingolive.mall.views;

import android.content.Context;
import android.view.ViewGroup;

import com.flamingolive.mall.adapter.BuyerOrderBaseAdapter;
import com.flamingolive.mall.adapter.BuyerOrderPayAdapter;

/**
 * 买家 订单列表 待付款
 */
public class BuyerOrderPayViewHolder extends AbsBuyerOrderViewHolder {

    public BuyerOrderPayViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public String getOrderType() {
        return "wait_payment";
    }

    @Override
    public BuyerOrderBaseAdapter getBuyerOrderAdapter() {
        return new BuyerOrderPayAdapter(mContext);
    }

}
