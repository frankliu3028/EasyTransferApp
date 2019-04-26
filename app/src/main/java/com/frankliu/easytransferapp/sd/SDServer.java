package com.frankliu.easytransferapp.sd;

import android.util.Log;

import com.frankliu.easytransferapp.protocol.BasicProtocol;
import com.frankliu.easytransferapp.protocol.DataFormat;
import com.frankliu.easytransferapp.protocol.ErrorCode;
import com.frankliu.easytransferapp.protocol.MsgId;
import com.frankliu.easytransferapp.protocol.ProtocolFactory;
import com.frankliu.easytransferapp.protocol.UtilProtocol;
import com.frankliu.easytransferapp.utils.Config;
import com.frankliu.easytransferapp.utils.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public class SDServer extends Thread{

    private String TAG = SDServer.class.getSimpleName();
    private MulticastSocket socket;
    private SDServerCallback callback;
    private String ip;
    private boolean isRunning = true;

    public SDServer(String ip, SDServerCallback callback){
        if(callback == null){
            Log.e(TAG, "SDServerCallback can not be null");
        }
        this.callback = callback;
        this.ip = ip;
    }

    @Override
    public void run() {
        if(Util.isLocalPortUsing(Config.SERVICE_DISCOVER_LISTEN_PORT)){
            Log.e(TAG, "service_discover_listen_port" + Config.SERVICE_DISCOVER_LISTEN_PORT + "is using");
            callback.serviceStartResults(ErrorCode.SD_START_ERROR_PORT_USED);
            return;
        }
        try{
            //InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName(ip), Config.SERVICE_DISCOVER_LISTEN_PORT);
            socket = new MulticastSocket(Config.SERVICE_DISCOVER_LISTEN_PORT);
            InetAddress multicastInetAddress = InetAddress.getByName(Config.multicastAddress);
            socket.joinGroup(multicastInetAddress);
            socket.setLoopbackMode(true);
            byte[] buffer = new byte[1024];
            DatagramPacket recPacket = new DatagramPacket(buffer, buffer.length);
            callback.serviceStartResults(ErrorCode.SUCCESS);
            while(isRunning){
                socket.receive(recPacket);
                handlePacketReceived(recPacket);
            }
        }catch (IOException e){
            e.printStackTrace();
            callback.serviceStartResults(ErrorCode.FAILURE);
        }

    }

    private void handlePacketReceived(DatagramPacket recPacket){
        BasicProtocol basicProtocol = UtilProtocol.readFromBytes(recPacket.getData());
        if(basicProtocol == null){
            Log.i(TAG, "parse failure, discard packet");
            return;
        }
        if(basicProtocol.getMsgId() != MsgId.SERVICE_DISCOVER_REQUEST){
            Log.w(TAG, "unknown msgid");
            return;
        }

        BasicProtocol retProtocol = ProtocolFactory.createServiceDiscoverResponse(ErrorCode.SUCCESS);

        byte[] retBuffer = retProtocol.getBytes();
        DatagramPacket retPacket = new DatagramPacket(retBuffer, retBuffer.length, recPacket.getSocketAddress());
        try{
            socket.send(retPacket);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void close(){
        Log.d(TAG, "close111111111111111111111111111");
        if(socket != null){
            isRunning = false;
            socket.close();
        }
    }
}
