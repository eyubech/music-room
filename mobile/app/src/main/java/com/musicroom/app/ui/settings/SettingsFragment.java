package com.musicroom.app.ui.settings;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.musicroom.app.R;
import com.musicroom.app.core.AppConfig;
import com.musicroom.app.core.ClientInfo;
import com.musicroom.app.core.Resource;
import com.musicroom.app.databinding.FragmentSettingsBinding;
import com.musicroom.app.databinding.ItemProfileFieldBinding;
import com.musicroom.app.network.dto.HealthDto;
import com.musicroom.app.ui.MainActivity;
import com.musicroom.app.ui.common.Ui;

/** Backend address (configurable for tests, subject V.5) and what the app sends with requests. */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        AppConfig config = viewModel.config();
        if (savedInstanceState == null) {
            Ui.setText(binding.urlLayout, config.getApiUrl());
        }

        binding.saveButton.setOnClickListener(v -> save());
        Ui.onImeDone(binding.urlInput, binding.saveButton);
        binding.testButton.setOnClickListener(v -> {
            String url = validatedUrl();
            if (url != null) {
                ((MainActivity) requireActivity()).withLocalNetworkAccess(url, () -> viewModel.test(url));
            }
        });
        binding.resetButton.setOnClickListener(v -> {
            config.resetApiUrl();
            Ui.setText(binding.urlLayout, config.getDefaultApiUrl());
            binding.urlLayout.setError(null);
            Ui.showMessage(binding.getRoot(), R.string.settings_saved);
        });

        ClientInfo info = viewModel.clientInfo();
        setField(binding.fieldPlatform, R.string.settings_platform, info.platform);
        setField(binding.fieldDevice, R.string.settings_device, info.device);
        setField(binding.fieldAppVersion, R.string.settings_app_version, info.appVersion);
        setField(binding.fieldDeviceId, R.string.settings_device_id, info.deviceId);

        viewModel.health().observe(getViewLifecycleOwner(), this::renderTest);
    }

    private void save() {
        String url = validatedUrl();
        if (url == null) {
            return;
        }
        viewModel.config().setApiUrl(url);
        Ui.setText(binding.urlLayout, url);
        Ui.hideKeyboard(binding.getRoot());
        Ui.showMessage(binding.getRoot(), R.string.settings_saved);
        ((MainActivity) requireActivity()).withLocalNetworkAccess(url, () -> { });
    }

    @Nullable
    private String validatedUrl() {
        String url = AppConfig.normalize(Ui.text(binding.urlLayout));
        binding.urlLayout.setError(url == null ? getString(R.string.error_url_invalid) : null);
        return url;
    }

    private void renderTest(Resource<HealthDto> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.testButton);
        if (result.isSuccess()) {
            String status = result.data != null && result.data.status != null ? result.data.status : "UP";
            binding.testResult.setText(getString(R.string.settings_test_ok, status));
            binding.testResult.setTextColor(themeColor(androidx.appcompat.R.attr.colorPrimary));
        } else if (result.isError() && result.error != null) {
            binding.testResult.setText(Ui.errorMessage(requireContext(), result.error));
            binding.testResult.setTextColor(themeColor(androidx.appcompat.R.attr.colorError));
        } else {
            binding.testResult.setText(null);
        }
    }

    private int themeColor(@AttrRes int attr) {
        TypedValue value = new TypedValue();
        requireContext().getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    private static void setField(ItemProfileFieldBinding field, @StringRes int label, String value) {
        field.label.setText(label);
        field.value.setText(value);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
