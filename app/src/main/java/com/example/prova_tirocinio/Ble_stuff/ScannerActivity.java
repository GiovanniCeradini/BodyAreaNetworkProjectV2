package com.example.prova_tirocinio.Ble_stuff;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.R;
import com.example.prova_tirocinio.adapters.AdapterDialogFragment;
import com.example.prova_tirocinio.databinding.ActivityScannerBinding;
import com.example.prova_tirocinio.objects.Device;
import com.example.prova_tirocinio.objects.ThingyDevice;
import com.example.prova_tirocinio.objects.WagooDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import no.nordicsemi.android.thingylib.utils.ThingyUtils;



public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "ScannerActivity";

    private final static long SCAN_DURATION = 8000;

    //eliminabili, erano per rendere codice più pulito usando Utils.requestUserBluetooth
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_BT_PERMISSIONS = 0;
    private final static int REQUEST_PERMISSION_REQ_CODE = 76; // any 8-bit number

    private Scanner_BTLE mScanner_btle;

    //Contiene la lista dei device trovati tramite Scanner_BRLE per singolo scanning
    private ArrayList<Device> mBTDevicesFoundArrayList;

    //Contiene corrispondenza address, Device, onBatchResult può venire chiamato più volte per dispositivo
    private static HashMap<String, Device> mBTDevicesHashMap;

    private List<ParcelUuid> mUuids;
    private final static ParcelUuid parcelThingyUuid=new ParcelUuid(ThingyUtils.THINGY_BASE_UUID);


    private ActivityScannerBinding mScannerBinding;
    private Button btnScan;
    private AdapterDialogFragment mAdapter;
    private RecyclerView mRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerBinding= DataBindingUtil.setContentView(this, R.layout.activity_scanner);


        mScanner_btle=Scanner_BTLE.getInstance(this);
        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesFoundArrayList = new ArrayList<>();


        mUuids= new ArrayList<>();
        mUuids.add(parcelThingyUuid);


        mRecyclerView=mScannerBinding.recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new AdapterDialogFragment(mBTDevicesFoundArrayList);
        mRecyclerView.setAdapter(mAdapter);

        btnScan=mScannerBinding.btnScan;
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScanner_btle.startScan(SCAN_DURATION,ScanSettings.SCAN_MODE_LOW_LATENCY,mUuids);
                btnScan.setText("Scanning...");
            }
        });
    }

    public void updateRecyclerView(ScanResult result){
        Log.d(TAG, "updateRecyclerView: ");
        addDevice(result);
        mAdapter.notifyDataSetChanged();
        btnScan.setText("Start Scan");
    }

    /**
     * Metodo per capire se ho già rilevato il device, onBathScanResult può essere chiamata più volte per lo stesso dispositivo
     * Se ho già rilevato il dispositivo non voglio mostrarlo un'altra volta
     */

    public void addDevice(ScanResult result) {
        Device btleDevice;

        String address = result.getDevice().getAddress();
        List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
        if (!mBTDevicesHashMap.containsKey(address)) {
            if(uuids.get(0).equals(parcelThingyUuid))
                btleDevice = new ThingyDevice(result.getDevice(),result.getRssi());
            else
                btleDevice = new WagooDevice(result.getDevice(),result.getRssi());

            mBTDevicesHashMap.put(address, btleDevice);
            mBTDevicesFoundArrayList.add(btleDevice);
        }
        else {
            mBTDevicesHashMap.get(address).setRssi(result.getRssi());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanner_btle.stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScanner_btle.stopScan();
    }

    public void showScanningResultInDialogFragment() {
    }
}
