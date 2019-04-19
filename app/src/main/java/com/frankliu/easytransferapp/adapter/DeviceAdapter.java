package com.frankliu.easytransferapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frankliu.easytransferapp.R;
import com.frankliu.easytransferapp.entity.DeviceInfo;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private ArrayList<DeviceInfo> mData;

    public DeviceAdapter(ArrayList<DeviceInfo> mData){
        this.mData = mData;
    }

    public void updateData(ArrayList<DeviceInfo> mData){
        this.mData = mData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceInfo deviceInfo = mData.get(position);
        holder.tvDeviceName.setText(deviceInfo.getHostname());
        holder.tvDeviceIp.setText(deviceInfo.getIp());
        holder.tvDevicePort.setText(String.valueOf(deviceInfo.getPort()));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_device_name) TextView tvDeviceName;
        @BindView(R.id.tv_device_ip) TextView tvDeviceIp;
        @BindView(R.id.tv_device_port) TextView tvDevicePort;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
