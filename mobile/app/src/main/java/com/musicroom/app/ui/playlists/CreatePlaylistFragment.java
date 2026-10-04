package com.musicroom.app.ui.playlists;

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
import com.musicroom.app.databinding.FragmentCreatePlaylistBinding;
import com.musicroom.app.network.dto.CreatePlaylistRequest;
import com.musicroom.app.network.dto.EditLicense;
import com.musicroom.app.network.dto.PlaylistDto;
import com.musicroom.app.network.dto.Visibility;
import com.musicroom.app.ui.common.OptionGroup;
import com.musicroom.app.ui.common.Ui;

/** Creates a playlist with its visibility and edit license (subject V.2.3). */
public class CreatePlaylistFragment extends Fragment {

    private FragmentCreatePlaylistBinding binding;
    private PlaylistsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(PlaylistsViewModel.class);

        OptionGroup<Visibility> visibility = new OptionGroup<Visibility>()
                .add(Visibility.PUBLIC, binding.optionPublic.getRoot(), R.drawable.ic_public,
                        R.string.visibility_public, R.string.visibility_public_hint)
                .add(Visibility.PRIVATE, binding.optionPrivate.getRoot(), R.drawable.ic_lock,
                        R.string.visibility_private, R.string.visibility_private_hint);
        visibility.setOnChange(value -> viewModel.visibility = value);
        visibility.select(viewModel.visibility);

        OptionGroup<EditLicense> license = new OptionGroup<EditLicense>()
                .add(EditLicense.EVERYONE, binding.optionEveryone.getRoot(), R.drawable.ic_users,
                        R.string.license_everyone, R.string.edit_everyone_desc)
                .add(EditLicense.INVITED_ONLY, binding.optionInvited.getRoot(), R.drawable.ic_mail,
                        R.string.license_invited, R.string.edit_invited_desc);
        license.setOnChange(value -> viewModel.editLicense = value);
        license.select(viewModel.editLicense);

        binding.createButton.setOnClickListener(v -> submit());
        viewModel.created().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        binding.nameLayout.setError(null);
        String name = Ui.text(binding.nameLayout);
        if (name.isEmpty()) {
            binding.nameLayout.setError(getString(R.string.error_required));
            return;
        }
        Ui.hideKeyboard(binding.getRoot());
        viewModel.create(new CreatePlaylistRequest(name, Ui.textOrNull(binding.descriptionLayout),
                viewModel.visibility, viewModel.editLicense));
    }

    private void render(Resource<PlaylistDto> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.createButton);
        if (result.isLoading() || !result.markHandled()) {
            return;
        }
        if (result.isSuccess()) {
            NavHostFragment.findNavController(this).popBackStack();
            Ui.showOnActivity(requireActivity(), R.string.playlist_created);
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
