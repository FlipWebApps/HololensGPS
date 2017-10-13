package com.example.mhew.hololensbluetoothgps;

import android.Manifest;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    MapView mapView;

    BluetoothAdapter mBAdapter;
    BluetoothManager mBManager;
    BluetoothLeAdvertiser mBLEAdvertiser;
    static final int BEACON_ID = 1775;

    LocationManager mLocationManager;
    static final int PERMISSION_RESULT_CODE = 1;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup UI References
        textView = (TextView) findViewById(R.id.textView);
        mapView = (MapView) findViewById(R.id.mapView);

        mBManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBAdapter = mBManager.getAdapter();
        mBLEAdvertiser = mBAdapter.getBluetoothLeAdvertiser();

        int permissionCheck = ContextCompat.checkSelfPermission((Context)this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startGPS();
        }
        else
        {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_RESULT_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBAdapter == null || !mBAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE support on this device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mBAdapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "No advertising support on this device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        startAdvertising();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAdvertising();
    }

    private void startAdvertising() {
        if (mBLEAdvertiser == null) return;
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(false)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();
        AdvertiseData data = new AdvertiseData.Builder()
                .addManufacturerData(BEACON_ID, buildGPSPacket())
                .build();
        mBLEAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    private void stopAdvertising() {
        if (mBLEAdvertiser == null) return;
        mBLEAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private void restartAdvertising() {
        stopAdvertising();
        startAdvertising();
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            String msg = "Service Running";
            mHandler.sendMessage(Message.obtain(null, 0, msg));
        }

        @Override
        public void onStartFailure(int errorCode) {
            if (errorCode != ADVERTISE_FAILED_ALREADY_STARTED) {
                String msg = "Service failed to start: " + errorCode;
                mHandler.sendMessage(Message.obtain(null, 0, msg));
            } else {
                restartAdvertising();
            }
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /*
            UI feedback to the user would go here.
            */
            Toast.makeText(MainActivity.this, (CharSequence)msg.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public void startGPS()
    {
        //Execute location service call if user has explicitly granted ACCESS_FINE_LOCATION..
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                UpdatePosition(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        try
        {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.i("GPS_Receiver", "startGPS: GPS Started..");
        }
        catch(SecurityException e)
        {
        }
    }
    public void UpdatePosition(Location location)
    {
        currentLocation = location;
        textView.setText(Double.toString(currentLocation.getLatitude()) + "," + Double.toString(currentLocation.getLongitude()));
        restartAdvertising();
    }

    private byte[] buildGPSPacket()
    {
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        byte[] packet = new byte[24];
        if(location != null) {
            try {
                double latitude = location.getLatitude();
                byte[] buffer = ByteBuffer.allocate(8).putDouble(latitude).array();
                for (int i = 0, j =7; i < 8; i++, j--) packet[i] = buffer[j];
                double longitude = location.getLongitude();
                buffer = ByteBuffer.allocate(8).putDouble(longitude).array();
                for (int i = 8, j =7; i < 16; i++, j--) packet[i] = buffer[j];
                float bearing = 0;
                bearing = location.getBearing();
                buffer = ByteBuffer.allocate(4).putFloat(bearing).array();
                for (int i = 16, j =3; i < 20; i++, j--) packet[i] = buffer[j];
                float speed = 0;
                speed = location.getSpeed();
                buffer = ByteBuffer.allocate(4).putFloat(speed).array();
                for (int i = 20, j =3; i < 24; i++, j--) packet[i] = buffer[j];
            } catch (NumberFormatException e) {
                packet = new byte[24];
            }
        }
        return packet;
    }
}
