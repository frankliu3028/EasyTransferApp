package com.frankliu.easytransferapp.sd;

import com.frankliu.easytransferapp.entity.DeviceInfo;

import java.util.List;

public interface SDClientCallback {
    void discoverDevices(List<DeviceInfo> deviceInfoList);
}
