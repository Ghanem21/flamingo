package com.flamingolive.mall.views;

import android.content.Context;
import android.view.ViewGroup;

import com.flamingolive.mall.adapter.BuyerOrderBaseAdapter;
import com.flamingolive.mall.adapter.BuyerOrderReceiveAdapter;

/**
 * 买家 订单列表 待收货
 */
public class BuyerOrderReceiveViewHolder extends AbsBuyerOrderViewHolder {

    public BuyerOrderReceiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public String getOrderType() {
        return "wait_receive";
    }

    @Override
    public BuyerOrderBaseAdapter getBuyerOrderAdapter() {
        return new BuyerOrderReceiveAdapter(mContext);
    }


}
