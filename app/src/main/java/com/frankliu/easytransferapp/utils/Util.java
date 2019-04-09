package com.frankliu.easytransferapp.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	
	private static final String TAG = Util.class.getSimpleName();
	
	private static ByteBuffer bb8 = ByteBuffer.allocate(8);
	
	public static int byteArrayToInt(byte[] b) {
		if(b.length != 4) {

			Log.e(TAG,  "byte array turn to INT length must be 4");
			return -1;
		}
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
	}
	
    public static byte[] int2ByteArrays(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }


	public static boolean isPortUsing(String host,int port) throws UnknownHostException{
		boolean flag = false;
		InetAddress theAddress = InetAddress.getByName(host);
		try {
			Socket socket = new Socket(theAddress,port);
			flag = true;
		} catch (IOException e) {
			
		}
		return flag;
	}

	public static boolean isLocalPortUsing(int port){
		boolean flag = true;
		Socket socket = new Socket();
		try{
			InetAddress inetAddress = InetAddress.getByName("localhost");
			InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
			socket.bind(inetSocketAddress);
			flag = false;
		}catch (IOException e){
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
    
    public static byte[] long2ByteArrays(long l) {
    	bb8.clear();
    	bb8.putLong(l);
    	return bb8.array();
    }
    
    public static long byteArray2Long(byte[] ab) {
    	bb8.clear();
    	bb8.put(ab);
    	bb8.flip();
    	return bb8.getLong();
    }
    
    public static String getLocalIpAddress() {
		Socket socket = new Socket();
        try {
            SocketAddress addr = new InetSocketAddress("www.baidu.com", 80);
            socket.connect(addr, 1000);
            InetAddress ia=socket.getLocalAddress();
            String res=ia.getHostAddress();
            return res;
            
        }catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }finally {
            try {
                socket.close();
            } catch (Throwable e) {
            	
            }
        }
	}

	public static String getLocalHostname(){
		String ip = getLocalIpAddress();
		try {
			InetAddress inetAddress = InetAddress.getByName(ip);
			String hostname = inetAddress.getHostName();
			return hostname;
		}catch (UnknownHostException e){
			e.printStackTrace();
		}
		return null;
	}
    
    public static String getCurrentWorkDir() {
    	return System.getProperty("user.dir");
    }
    
    public static String getUserHomeDir() {
    	return System.getProperty("user.home");
    }
    
    public static int bytes2Int(byte[] b, int byteOffset) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        byteBuffer.put(b, byteOffset, 4);
        byteBuffer.flip();
        return byteBuffer.getInt();
	}

	public static String getTimeString() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sd = sdf.format(new Date(System.currentTimeMillis()));
		return sd;
	}

	public static int getAValidPort(){
		for(int i = 10001; i < 65533; i++){
			if(!isLocalPortUsing(i)){
				return i;
			}
		}
		return -1;
	}

}
