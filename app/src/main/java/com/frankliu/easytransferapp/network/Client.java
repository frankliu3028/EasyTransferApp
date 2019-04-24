package com.frankliu.easytransferapp.network;

import com.frankliu.easytransferapp.entity.DeviceInfo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;

public class Client {

    private DeviceInfo deviceInfo;
    private File file;
    private Channel channel;
    private ClientCallback callback;

    public Client(DeviceInfo deviceInfo, File file, ClientCallback callback){
        this.deviceInfo = deviceInfo;
        this.file = file;
        this.callback = callback;
    }

    public void start(){
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolEncoder(),new ProtocolDecoder(), new ClientHandler(deviceInfo, file, callback));
                            //ch.pipeline().addLast(new ProtocolDecoder());
                        }
                    });
            ChannelFuture f = b.connect(deviceInfo.getIp(), deviceInfo.getPort()).sync();
            channel = f.channel();
            f.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
        }
    }

    public void close(){
        channel.close();
    }
}