package cz.schrek.skladnik.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.google.gson.Gson;
import cz.schrek.skladnik.Constants;
import cz.schrek.skladnik.R;
import cz.schrek.skladnik.model.UserAccount;


public class MainLogin extends AppCompatActivity {

    private String login, password;
    private View progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        init();
        redirectIfLogged();
        settingsOnCreate();
        addHandlers();
    }

    private void init() {
        progressBar = findViewById(R.id.main_login_progress_bar);
    }

    private void redirectIfLogged() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLogged = preferences.getBoolean(Constants.IS_LOGGED, false);

        if (isLogged) {
            Intent intent = new Intent(MainLogin.this, Dashboard.class);
        }

    }

    private void settingsOnCreate() {
        setTitle(getString(R.string.title_login));
        Backendless.initApp(this, Constants.BACKENDLESS_APP_ID, Constants.BACKENDLESS_SECRET_KEY, Constants.BACKENDLESS_APP_VERSION);
        progressBar.setVisibility(View.GONE);
    }

    private void addHandlers() {
        addLoginButClickHandler();
        addRegisterButClickHandler();
    }

    private void addRegisterButClickHandler() {
        Button bt = (Button) findViewById(R.id.main_login_bt_register);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterUser.class);
                startActivity(intent);
            }
        });
    }

    private void addLoginButClickHandler() {
        Button bt = (Button) findViewById(R.id.main_login_bt_login);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyInputs()) {
                    verifyLogin();
                } else {
                    Toast.makeText(MainLogin.this, getString(R.string.fill_all_inputs), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyLogin() {
        progressBar.setVisibility(View.VISIBLE);
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause("login = '" + login + "'");
        Backendless.Persistence.find(UserAccount.class, query, new AsyncCallback<BackendlessCollection<UserAccount>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserAccount> response) {
                if (response != null && !response.getCurrentPage().isEmpty()) {
                    UserAccount user = response.getCurrentPage().get(0);
                    if (user.getPassword().equals(password)) {
                        saveLoginInformation(user);
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainLogin.this, getString(R.string.password_is_bad), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainLogin.this, getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                progressBar.setVisibility(View.GONE);
                Log.wtf("LOG_ERROR:", fault.getMessage());
            }
        });
    }

    private void saveLoginInformation(UserAccount user) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);

        edit.putString(Constants.LOGGED_USER, json);
        edit.putBoolean(Constants.IS_LOGGED, true);
        edit.commit();
    }


    private boolean verifyInputs() {
        boolean success = true;
        EditText etLogin = (EditText) findViewById(R.id.main_login_et_login);
        EditText etPass = (EditText) findViewById(R.id.main_login_et_password);

        login = etLogin.getText().toString();
        password = etPass.getText().toString();

        if (login.matches("") || password.matches("")) {
            return false;
        }
        return true;
    }


}
