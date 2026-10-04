package com.musicroom.app;

import android.app.Application;
import android.content.Context;

import com.musicroom.app.core.ServiceLocator;

public class MusicRoomApp extends Application {

    private ServiceLocator services;

    @Override
    public void onCreate() {
        super.onCreate();
        services = new ServiceLocator(this);
    }

    public static ServiceLocator services(Context context) {
        return ((MusicRoomApp) context.getApplicationContext()).services;
    }
}
