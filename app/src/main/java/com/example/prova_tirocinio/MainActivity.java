package com.example.prova_tirocinio;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.prova_tirocinio.databinding.ActivityMainBinding;
import com.example.prova_tirocinio.fragments.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity{

    private FloatingActionButton fabAddDevice;
    private ActivityMainBinding mainBinding;

    private BluetoothAdapter mBluetoothAdapter  = null;
    public static final int REQUEST_BT_PERMISSIONS = 0;
    public static final int REQUEST_BT_ENABLE = 1;

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
//            Fragment thingyAddFragment = new MainFragment();
//            fm.beginTransaction().replace(R.id.fragment_container, thingyAddFragment).addToBackStack(null).commit();
            Intent intent = new Intent(this, ScannerActivity.class);
//            EditText editText = (EditText) findViewById(R.id.editText);
//            String message = editText.getText().toString();
//            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        });

        fm.addOnBackStackChangedListener(() -> {
            if (fm.getBackStackEntryCount() == 1) {
                fabAddDevice.show();
//                getSupportActionBar().setTitle("Signal Server - Paziente");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBtPermissions();
        enableBt();

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

    public void checkBtPermissions() {
        this.requestPermissions(
                new String[]{
                        Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN
                },
                REQUEST_BT_PERMISSIONS);
    }

    public void enableBt(){
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
        }
    }

}