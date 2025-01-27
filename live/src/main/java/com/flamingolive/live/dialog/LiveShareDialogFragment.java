package com.flamingolive.live.dialog;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.flamingolive.common.dialog.AbsDialogFragment;
import com.flamingolive.common.interfaces.OnItemClickListener;
import com.flamingolive.common.mob.MobBean;
import com.flamingolive.live.R;
import com.flamingolive.live.adapter.LiveShareAdapter;

/**
 * Created by cxf on 2018/10/19.
 * 直播分享弹窗
 */

public class LiveShareDialogFragment extends AbsDialogFragment implements OnItemClickListener<MobBean> {

    private RecyclerView mRecyclerView;
    private ActionListener mActionListener;
    private boolean mNoLink;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_share;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        LiveShareAdapter adapter = new LiveShareAdapter(mContext, mNoLink);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(MobBean bean, int position) {
        if (!canClick()) {
            return;
        }
        dismiss();
        mContext = null;
        if (mActionListener != null) {
            mActionListener.onItemClick(bean.getType());
        }
    }

    public interface ActionListener {
        void onItemClick(String type);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void setNoLink(boolean noLink) {
        mNoLink = noLink;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActionListener = null;
        mContext = null;
    }
}
