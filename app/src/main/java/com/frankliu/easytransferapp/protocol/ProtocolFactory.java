package com.frankliu.easytransferapp.protocol;


import com.frankliu.easytransferapp.utils.Config;
import com.frankliu.easytransferapp.utils.Util;

import java.io.File;
import java.nio.ByteBuffer;

public class ProtocolFactory {

    public static BasicProtocol createServiceDiscoverRequest(){
        BasicProtocol res = new BasicProtocol();
        res.setMsgId(MsgId.SERVICE_DISCOVER_REQUEST);
        return res;
    }

    public static BasicProtocol createFileSendResponse(byte errorCode, int port){
        BasicProtocol res = new BasicProtocol();
        res.setMsgId(MsgId.FILE_SEND_RESPONSE);
        res.setErrorCode(errorCode);
        res.setDataArray(Util.int2ByteArrays(port));
        return res;
    }

    public static BasicProtocol createFileSendRequest(File file){
        FileSendRequest fileSendRequest = new FileSendRequest();
        fileSendRequest.setFileName(file.getName());
        fileSendRequest.setFileLength(file.length());
        BasicProtocol basicProtocol = new BasicProtocol();
        basicProtocol.setMsgId(MsgId.FILE_SEND_REQUEST);
        basicProtocol.setDataArray(fileSendRequest.getMsgBytes());
        return basicProtocol;
    }

    public static BasicProtocol createServiceDiscoverResponse(byte errorCode){
        BasicProtocol basicProtocol = new BasicProtocol();
        if(ErrorCode.SUCCESS == errorCode){
            basicProtocol.setErrorCode(ErrorCode.SUCCESS);
            basicProtocol.setMsgId(MsgId.SERVICE_DISCOVER_RESPONSE);
            basicProtocol.setDataFormat(DataFormat.CUSTOM);
            String hostname = Util.getLocalHostname();
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + hostname.getBytes().length);
            byteBuffer.put(Util.int2ByteArrays(Config.FILE_TRANSFER_SERVICE_LISTEN_PORT));
            byteBuffer.put(hostname.getBytes());
            basicProtocol.setDataArray(byteBuffer.array());
            return basicProtocol;
        }
        return null;
    }
}
