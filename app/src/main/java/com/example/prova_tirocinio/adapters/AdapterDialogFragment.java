package com.example.prova_tirocinio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.databinding.ThingyItemBinding;
import com.example.prova_tirocinio.databinding.WagooItemBinding;
import com.example.prova_tirocinio.databinding.WatchItemBinding;
import com.example.prova_tirocinio.fragments.MainFragment;
import com.example.prova_tirocinio.objects.Device;
import com.example.prova_tirocinio.objects.ThingyDevice;
import com.example.prova_tirocinio.objects.WagooDevice;
import com.wagoo.wgcom.WagooGlassesInterface;


import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.thingylib.Thingy;
import no.nordicsemi.android.thingylib.utils.ThingyUtils;


/**
 * Prova Holder e Adapter Per Recycler View dello Scanner dei Thingy.
 */
public class AdapterDialogFragment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_THINGY = 1;
    private static final int TYPE_WAGOO = 2;
    private static final int TYPE_WATCH = 3;

    private List<Device> mDevices;
    private onDeviceListener mOnDeviceListener;
//    private DeviceConnectedClickListener listener;

    public AdapterDialogFragment(List<Device> list, onDeviceListener onDeviceListener){
        this.mDevices=list;
        this.mOnDeviceListener=onDeviceListener;
    }

    public AdapterDialogFragment(List<Device> list){
        this.mDevices=list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        //THINGY
        if (viewType == TYPE_THINGY){
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thingy_item, parent, false);
//            return new ThingyViewdHolder(view, listener);
//            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//            ThingyItemBinding thingyBinding = ThingyItemBinding.inflate(layoutInflater, parent, false);
//            return new ThingyViewdHolder(thingyBinding);
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ThingyItemBinding thingyBinding = ThingyItemBinding.inflate(layoutInflater, parent, false);
            return new ThingyViewdHolder(thingyBinding);
        }

        //WAGOO
        else if (viewType == TYPE_WAGOO){
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wagoo_item, parent, false);
//            return new WagooViewHolder(view);
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            WagooItemBinding wagooBinding = WagooItemBinding.inflate(layoutInflater, parent, false);
            return new WagooViewHolder(wagooBinding);
        }

        //SMARTWATCH
        else {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            WatchItemBinding watchBinding = WatchItemBinding.inflate(layoutInflater, parent, false);
            return new WatchViewHolder(watchBinding,mOnDeviceListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //THINGY
        if (getItemViewType(position) == TYPE_THINGY){
            ((ThingyViewdHolder)holder).bind(mDevices.get(position));

        }

        //WAGOO
        else if (getItemViewType(position) == TYPE_WAGOO){
            ((WagooViewHolder)holder).bind(mDevices.get(position));
        }

        //SMARTWATCH
        else {
            ((WatchViewHolder)holder).bind(mDevices.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDevices != null ? mDevices.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {

        //THINGY
        if (mDevices.get(position).getType() == 1)
            return TYPE_THINGY;

            //WAGOO
        else if (mDevices.get(position).getType() == 2)
            return TYPE_WAGOO;

            //SMARTWATCH
        else
            return TYPE_WATCH;
    }



    public void update(List<ScanResult> results) {
        for (final ScanResult result : results) {
            //TODO cerco se c'è già tra quelli già connessi nella lista che imposterò
            final Device device = findDevice(result);
            if (device == null) {
                if(result.getDevice().getUuids().equals(ThingyUtils.THINGY_BASE_UUID))
                    mDevices.add(new ThingyDevice(result));
//                if(result.getDevice().getUuids().equals(WagooGlassesInterface.getUuid()))
//                    mDevices.add(new WagooDevice(result));
            } else {
                //TODO quando avviene questo? cosa vuol dire la prima riga? ci serve o no l'rssi senno lo togliamo -> toglibile
                device.setName(result.getScanRecord() != null ? result.getScanRecord().getDeviceName() : null);
//                device.setRssi(result.getRssi());
            }
        }
        notifyDataSetChanged();
    }

    private Device findDevice(final ScanResult result) {
        for (final Device device : mDevices){
            if (device.matchesResultDevice(result))
                return device;
            }
        return null;
    }


    /**
     ViewHolder per i Thingy
     */

    public class ThingyViewdHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        private Device mThingyDevice;
        private ThingyItemBinding mThingyItemBinding;

        public ThingyViewdHolder(ThingyItemBinding binding) {
            super(binding.getRoot());
            this.mThingyItemBinding = binding;
        }

        public void bind(Device thingyDevice) {
            mThingyDevice=thingyDevice;
            mThingyItemBinding.setDevide(thingyDevice);
        }


        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }




    /**
    ViewHolder per i glasses Wagoo
     */

    public class WagooViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        private Device mWagooDevice;
        private WagooItemBinding mWagooItemBinding;

        /*
        TODO da togliere, la batteria non dovrebbe essere ottenibile, gli altri si devono settare in un'altra recyclerView solo quando vengono selezionati
        private TextView deviceAddress;
        private TextView deviceFrequency;
        private TextView deviceBattery;
         */


        public WagooViewHolder(WagooItemBinding binding) {
            super(binding.getRoot());
            this.mWagooItemBinding = binding;
        }

        public void bind(Device wagooDevice) {
            this.mWagooDevice=mWagooDevice;
            mWagooItemBinding.executePendingBindings();
            mWagooItemBinding.setDevice(wagooDevice);
        }


        @Override
        public void onClick(View view) {

        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }

    /**
     ViewHolder per lo smartwatch in caso
     */

    public class WatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        private Device mWatchDevice;
        private WatchItemBinding mWatchItemBinding;
        private onDeviceListener onDeviceListener;



        public WatchViewHolder(WatchItemBinding binding, onDeviceListener listener) {
            super(binding.getRoot());
            this.mWatchItemBinding = binding;
            onDeviceListener=listener;

            /**
             * impostando il listener al viewholder
             */
            itemView.setOnClickListener(this);
        }

        public void bind(Device watchDevice) {
            this.mWatchDevice=watchDevice;
            mWatchItemBinding.executePendingBindings();
            mWatchItemBinding.setDevice(watchDevice);
        }


        @Override
        public void onClick(View view) {
            onDeviceListener.onDeviceClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }

    /**
    Interfaccia per l'onClick sul singolo item
     */

    public interface onDeviceListener{
        void onDeviceClick(int position);
    }


}
