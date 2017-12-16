package com.example.muffin.myapplication;

import android.view.View;
import android.widget.Switch;

/**
 * Created by Muffin on 09.12.2017.
 */

public class SwitchOnOffClickListener implements View.OnClickListener {
    /* Main Activity */
    private MainActivity m_currentActivity;

    public SwitchOnOffClickListener(MainActivity currentActivity) {
        m_currentActivity = currentActivity;
    }

    @Override
    public void onClick(View v) {
        Switch nintendo = (Switch) v;
        boolean check = nintendo.isChecked();
        if (m_currentActivity.m_btMain != null && m_currentActivity.m_btMain.getSocket() != null && m_currentActivity.m_btMain.getSocket().isConnected() == true) {
            m_currentActivity.m_btMain.write(new String("O" + (check ? 1 : 0)).getBytes());
        }
    }
}
