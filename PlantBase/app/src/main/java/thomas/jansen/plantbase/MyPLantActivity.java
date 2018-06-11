package thomas.jansen.plantbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TabHost;

public class MyPLantActivity extends AppCompatActivity {

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myplant);

        TabHost host = findViewById(R.id.tabHost);
        host.setup();
//        host.setOnGenericMotionListener(new HostOnTouchEvent());

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
    }

//    private class HostOnTouchEvent implements TabHost.OnGenericMotionListener {
//        @Override
//        public boolean onGenericMotion(View view, MotionEvent motionEvent) {
//            float lastX = motionEvent.getX();
//            switch (motionEvent.getAction()) {
//                if (lastX < currentX) {
//                    switchTabs(false);
//                }
//                if (lastX > currentX) {
//                    switchTabs(true);
//                }
//                break;
//            }
//        return false;
//    }

    public void switchTabs(boolean direction) {
        if (direction)
        {
            if (tabHost.getCurrentTab() == 0)
                tabHost.setCurrentTab(tabHost.getTabWidget().getTabCount() - 1);
            else
                tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
        } else
        {
            if (tabHost.getCurrentTab() != (tabHost.getTabWidget()
                    .getTabCount() - 1))
                tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
            else
                tabHost.setCurrentTab(0);
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
