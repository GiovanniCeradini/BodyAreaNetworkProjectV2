package com.example.prova_tirocinio.objects;

import android.bluetooth.BluetoothDevice;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
    Classe di prova per implementare una recyclerview nella fase di scan con molteplici oggetti
 */

public class WagooDevice extends Device{

    private static int WAGOO_TYPE=2;

    public WagooDevice(String name) {
        super(name, WAGOO_TYPE);
    }

    public WagooDevice(ScanResult scanResult){
        super(scanResult);
    }


    public WagooDevice(BluetoothDevice device, int rssi) {
        super(device,rssi,WAGOO_TYPE);
    }
}

