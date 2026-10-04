package com.musicroom.app.ui.common;

import android.content.Context;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.musicroom.app.R;
import com.musicroom.app.core.Resource;
import com.musicroom.app.databinding.FragmentListBinding;
import com.musicroom.app.databinding.ViewStateBinding;
import com.musicroom.app.ui.widget.Motion;

import java.util.List;

/**
 * Behaviour shared by the fragment_list.xml screens: skeleton on first load, pull to refresh,
 * rows that animate in, empty and error states, and a create button that folds while scrolling.
 */
public final class ListScreen {

    private static final int FAB_SCROLL_THRESHOLD = 8;

    private final FragmentListBinding binding;
    private final EntryAdapter adapter;
    private boolean contentShown;

    public ListScreen(FragmentListBinding binding, EntryAdapter adapter, Runnable onRefresh) {
        this.binding = binding;
        this.adapter = adapter;
        binding.list.setAdapter(adapter);
        binding.swipe.setOnRefreshListener(onRefresh::run);
        binding.swipe.setColorSchemeResources(R.color.green);
        binding.swipe.setProgressBackgroundColorSchemeResource(R.color.surface_high);
        // The list is not the direct child of the SwipeRefreshLayout: tell it when it can scroll.
        binding.swipe.setOnChildScrollUpCallback((parent, child) -> binding.list.canScrollVertically(-1));
        binding.state.stateRetry.setOnClickListener(v -> onRefresh.run());
        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > FAB_SCROLL_THRESHOLD && binding.fab.isExtended()) {
                    binding.fab.shrink();
                } else if (dy < -FAB_SCROLL_THRESHOLD && !binding.fab.isExtended()) {
                    binding.fab.extend();
                }
            }
        });
    }

    /**
     * @param entries rows built from {@code resource.data}, or null when there is no data yet
     */
    public void render(Resource<?> resource, @Nullable List<Entry> entries, @DrawableRes int emptyIcon,
                       @StringRes int emptyTitle, @StringRes int emptyMessage) {
        Context context = binding.getRoot().getContext();
        boolean hasItems = entries != null && !entries.isEmpty();
        boolean firstLoad = resource.isLoading() && entries == null;

        binding.skeleton.getRoot().setVisibility(firstLoad ? View.VISIBLE : View.GONE);
        binding.swipe.setRefreshing(resource.isLoading() && !firstLoad);
        adapter.submitList(entries);

        if (hasItems && !contentShown) {
            contentShown = true;
            if (Motion.enabled()) {
                binding.list.scheduleLayoutAnimation();
            }
        }

        if (resource.isError() && !hasItems && resource.error != null) {
            showState(R.drawable.ic_cloud_off, context.getString(R.string.error_title),
                    Ui.errorMessage(context, resource.error), true);
        } else if (resource.isSuccess() && !hasItems) {
            showState(emptyIcon, context.getString(emptyTitle), context.getString(emptyMessage), false);
        } else {
            binding.state.getRoot().setVisibility(View.GONE);
        }

        if (resource.isError() && hasItems && resource.error != null && resource.markHandled()) {
            Ui.showError(binding.getRoot(), resource.error);
        }
    }

    private void showState(@DrawableRes int icon, String title, String message, boolean retry) {
        ViewStateBinding state = binding.state;
        state.stateIcon.setImageResource(icon);
        state.stateTitle.setText(title);
        state.stateMessage.setText(message);
        state.stateRetry.setVisibility(retry ? View.VISIBLE : View.GONE);
        if (state.getRoot().getVisibility() != View.VISIBLE) {
            state.getRoot().setAlpha(0f);
            state.getRoot().setVisibility(View.VISIBLE);
            state.getRoot().animate().alpha(1f).setDuration(260).start();
        }
    }
}
