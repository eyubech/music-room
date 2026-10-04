package com.musicroom.app.ui.common;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.card.MaterialCardView;
import com.musicroom.app.R;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Single choice between option cards (view_option.xml or view_option_compact.xml), like a radio
 * group but with an icon and a description for each choice.
 */
public final class OptionGroup<T> {

    private final Map<T, MaterialCardView> options = new LinkedHashMap<>();
    @Nullable
    private Consumer<T> listener;
    @Nullable
    private T selected;

    /** @param option the root of an included view_option layout */
    public OptionGroup<T> add(T value, View option, @DrawableRes int icon, @StringRes int title,
                              @StringRes int description) {
        ((ImageView) option.findViewById(R.id.option_icon)).setImageResource(icon);
        ((TextView) option.findViewById(R.id.option_title)).setText(title);
        ((TextView) option.findViewById(R.id.option_description)).setText(description);
        MaterialCardView card = (MaterialCardView) option;
        card.setOnClickListener(v -> select(value));
        options.put(value, card);
        return this;
    }

    public void setOnChange(@Nullable Consumer<T> listener) {
        this.listener = listener;
    }

    public void select(T value) {
        boolean changed = !value.equals(selected);
        selected = value;
        for (Map.Entry<T, MaterialCardView> option : options.entrySet()) {
            option.getValue().setChecked(option.getKey().equals(value));
        }
        if (changed && listener != null) {
            listener.accept(value);
        }
    }

    @Nullable
    public T selected() {
        return selected;
    }
}
