/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Shows a list with all MyPlants.
*/

package thomas.jansen.plantbase.Activities;

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
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.Collections;

import thomas.jansen.plantbase.Adapters.MyPlantsListAdapter;
import thomas.jansen.plantbase.Classes.MyPlant;
import thomas.jansen.plantbase.Helpers.BottomNavigationViewHelper;
import thomas.jansen.plantbase.R;
import thomas.jansen.plantbase.Requests.RequestMyPlants;

import static thomas.jansen.plantbase.Activities.AccountActivity.mAuth;

public class MyPlantsListActivity extends AppCompatActivity implements RequestMyPlants.Callback{

    ArrayList<MyPlant> arrayListMyPlants;
    ListView listViewMyPlants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_myplants);


        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth.getCurrentUser() == null) {
            Intent intentLogin = new Intent(MyPlantsListActivity.this, LoginActivity.class);
            startActivity(intentLogin);
            return;
        }

        arrayListMyPlants = new ArrayList<>();
        listViewMyPlants = findViewById(R.id.listViewMyPlants);
        listViewMyPlants.setAdapter(null);

        RequestMyPlants requestMyPlants = new RequestMyPlants();
        requestMyPlants.RequestMyPlants(this);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(1).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth.getCurrentUser() == null) {
            Intent intentLogin = new Intent(MyPlantsListActivity.this, LoginActivity.class);
            startActivity(intentLogin);
            return;
        }

        arrayListMyPlants = new ArrayList<>();
        listViewMyPlants = findViewById(R.id.listViewMyPlants);
        listViewMyPlants.setAdapter(null);


        RequestMyPlants requestMyPlants = new RequestMyPlants();
        requestMyPlants.RequestMyPlants(this);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(1).setChecked(true);
    }

    // Set adapter on listView
    @Override
    public void gotMyPlantsArray(ArrayList<MyPlant> arrayListMyPlants) {
        this.arrayListMyPlants = arrayListMyPlants;
        Collections.reverse(arrayListMyPlants);
        MyPlantsListAdapter adapterMyPlants = new MyPlantsListAdapter(this, R.layout.item_my_plant, arrayListMyPlants);
        listViewMyPlants.setAdapter(adapterMyPlants);
        listViewMyPlants.setOnItemClickListener(new ListItemClickListener());
    }

    @Override
    public void gotError(DatabaseError error) {
        String errorString = error.toString();
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            MyPlant clickedMyPlant = (MyPlant) adapterView.getItemAtPosition(i);
            Intent intentMyPlant = new Intent(MyPlantsListActivity.this, MyPlantActivity.class);
            intentMyPlant.putExtra("clickedMyPlant", clickedMyPlant);
            startActivity(intentMyPlant);
        }
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(MyPlantsListActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(MyPlantsListActivity.this, MyPlantsListActivity.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(MyPlantsListActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(MyPlantsListActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
