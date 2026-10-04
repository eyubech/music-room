package com.musicroom.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.musicroom.app.R;
import com.musicroom.app.core.Resource;
import com.musicroom.app.core.Validators;
import com.musicroom.app.databinding.FragmentResetPasswordBinding;
import com.musicroom.app.ui.common.Ui;

/** Sets a new password with the code received by email. */
public class ResetPasswordFragment extends Fragment {

    public static final String ARG_EMAIL = "email";

    private FragmentResetPasswordBinding binding;
    private AuthViewModel viewModel;
    private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        email = requireArguments().getString(ARG_EMAIL, "");
        binding.message.setText(getString(R.string.reset_message, email));
        binding.resetButton.setOnClickListener(v -> submit());
        Ui.onImeDone(binding.confirmInput, binding.resetButton);
        viewModel.action().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        Ui.clearErrors(binding.codeLayout, binding.passwordLayout, binding.confirmLayout);
        String code = Ui.text(binding.codeLayout);
        String password = Ui.rawText(binding.passwordLayout);
        boolean valid = true;
        if (!Validators.isVerificationCode(code)) {
            binding.codeLayout.setError(getString(R.string.error_code_invalid));
            valid = false;
        }
        if (!Validators.isStrongPassword(password)) {
            binding.passwordLayout.setError(getString(R.string.error_password_weak));
            valid = false;
        }
        if (!password.equals(Ui.rawText(binding.confirmLayout))) {
            binding.confirmLayout.setError(getString(R.string.error_password_mismatch));
            valid = false;
        }
        if (valid) {
            Ui.hideKeyboard(binding.getRoot());
            viewModel.resetPassword(email, code, password);
        }
    }

    private void render(Resource<Void> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.resetButton);
        if (result.isLoading() || !result.markHandled()) {
            return;
        }
        if (result.isSuccess()) {
            NavHostFragment.findNavController(this).popBackStack(R.id.loginFragment, false);
            Ui.showOnActivity(requireActivity(), R.string.reset_done);
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
