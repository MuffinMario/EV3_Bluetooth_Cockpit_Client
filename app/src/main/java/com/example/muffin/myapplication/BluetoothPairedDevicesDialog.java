package com.example.muffin.myapplication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Muffin on 09.12.2017.
 */
class BluetoothPairedDevicesDialogOnClickListener implements DialogInterface.OnClickListener {
    BluetoothDevice[] m_btDevices;
    EditText m_debugTextBox;
    MainActivity m_currentActivity;

    public BluetoothPairedDevicesDialogOnClickListener(EditText debugTextBox, MainActivity currentActivity, BluetoothDevice[] devices) {
        m_btDevices = devices;
        m_debugTextBox = debugTextBox;
        m_currentActivity = currentActivity;
    }

    public void connect(BluetoothDevice btDevice) {
        Thread t = new Thread(m_currentActivity.m_btMain = new BluetoothClientSocketThread(m_currentActivity, m_debugTextBox, btDevice));
        t.start();
    }

    public BluetoothDevice[] findDevices() {

        return null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //last one
        if (which == m_btDevices.length) {
            findDevices();
            //create new dialog

        } else {
            connect(m_btDevices[which]);
        }
        Toast.makeText(m_currentActivity, m_btDevices[which].getName(), Toast.LENGTH_SHORT).show();
    }
}

public class BluetoothPairedDevicesDialog extends DialogFragment {
    private MainActivity m_currentActivity;
    private EditText m_debugTextBox;
    private Set<BluetoothDevice> m_btDevices;

    public BluetoothPairedDevicesDialog initialize(MainActivity currentActivity, EditText debugTextBox, Set<BluetoothDevice> btDevices) {
        m_btDevices = btDevices;
        m_currentActivity = currentActivity;
        m_debugTextBox = debugTextBox;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /* Building the Dialog */
        final int BT_DEV_SIZE = m_btDevices.size();
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(getActivity());
        BluetoothDevice[] btArr = new BluetoothDevice[BT_DEV_SIZE];
        int i_c = 0;
        for (BluetoothDevice btDevice : m_btDevices) {
            btArr[i_c++] = btDevice;
        }
        String[] btDeviceNames = new String[btArr.length + 1];
        for (int i = 0; i < btArr.length; i++) {
            btDeviceNames[i] = btArr[i].getAddress() + " (" + btArr[i].getName() + ")";
        }
        btDeviceNames[btArr.length] = "Find other devices...";
        dBuilder.setItems(btDeviceNames, new BluetoothPairedDevicesDialogOnClickListener(m_debugTextBox, m_currentActivity, btArr));
        return dBuilder.create();
    }

}
