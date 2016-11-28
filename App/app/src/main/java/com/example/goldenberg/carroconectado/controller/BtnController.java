package com.example.goldenberg.carroconectado.controller;

import android.widget.CompoundButton;

import com.example.goldenberg.carroconectado.MainActivity;
import com.example.goldenberg.carroconectado.model.Model;
import com.example.goldenberg.carroconectado.view.SettingsView;

/**
 * Created by Goldenberg on 27/09/16.
 */
public class BtnController implements CompoundButton.OnCheckedChangeListener{
    SettingsView view;
    Model model;

    public BtnController(SettingsView view, Model model){
        this.view = view;
        this.model = model;

        this.view.bluetooth_btn.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            ((MainActivity) view.getContext()).connect();
        }
        else{
            //Turn Bluetooth offs
            ((MainActivity) view.getContext()).disconnect();
        }
    }
}
