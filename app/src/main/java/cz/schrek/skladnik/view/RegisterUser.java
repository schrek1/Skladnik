package cz.schrek.skladnik.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        setTitle("Registrace");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addHandlers();

    }

    private void addHandlers() {
        addRegisterButClickHandler();
    }

    private void addRegisterButClickHandler() {
        Button bt = (Button) findViewById(R.id.register_user_bt_register);
        bt.setOnClickListener(new View.OnClickListener() {
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
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            sendQueryOnUserExist(user);
        } catch (RuntimeException e) {
            Toast.makeText(this, "Uzivatel jiz existuje!", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void sendQueryOnUserExist(final UserAccount user) {
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

    private void actionOnQueryResponse(BackendlessCollection<UserAccount> response, UserAccount user) {
        if (response.getCurrentPage().isEmpty()) {
            Backendless.Persistence.save(user, new AsyncCallback<UserAccount>() {
                @Override
                public void handleResponse(UserAccount response) {
                    Toast.makeText(RegisterUser.this, "Uzivatel vytvoren!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(RegisterUser.this, "Uzivatel nebyl vytvoren!\nerror:" + fault.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            throw new RuntimeException("UserAccount is exist");
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
        if(login.isEmpty()){
            throw new SecurityException("Jmeno je prazdne!");
        }
        if (!password.equals(checkPass)) {
            throw new SecurityException("Hesla se neshoduji!");
        }
        if (password.isEmpty() || checkPass.isEmpty()) {
            throw new SecurityException("Heslo je prazdne!");
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
