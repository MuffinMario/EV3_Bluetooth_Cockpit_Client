package com.example.muffin.myapplication;

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

/**
 * Created by Muffin on 09.12.2017.
 */

class InformationWrapper {
    public static final int MOVE_DIRECTION_FORWARD = 0;
    public static final int MOVE_DIRECTION_BACKWARD = 1; // MODEL HAS BACKWARDS = FORWARD & VICE VERSA THUS BACKWARD IS ACTUALLY FORWARD
    public static final int MOVE_DIRECTION_STOP = 2;
    public static final int SPEED_INDEX = 1;
    public static final int ROTATION_INDEX = 2;
    public static final int DIRECTION_INDEX = 2;
    public static final int POWER_INDEX = 4;
    private final int m_speed;
    private final int m_rotation;
    private final int m_direction;
    private final boolean m_power;

    public InformationWrapper(String[] information_arr) {
        Log.d("power", information_arr[POWER_INDEX]);
        Log.d("SPEED", information_arr[SPEED_INDEX]);
        Log.d("dir", information_arr[DIRECTION_INDEX]);
        Log.d("rota", information_arr[ROTATION_INDEX]);
        m_speed = parsei(information_arr[SPEED_INDEX]) * (
                (m_direction = parsei(information_arr[DIRECTION_INDEX])) == MOVE_DIRECTION_BACKWARD ?
                        -1 : // if is backward
                        1);
        m_rotation = parsei(information_arr[ROTATION_INDEX]);
        m_power = parseb(information_arr[POWER_INDEX]);
    }

    private final static int parsei(String str) {
        return Integer.parseInt(str);
    }

    private final static boolean parseb(String str) {
        return Integer.parseInt(str) != 0;
    }

    public int getSpeed() {
        return m_speed;
    }

    public int getRotation() {
        return m_rotation;
    }

    public int getDirection() {
        return m_direction;
    }

    public boolean isPower() {
        return m_power;
    }

}
public class BluetoothClientSocketThread implements Runnable {
    /* Message constants */
    public static final int RETURN_WRITE = 0;
    public static final int RETURN_READ = 1;
    public static final int RETURN_TOAST = 2;
    public static final int RETURN_INFORMATION = 3;

    /* Socket and Device necessary */
    private final BluetoothDevice m_btDevice;
    private BluetoothSocket m_btSocket;
    /* Socket reading */
    private InputStream m_is;
    private OutputStream m_os;
    private byte[] m_buf;
    private Handler m_handler;
    /* Default */
    private MainActivity m_currentActivity;

    public BluetoothClientSocketThread(MainActivity currentActivity, EditText debugTextBox, BluetoothDevice btDevice) {
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
            btClient = m_btDevice.createRfcommSocketToServiceRecord(btDevice.getUuids()[0].getUuid());
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
        while (!quit) {
            int byteLen = 0;
            try {
                byteLen = m_is.read(m_buf);
                Log.d("read():", new String(m_buf).substring(0, byteLen - 1));
                Message theMessage = m_handler.obtainMessage(BluetoothClientSocketThread.RETURN_READ, byteLen, -1, new String(m_buf));
                theMessage.sendToTarget();
                quit = handleMessage(new String(m_buf));
            } catch (IOException e) {
                Log.e(getClass().getName(), e.getMessage());
                onConnectionLost();
            }
        }
    }
    private boolean handleMessage(String s) {
        boolean quit = false;
        if (s.startsWith("I")) {
            InformationWrapper informationWrapper = new InformationWrapper(s.trim().split("\\|"));

            Message message = m_handler.obtainMessage(BluetoothClientSocketThread.RETURN_INFORMATION, informationWrapper);
            message.sendToTarget();

        }
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
