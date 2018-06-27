/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Links plants to PlantNodes.
*/

package thomas.jansen.plantbase.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import thomas.jansen.plantbase.Adapters.AdapterLinkMyPlant;
import thomas.jansen.plantbase.Adapters.AdapterLinkNode;
import thomas.jansen.plantbase.Classes.MyPlant;
import thomas.jansen.plantbase.Helpers.BottomNavigationViewHelper;
import thomas.jansen.plantbase.R;
import thomas.jansen.plantbase.Requests.RequestMyPlants;
import thomas.jansen.plantbase.Requests.UpdateMyPlantRequest;

import static thomas.jansen.plantbase.Activities.AccountActivity.mAuth;

public class LinkingActivity extends AppCompatActivity implements RequestMyPlants.Callback {

    ArrayList<MyPlant> arrayListMyPlants;
    ArrayList<String> plantNodeNameList;
    View highlightedView;
    ListView plantNodeView;
    ListView plantsLinkView;
    MyPlant myPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linking);

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth.getCurrentUser() == null) {
            Intent intentLogin = new Intent(LinkingActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        }

        arrayListMyPlants = new ArrayList<>();
        plantNodeNameList = new ArrayList<>();

        myPlant = null;
        highlightedView = null;
        plantNodeView = findViewById(R.id.listViewPlantNodes);
        arrayListMyPlants.clear();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("plantnode_data");
        myRef.addListenerForSingleValueEvent(new myPlantListener());

        RequestMyPlants requestMyPlants = new RequestMyPlants();
        requestMyPlants.RequestMyPlants(this);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(2).setChecked(true);
    }

    // Get PlantNode names and set adapter.
    private class myPlantListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                plantNodeNameList.add(String.valueOf(child.getKey()));
                }
            AdapterLinkNode adapterLinkNode = new AdapterLinkNode(getApplicationContext(), R.layout.item_plant_node, plantNodeNameList);
            plantNodeView.setAdapter(adapterLinkNode);
            }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            String errorString = databaseError.toString();
            Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
        }
    }

    // Get MyPlants, set adapter and (long)clickListener.
    @Override
    public void gotMyPlantsArray(ArrayList<MyPlant> arrayListMyPlants) {

        for (int x = 0; x < arrayListMyPlants.size(); x++) {
            MyPlant myPlant = arrayListMyPlants.get(x);
            if (myPlant.isAlive()) {
                this.arrayListMyPlants.add(myPlant);
            }
        }
        plantsLinkView = findViewById(R.id.listViewMPLink);
        plantsLinkView.setAdapter(null);
        AdapterLinkMyPlant adapterLinkMyPlant = new AdapterLinkMyPlant(this, R.layout.item_link_myplant, this.arrayListMyPlants);
        plantsLinkView.setAdapter(adapterLinkMyPlant);
        plantsLinkView.setOnItemClickListener(new onPlantLinkClickListener());
        plantsLinkView.setOnItemLongClickListener(new onPlantLinkLongClickListener());
    }

    @Override
    public void gotError(DatabaseError error) {
        String errorString = error.toString();
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    // Highlight a clicked MyPlant and set listener on PlantNodes.
    private class onPlantLinkClickListener implements  AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            myPlant = (MyPlant) parent.getItemAtPosition(position);
            if (!myPlant.isConnected()) {
                if (highlightedView == view) {
                    highlightedView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.color_none));
                    highlightedView = null;
                    plantNodeView.setOnItemClickListener(null);
                } else {
                    for (int x = 0; x < parent.getCount(); x++) {
                        View viewX = parent.getChildAt(x);
                        if (viewX != null) {
                            viewX.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.color_none));
                        }
                    }
                    highlightedView = view;
                    highlightedView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.color_light_grey));
                    plantNodeView.setOnItemClickListener(new onNodeClickListener());
                }
            }
        }
    }

    // LongClickListener to delete connection between MyPlant and PlantNode.
    private class onPlantLinkLongClickListener implements  AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final MyPlant myPlant = (MyPlant) parent.getItemAtPosition(position);
            if (myPlant.isConnected()) {
                new AlertDialog.Builder(LinkingActivity.this)
                        .setTitle("Remove link")
                        .setMessage("Are you certain you want to unlink this plant?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                myPlant.setConnected(false);
                                myPlant.setArduinoName("none");
                                myPlant.setWaternotify(0);
                                new UpdateMyPlantRequest(myPlant, getApplicationContext());
                                Intent intentLinking = new Intent(LinkingActivity.this, LinkingActivity.class);
                                startActivity(intentLinking);
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
            return false;
        }
    }

    // OnclickListener for PlantNodes, establish connection
    private class onNodeClickListener implements  AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String nodeName = (String) parent.getItemAtPosition(position);
            if (myPlant != null) {
                myPlant.setArduinoName(nodeName);
                myPlant.setConnected(true);
                new UpdateMyPlantRequest(myPlant, getApplicationContext());
                Intent intentLinking = new Intent(LinkingActivity.this, LinkingActivity.class);
                startActivity(intentLinking);
            }
        }
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(LinkingActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(LinkingActivity.this, MyPlantsListActivity.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(LinkingActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(LinkingActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
