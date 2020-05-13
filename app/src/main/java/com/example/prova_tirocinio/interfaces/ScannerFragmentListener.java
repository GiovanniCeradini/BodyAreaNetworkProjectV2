package com.example.prova_tirocinio.interfaces;

import com.example.prova_tirocinio.objects.Device;

/**
 * Not yet used
 */


public interface ScannerFragmentListener {

    /**
     * Called when user has selected the device.
     *
     * @param device the selected device. May not be null.
     *
     */
    void onDeviceSelected(Device device);

    void onNothingSelected();
}
