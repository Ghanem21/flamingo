package com.flamingolive.video.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.CommonAppContext;
import com.flamingolive.common.HtmlConfig;
import com.flamingolive.common.bean.ConfigBean;
import com.flamingolive.common.bean.UserBean;
import com.flamingolive.common.http.FileDownloadCallback;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.http.UriDownloadCallback;
import com.flamingolive.common.interfaces.CommonCallback;
import com.flamingolive.common.interfaces.PermissionCallback;
import com.flamingolive.common.mob.MobCallback;
import com.flamingolive.common.mob.MobShareUtil;
import com.flamingolive.common.mob.ShareData;
import com.flamingolive.common.utils.DateFormatUtil;
import com.flamingolive.common.utils.DialogUitl;
import com.flamingolive.common.utils.DownloadUtil;
import com.flamingolive.common.utils.MediaUtil;
import com.flamingolive.common.utils.PermissionUtil;
import com.flamingolive.common.utils.StringUtil;
import com.flamingolive.common.utils.ToastUtil;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.video.R;
import com.flamingolive.video.bean.VideoBean;
import com.flamingolive.video.event.VideoDeleteEvent;
import com.flamingolive.video.event.VideoShareEvent;
import com.flamingolive.video.http.VideoHttpConsts;
import com.flamingolive.video.http.VideoHttpUtil;
import com.flamingolive.video.utils.VideoStorge;
import com.flamingolive.video.views.VideoScrollViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by cxf on 2019/2/28.
 */

public abstract class AbsVideoPlayActivity extends AbsVideoCommentActivity {

    protected VideoScrollViewHolder mVideoScrollViewHolder;
    private Dialog mDownloadVideoDialog;
    private ClipboardManager mClipboardManager;
    private MobCallback mMobCallback;
    private MobShareUtil mMobShareUtil;
    private DownloadUtil mDownloadUtil;
    private ConfigBean mConfigBean;
    private VideoBean mShareVideoBean;
    protected String mVideoKey;
    private boolean mPaused;


    @Override
    protected void main() {
        super.main();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                mConfigBean = bean;
            }
        });
    }


    /**
     * 复制视频链接
     */
    public void copyLink(VideoBean videoBean) {
        if (videoBean == null) {
            return;
        }
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("text", videoBean.getHrefW());
        mClipboardManager.setPrimaryClip(clipData);
        ToastUtil.show(R.string.copy_success);
    }

    /**
     * 分享页面链接
     */
    public void shareVideoPage(String type, VideoBean videoBean) {
        if (videoBean == null || mConfigBean == null) {
            return;
        }
        if (mMobCallback == null) {
            mMobCallback = new MobCallback() {

                @Override
                public void onSuccess(Object data) {
                    if (mShareVideoBean == null) {
                        return;
                    }
                    VideoHttpUtil.setVideoShare(mShareVideoBean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0 && mShareVideoBean != null) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                EventBus.getDefault().post(new VideoShareEvent(mShareVideoBean.getId(), obj.getString("shares")));
                            } else {
                                ToastUtil.show(msg);
                            }
                        }
                    });
                }

                @Override
                public void onError() {

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onFinish() {

                }
            };
        }
        mShareVideoBean = videoBean;
        ShareData data = new ShareData();
        String shareVideoTitle = mConfigBean.getVideoShareTitle();
        if (!TextUtils.isEmpty(shareVideoTitle) && shareVideoTitle.contains("{username}")) {
            UserBean userBean = videoBean.getUserBean();
            if (userBean != null) {
                shareVideoTitle = shareVideoTitle.replace("{username}", userBean.getUserNiceName());
            }
        }
        data.setTitle(shareVideoTitle);
        String videoTitle = videoBean.getTitle();
        if (TextUtils.isEmpty(videoTitle)) {
            data.setDes(mConfigBean.getVideoShareDes());
        } else {
            data.setDes(videoTitle);
        }
        data.setImgUrl(videoBean.getThumbs());
        String webUrl = StringUtil.contact(HtmlConfig.SHARE_VIDEO, videoBean.getId());
        data.setWebUrl(webUrl);
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        mMobShareUtil.execute(type, data, mMobCallback);
    }


    /**
     * 下载视频
     */
    public void downloadVideo(final VideoBean videoBean) {
        if (videoBean == null || TextUtils.isEmpty(videoBean.getHrefW())) {
            return;
        }
        PermissionUtil.request(this, new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        mDownloadVideoDialog = DialogUitl.loadingDialog(mContext);
                        mDownloadVideoDialog.show();
                        if (mDownloadUtil == null) {
                            mDownloadUtil = new DownloadUtil(videoBean.getTag());
                        }
                        long currentTimeMillis = SystemClock.uptimeMillis();
                        String fileName = StringUtil.generateFileName() + ".mp4";
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.MediaColumns.TITLE, fileName);
                        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                        values.put(MediaStore.MediaColumns.DATE_MODIFIED, currentTimeMillis);
                        values.put(MediaStore.MediaColumns.DATE_ADDED, currentTimeMillis);
                        values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, currentTimeMillis);
                        values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
                        } else {
                            values.put(MediaStore.MediaColumns.DATA, CommonAppConfig.VIDEO_DOWNLOAD_PATH + fileName);
                        }
                        Uri uri = CommonAppContext.getInstance().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                        mDownloadUtil.download(uri, videoBean.getHrefW(), new UriDownloadCallback() {
                            @Override
                            public void onSuccess() {
                                ToastUtil.show(R.string.video_download_success);
                                if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
                                    mDownloadVideoDialog.dismiss();
                                }
                                mDownloadVideoDialog = null;
                            }


                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.show(R.string.video_download_failed);
                                if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
                                    mDownloadVideoDialog.dismiss();
                                }
                                mDownloadVideoDialog = null;
                            }
                        });
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 删除视频
     */
    public void deleteVideo(final VideoBean videoBean) {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.confirm_delete), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                VideoHttpUtil.videoDelete(videoBean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (mVideoScrollViewHolder != null) {
                                mVideoScrollViewHolder.deleteVideo(videoBean);
                                EventBus.getDefault().post(new VideoDeleteEvent(videoBean.getId()));
                            }
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        });
    }


    public boolean isPaused() {
        return mPaused;
    }

    @Override
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
    }

    @Override
    public void release() {
        super.release();
        VideoHttpUtil.cancel(VideoHttpConsts.SET_VIDEO_SHARE);
        VideoHttpUtil.cancel(VideoHttpConsts.VIDEO_DELETE);
        if (mDownloadVideoDialog != null && mDownloadVideoDialog.isShowing()) {
            mDownloadVideoDialog.dismiss();
        }
        if (mVideoScrollViewHolder != null) {
            mVideoScrollViewHolder.release();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        VideoStorge.getInstance().removeDataHelper(mVideoKey);
        mDownloadVideoDialog = null;
        mVideoScrollViewHolder = null;
        mMobShareUtil = null;
    }


    public void setVideoScrollViewHolder(VideoScrollViewHolder videoScrollViewHolder) {
        mVideoScrollViewHolder = videoScrollViewHolder;
    }
}
