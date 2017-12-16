package com.example.muffin.myapplication;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Muffin on 09.12.2017.
 */

public class SeekBarSteerOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    /* Waiting time between next accepted updateProgress on onProgressChanged */
    private static final long TIME_CALL_COOLDOWN = 500L;
    int previous_rotation = 0;
    private MainActivity m_currentActivity;
    private TextView m_leftrightText;
    private long lastTimeCalled = 0L;

    public SeekBarSteerOnSeekBarChangeListener(MainActivity currentActivity, TextView leftrightText) {
        m_currentActivity = currentActivity;
        m_leftrightText = leftrightText;
    }

    private void updateVisual(SeekBar seekBar, int progress) {
        int realprogress = ((seekBar.getMax() + 1) / 2 - progress) * -1;
        // Very lazy way to fix wrong math
        if (realprogress > 255) realprogress = 255;


        int newclamp = invert_clamp(realprogress, -5, 5, 0);
        if (newclamp != realprogress) {
            realprogress = newclamp;
            seekBar.setProgress(seekBar.getMax() / 2);
        } else {
            seekBar.setProgress(progress);
        }

        String process = "Steering " + realprogress + "°";
        m_leftrightText.setText(process);
    }

    private void updateProgress(SeekBar seekBar, int progress) {
        int realprogress = ((seekBar.getMax() + 1) / 2 - progress) * -1;
        // Very lazy way to fix wrong math
        if (realprogress > 255) realprogress = 255;


        int newclamp = invert_clamp(realprogress, -5, 5, 0);
        if (newclamp != realprogress) {
            realprogress = newclamp;
        }
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
        updateVisual(seekBar, seekBar.getMax() / 2);
        updateProgress(seekBar, seekBar.getMax() / 2);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    private void rotateCar(int rotation) {
        if (rotation != previous_rotation) {
            int rotationDifference = Math.abs(rotation - previous_rotation);
            if (rotationDifference >= 5) {
                if (m_currentActivity.m_btMain != null) {
                    if (m_currentActivity.m_btMain.getSocket() != null) {
                        if (m_currentActivity.m_btMain.getSocket().isConnected()) {
                        /* If device is found - transmit data */
                            m_currentActivity.m_btMain.write(new String("S" + rotation).getBytes());
                            previous_rotation = rotation;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        updateVisual(seekBar, progress);
        long thisTime = System.currentTimeMillis();
        if (thisTime - lastTimeCalled >= TIME_CALL_COOLDOWN) {
            updateProgress(seekBar, progress);
            lastTimeCalled = thisTime;
        }
    }
}
