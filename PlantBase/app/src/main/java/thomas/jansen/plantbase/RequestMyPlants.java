package thomas.jansen.plantbase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static thomas.jansen.plantbase.AccountActivity.mAuth;

public class RequestMyPlants {

    ArrayList<MyPlant> arrayListMyPlants = new ArrayList<>();

    Callback callback;
    public interface Callback {
        void gotMyPlantsArray(ArrayList<MyPlant> arrayListMyPlants);
        void gotError(DatabaseError error);
    }

    public void RequestMyPlants(Callback callback) {
        this.callback = callback;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference myRef = database.getReference("users").child(Objects.requireNonNull(mAuth.getUid()));
            myRef.orderByChild("alive").addValueEventListener(new myPlantListener());
        }

    }

    private class myPlantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                MyPlant myplant = child.getValue(MyPlant.class);
                if (myplant != null) {
                    arrayListMyPlants.add(myplant);
                }
            }
            callback.gotMyPlantsArray(arrayListMyPlants);
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
           callback.gotError(databaseError);
        }

    }
}
