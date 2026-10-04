package com.musicroom.app.ui.common;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.navigation.NavController;

import com.musicroom.app.R;

/** Toolbar gear icon opening Settings. */
public final class SettingsMenu implements MenuProvider {

    private final NavController navController;

    public SettingsMenu(NavController navController) {
        this.navController = navController;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            navController.navigate(R.id.action_global_settings);
            return true;
        }
        return false;
    }
}
