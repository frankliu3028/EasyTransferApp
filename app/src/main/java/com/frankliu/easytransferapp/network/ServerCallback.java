package com.frankliu.easytransferapp.network;

import com.frankliu.easytransferapp.entity.TaskReceiveFile;

import io.netty.channel.ChannelHandlerContext;

public interface ServerCallback {
    void receiveFile(TaskReceiveFile taskReceiveFile, ChannelHandlerContext ctx);
}
