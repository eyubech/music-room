package com.musicroom.app.ui.events;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.musicroom.app.R;
import com.musicroom.app.core.Resource;
import com.musicroom.app.databinding.FragmentCreateEventBinding;
import com.musicroom.app.network.dto.CreateEventRequest;
import com.musicroom.app.network.dto.EventDto;
import com.musicroom.app.network.dto.Visibility;
import com.musicroom.app.network.dto.VoteLicense;
import com.musicroom.app.ui.common.OptionGroup;
import com.musicroom.app.ui.common.Ui;
import com.musicroom.app.ui.widget.Motion;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;

/** Creates a Music Track Vote event with its visibility and vote license (subject V.2.1). */
public class CreateEventFragment extends Fragment {

    private static final String TAG_DATE = "date";
    private static final String TAG_START = "start";
    private static final String TAG_END = "end";
    private static final int MIN_RADIUS = 10;
    private static final int MAX_RADIUS = 50_000;

    private final ActivityResultLauncher<String[]> locationPermission = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), this::onLocationPermission);

    private FragmentCreateEventBinding binding;
    private CreateEventViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);

        OptionGroup<Visibility> visibility = new OptionGroup<Visibility>()
                .add(Visibility.PUBLIC, binding.optionPublic.getRoot(), R.drawable.ic_public,
                        R.string.visibility_public, R.string.visibility_public_hint)
                .add(Visibility.PRIVATE, binding.optionPrivate.getRoot(), R.drawable.ic_lock,
                        R.string.visibility_private, R.string.visibility_private_hint);
        visibility.setOnChange(value -> viewModel.visibility = value);
        visibility.select(viewModel.visibility);

        OptionGroup<VoteLicense> license = new OptionGroup<VoteLicense>()
                .add(VoteLicense.EVERYONE, binding.optionEveryone.getRoot(), R.drawable.ic_users,
                        R.string.license_everyone, R.string.license_everyone_desc)
                .add(VoteLicense.INVITED_ONLY, binding.optionInvited.getRoot(), R.drawable.ic_mail,
                        R.string.license_invited, R.string.license_invited_desc)
                .add(VoteLicense.LOCATION_AND_TIME, binding.optionLocationTime.getRoot(),
                        R.drawable.ic_location, R.string.license_location_time_title,
                        R.string.license_location_time_desc);
        license.setOnChange(value -> {
            viewModel.license = value;
            updateVenueVisibility(true);
        });
        license.select(viewModel.license);
        updateVenueVisibility(false);

        binding.locationButton.setOnClickListener(v -> requestLocation());
        binding.dateButton.setOnClickListener(v -> showDatePicker());
        binding.startButton.setOnClickListener(v -> showTimePicker(TAG_START, viewModel.start));
        binding.endButton.setOnClickListener(v -> showTimePicker(TAG_END, viewModel.end));
        reattachPickers();
        renderVenue();

        binding.createButton.setOnClickListener(v -> submit());
        viewModel.result().observe(getViewLifecycleOwner(), this::render);
    }

    private void submit() {
        Ui.clearErrors(binding.nameLayout, binding.radiusLayout);
        String name = Ui.text(binding.nameLayout);
        if (name.isEmpty()) {
            binding.nameLayout.setError(getString(R.string.error_required));
            return;
        }
        CreateEventRequest request = new CreateEventRequest(name,
                Ui.textOrNull(binding.descriptionLayout), viewModel.visibility, viewModel.license);

        if (viewModel.license == VoteLicense.LOCATION_AND_TIME && !fillVenue(request)) {
            return;
        }
        Ui.hideKeyboard(binding.getRoot());
        viewModel.create(request);
    }

    /** Adds the venue and time window to the request, or shows what is missing. */
    private boolean fillVenue(CreateEventRequest request) {
        if (viewModel.latitude == null || viewModel.longitude == null) {
            Ui.showMessage(binding.getRoot(), R.string.error_location_missing);
            return false;
        }
        int radius;
        try {
            radius = Integer.parseInt(Ui.text(binding.radiusLayout));
        } catch (NumberFormatException e) {
            radius = -1;
        }
        if (radius < MIN_RADIUS || radius > MAX_RADIUS) {
            binding.radiusLayout.setError(getString(R.string.error_radius_invalid));
            return false;
        }
        if (!viewModel.end.isAfter(viewModel.start)) {
            Ui.showMessage(binding.getRoot(), R.string.error_time_window);
            return false;
        }
        ZoneId zone = ZoneId.systemDefault();
        request.latitude = viewModel.latitude;
        request.longitude = viewModel.longitude;
        request.radiusMeters = radius;
        request.voteStart = ZonedDateTime.of(viewModel.date, viewModel.start, zone).toOffsetDateTime().toString();
        request.voteEnd = ZonedDateTime.of(viewModel.date, viewModel.end, zone).toOffsetDateTime().toString();
        return true;
    }

    private void render(Resource<EventDto> result) {
        Ui.setLoading(result.isLoading(), binding.progress, binding.createButton);
        if (result.isLoading() || !result.markHandled()) {
            return;
        }
        if (result.isSuccess()) {
            NavHostFragment.findNavController(this).popBackStack();
            Ui.showOnActivity(requireActivity(), R.string.event_created);
        } else if (result.error != null) {
            Ui.showError(binding.getRoot(), result.error);
        }
    }

    // ----------------------------------------------------------------- venue

    /** The venue card only exists for the location + time license; it fades in when chosen. */
    private void updateVenueVisibility(boolean animate) {
        boolean onSite = viewModel.license == VoteLicense.LOCATION_AND_TIME;
        boolean shown = binding.venueGroup.getVisibility() == View.VISIBLE;
        if (onSite == shown) {
            return;
        }
        binding.venueGroup.setVisibility(onSite ? View.VISIBLE : View.GONE);
        if (onSite && animate && Motion.enabled()) {
            binding.venueGroup.setAlpha(0f);
            binding.venueGroup.setTranslationY(binding.venueGroup.getResources().getDisplayMetrics().density * 16);
            binding.venueGroup.animate().alpha(1f).translationY(0f).setDuration(280).start();
        }
    }

    private void renderVenue() {
        if (viewModel.latitude != null && viewModel.longitude != null) {
            binding.locationValue.setText(getString(R.string.location_value,
                    viewModel.latitude, viewModel.longitude));
        } else {
            binding.locationValue.setText(R.string.location_not_set);
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        binding.dateButton.setText(getString(R.string.pick_date, viewModel.date.format(dateFormat)));
        binding.startButton.setText(getString(R.string.pick_start, viewModel.start.format(timeFormat)));
        binding.endButton.setText(getString(R.string.pick_end, viewModel.end.format(timeFormat)));
    }

    private void requestLocation() {
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fetchLocation();
        } else {
            locationPermission.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    private void onLocationPermission(Map<String, Boolean> granted) {
        if (Boolean.TRUE.equals(granted.get(Manifest.permission.ACCESS_FINE_LOCATION))
                || Boolean.TRUE.equals(granted.get(Manifest.permission.ACCESS_COARSE_LOCATION))) {
            fetchLocation();
        } else if (binding != null) {
            Ui.showMessage(binding.getRoot(), R.string.location_permission_denied);
        }
    }

    @SuppressLint("MissingPermission") // checked by requestLocation()
    private void fetchLocation() {
        LocationManager manager = requireContext().getSystemService(LocationManager.class);
        String provider = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
        if (!manager.isProviderEnabled(provider)) {
            Ui.showMessage(binding.getRoot(), R.string.location_unavailable);
            return;
        }
        binding.locationButton.setEnabled(false);
        LocationManagerCompat.getCurrentLocation(manager, provider, (android.os.CancellationSignal) null,
                ContextCompat.getMainExecutor(requireContext()), location -> {
                    if (binding == null) {
                        return;
                    }
                    binding.locationButton.setEnabled(true);
                    Location found = location != null ? location : manager.getLastKnownLocation(provider);
                    if (found == null) {
                        Ui.showMessage(binding.getRoot(), R.string.location_unavailable);
                        return;
                    }
                    viewModel.latitude = found.getLatitude();
                    viewModel.longitude = found.getLongitude();
                    renderVenue();
                });
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    // ----------------------------------------------------------------- pickers

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setSelection(viewModel.date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build())
                .build();
        attachDatePicker(picker);
        picker.show(getChildFragmentManager(), TAG_DATE);
    }

    private void showTimePicker(String tag, LocalTime initial) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(DateFormat.is24HourFormat(requireContext())
                        ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                .setHour(initial.getHour())
                .setMinute(initial.getMinute())
                .build();
        attachTimePicker(picker, tag);
        picker.show(getChildFragmentManager(), tag);
    }

    private void attachDatePicker(MaterialDatePicker<Long> picker) {
        picker.addOnPositiveButtonClickListener(selection -> {
            viewModel.date = Instant.ofEpochMilli(selection).atZone(ZoneOffset.UTC).toLocalDate();
            renderVenue();
        });
    }

    private void attachTimePicker(MaterialTimePicker picker, String tag) {
        picker.addOnPositiveButtonClickListener(v -> {
            LocalTime time = LocalTime.of(picker.getHour(), picker.getMinute());
            if (TAG_START.equals(tag)) {
                viewModel.start = time;
            } else {
                viewModel.end = time;
            }
            renderVenue();
        });
    }

    /** Pickers are restored after a rotation without their listeners: add them back. */
    @SuppressWarnings("unchecked")
    private void reattachPickers() {
        Fragment date = getChildFragmentManager().findFragmentByTag(TAG_DATE);
        if (date instanceof MaterialDatePicker) {
            attachDatePicker((MaterialDatePicker<Long>) date);
        }
        for (String tag : new String[]{TAG_START, TAG_END}) {
            Fragment time = getChildFragmentManager().findFragmentByTag(tag);
            if (time instanceof MaterialTimePicker) {
                attachTimePicker((MaterialTimePicker) time, tag);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
