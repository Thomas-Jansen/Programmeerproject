package thomas.jansen.plantbase;

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

import static android.widget.Toast.LENGTH_LONG;
import static thomas.jansen.plantbase.AccountActivity.mAuth;

public class SearchActivity extends AppCompatActivity implements RequestPlants.Callback{

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
        buttonSearch.setOnClickListener(new searchonClickListenerSearch());
        Button buttonBrowse = findViewById(R.id.buttonBrowse);
        buttonBrowse.setOnClickListener(new browseonClickListener());

        Button buttonMyPlants = findViewById(R.id.buttonMyPlants);
        buttonMyPlants.setOnClickListener(new MPonClickListener());


        RequestPlants requestPlants =  new RequestPlants();
        requestPlants.RequestPlants(this);
    }

    public Context getContext() {
        Context context = SearchActivity.this;
        return context;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


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
            Intent intentMyPlants = new Intent(SearchActivity.this, MyPlantsList.class);
            startActivity(intentMyPlants);
        }
    }

    private class searchonClickListenerSearch implements View.OnClickListener {
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

    private class browseonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentSearchList = new Intent(SearchActivity.this, SearchList.class);
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
                    Intent intentMyPlants = new Intent(SearchActivity.this, MyPlantsList.class);
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
