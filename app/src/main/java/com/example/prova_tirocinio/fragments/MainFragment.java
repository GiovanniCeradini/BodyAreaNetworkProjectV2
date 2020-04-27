package com.example.prova_tirocinio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.prova_tirocinio.R;
import com.example.prova_tirocinio.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    private static final String TAG = "FragmentTestingRecViewP";

    private FragmentMainBinding mBinding;
    private Button mButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View v = mBinding.getRoot();
        mButton=mBinding.showDialogFragmentBtn;


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment mDialogFragmentTesting= new DevicesFoundDialogFragment();
                mDialogFragmentTesting.show(getFragmentManager(), "DevicesFoundDialogFragment");
            }
        });

        return v;
    }
}
