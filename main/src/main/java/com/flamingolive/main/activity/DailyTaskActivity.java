package com.flamingolive.main.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.http.HttpCallback;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.live.http.LiveHttpConsts;
import com.flamingolive.live.http.LiveHttpUtil;
import com.flamingolive.main.R;
import com.flamingolive.live.adapter.DailyTaskAdapter;
import com.flamingolive.live.bean.DailyTaskBean;

import java.util.List;

public class DailyTaskActivity extends AbsActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_daily_task;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.daily_task));
        LiveHttpUtil.getDailyTask("", 0, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    TextView tip = findViewById(R.id.tip);
                    tip.setText(obj.getString("tip_m"));
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    List<DailyTaskBean> list = JSON.parseArray(obj.getString("list"), DailyTaskBean.class);
                    recyclerView.setAdapter(new DailyTaskAdapter(mContext, list));
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_DAILY_TASK);
        LiveHttpUtil.cancel(LiveHttpConsts.DAILY_TASK_REWARD);
        super.onDestroy();
    }
}
