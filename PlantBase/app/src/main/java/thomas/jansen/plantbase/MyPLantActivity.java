package thomas.jansen.plantbase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    FirebaseAuth mAuth;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    int waternotify = 0;
    EditText nameView;
    Button buttonPlantDied;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplant);

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

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

        ImageButton addPhotoButton = findViewById(R.id.imageButtonAddPhoto);
        addPhotoButton.setOnClickListener(new addPhotoOnClickListener());

        Intent intent = getIntent();
        myPlant = (MyPlant) intent.getSerializableExtra("clickedMyPlant");
        if (myPlant == null) {
            myPlant = (MyPlant) intent.getSerializableExtra("MyPlant");
        }

        nameView = findViewById(R.id.editTextMyPlantName);
        nameView.setSelected(false);
        buttonPlantDied = findViewById(R.id.buttonMyPlantDied);
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


        checkBoxNever.setOnClickListener(new checkBoxOnClickListener());
        checkBoxDaily.setOnClickListener(new checkBoxOnClickListener());
        checkBoxTwoDays.setOnClickListener(new checkBoxOnClickListener());
        checkBoxWeekly.setOnClickListener(new checkBoxOnClickListener());
        checkBoxArduino.setOnClickListener(new checkBoxOnClickListener());

        nameView.setOnEditorActionListener(new nameEditListener());

        buttonPlantDied.setOnClickListener(new plantDiedOnClickListener());
    }

    private class plantDiedOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (myPlant.isAlive()) {
                myPlant.setAlive(false);
                myPlant.setStatus("Deceased");
                myPlant.setWaternotify(0);
            } else {
                myPlant.setAlive(true);
                myPlant.setStatus("OK");
            }
            new UpdateMyPlantActivity(myPlant, getApplicationContext());
            setViews(myPlant);
        }
    }

    private class nameEditListener implements EditText.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                myPlant.setName(String.valueOf(nameView.getText()));
                new UpdateMyPlantActivity(myPlant, getApplicationContext());
                hideKeyboard(MyPLantActivity.this);
                nameView.setFocusable(false);
                setViews(myPlant);
            }
            return true;
        }
    }

    private class addPhotoOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
//            ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private class checkBoxOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case (R.id.checkBoxNever):
                    waternotify = 0;
                    break;
                case (R.id.checkBoxArduino):
                    waternotify = 1;
                    break;
                case (R.id.checkBoxDaily):
                    waternotify = 2;
                    break;
                case (R.id.checkBoxTwoDays):
                    waternotify = 3;
                    break;
                case (R.id.checkBoxWeekly):
                    waternotify = 4;
                    break;
            }
            myPlant.setWaternotify(waternotify);
            new UpdateMyPlantActivity(myPlant, getApplicationContext());
            setViews(myPlant);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setViews(MyPlant myPlant) {

        TextView statusView = findViewById(R.id.textViewMyPlantStatus);
        TextView dateView = findViewById(R.id.textViewMyPlantStartDate);
        TextView dayView = findViewById(R.id.textViewMyPlantDay);
        ImageView imageView = findViewById(R.id.imageViewMyPlant);

        nameView.setText(myPlant.getName());
        nameView.setFocusable(true);
        statusView.setText("Status: "+myPlant.getStatus());
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date sinceDate = myPlant.getStartdate();
        String date = df.format(sinceDate);
        dateView.setText("Growing since: "+date);
        Date currentDate = Calendar.getInstance().getTime();
        dayView.setText("Day: "+(int)((currentDate.getTime()/(24*60*60*1000))-(int)(sinceDate.getTime()/(24*60*60*1000))));
        String imageName= myPlant.getImageID();
        int imageId = getResources().getIdentifier(imageName , "drawable", getPackageName());
        imageView.setImageResource(imageId);

        if (!myPlant.isAlive()) {
            buttonPlantDied.setText("I revived this plant");
        }

        checkBoxNever.setChecked(false);
        checkBoxArduino.setChecked(false);
        checkBoxDaily.setChecked(false);
        checkBoxTwoDays.setChecked(false);
        checkBoxWeekly.setChecked(false);
        if (myPlant.isConnected()) {
            checkBoxArduino.setEnabled(true);
        } else {
            checkBoxArduino.setEnabled(false);
        }


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

//  From: https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
