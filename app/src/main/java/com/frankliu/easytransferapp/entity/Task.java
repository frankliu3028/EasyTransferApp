package com.frankliu.easytransferapp.entity;

public class Task {
    public static final int TASK_TYPE_SEND_FILE = 0;
    public static final int TASK_TYPE_RECEIVE_FILE = 1;

    private int taskId;
    protected int taskType;
    private int progress;
    private String peerip;
    private String peerDeviceName;

    public Task(){

    }

    public int getTaskType(){
        return taskType;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getPeerip() {
        return peerip;
    }

    public void setPeerip(String peerip) {
        this.peerip = peerip;
    }

    public String getPeerDeviceName() {
        return peerDeviceName;
    }

    public void setPeerDeviceName(String peerDeviceName) {
        this.peerDeviceName = peerDeviceName;
    }
}
