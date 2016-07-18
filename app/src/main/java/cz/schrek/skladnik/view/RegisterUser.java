package cz.schrek.skladnik.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import cz.schrek.skladnik.R;
import cz.schrek.skladnik.model.UserAccount;

public class RegisterUser extends AppCompatActivity {

    private View progressBar = findViewById(R.id.register_user_progressBar);
    private Button registerBut = (Button) findViewById(R.id.register_user_bt_register);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        settingsOnCreate();
        addHandlers();

    }

    private void settingsOnCreate() {
        setTitle(getResources().getString(R.string.title_registration));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void addHandlers() {
        addRegisterButClickHandler();
    }

    private void addRegisterButClickHandler() {
        registerBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRegistration();
            }
        });
    }

    private void performRegistration() {
        UserAccount user;
        try {
            user = constructUserFromLayout();
        } catch (SecurityException e) {
            Toast.makeText(RegisterUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        createUser(user);
    }

    private void createUser(final UserAccount user) {
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause("login = '" + user.getLogin() + "'");
        Backendless.Persistence.find(UserAccount.class, query, new AsyncCallback<BackendlessCollection<UserAccount>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserAccount> response) {
                actionOnQueryResponse(response, user);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                throw new RuntimeException(fault.toString());
            }
        });
    }

    private void actionOnQueryResponse(BackendlessCollection<UserAccount> response, UserAccount user) throws RuntimeException {
        setWaiting(true);
        Backendless.Persistence.save(user, new AsyncCallback<UserAccount>() {
            @Override
            public void handleResponse(UserAccount response) {
                Toast.makeText(RegisterUser.this, getResources().getString(R.string.user_created), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(RegisterUser.this, getResources().getString(R.string.user_exists), Toast.LENGTH_SHORT).show();
                setWaiting(false);
                Log.wtf("INFO:", fault.getMessage());
            }
        });
    }

    private void setWaiting(boolean waiting) {
        if (waiting) {
            registerBut.setEnabled(false);
            registerBut.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            registerBut.setEnabled(true);
            registerBut.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private UserAccount constructUserFromLayout() throws SecurityException {
        String login = ((TextView) findViewById(R.id.register_user_et_login)).getText().toString();
        String password = ((TextView) findViewById(R.id.register_user_et_password)).getText().toString();
        String checkPass = ((TextView) findViewById(R.id.register_user_et_checkPass)).getText().toString();

        checkValues(password, checkPass, login);

        return new UserAccount(login, password);
    }

    private void checkValues(String password, String checkPass, String login) throws SecurityException {
        if (!login.matches("^[a-zA-Z0-9._-]{3,}$")) {
            throw new SecurityException(getResources().getString(R.string.login_bad_format));
        }
        if (password.length() < 4) {
            throw new SecurityException(getResources().getString(R.string.password_is_short));
        }
        if (!password.equals(checkPass)) {
            throw new SecurityException(getResources().getString(R.string.passwords_not_equal));
        }
        if (password.isEmpty() || checkPass.isEmpty()) {
            throw new SecurityException(getResources().getString(R.string.password_is_empty));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
