package com.sinpm.app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinpm.app.R;
import com.sinpm.app.Utils.UiUtils;
import com.sinpm.app.beans.PayTimesBean;

import java.util.ArrayList;
import java.util.List;


public class TimeChooseAdapter extends RecyclerView.Adapter<TimeChooseAdapter.ViewHolder> {
    Context context;
    List<PayTimesBean> list = new ArrayList<>();

    public TimeChooseAdapter(Context context, List<PayTimesBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.timechoose_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.timeTv.setText(list.get(position).getTime()+"分钟");
        holder.moneyTv.setText("¥"+ list.get(position).getPayMoney());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.startAnimator(v);
                onItemClickListener.OnItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTv,moneyTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.time_tv);
            moneyTv = itemView.findViewById(R.id.money_tv);
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void OnItemClick(int positon);
    }
}
