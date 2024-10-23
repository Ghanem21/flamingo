package com.flamingolive.main.views;

import android.content.Context;
import android.view.ViewGroup;

import com.yariksoffice.lingver.Lingver;
import com.flamingolive.common.Constants;
import com.flamingolive.common.views.AbsViewHolder;

/**
 * Created by cxf on 2018/10/26.
 */

public abstract class AbsMainViewHolder extends AbsViewHolder {

    protected boolean mFirstLoadData = true;
    protected boolean mShowed;

    public AbsMainViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
        Lingver.getInstance().setLocale(context, Constants.CUR_LANGUAGE);
    }

    public AbsMainViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public void loadData() {
    }

    protected boolean isFirstLoadData() {
        if (mFirstLoadData) {
            mFirstLoadData = false;
            return true;
        }
        return false;
    }


    public void setShowed(boolean showed) {
        mShowed = showed;
    }

    public boolean isShowed() {
        return mShowed;
    }
}
