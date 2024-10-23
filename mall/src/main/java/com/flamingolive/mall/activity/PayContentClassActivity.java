package com.flamingolive.mall.activity;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.flamingolive.common.Constants;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.adapter.RefreshAdapter;
import com.flamingolive.common.custom.CommonRefreshView;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.interfaces.OnItemClickListener;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.mall.R;
import com.flamingolive.mall.adapter.PayContentClassAdapter;
import com.flamingolive.mall.bean.PayContentClassBean;
import com.flamingolive.mall.http.MallHttpConsts;
import com.flamingolive.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 付费内容 选择类别
 */
public class PayContentClassActivity extends AbsActivity implements OnItemClickListener<PayContentClassBean> {

    private String mClassId;
    private PayContentClassAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_content_class;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_316));
        mClassId = getIntent().getStringExtra(Constants.CLASS_ID);
        CommonRefreshView refreshView = findViewById(R.id.refreshView);
        refreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        refreshView.setDataHelper(new CommonRefreshView.DataHelper<PayContentClassBean>() {
            @Override
            public RefreshAdapter<PayContentClassBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new PayContentClassAdapter(mContext);
                    mAdapter.setOnItemClickListener(PayContentClassActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getPayClassList(callback);
            }

            @Override
            public List<PayContentClassBean> processData(String[] info) {
                List<PayContentClassBean> list = JSON.parseArray(Arrays.toString(info), PayContentClassBean.class);
                if (list != null && list.size() > 0) {
                    if (TextUtils.isEmpty(mClassId)) {
                        list.get(0).setChecked(true);
                    } else {
                        for (PayContentClassBean bean : list) {
                            if (mClassId.equals(bean.getId())) {
                                bean.setChecked(true);
                                break;
                            }
                        }
                    }
                }
                return list;
            }

            @Override
            public void onRefreshSuccess(List<PayContentClassBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<PayContentClassBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        refreshView.initData();
    }

    @Override
    public void onItemClick(PayContentClassBean bean, int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CLASS_ID, bean.getId());
        intent.putExtra(Constants.CLASS_NAME, bean.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_PAY_CLASS_LIST);
        super.onDestroy();
    }
}
