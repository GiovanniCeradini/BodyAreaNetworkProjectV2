package com.example.prova_tirocinio.Ble_stuff;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.thingylib.utils.ThingyUtils;

/**
 * Implementazione dello scannner per i dispositivi BLE, implementazione statica dello scanner
 * In caso di riuso modificare l'activity al quale fa riferimento, necessaria per fare rierimento a un metodo esterno
 * per fare update interfaccia grafica
 */

public class Scanner_BTLE {

    private static final String TAG = "Scanner_BTLE";

    private ScannerActivity mScannerActivity;

    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner mBluetoothLeScanner;
    private boolean mIsScanning;
    private Handler mHandler;

    private static Scanner_BTLE instance=null;



    public static Scanner_BTLE getInstance(ScannerActivity mScannerActivity){
        if(instance==null)
            instance= new Scanner_BTLE(mScannerActivity);
        return instance;
    }

    private Scanner_BTLE(ScannerActivity scannerActivity) {
        mScannerActivity = scannerActivity;
        mHandler = new Handler();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) mScannerActivity.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
        this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();


    }


    public void startScan(long scanDuration, int scanMode, List<ParcelUuid> uuids) {
        Log.d(TAG, "startScan: ");
        // Since Android 6.0 we need to obtain either Manifest.permission.ACCESS_COARSE_LOCATION or Manifest.permission.ACCESS_FINE_LOCATION to be able to scan for
        // Bluetooth LE devices. This is related to beacons as proximity devices.
        // On API older than Marshmallow the following code does nothing.
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // When user pressed Deny and still wants to use this functionality, show the rationale
//            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                return;
//            }
//
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
//            return;
//        }

        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(scanMode).setReportDelay(750).build();
        //.setUseHardwareBatchingIfSupported(false).setUseHardwareFilteringIfSupported(false)
        final List<ScanFilter> filters = new ArrayList<>();
        for(ParcelUuid singleUuid:uuids)
            filters.add(new ScanFilter.Builder().setServiceUuid(singleUuid).build());
        mBluetoothLeScanner.startScan(filters,settings,scanCallback);

        this.mIsScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsScanning) {
                    stopScan();
//                    mScannerActivity.showScanningResultInDialogFragment();
                }
            }
        }, scanDuration);
    }
    public void stopScan() {
        if (this.mIsScanning) {
            Log.d(TAG, "stopScan: ");
            mBluetoothLeScanner.stopScan(scanCallback);
            mIsScanning = false;
        }
    }


    public ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
        //funziona
            for(ScanResult result: results){
                List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
                Log.d(TAG, "UUID "+uuids.get(0)+" onBatchScanResult: "+result.getDevice().getAddress()+"\nParcel"+ThingyUtils.THINGY_BASE_UUID);
                mScannerActivity.updateRecyclerView(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "onScanFailed: Not supposed to happend");
            super.onScanFailed(errorCode);
        }
    };
}