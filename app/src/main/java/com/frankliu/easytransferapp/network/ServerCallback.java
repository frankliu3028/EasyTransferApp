package com.frankliu.easytransferapp.network;

import com.frankliu.easytransferapp.entity.TaskReceiveFile;

public interface ServerCallback {
    void receiveFile(TaskReceiveFile taskReceiveFile);
}
