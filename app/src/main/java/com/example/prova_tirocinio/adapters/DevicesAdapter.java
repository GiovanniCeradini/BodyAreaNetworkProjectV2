package com.example.prova_tirocinio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.objects.ThingyDevice;

import java.util.List;

import it.univr.esdlab.signalsserver.R;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * Holder e Adapter Per Recycler View dello Scanner dei Thingy.
 */
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceHolder> {

    private List<ThingyDevice> mDevices;
    private ScannerFragment fragment;


    public DevicesAdapter(List<ThingyDevice> devices, ScannerFragment fragment) {
        mDevices = devices;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thingy_item, parent, false);
        DeviceHolder holder = new DeviceHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
        final ThingyDevice device = mDevices.get(position);
        holder.setDevice(device);
        holder.deviceContainer.setOnClickListener(view -> {
            fragment.onDeviceSelected(device);
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public void update(List<ScanResult> results) {
        for (final ScanResult result : results) {
            final ThingyDevice device = findDevice(result);
            if (device == null) {
                mDevices.add(new ThingyDevice(result));
            } else {
                device.setName(result.getScanRecord() != null ? result.getScanRecord().getDeviceName() : null);
                device.setRssi(result.getRssi());
            }
        }
        notifyDataSetChanged();
    }

    private ThingyDevice findDevice(final ScanResult result) {
        for (final ThingyDevice device : mDevices)
            if (device.matches(result))
                return device;
        return null;
    }


    /*VIEW HOLDER*/
    public class DeviceHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private TextView deviceAddress;
        private RelativeLayout deviceContainer;
        private TextView titleFrequency;
        private TextView deviceFrequency;
        private TextView titleBattery;
        private TextView deviceBattery;
        private ImageView deviceRssi;

        public DeviceHolder(@NonNull View itemView) {
            super(itemView);

            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);
            deviceContainer = itemView.findViewById(R.id.device_container);
            deviceRssi = itemView.findViewById(R.id.rssi);
            titleFrequency = itemView.findViewById(R.id.title_frequency);
            deviceFrequency = itemView.findViewById(R.id.device_frequency);
            titleBattery = itemView.findViewById(R.id.title_battery);
            deviceBattery = itemView.findViewById(R.id.device_battery);
            titleFrequency.setVisibility(View.GONE);
            deviceFrequency.setVisibility(View.GONE);
            titleBattery.setVisibility(View.GONE);
            deviceBattery.setVisibility(View.GONE);
        }

        public void setDevice(ThingyDevice device) {
            deviceName.setText(device.getName());
            deviceAddress.setText(device.getAddress());
            if (device.getRssi() != -1000) {
                final int rssiPercent = (int) (100.0f * (127.0f + device.getRssi()) / (127.0f + 20.0f));
                deviceRssi.setImageLevel(rssiPercent);
                deviceRssi.setVisibility(View.VISIBLE);
            } else {
                deviceRssi.setVisibility(View.GONE);
                deviceRssi.setVisibility(View.GONE);
            }
        }
    }
}
