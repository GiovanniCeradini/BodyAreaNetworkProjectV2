package com.example.prova_tirocinio.objects;

import android.bluetooth.BluetoothDevice;

/**
    Classe di prova per implementare una recyclerview nella fase di scan con molteplici oggetti
 */

public class GlassDevice {

    private BluetoothDevice device;
    private String name;
    private int rssi;

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
