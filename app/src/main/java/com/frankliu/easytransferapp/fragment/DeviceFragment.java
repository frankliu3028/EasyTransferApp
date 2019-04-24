package com.frankliu.easytransferapp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.frankliu.easytransferapp.R;
import com.frankliu.easytransferapp.adapter.DeviceAdapter;
import com.frankliu.easytransferapp.adapter.OnItemClickListener;
import com.frankliu.easytransferapp.entity.DeviceInfo;
import com.frankliu.easytransferapp.entity.TaskSendFile;
import com.frankliu.easytransferapp.network.Client;
import com.frankliu.easytransferapp.network.ClientCallback;
import com.frankliu.easytransferapp.protocol.ErrorCode;
import com.frankliu.easytransferapp.sd.SDClient;
import com.frankliu.easytransferapp.sd.SDClientCallback;
import com.frankliu.easytransferapp.sd.SDServer;
import com.frankliu.easytransferapp.sd.SDServerCallback;
import com.frankliu.easytransferapp.service.TaskService;
import com.frankliu.easytransferapp.utils.Util;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceFragment extends Fragment {

    private final String TAG = DeviceFragment.class.getSimpleName();

    @BindView(R.id.rv_device) RecyclerView rvDevice;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_my_hostname)
    TextView tvMyHostname;

    private DeviceAdapter deviceAdapter;
    private SDServer sdServer;

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //Toast.makeText(getActivity(), "refreshing", Toast.LENGTH_SHORT).show();
            Observable.create(new ObservableOnSubscribe<ArrayList<DeviceInfo>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<DeviceInfo>> emitter) throws Exception {
                    SDClient sdClient = new SDClient(new SDClientCallback() {
                        @Override
                        public void discoverDevices(ArrayList<DeviceInfo> deviceInfoList) {
                            emitter.onNext(deviceInfoList);
                        }
                    });
                    sdClient.start();
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<DeviceInfo>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            //swipeRefreshLayout.setRefreshing(true);
                        }

                        @Override
                        public void onNext(ArrayList<DeviceInfo> deviceInfos) {
                            deviceAdapter.updateData(deviceInfos);
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onComplete() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
        }
    };

    private ProgressDialog progressDialog;
    private DialogSelectionListener dialogSelectionListener = new DialogSelectionListener() {
        @Override
        public void onSelectedFilePaths(String[] files) {
            if(files.length > 1){
                Toast.makeText(getActivity(), "暂时只支持1个文件", Toast.LENGTH_SHORT).show();
                return;
            }
            if(currentSelectDevice == null){
                Log.w(TAG,"current select device is null");
                return;
            }
            Observable.create(new ObservableOnSubscribe<TaskSendFile>() {
                @Override
                public void subscribe(ObservableEmitter<TaskSendFile> emitter) throws Exception {
                    Client client = new Client(currentSelectDevice, new File(files[0]), new ClientCallback() {
                        @Override
                        public void startSendFile(TaskSendFile taskSendFile) {
                            emitter.onNext(taskSendFile);
                        }

                        @Override
                        public void receiveFileResponseError() {
                            Toast.makeText(getActivity(), "对方出错", Toast.LENGTH_SHORT).show();
                            emitter.onComplete();
                        }
                    });
                    client.start();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TaskSendFile>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            getProgressDialog().show();
                        }

                        @Override
                        public void onNext(TaskSendFile taskSendFile) {
                            Log.w(TAG, "onNext,taskSendFile:" + taskSendFile);
                            taskBinder.addTask(taskSendFile);
                            getProgressDialog().cancel();
                            Toast.makeText(getActivity(), "创建任务成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            getProgressDialog().cancel();
                            Toast.makeText(getActivity(), "创建任务失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            getProgressDialog().cancel();
                        }
                    });
        }
    };

    private DeviceInfo currentSelectDevice;
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            currentSelectDevice = deviceAdapter.getData().get(position);
            selectFile();
        }

        @Override
        public void onItemLongClick(int position) {

        }
    };

    private TaskService.TaskBinder taskBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            taskBinder = (TaskService.TaskBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), TaskService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if(!isWifiConnected()){
            Log.e(TAG, "wifi is not connected");
            return;
        }
        String wifiIp = getWifiIpAddress();
        sdServer = new SDServer(wifiIp, new SDServerCallback() {
            @Override
            public void serviceStartResults(int errorCode) {
                if(ErrorCode.SD_START_ERROR_PORT_USED == errorCode){
                    Log.e(TAG, "service discover listening port is used!");
                }else if(ErrorCode.FAILURE == errorCode){
                    Log.e(TAG, "start SD Service Failure");
                }else{
                    Log.d(TAG, "SD Service Start");
                }
            }
        });
        sdServer.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, rootView);
        rvDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(deviceAdapter == null){
            deviceAdapter = new DeviceAdapter(null);
        }
        rvDevice.setAdapter(deviceAdapter);
        rvDevice.setItemAnimator(new DefaultItemAnimator());
        rvDevice.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        getMyHostname();

        deviceAdapter.setOnItemClickListener(onItemClickListener);
        return rootView;
    }

    private ProgressDialog getProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("please wait...");
        }
        return progressDialog;
    }

    private String getWifiIpAddress(){
        Context context = getActivity().getApplicationContext();
        if(context == null){
            Log.e(TAG, "context is null");
            return null;
        }
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(isWifiConnected()){
            int ipAsInt = wm.getConnectionInfo().getIpAddress();
            if(ipAsInt == 0){
                return null;
            }else{
                String ipStr = Util.intIp2string(ipAsInt);
                return ipStr;
            }
        }
        return null;
    }

    private boolean isWifiConnected(){
        Context context = getActivity().getApplicationContext();
        if(context == null){
            Log.e(TAG, "context is null");
            return false;
        }
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isConnected = wifiInfo.isConnected();
            return isConnected;
        }
        return false;
    }

    private void selectFile(){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(getActivity(),properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(dialogSelectionListener);
        dialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if(dialog!=null)
//                    {   //Show dialog if the read permission has been granted.
//                        dialog.show();
//                    }
                }
                else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(getActivity(),"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getMyHostname(){
        String deivceId = Build.MODEL;
        Log.d(TAG, "model:" + deivceId);
        tvMyHostname.setText(deivceId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sdServer != null && sdServer.isAlive()){
            sdServer.close();
        }
    }
}
