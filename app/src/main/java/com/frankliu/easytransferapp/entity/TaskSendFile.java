package com.frankliu.easytransferapp.entity;

import java.io.File;

public class TaskSendFile extends Task {

    private String ip;
    private int port;
    private File file;

    public TaskSendFile(String ip, int port, File file) {
        this.ip = ip;
        this.port = port;
        this.file = file;
        taskType = TASK_TYPE_SEND_FILE;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "TaskSendFile{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", file=" + file.getName() +
                '}';
    }
}
