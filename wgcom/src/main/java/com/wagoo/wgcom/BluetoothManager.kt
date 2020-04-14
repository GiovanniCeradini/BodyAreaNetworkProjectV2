package com.wagoo.wgcom

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


enum class BluetoothStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

internal object BluetoothManager {
    private val SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val GAIA = UUID.fromString("00001107-D102-11E1-9B23-00025B00A5A5")
    private var connectionThread: Thread? = null
    private var communicationThread: Thread? = null
    private var aliveThread: Thread? = null

    fun startKeepAliveThread() {
        if (aliveThread?.isAlive != true) {
            aliveThread = Thread(Runnable {
                while (status == BluetoothStatus.CONNECTED) {
                    // Use an unknown chip id to make nordic ignore it
                    KotlinToNordicImpl.ping(4)
                    try {
                        Thread.sleep(3000)
                    } catch (ex: Exception) {

                    }
                }
            })
            aliveThread!!.start()
        }
    }

    private var device: BluetoothDevice? = null
    private var socket: BluetoothSocket? = null
    private var connectionChangeCallback: ((BluetoothStatus) -> Unit)? = null
    private fun selectUuid(uuids: Array<ParcelUuid>): UUID? {
        for (parcel in uuids) {
            if (parcel.uuid == SPP || parcel.uuid == GAIA) {
                return parcel.uuid
            }
        }
        return null
    }


    var status = BluetoothStatus.DISCONNECTED
    private set(value) {
        if (field != value) {
            connectionChangeCallback?.invoke(value)
            field = value
            if (value == BluetoothStatus.CONNECTED) {
                startKeepAliveThread()
            }
        }
    }

    fun auto_connect_to_bonded(context: Context, connectionChangeCallback: ((BluetoothStatus) -> Unit)? = null) {
        val adapter = BluetoothAdapter.getDefaultAdapter()

        //if Bluetooth is not enable you must enable it, does not work

        /*
        if (adapter == null || !adapter.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        */

//        adapter.startDiscovery()
        for (device in adapter.bondedDevices) {
            if (device.name.toLowerCase().contains("wagoohpglasses")) {
                Toast.makeText(context, "Connecting to " + device.name, Toast.LENGTH_SHORT).show()
                Log.d("BluetoothManager","Connecting to " + device.name)
                connect(context, device, {
                    WagooGlassesInterface.process_data(it, true)
                }, connectionChangeCallback)
                WagooGlassesInterface.init({
                    send_data(it)
                })
                return
            }
            else{

            }
        }
        //device is not in bondedDevices
        return
    }

    private fun listen(context: Context, device: BluetoothDevice, socket: BluetoothSocket, callback: (ByteArray) -> Unit) {
        status = BluetoothStatus.CONNECTING
        communicationThread?.let {
            it.interrupt()
        }

        communicationThread = thread {
            try {
                status = BluetoothStatus.CONNECTED
                inputLoop@ while (status == BluetoothStatus.CONNECTED) {
                    val bytes = ByteArray(2048)
                    when (val result = socket.inputStream.read(bytes)) {
                        0 -> continue@inputLoop
                        -1 -> {
                            status = BluetoothStatus.DISCONNECTED
                            socket.close()
                            continue@inputLoop
                        }
                        else -> {
                            callback(bytes.sliceArray(0 until result))
                        }
                    }
                }
            }
            catch (ex: Exception) {

            }
            status = BluetoothStatus.DISCONNECTED
            Thread.sleep(1000)
            connect(context, device, callback, this.connectionChangeCallback)

        }
    }

    fun send_data(data: ByteArray): Boolean {
        socket?.outputStream?.let {
            it.write(data)
            it.flush()
            return true
        }
        return false
    }

    fun connect(context: Context, device: BluetoothDevice, callback: (ByteArray) -> Unit, connectionChangeCallback: ((BluetoothStatus) -> Unit)? = null) {
        this.connectionChangeCallback = connectionChangeCallback

        connectionThread = thread {
            try {
                status = BluetoothStatus.CONNECTING
                //?
                device.fetchUuidsWithSdp()

                val lock = Semaphore(1)

                //critical section, lock is a semaphore

                lock.acquire()

                context.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        lock.release()
                    }
                }, IntentFilter(BluetoothDevice.ACTION_UUID))

                if (lock.tryAcquire(10, TimeUnit.SECONDS)) {
                    selectUuid(device.uuids)?.let { uuid ->
                        socket = device.createInsecureRfcommSocketToServiceRecord(uuid)
                        socket?.let {
                            it.connect()
                            listen(context, device, it, callback)
                        }
                        return@thread
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            status = BluetoothStatus.DISCONNECTED
            Thread.sleep(2000)
            connect(context, device, callback, connectionChangeCallback)
        }
    }

}