package com.example.prova_tirocinio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prova_tirocinio.databinding.ThingyItemBinding;
import com.example.prova_tirocinio.databinding.WagooItemBinding;
import com.example.prova_tirocinio.objects.Device;


import java.util.List;


/**
 * Prova Holder e Adapter Per Recycler View dello Scanner dei Thingy.
 */
public class AdapterDialogFragment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_THINGY = 1;
    private static final int TYPE_WAGOO = 2;
    private static final int TYPE_WATCH = 3;

    private List<Device> devices;
//    private DeviceConnectedClickListener listener;

    public AdapterDialogFragment(List<Device> list){
        this.devices=list;
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

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //THINGY
        if (getItemViewType(position) == TYPE_THINGY){
            ((ThingyViewdHolder)holder).bind(devices.get(position));

        }

        //WAGOO
        else if (getItemViewType(position) == TYPE_WAGOO){
            ((WagooViewHolder)holder).bind(devices.get(position));
        }

        //SMARTWATCH
        else {

        }
    }

    @Override
    public int getItemCount() {
        return devices != null ? devices.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {

        //THINGY
        if (devices.get(position).getType() == 1)
            return TYPE_THINGY;

            //WAGOO
        else if (devices.get(position).getType() == 2)
            return TYPE_WAGOO;

            //SMARTWATCH
        else
            return 1;
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


}
