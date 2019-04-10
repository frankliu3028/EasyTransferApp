package com.frankliu.easytransferapp.protocol;

import com.frankliu.easytransferapp.utils.Util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class UtilProtocol {
	
	public static BasicProtocol readFromStream(InputStream is) {
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] lengthByte = new byte[4];
		try {
			bis.read(lengthByte);
			int length = Util.byteArrayToInt(lengthByte);
			byte[] dataByte = new byte[length];
			bis.read(dataByte);
			BasicProtocol p = new BasicProtocol();
			if(p.parse(dataByte)) {
				p.setLength(length);
				return p;
			}else {
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeToStream(OutputStream os, BasicProtocol protocol) {
		try {
			os.write(protocol.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static BasicProtocol readFromBytes(byte[] data) {
		BasicProtocol p = new BasicProtocol();
		if(p.parse(data)) {
			return p;
		}
		return null;
	}

	public static BasicProtocol readFromBytesWithoutLength(byte[] data){
		BasicProtocol p = new BasicProtocol();
		if(p.parseWithoutLength(data)){
			return p;
		}
		return null;
	}
}
