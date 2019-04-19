package com.frankliu.easytransferapp.sd;

import com.frankliu.easytransferapp.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public interface SDClientCallback {
    void discoverDevices(ArrayList<DeviceInfo> deviceInfoList);
}
