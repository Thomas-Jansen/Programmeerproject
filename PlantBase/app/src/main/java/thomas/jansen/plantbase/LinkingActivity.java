package thomas.jansen.plantbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import static thomas.jansen.plantbase.AccountActivity.mAuth;

public class LinkingActivity extends AppCompatActivity {

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

        ListView plantsLinkView = findViewById(R.id.listViewMPLink);
        ListView plantNodeView = findViewById(R.id.listViewPlantNodes);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(2).setChecked(true);
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
                    Intent intentMyPlants = new Intent(LinkingActivity.this, MyPlantsList.class);
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
