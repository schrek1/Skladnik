package cz.schrek.skladnik.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import cz.schrek.skladnik.controler.Utility;
import cz.schrek.skladnik.model.UserAccount;


public class MainLogin extends AppCompatActivity {

    private String login, password;
    private EditText inputLogin;
    private EditText inputPassword;
    private Button butLogin;
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
        inputLogin = (EditText) findViewById(R.id.main_login_et_login);
        inputPassword = (EditText) findViewById(R.id.main_login_et_password);
        butLogin = (Button) findViewById(R.id.main_login_bt_login);
    }

    private void redirectIfLogged() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLogged = preferences.getBoolean(Constants.IS_LOGGED, false);

        if (isLogged) {
            Intent intent = new Intent(MainLogin.this, Dashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
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
        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIME();
                if (verifyInputs()) {
                    verifyLogin();
                } else {
                    Toast.makeText(MainLogin.this, getString(R.string.fill_all_inputs), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void hideIME() {
        InputMethodManager inmngr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inmngr.hideSoftInputFromWindow(inputLogin.getWindowToken(), 0);
        inmngr.hideSoftInputFromWindow(inputPassword.getWindowToken(), 0);
    }

    private void verifyLogin() {
        waiting(true);
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause("login = '" + login + "'");
        Backendless.Persistence.find(UserAccount.class, query, new AsyncCallback<BackendlessCollection<UserAccount>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserAccount> response) {
                if (response != null && !response.getCurrentPage().isEmpty()) {
                    UserAccount user = response.getCurrentPage().get(0);
                    if (!password.isEmpty() && user.getPassword().equals(Utility.getHash(password))) {
                        saveLoginInformation(user);
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainLogin.this, getString(R.string.password_is_bad), Toast.LENGTH_SHORT).show();
                        inputPassword.setError(getString(R.string.password_is_bad));
                        waiting(false);
                    }
                } else {
                    Toast.makeText(MainLogin.this, getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
                    inputLogin.setError(getString(R.string.user_not_exists));
                    waiting(false);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                waiting(false);
                Log.wtf("LOG_ERROR:", fault.getMessage());
            }
        });
    }

    private void waiting(boolean waiting) {
        if (waiting) {
            butLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            butLogin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void saveLoginInformation(UserAccount user) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);

        edit.putString(Constants.LOGGED_USER, json).commit();
        edit.putBoolean(Constants.IS_LOGGED, true).commit();
    }


    private boolean verifyInputs() {
        boolean valid = true;
        login = inputLogin.getText().toString();
        password = inputPassword.getText().toString();

        if (login.matches("")) {
            inputLogin.setError(getString(R.string.fill_all_inputs));
            valid = false;
        }

        if (password.matches("")) {
            inputPassword.setError(getString(R.string.fill_all_inputs));
            valid = false;
        }

        return valid;
    }

}
