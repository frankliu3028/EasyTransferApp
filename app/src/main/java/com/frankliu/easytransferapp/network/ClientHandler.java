package com.frankliu.easytransferapp.network;

import android.util.Log;

import com.frankliu.easytransferapp.entity.DeviceInfo;
import com.frankliu.easytransferapp.entity.Task;
import com.frankliu.easytransferapp.entity.TaskSendFile;
import com.frankliu.easytransferapp.protocol.BasicProtocol;
import com.frankliu.easytransferapp.protocol.ErrorCode;
import com.frankliu.easytransferapp.protocol.MsgId;
import com.frankliu.easytransferapp.protocol.ProtocolFactory;
import com.frankliu.easytransferapp.utils.Util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final String TAG = ClientHandler.class.getSimpleName();

    private ClientCallback callback;
    private DeviceInfo deviceInfo;
    private File file;

    public ClientHandler(DeviceInfo deviceInfo, File file, ClientCallback callback){
        this.deviceInfo = deviceInfo;
        this.file = file;
        this.callback = callback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        BasicProtocol basicProtocol = ProtocolFactory.createFileSendRequest(file);
        ctx.writeAndFlush(basicProtocol);
        Log.w(TAG, "send msg:" + basicProtocol.toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicProtocol basicProtocol = (BasicProtocol)msg;
        Log.w(TAG, "receive msg:" + basicProtocol.toString());
        switch (basicProtocol.getMsgId()){
            case MsgId.FILE_SEND_RESPONSE:
                if(basicProtocol.getErrorCode() == ErrorCode.SUCCESS){
                    int port = Util.byteArrayToInt(basicProtocol.getDataArray());
                    TaskSendFile taskSendFile = new TaskSendFile(deviceInfo.getIp(), port, file);
                    callback.startSendFile(taskSendFile);
                    ctx.close();
                }else if(basicProtocol.getErrorCode() == ErrorCode.FAILURE){
                    callback.receiveFileResponseError();
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
