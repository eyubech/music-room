package com.musicroom.app.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AppConfigTest {

    @Test
    public void addsTrailingSlash() {
        assertEquals("http://10.0.2.2:8080/", AppConfig.normalize("http://10.0.2.2:8080"));
    }

    @Test
    public void defaultsToHttpWhenSchemeIsMissing() {
        assertEquals("http://192.168.1.20:8080/", AppConfig.normalize("192.168.1.20:8080"));
    }

    @Test
    public void keepsHttpsAndPath() {
        assertEquals("https://api.example.com/music/", AppConfig.normalize(" https://api.example.com/music "));
    }

    @Test
    public void rejectsEmptyInput() {
        assertNull(AppConfig.normalize(null));
        assertNull(AppConfig.normalize("   "));
    }

    @Test
    public void rejectsOtherSchemes() {
        assertNull(AppConfig.normalize("ftp://example.com"));
    }

    @Test
    public void rejectsQueryAndFragment() {
        assertNull(AppConfig.normalize("http://example.com/?debug=1"));
        assertNull(AppConfig.normalize("http://example.com/#top"));
    }
}
