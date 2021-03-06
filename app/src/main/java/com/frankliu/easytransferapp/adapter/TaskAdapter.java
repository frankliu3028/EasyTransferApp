package com.frankliu.easytransferapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frankliu.easytransferapp.R;
import com.frankliu.easytransferapp.entity.Task;
import com.frankliu.easytransferapp.entity.TaskReceiveFile;
import com.frankliu.easytransferapp.entity.TaskSendFile;
import com.frankliu.easytransferapp.utils.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final String TAG = TaskAdapter.class.getSimpleName();

    private ArrayList<Task> datas;
    private OnItemClickListener onItemClickListener;

    public TaskAdapter(ArrayList<Task> datas){
        this.datas = datas;
    }

    public void updateDatas(ArrayList<Task> datas){
        Log.w(TAG, "updateDatas:" + datas.size());
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addTask(Task task){
        Log.w(TAG, "add task:" + task.toString());
        //datas.add(task);
        notifyItemInserted(getTaskPostion(task));
    }

    public void removeTask(Task task){
        //datas.remove(task);
        notifyItemRemoved(getTaskPostion(task));
    }

    private int getTaskPostion(Task task){
        for(int i = 0; i < datas.size(); i++){
            if(datas.get(i).getTaskId() == task.getTaskId()){
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.w(TAG, "onCreateViewHolder:" + datas.size());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = datas.get(position);
        //Log.w(TAG, "onBindViewHolder:" + position + "    :" + task.toString());
        String taskTypeStr = "unknown";
        double fileSize = 0L;
        if(task.getTaskType() == Task.TASK_TYPE_SEND_FILE){
            taskTypeStr = "发送";
        }else if(task.getTaskType() == Task.TASK_TYPE_RECEIVE_FILE){
            taskTypeStr = "接收";
        }
        String fileName = "";
        if(task.getTaskType() == Task.TASK_TYPE_SEND_FILE){
            fileName = ((TaskSendFile)task).getFile().getName();
            double fileLength = ((TaskSendFile) task).getFile().length();
            fileSize = Util.formatDouble(fileLength/(1024*1024));
        }else if(task.getTaskType() == Task.TASK_TYPE_RECEIVE_FILE){
            fileName = ((TaskReceiveFile)task).getFileName();
            double fileLength = ((TaskReceiveFile) task).getFileSize();
            fileSize = Util.formatDouble(fileLength/(1024*1024));
        }
        holder.tvTaskType.setText(taskTypeStr);
        holder.tvPeerip.setText(task.getPeerip());
        holder.tvFilename.setText(fileName);
        holder.progressBar.setProgress(task.getProgress());
        holder.tvProgressText.setText(task.getProgress() + "%");
        holder.tvFileSize.setText(fileSize + "MB");
    }

    @Override
    public int getItemCount() {
        //int ret = datas == null ? 0 : datas.size();
        //Log.w(TAG, "getItemCount:" + ret);
//        if(ret != 0){
//            for(Task task:datas){
//                Log.w(TAG, "task:" + task.toString());
//            }
//        }

        return datas == null ? 0 : datas.size();
    }

    public void updateItemProgress(int position, int progress){
        //Log.w(TAG, "up:" + position + " :" + progress);
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
        @BindView(R.id.tv_progress_text)
        TextView tvProgressText;
        @BindView(R.id.tv_file_size)
        TextView tvFileSize;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
