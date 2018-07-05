package com.btzh.rabbitmq.view.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Created by Lenovo on 2018/7/5.
 */

public class BaseActivity extends AppCompatActivity {
    protected Boolean agreedAllPermissions = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dynamicPermissions();
    }

    private void dynamicPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                //Manifest.permission.CAMERA,
                //Manifest.permission.SYSTEM_ALERT_WINDOW, //设置悬浮窗等
                //Manifest.permission.WRITE_SETTINGS
        )
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        agreedAllPermissions = true;
                    } else {
                        agreedAllPermissions = false;
                    }
                });
    }
}
