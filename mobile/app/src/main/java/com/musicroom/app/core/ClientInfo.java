package com.musicroom.app.core;

import android.content.SharedPreferences;
import android.os.Build;

import com.musicroom.app.BuildConfig;

import java.util.Locale;
import java.util.UUID;

/**
 * Describes this installation. Sent as headers with every request so the backend can log
 * platform, device and app version for each action (subject V.6).
 */
public final class ClientInfo {

    private static final String KEY_DEVICE_ID = "client.device_id";

    public final String platform;
    public final String device;
    public final String appVersion;
    /** Random per installation (no hardware identifier), used by the backend's device registry. */
    public final String deviceId;

    private ClientInfo(String platform, String device, String appVersion, String deviceId) {
        this.platform = platform;
        this.device = device;
        this.appVersion = appVersion;
        this.deviceId = deviceId;
    }

    public static ClientInfo create(SharedPreferences prefs) {
        String deviceId = prefs.getString(KEY_DEVICE_ID, null);
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply();
        }
        return new ClientInfo(
                "Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")",
                deviceName(),
                BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")",
                deviceId);
    }

    private static String deviceName() {
        String manufacturer = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER.trim();
        String model = Build.MODEL == null ? "" : Build.MODEL.trim();
        String name = model.toLowerCase(Locale.ROOT).startsWith(manufacturer.toLowerCase(Locale.ROOT))
                ? model
                : manufacturer + " " + model;
        name = asciiOnly(name.trim());
        return name.isEmpty() ? "Unknown" : Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /** HTTP header values must be printable ASCII. */
    static String asciiOnly(String value) {
        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            builder.append(c >= 0x20 && c < 0x7f ? c : '?');
        }
        return builder.toString();
    }
}
