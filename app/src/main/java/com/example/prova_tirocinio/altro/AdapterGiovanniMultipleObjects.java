//package com.example.prova_tirocinio.altro;
//
//import android.bluetooth.BluetoothDevice;
//import android.graphics.PorterDuff;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import it.univr.esdlab.signalsserver.R;
//
///**
// * Parzialmente implementato da Giovanni, usalo come riferimento, adapter per più dispostivi
// */
//
//public class AdapterGiovanniMultipleObjects extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
//
//    //
//    private static final String TAG = "AdapterGiovanniMultipleObjects";
//    private static final int TYPE_THINGY = 1;
//    private static final int TYPE_WAGOO = 2;
//    private static final int TYPE_WATCH = 3;
//
//    //
//    private List<DeviceConnected> devices = new ArrayList<>();
//    private DeviceConnectedClickListener listener;
//
//    public AdapterGiovanniMultipleObjects(DeviceConnectedClickListener listener){
//        this.listener = listener;
//
//    }
//
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view;
//
//        //THINGY
//        if (viewType == TYPE_THINGY){
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thingy_item, parent, false);
//            return new DeviceConnectedHolder(view, listener);
//        }
//
//        //WAGOO
//        else if (viewType == TYPE_WAGOO){
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waggoo_item, parent, false);
//            return new WagooViewHolder(view);
//        }
//
//        //SMARTWATCH
//        else {
//
//        }
//        return null;
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//        //THINGY
//        if (getItemViewType(position) == TYPE_THINGY){
//            ((DeviceConnectedHolder) holder).setDevice(devices.get(position));
//        }
//
//        //WAGOO
//        else if (getItemViewType(position) == TYPE_WAGOO){
//            ((WagooViewHolder)holder).setDevice(devices.get(position));
//        }
//
//        //SMARTWATCH
//        else {
//
//        }
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return devices != null ? devices.size() : 0;
//    }
//
//
//    @Override
//    public int getItemViewType(int position) {
//
//        //THINGY
//        if (devices.get(position).getType() == 1)
//            return TYPE_THINGY;
//
//        //WAGOO
//        else if (devices.get(position).getType() == 2)
//            return TYPE_WAGOO;
//
//        //SMARTWATCH
//        else
//            return 1;
//    }
//
//
//
//    /**
//     *
//     * @param deviceList
//     */
//    public void setDevice(List<DeviceConnected> deviceList){
//        devices = deviceList;
//        notifyDataSetChanged();
//    }
//
//
//    /**
//     *
//     */
//    public class DeviceConnectedHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
//                                                                                View.OnLongClickListener{
//
//        private TextView deviceName;
//        private TextView deviceAddress;
//        private TextView deviceFrequency;
//        private TextView deviceBattery;
//        private ImageView icon;
//        private ImageView rssi;
//        private RelativeLayout deviceContainer;
//
//        private DeviceConnected device;
//        private DeviceConnectedClickListener listener;
//
//        public DeviceConnectedHolder(@NonNull View itemView, DeviceConnectedClickListener listener) {
//            super(itemView);
//
//            //SET ITEM
//            deviceFrequency = itemView.findViewById(R.id.device_frequency);
//            deviceBattery = itemView.findViewById(R.id.device_battery);
//            deviceName = itemView.findViewById(R.id.device_name);
//            deviceAddress = itemView.findViewById(R.id.device_address);
//            icon = itemView.findViewById(R.id.icon);
//            rssi = itemView.findViewById(R.id.rssi);
//            deviceContainer = itemView.findViewById(R.id.device_container);
//            rssi.setVisibility(View.GONE);
//            //int newColor = itemView.getResources().getColor(R.color.nordicLake);
//            //icon.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
//
//            //SET LISTENER
//            this.listener = listener;
//            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
//        }
//
//
//        /**
//         *
//         * @param device
//         */
//        public void setDevice(DeviceConnected device){
//            this.device = device;
//            deviceName.setText(device.getName());
//            deviceAddress.setText(device.getAddress());
//        }
//
//        @Override
//        public void onClick(View view) {
//            listener.onClick(view, getAdapterPosition(), device);
//        }
//
//        @Override
//        public boolean onLongClick(View view) {
//           listener.onLongClick(view, getAdapterPosition(), device);
//           return true;
//        }
//    }
//
//
//    /**
//     ViewHolder per i glassesWagoo
//     */
//    public class WagooViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
//                                                                                View.OnLongClickListener{
//
//        private TextView deviceName;
//        private TextView deviceAddress;
//        private TextView deviceFrequency;
//        private TextView deviceBattery;
//        private ImageView icon;
//        private RelativeLayout deviceContainer;
//
//        private DeviceConnected deviceConnected;
//
//        public WagooViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            //SET ITEM
//           // deviceFrequency = itemView.findViewById(R.id.device_frequency);
//           // deviceBattery = itemView.findViewById(R.id.device_battery);
//            deviceName = itemView.findViewById(R.id.wagoo_name);
//            deviceAddress = itemView.findViewById(R.id.wagoo_address);
//            icon = itemView.findViewById(R.id.wagoo_icon);
//            deviceContainer = itemView.findViewById(R.id.wagoo_container);
//
//        }
//
//
//        public void setDevice(DeviceConnected device){
//            this.deviceConnected = device;
//            deviceName.setText(device.getName());
//            deviceName.setText(device.getAddress());
//        }
//
//        @Override
//        public void onClick(View view) {
//
//        }
//
//        @Override
//        public boolean onLongClick(View view) {
//            return false;
//        }
//    }
//}
