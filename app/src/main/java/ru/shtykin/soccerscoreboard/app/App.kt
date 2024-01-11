package ru.shtykin.soccerscoreboard.app

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import dagger.hilt.android.HiltAndroidApp
import ru.shtykin.bluetooth.broadcast_receiver.BluetoothBroadcastReceiver

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        registerBroadcastReceiver()
    }

    private fun registerBroadcastReceiver() {
        val bluetoothReceiver = BluetoothBroadcastReceiver()
        val filter1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val filter2 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        val filter3 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(bluetoothReceiver, filter1)
        registerReceiver(bluetoothReceiver, filter2)
        registerReceiver(bluetoothReceiver, filter3)
    }
}