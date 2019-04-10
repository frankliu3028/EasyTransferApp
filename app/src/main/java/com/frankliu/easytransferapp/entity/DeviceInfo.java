package com.frankliu.easytransferapp.entity;

public class DeviceInfo {

	private String ip;
	private int port;
	private String hostname;
	
	public DeviceInfo(String ip, int port, String hostname) {
		super();
		this.ip = ip;
		this.port = port;
		this.hostname = hostname;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", hostname='" + hostname + '\'' +
                '}';
    }

}
