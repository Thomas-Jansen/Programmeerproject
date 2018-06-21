package thomas.jansen.plantbase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestPlants {

    ArrayList<Plant> arrayListPlants = new ArrayList<>();

    RequestPlants.Callback callback;
    public interface Callback {
        void gotPlantsArray(ArrayList<Plant> arrayListPlants);
        void gotError(DatabaseError error);
    }

    public void RequestPlants(RequestPlants.Callback callback) {
        this.callback = callback;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("plantsdata");
        myRef.orderByChild("name").addValueEventListener(new plantListener());
    }

    private class plantListener implements ValueEventListener {


        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Plant plant = child.getValue(Plant.class);
                if (plant != null) {
                    System.out.println(plant.getName());
                    arrayListPlants.add(plant);
                }
            }
            callback.gotPlantsArray(arrayListPlants);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.gotError(databaseError);
        }

    }
}
