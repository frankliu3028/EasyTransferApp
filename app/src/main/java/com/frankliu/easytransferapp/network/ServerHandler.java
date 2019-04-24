package com.frankliu.easytransferapp.network;

import android.util.Log;

import com.frankliu.easytransferapp.entity.TaskReceiveFile;
import com.frankliu.easytransferapp.protocol.BasicProtocol;
import com.frankliu.easytransferapp.protocol.FileSendRequest;
import com.frankliu.easytransferapp.protocol.MsgId;
import com.frankliu.easytransferapp.protocol.Parser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final String TAG = ServerHandler.class.getSimpleName();
    private ServerCallback callback;

    public ServerHandler(ServerCallback callback){
        this.callback = callback;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicProtocol basicProtocol = (BasicProtocol)msg;
        switch (basicProtocol.getMsgId()){
            case MsgId.FILE_SEND_REQUEST:
                FileSendRequest fileSendRequest = Parser.parseFileSendRequest(basicProtocol.getDataArray());
                TaskReceiveFile taskReceiveFile = new TaskReceiveFile(fileSendRequest.getFileName(), fileSendRequest.getFileLength());
                callback.receiveFile(taskReceiveFile, ctx);
                break;
                default:
                    Log.w(TAG, "unknow msg");
                    break;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
