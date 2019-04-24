package com.frankliu.easytransferapp.network;

import android.util.Log;

import com.frankliu.easytransferapp.utils.Config;
import com.frankliu.easytransferapp.utils.Util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiver implements Runnable{

    private final String TAG = FileReceiver.class.getSimpleName();

    private String fileName;
    private long fileSize;
    private FileReceiverCallback callback;
    private ServerSocket socket;
    private File fileWillBeSaved;

    private final int BLOCK_SIZE = 1024;

    public FileReceiver(String fileName, long fileSize, FileReceiverCallback callback){
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.callback = callback;
    }

    @Override
    public void run() {
        Socket worker = null;
        try{
            int listenPort = Util.getAValidPort();
            Log.w(TAG, "get port:" + listenPort);
            socket = new ServerSocket(listenPort);
            callback.ready(listenPort);
            worker = socket.accept();
            receiveFileBySocket(new File(Config.fileSaveDir), worker.getInputStream());
            callback.finish();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(socket != null){
                    socket.close();
                }
                if(worker != null){
                    worker.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }

    private void receiveFileBySocket(File fileSavePath, InputStream inputStream)
    {
        if(!fileSavePath.isDirectory())
        {
            Log.e(TAG, "fileSavePath must be a directory!");
            return;
        }
        byte[] fileNameLenByte = new byte[4];
        try {
            int readTemp = inputStream.read(fileNameLenByte);
            if(readTemp != 4)
            {
                Log.e(TAG, "read fileNameLenByte error!readTemp is :"+readTemp);
                return;
            }
            int fileNameLen = Util.byteArrayToInt(fileNameLenByte);
            byte[] fileNameByte = new byte[fileNameLen];
            readTemp = inputStream.read(fileNameByte, 0, fileNameLen);
            if(readTemp != fileNameLen)
            {
                Log.e(TAG, "read fileNameByte error!");
                return;
            }
            String fileName = new String(fileNameByte, 0, fileNameByte.length);
            Log.i(TAG, "fileName is :" + fileName);
            fileWillBeSaved = new File(fileSavePath.getPath()+"/"+fileName);
            Log.i(TAG, "file saved to:"+fileWillBeSaved.getAbsolutePath());
            byte[] fileLenByte = new byte[8];
            readTemp = inputStream.read(fileLenByte);
            if(readTemp != 8)
            {
                Log.e(TAG, "read fileLenByte error!");
                return;
            }
            long fileLen = Util.byteArray2Long(fileLenByte);
            Log.i(TAG, "file length is :" + fileLen);

            readISToFile(inputStream, fileWillBeSaved);
            Log.i(TAG, "file receive finished!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void readISToFile(InputStream is, File file) throws IOException
    {
        DataInputStream dis = new DataInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[BLOCK_SIZE];
        int once_read_len = 0;
        long totalSizeHaveRead = 0;
        while((once_read_len = dis.read(buffer)) != -1)
        {
            bos.write(buffer, 0, once_read_len);
            totalSizeHaveRead += once_read_len;
            callback.currentProgress((int)(totalSizeHaveRead*100/fileSize));
        }
        bos.flush();
        bos.close();
        Log.i(TAG, "total read:"+totalSizeHaveRead);
    }

    public String getFileAbPath(){
        if(fileWillBeSaved != null){
            return fileWillBeSaved.getAbsolutePath();
        }
        return null;
    }
}
