package com.example.goldenberg.carroconectado.controller;

import android.view.View;

import com.example.goldenberg.carroconectado.HttpRequest.GetSpeedLimit;
import com.example.goldenberg.carroconectado.HttpRequest.SendData;
import com.example.goldenberg.carroconectado.model.Model;
import com.example.goldenberg.carroconectado.view.SettingsView;

/**
 * Created by Goldenberg on 27/09/16.
 */
public class BtnHttpCtrl implements View.OnClickListener{
    SettingsView view;
    Model model;

    public BtnHttpCtrl(SettingsView view, Model model){
        this.view = view;
        this.model = model;

        this.view.httpGet.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == view.httpGet){
            //new GetSpeedLimit().execute();
            String speed_obd,speed_limit,GPS,date,user_id,arduino_id;
            speed_obd = "25";
            speed_limit = "50";
            GPS = "-46.1234,-23.5677";
            date = "23/10/2016";
            user_id = "583a02f555e01f5b9a9bf972";
            arduino_id = "583a030c55e01f5b9a9bf973";
            new SendData().execute(speed_obd,speed_limit,GPS,date,user_id,arduino_id);
        }
    }
}
