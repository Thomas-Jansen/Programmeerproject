package thomas.jansen.plantbase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import static thomas.jansen.plantbase.AccountActivity.mAuth;

public class PlantInfoActivity extends AppCompatActivity {

    Plant currentPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantinfo);

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(0).setChecked(true);

        Intent intent = getIntent();
        currentPlant = (Plant) intent.getSerializableExtra("searchedPlant");
        if (currentPlant != null) {
            setTexts(currentPlant);
        } else {
            currentPlant = (Plant) intent.getSerializableExtra("clickedPlant");
            setTexts(currentPlant);
        }

        Button addToCollection = findViewById(R.id.buttonAddCollection);
        addToCollection.setOnClickListener(new addToCollectionOnClickListener());
    }

    @SuppressLint("SetTextI18n")
    private  void  setTexts(Plant plant) {
        TextView nameView = findViewById(R.id.textViewPlantName);
        TextView nameLatinView = findViewById(R.id.textViewPlantNameLatin);
        TextView minPhView = findViewById(R.id.textViewMinPh);
        TextView maxPhView = findViewById(R.id.textViewMaxPh);
        TextView minTempView = findViewById(R.id.textViewMinTemp);
        TextView maxTempView = findViewById(R.id.textViewMaxTemp);
        TextView waterView = findViewById(R.id.textViewWater);
        ImageView imageViewPlant = findViewById(R.id.imageViewPlant);

        String name = plant.getName();
        String nameLatin = plant.getLatinName();
        Float minPh = plant.getMinpH();
        Float maxPh = plant.getMaxpH();
        int minTemp = (int) plant.getMinTemp();
        int maxTemp = (int) plant.getMaxTemp();
        int water = (int) plant.getWatering();
        String imageName= plant.getImageID();
        int imageId = getResources().getIdentifier(imageName , "drawable", getPackageName());

        nameView.setText(name);
        nameLatinView.setText(nameLatin);
        minPhView.setText("Min pH: "+minPh);
        maxPhView.setText("Max pH: "+maxPh);
        minTempView.setText("Min Temp: "+minTemp+" \u2103");
        maxTempView.setText("Max Temp: "+maxTemp+" \u2103");
        waterView.setText("Watering level: "+water);
        imageViewPlant.setImageResource(imageId);
    }

    private class addToCollectionOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() == null) {
                Intent intentLogin = new Intent(PlantInfoActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                return;
            }

            MyPlant newMyPlant =  createMyPlant(currentPlant);
            Intent intentPlant = new Intent(PlantInfoActivity.this, MyPLantActivity.class);
            intentPlant.putExtra("MyPlant", newMyPlant);
            startActivity(intentPlant);
        }
    }

    private MyPlant createMyPlant(Plant plant) {

        MyPlant newMyPlant = new MyPlant();
        newMyPlant.setName(plant.getName());
        newMyPlant.setLatinName(plant.getLatinName());
        newMyPlant.setMinpH(plant.getMinpH());
        newMyPlant.setMaxpH(plant.getMaxpH());
        newMyPlant.setMinTemp(plant.getMinTemp());
        newMyPlant.setMaxTemp(plant.getMaxTemp());
        newMyPlant.setWatering(plant.getWatering());
        newMyPlant.setAlive(true);
        newMyPlant.setConnected(false);
        newMyPlant.setArduinoName(null);
        newMyPlant.setStartdate(Calendar.getInstance().getTimeInMillis());
        newMyPlant.setWaternotify(0);
        newMyPlant.setStatus("OK");
        newMyPlant.setImageID(plant.getImageID());
        newMyPlant.setArduinoName("none");
        newMyPlant.setAvatarImage("none");
        newMyPlant.setLastwatered(Calendar.getInstance().getTimeInMillis());

        ArrayList<String> addedImages = new ArrayList<>();
        Uri imageUri = Uri.parse("android.resource://thomas.jansen.plantbase/drawable/" +plant.getName().toLowerCase()+"_stock");
        addedImages.add(String.valueOf(imageUri));
        newMyPlant.setAddedImages(addedImages);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(mAuth.getUid()).push().setValue(newMyPlant);

        return  newMyPlant;
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(PlantInfoActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(PlantInfoActivity.this, MyPlantsList.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(PlantInfoActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(PlantInfoActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
