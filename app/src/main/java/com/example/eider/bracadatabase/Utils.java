package com.example.eider.bracadatabase;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Eider on 27/06/2017.
 */

public class Utils {

    public static boolean checkBluetooth(BluetoothAdapter bluetoothAdapter) {

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }
        else {
            return true;
        }
    }

    public static void requestUserBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
    }

    public static IntentFilter makeGattUpdateIntentFilter() {

        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_CONNECTED);
        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(Service_BTLE_GATT.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(Service_BTLE_GATT.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    public static String hexToString(byte[] data) {
        final StringBuilder sb = new StringBuilder(data.length);

        for(byte byteChar : data) {
            sb.append(String.format("%02X ", byteChar));
        }

        return sb.toString();
    }

    public static int hasWriteProperty(int property) {
        return property & BluetoothGattCharacteristic.PROPERTY_WRITE;
    }

    public static int hasReadProperty(int property) {
        return property & BluetoothGattCharacteristic.PROPERTY_READ;
    }

    public static int hasNotifyProperty(int property) {
        return property & BluetoothGattCharacteristic.PROPERTY_NOTIFY;
    }

    public static void toast(Context context, String string) {

        Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        toast.show();
    }
    
    public static String IntToCoord(int number) {
          String res ="";
        if(1 == number)res = "1,1";
        if(2 == number)res = "1,2";
        if(3 == number)res = "1,3";
        if(4 == number)res = "1,4";

        if(5 == number)res = "2,1";
        if(6 == number)res = "2,2";
        if(7 == number)res = "2,3";
        if(8 == number)res = "2,4";

        if(9 == number)res = "3,1";
        if(10 == number)res = "3,2";
        if(11 == number)res = "3,3";
        if(12 == number)res = "3,4";

        if(13 == number)res = "4,1";
        if(14 == number)res = "4,2";
        if(15 == number)res = "4,3";
        if(16 == number)res = "4,4";

        return res;
    }
}
