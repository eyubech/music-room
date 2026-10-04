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
import com.musicroom.app.databinding.FragmentForgotPasswordBinding;
import com.musicroom.app.ui.common.Ui;

/** Asks the backend to email a reset code. */
public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private AuthViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.sendButton.setOnClickListener(v -> submit());
        Ui.onImeDone(binding.emailInput, binding.sendButton);
        viewModel.action().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        binding.emailLayout.setError(null);
        String email = Ui.text(binding.emailLayout);
        if (!Validators.isEmail(email)) {
            binding.emailLayout.setError(getString(R.string.error_email_invalid));
            return;
        }
        Ui.hideKeyboard(binding.getRoot());
        viewModel.forgotPassword(email);
    }

    private void render(Resource<Void> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.sendButton);
        if (result.isLoading() || !result.markHandled()) {
            return;
        }
        if (result.isSuccess()) {
            Bundle args = new Bundle();
            args.putString(ResetPasswordFragment.ARG_EMAIL, Ui.text(binding.emailLayout));
            NavHostFragment.findNavController(this).navigate(R.id.action_forgot_to_reset, args);
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
