package thomas.jansen.plantbase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class MyPLantActivity extends AppCompatActivity {

    TabHost tabHost;
    CheckBox checkBoxNever;
    CheckBox checkBoxArduino;
    CheckBox checkBoxDaily;
    CheckBox checkBoxTwoDays;
    CheckBox checkBoxWeekly;
    MyPlant myPlant;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplant);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intentLogin = new Intent(MyPLantActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(Objects.requireNonNull(mAuth.getUid()));

        checkBoxNever = findViewById(R.id.checkBoxNever);
        checkBoxArduino = findViewById(R.id.checkBoxArduino);
        checkBoxDaily = findViewById(R.id.checkBoxDaily);
        checkBoxTwoDays = findViewById(R.id.checkBoxTwoDays);
        checkBoxWeekly = findViewById(R.id.checkBoxWeekly);

        Intent intent = getIntent();
        myPlant = (MyPlant) intent.getSerializableExtra("clickedMyPlant");
        if (myPlant == null) {
            myPlant = (MyPlant) intent.getSerializableExtra("MyPlant");
        }
        setViews(myPlant);

        TabHost host = findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Status");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Status");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Watering");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Watering");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Data");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Data");
        host.addTab(spec);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(1).setChecked(true);

        checkBoxNever.setOnCheckedChangeListener(new checkChangedListener());
        checkBoxArduino.setOnCheckedChangeListener(new checkChangedListener());
        checkBoxDaily.setOnCheckedChangeListener(new checkChangedListener());
        checkBoxTwoDays.setOnCheckedChangeListener(new checkChangedListener());
        checkBoxWeekly.setOnCheckedChangeListener(new checkChangedListener());
    }

    private class checkChangedListener implements CheckBox.OnCheckedChangeListener {
          @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            System.out.println(buttonView.getId());
              switch (buttonView.getId()) {
                  case (R.id.checkBoxNever):
                      myPlant.setWaternotify(0);
                      break;
                  case (R.id.checkBoxArduino):
                      myPlant.setWaternotify(1);
                      break;
                  case (R.id.checkBoxDaily):
                      myPlant.setWaternotify(2);
                      break;
                  case (R.id.checkBoxTwoDays):
                      myPlant.setWaternotify(3);
                      break;
                  case (R.id.checkBoxWeekly):
                      myPlant.setWaternotify(4);
                      break;
              }
//              myRef.
        }
    }

    @SuppressLint("SetTextI18n")
    private void setViews(MyPlant myPlant) {
        TextView nameView = findViewById(R.id.textViewMyPlantName);
        TextView statusView = findViewById(R.id.textViewMyPlantStatus);
        TextView dateView = findViewById(R.id.textViewMyPlantStartDate);

        nameView.setText(myPlant.getName());
        statusView.setText("Status: "+myPlant.getStatus());
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        String date = df.format(myPlant.getStartdate());
        dateView.setText("Growing since: "+date);

        checkBoxNever.setChecked(false);
        checkBoxArduino.setChecked(false);
        checkBoxDaily.setChecked(false);
        checkBoxTwoDays.setChecked(false);
        checkBoxWeekly.setChecked(false);

        int x = myPlant.getWaternotify();
        switch (x) {
            case (0):
                checkBoxNever.setChecked(true);
                break;
            case (1):
                checkBoxArduino.setChecked(true);
                break;
            case (2):
                checkBoxDaily.setChecked(true);
                break;
            case (3):
                checkBoxTwoDays.setChecked(true);
                break;
            case (4):
                checkBoxWeekly.setChecked(true);
                break;
        }

    }

    public void switchTabs(boolean direction) {
        if (direction) {
            if (tabHost.getCurrentTab() == 0) {
                tabHost.setCurrentTab(tabHost.getTabWidget().getTabCount() - 1);
            } else {
                tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
            }
        } else {
            if (tabHost.getCurrentTab() != (tabHost.getTabWidget().getTabCount() - 1)) {
                tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
            } else {
                tabHost.setCurrentTab(0);
            }
        }
    }


    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(MyPLantActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(MyPLantActivity.this, MyPlantsList.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(MyPLantActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(MyPLantActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
