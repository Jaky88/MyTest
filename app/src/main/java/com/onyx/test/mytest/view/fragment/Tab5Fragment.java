package com.onyx.test.mytest.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.FragmentTab05Model;
import com.onyx.test.mytest.databinding.FragmentTab5Binding;

import me.tatarka.bindingcollectionadapter.LayoutManagers;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab5Fragment extends BaseFragment<FragmentTab5Binding> {

    @Override
    public int getLayout() {
        return R.layout.fragment_tab5;
    }

    @Override
    public void bindData() {
        bindingView.setBean(new FragmentTab05Model(getActivity()));
//        initView();
    }

    private void initView() {
        bindingView.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        bindingView.recyclerview.setAdapter(new RVAdapter());
    }

    class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_recycleview, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.key.setText(bindingView.getBean().getDatas().get(position).first);
            holder.value.setText(bindingView.getBean().getDatas().get(position).second);
        }

        @Override
        public int getItemCount() {
            return bindingView.getBean().getDatas().size();
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
