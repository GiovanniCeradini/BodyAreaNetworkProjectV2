package com.example.prova_tirocinio.objects;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
    Classe che rappresenta i device che posso trovare nella scansione, usata per provare
    recyclerview con più view, in futuro separazione in vari oggetti
 */


public class Device {

    private String mName;
    private int mType;
    private BluetoothDevice mBluetoothDevice;
    private int mRssi;

    public Device(String name, int type){
        this.mName=name;
        this.mType=type;
    }

    public Device(ScanResult scanResult) {
        mBluetoothDevice = scanResult.getDevice();
        this.mName = scanResult.getScanRecord() != null ? scanResult.getScanRecord().getDeviceName() : null;
//        this.rssi = scanResult.getRssi();
    }

    public Device(BluetoothDevice bluetoothDevice, int rssi, int type){
        this.mBluetoothDevice=bluetoothDevice;
        this.mRssi=rssi;
        this.mType=type;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setRssi(int rssi) {
        this.mRssi = rssi;
    }

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public int getRssi() {
        return mRssi;
    }

    public String getAddress(){return this.mBluetoothDevice.getAddress();}

    /**
     *
     * @param scanResult
     * @return true if scanResult.BluetoothDevice equals this.bluetoothDevice
     */
    public boolean matchesResultDevice(final ScanResult scanResult) {
        return this.mBluetoothDevice.getAddress().equals(scanResult.getDevice().getAddress());
    }
    public BluetoothDevice getBluetoothDevice() {
        return this.mBluetoothDevice;
    }

    public static List<Device> get10DevicesForTesting(){
        //1 Thingy, 2 Wagoo, 3 SmartWatch

        List <Device> list =new ArrayList<>();

        list.add(new Device("Thingy1", 1));
        list.add(new Device("Wagoo2", 2));
        list.add(new Device("Watch3", 3));
        list.add(new Device("Thingy4", 1));
        list.add(new Device("Wagoo5", 2));
        list.add(new Device("Watch6", 3));
        list.add(new Device("Thingy7", 1));
        list.add(new Device("Wagoo8", 2));
        list.add(new Device("Watch9", 3));
        list.add(new Device("Thingy10", 1));

        return list;

    }

}
