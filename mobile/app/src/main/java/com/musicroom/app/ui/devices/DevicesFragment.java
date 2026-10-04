package com.musicroom.app.ui.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.musicroom.app.R;
import com.musicroom.app.core.Resource;
import com.musicroom.app.databinding.FragmentListBinding;
import com.musicroom.app.network.dto.DelegationDto;
import com.musicroom.app.network.dto.DeviceDto;
import com.musicroom.app.ui.common.Entry;
import com.musicroom.app.ui.common.EntryAdapter;
import com.musicroom.app.ui.common.ListScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Music Control Delegation: the user's devices (each has its own license) and the devices
 * friends let them control.
 */
public class DevicesFragment extends Fragment {

    private FragmentListBinding binding;
    private DevicesViewModel viewModel;
    private ListScreen screen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DevicesViewModel.class);
        // Sharing control of a device comes with the Control Delegation service.
        screen = new ListScreen(binding, new EntryAdapter(null), viewModel::refresh);
        binding.fab.setVisibility(View.GONE);

        viewModel.devices().observe(getViewLifecycleOwner(), r -> render());
        viewModel.delegations().observe(getViewLifecycleOwner(), r -> render());
        viewModel.refresh();
    }

    private void render() {
        Resource<List<DeviceDto>> devices = viewModel.devices().getValue();
        Resource<List<DelegationDto>> delegations = viewModel.delegations().getValue();
        if (devices == null) {
            return;
        }
        List<Entry> entries = null;
        if (devices.data != null) {
            entries = new ArrayList<>();
            entries.add(Entry.intro(getString(R.string.devices_intro_overline),
                    getString(R.string.devices_intro)));
            entries.add(Entry.header(getString(R.string.devices_header_mine)));
            for (DeviceDto device : devices.data) {
                entries.add(Entry.item(device.id, device.name,
                        getString(R.string.device_subtitle, device.platform, device.appVersion),
                        device.current ? getString(R.string.device_current) : null,
                        R.drawable.ic_smartphone));
            }
            entries.add(Entry.header(getString(R.string.devices_header_delegated)));
            if (delegations != null && delegations.data != null && !delegations.data.isEmpty()) {
                for (DelegationDto delegation : delegations.data) {
                    entries.add(Entry.item(delegation.id, delegation.deviceName,
                            getString(R.string.delegation_subtitle, delegation.ownerName), null,
                            R.drawable.ic_swap));
                }
            } else {
                entries.add(Entry.note(getString(R.string.delegations_none)));
            }
        }
        screen.render(devices, entries, R.drawable.ic_devices, R.string.devices_empty_title,
                R.string.devices_empty_message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
