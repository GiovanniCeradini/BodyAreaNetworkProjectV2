package com.example.prova_tirocinio.objects;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class ThingyDevice extends Device {

    private static int THINGY_TYPE=1;

    public ThingyDevice(String name) {
        super(name, THINGY_TYPE);
    }

    public ThingyDevice(ScanResult scanResult) {
        super(scanResult);
    }
}
