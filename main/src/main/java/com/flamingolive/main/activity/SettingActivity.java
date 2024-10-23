package com.flamingolive.main.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.Constants;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.activity.WebViewActivity;
import com.flamingolive.common.bean.ConfigBean;
import com.flamingolive.common.event.LoginChangeEvent;
import com.flamingolive.common.glide.ImgLoader;
import com.flamingolive.common.http.CommonHttpConsts;
import com.flamingolive.common.http.CommonHttpUtil;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.interfaces.CommonCallback;
import com.flamingolive.common.utils.DialogUitl;
import com.flamingolive.common.utils.LanguageUtil;
import com.flamingolive.common.utils.SpUtil;
import com.flamingolive.common.utils.StringUtil;
import com.flamingolive.common.utils.ToastUtil;
import com.flamingolive.common.utils.UmengUtil;
import com.flamingolive.common.utils.VersionUtil;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.im.tpns.TpnsUtil;
import com.flamingolive.im.utils.ImMessageUtil;
import com.flamingolive.main.R;
import com.flamingolive.main.adapter.SettingAdapter;
import com.flamingolive.main.bean.SettingBean;
import com.flamingolive.main.http.MainHttpConsts;
import com.flamingolive.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingActivity extends AbsActivity implements SettingAdapter.ActionListener {

    private static final String TAG = "";
    private RecyclerView mRecyclerView;
    private Handler mHandler;
    private SettingAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.setting));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        MainHttpUtil.getSettingList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<SettingBean> list0 = JSON.parseArray(Arrays.toString(info), SettingBean.class);
                List<SettingBean> list = new ArrayList<>();
                SettingBean bean = new SettingBean();
                bean.setId(-1);
                bean.setName(WordUtil.getString(R.string.setting_brightness));
                bean.setChecked(SpUtil.getInstance().getBrightness() == 0.05f);
                list.add(bean);

                bean = new SettingBean();
                bean.setId(-3);
                bean.setName(WordUtil.getString(R.string.setting_msg_window));
                bean.setChecked(CommonAppConfig.getInstance().isShowLiveFloatWindow());
                list.add(bean);

                bean = new SettingBean();
                bean.setId(-2);
                bean.setName(WordUtil.getString(R.string.setting_msg_ring));
                bean.setChecked(SpUtil.getInstance().isImMsgRingOpen());
                list.add(bean);

                bean = new SettingBean();
                bean.setId(-4);
                bean.setName(WordUtil.getString(R.string.setting_msg_language));
                list.add(bean);

                list.addAll(list0);
                bean = new SettingBean();
                bean.setId(-5);
                bean.setName(WordUtil.getString(R.string.setting_exit));
                list.add(bean);
                mAdapter = new SettingAdapter(mContext, list, VersionUtil.getVersion(), getCacheSize());
                mAdapter.setActionListener(SettingActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onItemClick(SettingBean bean, int position) {
        String href = bean.getHref();
        if (TextUtils.isEmpty(href)) {
            if (bean.getId() == -5) {//退出登录
                new DialogUitl.Builder(mContext)
                        .setContent(WordUtil.getString(R.string.logout_confirm))
                        .setConfrimString(WordUtil.getString(R.string.logout_confirm_2))
                        .setCancelable(true)
                        .setIsHideTitle(true)
                        .setBackgroundDimEnabled(true)
                        .setClickCallback(new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                logout();
                            }
                        })
                        .build()
                        .show();

            } else if (bean.getId() == Constants.SETTING_MODIFY_PWD) {//修改密码
                forwardModifyPwd();
            } else if (bean.getId() == Constants.SETTING_UPDATE_ID) {//检查更新
                checkVersion();
            } else if (bean.getId() == Constants.SETTING_CLEAR_CACHE) {//清除缓存
                clearCache(position);
            }
        } else {
            if (bean.getId() == 19) {//注销账号
                CancelConditionActivity.forward(mContext, href);
                return;
            }
            if (bean.getId() == 17) {//意见反馈要在url上加版本号和设备号
                if (!href.contains("?")) {
                    href = StringUtil.contact(href, "?");
                }
                href = StringUtil.contact(href, "&version=", android.os.Build.VERSION.RELEASE, "&model=", android.os.Build.MODEL);
            }
            WebViewActivity.forward(mContext, href);
        }
    }

    @Override
    public void onCheckChanged(SettingBean bean) {
        int id = bean.getId();
        if (id == -1) {
            SpUtil.getInstance().setBrightness(bean.isChecked() ? 0.05f : -1f);
            updateBrightness();
        } else if (id == -2) {
            SpUtil.getInstance().setImMsgRingOpen(bean.isChecked());
        }
    }

    @Override
    public void onLanguageClick() {
        DialogUitl.showStringArrayDialog(mContext, new String[]{
                "简体中文", "English","عربي"
        }, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                String lang = null;
                if ("English"==(text)) {
                    lang = "en";
                }else if ("简体中文"==(text)) {
                    lang = "zh";
                } else {
                    lang ="ar";
                }
                Log.e("curLang",lang);
                changeLanguage(lang);
            }
        });
    }

    /**
     * 切换语言
     */
    private void changeLanguage(String lang) {
           saveToSharedPref(lang);
            doRestart();
    }

    private void saveToSharedPref(String lang) {
        SharedPreferences preferences=getSharedPreferences("myCurrentLang",MODE_PRIVATE);
        preferences.edit().putString("current_lang",lang).commit();
    }

    public void doRestart() {
        try {
            //fetch the packagemanager so we can get the default launch activity
            // (you can replace this intent with any other activity if you want
            PackageManager pm = getApplicationContext().getPackageManager();
            //check if we got the PackageManager
            if (pm != null) {
                //create the intent with the default start activity for your application
                Intent mStartActivity = pm.getLaunchIntentForPackage(
                        getApplicationContext().getPackageName()
                );
                if (mStartActivity != null) {
                    mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //create a pending intent so the application is restarted after System.exit(0) was called.
                    // We use an AlarmManager to call this intent in 100ms
                    int mPendingIntentId = 223344;
                    PendingIntent mPendingIntent = PendingIntent
                            .getActivity( getApplicationContext(), mPendingIntentId, mStartActivity,
                                    PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager mgr = (AlarmManager)  getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
                    //kill the application
                    System.exit(0);
                } else {
                    Log.e(TAG, "Was not able to restart application, mStartActivity null");
                }
            } else {
                Log.e(TAG, "Was not able to restart application, PM null");
            }
        } catch (Exception ex) {
            Log.e(TAG, "Was not able to restart application");
        }

    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (VersionUtil.isLatest(configBean.getVersion())) {
                        ToastUtil.show(R.string.version_latest);
                    } else {
                        VersionUtil.showDialog(mContext, configBean, configBean.getDownloadApkUrl());
                    }
                }
            }
        });

    }

    /**
     * 退出登录
     */
    private void logout() {
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出IM
        ImMessageUtil.getInstance().logoutImClient();
        //友盟统计登出
        UmengUtil.userLogout();
        EventBus.getDefault().post(new LoginChangeEvent(false, false));
        finish();
    }

    /**
     * 修改密码
     */
    private void forwardModifyPwd() {
        startActivity(new Intent(mContext, ModifyPwdActivity.class));
    }

    /**
     * 获取缓存
     */
    private String getCacheSize() {
        return ImgLoader.getCacheSize();
    }

    /**
     * 清除缓存
     */
    private void clearCache(final int position) {
        final Dialog dialog = DialogUitl.loadingDialog(mContext, getString(R.string.setting_clear_cache_ing));
        dialog.show();
        ImgLoader.clearImageCache();
        File gifGiftDir = new File(CommonAppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (mAdapter != null) {
                    mAdapter.setCacheString(getCacheSize());
                    mAdapter.notifyItemChanged(position);
                }
                ToastUtil.show(R.string.setting_clear_cache);
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_SETTING_LIST);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        MainHttpUtil.cancel(MainHttpConsts.SET_LIVE_WINDOW);
        super.onDestroy();
    }


}
