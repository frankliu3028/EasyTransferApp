package com.frankliu.easytransferapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.frankliu.easytransferapp.R;
import com.frankliu.easytransferapp.adapter.DeviceAdapter;
import com.frankliu.easytransferapp.entity.DeviceInfo;
import com.frankliu.easytransferapp.protocol.ErrorCode;
import com.frankliu.easytransferapp.sd.SDClient;
import com.frankliu.easytransferapp.sd.SDClientCallback;
import com.frankliu.easytransferapp.sd.SDServer;
import com.frankliu.easytransferapp.sd.SDServerCallback;
import com.frankliu.easytransferapp.utils.Util;

import java.lang.reflect.Array;
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
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

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
            Toast.makeText(getActivity(), "refreshing", Toast.LENGTH_SHORT).show();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, rootView);
        rvDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
        deviceAdapter = new DeviceAdapter(null);
        rvDevice.setAdapter(deviceAdapter);
        rvDevice.setItemAnimator(new DefaultItemAnimator());
        rvDevice.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        if(!isWifiConnected()){
            Log.e(TAG, "wifi is not connected");
            return null;
        }
        String wifiIp = getWifiIpAddress();
        Toast.makeText(getActivity(), "ip" + wifiIp, Toast.LENGTH_SHORT).show();
        sdServer = new SDServer(wifiIp, new SDServerCallback() {
            @Override
            public void serviceStartResults(int errorCode) {
                if(ErrorCode.SD_START_ERROR_PORT_USED == errorCode){
                    Log.e(TAG, "service discover listening port is used!");
                    //Toast.makeText(getActivity(), "invalid port", Toast.LENGTH_SHORT).show();
                }else if(ErrorCode.FAILURE == errorCode){
                    Log.e(TAG, "start SD Service Failure");
                }else{
                    //Toast.makeText(getActivity(), "SD service start", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "SD Service Start");
                }
            }
        });
        sdServer.start();
        getMyHostname();

        deviceAdapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                FilePickerBuilder.getInstance().setMaxCount(10)
                        .setActivityTheme(R.style.LibAppTheme)
                        .enableVideoPicker(true)
                        .pickFile(DeviceFragment.this);
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        return rootView;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case FilePickerConst.REQUEST_CODE_DOC:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    ArrayList<String> docPaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    for(String s:docPaths){
                        System.out.println("choose:" + s);
                    }
                }
                break;
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
