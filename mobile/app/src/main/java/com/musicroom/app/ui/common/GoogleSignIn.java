package com.musicroom.app.ui.common;

import android.app.Activity;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.musicroom.app.BuildConfig;
import com.musicroom.app.R;

/**
 * Gets a Google ID token through Credential Manager. The app never trusts it itself: the token
 * is sent to the backend, which verifies it with Google and opens a session.
 */
public final class GoogleSignIn {

    public interface Callback {
        void onIdToken(String idToken);

        void onError(@StringRes int message);
    }

    private GoogleSignIn() {
    }

    public static void requestIdToken(Activity activity, Callback callback) {
        String serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID;
        if (serverClientId.isEmpty()) {
            callback.onError(R.string.google_not_configured);
            return;
        }
        GetGoogleIdOption option = new GetGoogleIdOption.Builder()
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .build();
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build();

        CredentialManager.create(activity).getCredentialAsync(activity, request,
                new CancellationSignal(), ContextCompat.getMainExecutor(activity),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse response) {
                        Credential credential = response.getCredential();
                        if (credential instanceof CustomCredential && GoogleIdTokenCredential
                                .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                            try {
                                callback.onIdToken(GoogleIdTokenCredential
                                        .createFrom(credential.getData()).getIdToken());
                            } catch (Exception e) {
                                callback.onError(R.string.google_failed);
                            }
                        } else {
                            callback.onError(R.string.google_failed);
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        if (e instanceof GetCredentialCancellationException) {
                            return;
                        }
                        callback.onError(e instanceof NoCredentialException
                                ? R.string.google_no_account : R.string.google_failed);
                    }
                });
    }
}
