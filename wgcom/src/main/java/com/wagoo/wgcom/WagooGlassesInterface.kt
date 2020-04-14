package com.wagoo.wgcom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.wagoo.activities.UpdateActivity
import java.net.URL
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread


object WagooGlassesInterface {

    private val REBOOT_CONSTANT = 0x3EB007;
    private val sendSemaphore = Semaphore(1)
    private val widQueue = MutableList(256) { it }
    internal var firmware_version = mutableListOf<IntArray?>(
            null, null, null
    )

    var sendPacketCallback: ((ByteArray) -> Unit)? = null

    private var versionThread: Thread? = null

    fun bluetoothAutoInit(context: Context, onConnectionChange: ((BluetoothStatus) -> Unit)? = null) {
        if (BluetoothManager.status == BluetoothStatus.DISCONNECTED) {
            BluetoothManager.auto_connect_to_bonded(context, onConnectionChange)
        }
    }

    fun init(sendDataCallback: (ByteArray) -> Unit, addGaiaHeader: Boolean = true, msBetweenSends: Long = 75) {
        sendPacketCallback = {
            packet ->

            sendSemaphore.acquire()
            try {
                if (addGaiaHeader) {
                    val gaiaPacket = byteArrayOf(
                        -1,                     // 255
                        1,                      // version
                        0,                      // flags
                        packet.size.toByte(),   // length
                        0,
                        0xb,                    // vendor id (Wagoo)
                        0x0,
                        0x10,                   // command id (not used)
                        *packet
                    )
                    sendDataCallback(gaiaPacket)
                } else {
                    sendDataCallback(packet)
                }
                Thread.sleep(msBetweenSends)
            }
            finally {
                sendSemaphore.release()
            }
        }
    }

    fun process_data(data: ByteArray, stripGaia: Boolean = true) {
        NordicToKotlinImpl.process_data(
            if (stripGaia) {
                if (data.size < 9 + 12) return
                data.sliceArray(9 until data.size)
            } else {
                data
            }
        )
    }

    fun firmware_update_from_data(master: ByteArray?, slave: ByteArray?, progress: (Int, Int) -> Unit) {
        Updater.start_firmware_update(master, slave, progress)
        on_ping_update()
        firmware_version = mutableListOf(null, null, null)
    }

    fun firmware_update_from_embedded_resources(context: Context, master: Boolean, slave: Boolean, progress: (Int, Int) -> Unit) {
        Updater.start_firmware_update(context, master, slave, progress)
        on_ping_update()
        firmware_version = mutableListOf(null, null, null)
    }

    fun check_for_updates(context: Context) {

        thread {
            try {
                var updateSlave = false
                var updateMaster = false

                val url = "https://guilucand.xyz/wagoo"

                val version_master_str = URL("$url/version_master").readText().trim()
                val version_master = version_master_str.split("_").map { it.toInt() }

                val version_slave_str = URL("$url/version_slave").readText().trim()
                val version_slave = version_slave_str.split("_").map { it.toInt() }

                wait_for_chip_version()

                if (firmware_version[0]?.contentEquals(version_master.toIntArray()) != true) {
                    updateMaster = true
                }
                if (firmware_version[1]?.contentEquals(version_slave.toIntArray()) != true) {
                    updateSlave = true
                }
                if (firmware_version[2]?.contentEquals(version_slave.toIntArray()) != true) {
                    updateSlave = true
                }

                if (updateMaster || updateSlave) {
                    val intent = Intent(context, UpdateActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                    startActivity(context, intent, null)

                }

            } catch (ex: Exception) {

            }
        }
    }

    private fun wait_for_chip_version() {
        request_version()
        while (firmware_version.any { it == null }) Thread.sleep(1000)
        stop_request_version()
    }


    fun firmware_update_online(activity: Activity?, progress: (Int, Int, Boolean) -> Unit) {
        thread {
            try {
                val url = "https://guilucand.xyz/wagoo"

                val version_master_str = URL("$url/version_master").readText().trim()
                val version_master = version_master_str.split("_").map { it.toInt() }

                wait_for_chip_version()

                var updateMaster = false
                if (firmware_version[0]?.contentEquals(version_master.toIntArray()) != true) {
                    updateMaster = true
                }

                var masterFirmware: ByteArray? = null
                if (updateMaster) {
                    val major = version_master[0]
                    val minor = version_master[1]
                    val patch = version_master[2]
                    activity?.let {
                        activity.runOnUiThread {
                            Toast.makeText(it, "Downloading master firmware version $major.$minor.$patch", Toast.LENGTH_SHORT).show()
                        }
                        Thread.sleep(1000)
                    }

                    masterFirmware = URL(
                            "$url/master_${version_master_str}"
                    ).readBytes()
                }
                else {
                    progress(0, 100, false)
                    KotlinToNordicImpl.ping(0)

                    activity?.let {
                        it.runOnUiThread {
                            Toast.makeText(it, "Master version: ${version_master[0]}.${version_master[1]}.${version_master[2]}", Toast.LENGTH_SHORT).show()
                        }
                        Thread.sleep(1000)
                    }
                }

                var updateSlave = false
                val version_slave_str = URL("$url/version_slave").readText().trim()
                val version_slave = version_slave_str.split("_").map { it.toInt() }

                if (firmware_version[1]?.contentEquals(version_slave.toIntArray()) != true) {
                    updateSlave = true
                }
                if (firmware_version[2]?.contentEquals(version_slave.toIntArray()) != true) {
                    updateSlave = true
                }

                firmware_version = mutableListOf(null, null, null)

                var slaveFirmware: ByteArray? = null
                if (updateSlave) {
                    val major = version_slave[0]
                    val minor = version_slave[1]
                    val patch = version_slave[2]
                    activity?.let {
                        it.runOnUiThread {
                            Toast.makeText(it, "Downloading slave firmware version $major.$minor.$patch", Toast.LENGTH_SHORT).show()
                        }
                        Thread.sleep(1000)
                    }

                    slaveFirmware = URL(
                            "$url/slave_${version_slave_str}"
                    ).readBytes()
                }
                else {
                    progress(1, 100, false)
                    progress(2, 100, false)
                    activity?.let {
                        it.runOnUiThread {
                            Toast.makeText(it, "Slave version: ${version_slave[0]}.${version_slave[1]}.${version_slave[2]}", Toast.LENGTH_SHORT).show()
                        }
                        Thread.sleep(1000)
                    }
                    KotlinToNordicImpl.ping(1)
                    KotlinToNordicImpl.ping(2)
                }
                if (masterFirmware != null || slaveFirmware != null) {
                    Updater.start_firmware_update(masterFirmware, slaveFirmware) {
                        chip, progress ->
                        progress(chip, progress, true)
                    }
                }
                on_ping_update()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private var requestVersion = false

    internal fun stop_request_version() {
        requestVersion = false
        versionThread?.interrupt()
    }

    internal fun request_version() {
        requestVersion = true
        if (versionThread?.isAlive != true) {
            versionThread = thread {
                try {
                    while (requestVersion) {//(firmware_version.count { it == null } > 0) {
                        for ((index, version) in firmware_version.iterator().withIndex()) {
                            if (version == null) {
                                KotlinToNordicImpl.ping(index.toByte())
                            }
                        }
                        Thread.sleep(1500)
                    }
                }
                catch (ex: Exception) {

                }
            }
        }
    }

    fun ping_glasses() {
        KotlinToNordicImpl.ping(0)
        KotlinToNordicImpl.ping(1)
        KotlinToNordicImpl.ping(2)
    }

    fun set_lights(intensity: Float, blink_time_ms: Int?, red: Boolean, yellow: Boolean, green: Boolean) {

        val config = LightsConfig(
                LightMode((intensity * 100).toInt(),
                        blink_time_ms ?: 0),
                red,
                yellow,
                green)

        KotlinToNordicImpl.set_lights(config, config)
    }

    fun set_both_lights(right_intensity: Float,
                        right_blink_time_ms: Int?,
                        right_red: Boolean,
                        right_yellow: Boolean,
                        right_green: Boolean,

                        left_intensity: Float,
                        left_blink_time_ms: Int?,
                        left_red: Boolean,
                        left_yellow: Boolean,
                        left_green: Boolean)
    {

        val rightConfig = LightsConfig(
                LightMode((right_intensity * 100).toInt(),
                        right_blink_time_ms ?: 0),
                right_red,
                right_yellow,
                right_green)

        val leftConfig = LightsConfig(
                LightMode((left_intensity * 100).toInt(),
                        left_blink_time_ms ?: 0),
                left_red,
                left_yellow,
                left_green)

        KotlinToNordicImpl.set_lights(rightConfig, leftConfig)
    }

    fun enable_collect_mode() {
        KotlinToNordicImpl.enable_collect_mode()
    }

    fun disable_collect_mode() {
        KotlinToNordicImpl.disable_collect_mode()
    }

    fun enable_sensors() {
        KotlinToNordicImpl.enable_sensors_gathering()
    }

    fun disable_sensors() {
        KotlinToNordicImpl.disable_sensors_gathering()
    }

    private fun getNextWid(): Int? {
        val wid = widQueue.getOrNull(0) ?: return null
        widQueue.removeAt(0)
        return wid
    }

    fun whitelist_add_mac(mac: ByteArray): Int? {
        if (mac.size != 6) return null
        val wid = getNextWid() ?: return null

        KotlinToNordicImpl.whitelist_add_mac(wid.toByte(), mac)
        return wid
    }

    fun whitelist_add_uuid(uuid: ByteArray): Int? {
        if (uuid.size != 16) return null
        val wid = getNextWid() ?: return null

        KotlinToNordicImpl.whitelist_add_uuid(wid.toByte(), uuid)
        return wid
    }

    fun whitelist_delete_wid(wid: Int) {
        if (widQueue.contains(wid)) return
        KotlinToNordicImpl.whitelist_remove(wid.toByte())
        widQueue.add(wid)
    }

    fun whitelist_clear() {
        KotlinToNordicImpl.whitelist_clear_all()
    }

    private var sensors_callback: ((AccelGyroInfo) -> Unit)? = null
    private var beacons_callback: ((BeaconLight) -> Unit)? = null
    private var direction_callback: ((Int, Int, Int, Float, Long) -> Unit)? = null
    private var ping_callback: ((MutableList<IntArray?>) -> Unit)? = null

    internal fun on_sensors_data(data: AccelGyroInfo) {
        sensors_callback?.invoke(data)
    }

    internal fun on_beacons_data(data: BeaconLight) {
        beacons_callback?.invoke(data)
    }

    internal fun on_direction_data(wid: Int, direction: Int, progress: Int, findex: Float, ms_since_last: Long) {
        direction_callback?.invoke(wid, direction, progress, findex, ms_since_last)
    }

    internal fun on_ping_update() {
        ping_callback?.invoke(firmware_version)
    }

    fun reboot(chip: Int, delay: Int) {
        KotlinToNordicImpl.reboot(chip.toByte(), delay, REBOOT_CONSTANT)
    }

    fun register_collect_sensors_callback(callback: (AccelGyroInfo) -> Unit) {
        sensors_callback = callback
    }

    fun register_collect_beacons_callback(callback: (BeaconLight) -> Unit) {
        beacons_callback = callback
    }

    fun register_direction_update_callback(callback: (Int, Int, Int, Float, Long) -> Unit) {
        direction_callback = callback
    }

    fun register_ping_callback(callback: (MutableList<IntArray?>) -> Unit) {
        ping_callback = callback
    }

}