package com.frankliu.easytransferapp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;

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
        rvDevice.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        sdServer = new SDServer(new SDServerCallback() {
            @Override
            public void serviceStartResults(int errorCode) {
                if(ErrorCode.SD_START_ERROR_PORT_USED == errorCode){
                    Log.e(TAG, "service discover listening port is used!");
                    //Toast.makeText(getActivity(), "invalid port", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(getActivity(), "SD service start", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "SD Service Start");
                }
            }
        });
        sdServer.start();
        getMyHostname();
        return rootView;
    }

    private void getMyHostname(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String myHostname = Util.getLocalHostname();
                emitter.onNext(myHostname);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        tvMyHostname.setText("my_hostname:" + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sdServer != null && sdServer.isAlive()){
            sdServer.close();
        }
    }
}
