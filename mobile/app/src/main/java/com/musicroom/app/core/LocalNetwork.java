package com.musicroom.app.core;

import androidx.annotation.Nullable;

import java.util.Locale;

import okhttp3.HttpUrl;

/**
 * Since Android 17, apps need the ACCESS_LOCAL_NETWORK permission to reach private addresses:
 * the dev machine from the emulator (10.0.2.2) or a backend on the LAN (192.168.x.x).
 */
public final class LocalNetwork {

    private LocalNetwork() {
    }

    /** True if the URL's host is a private, link-local or mDNS (.local) address. */
    public static boolean isPrivateAddress(@Nullable String url) {
        HttpUrl parsed = url == null ? null : HttpUrl.parse(url);
        if (parsed == null) {
            return false;
        }
        String host = parsed.host().toLowerCase(Locale.ROOT);
        if (host.endsWith(".local")) {
            return true;
        }
        if (host.contains(":")) {
            // IPv6 unique local (fc00::/7) and link-local (fe80::/10)
            return host.startsWith("fc") || host.startsWith("fd") || host.startsWith("fe8")
                    || host.startsWith("fe9") || host.startsWith("fea") || host.startsWith("feb");
        }
        String[] parts = host.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        int a;
        int b;
        try {
            a = Integer.parseInt(parts[0]);
            b = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false; // a host name, not an IPv4 address
        }
        return a == 10
                || (a == 172 && b >= 16 && b <= 31)
                || (a == 192 && b == 168)
                || (a == 169 && b == 254);
    }
}
