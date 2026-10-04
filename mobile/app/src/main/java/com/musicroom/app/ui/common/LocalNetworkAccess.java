package com.musicroom.app.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.musicroom.app.core.LocalNetwork;

/** Runtime side of {@link LocalNetwork}: whether the permission must be asked for a server. */
public final class LocalNetworkAccess {

    public static final String PERMISSION = Manifest.permission.ACCESS_LOCAL_NETWORK;

    private LocalNetworkAccess() {
    }

    /** True when calls to this server will be blocked until the user allows local network access. */
    public static boolean isMissing(Context context, String url) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN
                && LocalNetwork.isPrivateAddress(url)
                && ContextCompat.checkSelfPermission(context, PERMISSION)
                != PackageManager.PERMISSION_GRANTED;
    }
}
