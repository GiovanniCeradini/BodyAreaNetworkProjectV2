//package com.example.prova_tirocinio.fragments;
//
//
//import android.Manifest;
//import android.app.Dialog;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.ParcelUuid;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.DialogFragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import it.univr.esdlab.signalsserver.R;
//import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
//import no.nordicsemi.android.support.v18.scanner.ScanCallback;
//import no.nordicsemi.android.support.v18.scanner.ScanFilter;
//import no.nordicsemi.android.support.v18.scanner.ScanResult;
//import no.nordicsemi.android.support.v18.scanner.ScanSettings;
//
//
///* E' un fragment che mostra in una lista di ble appartenenti a un range di UUID, quindi mostra tutti i NordiThingy disponibili a cui potersi connettere */
//
///**
// * ScannerFragment class scan required BLE devices and shows them in a list. This class scans and filter devices with given BLE Service UUID which may be null. It contains a
// * list and a button to scan/cancel. The scanning will continue for 5 seconds and then stop.
// */
//public class ScannerFragment extends DialogFragment {
//    private final static String TAG = "ScannerFragment";
//
//    private final static String PARAM_UUID = "param_uuid";
//    private final static String PARAM_UUID1 = "param_uuid1";
//    private final static long SCAN_DURATION = 8000;
//    /* package */static final int NO_RSSI = -1000;
//
//    private final static int REQUEST_PERMISSION_REQ_CODE = 76; // any 8-bit number
//
//    //private LinearLayout troubleshootView;
//   // private DeviceListAdapter mAdapter;
//    private Handler mHandler = new Handler();
//    private Button mScanButton;
//
//    private ParcelUuid mUuid;
//    private boolean mIsScanning = false;
//
//    private List<ThingyDevice> mDevices = new ArrayList<>();
//    private RecyclerView mDevicesRecyclerView;
//    private DevicesAdapter mDeviceAdapter;
//    private ProgressBar mProgressBar;
//
//    /**
//     * Static implementation of fragment so that it keeps data when phone orientation is changed For standard BLE Service UUID, we can filter devices using normal android provided command
//     * startScanLe() with required BLE Service UUID For custom BLE Service UUID, we will use class ScannerServiceParser to filter out required device
//     */
//    public static ScannerFragment getInstance(@Nullable final UUID uuid) {
//        final ScannerFragment fragment = new ScannerFragment();
//
//        final Bundle args = new Bundle();
//        if (uuid != null) {
//            //TODO, tutti gli uuid necessari->occhiali
//            args.putParcelable(PARAM_UUID, new ParcelUuid(uuid));
//        }
//
//        fragment.setArguments(args);
//
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        final Bundle args = getArguments();
//        mUuid = args.getParcelable(PARAM_UUID);
//    }
//
//    @Override
//    public void onStop() {
//        // Stop scan moved from onDestroyView to onStop
//        stopScan();
//        super.onStop();
//    }
//
//    /**
//     * When dialog is created then set AlertDialog with list and button views
//     */
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(final Bundle savedInstanceState) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        final View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_scanner_device_selection, null);
//
//
//        //troubleshootView = dialogView.findViewById(R.id.troubleshoot_guide);
//
//        /*RECYCLER VIEW DEVICE TROVATI*/
//        mDevicesRecyclerView = dialogView.findViewById(R.id.recycler_dev_discovered);
//        mDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        mDeviceAdapter = new DevicesAdapter(mDevices, this);
//        mDevicesRecyclerView.setAdapter(mDeviceAdapter);
//
//        /*PROGRESS BAR*/
//        mProgressBar = dialogView.findViewById(R.id.progress_bar);
//        mProgressBar.setIndeterminate(true);
//        mProgressBar.setVisibility(View.INVISIBLE);
//
//        final AlertDialog dialog = builder
//                .setTitle("Select device:")
//                .setView(dialogView)
//                .setPositiveButton("Scan", null)
//                .show();
//
//        // Button listener needs to be set like this, otherwise it would always dismiss the dialog.
//        mScanButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        mScanButton.setOnClickListener((v) -> {
//
//            if (mIsScanning) {
//                final ScannerFragmentListener listener = (ScannerFragmentListener) getTargetFragment();
//                listener.onNothingSelected();
//                dialog.cancel();
//            } else {
//                startScan();
//            }
//        });
//
//
//
//        if (savedInstanceState == null)
//            startScan();
//        return dialog;
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSION_REQ_CODE: {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // We have been granted the Manifest.permission.ACCESS_COARSE_LOCATION permission. Now we may proceed with scanning.
//                    startScan();
//                } else {
//                    Toast.makeText(getActivity(), R.string.rationale_permission_denied, Toast.LENGTH_SHORT).show();
//                }
//                break;
//            }
//        }
//    }
//
//    /**
//     * Scan for 5 seconds and then stop scanning when a BluetoothLE device is found then mLEScanCallback is activated This will perform regular scan for custom BLE Service UUID and then filter out
//     * using class ScannerServiceParser
//     */
//    private void startScan() {
//        // Since Android 6.0 we need to obtain either Manifest.permission.ACCESS_COARSE_LOCATION or Manifest.permission.ACCESS_FINE_LOCATION to be able to scan for
//        // Bluetooth LE devices. This is related to beacons as proximity devices.
//        // On API older than Marshmallow the following code does nothing.
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // When user pressed Deny and still wants to use this functionality, show the rationale
//            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                return;
//            }
//
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
//            return;
//        }
//
//        mProgressBar.setVisibility(View.VISIBLE);
//
//        //mAdapter.clearDevices();
//        mDevices.clear();
//        mDeviceAdapter.notifyDataSetChanged();
//        mScanButton.setText(R.string.scanner_action_cancel);
//       // troubleshootView.setVisibility(View.VISIBLE);
//
//        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
//        final ScanSettings settings = new ScanSettings.Builder()
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(750).setUseHardwareBatchingIfSupported(false).setUseHardwareFilteringIfSupported(false).build();
//        final List<ScanFilter> filters = new ArrayList<>();
//        filters.add(new ScanFilter.Builder().setServiceUuid(mUuid).build());
//        scanner.startScan(filters, settings, scanCallback);
//
//        mIsScanning = true;
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mIsScanning) {
//                    stopScan();
//                }
//            }
//        }, SCAN_DURATION);
//    }
//
//    /**
//     * Stop scan if user tap Cancel button
//     */
//    private void stopScan() {
//        if (mIsScanning) {
//            mScanButton.setText(R.string.scanner_action_scan);
//            getActivity().findViewById(R.id.scan_device_fab).setEnabled(true);
//            mProgressBar.setVisibility(View.INVISIBLE);
//
//            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
//            scanner.stopScan(scanCallback);
//
//            mIsScanning = false;
//        }
//    }
//
//    private ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
//            // do nothing
//        }
//
//        @Override
//        public void onBatchScanResults(final List<ScanResult> results) {
//            mDeviceAdapter.update(results);
//            //mDeviceAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onScanFailed(final int errorCode) {
//            // should never be called
//        }
//    };
//
//    private ThingyDevice findDevice(ScanResult result){
//        for (ThingyDevice device : mDevices)
//            if (device.matches(result))
//                return  device;
//
//        return null;
//    }
//
//    public void onDeviceSelected(ThingyDevice device){
//
//            stopScan();
//            dismiss();
//            ScannerFragmentListener listener = (ScannerFragmentListener) getTargetFragment();
//            listener.onDeviceSelected(device);
//
//    }
//}
