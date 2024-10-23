package com.flamingolive.common.banner.indicator;

import android.view.View;

import androidx.annotation.NonNull;

import com.flamingolive.common.banner.config.IndicatorConfig;
import com.flamingolive.common.banner.listener.OnPageChangeListener;

public interface Indicator extends OnPageChangeListener {
    @NonNull
    View getIndicatorView();

    IndicatorConfig getIndicatorConfig();

    void onPageChanged(int count, int currentPosition);

}
