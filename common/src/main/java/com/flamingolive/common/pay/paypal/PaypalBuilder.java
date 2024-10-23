package com.flamingolive.common.pay.paypal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flamingolive.common.R;
import com.flamingolive.common.activity.PaypalDropInActivity;
import com.flamingolive.common.http.CommonHttpUtil;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.interfaces.ActivityResultCallback;
import com.flamingolive.common.pay.PayCallback;
import com.flamingolive.common.utils.ActivityResultUtil;
import com.flamingolive.common.utils.DialogUitl;
import com.flamingolive.common.utils.ToastUtil;

import java.util.Map;

public class PaypalBuilder {

    private FragmentActivity mActivity;
    private PayCallback mPayCallback;
    private String mMoney;//要支付的金额
    private String mGoodsName;//商品名称
    private String mBuyType;
    private String mBraintreeToken;
    private String mOrderId;

    public PaypalBuilder(FragmentActivity activity) {
        mActivity = activity;
    }

    public void setMoney(String money) {
        mMoney = money;
    }

    public void setGoodsName(String goodsName) {
        mGoodsName = goodsName;
    }

    public void setPayCallback(PayCallback callback) {
        mPayCallback = callback;
    }

    public void setBuyType(String buyType) {
        mBuyType = buyType;
    }

    public void setBraintreeToken(String braintreeToken) {
        mBraintreeToken = braintreeToken;
    }

    public void pay(String serviceName, Map<String, String> params) {
        CommonHttpUtil.getPaypalOrder(serviceName, params, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mOrderId = obj.getString("orderid");
                    Intent intent = new Intent(mActivity, PaypalDropInActivity.class);
                    intent.putExtra("braintreeToken", mBraintreeToken);
                    intent.putExtra("money", mMoney);
                    intent.putExtra("goodsName", mGoodsName);
                    ActivityResultUtil.startActivityForResult(mActivity, intent, new ActivityResultCallback() {
                        @Override
                        public void onSuccess(Intent intent) {

                        }

                        @Override
                        public void onResult(int resultCode, Intent data) {
                            if (resultCode == Activity.RESULT_OK) {
                                String nonce = data.getStringExtra("nonce");
                                if (!TextUtils.isEmpty(nonce)) {
                                    CommonHttpUtil.braintreeCallback(mOrderId, mBuyType, nonce, mMoney, new HttpCallback() {
                                        @Override
                                        public void onSuccess(int code, String msg, String[] info) {
                                            if (code == 0) {
                                                if (mPayCallback != null) {
                                                    mPayCallback.onSuccess();
                                                }
                                            } else {
                                                ToastUtil.show(msg);
                                            }
                                        }

                                        @Override
                                        public boolean showLoadingDialog() {
                                            return true;
                                        }

                                        @Override
                                        public Dialog createLoadingDialog() {
                                            return DialogUitl.loadingDialog(mActivity);
                                        }
                                    });
                                } else {
                                    ToastUtil.show(R.string.pay_fail);
                                }
                            } else {
                                ToastUtil.show(R.string.pay_fail);
                            }
                        }
                    });
                } else {
                    ToastUtil.show(msg);
                }
            }

        });
    }

}
