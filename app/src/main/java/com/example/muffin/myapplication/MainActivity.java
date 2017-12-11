package com.example.muffin.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /* Constants */
    public static final int REQUEST_ENABLE_BT = 1;
    public static BluetoothClientSocketThread m_btMain;
    /* GUI */
    private Button m_connectButton;
    private Switch m_onoffSwitch;
    private SeekBar m_steerSeekbar;
    private VerticalSeekBar m_moveSeekbar;
    private EditText m_textBox;
    private TextView m_leftrightText;
    private TextView m_moveText;
    /* Adapter */
    private BluetoothAdapter m_btAdapter;
    /* Sensors */
    private SensorManager m_sensorManager;
    private Sensor m_rotationSensor;


    private boolean init() {
        /* 1. Initiate by XML */
        m_connectButton = findViewById(R.id.button_connect);
        m_onoffSwitch = findViewById(R.id.switch_onoff);
        m_steerSeekbar = findViewById(R.id.seekBar_steer);
        m_moveSeekbar = findViewById(R.id.seekBar_move);
        m_textBox = findViewById(R.id.editText_debugConsole);
        m_leftrightText = findViewById(R.id.textView_leftright);
        m_moveText = findViewById(R.id.textView_move);

        /* 2. Initiate sensors */
        m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        m_rotationSensor = m_sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        /* 3. Check for adapters */
        m_btAdapter = BluetoothAdapter.getDefaultAdapter();
        m_btMain = null;

        /* 4. Add Listeners */
        m_connectButton.setOnClickListener(new ButtonConnectOnClickListener(m_btAdapter, this, m_textBox));
        m_onoffSwitch.setOnClickListener(new SwitchOnOffClickListener(this));
        m_steerSeekbar.setOnSeekBarChangeListener(new SeekBarSteerOnSeekBarChangeListener(this, m_leftrightText));
        m_moveSeekbar.setOnSeekBarChangeListener(new SeekBarMoveOnSeekBarChangeListener(this, m_moveText));
        m_sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                String txt = "";
                //m_textBox.getText().clear();
                for (int i = 0; i < event.values.length; i++) {
                    txt += ("values[" + i + "]: " + Math.round(Math.toDegrees(event.values[i]))) + "\r\n";
                }
                /*
                if(event.values.length > 0) {
                    m_textBox.append(txt);
                }*/
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.i("SensorEventListener()", "Listener: " + sensor.getName() + " | acc: " + accuracy);
            }
        }, m_rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        /* Returns true on all init */
        boolean all_success;

        all_success = (m_connectButton != null) && (m_onoffSwitch != null) && (m_steerSeekbar != null) && (m_moveSeekbar != null) && (m_textBox != null)
                && (m_btAdapter != null)
                && (m_rotationSensor != null);
        return all_success;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (!init()) {
            System.exit(1);
        }
    }
}
