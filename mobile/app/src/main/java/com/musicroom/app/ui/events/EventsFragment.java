package com.musicroom.app.ui.events;

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
import com.musicroom.app.network.dto.EventDto;
import com.musicroom.app.network.dto.PageDto;
import com.musicroom.app.ui.common.Entry;
import com.musicroom.app.ui.common.EntryAdapter;
import com.musicroom.app.ui.common.Labels;
import com.musicroom.app.ui.common.ListScreen;

import java.util.ArrayList;
import java.util.List;

/** Music Track Vote: events the user can find (public ones and private invitations). */
public class EventsFragment extends Fragment {

    private FragmentListBinding binding;
    private EventsViewModel viewModel;
    private ListScreen screen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        // Opening an event (queue and votes) comes with the Track Vote service.
        screen = new ListScreen(binding, new EntryAdapter(null), viewModel::refresh);
        binding.fab.setText(R.string.action_create_event);
        binding.fab.extend();
        binding.fab.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_events_to_create));

        viewModel.events().observe(getViewLifecycleOwner(), this::render);
        viewModel.refresh();
    }

    private void render(Resource<PageDto<EventDto>> result) {
        List<Entry> entries = null;
        if (result.data != null && result.data.items != null) {
            Context context = requireContext();
            entries = new ArrayList<>();
            if (!result.data.items.isEmpty()) {
                int count = (int) Math.max(result.data.totalItems, result.data.items.size());
                String overline = getResources().getQuantityString(
                        R.plurals.events_intro_count, count, count);
                entries.add(Entry.intro(overline, getString(R.string.events_intro)));
            }
            for (EventDto event : result.data.items) {
                entries.add(Entry.cover(event.id, event.name,
                        Labels.ownerLine(context, event.ownerName, event.trackCount,
                                Labels.voteLicense(context, event.voteLicense)),
                        Labels.visibility(context, event.visibility), R.drawable.ic_live));
            }
        }
        screen.render(result, entries, R.drawable.ic_live, R.string.events_empty_title,
                R.string.events_empty_message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
