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
import com.musicroom.app.databinding.FragmentRegisterBinding;
import com.musicroom.app.network.ApiError;
import com.musicroom.app.ui.common.Ui;

/** Email / password sign-up. The account must then be verified with the emailed code. */
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.registerButton.setOnClickListener(v -> submit());
        Ui.onImeDone(binding.confirmInput, binding.registerButton);
        binding.loginButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack());
        viewModel.action().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        Ui.clearErrors(binding.nameLayout, binding.emailLayout, binding.passwordLayout,
                binding.confirmLayout);
        String name = Ui.text(binding.nameLayout);
        String email = Ui.text(binding.emailLayout);
        String password = Ui.rawText(binding.passwordLayout);
        boolean valid = true;
        if (name.isEmpty()) {
            binding.nameLayout.setError(getString(R.string.error_required));
            valid = false;
        }
        if (!Validators.isEmail(email)) {
            binding.emailLayout.setError(getString(R.string.error_email_invalid));
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
            viewModel.register(name, email, password);
        }
    }

    private void render(Resource<Void> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.registerButton);
        if (result.isLoading() || !result.markHandled()) {
            return;
        }
        ApiError error = result.error;
        if (result.isSuccess()) {
            Bundle args = new Bundle();
            args.putString(VerifyEmailFragment.ARG_EMAIL, Ui.text(binding.emailLayout));
            NavHostFragment.findNavController(this).navigate(R.id.action_register_to_verify, args);
        } else if (error != null && error.status == 409) {
            binding.emailLayout.setError(Ui.errorMessage(requireContext(), error));
        } else if (error != null) {
            Ui.showError(binding.getRoot(), error);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
