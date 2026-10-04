package com.musicroom.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.musicroom.app.R;
import com.musicroom.app.core.Resource;
import com.musicroom.app.core.Validators;
import com.musicroom.app.databinding.FragmentLoginBinding;
import com.musicroom.app.network.ApiError;
import com.musicroom.app.network.dto.TokenResponse;
import com.musicroom.app.ui.common.GoogleSignIn;
import com.musicroom.app.ui.common.SettingsMenu;
import com.musicroom.app.ui.common.Ui;

/** First screen of a logged-out user. A successful login switches MainActivity to the app. */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        NavController nav = NavHostFragment.findNavController(this);
        requireActivity().addMenuProvider(new SettingsMenu(nav), getViewLifecycleOwner(),
                Lifecycle.State.RESUMED);

        binding.loginButton.setOnClickListener(v -> submit());
        Ui.onImeDone(binding.passwordInput, binding.loginButton);
        binding.forgotButton.setOnClickListener(v -> nav.navigate(R.id.action_login_to_forgot));
        binding.registerButton.setOnClickListener(v -> nav.navigate(R.id.action_login_to_register));
        binding.googleButton.setOnClickListener(v ->
                GoogleSignIn.requestIdToken(requireActivity(), new GoogleSignIn.Callback() {
                    @Override
                    public void onIdToken(String idToken) {
                        viewModel.loginWithGoogle(idToken);
                    }

                    @Override
                    public void onError(int message) {
                        if (binding != null) {
                            Ui.showMessage(binding.getRoot(), message);
                        }
                    }
                }));

        viewModel.session().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        Ui.clearErrors(binding.emailLayout, binding.passwordLayout);
        String email = Ui.text(binding.emailLayout);
        String password = Ui.rawText(binding.passwordLayout);
        boolean valid = true;
        if (!Validators.isEmail(email)) {
            binding.emailLayout.setError(getString(R.string.error_email_invalid));
            valid = false;
        }
        if (password.isEmpty()) {
            binding.passwordLayout.setError(getString(R.string.error_required));
            valid = false;
        }
        if (valid) {
            Ui.hideKeyboard(binding.getRoot());
            viewModel.login(email, password);
        }
    }

    private void render(Resource<TokenResponse> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.loginButton, binding.googleButton);
        ApiError error = result.error;
        if (!result.isError() || error == null || !result.markHandled()) {
            return;
        }
        if (error.is("EMAIL_NOT_VERIFIED")) {
            Bundle args = new Bundle();
            args.putString(VerifyEmailFragment.ARG_EMAIL, Ui.text(binding.emailLayout));
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_verify, args);
        } else if (error.status == 401) {
            Ui.showMessage(binding.getRoot(), R.string.error_bad_credentials);
        } else {
            Ui.showError(binding.getRoot(), error);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
