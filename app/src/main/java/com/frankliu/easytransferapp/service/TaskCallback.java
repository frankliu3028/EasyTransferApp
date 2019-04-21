package com.frankliu.easytransferapp.service;

import com.frankliu.easytransferapp.entity.Task;

public interface TaskCallback {
    void taskReady(Task task);
    void taskFinished(Task task);
}
