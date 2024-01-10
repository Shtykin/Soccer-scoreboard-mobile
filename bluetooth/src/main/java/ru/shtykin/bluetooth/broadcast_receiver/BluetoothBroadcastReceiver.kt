package ru.shtykin.bluetooth.broadcast_receiver

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.shtykin.bluetooth.Repository
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@AndroidEntryPoint
class BluetoothBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: Repository

    override fun onReceive(p0: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    goAsync{ repository.emitDeviceToFlow(device)}
                }
            }
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                goAsync { repository.stopDeviceFlow() }
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                goAsync { repository.stopDeviceFlow() }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    GlobalScope.launch(context) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}