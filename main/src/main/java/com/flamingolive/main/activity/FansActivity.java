package com.flamingolive.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.Constants;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.adapter.RefreshAdapter;
import com.flamingolive.common.custom.CommonRefreshView;
import com.flamingolive.common.event.FollowEvent;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.interfaces.OnItemClickListener;
import com.flamingolive.common.utils.RouteUtil;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.main.R;
import com.flamingolive.main.adapter.SearchAdapter;
import com.flamingolive.live.bean.SearchUserBean;
import com.flamingolive.main.http.MainHttpConsts;
import com.flamingolive.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 * 我的粉丝 TA的粉丝
 */

public class FansActivity extends AbsActivity implements OnItemClickListener<SearchUserBean> {

    public static void forward(Context context, String toUid) {
        Intent intent = new Intent(context, FansActivity.class);
        intent.putExtra(Constants.TO_UID, toUid);
        context.startActivity(intent);
    }

    private CommonRefreshView mRefreshView;
    private SearchAdapter mAdapter;
    private String mToUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fans;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.fans));
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mRefreshView = findViewById(R.id.refreshView);
        if (mToUid.equals(CommonAppConfig.getInstance().getUid())) {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_fans);
        } else {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_fans_2);
        }
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SearchUserBean>() {
            @Override
            public RefreshAdapter<SearchUserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SearchAdapter(mContext, Constants.FOLLOW_FROM_FANS);
                    mAdapter.setOnItemClickListener(FansActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFansList(mToUid, p, callback);
            }

            @Override
            public List<SearchUserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), SearchUserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<SearchUserBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<SearchUserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        EventBus.getDefault().register(this);
        mRefreshView.initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (mAdapter != null) {
            mAdapter.updateItem(e.getToUid(), e.getIsAttention());
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.GET_FANS_LIST);
        super.onDestroy();
    }

    @Override
    public void onItemClick(SearchUserBean bean, int position) {
        RouteUtil.forwardUserHome(mContext, bean.getId());
    }
}