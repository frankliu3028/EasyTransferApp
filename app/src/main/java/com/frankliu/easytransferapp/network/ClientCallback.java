package com.frankliu.easytransferapp.network;

import com.frankliu.easytransferapp.entity.TaskSendFile;

public interface ClientCallback {
    void startSendFile(TaskSendFile taskSendFile);
    void receiveFileResponseError();
}
