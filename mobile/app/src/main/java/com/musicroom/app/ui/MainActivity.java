package com.musicroom.app.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.musicroom.app.MusicRoomApp;
import com.musicroom.app.R;
import com.musicroom.app.core.ServiceLocator;
import com.musicroom.app.databinding.ActivityMainBinding;
import com.musicroom.app.ui.common.LocalNetworkAccess;
import com.musicroom.app.ui.common.Ui;

import java.util.HashSet;
import java.util.Set;

/**
 * Single activity hosting every screen. Shows the login flow or the main sections depending on
 * the session, and switches between them when the user logs in or out.
 */
public class MainActivity extends AppCompatActivity {

    private static final Set<Integer> MAIN_SECTIONS = Set.of(R.id.eventsFragment,
            R.id.playlistsFragment, R.id.devicesFragment, R.id.profileFragment);

    @Nullable
    private Runnable afterLocalNetworkPermission;
    private final ActivityResultLauncher<String> localNetworkPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                Runnable next = afterLocalNetworkPermission;
                afterLocalNetworkPermission = null;
                if (next != null) {
                    next.run(); // if denied, the call fails with an explanatory message
                }
            });

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The theme is always dark: keep light system bar icons whatever the system setting.
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        applyWindowInsets();

        ServiceLocator services = MusicRoomApp.services(this);
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = host.getNavController();
        showGraph(services.session().isLoggedIn());

        Set<Integer> topLevel = new HashSet<>(MAIN_SECTIONS);
        topLevel.add(R.id.loginFragment);
        appBarConfiguration = new AppBarConfiguration.Builder(topLevel).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            boolean mainSection = MAIN_SECTIONS.contains(destination.getId());
            binding.bottomNav.setVisibility(mainSection ? View.VISIBLE : View.GONE);
            ViewCompat.requestApplyInsets(binding.getRoot());
        });

        services.session().loggedIn().observe(this, this::showGraph);
        services.sessionExpired().observe(this, event -> {
            if (event.consume() != null) {
                Ui.showOnActivity(this, R.string.session_expired);
            }
        });

        if (savedInstanceState == null) {
            withLocalNetworkAccess(services.config().getApiUrl(), () -> { });
        }
    }

    /**
     * Android 17+ blocks private addresses (the dev machine, a LAN server) until the user allows
     * local network access. Asks for it when this server needs it, then runs {@code next}.
     */
    public void withLocalNetworkAccess(String url, Runnable next) {
        if (LocalNetworkAccess.isMissing(this, url)) {
            afterLocalNetworkPermission = next;
            localNetworkPermission.launch(LocalNetworkAccess.PERMISSION);
        } else {
            next.run();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    /** Starts the graph at login or at the events list; does nothing if already there. */
    private void showGraph(boolean loggedIn) {
        int start = loggedIn ? R.id.eventsFragment : R.id.loginFragment;
        NavGraph current = navController.getCurrentDestination() == null ? null : navController.getGraph();
        if (current != null && current.getStartDestinationId() == start) {
            return;
        }
        NavGraph graph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        graph.setStartDestination(start);
        navController.setGraph(graph, null);
    }

    /**
     * The app draws edge to edge: pad the toolbar below the status bar and keep the content
     * above the navigation bar and the keyboard. The bottom navigation pads itself.
     */
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (root, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            root.setPadding(bars.left, 0, bars.right, 0);
            binding.appBar.setPadding(0, bars.top, 0, 0);
            boolean bottomNavShown = binding.bottomNav.getVisibility() == View.VISIBLE;
            int bottom = bottomNavShown
                    ? Math.max(0, ime.bottom - binding.bottomNav.getHeight())
                    : Math.max(ime.bottom, bars.bottom);
            binding.navHostFragment.setPadding(0, 0, 0, bottom);
            return insets;
        });
    }
}
