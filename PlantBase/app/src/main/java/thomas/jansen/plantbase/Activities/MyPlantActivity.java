/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    MyPlant activity handles everything related to MyPlant.
*/

package thomas.jansen.plantbase.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import thomas.jansen.plantbase.Classes.MyPlant;
import thomas.jansen.plantbase.Helpers.BottomNavigationViewHelper;
import thomas.jansen.plantbase.R;
import thomas.jansen.plantbase.Requests.RequestPlantNode;
import thomas.jansen.plantbase.Requests.StorageRequest;
import thomas.jansen.plantbase.Requests.UpdateMyPlantRequest;

import static android.widget.Toast.LENGTH_LONG;

public class MyPlantActivity extends AppCompatActivity implements StorageRequest.Callback, RequestPlantNode.Callback {

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
    int waternotify = 0;
    EditText nameView;
    Button buttonPlantDied;
    ImageView imageViewMyPlant;
    LinearLayout photosLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplant);

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth.getCurrentUser() == null) {
            Intent intentLogin = new Intent(MyPlantActivity.this, LoginActivity.class);
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

        imageViewMyPlant = findViewById(R.id.imageViewMyPlant);
        nameView = findViewById(R.id.editTextMyPlantName);
        nameView.setSelected(false);
        buttonPlantDied = findViewById(R.id.buttonMyPlantDied);
        photosLayout = findViewById(R.id.linearPhotos);
        setViews(myPlant);

        // Setup Tabs
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
        Button buttonRemovePlant = findViewById(R.id.buttonRemoveMyPlant);
        buttonRemovePlant.setOnClickListener(new removeOnClickListener());

        Button plantWatered = findViewById(R.id.buttonWatered);
        plantWatered.setOnClickListener(new wateredOnClickListener());
    }

    // Sets living plants to deceased and deceased plant to alive.
    private class plantDiedOnClickListener implements View.OnClickListener {

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            Button diedButton = findViewById(v.getId());
            if (myPlant.isAlive()) {
                myPlant.setAlive(false);
                myPlant.setStatus("Deceased");
                myPlant.setConnected(false);
                myPlant.setArduinoName("none");
                myPlant.setWaternotify(0);
                diedButton.setText("I revived this plant");
            } else {
                myPlant.setAlive(true);
                myPlant.setStatus("OK");
                diedButton.setText("This plant died");
            }
            new UpdateMyPlantRequest(myPlant, getApplicationContext());
            setViews(myPlant);
        }
    }

    // Update last watered for MyPlant.
    private class wateredOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Long now = Calendar.getInstance().getTimeInMillis();
            myPlant.setLastwatered(now);
            myPlant.setStatus("OK");
            new UpdateMyPlantRequest(myPlant, getApplicationContext());
            setViews(myPlant);
        }
    }

    // Delete current MyPlant.
    private class removeOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(MyPlantActivity.this)
                    .setTitle("Remove "+myPlant.getName())
                    .setMessage("Are you certain you want to remove this plant?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new UpdateMyPlantRequest(myPlant, getApplicationContext()).RemoveMyPlantActivity();
                            Intent intentMyPlants = new Intent(MyPlantActivity.this, MyPlantsListActivity.class);
                            startActivity(intentMyPlants);
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create()
                    .show();
        }
    }

    // Edit plant name for current MyPlant.
    private class nameEditListener implements EditText.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                myPlant.setName(String.valueOf(nameView.getText()));
                new UpdateMyPlantRequest(myPlant, getApplicationContext());
                hideKeyboard(MyPlantActivity.this);
                nameView.setFocusable(false);
                setViews(myPlant);
            }
            return true;
        }
    }

    // Ad an photo from your gallery to MyPlant or take a new photo.
    private class addPhotoOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(MyPlantActivity.this)
                    .setTitle("Add a picture of "+myPlant.getName())
                    .setMessage("Take a picture or choose an existing one")
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton("Choose from gallery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                        }
                    })
                    .setNegativeButton("Take a photo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                        }
                    })
                    .create()
                    .show();
        }
    }

    // Activity result from addPhoto.
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                // New photo is taken.
                if(resultCode == RESULT_OK){
                    Bitmap takenimage = (Bitmap) Objects.requireNonNull(imageReturnedIntent.getExtras()).get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    assert takenimage != null;
                    takenimage.compress(Bitmap.CompressFormat.JPEG, 500, baos);
                    byte[] newPhoto =  baos.toByteArray();
                    StorageRequest storageRequest = new StorageRequest(this, myPlant, this);
                    storageRequest.StoreImage(newPhoto);
                }
                break;
            case 1:
                // A photo has been chosen from gallery.
                if(resultCode == RESULT_OK){
                    Uri imageReturned = imageReturnedIntent.getData();
                    byte[] photo = null;
                    try {
                        ContentResolver cr = getBaseContext().getContentResolver();
                        assert imageReturned != null;
                        InputStream inputStream = cr.openInputStream(imageReturned);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        photo = baos.toByteArray();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    StorageRequest storageRequest =  new StorageRequest(this, myPlant, this);
                    storageRequest.StoreImage(photo);
                }
                break;
        }
    }

    // Got photos from Firebase Storage.
    @Override
    public void gotPhotoUri(Uri addedPhoto) {
        ArrayList<String> uriArrayList = myPlant.getAddedImages();
        uriArrayList.add(String.valueOf(addedPhoto));
        myPlant.setAddedImages(uriArrayList);
        new UpdateMyPlantRequest(myPlant, this);
        setViews(myPlant);
    }

    // Show added photo in popup screen.
    private class onPhotoClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (!v.getTag().equals("none")) {
                AlertDialog.Builder ImageDialog = new AlertDialog.Builder(MyPlantActivity.this);
                ImageDialog.setTitle(myPlant.getName());
                ImageView showImage = new ImageView(MyPlantActivity.this);
                Picasso.with(getApplicationContext())
                        .load(Uri.parse((String) v.getTag()))
                        .into(showImage);
                ImageDialog.setView(showImage);

                ImageDialog.setNegativeButton("", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                    }
                });
                ImageDialog.show();
            }
        }
    }

    // Delete added photo.
    private class deleteOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(final View v) {
            new AlertDialog.Builder(MyPlantActivity.this)
                    .setTitle("Remove photo")
                    .setMessage("Are you certain you want to remove this photo?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String imageUri = (String) v.getTag();
                            new StorageRequest(getApplicationContext(), myPlant,
                                    MyPlantActivity.this).DeleteStoredPhoto(Uri.parse(imageUri));
                            ArrayList<String> addedImages = myPlant.getAddedImages();
                            for (int x = 0; x < addedImages.size(); x++) {
                                if (imageUri.equals(addedImages.get(x))) {
                                    addedImages.remove(x);
                                }
                            }
                            myPlant.setAddedImages(addedImages);
                            if (myPlant.getAvatarImage().equals(imageUri)) {
                                myPlant.setAvatarImage("none");
                            }
                            new UpdateMyPlantRequest(myPlant, getApplicationContext());
                            setViews(myPlant);
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create()
                    .show();
            return false;
        }
    }

    // Set water notifications.
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
            new UpdateMyPlantRequest(myPlant, getApplicationContext());
            setViews(myPlant);
        }
    }

    // Set all MyPlant views. Gets updated whenever something has been changed.
    @SuppressLint("SetTextI18n")
    private void setViews(MyPlant myPlant) {

        TextView statusView = findViewById(R.id.textViewMyPlantStatus);
        TextView dateView = findViewById(R.id.textViewMyPlantStartDate);
        TextView dayView = findViewById(R.id.textViewMyPlantDay);
        TextView textViewConnected = findViewById(R.id.textViewMPConnectedTo);

        nameView.setText(myPlant.getName());
        nameView.setFocusable(true);
        statusView.setText("Status: "+myPlant.getStatus());
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Long sinceDate = myPlant.getStartdate();
        String date = df.format(sinceDate);
        dateView.setText("Growing since: "+date);
        Long currentDate = Calendar.getInstance().getTimeInMillis();
        dayView.setText("Days: "+(currentDate - sinceDate)/(24*60*60*1000));
        String imageName= myPlant.getImageID();
        int imageId = getResources().getIdentifier(imageName , "drawable", getPackageName());

        if (myPlant.getAvatarImage().equals("none")) {
            imageViewMyPlant.setImageResource(imageId);
        } else {
            Picasso.with(this)
                    .load(Uri.parse(myPlant.getAvatarImage()))
                    .into(imageViewMyPlant);
        }

        textViewConnected.setText("Connected: "+myPlant.isConnected());

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

        // Set added photos in a dynamic view.
        photosLayout.removeAllViews();
        if (myPlant.getAddedImages() != null) {
            ArrayList<String> addedPhotos = myPlant.getAddedImages();
            for (x = 0; x < addedPhotos.size(); x++) {
                Uri photoUri = Uri.parse(addedPhotos.get(x));
                ImageView photoView = new ImageView(MyPlantActivity.this);
                photoView.setLayoutParams(new android.view.ViewGroup.LayoutParams(400,400));
                photoView.setMaxHeight(400);
                photoView.setMaxWidth(400);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(photoView.getLayoutParams());
                lp.setMargins(10, 10, 10, 10);
                photoView.setLayoutParams(lp);

                if (x == 0) {
                    photoView.setImageURI(photoUri);
                    photoView.setTag("none");
                } else {
                    Picasso.with(this)
                            .load(photoUri)
                            .into(photoView);
                    photoView.setTag(addedPhotos.get(x));
                    photoView.setOnLongClickListener(new deleteOnLongClickListener());
                }

                photoView.setOnClickListener(new onPhotoClickListener());
                photosLayout.addView(photoView);
            }
        }

        // If plant is connected to a PlantNode, get PlantNode data.
        if (myPlant.isConnected()) {
            TextView textViewNotConnected = findViewById(R.id.textViewNotConnected);
            textViewNotConnected.setVisibility(View.INVISIBLE);
            new RequestPlantNode().RequestPlantNodeData(this, myPlant.getArduinoName());
        } else {
            GraphView graphView = findViewById(R.id.graph);
            graphView.setVisibility(View.INVISIBLE);
            TextView textViewNotConnected = findViewById(R.id.textViewNotConnected);
            textViewNotConnected.setText(myPlant.getName()+" is not connected to a PlantNode");
        }
    }

    // Got PlantNode data, create graph.
    @Override
    public void gotPlantNodeData(DataPoint[] dataPointsTemp, DataPoint[] dataPointsHum, DataPoint[] dataPointsMoist, DataPoint[] dataPointsLight) {

        LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<>(dataPointsTemp);
        LineGraphSeries<DataPoint> seriesHum = new LineGraphSeries<>(dataPointsHum);
        LineGraphSeries<DataPoint> seriesMoist = new LineGraphSeries<>(dataPointsMoist);
        LineGraphSeries<DataPoint> seriesLight = new LineGraphSeries<>(dataPointsLight);

        seriesTemp.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_english_vermillion));
        seriesTemp.setTitle("Temperature \u2103");
        seriesHum.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_blue_jeans));
        seriesHum.setTitle("Relative Humidity %");
        seriesMoist.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_android_green));
        seriesMoist.setTitle("Soil Moisture %");
        seriesLight.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_gargoyle_gas));
        seriesLight.setTitle("Light %");

        GraphView graph = findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);

        graph.getGridLabelRenderer().setNumHorizontalLabels(dataPointsTemp.length / 10);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(dataPointsTemp.length);

        graph.getViewport().setScalable(true);

        graph.destroyDrawingCache();
        graph.addSeries(seriesTemp);
        graph.addSeries(seriesHum);
        graph.addSeries(seriesMoist);
        graph.addSeries(seriesLight);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    @Override
    public void gotError(DatabaseError error) {
        Toast.makeText(this, (CharSequence) error, LENGTH_LONG).show();
    }

    @Override
    public void gotLastPlantNodeData(int[] lastData, MyPlant myPlant) {}

    // Switch tabs whenever a tab is pressed.
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

    // Hide Keyboard after typing.
    // From: https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(MyPlantActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(MyPlantActivity.this, MyPlantsListActivity.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(MyPlantActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(MyPlantActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
