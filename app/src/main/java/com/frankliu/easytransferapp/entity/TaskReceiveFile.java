package com.frankliu.easytransferapp.entity;

public class TaskReceiveFile extends Task {
    private String fileName;
    private long fileSize;
    private int receivePort;

    public TaskReceiveFile(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        taskType = TASK_TYPE_RECEIVE_FILE;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

}
