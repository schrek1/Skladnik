package cz.schrek.skladnik.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import cz.schrek.skladnik.R;
import cz.schrek.skladnik.controler.Utility;
import cz.schrek.skladnik.model.UserAccount;

public class RegisterUser extends AppCompatActivity {

    private View progressBar;
    private Button registerBut;
    private EditText inputLogin;
    private EditText inputPassword;
    private EditText inputChckPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        init();
        settingsOnCreate();
        addHandlers();

    }

    private void init() {
        progressBar = findViewById(R.id.register_user_progressBar);
        registerBut = (Button) findViewById(R.id.register_user_bt_register);
        inputLogin = (EditText) findViewById(R.id.register_user_et_login);
        inputPassword = (EditText) findViewById(R.id.register_user_et_password);
        inputChckPass = (EditText) findViewById(R.id.register_user_et_checkPass);
    }

    private void settingsOnCreate() {
        setTitle(getString(R.string.title_registration));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void addHandlers() {
        addRegisterButClickHandler();
        addTextHandlersToInputs();
    }

    private void addTextHandlersToInputs() {
        inputLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().matches("^[a-zA-Z0-9._-]{3,}$")) {
                    inputLogin.setError(getString(R.string.login_bad_format));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                BackendlessDataQuery query = new BackendlessDataQuery();
                query.setWhereClause("login = '" + editable.toString() + "'");
                Backendless.Persistence.find(UserAccount.class, query, new AsyncCallback<BackendlessCollection<UserAccount>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<UserAccount> response) {
                        if (!response.getCurrentPage().isEmpty()) {
                            inputLogin.setError(getString(R.string.user_exists));
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        throw new RuntimeException(fault.toString());
                    }
                });
            }
        });
        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 4) {
                    inputPassword.setError(getString(R.string.password_is_short));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputChckPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 4) {
                    inputChckPass.setError(getString(R.string.password_is_short));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!inputPassword.getText().toString().equals(inputChckPass.getText().toString())) {
                    inputChckPass.setError(getString(R.string.passwords_not_equal));
                }
            }
        });
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
        setWaiting(true);
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause("login = '" + user.getLogin() + "'");
        Backendless.Persistence.find(UserAccount.class, query, new AsyncCallback<BackendlessCollection<UserAccount>>() {
            @Override
            public void handleResponse(BackendlessCollection<UserAccount> response) {
                if (response.getCurrentPage().isEmpty()) {
                    actionOnQueryResponse(response, user);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                throw new RuntimeException(fault.toString());
            }
        });
    }

    private void actionOnQueryResponse(BackendlessCollection<UserAccount> response, UserAccount user) throws RuntimeException {
        Backendless.Persistence.save(user, new AsyncCallback<UserAccount>() {
            @Override
            public void handleResponse(UserAccount response) {
                Toast.makeText(RegisterUser.this, getString(R.string.user_created), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(RegisterUser.this, getString(R.string.user_exists), Toast.LENGTH_SHORT).show();
                setWaiting(false);
                Log.wtf("REGISTER_ERROR:", fault.getMessage());
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

        String hash = Utility.getHash(password);

        return new UserAccount(login, hash);
    }

    private void checkValues(String password, String checkPass, String login) throws SecurityException {
        if (login.isEmpty() || password.isEmpty() || checkPass.isEmpty()) {
            throw new SecurityException(getString(R.string.fill_all_inputs));
        }

        if (!login.matches("^[a-zA-Z0-9._-]{3,}$")) {
            throw new SecurityException(getString(R.string.login_bad_format));
        }
        if (password.length() < 4) {
            throw new SecurityException(getString(R.string.password_is_short));
        }
        if (!password.equals(checkPass)) {
            throw new SecurityException(getString(R.string.passwords_not_equal));
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
