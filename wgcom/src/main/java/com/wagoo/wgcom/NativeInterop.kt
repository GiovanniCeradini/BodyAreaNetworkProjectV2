package com.wagoo.wgcom

data class FileParts(val parts_num: Byte, val parts: IntArray)
enum class ChipIdentity(value: Int) {
	Master(0),
	RightSlave(1),
	LeftSlave(2),
}
data class BeaconLight(val antenna: Byte, val wid: Byte, val timestamp: Long, val channel: Byte, val rssi: Byte, val crcok: Byte, val index: Int)
data class AccelGyroInfo(val timestamp: Long, val gyro: ShortArray, val accl: ShortArray)
data class LightMode(val intensity: Int, val frequency_in_ms: Int)
data class LightsConfig(val mode: LightMode, val red: Boolean, val yellow: Boolean, val green: Boolean)

internal abstract class InteropInit {
	protected external fun init()

}

internal abstract class NordicToKotlin {
	external fun process_data(data: ByteArray)

	abstract fun pong(chip: Byte, status: Int, major: Int, minor: Int, patch: Int)
	abstract fun file_request(path: String, fid: Int)
	abstract fun file_request_parts(fid: Int, parts: FileParts)
	abstract fun update_notify_progress(chip: Byte, progress: Byte)
	abstract fun collect_sensors_send(data: AccelGyroInfo)
	abstract fun collect_beacon_send(data: BeaconLight)
	abstract fun beacon_send_direction(wid: Byte, dir: Byte, accuracy: Byte, findex: Float, elapsed_us_since_last: Long)
}
internal abstract class KotlinToNordic {
	abstract fun send_data(data: ByteArray)

	external fun ping(chip: Byte)
	external fun update_request(master_path: String, slave_path: String)
	external fun file_send_info(fid: Int, checksum: Int, block_size: Int, file_size: Int)
	external fun file_send_part(fid: Int, checksum: Int, start: Int, xdata: ByteArray)
	external fun file_inform_all_packets_sent(fid: Int)
	external fun reboot(chip: Byte, delay: Int, magic: Int)
	external fun disable_collect_mode()
	external fun enable_collect_mode()
	external fun whitelist_add_uuid(wid: Byte, uuid: ByteArray)
	external fun whitelist_add_mac(wid: Byte, mac: ByteArray)
	external fun whitelist_remove(wid: Byte)
	external fun whitelist_clear_all()
	external fun enable_sensors_gathering()
	external fun disable_sensors_gathering()
	external fun set_lights(right_config: LightsConfig, left_config: LightsConfig)
}
