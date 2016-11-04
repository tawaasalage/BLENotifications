package com.apex.blenotifications;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.jakewharton.rxbinding.view.RxView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class Home extends AppCompatActivity
{
    @Bind(R.id.btnStartService)
    Button btnStartService;

    @Bind(R.id.btnStopService)
    Button btnStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        RxView.clicks(btnStartService)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startService(new Intent(Home.this, BLENotificationService.class));
                    }
                });

        RxView.clicks(btnStopService)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        stopService(new Intent(Home.this, BLENotificationService.class));
                    }
                });
    }
}
