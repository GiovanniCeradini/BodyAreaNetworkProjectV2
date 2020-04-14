package com.wagoo.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.wagoo.wgcom.BluetoothStatus
import com.wagoo.wgcom.R
import com.wagoo.wgcom.WagooGlassesInterface
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.content_update.*
import kotlin.concurrent.thread

class UpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        setSupportActionBar(toolbar)


        WagooGlassesInterface.bluetoothAutoInit(
                this.applicationContext) {

            this.window?.decorView?.rootView?.let {
                view ->
                Snackbar.make(view, "Connection status: " +
                        when (it) {
                            BluetoothStatus.DISCONNECTED -> "disconnected"
                            BluetoothStatus.CONNECTING -> "connecting"
                            BluetoothStatus.CONNECTED -> "connected"
                        }
                        , Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null)
                        .show()
            }



            thread {
                Thread.sleep(500)
                WagooGlassesInterface.ping_glasses()
            }
        }

        WagooGlassesInterface.register_ping_callback {
            masterVersion.text = if (it[0] == null) { "loading..." } else { "${it[0]!![0]}.${it[0]!![1]}.${it[0]!![2]}" }
            rightSlaveVersion.text = if (it[1] == null) { "loading..." } else { "${it[1]!![0]}.${it[1]!![1]}.${it[1]!![2]}" }
            leftSlaveVersion.text = if (it[2] == null) { "loading..." } else { "${it[2]!![0]}.${it[2]!![1]}.${it[2]!![2]}" }
        }
        thread {
            Thread.sleep(3000)
            WagooGlassesInterface.ping_glasses()
        }

        updateButton.setOnClickListener {

            masterProgressBar.progress = 0
            masterProgressBar.visibility = View.VISIBLE
            rightSlaveProgressBar.progress = 0
            rightSlaveProgressBar.visibility = View.VISIBLE
            leftSlaveProgressBar.progress = 0
            leftSlaveProgressBar.visibility = View.VISIBLE
            checkMaster.uncheck()
            checkRightSlave.uncheck()
            checkLeftSlave.uncheck()
            var completed = 0

            var rebootRequested = false
            WagooGlassesInterface.set_lights(0.7f, 500, false, true, false)

            WagooGlassesInterface.firmware_update_online(this) {
                chip, progress, chipRebootRequested ->
                if (chipRebootRequested) {
                    rebootRequested = true
                }

                val (progressBar , checkMark, versionText) = when (chip) {
                    0 -> Triple(masterProgressBar, checkMaster, masterVersion)
                    1 -> Triple(rightSlaveProgressBar, checkRightSlave, rightSlaveVersion)
                    2 -> Triple(leftSlaveProgressBar, checkLeftSlave, leftSlaveVersion)
                    else -> return@firmware_update_online
                }

                if (progress == 100 && progressBar.visibility == View.VISIBLE) {
                    versionText.text = "$progress%"
                    progressBar.visibility = View.INVISIBLE
                    runOnUiThread { checkMark.check() }
                    completed += 1
                    if (completed == 3) {
                        if (rebootRequested) {
                            WagooGlassesInterface.stop_request_version()
                            masterVersion.text = "rebooting..."
                            rightSlaveVersion.text = "rebooting..."
                            leftSlaveVersion.text = "rebooting..."
                            WagooGlassesInterface.reboot(1, 2000)
                            WagooGlassesInterface.reboot(2, 2000)
                            WagooGlassesInterface.reboot(0, 2000)
                            thread {
                                Thread.sleep(4000)
                                WagooGlassesInterface.firmware_version = mutableListOf(null, null, null)
                                WagooGlassesInterface.request_version()
                            }
                        }
                        else {
                            WagooGlassesInterface.request_version()
                            WagooGlassesInterface.set_lights(0.0f, 0, false, false, false)
                        }
                    }
                } else {
                    progressBar.progress = progress
                    versionText.text = "$progress%"
                }
            }
            WagooGlassesInterface.request_version()
        }

//        fab.setOnClickListener { view ->
//        }
        masterProgressBar.visibility = View.INVISIBLE
        rightSlaveProgressBar.visibility = View.INVISIBLE
        leftSlaveProgressBar.visibility = View.INVISIBLE
    }

}
