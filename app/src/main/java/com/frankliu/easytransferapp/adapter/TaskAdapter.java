package com.frankliu.easytransferapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frankliu.easytransferapp.R;
import com.frankliu.easytransferapp.entity.Task;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private ArrayList<Task> datas;
    private OnItemClickListener onItemClickListener;

    public TaskAdapter(ArrayList<Task> datas){
        this.datas = datas;
    }

    public void updateDatas(ArrayList<Task> datas){
        this.datas = datas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = datas.get(position);
        String taskTypeStr = "unknown";
        if(task.getTaskType() == Task.TASK_TYPE_SEND_FILE){
            taskTypeStr = "SEND";
        }else if(task.getTaskType() == Task.TASK_TYPE_RECEIVE_FILE){
            taskTypeStr = "RECEIVE";
        }

        holder.tvTaskType.setText(taskTypeStr);
        holder.tvPeerip.setText(task.getPeerip());
        holder.tvFilename.setText(task.getPeerDeviceName());
        holder.progressBar.setProgress(task.getProgress());
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void updateItemProgress(int position, int progress){
        datas.get(position).setProgress(progress);
        notifyItemChanged(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_task_type)
        TextView tvTaskType;
        @BindView(R.id.tv_filename)
        TextView tvFilename;
        @BindView(R.id.tv_peerip)
        TextView tvPeerip;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
