package com.example.eider.bracadatabase;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Eider on 27/06/2017.
 */

public class BTLE_Device {
    private BluetoothDevice bluetoothDevice;
    private int rssi;

    public BTLE_Device(android.bluetooth.BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    public String getName() {
        return bluetoothDevice.getName();
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }

    public int getRSSI() {
        return rssi;
    }
}
