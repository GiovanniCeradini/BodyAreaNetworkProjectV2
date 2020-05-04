package com.example.prova_tirocinio;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.prova_tirocinio.databinding.ActivityMainBinding;
import com.example.prova_tirocinio.fragments.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAddDevice;
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//



        FragmentManager fm = getSupportFragmentManager();

        fabAddDevice=mainBinding.addThingyDevice;

        fabAddDevice.setOnClickListener(v -> {
            fabAddDevice.hide();
            Fragment thingyAddFragment = new MainFragment();
            fm.beginTransaction().replace(R.id.fragment_container, thingyAddFragment).addToBackStack(null).commit();
        });

        fm.addOnBackStackChangedListener(() -> {
            if (fm.getBackStackEntryCount() == 0) {
                fabAddDevice.show();
//                getSupportActionBar().setTitle("Signal Server - Paziente");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });
//        if(fragment==null){
//            fragment=new MainFragment();
//            fm.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
//        }
//        else
//            fm.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            fabAddDevice.hide();
            Fragment fragment=new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        }

        return super.onOptionsItemSelected(item);
    }
}