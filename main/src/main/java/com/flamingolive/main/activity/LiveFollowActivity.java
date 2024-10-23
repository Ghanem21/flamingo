package com.flamingolive.main.activity;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.Constants;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.adapter.RefreshAdapter;
import com.flamingolive.common.bean.LiveClassBean;
import com.flamingolive.common.custom.CommonRefreshView;
import com.flamingolive.common.custom.ItemDecoration;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.interfaces.OnItemClickListener;
import com.flamingolive.common.utils.FloatWindowHelper;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.live.bean.LiveBean;
import com.flamingolive.live.livegame.luckpan.utils.LiveStorge;
import com.flamingolive.main.R;
import com.flamingolive.main.adapter.MainHomeFollowAdapter;
import com.flamingolive.main.adapter.MainLiveClassAdapter;
import com.flamingolive.main.http.MainHttpConsts;
import com.flamingolive.main.http.MainHttpUtil;
import com.flamingolive.main.presenter.CheckLivePresenter;

import java.util.List;

/**
 * Created by 云豹科技 on 2022/5/31.
 */
public class LiveFollowActivity extends AbsActivity implements OnItemClickListener<LiveBean> {


    private CommonRefreshView mRefreshView;
    private MainHomeFollowAdapter mAdapter;
    private int mLiveClassId;
    private CheckLivePresenter mCheckLivePresenter;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, LiveFollowActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_follow;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.a_104));
        RecyclerView classRecyclerView = findViewById(R.id.recyclerView_class);
        classRecyclerView.setHasFixedSize(true);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        MainLiveClassAdapter classAdapter = new MainLiveClassAdapter(mContext);
        classAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
            @Override
            public void onItemClick(LiveClassBean bean, int position) {
                mLiveClassId = bean.getId();
                if (mRefreshView != null) {
                    mRefreshView.initData();
                }
            }
        });
        classRecyclerView.setAdapter(classAdapter);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_follow);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {
            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeFollowAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveFollowActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFollow(p, mLiveClassId, callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                return JSON.parseArray(obj.getString("list"), LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> adapterItemList, int allItemCount) {
                if (CommonAppConfig.LIVE_ROOM_SCROLL) {
                    LiveStorge.getInstance().put(Constants.LIVE_FOLLOW, adapterItemList);
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });

    }


    @Override
    public void onItemClick(LiveBean liveBean, int position) {
        if (!FloatWindowHelper.checkVoice(true)) {
            return;
        }
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mCheckLivePresenter.watchLive(liveBean, Constants.LIVE_FOLLOW, position);
        } else {
            mCheckLivePresenter.watchLive(liveBean);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW);
        super.onDestroy();

    }

}
