/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Request data from connected PlantNode.
*/

package thomas.jansen.plantbase.Requests;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;

import thomas.jansen.plantbase.Classes.MyPlant;

public class RequestPlantNode {


    private Callback callback;
    private DataPoint[] dataPointsTemp;
    private DataPoint[] dataPointsHum;
    private DataPoint[] dataPointsMoist;
    private DataPoint[] dataPointsLight;

    private int[] lastData;
    private MyPlant myPlant;

    public interface Callback {
        void gotPlantNodeData(DataPoint[] dataPointsTemp, DataPoint[] dataPointsHum, DataPoint[] dataPointsMoist, DataPoint[] dataPointsLight);
        void gotError(DatabaseError error);
        void gotLastPlantNodeData(int[] lastData, MyPlant myPlant);
    }


    // Request last 1000 data points.
    public void RequestPlantNodeData(Callback callback, String nodeName) {

        this.callback = callback;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("plantnode_data").child(nodeName);
        myRef.limitToLast(1000).addValueEventListener(new myNodeListener());
    }

    // Request last data points
    public void RequestLastNodeData(Callback callback, String nodeName, MyPlant myPlant) {

        this.callback = callback;
        this.myPlant = myPlant;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("plantnode_data").child(nodeName);
        myRef.limitToLast(1).addValueEventListener(new myLastNodeListener());
    }

    // Create data points.
    private class myNodeListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            // Get a number of data points.
            int dataSnapshotSize = 0;
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                dataSnapshotSize++;
            }

            int count = 0;

            dataPointsTemp = new DataPoint[dataSnapshotSize];
            dataPointsHum = new DataPoint[dataSnapshotSize];
            dataPointsMoist = new DataPoint[dataSnapshotSize];
            dataPointsLight = new DataPoint[dataSnapshotSize];

            for (DataSnapshot child : dataSnapshot.getChildren()) {
                int temp = Integer.parseInt((String) child.child("Temp").getValue());
                int humidity = Integer.parseInt((String) child.child("Humidity").getValue());
                int light = Integer.parseInt((String) child.child("Light").getValue());
                int moisture = Integer.parseInt((String) child.child("SoilMoisture").getValue());

                dataPointsTemp[count] = new DataPoint(count, temp);
                dataPointsHum[count] = new DataPoint(count, humidity);
                dataPointsMoist[count] = new DataPoint(count, light);
                dataPointsLight[count] = new DataPoint(count, moisture);
                count++;
            }
            callback.gotPlantNodeData(dataPointsTemp, dataPointsHum, dataPointsMoist, dataPointsLight);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.gotError(databaseError);
        }
    }

    // Create data points from last PlantNode data.
    private class myLastNodeListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            lastData = new int[4];
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                lastData[0] = Integer.parseInt((String) child.child("Temp").getValue());
                lastData[1] = Integer.parseInt((String) child.child("Humidity").getValue());
                lastData[2] = Integer.parseInt((String) child.child("Light").getValue());
                lastData[3] = Integer.parseInt((String) child.child("SoilMoisture").getValue());
            }
            callback.gotLastPlantNodeData(lastData, myPlant);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.gotError(databaseError);
        }
    }
}
