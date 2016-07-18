package cz.schrek.skladnik.view;


import android.content.Intent;
import android.os.Bundle;
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
import cz.schrek.skladnik.Constants;
import cz.schrek.skladnik.R;
import cz.schrek.skladnik.model.UserAccount;


public class MainLogin extends AppCompatActivity {

    private String login, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        settingsOnCreate();
        addHandlers();
    }

    private void settingsOnCreate() {
        setTitle(getString(R.string.title_login));
        Backendless.initApp(this, Constants.BACKENDLESS_APP_ID, Constants.BACKENDLESS_SECRET_KEY, Constants.BACKENDLESS_APP_VERSION);
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
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause("login = '" + login + "'");
        Backendless.Persistence.find(UserAccount.class, query, new AsyncCallback<BackendlessCollection<UserAccount>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserAccount> response) {
                if (response != null && !response.getCurrentPage().isEmpty()) {
                    UserAccount user = response.getCurrentPage().get(0);
                    if (user.getPassword().equals(password)) {
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainLogin.this, getString(R.string.password_is_bad), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainLogin.this, getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.wtf("LOG_ERROR:", fault.getMessage());
            }
        });
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
