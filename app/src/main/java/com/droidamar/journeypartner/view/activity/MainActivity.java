package com.droidamar.journeypartner.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.droidamar.journeypartner.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatButton btnDestination, btnStartAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListeners();
    }

    private void init() {
        btnDestination = (AppCompatButton) findViewById(R.id.btnDestination);
        btnStartAlarm = (AppCompatButton) findViewById(R.id.btnStartAlarm);
    }

    private void initListeners() {
        btnDestination.setOnClickListener(this);
        btnStartAlarm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDestination:
                selectDestination();
                break;
            case R.id.btnStartAlarm:
                startAlarm();
                break;
        }
    }

    private void selectDestination() {
        //TODO init and open map fragment code to choose destination
//        Snackbar.make(btnDestination, "Please select destination!", 1000).show();
        startActivity(new Intent(this, MapActivity.class));
    }

    private void startAlarm() {
        //TODO logic to start alarm
        Snackbar.make(btnDestination, "Please start alarm!", 1000).show();
    }
}
