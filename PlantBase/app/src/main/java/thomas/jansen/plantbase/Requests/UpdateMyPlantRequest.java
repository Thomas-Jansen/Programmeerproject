/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Update or delete a MyPlant in the FireBase database, using startDate to find a match.
*/

package thomas.jansen.plantbase.Requests;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import thomas.jansen.plantbase.Classes.MyPlant;

import static android.widget.Toast.LENGTH_LONG;
import static thomas.jansen.plantbase.Activities.AccountActivity.mAuth;

public class UpdateMyPlantRequest {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private MyPlant myPlant;
    private Context context;

    public UpdateMyPlantRequest(MyPlant myPlant, Context context) {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        this.context = context;
        this.myPlant = myPlant;
        // query
        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference("users").child(Objects.requireNonNull(mAuth.getUid()));
        myRef.orderByChild("name").addListenerForSingleValueEvent(new myPlantListener());
    }

    // Find matching MyPlant and update value.
    private class myPlantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                MyPlant myplant = child.getValue(MyPlant.class);
                if (myplant != null) {
                    if (myplant.getStartdate().compareTo(myPlant.getStartdate()) == 0) {
                        myRef.child(child.getKey()).setValue(myPlant);
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(context, (CharSequence) databaseError, LENGTH_LONG).show();
        }
    }

    // Delete query.
    public void RemoveMyPlantActivity() {
        myRef.orderByChild("name").addListenerForSingleValueEvent(new myPlantRemoveListener());
    }


    // Delete matching MyPlant.
    private class myPlantRemoveListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                MyPlant myplant = child.getValue(MyPlant.class);
                if (myplant != null) {
                    if (myplant.getStartdate().compareTo(myPlant.getStartdate()) == 0) {
                        myRef.child(child.getKey()).setValue(null);
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(context, (CharSequence) databaseError, LENGTH_LONG).show();
        }
    }
}
