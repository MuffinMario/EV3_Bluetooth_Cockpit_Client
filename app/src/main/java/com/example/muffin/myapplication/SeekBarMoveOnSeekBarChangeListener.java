package com.example.muffin.myapplication;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Muffin on 09.12.2017.
 */

public class SeekBarMoveOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    int previous_speed = 0;
    private MainActivity m_currentActivity;
    private TextView m_moveText;

    public SeekBarMoveOnSeekBarChangeListener(MainActivity currentActivity, TextView moveText) {
        m_currentActivity = currentActivity;
        m_moveText = moveText;
    }

    private void updateProgress(SeekBar seekBar, int progress) {
        int realProgress = ((seekBar.getMax()) / 2 - progress) * -1;
        // Very lazy way to fix wrong math
        if (realProgress < -255) realProgress = -255;
        if (realProgress > 255) realProgress = 255;

        //clamp if close to 0 to 0
        int newClamp = invert_clamp(realProgress, -5, 5, 0);
        if (newClamp != realProgress) {
            realProgress = newClamp;
            seekBar.setProgress(seekBar.getMax() / 2);
        }

        String process = "Move Speed " + realProgress + "";
        Log.i(getClass().getName(), process);
        m_moveText.setText(process);
        changeSpeed(realProgress);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    private void changeSpeed(int speed) {
        if (speed != previous_speed) {
            if (m_currentActivity.m_btMain != null) {
                if (m_currentActivity.m_btMain.getSocket() != null) {
                    if (m_currentActivity.m_btMain.getSocket().isConnected()) {

                /* If device is found - transmit data */
                        m_currentActivity.m_btMain.write(new String("S|" + speed).getBytes());
                    }
                }
            }
        }
    }

    private int invert_clamp(int val, int min, int max, int goalval) {
        if (val < min || val > max) {
            return val;
        }
        return goalval;

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        updateProgress(seekBar, progress);
    }
}
