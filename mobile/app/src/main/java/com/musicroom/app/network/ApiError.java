package com.musicroom.app.network;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A failed call. HTTP errors carry the backend's JSON error shape:
 * {@code {"error": "CODE", "message": "...", "details": {...}}}.
 */
public final class ApiError {

    public enum Kind {
        /** The server could not be reached (offline, wrong address, timeout). */
        NETWORK,
        /** The server answered with an error status. */
        HTTP
    }

    public final Kind kind;
    /** HTTP status, 0 for network errors. */
    public final int status;
    /** Machine-readable code, e.g. EMAIL_NOT_VERIFIED. */
    @Nullable
    public final String code;
    /** Human-readable message from the server. */
    @Nullable
    public final String message;

    private ApiError(Kind kind, int status, @Nullable String code, @Nullable String message) {
        this.kind = kind;
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static ApiError network() {
        return new ApiError(Kind.NETWORK, 0, null, null);
    }

    public static ApiError fromResponse(Response<?> response) {
        String body = null;
        try (ResponseBody errorBody = response.errorBody()) {
            if (errorBody != null) {
                body = errorBody.string();
            }
        } catch (IOException ignored) {
            // Keep the status code only.
        }
        return parse(response.code(), body);
    }

    static ApiError parse(int status, @Nullable String body) {
        String code = null;
        String message = null;
        if (body != null && !body.isEmpty()) {
            try {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                code = stringOrNull(json, "error");
                message = stringOrNull(json, "message");
            } catch (RuntimeException ignored) {
                // Not our JSON error shape (e.g. a proxy's HTML page).
            }
        }
        return new ApiError(Kind.HTTP, status, code, message);
    }

    public boolean is(String errorCode) {
        return errorCode.equals(code);
    }

    @Nullable
    private static String stringOrNull(JsonObject json, String key) {
        JsonElement element = json.get(key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }
}
