package com.flamingolive.main.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.Constants;
import com.flamingolive.common.adapter.RefreshAdapter;
import com.flamingolive.common.bean.ConfigBean;
import com.flamingolive.common.bean.VideoClassBean;
import com.flamingolive.common.custom.CommonRefreshView;
import com.flamingolive.common.custom.ItemDecoration;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.interfaces.OnItemClickListener;
import com.flamingolive.common.utils.FloatWindowHelper;
import com.flamingolive.common.utils.JsonUtil;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.main.R;
import com.flamingolive.main.adapter.MainHomeVideoAdapter;
import com.flamingolive.main.adapter.MainHomeVideoClassAdapter;
import com.flamingolive.video.activity.VideoPlayActivity;
import com.flamingolive.video.bean.VideoBean;
import com.flamingolive.video.event.VideoDeleteEvent;
import com.flamingolive.video.event.VideoScrollPageEvent;
import com.flamingolive.video.http.VideoHttpConsts;
import com.flamingolive.video.http.VideoHttpUtil;
import com.flamingolive.video.interfaces.VideoScrollDataHelper;
import com.flamingolive.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by cxf on 2018/9/22.
 * 首页视频
 */

public class MainHomeVideoViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<VideoBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeVideoAdapter mAdapter;
    private VideoScrollDataHelper mVideoScrollDataHelper;
    private RecyclerView mClassRecyclerView;
    private MainHomeVideoClassAdapter mClassAdapter;
    private static final int ID_RECOMMEND = -1;
    private int mVideoClassId = ID_RECOMMEND;


    public MainHomeVideoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_video;
    }

    @Override
    public void init() {
        mClassRecyclerView = findViewById(R.id.recyclerView_class);
        mClassRecyclerView.setHasFixedSize(true);
        mClassRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        List<VideoClassBean> videoClassList = new ArrayList<>();
        videoClassList.add(new VideoClassBean(ID_RECOMMEND, WordUtil.getString(R.string.recommend), true));
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<VideoClassBean> list = JSON.parseArray(configBean.getVideoClass(), VideoClassBean.class);
            if (list != null && list.size() > 0) {
                videoClassList.addAll(list);
            }
        }
        mClassAdapter = new MainHomeVideoClassAdapter(mContext, videoClassList);
        mClassAdapter.setOnItemClickListener(new OnItemClickListener<VideoClassBean>() {
            @Override
            public void onItemClick(VideoClassBean bean, int position) {
                mVideoClassId = bean.getId();
                if (mRefreshView != null) {
                    mRefreshView.initData();
                }
            }
        });
        mClassRecyclerView.setAdapter(mClassAdapter);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_video);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<VideoBean>() {
            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeVideoAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeVideoViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (mVideoClassId == ID_RECOMMEND) {
                    VideoHttpUtil.getHomeVideoList(p, callback);
                } else {
                    VideoHttpUtil.getHomeVideoClassList(mVideoClassId, p, callback);
                }
            }

            @Override
            public List<VideoBean> processData(String[] info) {
                return JsonUtil.getJsonToList(Arrays.toString(info), VideoBean.class);

            }

            @Override
            public void onRefreshSuccess(List<VideoBean> list, int listCount) {
                VideoStorge.getInstance().put(Constants.VIDEO_HOME, list);
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<VideoBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoScrollPageEvent(VideoScrollPageEvent e) {
        if (Constants.VIDEO_HOME.equals(e.getKey()) && mRefreshView != null) {
            mRefreshView.setPageCount(e.getPage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        if (mAdapter != null) {
            mAdapter.deleteVideo(e.getVideoId());
            if (mAdapter.getItemCount() == 0 && mRefreshView != null) {
                mRefreshView.showEmpty();
            }
        }
    }

    @Override
    public void onItemClick(VideoBean bean, int position) {
        int page = 1;
        if (mRefreshView != null) {
            page = mRefreshView.getPageCount();
        }
        if (mVideoScrollDataHelper == null) {
            mVideoScrollDataHelper = new VideoScrollDataHelper() {

                @Override
                public void loadData(int p, HttpCallback callback) {
                    if (mVideoClassId == ID_RECOMMEND) {
                        VideoHttpUtil.getHomeVideoList(p, callback);
                    } else {
                        VideoHttpUtil.getHomeVideoClassList(mVideoClassId, p, callback);
                    }
                }
            };
        }
        VideoStorge.getInstance().putDataHelper(Constants.VIDEO_HOME, mVideoScrollDataHelper);
        VideoPlayActivity.forward(mContext, position, Constants.VIDEO_HOME, page);
    }

    @Override
    public void release() {
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_LIST);
        VideoHttpUtil.cancel(VideoHttpConsts.GET_HOME_VIDEO_CLASS_LIST);
        EventBus.getDefault().unregister(this);
        mVideoScrollDataHelper = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

}
