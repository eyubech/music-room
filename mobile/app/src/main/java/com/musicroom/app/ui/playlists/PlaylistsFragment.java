package com.musicroom.app.ui.playlists;

import android.content.Context;
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
import com.musicroom.app.databinding.FragmentListBinding;
import com.musicroom.app.network.dto.PageDto;
import com.musicroom.app.network.dto.PlaylistDto;
import com.musicroom.app.ui.common.Entry;
import com.musicroom.app.ui.common.EntryAdapter;
import com.musicroom.app.ui.common.Labels;
import com.musicroom.app.ui.common.ListScreen;

import java.util.ArrayList;
import java.util.List;

/** Music Playlist Editor: playlists the user can access. */
public class PlaylistsFragment extends Fragment {

    private FragmentListBinding binding;
    private PlaylistsViewModel viewModel;
    private ListScreen screen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(PlaylistsViewModel.class);
        // Opening a playlist (real-time editor) comes with the Playlist Editor service.
        screen = new ListScreen(binding, new EntryAdapter(null), viewModel::refresh);
        binding.fab.setText(R.string.action_create_playlist);
        binding.fab.extend();
        binding.fab.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_playlists_to_create));

        viewModel.playlists().observe(getViewLifecycleOwner(), this::render);
        viewModel.refresh();
    }

    private void render(Resource<PageDto<PlaylistDto>> result) {
        List<Entry> entries = null;
        if (result.data != null && result.data.items != null) {
            Context context = requireContext();
            entries = new ArrayList<>();
            if (!result.data.items.isEmpty()) {
                int count = (int) Math.max(result.data.totalItems, result.data.items.size());
                String overline = getResources().getQuantityString(
                        R.plurals.playlists_intro_count, count, count);
                entries.add(Entry.intro(overline, getString(R.string.playlists_intro)));
            }
            for (PlaylistDto playlist : result.data.items) {
                entries.add(Entry.cover(playlist.id, playlist.name,
                        Labels.ownerLine(context, playlist.ownerName, playlist.trackCount,
                                Labels.editLicense(context, playlist.editLicense)),
                        Labels.visibility(context, playlist.visibility), R.drawable.ic_queue_music));
            }
        }
        screen.render(result, entries, R.drawable.ic_queue_music, R.string.playlists_empty_title,
                R.string.playlists_empty_message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
