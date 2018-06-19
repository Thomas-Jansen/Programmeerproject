package thomas.jansen.plantbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;

public class AccountActivity extends AppCompatActivity implements RequestMyPlants.Callback{

    static FirebaseAuth mAuth;
    int myGrowingPlantsNumber = 0;
    int myDeceasedPlantsNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intentLogin = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView nameTextView = findViewById(R.id.textViewAccountName);
        assert currentUser != null;
        if (Objects.equals(currentUser.getDisplayName(), "")) {
            nameTextView.setText(currentUser.getEmail());
        } else {
            nameTextView.setText(currentUser.getDisplayName());
        }

        RequestMyPlants requestMyPlants = new RequestMyPlants();
        requestMyPlants.RequestMyPlants(this);



        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new logoutOnClickListener());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(3).setChecked(true);
    }

    private class logoutOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mAuth.signOut();
            Intent intentLogin = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        }
    }

    @Override
    public void gotMyPlantsArray(ArrayList<MyPlant> arrayListMyPlants) {
        for (MyPlant myPlant: arrayListMyPlants) {
            if (myPlant.isAlive()) {
                myGrowingPlantsNumber += 1;
            } else {
                myDeceasedPlantsNumber += 1;
            }
        }
        TextView textViewGrowingPlants = findViewById(R.id.textViewGrowingPlants);
        if (myGrowingPlantsNumber == 1) {
            textViewGrowingPlants.setText("You are growing " + myGrowingPlantsNumber + " plant");
        } else {
            textViewGrowingPlants.setText("You are growing " + myGrowingPlantsNumber + " plants");
        }
        TextView textViewDeceasedPlants = findViewById(R.id.textViewDeadPlants);
        if (myDeceasedPlantsNumber == 1) {
            textViewDeceasedPlants.setText(myDeceasedPlantsNumber+ " plant died");
        } else {
            textViewDeceasedPlants.setText(myDeceasedPlantsNumber+ " plants died");
        }


    }

    @Override
    public void gotError(DatabaseError error) {
        Toast.makeText(AccountActivity.this, (CharSequence) error, LENGTH_LONG).show();
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(AccountActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(AccountActivity.this, MyPlantsList.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(AccountActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(AccountActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
