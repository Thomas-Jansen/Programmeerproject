/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Login activity. User can create a new account using their email or login with an existing account.
*/

package thomas.jansen.plantbase.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import thomas.jansen.plantbase.Helpers.BottomNavigationViewHelper;
import thomas.jansen.plantbase.R;

import static android.widget.Toast.LENGTH_LONG;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static final String TAG = "PlantBase";
    EditText editTextEmail = null;
    EditText editTextPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Intent intentAccount = new Intent(LoginActivity.this, AccountActivity.class);
            startActivity(intentAccount);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new mOnNavigationItemSelectedListener());
        navigation.getMenu().getItem(3).setChecked(true);

        editTextEmail = findViewById(R.id.editTextMail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonCreate = findViewById(R.id.buttonCreateAccount);
        buttonLogin.setOnClickListener(new loginOnclickListener());
        buttonCreate.setOnClickListener(new createOnclickListener());
    }

    // Create a new account.
    private class createOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            String accountEmail = String.valueOf(editTextEmail.getText());
            String accountPassword = String.valueOf(editTextPassword.getText());
            if (accountEmail.isEmpty() || accountPassword.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please provide email and password", LENGTH_LONG).show();
            }
            if (accountPassword.length() < 6) {
                Toast.makeText(LoginActivity.this, "Please provide a password with a minimum of 6 characters", LENGTH_LONG).show();
            }
            else {
                mAuth.createUserWithEmailAndPassword(accountEmail, accountPassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                            Intent intentAccount = new Intent(LoginActivity.this, AccountActivity.class);
                            startActivity(intentAccount);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    // Login with existing account
    private class loginOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            String accountEmail = String.valueOf(editTextEmail.getText());
            String accountPassword = String.valueOf(editTextPassword.getText());
            if (!accountEmail.isEmpty() && !accountPassword.isEmpty()) {
                mAuth.signInWithEmailAndPassword(accountEmail, accountPassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    Intent intentAccount = new Intent(LoginActivity.this, AccountActivity.class);
                                    startActivity(intentAccount);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
            else {
                Toast.makeText(LoginActivity.this, "Please provide email and password", LENGTH_LONG).show();
            }
        }
    }

    private class mOnNavigationItemSelectedListener
            implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent intentSearch = new Intent(LoginActivity.this, SearchActivity.class);
                    startActivity(intentSearch);
                    return true;
                case R.id.navigation_plants:
                    Intent intentMyPlants = new Intent(LoginActivity.this, MyPlantsListActivity.class);
                    startActivity(intentMyPlants);
                    return true;
                case R.id.navigation_linking:
                    Intent intentLinking = new Intent(LoginActivity.this, LinkingActivity.class);
                    startActivity(intentLinking);
                    return true;
                case R.id.navigation_account:
                    Intent intentLogin = new Intent(LoginActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
            }
            return false;
        }
    }
}
