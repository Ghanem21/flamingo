package com.flamingolive.im.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.flamingolive.common.CommonAppConfig;
import com.flamingolive.common.Constants;
import com.flamingolive.common.activity.AbsActivity;
import com.flamingolive.common.utils.RouteUtil;
import com.flamingolive.common.utils.ToastUtil;
import com.flamingolive.im.R;
import com.flamingolive.im.bean.ImConUserBean;
import com.flamingolive.im.views.ImConversationViewHolder;

/**
 * Created by cxf on 2018/10/24.
 */

public class ChatActivity extends AbsActivity {

    private ImConversationViewHolder mImConversationViewHolder;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void main() {
        CommonAppConfig.getInstance().setTopActivityType(Constants.PUSH_TYPE_MESSAGE);
        mImConversationViewHolder = new ImConversationViewHolder(mContext, (ViewGroup) findViewById(R.id.root), ImConversationViewHolder.TYPE_ACTIVITY);
        mImConversationViewHolder.setActionListener(new ImConversationViewHolder.ActionListener() {
            @Override
            public void onCloseClick() {
                onBackPressed();
            }

            @Override
            public void onItemClick(ImConUserBean bean) {
                if (Constants.MALL_GOODS_ORDER.equals(bean.getId())) {
                    if (CommonAppConfig.getInstance().isTeenagerType()) {
                        ToastUtil.show(com.flamingolive.common.R.string.a_137);
                    }else{
                        RouteUtil.forward(mContext,"com.flamingolive.mall.activity.OrderMessageActivity");
                    }
                } else {
                    ChatRoomActivity.forward(mContext, bean, bean.getAttent() == 1, false);
                }
            }
        });
        mImConversationViewHolder.addToParent();
        mImConversationViewHolder.loadData();
    }

    @Override
    protected void onDestroy() {
        CommonAppConfig.getInstance().setTopActivityType(0);
        if (mImConversationViewHolder != null) {
            mImConversationViewHolder.release();
        }
        super.onDestroy();
    }
}
