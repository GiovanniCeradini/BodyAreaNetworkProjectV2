package com.example.prova_tirocinio;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.Ble_stuff.Scanner_BTLE;
import com.example.prova_tirocinio.adapters.DevicesAdapter;
import com.example.prova_tirocinio.databinding.ActivityScannerBinding;
import com.example.prova_tirocinio.fragments.ScannerSelectionDialogFragment;
import com.example.prova_tirocinio.objects.Device;
import com.example.prova_tirocinio.objects.ThingyDevice;
import com.example.prova_tirocinio.objects.WagooDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.nordicsemi.android.thingylib.utils.ThingyUtils;



public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "ScannerActivity";

    private ActivityScannerBinding mScannerBinding;
    private Button btnScan;
    private DevicesAdapter mAdapterDevicesConnected;
    private RecyclerView mRecyclerViewDevicesConnected;


    /**
     * Scanning Stuff
     */

    private final static long SCAN_DURATION = 8000;

    private Scanner_BTLE mScanner_btle;

    //Contiene la lista dei device trovati tramite Scanner_BRLE per singolo scanning
    private ArrayList<Device> mBTDevicesFoundArrayList;

    //Contiene corrispondenza address, Device, onBatchResult può venire chiamato più volte per dispositivo
    private static HashMap<String, Device> mBTDevicesFoundHashMap;

    private List<ParcelUuid> mUuids;
    private final static ParcelUuid parcelThingyUuid=new ParcelUuid(ThingyUtils.THINGY_BASE_UUID);

    private DevicesAdapter mAdapterDialogFragment;
    private Boolean isScanning=false;

    private  ScannerSelectionDialogFragment mScannerDialogFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerBinding= DataBindingUtil.setContentView(this, R.layout.activity_scanner);


        mScanner_btle=Scanner_BTLE.getInstance(this);
        mBTDevicesFoundHashMap = new HashMap<>();
        mBTDevicesFoundArrayList = new ArrayList<>();
        mUuids= new ArrayList<>();
        mUuids.add(parcelThingyUuid);
        mAdapterDialogFragment=new DevicesAdapter(mBTDevicesFoundArrayList);



        mRecyclerViewDevicesConnected=mScannerBinding.recyclerView;
        mRecyclerViewDevicesConnected.setLayoutManager(new LinearLayoutManager(this));
        mAdapterDevicesConnected=new DevicesAdapter(mBTDevicesFoundArrayList);
        mRecyclerViewDevicesConnected.setAdapter(mAdapterDevicesConnected);

        btnScan=mScannerBinding.btnScan;



        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isScanning){
                    mBTDevicesFoundArrayList.clear();
                    mBTDevicesFoundHashMap.clear();
                    mAdapterDialogFragment.notifyDataSetChanged();
                    showAlertDialog();
                    mScanner_btle.startScan(SCAN_DURATION, ScanSettings.SCAN_MODE_LOW_LATENCY,mUuids);
                    btnScan.setText("Scanning,press to stop scan");
                    isScanning=true;
                }else{
                    mScanner_btle.stopScan();
                    btnScan.setText("Start Scan");
                    isScanning=false;
                }
            }
        });
    }

    public void updateRecyclerViewDialogFragment(ScanResult result){
        Log.d(TAG, "updateRecyclerView: ");
        addDevice(result);
        mScannerDialogFragment.setProgressBarVisibility(false);
        mAdapterDialogFragment.notifyDataSetChanged();

    }

    /**
     * Metodo per capire se ho già rilevato il device, onBathScanResult può essere chiamata più volte per lo stesso dispositivo
     * Se ho già rilevato il dispositivo non voglio mostrarlo un'altra volta
     */

    public void addDevice(ScanResult result) {
        Device btleDevice;

        String address = result.getDevice().getAddress();
        List<ParcelUuid> uuids = result.getScanRecord().getServiceUuids();
        if (!mBTDevicesFoundHashMap.containsKey(address)) {
            if(uuids.get(0).equals(parcelThingyUuid))
                btleDevice = new ThingyDevice(result.getDevice(),result.getRssi());
            else
                btleDevice = new WagooDevice(result.getDevice(),result.getRssi());

            mBTDevicesFoundHashMap.put(address, btleDevice);
            mBTDevicesFoundArrayList.add(btleDevice);
        }
        else {
            mBTDevicesFoundHashMap.get(address).setRssi(result.getRssi());
        }
    }

    private void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        mScannerDialogFragment = ScannerSelectionDialogFragment.newInstance("Devices Found", mAdapterDialogFragment,this);
        mScannerDialogFragment.show(fm, "fragment_alert");
    }

    public void stopScanBTLE(){
        mScanner_btle.stopScan();
        Log.d(TAG, "stopScan: ");
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
}
