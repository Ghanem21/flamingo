package com.flamingolive.live.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.glide.ImgLoader;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.utils.LanguageUtil;
import com.flamingolive.common.utils.StringUtil;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.common.views.AbsViewHolder;
import com.flamingolive.live.R;
import com.flamingolive.live.activity.LiveAnchorActivity;
import com.flamingolive.live.activity.LiveAudienceActivity;
import com.flamingolive.live.bean.LiveBean;
import com.flamingolive.live.http.LiveHttpConsts;
import com.flamingolive.live.http.LiveHttpUtil;

/**
 * Created by cxf on 2018/10/9.
 */

public class LiveEndViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ImageView mAvatar1;
    private ImageView mAvatar2;
    private TextView mName;
    private TextView mDuration;//直播时长
    private TextView mVotes;//收获映票
    private TextView mWatchNum;//观看人数

    public LiveEndViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_end;
    }

    @Override
    public void init() {
        mAvatar1 = (ImageView) findViewById(R.id.avatar_1);
        mAvatar2 = (ImageView) findViewById(R.id.avatar_2);
        mName = (TextView) findViewById(R.id.name);
        mDuration = (TextView) findViewById(R.id.duration);
        mVotes = (TextView) findViewById(R.id.votes);
        mWatchNum = (TextView) findViewById(R.id.watch_num);
        findViewById(R.id.btn_back).setOnClickListener(this);
        TextView votesName = (TextView) findViewById(R.id.votes_name);
        if (LanguageUtil.isZh()) {
            votesName.setText(WordUtil.getString(R.string.live_votes) + CommonAppConfig.getInstance().getVotesName());
        } else {
            votesName.setText(WordUtil.getString(R.string.live_votes));
        }
    }

    public void showData(LiveBean liveBean, final String stream) {
        if (liveBean != null) {
            mName.setText(liveBean.getUserNiceName());
            try {
                ImgLoader.displayBlur(mContext, liveBean.getAvatar(), mAvatar1);
                ImgLoader.displayAvatar(mContext, liveBean.getAvatar(), mAvatar2);
            } catch (Exception e) {

            }
        }
        mParentView.postDelayed(new Runnable() {
            @Override
            public void run() {

                LiveHttpUtil.getLiveEndInfo(stream, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            mVotes.setText(obj.getString("votes"));
                            mDuration.setText(obj.getString("length"));
                            mWatchNum.setText(StringUtil.toWan(obj.getLongValue("nums")));
                        }
                    }
                });
            }
        }, 500);

    }

    @Override
    public void onClick(View v) {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).superBackPressed();
        } else if (mContext instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) mContext).exitLiveRoom();
        }
    }

    @Override
    public void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_LIVE_END_INFO);
    }

}
