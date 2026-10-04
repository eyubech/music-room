package com.musicroom.app.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ApiErrorTest {

    @Test
    public void readsBackendErrorShape() {
        ApiError error = ApiError.parse(403,
                "{\"error\":\"EMAIL_NOT_VERIFIED\",\"message\":\"Verify your email first\",\"details\":{}}");
        assertEquals(ApiError.Kind.HTTP, error.kind);
        assertEquals(403, error.status);
        assertTrue(error.is("EMAIL_NOT_VERIFIED"));
        assertEquals("Verify your email first", error.message);
    }

    @Test
    public void keepsStatusWhenBodyIsNotJson() {
        ApiError error = ApiError.parse(502, "<html>Bad Gateway</html>");
        assertEquals(502, error.status);
        assertNull(error.code);
        assertNull(error.message);
    }

    @Test
    public void handlesEmptyBodyAndUnexpectedJson() {
        assertNull(ApiError.parse(500, null).code);
        assertNull(ApiError.parse(500, "").code);
        assertNull(ApiError.parse(500, "[1,2]").code);
        assertNull(ApiError.parse(400, "{\"error\":{\"nested\":true}}").code);
    }

    @Test
    public void networkErrorHasNoStatus() {
        ApiError error = ApiError.network();
        assertEquals(ApiError.Kind.NETWORK, error.kind);
        assertEquals(0, error.status);
        assertFalse(error.is("ANYTHING"));
    }
}
