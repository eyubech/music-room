package com.musicroom.app.ui.common;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.R;
import com.musicroom.app.network.ApiError;

/** Small helpers shared by the screens. */
public final class Ui {

    private Ui() {
    }

    /** Trimmed text of a field. */
    public static String text(TextInputLayout layout) {
        return rawText(layout).trim();
    }

    /** Untrimmed text, for passwords. */
    public static String rawText(TextInputLayout layout) {
        EditText field = layout.getEditText();
        return field == null || field.getText() == null ? "" : field.getText().toString();
    }

    /** Null for an empty field, so optional values are left out of requests. */
    @Nullable
    public static String textOrNull(TextInputLayout layout) {
        String value = text(layout);
        return value.isEmpty() ? null : value;
    }

    public static void setText(TextInputLayout layout, @Nullable String value) {
        EditText field = layout.getEditText();
        if (field != null) {
            field.setText(value == null ? "" : value);
        }
    }

    public static void clearErrors(TextInputLayout... layouts) {
        for (TextInputLayout layout : layouts) {
            layout.setError(null);
        }
    }

    /**
     * The keyboard's Done key presses {@code button}. Going through the button means a request
     * already in flight (button disabled) is not sent twice: keyboards can deliver Done twice.
     */
    public static void onImeDone(EditText field, View button) {
        field.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (button.isEnabled()) {
                    button.performClick();
                }
                return true;
            }
            return false;
        });
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = view.getContext().getSystemService(InputMethodManager.class);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /** Shows a progress indicator and disables the controls while a call runs. */
    public static void setLoading(boolean loading, View progress, View... controls) {
        progress.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        for (View control : controls) {
            control.setEnabled(!loading);
        }
    }

    public static String errorMessage(Context context, ApiError error) {
        if (error.kind == ApiError.Kind.NETWORK) {
            String apiUrl = MusicRoomApp.services(context).config().getApiUrl();
            return context.getString(LocalNetworkAccess.isMissing(context, apiUrl)
                    ? R.string.error_local_network : R.string.error_network);
        }
        if (error.message != null && !error.message.isEmpty()) {
            return error.message;
        }
        switch (error.status) {
            case 403:
                return context.getString(R.string.error_forbidden);
            case 404:
                return context.getString(R.string.error_not_found);
            case 409:
                return context.getString(R.string.error_conflict);
            case 429:
                return context.getString(R.string.error_rate_limited);
            default:
                return context.getString(error.status >= 500 ? R.string.error_server : R.string.error_unknown);
        }
    }

    public static void showError(View view, ApiError error) {
        snackbar(view, errorMessage(view.getContext(), error)).show();
    }

    public static void showMessage(View view, @StringRes int message) {
        snackbar(view, view.getContext().getString(message)).show();
    }

    /** For messages that must outlive the current screen (shown after navigating away). */
    public static void showOnActivity(Activity activity, @StringRes int message) {
        View host = activity.findViewById(R.id.nav_host_fragment);
        // Posted so the destination screen is attached and the snackbar can sit above its button.
        host.post(() -> showMessage(host, message));
    }

    /** Keeps the snackbar above the create button and the bottom navigation. */
    private static Snackbar snackbar(View view, CharSequence text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View root = view.getRootView();
        View fab = root.findViewById(R.id.fab);
        View bottomNav = root.findViewById(R.id.bottom_nav);
        if (fab != null && fab.isShown()) {
            snackbar.setAnchorView(fab);
        } else if (bottomNav != null && bottomNav.isShown()) {
            snackbar.setAnchorView(bottomNav);
        }
        return snackbar;
    }
}
