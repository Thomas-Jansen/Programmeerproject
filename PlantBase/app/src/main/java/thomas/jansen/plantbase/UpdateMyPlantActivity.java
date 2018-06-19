package thomas.jansen.plantbase;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;
import static thomas.jansen.plantbase.AccountActivity.mAuth;

public class UpdateMyPlantActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private MyPlant myPlant;
    private Context context;

    public UpdateMyPlantActivity(MyPlant myPlant, Context context) {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        this.context = context;
        this.myPlant = myPlant;
        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference("users").child(Objects.requireNonNull(mAuth.getUid()));
        myRef.orderByChild("name").addListenerForSingleValueEvent(new myPlantListener());
    }

    private class myPlantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                MyPlant myplant = child.getValue(MyPlant.class);
                if (myplant != null) {
                    System.out.println(myplant.getName());
                    if (myplant.getStartdate().compareTo(myPlant.getStartdate()) == 0) {
                        myRef.child(child.getKey()).setValue(myPlant);
                        System.out.println("geupdatet");
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(context, (CharSequence) databaseError, LENGTH_LONG).show();
        }
    }

    public void RemoveMyPlantActivity() {
        myRef.orderByChild("name").addListenerForSingleValueEvent(new myPlantRemoveListener());
    }


    private class myPlantRemoveListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                MyPlant myplant = child.getValue(MyPlant.class);
                if (myplant != null) {
                    System.out.println(myplant.getName());
                    if (myplant.getStartdate().compareTo(myPlant.getStartdate()) == 0) {
                        myRef.child(child.getKey()).setValue(null);
                        System.out.println("Deleted");



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
