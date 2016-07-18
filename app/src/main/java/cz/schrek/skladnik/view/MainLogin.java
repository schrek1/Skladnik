package cz.schrek.skladnik.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.backendless.Backendless;
import cz.schrek.skladnik.Constants;
import cz.schrek.skladnik.R;


public class MainLogin extends AppCompatActivity {

    private String login, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        setTitle("Prihlaseni");

        Backendless.initApp(this, Constants.BACKENDLESS_APP_ID, Constants.BACKENDLESS_SECRET_KEY, Constants.BACKENDLESS_APP_VERSION);

        addHandlers();
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
                    showBadInputAlert();
                }
            }
        });
    }

    private void verifyLogin() {
        //TODO dodelat prihlasovani do systemu

    }

    private void showBadInputAlert() {
        Toast.makeText(this, "Vyplňte všechna pole", Toast.LENGTH_SHORT).show();
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
