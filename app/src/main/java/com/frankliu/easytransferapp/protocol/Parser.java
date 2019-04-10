package com.frankliu.easytransferapp.protocol;

public class Parser {

    public static FileSendRequest parseFileSendRequest(byte[] data){
        FileSendRequest fileSendRequest = new FileSendRequest();
        fileSendRequest.parse(data);
        return fileSendRequest;
    }
}
