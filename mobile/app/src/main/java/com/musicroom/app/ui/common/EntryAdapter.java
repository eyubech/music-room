package com.musicroom.app.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.musicroom.app.R;
import com.musicroom.app.databinding.ItemEntryBinding;
import com.musicroom.app.databinding.ItemHeaderBinding;
import com.musicroom.app.databinding.ItemIntroBinding;
import com.musicroom.app.databinding.ItemNoteBinding;

/** Renders {@link Entry} rows for every list screen. */
public final class EntryAdapter extends ListAdapter<Entry, EntryAdapter.Holder> {

    public interface OnEntryClick {
        void onClick(Entry entry);
    }

    private static final DiffUtil.ItemCallback<Entry> DIFF = new DiffUtil.ItemCallback<Entry>() {
        @Override
        public boolean areItemsTheSame(@NonNull Entry a, @NonNull Entry b) {
            return a.id.equals(b.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Entry a, @NonNull Entry b) {
            return a.equals(b);
        }
    };

    @Nullable
    private final OnEntryClick onClick;

    /** @param onClick null when the items are not clickable yet */
    public EntryAdapter(@Nullable OnEntryClick onClick) {
        super(DIFF);
        this.onClick = onClick;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type.ordinal();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (Entry.Type.values()[viewType]) {
            case INTRO:
                return new Holder(ItemIntroBinding.inflate(inflater, parent, false));
            case HEADER:
                return new Holder(ItemHeaderBinding.inflate(inflater, parent, false));
            case NOTE:
                return new Holder(ItemNoteBinding.inflate(inflater, parent, false));
            default:
                return new Holder(ItemEntryBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Entry entry = getItem(position);
        if (holder.binding instanceof ItemIntroBinding) {
            ItemIntroBinding b = (ItemIntroBinding) holder.binding;
            b.overline.setText(entry.title);
            b.text.setText(entry.subtitle);
        } else if (holder.binding instanceof ItemHeaderBinding) {
            ((ItemHeaderBinding) holder.binding).text.setText(entry.title);
        } else if (holder.binding instanceof ItemNoteBinding) {
            ((ItemNoteBinding) holder.binding).text.setText(entry.title);
        } else {
            ItemEntryBinding b = (ItemEntryBinding) holder.binding;
            b.title.setText(entry.title);
            b.subtitle.setText(entry.subtitle);
            b.subtitle.setVisibility(entry.subtitle == null ? View.GONE : View.VISIBLE);
            b.badge.setText(entry.badge);
            b.badge.setVisibility(entry.badge == null ? View.GONE : View.VISIBLE);
            b.icon.setImageResource(entry.icon);
            Context context = b.icon.getContext();
            if (entry.cover) {
                b.icon.setBackground(Covers.forId(entry.id,
                        context.getResources().getDimension(R.dimen.cover_corner_radius)));
                b.icon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            } else {
                b.icon.setBackgroundResource(R.drawable.bg_tile);
                b.icon.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.gold)));
            }
            if (onClick != null) {
                b.getRoot().setOnClickListener(v -> onClick.onClick(entry));
            }
            b.getRoot().setClickable(onClick != null);
        }
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ViewBinding binding;

        Holder(ViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
