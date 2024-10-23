package com.flamingolive.main.presenter;

import android.app.Dialog;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.Constants;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.utils.DialogUitl;
import com.flamingolive.common.utils.ToastUtil;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.live.activity.LiveAudienceActivity;
import com.flamingolive.live.bean.LiveBean;
import com.flamingolive.live.dialog.LiveCheckDialogFragment;
import com.flamingolive.live.http.LiveHttpConsts;
import com.flamingolive.live.http.LiveHttpUtil;
import com.flamingolive.main.R;
import com.flamingolive.main.activity.TeenagerActivity;

/**
 * Created by cxf on 2017/9/29.
 */

public class CheckLivePresenter {

    private Context mContext;
    private LiveBean mLiveBean;//选中的直播间信息
    private String mKey;
    private int mPosition;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码
    private int mLiveSdk;
    private boolean mIsChatRoom;
    private int mChatRoomType;

    public CheckLivePresenter(Context context) {
        mContext = context;
    }

    public void watchLive(LiveBean bean) {
        watchLive(bean, "", 0);
    }

    public void watchLive(LiveBean bean, boolean needShowDialog) {
        watchLive(bean, "", 0, needShowDialog);
    }

    public void watchLive(LiveBean bean, String key, int position) {
        watchLive(bean, key, position, true);
    }

    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBean bean, String key, int position, final boolean needShowDialog) {
        mLiveBean = bean;
        mKey = key;
        mPosition = position;
        LiveHttpUtil.checkLive(bean.getUid(), bean.getStream(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mIsChatRoom = obj.getIntValue("live_type") == 1;
                        mChatRoomType = obj.getIntValue("voice_type");
                        mLiveType = obj.getIntValue("type");
                        mLiveTypeVal = obj.getIntValue("type_val");
                        mLiveTypeMsg = obj.getString("type_msg");
                        mLiveSdk = obj.getIntValue("live_sdk");
                        if (mLiveType == Constants.LIVE_TYPE_NORMAL) {
                            forwardNormalRoom();
                        } else {
                            if (CommonAppConfig.getInstance().isTeenagerType()) {
                                if (mLiveType == Constants.LIVE_TYPE_PAY || mLiveType == Constants.LIVE_TYPE_TIME) {
                                    new DialogUitl.Builder(mContext)
                                            .setContent(WordUtil.getString(R.string.a_137))
                                            .setCancelString(WordUtil.getString(R.string.know))
                                            .setConfrimString(WordUtil.getString(R.string.a_118))
                                            .setCancelable(true)
                                            .setBackgroundDimEnabled(true)
                                            .setClickCallback(new DialogUitl.SimpleCallback() {


                                                @Override
                                                public void onConfirmClick(Dialog dialog, String content) {
                                                    TeenagerActivity.forward(mContext);
                                                }
                                            })
                                            .build()
                                            .show();

                                    return;
                                }
                            }
                            if (needShowDialog) {
                                LiveCheckDialogFragment fragment = new LiveCheckDialogFragment();
                                if (mLiveType == Constants.LIVE_TYPE_PWD) {
                                    fragment.setLiveType(mLiveType, mLiveTypeMsg);
                                } else {
                                    fragment.setLiveType(mLiveType, String.valueOf(mLiveTypeVal));
                                }
                                fragment.setActionListener(new LiveCheckDialogFragment.ActionListener() {
                                    @Override
                                    public void onConfirmClick() {
                                        if (mLiveType == Constants.LIVE_TYPE_PWD) {
                                            forwardNormalRoom();
                                        } else {
                                            if (((AbsActivity) mContext).checkLogin()) {
                                                roomCharge();
                                            }
                                        }
                                    }
                                });
                                fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "LiveRoomCheckDialogFragment2");
                            } else {
                                if (mLiveType == Constants.LIVE_TYPE_PWD) {
                                    forwardNormalRoom();
                                } else {
                                    if (((AbsActivity) mContext).checkLogin()) {
                                        roomCharge();
                                    }
                                }
                            }
                        }
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
                return DialogUitl.loadingDialog(mContext);
            }
        });
    }


    /**
     * 前往普通房间
     */
    private void forwardNormalRoom() {
        forwardLiveAudienceActivity();
    }


    public void roomCharge() {
        LiveHttpUtil.roomCharge(mLiveBean.getUid(), mLiveBean.getStream(), mRoomChargeCallback);
    }

    private HttpCallback mRoomChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                forwardLiveAudienceActivity();
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
            return DialogUitl.loadingDialog(mContext);
        }
    };

    public void cancel() {
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
    }

    /**
     * 跳转到直播间
     */
    private void forwardLiveAudienceActivity() {
        if (mIsChatRoom) {
            LiveAudienceActivity.forward(mContext, mLiveBean, mLiveType, mLiveTypeVal, "", 0, mLiveSdk, true,mChatRoomType);
        } else {
            LiveAudienceActivity.forward(mContext, mLiveBean, mLiveType, mLiveTypeVal, mKey, mPosition, mLiveSdk, false,mChatRoomType);
        }
    }
}
