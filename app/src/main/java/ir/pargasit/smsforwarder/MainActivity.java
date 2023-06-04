package ir.pargasit.smsforwarder;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkForSmsReceivePermissions("android.permission.RECEIVE_SMS");
        checkForSmsReceivePermissions("android.permission.INTERNET");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_setting)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String targetIp = defaultSharedPreferences.getString("target_ip", "");
        String targetPort = defaultSharedPreferences.getString("target_port", "");
        String sourceNumber = defaultSharedPreferences.getString("source_number", "");

        if (targetIp.equals("") || targetPort.equals("") || sourceNumber.equals("")) {
            Toast.makeText(this, "تنظیمات اپلیکیشن انجام نشده است!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    void checkForSmsReceivePermissions(String permission) {
        // Check if App already has permissions for receiving SMS
        if (ContextCompat.checkSelfPermission(getBaseContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            // App has permissions to listen incoming SMS messages
            Log.d("pargas-it", "checkForSmsReceivePermissions: Allowed");
        } else {
            // App don't have permissions to listen incoming SMS messages
            Log.d("pargas-it", "checkForSmsReceivePermissions: Denied");

            // Request permissions from user
            ActivityCompat.requestPermissions(this, new String[]{permission}, 43391);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 43391) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("pargas-it", permissions + " Permission granted");
            } else {
                Log.d("pargas-it", permissions + " Permission denied");
            }
        }
    }
}