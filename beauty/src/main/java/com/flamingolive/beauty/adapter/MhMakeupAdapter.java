package com.flamingolive.beauty.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.flamingolive.beauty.R;
import com.flamingolive.beauty.bean.MakeupBean;
import com.flamingolive.beauty.constant.Constants;
import com.flamingolive.beauty.interfaces.OnItemClickListener;
import com.flamingolive.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.MHSDK;
import com.flamingolive.common.utils.WordUtil;

import java.util.List;

public class MhMakeupAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private List<MakeupBean> mList;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition = -1;
    private int mColor0;
    private int mColor1;
    private OnItemClickListener<MakeupBean> mOnItemClickListener;

    public MhMakeupAdapter(Context context, List<MakeupBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mColor0 = ContextCompat.getColor(context, R.color.mh_textColor2);
        mColor1 = ContextCompat.getColor(context, R.color.mh_global);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();

                MakeupBean bean = mList.get(position);
                if (position == 0){
                    for (MakeupBean item:mList) {
                        item.setChecked(false);
                    }
                    bean.setChecked(true);
                    notifyDataSetChanged();
                }else {

                    MakeupBean bean0 = mList.get(0);
                    bean0.setChecked(false);
                    notifyItemChanged(0, Constants.PAYLOAD);

                    boolean checked = bean.isChecked();
                    bean.setChecked(!checked);
                    notifyItemChanged(position, Constants.PAYLOAD);

                }
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, position);
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<MakeupBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public int getCheckedName() {
        if (mCheckedPosition == -1) {
            return -1;
        }
        return mList.get(mCheckedPosition).getName();
    }

    public MakeupBean getCheckedBean() {
        if (mCheckedPosition == -1) {
            return null;
        }
        return mList.get(mCheckedPosition);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_meiyan_5, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        FrameLayout mSelectBg;
        ImageView mThumb;
        TextView mName;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mSelectBg = itemView.findViewById(R.id.frame_select_bg);

            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(MakeupBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(WordUtil.getString(bean.getName()));
            }
            if (bean.isChecked()) {
                mName.setTextColor(mColor1);
                mThumb.setImageDrawable(bean.getDrawable1());
                if (MHSDK.isEngLish) {
                    mThumb.setImageDrawable(bean.getDrawable0());
                    mSelectBg.setBackgroundResource(R.mipmap.bg_select_en);
                }
            } else {
                mName.setTextColor(mColor0);
                mThumb.setImageDrawable(bean.getDrawable0());
                if (MHSDK.isEngLish) {
                    mSelectBg.setBackgroundResource(0);
                }
            }
        }

    }
}
