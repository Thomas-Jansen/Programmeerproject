/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    ListView to show all Plants.
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

import java.util.ArrayList;

import thomas.jansen.plantbase.Adapters.PlantsListAdapter;
import thomas.jansen.plantbase.Classes.Plant;
import thomas.jansen.plantbase.Helpers.BottomNavigationViewHelper;
import thomas.jansen.plantbase.R;

public class SearchListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_search);

        Intent intent = getIntent();
        ArrayList<Plant> arrayListPlant = (ArrayList<Plant>) intent.getSerializableExtra("plantsList");

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(0).setChecked(true);

        ListView listViewPlants = findViewById(R.id.listViewPlants);
        PlantsListAdapter adapterPlants = new PlantsListAdapter(this, R.layout.item_plant_info, arrayListPlant);
        listViewPlants.setAdapter(adapterPlants);
        listViewPlants.setOnItemClickListener(new ListItemClickListener());
    }

    // Show clicked Plant.
    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Plant clickedPlant = (Plant) adapterView.getItemAtPosition(i);
            Intent intentPlant = new Intent(SearchListActivity.this, PlantInfoActivity.class);
            intentPlant.putExtra("clickedPlant", clickedPlant);
            startActivity(intentPlant);
        }
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(SearchListActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(SearchListActivity.this, MyPlantsListActivity.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(SearchListActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(SearchListActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
