package com.example.prova_tirocinio.interfaces;

import com.example.prova_tirocinio.objects.ThingyDevice;

public interface ScannerFragmentListener {

    /**
     * Called when user has selected the device.
     *
     * @param device the selected device. May not be null.
     *
     */
    void onDeviceSelected(ThingyDevice device);

    void onNothingSelected();
}
