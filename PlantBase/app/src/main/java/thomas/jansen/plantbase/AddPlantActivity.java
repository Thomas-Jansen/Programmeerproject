package thomas.jansen.plantbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.LENGTH_LONG;

public class AddPlantActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplant);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(0).setChecked(true);

        Button buttonAddPlant = findViewById(R.id.buttonAddPlant);
        buttonAddPlant.setOnClickListener(new addPlantonClickListener());

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private class addPlantonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            EditText nameText = findViewById(R.id.editTextName);
            EditText latinNameText = findViewById(R.id.editTextLatinName);
            EditText maxTempText = findViewById(R.id.editTextMaxTemp);
            EditText minTempText = findViewById(R.id.editTextMinTemp);
            EditText maxpHText = findViewById(R.id.editTextMaxpH);
            EditText minpHText = findViewById(R.id.editTextMinpH);
            EditText wateringText = findViewById(R.id.editTextWatering);

            String name = String.valueOf(nameText.getText());
            String nameLatin = String.valueOf(latinNameText.getText());
            int maxTemp = Integer.parseInt(maxTempText.getText().toString());
            int minTemp = Integer.parseInt(minTempText.getText().toString());
            float maxpH = Float.parseFloat(maxpHText.getText().toString());
            float minpH = Float.parseFloat(minpHText.getText().toString());
            int watering = Integer.parseInt(wateringText.getText().toString());

            Plant newPlant = new Plant(name, nameLatin, maxTemp, maxpH, minTemp, minpH, watering);

            System.out.println("Plant gemaak");

            mDatabase.child("plantsdata").push().setValue(newPlant);

            Toast.makeText(AddPlantActivity.this, "Plant added!", LENGTH_LONG).show();

            nameText.setText("");
            latinNameText.setText("");
            maxTempText.setText("");
            minTempText.setText("");
            maxpHText.setText("");
            minpHText.setText("");
            wateringText.setText("");
        }
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(AddPlantActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(AddPlantActivity.this, MyPlantsList.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(AddPlantActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(AddPlantActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
