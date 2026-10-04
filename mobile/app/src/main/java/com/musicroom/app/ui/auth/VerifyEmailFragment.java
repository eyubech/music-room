package com.musicroom.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.musicroom.app.R;
import com.musicroom.app.core.Resource;
import com.musicroom.app.core.Validators;
import com.musicroom.app.databinding.FragmentVerifyEmailBinding;
import com.musicroom.app.network.dto.TokenResponse;
import com.musicroom.app.ui.common.Ui;

/** Mandatory email validation for email / password accounts (subject V.1). */
public class VerifyEmailFragment extends Fragment {

    public static final String ARG_EMAIL = "email";

    private FragmentVerifyEmailBinding binding;
    private AuthViewModel viewModel;
    private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVerifyEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        email = requireArguments().getString(ARG_EMAIL, "");
        binding.message.setText(getString(R.string.verify_message, email));

        binding.verifyButton.setOnClickListener(v -> submit());
        Ui.onImeDone(binding.codeInput, binding.verifyButton);
        binding.resendButton.setOnClickListener(v -> viewModel.resendVerification(email));

        // Success stores the session: MainActivity then switches to the app.
        viewModel.session().observe(getViewLifecycleOwner(), this::renderVerify);
        viewModel.action().observe(getViewLifecycleOwner(), this::renderResend);
    }

    private void submit() {
        binding.codeLayout.setError(null);
        String code = Ui.text(binding.codeLayout);
        if (!Validators.isVerificationCode(code)) {
            binding.codeLayout.setError(getString(R.string.error_code_invalid));
            return;
        }
        Ui.hideKeyboard(binding.getRoot());
        viewModel.verifyEmail(email, code);
    }

    private void renderVerify(Resource<TokenResponse> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.verifyButton, binding.resendButton);
        if (result.isError() && result.error != null && result.markHandled()) {
            binding.codeLayout.setError(Ui.errorMessage(requireContext(), result.error));
        }
    }

    private void renderResend(Resource<Void> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.verifyButton, binding.resendButton);
        if (result.isLoading() || !result.markHandled()) {
            return;
        }
        if (result.isSuccess()) {
            Ui.showMessage(binding.getRoot(), R.string.verify_resent);
        } else if (result.error != null) {
            Ui.showError(binding.getRoot(), result.error);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
