package com.example.prova_tirocinio.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.R;
import com.example.prova_tirocinio.ScannerActivity;
import com.example.prova_tirocinio.adapters.DevicesAdapter;
import com.example.prova_tirocinio.databinding.DialogFragmentScannerSelectionBinding;

public class ScannerSelectionDialogFragment extends DialogFragment {

    private static final String TAG = "ScannerSelectionDialogF";

    DialogFragmentScannerSelectionBinding mBinding;

    private RecyclerView mDevicesRecyclerView;
    private DevicesAdapter mDeviceAdapter;
    private ProgressBar mProgressBar;

    private ScannerActivity mScannerActivity;


    public ScannerSelectionDialogFragment(DevicesAdapter devicesAdapter, ScannerActivity scannerActivity) {
        mDeviceAdapter=devicesAdapter;
        mScannerActivity=scannerActivity;
    }

    public static ScannerSelectionDialogFragment newInstance(String title, DevicesAdapter devicesAdapter, ScannerActivity scannerActivity) {
        ScannerSelectionDialogFragment frag = new ScannerSelectionDialogFragment(devicesAdapter, scannerActivity);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_fragment_scanner_selection, null, false);


        mDevicesRecyclerView=mBinding.recyclerDevDiscovered;
        mDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDevicesRecyclerView.setAdapter(mDeviceAdapter);

        String title = getArguments().getString("title");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(mBinding.getRoot());

        final AlertDialog dialog = alertDialogBuilder
                .setTitle(title)
                .setView(mBinding.getRoot())
                .setPositiveButton("Stop Scan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mScannerActivity.stopScanBTLE();
                        dismiss();
                    }
                })
                .show();

        mProgressBar=mBinding.progressBar;
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);

        return dialog;
    }

    public void setProgressBarVisibility(boolean state) {
        if(state)
            mProgressBar.setVisibility(View.VISIBLE);
        else
            mProgressBar.setVisibility(View.INVISIBLE);
    }
}
