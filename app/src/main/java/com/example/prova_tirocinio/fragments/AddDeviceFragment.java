//package com.example.prova_tirocinio.fragments;
//
//
//import android.Manifest;
//import android.app.Dialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.Settings;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.databinding.DataBindingUtil;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.prova_tirocinio.R;
//import com.example.prova_tirocinio.databinding.FragmentAddDeviceBinding;
//import com.example.prova_tirocinio.databinding.FragmentProvaBinding;
//import com.example.prova_tirocinio.interfaces.ScannerFragmentListener;
//import com.example.prova_tirocinio.objects.ThingyDevice;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.wagoo.wgcom.BluetoothStatus;
//import com.wagoo.wgcom.WagooGlassesInterface;
//
//import java.util.ArrayList;
//
//import it.univr.esdlab.signalsserver.R;
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//import no.nordicsemi.android.thingylib.ThingyListener;
//import no.nordicsemi.android.thingylib.ThingyListenerHelper;
//import no.nordicsemi.android.thingylib.ThingySdkManager;
//import no.nordicsemi.android.thingylib.utils.ThingyUtils;
//import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
//
///**
// * Fragment principale per la connessione verso Nordic Thingy.
// */
//public class AddDeviceFragment extends Fragment implements ScannerFragmentListener,
//        PermissionRationaleDialogFragment.PermissionDialogListener,
//        ThingySdkManager.ServiceConnectionListener {
//
//    private static final String PROGRESS_DIALOG_TAG = "PROG_DIALOG";
//    private static final int REQUEST_ENABLE_BT = 1020;
//    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1021;
//    private ThingySdkManager mThingySdkManager;
//    private ScannerFragment mScannerFragment;
//    private ProgressDialogFragment mProgressDialog;
//    private ThingyService.ThingyBinder mBinder;
//    private FloatingActionButton fabScan;
//    private Handler mProgressHandler = new Handler();
//    private RecyclerView mDevicesSelectedRecyclerView;
//    private ArrayList<ThingyDevice> mDevicesSelected = new ArrayList<>();
//    private DevicesSelectedAdapter mDevicesSelectedAdapter;
//    private SingleNordicFragment singleNordicFragment = null;
//    private MaterialTapTargetPrompt.Builder builder;
//
//    //binding
//    FragmentAddDeviceBinding mBinding;
//
//    private static final String TAG = "AddDeviceFragment";
//
//    //TODO da togliere, da mettere nella recyclerview
//    //Glasses
//    private RelativeLayout mGlassLayout;
//    private TextView mGlassNameTextView;
//    private TextView mGlassAddressTextView;
//    private TextView mGlassOnOffTextView;
//    private ImageView mGlassImageIconTextView;
//
//
//
//
//    public AddDeviceFragment() {
//    }
//
//    public ArrayList<ThingyDevice> getmDevicesSelected() {
//        if (mDevicesSelected == null)
//            return null;
//        else
//            return mDevicesSelected;
//    }
//
//
//    private ThingyListener mThingyListener = new ThingyListener() {
//        /**
//         * Chiamato quando la connessione a un Thingy avviene con successo.
//         * @param device
//         * @param connectionState
//         */
//        @Override
//        public void onDeviceConnected(BluetoothDevice device, int connectionState) {
//            updateProgressDialogState(getString(R.string.state_discovering_services, device.getName()));
//        }
//
//        /**
//         * Chiamato quando un Thingy si disconnette.
//         * @param device
//         * @param connectionState
//         */
//        @Override
//        public void onDeviceDisconnected(BluetoothDevice device, int connectionState) {
//            mDevicesSelected.remove(new ThingyDevice(device));
//            mDevicesSelectedAdapter.notifyDataSetChanged();
//        }
//
//        /**
//         * Chiamato quando vengono scoperti i servizi di un Thingy. (non ancora connesso).
//         * @param device
//         */
//        @Override
//        public void onServiceDiscoveryCompleted(BluetoothDevice device) {
//
//            mDevicesSelected.add(new ThingyDevice(device));
//            mDevicesSelectedAdapter.notifyDataSetChanged();
//            hideProgressDialog();
//        }
//
//        @Override
//        public void onBatteryLevelChanged(BluetoothDevice bluetoothDevice, int batteryLevel) {
//
//        }
//
//        @Override
//        public void onTemperatureValueChangedEvent(BluetoothDevice bluetoothDevice, String temperature) {
//
//        }
//
//        @Override
//        public void onPressureValueChangedEvent(BluetoothDevice bluetoothDevice, String pressure) {
//
//        }
//
//        @Override
//        public void onHumidityValueChangedEvent(BluetoothDevice bluetoothDevice, String humidity) {
//
//        }
//
//        @Override
//        public void onAirQualityValueChangedEvent(BluetoothDevice bluetoothDevice, int eco2, int tvoc) {
//
//        }
//
//        @Override
//        public void onColorIntensityValueChangedEvent(BluetoothDevice bluetoothDevice, float red, float green, float blue, float alpha) {
//
//        }
//
//        @Override
//        public void onButtonStateChangedEvent(BluetoothDevice bluetoothDevice, int buttonState) {
//
//        }
//
//        @Override
//        public void onTapValueChangedEvent(BluetoothDevice bluetoothDevice, int direction, int count) {
//
//
//        }
//
//        @Override
//        public void onOrientationValueChangedEvent(BluetoothDevice bluetoothDevice, int orientation) {
//
//        }
//
//        @Override
//        public void onQuaternionValueChangedEvent(BluetoothDevice bluetoothDevice, float w, float x, float y, float z) {
//        }
//
//        @Override
//        public void onPedometerValueChangedEvent(BluetoothDevice bluetoothDevice, int steps, long duration) {
//
//        }
//
//        @Override
//        public void onAccelerometerValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {
//
//        }
//
//        @Override
//        public void onGyroscopeValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {
//
//        }
//
//        @Override
//        public void onCompassValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {
//
//        }
//
//        @Override
//        public void onEulerAngleChangedEvent(BluetoothDevice bluetoothDevice, float roll, float pitch, float yaw) {
//
//        }
//
//        @Override
//        public void onRotationMatrixValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] matrix) {
//
//        }
//
//        @Override
//        public void onHeadingValueChangedEvent(BluetoothDevice bluetoothDevice, float heading) {
//
//        }
//
//        @Override
//        public void onGravityVectorChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {
//
//        }
//
//        @Override
//        public void onSpeakerStatusValueChangedEvent(BluetoothDevice bluetoothDevice, int status) {
//
//        }
//
//        @Override
//        public void onMicrophoneValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] data) {
//
//        }
//    };
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_device, container, false);
//        View v = mBinding.getRoot();
//
//        return v;
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
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add your Thingy:52");
//    }
//
//    /*
//    TODO toglierli in quanto devono essere visualizzati nella recycleview
//    */
//    private void setTextGlasse(String text){
//        mGlassOnOffTextView.setText(text);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mThingySdkManager = ThingySdkManager.getInstance();
//        mScannerFragment = ScannerFragment.getInstance(ThingyUtils.THINGY_BASE_UUID);
//
//        //Glasses
//        /*
//        TODO toglierli in quanto devono essere visualizzati nella recycleview
//         */
//
//        mGlassLayout=view.findViewById((R.id.glasses_layout));
//        mGlassNameTextView=view.findViewById(R.id.glass_name);
//        mGlassAddressTextView=view.findViewById(R.id.glass_address);
//        mGlassOnOffTextView=view.findViewById(R.id.glass_on_off);
//        mGlassImageIconTextView=view.findViewById(R.id.glass_icon);
//
//        //this.getApplicationContext()
//
//        //TODO da togliere, in attessa di manager wagoo
////
////        WagooGlassesInterface.INSTANCE.bluetoothAutoInit(
////                getActivity().getApplicationContext(), new Function1<BluetoothStatus, Unit>() {
////
////                    @Override
////                    public Unit invoke(BluetoothStatus status) {
////                        String statusStr = "";
////                        switch (status) {
////                            case DISCONNECTED: {
////                                statusStr = "disconnected";
////                                Log.d(TAG, "invoke: disconnected");
////                                //glassOnOff.setText("OFF");
////
////                                //AddDeviceFragment.this.setTextGlasse("disconnected");
////                            }
////                            break;
////                            case CONNECTING: {
////                                statusStr = "connecting...";
////
////                                //AddDeviceFragment.this.setTextGlasse("connecting...");
////                            }
////                            break;
////                            case CONNECTED: {
////                                statusStr = "connected";
////                                //glassOnOff.setText("ON");
////                                //MainActivity.this.onGlassesConnection();
////                                //AddDeviceFragment.this.setTextGlasse("connected");
////
////
////                                //Log.d(TAG, "invoke: connected");
////                                //WagooGlassesInterface.INSTANCE.check_for_updates(getContext());
////                            }
////                            break;
////                        }
////
////                        /*
////                        Snackbar.make(MainActivity.this.getWindow().getDecorView().getRootView(),
////                                "Connection status: " + statusStr,
////                                Snackbar.LENGTH_SHORT)
////                                .show();
////
////                         */
////
////                        return Unit.INSTANCE;
////                    }
////
////
////
////                });
////
//
//        // Imposto la recycler View dei device a cui mi sono connesso.
//        mDevicesSelectedRecyclerView=mBinding.recyclerDevSelected;
////        mDevicesSelectedRecyclerView = getView().findViewById(R.id.recycler_dev_selected);
//        mDevicesSelectedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mDevicesSelectedAdapter = new DevicesSelectedAdapter(mDevicesSelected, mThingySdkManager, this);
//        mDevicesSelectedRecyclerView.setAdapter(mDevicesSelectedAdapter);
//
//        // Impostazione Fab button per lo scan dei Thingy.
//        fabScan=mBinding.scanDeviceFab;
////        fabScan = getView().findViewById(R.id.scan_device_fab);
//        fabScan.show();
//        fabScan.setOnClickListener(v -> {
//            fabScan.setEnabled(false);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                    if (isLocationEnabled()) {
//                        if (isBleEnabled()) {
//                            mScannerFragment.setTargetFragment(this, 0);
//                            /*lancio il fragment*/
//                            mScannerFragment.show(getActivity().getSupportFragmentManager(), null);
//
//                        } else enableBle();
//                    } else {
//                        Toast.makeText(getActivity(), getString(R.string.location_services_disabled), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    final PermissionRationaleDialogFragment dialog = PermissionRationaleDialogFragment.getInstance(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_ACCESS_COARSE_LOCATION, getString(R.string.rationale_message_location));
//                    dialog.show(getActivity().getSupportFragmentManager(), null);
//                }
//            } else {
//                if (isBleEnabled()) {
//                    //Dialog Fragment per visualizzare risultati scan bluetooth.
//                    mScannerFragment.show(getActivity().getSupportFragmentManager(), null);
//
//                } else enableBle();
//            }
//        });
//
//        //Settaggio dell'indicatore sul fab scan
//        builder = new MaterialTapTargetPrompt.Builder(getActivity());
//        builder.setTarget(R.id.scan_device_fab);
//        builder.setPrimaryText("Connect to your Thingy:52");
//        builder.setSecondaryText("to begin analyzing motion data.");
//        builder.setBackgroundColour(getResources().getColor(R.color.nordicLake));
//        builder.show();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (!isBleEnabled()) {
//            enableBle();
//        }
//        mThingySdkManager.bindService(getContext(), ThingyService.class);
//        ThingyListenerHelper.registerThingyListener(getContext(), mThingyListener);
//        getActivity().registerReceiver(mBleStateChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mThingySdkManager.unbindService(getActivity());
//        ThingyListenerHelper.unregisterThingyListener(getActivity(), mThingyListener);
//        getActivity().unregisterReceiver(mBleStateChangedReceiver);
//
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull final Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//    }
//
//    @Override
//    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        switch (requestCode) {
//            case REQUEST_ENABLE_BT:
//                if (resultCode != getActivity().RESULT_OK) {
//                    if (mScannerFragment != null && mScannerFragment.isVisible()) {
//                        mScannerFragment.dismiss();
//                    }
//                    // finish();
//                }
//                break;
//            default:
//                super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    @Override
//    public void onRequestPermission(final String permission, final int requestCode) {
//        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
//    }
//
//    @Override
//    public void onCancellingPermissionRationale() {
//        Toast.makeText(getActivity(), getString(R.string.requested_permission_not_granted_rationale), Toast.LENGTH_SHORT).show();
//    }
//
//    private boolean isBleEnabled() {
//        final BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
//        return ba != null && ba.isEnabled();
//    }
//
//    private void enableBle() {
//        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//    }
//
//    /**
//     * Connessione verso il device selzionato dalla lista di device risultanti dallo scan.
//     *
//     * @param device the selected device. May not be null.
//     */
//    @Override
//    public void onDeviceSelected(ThingyDevice device) {
//        fabScan.setEnabled(true);
//        if (mThingySdkManager != null) {
//            mThingySdkManager.connectToThingy(getContext(), device.getDevice(), ThingyService.class);
//        }
//        showConnectionProgressDialog(getString(R.string.state_connecting));
//    }
//
//    private void showConnectionProgressDialog(final String message) {
//
//        if (mProgressDialog != null) {
//            if (!mProgressDialog.isAdded()) {
//                mProgressDialog = ProgressDialogFragment.newInstance(message);
//                mProgressDialog.setCancelable(false);
//                mProgressDialog.show(getActivity().getSupportFragmentManager(), PROGRESS_DIALOG_TAG);
//            }
//        } else {
//
//            mProgressDialog = ProgressDialogFragment.newInstance(message);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.show(getActivity().getSupportFragmentManager(), PROGRESS_DIALOG_TAG);
//        }
//
//        mProgressHandler.postDelayed(mProgressDialogRunnable, 15000);
//    }
//
//    private final Runnable mProgressDialogRunnable = new Runnable() {
//        public void run() {
//            hideProgressDialog();
//        }
//    };
//
//    private void updateProgressDialogState(String message) {
//        if (mProgressDialog != null) {
//            final Dialog dialog = mProgressDialog.getDialog();
//            if (dialog != null) {
//                mProgressDialog.setMessage(message);
//            }
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                getActivity().onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    private void hideProgressDialog() {
//        if (mProgressDialog != null) {
//            final Dialog dialog = mProgressDialog.getDialog();
//            if (dialog != null) {
//                dialog.dismiss();
//            }
//        }
//    }
//
//    @Override
//    public void onNothingSelected() {
//
//    }
//
//    @Override
//    public void onServiceConnected() {
//        mBinder = (ThingyService.ThingyBinder) mThingySdkManager.getThingyBinder();
//    }
//
//    final BroadcastReceiver mBleStateChangedReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            final String action = intent.getAction();
//            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
//                        BluetoothAdapter.ERROR);
//                switch (state) {
//                    case BluetoothAdapter.STATE_OFF:
//                        enableBle();
//                        break;
//                }
//            }
//        }
//    };
//
//    public boolean isLocationEnabled() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            int locationMode = Settings.Secure.LOCATION_MODE_OFF;
//            try {
//                locationMode = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
//            } catch (final Settings.SettingNotFoundException e) {
//                // do nothing
//            }
//            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
//        }
//        return true;
//    }
//
//    /**
//     * Viusalizzazione live dei dati registrati dai sensori nordic.
//     *
//     * @param device device selezionato, dalla lista di device connessi.
//     */
//    public void showDataDevice(ThingyDevice device) {
//        mThingySdkManager.setSelectedDevice(device.getDevice());
//        mDevicesSelectedRecyclerView.setVisibility(View.GONE);
//        fabScan.hide();
//        singleNordicFragment = new SingleNordicFragment();
//        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, singleNordicFragment).addToBackStack(null).commit();
//    }
//
//
//    public boolean disconectDevice(ThingyDevice device) {
//        mThingySdkManager.disconnectFromThingy(device.getDevice());
//        return true;
//    }
//
//    public void disconectAllDevices(ArrayList<ThingyDevice> devices) {
//        for (ThingyDevice device : devices) {
//            disconectDevice(device);
//        }
//    }
//
//}
//
