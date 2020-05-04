//package com.example.prova_tirocinio.fromOldApp;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.preference.PreferenceFragmentCompat;
//
///**
// * PreferenceFragment per settare le impostazione di connessione verso il DB.
// */
//public class DatabaseSettingsFragment extends PreferenceFragmentCompat {
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public void onPrepareOptionsMenu(@NonNull Menu menu) {
//        menu.clear();
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Database Settings");
//    }
//
//    @Override
//    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        setPreferencesFromResource(R.xml.preferences, rootKey);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
//                String name = pref.getString("db_name", "");
//                String url = pref.getString("db_url", "");
//                Toast.makeText(getContext(), url + name, Toast.LENGTH_SHORT).show();
//                getActivity().onBackPressed();
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//}
