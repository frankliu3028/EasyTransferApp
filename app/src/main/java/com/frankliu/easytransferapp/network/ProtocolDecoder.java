package com.frankliu.easytransferapp.network;

import android.util.Log;

import com.frankliu.easytransferapp.protocol.BasicProtocol;
import com.frankliu.easytransferapp.protocol.UtilProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProtocolDecoder extends ByteToMessageDecoder {

    private final String TAG = ProtocolDecoder.class.getSimpleName();
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Log.w(TAG, "readableBytes: " + in.readableBytes());
        if(in.readableBytes() > 4){
            int length = in.readInt();
            if(in.readableBytes() < length - 4){
                in.resetReaderIndex();
                return;
            }
            byte[] msgBytes = new byte[length - 4];
            in.readBytes(msgBytes);
            BasicProtocol basicProtocol = UtilProtocol.readFromBytesWithoutLength(msgBytes);
            out.add(basicProtocol);
        }
    }
}
