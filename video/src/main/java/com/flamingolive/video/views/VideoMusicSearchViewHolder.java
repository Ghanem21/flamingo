package com.flamingolive.video.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.flamingolive.common.adapter.RefreshAdapter;
import com.flamingolive.common.custom.CommonRefreshView;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.video.R;
import com.flamingolive.video.adapter.MusicAdapter;
import com.flamingolive.video.bean.MusicBean;
import com.flamingolive.video.http.VideoHttpUtil;
import com.flamingolive.video.interfaces.VideoMusicActionListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/12/7.
 * 视频搜索音乐
 */

public class VideoMusicSearchViewHolder extends VideoMusicChildViewHolder {

    private String mKey;

    public VideoMusicSearchViewHolder(Context context, ViewGroup parentView, VideoMusicActionListener actionListener) {
        super(context, parentView, actionListener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_music_search;
    }

    @Override
    public void init() {
        super.init();
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_search);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<MusicBean>() {
            @Override
            public RefreshAdapter<MusicBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MusicAdapter(mContext);
                    mAdapter.setActionListener(mActionListener);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (!TextUtils.isEmpty(mKey)) {
                    VideoHttpUtil.videoSearchMusic(mKey, p, callback);
                }
            }

            @Override
            public List<MusicBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), MusicBean.class);
            }

            @Override
            public void onRefreshSuccess(List<MusicBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<MusicBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    public void setKey(String key) {
        mKey = key;
    }

    public void clearData() {
        mKey=null;
        if(mAdapter!=null){
            mAdapter.clearData();
        }
    }
}
