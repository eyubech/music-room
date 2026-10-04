package com.musicroom.app.network.dto;

public final class DeviceDto {
    public String id;
    public String name;
    public String platform;
    public String appVersion;
    public String lastSeenAt;
    /** True for the device making the request. */
    public boolean current;
}
