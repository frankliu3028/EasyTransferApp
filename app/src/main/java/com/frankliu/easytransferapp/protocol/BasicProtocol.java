package com.frankliu.easytransferapp.protocol;

import com.frankliu.easytransferapp.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class BasicProtocol {
	private int length;
	private byte errorCode;
	private byte msgId;
	private byte dataFormat;
	private byte[] dataArray;
	
	public BasicProtocol() {
		
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(byte errorCode) {
		this.errorCode = errorCode;
	}

	public byte getMsgId() {
		return msgId;
	}

	public void setMsgId(byte msgId) {
		this.msgId = msgId;
	}

	public byte getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(byte dataFormat) {
		this.dataFormat = dataFormat;
	}

	public byte[] getDataArray() {
		return dataArray;
	}

	public void setDataArray(byte[] dataArray) {
		this.dataArray = dataArray;
	}
	
	
	public byte[] getBytes() {
		if(dataArray != null) {
			length = 7 + dataArray.length;
		}else {
			length = 7;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(Util.int2ByteArrays(length));
			baos.write(errorCode);
			baos.write(msgId);
			baos.write(dataFormat);
			if(dataArray != null) {
				baos.write(dataArray);
			}
			
			return baos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean parseWithoutLength(byte[] data) {
		if(data.length <= 3) {
			return false;
		}
		int offset = 0;
		offset += parseErrorCode(data, offset);
		offset += parseMsgId(data, offset);
		offset += parseDataFormat(data, offset);
		parseDataArray(data, offset);
		return true;
	}
	
	public boolean parse(byte[] data) {
		if(data.length <= 3) {
			return false;
		}
		int offset = 0;
		offset += parseLength(data, offset);
		offset += parseErrorCode(data, offset);
		offset += parseMsgId(data, offset);
		offset += parseDataFormat(data, offset);
		parseDataArray(data, offset);
		return true;
	}
	
	private int parseLength(byte[] data, int offset) {
		length = Util.bytes2Int(data, offset);
		return 4;
	}
	
	private int parseErrorCode(byte[] data, int offset) {
		errorCode = data[offset];
		return 1;
	}
	
	private int parseMsgId(byte[] data, int offset) {
		msgId = data[offset];
		return 1;
	}
	
	private int parseDataFormat(byte[] data, int offset) {
		dataFormat = data[offset];
		return 1;
	}
	
	private int parseDataArray(byte[] data, int offset) {
		if(offset >= data.length) {
			return -1;
		}
		dataArray = Arrays.copyOfRange(data, offset, data.length);
		return dataArray.length;
	}

	@Override
	public String toString() {
		return "BasicProtocol [length=" + length + ", errorCode=" + errorCode + ", msgId=" + msgId + ", dataFormat="
				+ dataFormat + "]";
	}
	
	
	
	
	
}
