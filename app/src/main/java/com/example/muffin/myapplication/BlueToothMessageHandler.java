package com.example.muffin.myapplication;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Muffin on 09.12.2017.
 */

public class BlueToothMessageHandler extends Handler {
    private Activity m_currentActivity;
    private EditText m_textBox;

    public BlueToothMessageHandler(Activity currentActivity, EditText debugTextBox) {
        m_currentActivity = currentActivity;
        m_textBox = debugTextBox;
    }

    @Override
    public void handleMessage(Message msg) {
        // Codes
        switch (msg.what) {
            case BluetoothClientSocketThread.RETURN_WRITE:
                m_textBox.append("WRITTEN: " + msg.obj + "\r\n");
                break;
            case BluetoothClientSocketThread.RETURN_READ:
                m_textBox.append("READ: " + msg.obj + "\r\n");
                break;
            case BluetoothClientSocketThread.RETURN_TOAST:
                Toast.makeText(m_currentActivity.getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
