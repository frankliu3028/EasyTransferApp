package com.frankliu.easytransferapp.protocol;

import com.frankliu.easytransferapp.utils.Util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FileSendRequest {
    private String fileName;
    private long fileLength;

    public FileSendRequest(String fileName, long fileLength) {
        this.fileName = fileName;
        this.fileLength = fileLength;
    }

    public FileSendRequest(){

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public byte[] getMsgBytes(){
        ByteBuf buf = Unpooled.buffer();
        byte[] fileNameBytes = fileName.getBytes();
        int fileNameLen = fileNameBytes.length;
        buf.writeBytes(Util.int2ByteArrays(fileNameLen));
        buf.writeBytes(fileNameBytes);
        buf.writeBytes(Util.long2ByteArrays(fileLength));
        return buf.array();
    }

    public void parse(byte[] data){
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(data);
        int fileNameLen = buf.readInt();
        byte[] fileNameBytes = new byte[fileNameLen];
        buf.readBytes(fileNameBytes);
        fileName = new String(fileNameBytes, 0, fileNameBytes.length);
        fileLength = buf.readLong();
    }

    @Override
    public String toString() {
        return "FileSendRequest{" +
                "fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                '}';
    }
}
