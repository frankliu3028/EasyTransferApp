package com.frankliu.easytransferapp.network;

import android.util.Log;

import com.frankliu.easytransferapp.protocol.BasicProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtocolEncoder extends MessageToByteEncoder<BasicProtocol> {

    private final String TAG = ProtocolEncoder.class.getSimpleName();

    @Override
    protected void encode(ChannelHandlerContext ctx, BasicProtocol msg, ByteBuf out) throws Exception {
        Log.w(TAG, "send message:" + msg.toString());
        out.writeBytes(msg.getBytes());
    }
}
