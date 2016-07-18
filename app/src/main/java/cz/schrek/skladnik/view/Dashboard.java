package cz.schrek.skladnik.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import com.google.gson.Gson;
import cz.schrek.skladnik.Constants;
import cz.schrek.skladnik.R;
import cz.schrek.skladnik.model.UserAccount;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        settingsOnCreate();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLogged = preferences.getBoolean(Constants.IS_LOGGED, false);

        TextView tv = (TextView) findViewById(R.id.txt);
        tv.setText(isLogged+"");

        if (isLogged) {
            Gson gson = new Gson();
            String json = preferences.getString(Constants.LOGGED_USER, "");
            UserAccount user = gson.fromJson(json, UserAccount.class);

            tv.setText(tv.getText()+"\n"+user.toString());
        }
    }

    private void settingsOnCreate() {
        setTitle(getString(R.string.title_dashboard));
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
