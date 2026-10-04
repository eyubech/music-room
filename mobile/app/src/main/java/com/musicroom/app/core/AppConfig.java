package com.musicroom.app.core;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import okhttp3.HttpUrl;

/** Address of the backend API. It can be changed at runtime from Settings (subject V.5). */
public final class AppConfig {

    private static final String KEY_API_URL = "config.api_url";

    private final SharedPreferences prefs;
    private final String defaultApiUrl;

    public AppConfig(SharedPreferences prefs, String defaultApiUrl) {
        this.prefs = prefs;
        this.defaultApiUrl = defaultApiUrl;
    }

    public String getApiUrl() {
        return prefs.getString(KEY_API_URL, defaultApiUrl);
    }

    public String getDefaultApiUrl() {
        return defaultApiUrl;
    }

    /** @param url a value returned by {@link #normalize(String)} */
    public void setApiUrl(String url) {
        prefs.edit().putString(KEY_API_URL, url).apply();
    }

    public void resetApiUrl() {
        prefs.edit().remove(KEY_API_URL).apply();
    }

    /**
     * Turns user input into a Retrofit base URL ({@code http://host:port/path/}), or returns null
     * if it is not a usable http(s) address. A missing scheme defaults to http, since local test
     * servers rarely have TLS.
     */
    @Nullable
    public static String normalize(@Nullable String input) {
        if (input == null) {
            return null;
        }
        String url = input.trim();
        if (url.isEmpty()) {
            return null;
        }
        if (!url.contains("://")) {
            url = "http://" + url;
        }
        HttpUrl parsed = HttpUrl.parse(url);
        if (parsed == null || parsed.host().isEmpty()
                || parsed.query() != null || parsed.fragment() != null) {
            return null;
        }
        String result = parsed.toString();
        return result.endsWith("/") ? result : result + "/";
    }
}
