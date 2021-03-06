/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Start activity for app. Search plants, browse plants, or view MyPlants.
*/

package thomas.jansen.plantbase.Activities;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import thomas.jansen.plantbase.Notification.BackgroundTask;
import thomas.jansen.plantbase.Classes.Plant;
import thomas.jansen.plantbase.Helpers.BottomNavigationViewHelper;
import thomas.jansen.plantbase.R;
import thomas.jansen.plantbase.Requests.RequestPlants;

import static android.widget.Toast.LENGTH_LONG;
import static thomas.jansen.plantbase.Activities.AccountActivity.mAuth;

public class SearchActivity extends AppCompatActivity implements RequestPlants.Callback {

    ArrayList<Plant> arrayListPlants;
    Intent mBackgroundIntent;
    Service mBackgroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        if (mAuth.getCurrentUser() != null) {
            // Start BackgroundTask for notifications.
            mBackgroundService = new BackgroundTask(getApplicationContext());
            mBackgroundIntent = new Intent(getApplicationContext(), BackgroundTask.class);
            if (!isMyServiceRunning(mBackgroundService.getClass())) {
                startService(mBackgroundIntent);
            }
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(0).setChecked(true);

        ImageButton buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new searchOnClickListenerSearch());
        Button buttonBrowse = findViewById(R.id.buttonBrowse);
        buttonBrowse.setOnClickListener(new browseOnClickListener());

        Button buttonMyPlants = findViewById(R.id.buttonMyPlants);
        buttonMyPlants.setOnClickListener(new MPonClickListener());


        RequestPlants requestPlants =  new RequestPlants();
        requestPlants.RequestPlants(this);
    }

    // When BackgroundTask is already running, do nothing
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    // When app is closed, stop BackgroundTask so it can turn itself on again.
    @Override
    protected void onDestroy() {
        stopService(mBackgroundIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }

    @Override
    public void gotPlantsArray(ArrayList<Plant> arrayListPlant) {
        arrayListPlants = arrayListPlant;
    }

    @Override
    public void gotError(DatabaseError error) {
        Toast.makeText(SearchActivity.this, (CharSequence) error, LENGTH_LONG).show();
    }

    private class MPonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentMyPlants = new Intent(SearchActivity.this, MyPlantsListActivity.class);
            startActivity(intentMyPlants);
        }
    }

    // Search for queried plant
    private class searchOnClickListenerSearch implements View.OnClickListener {
        String searchString;
        @Override
        public void onClick(View view) {
            EditText editTextSearch = findViewById(R.id.editTextSearch);
            searchString = String.valueOf(editTextSearch.getText()).toLowerCase();
            if (!searchString.isEmpty()) {
                searchString = searchString.substring(0, 1).toUpperCase() + searchString.substring(1);
                for (Plant plant: arrayListPlants) {
                    if (plant.getName().equals(searchString)) {
                        Intent intentSearchList = new Intent(SearchActivity.this, PlantInfoActivity.class);
                        intentSearchList.putExtra("searchedPlant", plant);
                        startActivity(intentSearchList);
                        return;
                    }
                }
                Toast.makeText(SearchActivity.this, "Couldn't find the plant you're looking for", LENGTH_LONG).show();
            }
        }
    }

    private class browseOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentSearchList = new Intent(SearchActivity.this, SearchListActivity.class);
            intentSearchList.putExtra("plantsList", arrayListPlants);
            startActivity(intentSearchList);
        }
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(SearchActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(SearchActivity.this, MyPlantsListActivity.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(SearchActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(SearchActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
