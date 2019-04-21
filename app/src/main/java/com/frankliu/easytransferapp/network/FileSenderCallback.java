package com.frankliu.easytransferapp.network;

public interface FileSenderCallback {
    void currentProgress(int progress);
    void finish();
}
