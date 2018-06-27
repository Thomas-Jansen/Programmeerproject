/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Performs background calculations and requests a notification whenever a plant needs water.
    From: https://fabcirablog.weebly.com/blog/creating-a-never-ending-background-service-in-android
*/

package thomas.jansen.plantbase.Notification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import thomas.jansen.plantbase.Classes.MyPlant;
import thomas.jansen.plantbase.Requests.RequestMyPlants;
import thomas.jansen.plantbase.Requests.RequestPlantNode;
import thomas.jansen.plantbase.Requests.UpdateMyPlantRequest;

import static thomas.jansen.plantbase.Activities.AccountActivity.mAuth;

public class BackgroundTask extends Service implements RequestMyPlants.Callback, RequestPlantNode.Callback {

    static Context context;
    ArrayList<String> plantNames = new ArrayList<>();
    int SECOND = 1000;
    int HOUR = 3600000;
    int DAY = 86400000;
    int TWO_DAYS = 172800000;
    int WEEK = 604800000;


    public BackgroundTask() {}

    public BackgroundTask(Context context) {
        this.context = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    // Restarts itself whenever the app is closed.
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();

        // schedule the timer, to wake up every hour
        timer.schedule(timerTask, SECOND,  HOUR);
    }

    // Request notification.
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() { getWaterneedingPlants();
            if (!plantNames.isEmpty()) {
                if (context != null) {
                    NotificationClass notificationClass = new  NotificationClass(context, plantNames);
                    notificationClass.createNotificationChannel();
                    plantNames = new ArrayList<>();
                }

            }
            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getWaterneedingPlants() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        new RequestMyPlants().RequestMyPlants(BackgroundTask.this);
    }

    // Check whether a plant needs water, using last watering date and notification settings.
    @Override
    public void gotMyPlantsArray(ArrayList<MyPlant> arrayListMyPlants) {

        Long now = Calendar.getInstance().getTimeInMillis();
        plantNames.clear();

        for (int x = 0; x < arrayListMyPlants.size(); x++) {
            MyPlant myPlant = arrayListMyPlants.get(x);
            switch (myPlant.getWaternotify()) {
                case (0): {
                    continue;
                }
                case (1): {
                    if (( now - myPlant.getLastwatered()) > DAY) {
                        plantNames.add(myPlant.getName());
                        myPlant.setStatus("Water!");
                        new UpdateMyPlantRequest(myPlant, context);
                    }
                }
                case (2): {
                    if (( now - myPlant.getLastwatered()) > TWO_DAYS) {
                        plantNames.add(myPlant.getName());
                        myPlant.setStatus("Water!");
                        new UpdateMyPlantRequest(myPlant, context);
                    }
                }
                case (3): {
                    if (( now -  myPlant.getLastwatered()) > WEEK) {
                        plantNames.add(myPlant.getName());
                        myPlant.setStatus("Water!");
                        new UpdateMyPlantRequest(myPlant, context);
                    }
                }
                case (4): {
                    if (( now -  myPlant.getLastwatered()) > DAY) {
                        new RequestPlantNode().RequestLastNodeData(BackgroundTask.this, myPlant.getArduinoName(), myPlant);
                    }

                }
            }
        }
    }

    @Override
    public void gotPlantNodeData(DataPoint[] dataPointsTemp, DataPoint[] dataPointsHum, DataPoint[] dataPointsMoist, DataPoint[] dataPointsLight) {}

    @Override
    public void gotError(DatabaseError error) {}

    // Check whether a plant needs water, using PlantNode data and plant specific watering needs.
    @Override
    public void gotLastPlantNodeData(int[] lastData, MyPlant myPlant) {

        int waterData = lastData[3];
        switch ((int) myPlant.getWatering()) {
            case (1): {
                if (waterData < 10) {
                    plantNames.add(myPlant.getName());
                    myPlant.setStatus("Water!");
                    new UpdateMyPlantRequest(myPlant, context);
                }
            }
            case (2): {
                if (waterData < 20) {
                    plantNames.add(myPlant.getName());
                    myPlant.setStatus("Water!");
                    new UpdateMyPlantRequest(myPlant, context);
                }
            }
            case (3): {
                if (waterData < 40) {
                    plantNames.add(myPlant.getName());
                    myPlant.setStatus("Water!");
                    new UpdateMyPlantRequest(myPlant, context);
                }
            }
            case (4): {
                if (waterData < 50) {
                    plantNames.add(myPlant.getName());
                    myPlant.setStatus("Water!");
                    new UpdateMyPlantRequest(myPlant, context);
                }
            }
            case (5): {
                if (waterData < 60) {
                    plantNames.add(myPlant.getName());
                    myPlant.setStatus("Water!");
                    new UpdateMyPlantRequest(myPlant, context);
                }
            }
        }
    }
}

