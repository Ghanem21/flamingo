package com.flamingolive.live.views;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.Constants;
import com.flamingolive.common.HtmlConfig;
import com.flamingolive.common.utils.LanguageUtil;
import com.flamingolive.common.utils.StringUtil;
import com.flamingolive.common.views.AbsLivePageViewHolder;
import com.flamingolive.live.R;
import com.flamingolive.live.activity.LiveActivity;
import com.flamingolive.live.activity.LiveAnchorActivity;
import com.flamingolive.live.activity.LiveAudienceActivity;
import com.flamingolive.live.activity.LiveContributeActivity;
import com.flamingolive.common.utils.L;

/**
 * Created by cxf on 2018/10/15.
 * 直播间礼物贡献榜
 */

public class LiveContributeViewHolder extends AbsLivePageViewHolder implements View.OnClickListener {

    private ProgressBar mProgressBar;
    private WebView mWebView;

    public LiveContributeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_contribute;
    }

    @Override
    public void init() {
        super.init();
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mWebView = new WebView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(params);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e("H5-------->" + url);
                view.loadUrl(url);
                return true;
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 70) {
                    if (mProgressBar.getVisibility() == View.VISIBLE) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        ((LinearLayout) mContentView).addView(mWebView);
    }

    @Override
    public void loadData() {
        String liveUid = null;
        if (mContext instanceof LiveActivity) {
            liveUid = ((LiveActivity) mContext).getLiveUid();
        } else if (mContext instanceof LiveContributeActivity) {
            liveUid = ((LiveContributeActivity) mContext).getLiveUid();
        }
        if (!TextUtils.isEmpty(liveUid) && mWebView != null) {
            mWebView.loadUrl(StringUtil.contact(HtmlConfig.LIVE_LIST, liveUid, "&", Constants.LANGUAGE, "=", LanguageUtil.getInstance().getLanguage()));
        }
    }

    @Override
    public void release() {
        super.release();
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void hide() {
        if (mContext instanceof LiveContributeActivity) {
            ((LiveContributeActivity) mContext).onBackPressed();
        } else {
            super.hide();
        }
    }

    @Override
    public void onHide() {
        if (CommonAppConfig.LIVE_ROOM_SCROLL && mContext != null && mContext instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) mContext).setScrollFrozen(false);
        }
    }
}
