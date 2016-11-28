package com.example.goldenberg.carroconectado.Application;

/**
 * Created by Goldenberg on 27/09/16.
 */
import android.app.Application;

import com.example.goldenberg.carroconectado.model.Model;

public class CarroConectado extends Application {
    private Model model = new Model();

    public Model getModel() { return model; }

    public void setModel(Model model) {
        this.model = model;
    }
}
