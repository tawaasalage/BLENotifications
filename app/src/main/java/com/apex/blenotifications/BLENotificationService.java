package com.apex.blenotifications;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import com.github.pwittchen.reactivebeacons.library.Beacon;
import com.github.pwittchen.reactivebeacons.library.Filter;
import com.github.pwittchen.reactivebeacons.library.Proximity;
import com.github.pwittchen.reactivebeacons.library.ReactiveBeacons;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleDeviceServices;
import com.polidea.rxandroidble.RxBleScanResult;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BLENotificationService extends Service {
    Handler handler=new Handler();

    private BluetoothAdapter mBluetoothAdapter = null;
    private Subscription subscription;

    RxBleClient rxBleClient;
    //private ReactiveBeacons reactiveBeacons;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        rxBleClient = RxBleClient.create(this);
        //reactiveBeacons = new ReactiveBeacons(this);
        handler.postDelayed(runnable,5000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();

    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);

            checkBluetoothStatus();

            handler.postDelayed(runnable,10000);
        }
    };


    public void scanBLE()
    {
        unsubscribe(subscription);
        subscription=rxBleClient.scanBleDevices()
                .subscribe(new Observer<RxBleScanResult>() {
                    @Override
                    public void onCompleted()
                    {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("BLEResult",e.toString());
                    }

                    @Override
                    public void onNext(RxBleScanResult rxBleScanResult)
                    {
                        RxBleDevice dleD=rxBleScanResult.getBleDevice();

                        Log.d("BeaconInfoFinal", getGuidFromByteArray(rxBleScanResult.getScanRecord())+" "+dleD.getMacAddress());


                    }
                });
    }

    /*
    public void scanBLE()
    {
        if (!reactiveBeacons.isBleSupported()) {
            return;
        }

        unsubscribe(subscription);
        subscription = reactiveBeacons.observe()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(Filter.proximityIsEqualTo(Proximity.NEAR,Proximity.IMMEDIATE))
                .subscribe(new Action1<Beacon>() {
                    @Override
                    public void call(Beacon beacon) {
                        Log.d("BeaconInfoFinal", getGuidFromByteArray(beacon.scanRecord));
                        Log.d("BeaconInfoFinal", getHBGuidFromByteArray(beacon.scanRecord));

                    }
                });
    }
    */

    public static String getGuidFromByteArray(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        return uuid.toString();
    }

    private static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    public void checkBluetoothStatus()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d("BLEResult","Bluetooth is OFF");
        }
        else
        {
            Log.d("BLEResult","Called");
            scanBLE();
        }
    }
}
