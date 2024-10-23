package com.flamingolive.mall.activity;

import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.Constants;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.bean.ConfigBean;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.utils.DialogUitl;
import com.flamingolive.common.utils.RouteUtil;
import com.flamingolive.common.utils.ToastUtil;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.mall.R;
import com.flamingolive.mall.http.MallHttpConsts;
import com.flamingolive.mall.http.MallHttpUtil;

/**
 * 开店保证金
 */
public class ShopApplyBondActivity extends AbsActivity implements View.OnClickListener {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop_apply_bond;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_046));
        findViewById(R.id.btn_submit).setOnClickListener(this);
        TextView bond = findViewById(R.id.bond);
        bond.setText(getIntent().getStringExtra(Constants.MALL_APPLY_BOND));
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            TextView textView = findViewById(R.id.tip);
            textView.setText(String.format(WordUtil.getString(R.string.mall_050), configBean.getShopSystemName()));
        }

    }

    @Override
    public void onClick(View v) {
        MallHttpUtil.setBond(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    finish();
                }else if(code==1004){//余额不足
                    new DialogUitl.Builder(mContext)
                            .setContent(WordUtil.getString(R.string.a_020))
                            .setConfrimString(WordUtil.getString(R.string.a_021))
                            .setCancelable(true)
                            .setIsHideTitle(true)
                            .setBackgroundDimEnabled(true)
                            .setClickCallback(new DialogUitl.SimpleCallback() {
                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                    RouteUtil.forwardMyCoin(mContext);
                                }
                            })
                            .build()
                            .show();
                }else{
                    ToastUtil.show(msg);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.SET_BOND);
        super.onDestroy();
    }
}
