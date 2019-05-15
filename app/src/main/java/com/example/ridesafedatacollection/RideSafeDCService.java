package com.example.ridesafedatacollection;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.math.BigDecimal;
import static com.example.ridesafedatacollection.notificationConfig.CHANNEL_ID;

public class RideSafeDCService extends Service implements  SensorEventListener, GPSUpdate {


    private Sensor myAccelerometer, myGyroscope;
    private SensorManager SM;
    Notification notification;
    DatabaseHelper mDatabaseHelper;
    int sensorType;
    private GPSConfig gpsManager = null;
    private double speed = 0.0;
    LocationManager locationManager;
    boolean SpeedAquired, GAquired, RAquired;
    double GYROX, GYROY, GYROZ, ACCELEROMETER, SPEEDPREV, SPEEDCURR, SPEEDDIFF, currentSpeed, X, Y, Z;
    double nullNum = 9999;




    @Override
    public void onCreate() {
        super.onCreate();
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        myAccelerometer = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        myGyroscope = SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mDatabaseHelper = new DatabaseHelper(this);
        SpeedAquired = false;
        GAquired = false;
        RAquired = false;
        SM.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(this, myGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSConfig(RideSafeDCService.this);
        gpsManager.startListening(getApplicationContext());
        gpsManager.setGPSCallback(this);
        SPEEDPREV = 0.0;

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        Intent notificationIntent = new Intent(this, main_Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("RideSafe")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.logo2use)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        return START_NOT_STICKY;
    }


    ///  Accelerometer && Gyroscope --------------------------------------------------------------------------------

    @Override
    public void onSensorChanged(SensorEvent event) {

        sensorType = event.sensor.getType();
        if (sensorType == 1 && !GAquired) {     // accelerometer

            X = event.values[0];
            Y = event.values[1];
            Z = event.values[2];
            ACCELEROMETER = Math.sqrt(X * X + Y * Y + Z * Z) - 9.807;
            ACCELEROMETER = (double)Math.round(ACCELEROMETER * 100d)/100d;
            GAquired = true;

            if(RAquired) {
                GAquired = false;
                RAquired = false;
               AddData();
            }
        } else if (sensorType != 1 && !RAquired) {

            GYROX = (double)Math.round(event.values[0] * 100d)/100d;
            GYROY = (double)Math.round(event.values[1] * 100d)/100d;
            GYROZ = (double)Math.round(event.values[2] * 100d)/100d;

            RAquired = true;
            if(GAquired){
                GAquired = false;
                RAquired = false;
                AddData();
            }

        }
    }

    @Override  // sensors -- unused
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


//------------------------------------------------------------------------------------------------------------

    // service
    @Override
    public void onDestroy() {
        super.onDestroy();
        SM.unregisterListener(this, myAccelerometer);
        SM.unregisterListener(this, myGyroscope);
        gpsManager.stopListening();
        gpsManager.setGPSCallback(null);
        gpsManager = null;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent Intent) {
        return null;
    }


    //-------------------------------------------------------------------------------------------


//----------------------------------------------------------------------------------------------------


    // location

    public void getCurrentSpeed() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsManager = new GPSConfig(RideSafeDCService.this);
        gpsManager.startListening(getApplicationContext());
        gpsManager.setGPSCallback(this);


    }

    @Override
    public void onGPSUpdate(Location location) {

        speed = location.getSpeed();
        currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP);
        SPEEDCURR = round((currentSpeed * 3.6), 3, BigDecimal.ROUND_HALF_UP);
    }


    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }


    public void AddData() {

        if (ACCELEROMETER != nullNum && GYROX != nullNum && GYROY != nullNum && GYROZ != nullNum && SPEEDCURR != nullNum) {
           // SPEEDDIFF = diff(SPEEDCURR,SPEEDPREV);

            double averageGyro = (GYROX + GYROY + GYROZ)/ 3;
            averageGyro = (double)Math.round(averageGyro * 1d)/1d;


            ContentValues values = new ContentValues();
            values.put("gforce",ACCELEROMETER);
            values.put("gx", GYROX);
            values.put("gy", GYROY);
            values.put("gz", GYROZ);
            values.put("speed", SPEEDCURR);

            mDatabaseHelper.addRow(values, "sensor_values");
            ACCELEROMETER = nullNum;
            GYROX = nullNum;
            GYROY = nullNum;
            GYROZ = nullNum;
            SPEEDPREV = SPEEDCURR;
            SPEEDDIFF = nullNum;
        }


    }


    public double diff(double current, double previous){
        double difference = -99;

        if (current == previous) difference = 0.0;

        else if (current > previous) difference = current-previous;

        else if (current < previous){
            difference = previous - current;
            difference = difference * -1;

        }

        return difference;

    }


}














