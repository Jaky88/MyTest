package com.onyx.test.mytest.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.entity.DataUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab5Fragment extends BaseFragment {

    private Context mContext;
    private RVAdapter mAdapter;
    private List<Pair<String, String>> mDatas;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerview;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fl = inflater.inflate(R.layout.tab05, null);
        ButterKnife.bind(this, fl);
        mContext = Tab5Fragment.this.getActivity();
        initData();
        initView();
        return fl;
    }

    private void initData() {
        mDatas = DataUtil.getInstance(mContext).getData();
    }

    private void initView() {
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerview.setAdapter(mAdapter = new RVAdapter());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {
        public int ITEM_TYPE_CATEGORY = 0;
        public int ITEM_TYPE_PROPERTY = 1;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_recycleview, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.key.setText(mDatas.get(position).first);
            holder.value.setText(mDatas.get(position).second);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView key;
            TextView value;

            public MyViewHolder(View view) {
                super(view);
                key = (TextView) view.findViewById(R.id.item_key);
                value = (TextView) view.findViewById(R.id.item_value);
            }
        }
    }
}
