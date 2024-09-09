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
import com.sinpm.app.beans.DeviceUnLockDTO;

import java.util.ArrayList;
import java.util.List;

public class PayWayAdapter extends RecyclerView.Adapter<PayWayAdapter.ViewHolder> {
    List<DeviceUnLockDTO> payWays = new ArrayList<>();
    Context context;

    public PayWayAdapter(List<DeviceUnLockDTO> payWays, Context context) {
        this.payWays = payWays;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payways_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.payWayNameTv.setText(payWays.get(position).getName());
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
        return payWays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView payWayNameTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            payWayNameTv = itemView.findViewById(R.id.payNameTv);
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
