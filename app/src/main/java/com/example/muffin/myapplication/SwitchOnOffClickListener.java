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
        m_currentActivity.m_btMain.write(new String("O|" + check).getBytes());
    }
}
