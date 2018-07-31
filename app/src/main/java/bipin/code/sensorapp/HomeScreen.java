package bipin.code.sensorapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import bipin.code.sensorapp.Bluetooth.MainActivity;

import static bipin.code.sensorapp.StaticConstants.ACCELEROMETER;
import static bipin.code.sensorapp.StaticConstants.DEVICE_ADDRESS;
import static bipin.code.sensorapp.StaticConstants.GPS;
import static bipin.code.sensorapp.StaticConstants.GYROSCOPE;
import static bipin.code.sensorapp.StaticConstants.LIGHT;
import static bipin.code.sensorapp.StaticConstants.MAGNETIC;
import static bipin.code.sensorapp.StaticConstants.PROXIMITY;

public class HomeScreen extends AppCompatActivity implements SensorEventListener,CheckInterface, View.OnClickListener {
    static public final int REQUEST_LOCATION = 1;
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    Sensor ACCELEROMETER_m,PROXIMITY_m,MAGNETIC_m,GYROSCOPE_m,LIGHT_m;
    SensorManager manager;
    GridView listView;
    List<String> names = new ArrayList<>();
    List<Sensor> sensors;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    Intent intent;
    private OutputStream outputStream;
    private InputStream inputStream;
    ToggleButton fgps,nmode;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Toast.makeText(getApplicationContext(), GPS + intent.getDoubleExtra("longitude", 0.0) + "," + intent.getDoubleExtra("latitude", 0.0) + "," + intent.getDoubleExtra("altitude", 0.0), Toast.LENGTH_LONG).show();
                SendData(GPS + intent.getDoubleExtra("longitude", 0.0) + "," + intent.getDoubleExtra("latitude", 0.0) + "," + intent.getDoubleExtra("altitude", 0.0) + "\n","0","0","0");
            }
        }
    };
    private CustomAdapter customAdapter;
    private boolean deviceConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
        fgps = findViewById(R.id.FGPS);
        nmode = findViewById(R.id.NMODE);
        listView = findViewById(R.id.list);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = manager.getSensorList(Sensor.TYPE_ALL);
        ACCELEROMETER_m = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        names.add("Accelerometer");
        PROXIMITY_m = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//        names.add("Proximity");
        MAGNETIC_m = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        names.add("Magnetic");
        GYROSCOPE_m = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        names.add("gyroscope");
        LIGHT_m = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        names.add("light");
        names.add("gps");
//        manager.registerListener(this, ACCELEROMETER_m, manager.SENSOR_DELAY_NORMAL);
//        manager.registerListener(this, PROXIMITY_m, manager.SENSOR_DELAY_NORMAL);
//        manager.registerListener(this, MAGNETIC_m, manager.SENSOR_DELAY_NORMAL);
//        manager.registerListener(this, GYROSCOPE_m, manager.SENSOR_DELAY_NORMAL);
//        manager.registerListener(this, LIGHT_m, manager.SENSOR_DELAY_NORMAL);
        customAdapter=new CustomAdapter(this, names,this);
        listView.setAdapter(customAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initBluetooth();
            }
        }, 2000);
        intent = new Intent(StaticConstants.BROADCAST_ACTION);

        fgps.setOnClickListener(this);
        nmode.setOnClickListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                SendData(ACCELEROMETER + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n",""+event.values[0],""+event.values[1],""+event.values[2]);
                break;
            case Sensor.TYPE_PROXIMITY:
                SendData(PROXIMITY + event.values[0] + "\n",""+event.values[0],"0","0");
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                SendData(MAGNETIC + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n",""+event.values[0],""+event.values[1],""+event.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                SendData(GYROSCOPE + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n",""+event.values[0],""+event.values[1],""+event.values[2]);
                break;
            case Sensor.TYPE_LIGHT:
                SendData(LIGHT + event.values[0] + "\n",""+event.values[0],"0","0");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(StaticConstants.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        manager.unregisterListener(this);
        try {
            if (outputStream != null) {
                outputStream.close();
                inputStream.close();
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        deviceConnected = false;
    }

    void initBluetooth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BTinit()) {
                    if (BTconnect()) {
//                        Toast.makeText(getApplicationContext(), "Connected to bluetooth", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.v("Bluetooth prep : ", "Bluetooth prepration finished");
            }
        }).start();
    }


    public boolean BTconnect() {
        boolean connected = true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }
        if (connected) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connected;
    }

    public boolean BTinit() {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    Log.d("Device Paired:", "Paired with : " + DEVICE_ADDRESS);
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    View v;
    public void SendData(String string,String value1,String value2,String value3) {
        string.concat("\n");
        if (v!=null) {
            TextView some = v.findViewById(R.id.values);
            some.setText(value1 + "," + value2 + "," + value3);
        }
            try {
                if (outputStream != null) {
                    outputStream.write(string.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void SendData(String string) {
        string.concat("\n");
        try {
            if (outputStream != null) {
                outputStream.write(string.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void checkChange(String value, Boolean bool) {
        if (value.equalsIgnoreCase("0")){
            v = listView.getChildAt(0);
            if (bool){
//                Toast.makeText(this,"accelerometer clicked",Toast.LENGTH_SHORT).show();
                manager.registerListener(this, ACCELEROMETER_m, manager.SENSOR_DELAY_NORMAL);
            }
            else {
//                Toast.makeText(this, "accelerometer unclicked", Toast.LENGTH_SHORT).show();
                manager.unregisterListener(this,ACCELEROMETER_m);
            }
        }
        if (value.equalsIgnoreCase("1")){
            v = listView.getChildAt(1);
            if (bool) {
//                Toast.makeText(this, "proximity clicked", Toast.LENGTH_SHORT).show();
                manager.registerListener(this, PROXIMITY_m, manager.SENSOR_DELAY_NORMAL);
            }
            else {
//                Toast.makeText(this, "proximity unclicked", Toast.LENGTH_SHORT).show();
                manager.unregisterListener(this,PROXIMITY_m);
            }
        }
        if (value.equalsIgnoreCase("2")){
            v = listView.getChildAt(2);
            if (bool) {
//                Toast.makeText(this, "magnetic clicked", Toast.LENGTH_SHORT).show();
                manager.registerListener(this, MAGNETIC_m, manager.SENSOR_DELAY_NORMAL);
            }
            else {
//                Toast.makeText(this, "magnetic unclicked", Toast.LENGTH_SHORT).show();
                manager.unregisterListener(this,MAGNETIC_m);
            }
        }
        if (value.equalsIgnoreCase("3")){
            v = listView.getChildAt(3);
            if (bool) {
//                Toast.makeText(this, "gyroscope clicked", Toast.LENGTH_SHORT).show();
                manager.registerListener(this, GYROSCOPE_m, manager.SENSOR_DELAY_NORMAL);
            }
            else {
//                Toast.makeText(this, "gyroscope unclicked", Toast.LENGTH_SHORT).show();
                manager.unregisterListener(this,GYROSCOPE_m);
            }
        }
        if (value.equalsIgnoreCase("4")){
            v = listView.getChildAt(4);
            if (bool) {
//                Toast.makeText(this, "light clicked", Toast.LENGTH_SHORT).show();
                manager.registerListener(this, LIGHT_m, manager.SENSOR_DELAY_NORMAL);
            }
            else {
//                Toast.makeText(this, "light unclicked", Toast.LENGTH_SHORT).show();
                manager.unregisterListener(this,LIGHT_m);
            }
        }
        if (value.equalsIgnoreCase("5")){
            v = listView.getChildAt(5);
            if (bool) {
//                Toast.makeText(this, "light clicked", Toast.LENGTH_SHORT).show();
                try {
                    Toast.makeText(this, "from clicking" + SApplication.LOCATION.getLongitude() + "," + SApplication.LOCATION.getLatitude() + "" + SApplication.LOCATION.getAltitude(), Toast.LENGTH_LONG).show();
                    intent.putExtra("longitude",SApplication.LOCATION.getLongitude());
                    intent.putExtra("latitude",SApplication.LOCATION.getLatitude());
                    intent.putExtra("altitude",SApplication.LOCATION.getAltitude());
//                    Log.d("longitude", String.valueOf(location.getLongitude()));
//                    Log.d("latitude", String.valueOf(location.getLatitude()));
//                    Log.d("altitude", String.valueOf(location.getAltitude())) ;
                    sendBroadcast(intent);
//                    SendData(GPS + SApplication.LOCATION.getLongitude() + "," + SApplication.LOCATION.getLatitude() + "," + SApplication.LOCATION.getAltitude(), SApplication.LOCATION.getLongitude() + "", "" + SApplication.LOCATION.getLatitude(), "" + SApplication.LOCATION.getAltitude());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
//                Toast.makeText(this, "light unclicked", Toast.LENGTH_SHORT).show();
//                manager.unregisterListener(this,LIGHT_m);
            }
        }
    }

    public void forward(View view) {
        SendData("F");
    }

    public void backward(View view) {
        SendData("B");
    }
    public void stop(View view) {

        SendData("S");
    }
    public void left(View view) {

        SendData("L");
    }
    public void right(View view) {

        SendData("R");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.FGPS:
                if (fgps.getText().equals("F-GPS On"))
                    SendData("U");
                else
                    SendData("u");
                break;
            case R.id.NMODE:
                if (nmode.getText().equals("N-Mode On"))
                    SendData("N");
                else
                    SendData("n");
                break;
        }
    }
}
