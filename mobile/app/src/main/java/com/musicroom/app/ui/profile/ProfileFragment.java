package com.musicroom.app.ui.profile;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.chip.Chip;
import com.musicroom.app.R;
import com.musicroom.app.core.Resource;
import com.musicroom.app.databinding.FragmentProfileBinding;
import com.musicroom.app.databinding.ItemProfileFieldBinding;
import com.musicroom.app.databinding.ViewActionRowBinding;
import com.musicroom.app.network.dto.ProfileDto;
import com.musicroom.app.ui.common.GoogleSignIn;
import com.musicroom.app.ui.common.Ui;
import com.musicroom.app.ui.widget.Motion;

import java.util.Locale;

/** The user's profile, split by visibility: public, friends only, private (subject V.1). */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        NavController nav = NavHostFragment.findNavController(this);

        binding.swipe.setOnRefreshListener(viewModel::load);
        binding.state.stateRetry.setOnClickListener(v -> viewModel.load());
        setAction(binding.editButton, R.drawable.ic_edit, R.string.profile_edit, false,
                v -> nav.navigate(R.id.action_profile_to_edit));
        setAction(binding.settingsButton, R.drawable.ic_settings, R.string.action_settings, false,
                v -> nav.navigate(R.id.action_global_settings));
        setAction(binding.logoutButton, R.drawable.ic_logout, R.string.action_logout, true,
                v -> viewModel.logout());

        viewModel.profile().observe(getViewLifecycleOwner(), this::render);
        viewModel.load();
    }

    private void render(Resource<ProfileDto> result) {
        ProfileDto profile = result.data;
        binding.swipe.setRefreshing(result.isLoading() && profile != null);

        if (profile != null) {
            show(profile);
            binding.state.getRoot().setVisibility(View.GONE);
            Motion.crossfade(binding.skeleton.getRoot(), binding.content);
        } else if (result.isLoading()) {
            binding.state.getRoot().setVisibility(View.GONE);
            binding.skeleton.getRoot().setVisibility(View.VISIBLE);
        } else if (result.isError() && result.error != null) {
            binding.skeleton.getRoot().setVisibility(View.GONE);
            binding.content.setVisibility(View.GONE);
            binding.state.stateIcon.setImageResource(R.drawable.ic_cloud_off);
            binding.state.stateTitle.setText(R.string.error_title);
            binding.state.stateMessage.setText(Ui.errorMessage(requireContext(), result.error));
            binding.state.getRoot().setVisibility(View.VISIBLE);
        }

        if (result.isError() && profile != null && result.error != null && result.markHandled()) {
            Ui.showError(binding.getRoot(), result.error);
        }
    }

    private void show(ProfileDto profile) {
        ProfileDto.PublicInfo publicInfo = profile.publicInfo != null ? profile.publicInfo : new ProfileDto.PublicInfo();
        ProfileDto.FriendsInfo friendsInfo = profile.friendsInfo != null ? profile.friendsInfo : new ProfileDto.FriendsInfo();
        ProfileDto.PrivateInfo privateInfo = profile.privateInfo != null ? profile.privateInfo : new ProfileDto.PrivateInfo();

        String name = isBlank(publicInfo.displayName) ? profile.email : publicInfo.displayName;
        binding.displayName.setText(name);
        binding.avatar.setText(isBlank(name) ? "?" : name.substring(0, 1).toUpperCase(Locale.getDefault()));
        binding.email.setText(profile.email);
        binding.verified.setVisibility(profile.emailVerified ? View.VISIBLE : View.GONE);

        setField(binding.fieldBio, R.string.field_bio, publicInfo.bio);
        setField(binding.fieldRealName, R.string.field_real_name, friendsInfo.realName);
        setField(binding.fieldCity, R.string.field_city, friendsInfo.city);
        setField(binding.fieldPhone, R.string.field_phone, privateInfo.phone);
        setField(binding.fieldBirthDate, R.string.profile_birth_date, privateInfo.birthDate);

        binding.genres.removeAllViews();
        boolean hasGenres = profile.musicPreferences != null && !profile.musicPreferences.isEmpty();
        if (hasGenres) {
            for (String genre : profile.musicPreferences) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_genre_chip, binding.genres, false);
                chip.setText(genre);
                chip.setCheckable(false);
                chip.setClickable(false);
                chip.setChipBackgroundColorResource(R.color.gold_container);
                chip.setChipStrokeWidth(0f);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.gold_light));
                binding.genres.addView(chip);
            }
        }
        binding.genres.setVisibility(hasGenres ? View.VISIBLE : View.GONE);
        binding.noGenres.setVisibility(hasGenres ? View.GONE : View.VISIBLE);

        boolean googleLinked = profile.linkedProviders != null
                && profile.linkedProviders.contains(ProfileViewModel.PROVIDER_GOOGLE);
        binding.googleLinkButton.setText(googleLinked ? R.string.profile_unlink : R.string.profile_link);
        binding.googleLinkButton.setOnClickListener(v -> {
            if (googleLinked) {
                viewModel.unlinkGoogle();
            } else {
                linkGoogle();
            }
        });
    }

    private void linkGoogle() {
        GoogleSignIn.requestIdToken(requireActivity(), new GoogleSignIn.Callback() {
            @Override
            public void onIdToken(String idToken) {
                viewModel.linkGoogle(idToken);
            }

            @Override
            public void onError(int message) {
                if (binding != null) {
                    Ui.showMessage(binding.getRoot(), message);
                }
            }
        });
    }

    private void setAction(ViewActionRowBinding row, @DrawableRes int icon, @StringRes int label,
                           boolean destructive, View.OnClickListener onClick) {
        row.actionIcon.setImageResource(icon);
        row.actionLabel.setText(label);
        if (destructive) {
            int red = ContextCompat.getColor(requireContext(), R.color.error);
            row.actionIcon.setImageTintList(ColorStateList.valueOf(red));
            row.actionLabel.setTextColor(red);
            row.actionChevron.setVisibility(View.GONE);
        }
        row.getRoot().setOnClickListener(onClick);
    }

    private void setField(ItemProfileFieldBinding field, @StringRes int label, @Nullable String value) {
        field.label.setText(label);
        field.value.setText(isBlank(value) ? getString(R.string.profile_not_set) : value);
        field.value.setEnabled(!isBlank(value));
    }

    private static boolean isBlank(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
