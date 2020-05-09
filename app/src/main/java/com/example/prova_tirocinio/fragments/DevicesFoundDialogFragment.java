package com.example.prova_tirocinio.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.R;
import com.example.prova_tirocinio.adapters.AdapterDialogFragment;
import com.example.prova_tirocinio.databinding.DialogFragmentDevicesFoundBinding;
import com.example.prova_tirocinio.objects.Device;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import no.nordicsemi.android.thingylib.utils.ThingyUtils;


public class DevicesFoundDialogFragment extends AppCompatDialogFragment implements AdapterDialogFragment.onDeviceListener{

    private static final String TAG = "DevicesFoundDialogFragm";
    private final static long SCAN_DURATION = 8000;
    private final static int REQUEST_PERMISSION_REQ_CODE = 76;
    private final static ParcelUuid parcelThingyUuid=new ParcelUuid(ThingyUtils.THINGY_BASE_UUID);

    private Handler mHandler = new Handler();
    private List<Device> mDevices = new ArrayList<>();

    private DialogFragmentDevicesFoundBinding mBinding;
    private RecyclerView mRecyclerView;
    private AdapterDialogFragment mAdapter;
    private ProgressBar mProgressBar;

    private ParcelUuid mUuid;
    private boolean mIsScanning = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_devices_found, container, false);
        View v = mBinding.getRoot();

        mRecyclerView=mBinding.recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mAdapter=new AdapterDialogFragment(Device.get10DevicesForTesting(),this);
        mAdapter=new AdapterDialogFragment(mDevices,this);
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = mBinding.progressBar;
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.INVISIBLE);

        startScan();

        return v;

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        final View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_fragment_devices_found, null);

        final AlertDialog dialog = builder
                .setTitle("Select device:")
                .setView(dialogView)
                .setPositiveButton("Scan", null)
                .show();

        return dialog;

    }

        private void startScan() {
            // Since Android 6.0 we need to obtain either Manifest.permission.ACCESS_COARSE_LOCATION or Manifest.permission.ACCESS_FINE_LOCATION to be able to scan for
            // Bluetooth LE devices. This is related to beacons as proximity devices.
            // On API older than Marshmallow the following code does nothing.
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // When user pressed Deny and still wants to use this functionality, show the rationale
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    return;
                }

                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
                return;
            }

            mProgressBar.setVisibility(View.VISIBLE);

            //mAdapter.clearDevices();
            mDevices.clear();
            //Notify any registered observers that the data set has changed.
            mAdapter.notifyDataSetChanged();
    //        mScanButton.setText(R.string.scanner_action_cancel);
            // troubleshootView.setVisibility(View.VISIBLE);

            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            final ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(750).setUseHardwareBatchingIfSupported(false).setUseHardwareFilteringIfSupported(false).build();
            final List<ScanFilter> filters = new ArrayList<>();
            filters.add(new ScanFilter.Builder().setServiceUuid(parcelThingyUuid).build());
            //TODO aggiungere uuid dei wagoo
//            filters.add(new ScanFilter.Builder().setServiceUuid(wagooUuid).build());
            scanner.startScan(filters, settings, scanCallback);

            mIsScanning = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mIsScanning) {
                        stopScan();
                    }
                }
            }, SCAN_DURATION);
        }


        private void stopScan() {
            if (mIsScanning) {
    //            mScanButton.setText(R.string.scanner_action_scan);
    //            getActivity().findViewById(R.id.scan_device_fab).setEnabled(true);
                mProgressBar.setVisibility(View.INVISIBLE);

                final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
                scanner.stopScan(scanCallback);

                mIsScanning = false;
            }
        }

        private ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
                // do nothing
            }

            @Override
            public void onBatchScanResults(final List<ScanResult> results) {
                mAdapter.update(results);
                //mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFailed(final int errorCode) {
                // should never be called
            }
        };

        @Override
        public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQUEST_PERMISSION_REQ_CODE: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // We have been granted the Manifest.permission.ACCESS_COARSE_LOCATION permission. Now we may proceed with scanning.
                        //startScan();
                    } else {
                        Toast.makeText(getActivity(), R.string.rationale_permission_denied, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }

    @Override
    public void onDeviceClick(int position) {
        Log.d(TAG, "onDeviceClick: 1");
//        MainFragment.addDeviceToRecyclerView(mDevices.get(position));
        dismiss();
    }
}


