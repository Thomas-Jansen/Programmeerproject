package thomas.jansen.plantbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;

public class MyPlantsList extends AppCompatActivity {

    ArrayList<MyPlant> arrayListMyPlants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_myplants);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intentLogin = new Intent(MyPlantsList.this, LoginActivity.class);
            startActivity(intentLogin);
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(Objects.requireNonNull(mAuth.getUid()));
        myRef.orderByChild("name").addValueEventListener(new myPlantListener());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(1).setChecked(true);
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            MyPlant clickedMyPlant = (MyPlant) adapterView.getItemAtPosition(i);
            Intent intentMyPlant = new Intent(MyPlantsList.this, MyPLantActivity.class);
            intentMyPlant.putExtra("clickedMyPlant", clickedMyPlant);
            startActivity(intentMyPlant);
        }
    }

    private void gotMyPlantsArraylist() {
        ListView listViewMyPlants = findViewById(R.id.listViewMyPlants);
        myPlantsListAdapter adapterMyPlants = new myPlantsListAdapter(this, R.layout.item_my_plant, arrayListMyPlants);
        listViewMyPlants.setAdapter(adapterMyPlants);
        listViewMyPlants.setOnItemClickListener(new ListItemClickListener());
    }

    private class myPlantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                MyPlant myplant = child.getValue(MyPlant.class);
                if (myplant != null) {
                    System.out.println(myplant.getName());
                    arrayListMyPlants.add(myplant);
                }
            }
            gotMyPlantsArraylist();
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(MyPlantsList.this, (CharSequence) databaseError, LENGTH_LONG).show();
        }
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(MyPlantsList.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(MyPlantsList.this, MyPlantsList.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(MyPlantsList.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(MyPlantsList.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
