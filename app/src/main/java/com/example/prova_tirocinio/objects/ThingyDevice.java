package com.example.prova_tirocinio.objects;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * Aggiunta di attributi, non presenti in BluetoothDevice.
 */
public class ThingyDevice implements Parcelable {

    private BluetoothDevice device;
    private ScanResult lastScanResult;
    private String name;
    private int rssi;

    public ThingyDevice(final ScanResult scanResult) {
        device = scanResult.getDevice();
        this.name = scanResult.getScanRecord() != null ? scanResult.getScanRecord().getDeviceName() : null;
        this.rssi = scanResult.getRssi();
    }

    public ThingyDevice(final BluetoothDevice devicein) {
        device = devicein;
        this.name = devicein.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return device.getAddress();
    }

    public int getRssi() {
        return rssi;
    }

    public boolean matches(final ScanResult scanResult) {
        return device.getAddress().equals(scanResult.getDevice().getAddress());
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ThingyDevice) {
            final ThingyDevice that = (ThingyDevice) o;
            return device.getAddress().equals(that.device.getAddress());
        }
        return super.equals(o);
    }


    /*PERCELABE IMPLEMENTATION*/
    private ThingyDevice(Parcel in) {
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        lastScanResult = in.readParcelable(ScanResult.class.getClassLoader());
        name = in.readString();
        rssi = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(device, flags);
        parcel.writeParcelable(lastScanResult, flags);
        parcel.writeString(name);
        parcel.writeInt(rssi);
    }

    public static final Creator<ThingyDevice> CREATOR = new Creator<ThingyDevice>() {

        @Override
        public ThingyDevice createFromParcel(Parcel in) {
            return new ThingyDevice(in);
        }

        @Override
        public ThingyDevice[] newArray(int size) {
            return new ThingyDevice[size];
        }
    };


}
