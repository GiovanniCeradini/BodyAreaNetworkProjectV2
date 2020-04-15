package com.example.prova_tirocinio;

import android.os.Bundle;

import com.example.prova_tirocinio.databinding.ActivityMainBinding;
import com.example.prova_tirocinio.fragments.FragmentProva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();
        Fragment fragment=mFragmentManager.findFragmentById(R.id.fragment_container);

        if(fragment==null){
            fragment=new FragmentProva();
            mFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit();
        }
        else
            mFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit();


        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = mBinding.fab;
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ScannerFragment fragment=new ScannerFragment();
                mFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });
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
