package com.frankliu.easytransferapp.network;

public interface FileReceiverCallback {
    void ready(int port);
    void currentProgress(int progress);
    void finish();
}
