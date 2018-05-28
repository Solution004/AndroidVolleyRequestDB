package com.dummy.androidvolleyrequestdb.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dummy.androidvolleyrequestdb.R;
import com.dummy.androidvolleyrequestdb.model.Data;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private ArrayList<Data> mHomeArrayList;

    public HomeAdapter(Context mContext, ArrayList<Data> list) {
        this.mHomeArrayList = list;
    }


    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_home, parent, false);
        return new HomeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HomeAdapter.MyViewHolder holder, int position) {
        holder.mMessageTextView.setText(mHomeArrayList.get(position).getEmail());
        holder.mDeleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mHomeArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mMessageTextView;
        private TextView mDeleteTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mMessageTextView = itemView.findViewById(R.id.tv_message);
            mDeleteTextView = itemView.findViewById(R.id.tv_delete);
        }
    }
}