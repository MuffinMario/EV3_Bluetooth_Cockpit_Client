package com.example.muffin.myapplication;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Muffin on 09.12.2017.
 */

public class SeekBarSteerOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    int previous_rotation = 0;
    private MainActivity m_currentActivity;
    private TextView m_leftrightText;

    public SeekBarSteerOnSeekBarChangeListener(MainActivity currentActivity, TextView leftrightText) {
        m_currentActivity = currentActivity;
        m_leftrightText = leftrightText;
    }

    private void updateProgress(SeekBar seekBar, int progress) {

        int realprogress = ((seekBar.getMax() + 1) / 2 - progress) * -1;
        // Very lazy way to fix wrong math
        if (realprogress > 255) realprogress = 255;


        int newclamp = invert_clamp(realprogress, -5, 5, 0);
        if (newclamp != realprogress) {
            realprogress = newclamp;
            seekBar.setProgress(seekBar.getMax() / 2);
        }

        String process = "Steering " + realprogress + "°";
        Log.i(getClass().getName(), process);
        m_leftrightText.setText(process);
        if (realprogress < 0)
            rotateCar(realprogress);
    }

    private int invert_clamp(int val, int min, int max, int goalval) {
        if (val < min || val > max) {
            return val;
        }
        return goalval;

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        seekBar.setProgress(seekBar.getMax() / 2);
        updateProgress(seekBar, seekBar.getMax() / 2);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    private void rotateCar(int rotation) {
        if (rotation != previous_rotation) {
            if (m_currentActivity.m_btMain != null) {
                if (m_currentActivity.m_btMain.getSocket() != null) {
                    if (m_currentActivity.m_btMain.getSocket().isConnected()) {
                    /* If device is found - transmit data */
                        m_currentActivity.m_btMain.write(new String("R|" + rotation).getBytes());
                    }
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        updateProgress(seekBar, progress);
    }
}
