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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.R;
import com.example.prova_tirocinio.adapters.DevicesAdapter;
import com.example.prova_tirocinio.databinding.FragmentMainBinding;
import com.example.prova_tirocinio.objects.Device;

import java.util.List;



public class MainFragment extends Fragment implements DevicesAdapter.onDeviceListener{

    private static final String TAG = "FragmentTestingRecViewP";

    private FragmentMainBinding mBinding;
    private Button mButton;

    private List<Device> mSelectedDevices;
    private RecyclerView mDevicesSelectedRecyclerView;
    private DevicesAdapter mSelectedDevicesAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View v = mBinding.getRoot();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDevicesSelectedRecyclerView=mBinding.recyclerView;
        mButton=mBinding.showDialogFragmentBtn;

        mDevicesSelectedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //TODO perchè c'è mThingySdkManager, indagare
//        mDevicesSelectedAdapter = new DevicesSelectedAdapter(mDevicesSelected, mThingySdkManager, this);
        mSelectedDevicesAdapter = new DevicesAdapter(mSelectedDevices,this);
        mDevicesSelectedRecyclerView.setAdapter(mSelectedDevicesAdapter);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment mDialogFragmentTesting= new DevicesFoundDialogFragment();
                mDialogFragmentTesting.show(getFragmentManager(), "DevicesFoundDialogFragment");
            }
        });
    }


//    public static void addDeviceToRecyclerView(Device device){
//        mSelectedDevices.add(device);
//        mSelectedDevicesAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onDeviceClick(int position) {

    }
}
