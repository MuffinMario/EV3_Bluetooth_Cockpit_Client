package com.example.muffin.myapplication;

import android.view.View;

/**
 * Created by Muffin on 09.12.2017.
 */

public class ButtonGoOnClickListener implements View.OnClickListener {

    public void onClick(View v) {
        System.out.println("Clicked: " + v.getTransitionName());
    }
}
