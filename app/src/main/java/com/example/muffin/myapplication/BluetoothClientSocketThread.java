package com.example.muffin.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Muffin on 09.12.2017.
 */

public class BluetoothClientSocketThread implements Runnable {
    /* Message constants */
    public static final int RETURN_WRITE = 0;
    public static final int RETURN_READ = 1;
    public static final int RETURN_TOAST = 2;
    /* Socket and Device necessary */
    private final BluetoothDevice m_btDevice;
    private BluetoothSocket m_btSocket;
    /* Socket reading */
    private InputStream m_is;
    private OutputStream m_os;
    private byte[] m_buf;
    private Handler m_handler;
    /* Default */
    private Activity m_currentActivity;

    public BluetoothClientSocketThread(Activity currentActivity, EditText debugTextBox, BluetoothDevice btDevice) {
        /* Temporary variable, because of final initiation */
        BluetoothSocket btClient = null;
        m_btDevice = btDevice;

        m_currentActivity = currentActivity;

        /* Set default values */
        m_is = null;
        m_os = null;
        m_buf = null;
        m_handler = new BlueToothMessageHandler(m_currentActivity, debugTextBox);


        try {
            btClient = m_btDevice.createRfcommSocketToServiceRecord(UUID.randomUUID());
        } catch (IOException ioe) {
            Log.e(this.getClass().getName() + "()", "Socket couldn't be created: ", ioe);
        }
        m_btSocket = btClient;
    }

    public void write(byte[] bytes) {
        try {
            String text = new String(bytes) + "\r\n";
            m_os.write(text.getBytes());

            Message msg = m_handler.obtainMessage(BluetoothClientSocketThread.RETURN_WRITE, -1, -1, new String(bytes));
            msg.sendToTarget();
        } catch (IOException e) {
            Log.e("write()", "Error on write", e);
        }
    }

    public boolean connect() {
        /* Close discovering of BT, because it slows down */
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        Log.i("connect()", "Connecting...");
        try {
            m_btSocket.connect();
        } catch (IOException e) {
            try {
                m_btSocket.close();
            } catch (IOException e1) {
                Log.e("[" + this.getClass().getName() + "]", "Socket couldn't close: ", e1);
                onConnectionLost();
            }
            // On error stop
            return false;
        }

        Log.i("connect()", "Setting up input streams...");
        String stream = "Input";
        try {
            m_is = m_btSocket.getInputStream();
            stream = "Output";
            m_os = m_btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(this.getClass().getName(), stream + " stream couldn't be created", e);
            return false;
        }
        return true;
    }

    public void listen() {
        /* Allocating more-than-enough memory for listening */
        m_buf = new byte[1024];

        boolean quit = false;
        while (quit) {
            int byteLen = 0;
            try {
                byteLen = m_is.read(m_buf);
                Message theMessage = m_handler.obtainMessage(BluetoothClientSocketThread.RETURN_READ, byteLen, -1, new String(m_buf));
                theMessage.sendToTarget();
                quit = handleMessage(new String(m_buf));
            } catch (IOException e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
    }

    private boolean handleMessage(String s) {
        boolean quit = false;

        if (s.equals("EXIT")) {
            quit = true;
        }

        return quit;
    }

    /* "Hello User, your device disconnected from the Bluetooth server (the other guy). Do you want to reconnect? (Yes / Yes Other / No) " */
    public void onConnectionLost() {
        CheckBox connectedCheckBox = m_currentActivity.findViewById(R.id.checkBox);
        connectedCheckBox.setChecked(false);

    }

    public void run() {
        boolean isConnected = connect();
        CheckBox connectedCheckBox = m_currentActivity.findViewById(R.id.checkBox);
        connectedCheckBox.setChecked(isConnected);
        Log.i("run()", "Connected? : " + isConnected);
        if (isConnected) {
            listen();
        } else {
            Message theMessage = m_handler.obtainMessage(BluetoothClientSocketThread.RETURN_TOAST, -1, -1, "Could not create connection");
            theMessage.sendToTarget();
        }

    }

    public BluetoothSocket getSocket() {
        return m_btSocket;
    }
}
