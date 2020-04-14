package com.wagoo.wgcom

import android.util.Log

internal object InteropInitImpl : InteropInit() {
    init {
        System.loadLibrary("android_native_protocol")
        init()
    }
    fun create() {} // Used to lazy initialize the object
}


internal object NordicToKotlinImpl : NordicToKotlin() {

    init {
        InteropInitImpl.create()
    }

    override fun pong(chip: Byte, status: Int, major: Int, minor: Int, patch: Int) {
        WagooGlassesInterface.firmware_version[chip.toInt()] = intArrayOf(major, minor, patch)
        WagooGlassesInterface.on_ping_update()
        Log.d("PONG", "Version of chip $chip: $major.$minor.$patch")
    }

    override fun file_request(path: String, fid: Int) {
        Updater.send_firmware(fid, path)
    }

    override fun file_request_parts(fid: Int, parts: FileParts) {
        Updater.send_missing_blocks(fid, parts.parts.sliceArray(0 until parts.parts_num))
    }

    override fun update_notify_progress(chip: Byte, progress: Byte) {
        Updater.notify_progress(chip, progress)
    }

    override fun collect_sensors_send(data: AccelGyroInfo) {
        WagooGlassesInterface.on_sensors_data(data)
    }

    override fun collect_beacon_send(data: BeaconLight) {
        WagooGlassesInterface.on_beacons_data(data)
    }

    override fun beacon_send_direction(wid: Byte, dir: Byte, accuracy: Byte, findex: Float, elapsed_us_since_last: Long) {
        WagooGlassesInterface.on_direction_data(wid.toInt(), dir.toInt(), accuracy.toInt(), findex, elapsed_us_since_last / 1000)
    }
}

internal object KotlinToNordicImpl : KotlinToNordic() {

    init {
        InteropInitImpl.create()
    }

    override fun send_data(data: ByteArray) {
        WagooGlassesInterface.sendPacketCallback?.invoke(data)
    }
}