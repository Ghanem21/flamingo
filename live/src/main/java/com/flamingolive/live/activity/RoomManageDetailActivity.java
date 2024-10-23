package com.flamingolive.live.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.flamingolive.common.Constants;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.utils.StringUtil;
import com.flamingolive.common.utils.WordUtil;
import com.flamingolive.live.R;
import com.flamingolive.live.bean.LiveAdminRoomBean;

/**
 * Created by cxf on 2019/4/27.
 */

public class RoomManageDetailActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, LiveAdminRoomBean bean) {
        Intent intent = new Intent(context, RoomManageDetailActivity.class);
        intent.putExtra(Constants.LIVE_ADMIN_ROOM, bean);
        context.startActivity(intent);
    }


    private LiveAdminRoomBean mAdminRoomBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_room_manage_detail;
    }

    @Override
    protected void main() {
        mAdminRoomBean = getIntent().getParcelableExtra(Constants.LIVE_ADMIN_ROOM);
        if (mAdminRoomBean == null) {
            return;
        }
        setTitle(String.format( WordUtil.getString(R.string.live_admin_room),mAdminRoomBean.getUserNiceName()));
        findViewById(R.id.btn_user_shut_up).setOnClickListener(this);
        findViewById(R.id.btn_user_black).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_user_shut_up) {
            LiveShutUpActivity.forward(mContext, mAdminRoomBean.getLiveUid());
        } else if (i == R.id.btn_user_black) {
            LiveBlackActivity.forward(mContext, mAdminRoomBean.getLiveUid());
        }
    }
}