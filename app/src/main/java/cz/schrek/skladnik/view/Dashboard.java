package cz.schrek.skladnik.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
        tv.setText(isLogged + "");

        if (isLogged) {
            Gson gson = new Gson();
            String json = preferences.getString(Constants.LOGGED_USER, "");
            UserAccount user = gson.fromJson(json, UserAccount.class);

            tv.setText(tv.getText() + "\n" + user.toString());
        }
    }

    private void settingsOnCreate() {
        setTitle(getString(R.string.title_dashboard));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_menu_logout:
                Intent intent= new Intent(Dashboard.this, MainLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logout();
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLogged = preferences.getBoolean(Constants.IS_LOGGED, false);

        if (isLogged) {
            preferences.edit().remove(Constants.LOGGED_USER).commit();
            preferences.edit().putBoolean(Constants.IS_LOGGED, false).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        Drawable drawable = menu.findItem(R.id.app_menu_logout).getIcon(); // set white overlay for icon :)
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
