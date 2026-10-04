package com.musicroom.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.musicroom.app.R;
import com.musicroom.app.core.Resource;
import com.musicroom.app.core.Validators;
import com.musicroom.app.databinding.FragmentEditProfileBinding;
import com.musicroom.app.network.dto.ProfileDto;
import com.musicroom.app.network.dto.ProfileUpdateRequest;
import com.musicroom.app.ui.common.Ui;
import com.musicroom.app.ui.widget.Motion;

import java.util.ArrayList;
import java.util.List;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        for (String genre : getResources().getStringArray(R.array.music_genres)) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_genre_chip, binding.genres, false);
            chip.setId(View.generateViewId());
            chip.setText(genre);
            binding.genres.addView(chip);
        }
        binding.saveButton.setOnClickListener(v -> submit());

        viewModel.profile().observe(getViewLifecycleOwner(), this::renderProfile);
        viewModel.saved().observe(getViewLifecycleOwner(), this::renderSave);
        if (viewModel.formFilled) {
            binding.form.setVisibility(View.VISIBLE);
        } else {
            binding.skeleton.getRoot().setVisibility(View.VISIBLE);
            viewModel.load();
        }
    }

    /** The form stays behind a skeleton until it is filled from the server. */
    private void renderProfile(Resource<ProfileDto> result) {
        if (viewModel.formFilled || result.isLoading()) {
            return;
        }
        if (result.isSuccess() && result.data != null) {
            fill(result.data);
            viewModel.formFilled = true;
        } else if (result.error != null && result.markHandled()) {
            Ui.showError(binding.getRoot(), result.error);
        }
        Motion.crossfade(binding.skeleton.getRoot(), binding.form);
    }

    private void fill(ProfileDto profile) {
        if (profile.publicInfo != null) {
            Ui.setText(binding.displayNameLayout, profile.publicInfo.displayName);
            Ui.setText(binding.bioLayout, profile.publicInfo.bio);
        }
        if (profile.friendsInfo != null) {
            Ui.setText(binding.realNameLayout, profile.friendsInfo.realName);
            Ui.setText(binding.cityLayout, profile.friendsInfo.city);
        }
        if (profile.privateInfo != null) {
            Ui.setText(binding.phoneLayout, profile.privateInfo.phone);
            Ui.setText(binding.birthDateLayout, profile.privateInfo.birthDate);
        }
        if (profile.musicPreferences != null) {
            for (int i = 0; i < binding.genres.getChildCount(); i++) {
                Chip chip = (Chip) binding.genres.getChildAt(i);
                chip.setChecked(profile.musicPreferences.contains(chip.getText().toString()));
            }
        }
    }

    private void submit() {
        Ui.clearErrors(binding.displayNameLayout, binding.birthDateLayout);
        String displayName = Ui.text(binding.displayNameLayout);
        String birthDate = Ui.textOrNull(binding.birthDateLayout);
        boolean valid = true;
        if (displayName.isEmpty()) {
            binding.displayNameLayout.setError(getString(R.string.error_required));
            valid = false;
        }
        if (birthDate != null && !Validators.isPastIsoDate(birthDate)) {
            binding.birthDateLayout.setError(getString(R.string.error_birth_date));
            valid = false;
        }
        if (!valid) {
            return;
        }

        ProfileDto.PublicInfo publicInfo = new ProfileDto.PublicInfo();
        publicInfo.displayName = displayName;
        publicInfo.bio = Ui.textOrNull(binding.bioLayout);
        ProfileDto.FriendsInfo friendsInfo = new ProfileDto.FriendsInfo();
        friendsInfo.realName = Ui.textOrNull(binding.realNameLayout);
        friendsInfo.city = Ui.textOrNull(binding.cityLayout);
        ProfileDto.PrivateInfo privateInfo = new ProfileDto.PrivateInfo();
        privateInfo.phone = Ui.textOrNull(binding.phoneLayout);
        privateInfo.birthDate = birthDate;
        List<String> genres = new ArrayList<>();
        for (int i = 0; i < binding.genres.getChildCount(); i++) {
            Chip chip = (Chip) binding.genres.getChildAt(i);
            if (chip.isChecked()) {
                genres.add(chip.getText().toString());
            }
        }

        Ui.hideKeyboard(binding.getRoot());
        viewModel.save(new ProfileUpdateRequest(publicInfo, friendsInfo, privateInfo, genres));
    }

    private void renderSave(Resource<ProfileDto> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.saveButton);
        if (result.isLoading() || !result.markHandled()) {
            return;
        }
        if (result.isSuccess()) {
            NavHostFragment.findNavController(this).popBackStack();
            Ui.showOnActivity(requireActivity(), R.string.profile_saved);
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
