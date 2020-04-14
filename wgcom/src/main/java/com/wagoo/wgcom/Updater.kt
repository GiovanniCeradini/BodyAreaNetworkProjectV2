package com.wagoo.wgcom

import android.content.Context
import android.util.Log
import java.util.zip.CRC32
import kotlin.concurrent.thread
import kotlin.math.min

object Updater {

    private const val block_size = 192
    var files_id: HashMap<Int, ByteArray> = HashMap()
    var files: HashMap<String, ByteArray> = HashMap()
    var progressCallback: ((Int, Int) -> Unit)? = null
    var transmitting = false

    var files_list: MutableList<Pair<Int, String>> = mutableListOf()

    fun start_firmware_update(master: ByteArray?, slave: ByteArray?, progress: (Int, Int) -> Unit) {
        progressCallback = progress
        KotlinToNordicImpl.update_request(
            master?.let {
                files["master"] = it
                "master"
            } ?: "",
            slave?.let {
                files["slave"] = it
                "slave"
            } ?: "")

    }

    fun start_firmware_update(context: Context, master: Boolean, slave: Boolean, progress: (Int, Int) -> Unit) {
        var masterFirmware: ByteArray? = null
        var slaveFirmware: ByteArray? = null
        if (master) {
            masterFirmware = context.resources.openRawResource(R.raw.signed_master).readBytes()
        }
        if (slave) {
            slaveFirmware = context.resources.openRawResource(R.raw.signed_slave).readBytes()
        }
        start_firmware_update(masterFirmware, slaveFirmware, progress)
    }

    fun send_firmware(fid: Int, path: String) {

        val file = files[path] ?: return
        files_id.set(fid, file)

        var globalCrc = CRC32()
        globalCrc.update(file)

        KotlinToNordicImpl.file_send_info(fid, globalCrc.value.toInt(), block_size, file.size)

        if (!transmitting) {
            transmitting = true
            thread {
                var i = 0
                while (i < file.size) {
                    var crc = CRC32()
                    val slice = file.sliceArray(i until min(i + block_size, file.size))

                    crc.update(slice)
                    // FIXME: Check why randomizing packets leads to a corrupted firmware!
                    KotlinToNordicImpl.file_send_part(fid, crc.value.toInt(), i, slice)
                    Log.d("UPDATER", "Sent block ${i / block_size}")
                    i += block_size
                }
                KotlinToNordicImpl.file_inform_all_packets_sent(fid)
                // Just to be sure FIXME!
                KotlinToNordicImpl.file_inform_all_packets_sent(fid)
            }
        }
        else {
            if (!files_list.contains(fid to path)) {
                files_list.add(fid to path)
            }
        }
    }

    fun send_missing_blocks(fid: Int, blocks: IntArray) {

        val file = files_id[fid] ?: return

        for (block in blocks) {
            val address = block * block_size
            var crc = CRC32()
            val slice = file.sliceArray(address until min(address+block_size, file.size))

            crc.update(slice)
            KotlinToNordicImpl.file_send_part(fid, crc.value.toInt(), address, slice)
            Log.d("UPDATER", "Resent block $block")
        }
        KotlinToNordicImpl.file_inform_all_packets_sent(fid)
    }

    fun notify_progress(chip: Byte, progress: Byte) {

        if (progress == 100.toByte()) {
            transmitting = false
            if (files_list.isNotEmpty()) {
                val (fid, file) = files_list.get(0)
                files_list.removeAt(0)
                send_firmware(fid, file)
            }
        }

        progressCallback?.invoke(chip.toInt(), progress.toInt())
    }
}