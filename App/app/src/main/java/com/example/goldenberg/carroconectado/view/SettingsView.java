package com.example.goldenberg.carroconectado.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.example.goldenberg.carroconectado.model.Model;
import com.example.goldenberg.carroconectado.R;

/**
 * Created by Goldenberg on 27/09/16.
 */
public class SettingsView {
    Model model;
    View view;
    public ToggleButton bluetooth_btn;
    public Button httpGet;

    public SettingsView(View view, Model model){
        this.model = model;
        this.view = view;

        this.bluetooth_btn = (ToggleButton) view.findViewById(R.id.toggleButton);
        this.httpGet = (Button) view.findViewById(R.id.button);
    }

    public Context getContext(){return view.getContext();}
}
