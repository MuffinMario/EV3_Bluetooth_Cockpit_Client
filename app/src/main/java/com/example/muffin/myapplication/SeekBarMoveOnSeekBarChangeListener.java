package com.example.muffin.myapplication;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Muffin on 09.12.2017.
 */

public class SeekBarMoveOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    /* Waiting time between next accepted updateProgress on onProgressChanged */
    private static final long TIME_CALL_COOLDOWN = 500L;
    private final SeekBar m_seekBar;
    private int previous_speed = 0;
    private MainActivity m_currentActivity;
    private TextView m_moveText;
    private Thread listenerThread;

    public SeekBarMoveOnSeekBarChangeListener(MainActivity currentActivity, SeekBar seekBar, TextView moveText) {
        m_currentActivity = currentActivity;
        m_moveText = moveText;
        m_seekBar = seekBar;
    }

    private void updateVisual(SeekBar seekBar, int progress) {
        int realProgress = ((seekBar.getMax() + 1) / 2 - progress) * -1;
        // Very lazy way to fix wrong math
        if (realProgress > 100) realProgress = 100;
        if (realProgress < -100) realProgress = -100;


        int newclamp = invert_clamp(realProgress, -10, 10, 0);
        if (newclamp != realProgress) {
            realProgress = newclamp;
            seekBar.setProgress(seekBar.getMax() / 2);
        } else {
            seekBar.setProgress(progress);
        }

        String process = "Move Speed " + realProgress + "";
        m_moveText.setText(process);
    }

    private void updateProgress(SeekBar seekBar, int progress) {
        int realprogress = ((seekBar.getMax() + 1) / 2 - progress) * -1;
        // Very lazy way to fix wrong math
        if (realprogress > 100) realprogress = 100;
        if (realprogress < -100) realprogress = -100;


        int newclamp = invert_clamp(realprogress, -5, 5, 0);
        if (newclamp != realprogress) {
            realprogress = newclamp;
        }
        changeSpeed(realprogress);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i("stop", "now");
        stopListener();
        updateVisual(seekBar, seekBar.getProgress());
        updateProgress(seekBar, seekBar.getProgress());
        listenerThread.interrupt();
        Log.i("stop", "now 2");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        Log.i("start", "now");
        stopListener();
        Log.i("start", "now 2");
        listenerThread = new Thread(new ListenerRunnable(m_seekBar));
    }

    private void stopListener() {
        if (listenerThread != null) {
            listenerThread.interrupt();
            try {
                listenerThread.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeSpeed(int speed) {
        if (speed != previous_speed) {
            int speedDifference = Math.abs(speed - previous_speed);
            if (speedDifference >= 5) {
                if (m_currentActivity.m_btMain != null) {
                    if (m_currentActivity.m_btMain.getSocket() != null) {
                        if (m_currentActivity.m_btMain.getSocket().isConnected()) {
                            /* If device is found - transmit data */
                            m_currentActivity.m_btMain.write(new String("M" + speed).getBytes());
                            previous_speed = speed;
                        }
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
        //idk replaced with thread
    }

    class ListenerRunnable implements Runnable {
        private volatile SeekBar m_seekBar;

        public ListenerRunnable(SeekBar seekBar) {
            m_seekBar = seekBar;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                updateVisual(m_seekBar, m_seekBar.getProgress());
                updateProgress(m_seekBar, m_seekBar.getProgress());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
