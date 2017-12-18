package com.example.muffin.myapplication;

import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Muffin on 09.12.2017.
 */

public class BlueToothMessageHandler extends Handler {
    private MainActivity m_currentActivity;
    private EditText m_textBox;

    public BlueToothMessageHandler(MainActivity currentActivity, EditText debugTextBox) {
        m_currentActivity = currentActivity;
        m_textBox = debugTextBox;
    }

    private void updateAllItems(InformationWrapper informationWrapper) {
        int h_1 = m_currentActivity.m_moveSeekbar.getMax() / 2;
        m_currentActivity.m_moveSeekbar.setProgress(h_1 + informationWrapper.getSpeed());
        m_currentActivity.m_moveText.setText("Move Speed " + informationWrapper.getSpeed());
        int h_2 = m_currentActivity.m_steerSeekbar.getMax() / 2;
        m_currentActivity.m_steerSeekbar.setProgress(h_2 + informationWrapper.getRotation());
        m_currentActivity.m_leftrightText.setText("Steering " + informationWrapper.getRotation() + "°");

        m_currentActivity.m_onoffSwitch.setChecked(informationWrapper.isPower());
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
            case BluetoothClientSocketThread.RETURN_INFORMATION:
                InformationWrapper informationWrapper = (InformationWrapper) msg.obj;
                updateAllItems(informationWrapper);


        }
    }
}
