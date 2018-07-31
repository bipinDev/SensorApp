package bipin.code.sensorapp;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.security.Permission;

public class SApplication extends Application {
    private final String TAG = SApplication.class.getSimpleName();
    public static Location LOCATION = null;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");
        init();
    }
    void init(){
        Intent location = new Intent(getApplicationContext(), LocationService.class);
        startService(location);
    }
}
