package com.frankliu.easytransferapp.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frankliu.easytransferapp.R;
import com.frankliu.easytransferapp.adapter.TaskAdapter;
import com.frankliu.easytransferapp.entity.Task;
import com.frankliu.easytransferapp.service.TaskCallback;
import com.frankliu.easytransferapp.service.TaskService;
import com.frankliu.easytransferapp.utils.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskFragment extends Fragment {

    private final String TAG = TaskFragment.class.getSimpleName();

    private TaskService.TaskBinder taskBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            taskBinder = (TaskService.TaskBinder)service;
            taskBinder.setTaskCallback(taskCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private LocalBroadcastManager localBroadcastManager;
    private LocalBroadcastReceiver localBroadcastReceiver;
    private TaskCallback taskCallback = new TaskCallback() {
        @Override
        public void taskReady(Task task) {

        }

        @Override
        public void taskFinished(Task task) {

        }
    };

    @BindView(R.id.rv_task)
    RecyclerView rvTask;

    private ArrayList<Task> taskDatas;
    private TaskAdapter taskAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent bindIntent = new Intent(getActivity(), TaskService.class);
        getActivity().bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_UPDATE_TASK_PROGRESS);
        localBroadcastReceiver = new LocalBroadcastReceiver();
        localBroadcastManager.registerReceiver(localBroadcastReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, rootView);
        rvTask.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTask.setItemAnimator(new DefaultItemAnimator());
        rvTask.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        //taskDatas = taskBinder.getTasks();
        taskAdapter = new TaskAdapter(null);
        rvTask.setAdapter(taskAdapter);
        return rootView;
    }


    class LocalBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case Constant.ACTION_UPDATE_TASK_PROGRESS:
                    int taskId = intent.getIntExtra("taskId", -1);
                    int progress = intent.getIntExtra("progress", -1);
                    if(taskId != -1 && progress != -1){
                        updateTaskProgress(taskId, progress);
                    }else{
                        Log.e(TAG, "broadcast UPDATE_TASK_PROGRESS parameter error");
                    }

                    break;
                    default:
                        break;
            }
        }
    }

    private void updateTaskProgress(int taskId, int progress){
        int position = 0;
        for(Task task:taskDatas){
            if(task.getTaskId() == taskId){
                taskAdapter.updateItemProgress(position, progress);
                return;
            }
            position++;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localBroadcastReceiver);
    }
}
