package com.example.prova_tirocinio;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.prova_tirocinio.fragments.MainFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragment_container);


        if(fragment==null){
            fragment=new MainFragment();
            fm.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
        }
        else
            fm.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}