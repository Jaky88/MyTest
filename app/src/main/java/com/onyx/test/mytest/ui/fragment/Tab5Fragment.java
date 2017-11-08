package com.onyx.test.mytest.ui.fragment;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.FragmentTab05Model;
import com.onyx.test.mytest.databinding.FragmentTab5Binding;
import com.onyx.test.mytest.model.entity.DataUtil;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab5Fragment extends BaseFragment<FragmentTab5Binding> {

    private FragmentTab05Model bean;
    private Context mContext;
    private RVAdapter mAdapter;
    private List<Pair<String, String>> mDatas;


    @Override
    public int getLayout() {
        return R.layout.fragment_tab5;
    }

    @Override
    public void bindData() {
        bean = new FragmentTab05Model(Tab5Fragment.this, config);
        bindingView.setBean(bean);
        initView();
        initData();
    }

    private void initData() {
        mDatas = DataUtil.getInstance(mContext).getData();
    }

    private void initView() {
        bindingView.recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        bindingView.recyclerview.setAdapter(mAdapter = new RVAdapter());
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
