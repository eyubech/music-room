package com.musicroom.app.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LocalNetworkTest {

    @Test
    public void privateIpv4Ranges() {
        assertTrue(LocalNetwork.isPrivateAddress("http://10.0.2.2:8080/"));
        assertTrue(LocalNetwork.isPrivateAddress("http://172.16.0.5/"));
        assertTrue(LocalNetwork.isPrivateAddress("http://172.31.255.1/"));
        assertTrue(LocalNetwork.isPrivateAddress("http://192.168.1.20:8080/"));
        assertTrue(LocalNetwork.isPrivateAddress("http://169.254.10.1/"));
    }

    @Test
    public void publicAndLoopbackAddresses() {
        assertFalse(LocalNetwork.isPrivateAddress("https://api.example.com/"));
        assertFalse(LocalNetwork.isPrivateAddress("http://172.32.0.1/"));
        assertFalse(LocalNetwork.isPrivateAddress("http://8.8.8.8/"));
        assertFalse(LocalNetwork.isPrivateAddress("http://127.0.0.1:8080/"));
        assertFalse(LocalNetwork.isPrivateAddress("http://localhost:8080/"));
    }

    @Test
    public void mdnsAndIpv6() {
        assertTrue(LocalNetwork.isPrivateAddress("http://music-server.local/"));
        assertTrue(LocalNetwork.isPrivateAddress("http://[fd12:3456::1]:8080/"));
        assertTrue(LocalNetwork.isPrivateAddress("http://[fe80::1]/"));
        assertFalse(LocalNetwork.isPrivateAddress("http://[2001:db8::1]/"));
    }

    @Test
    public void invalidInput() {
        assertFalse(LocalNetwork.isPrivateAddress(null));
        assertFalse(LocalNetwork.isPrivateAddress("not a url"));
    }
}
