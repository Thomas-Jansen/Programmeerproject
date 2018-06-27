/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Helper class for backgroundTask.
*/

package thomas.jansen.plantbase.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, BackgroundTask.class));;
    }
}