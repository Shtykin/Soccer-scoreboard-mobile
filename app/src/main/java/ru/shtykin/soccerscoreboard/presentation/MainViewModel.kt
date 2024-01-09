package ru.shtykin.soccerscoreboard.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.shtykin.bluetooth.domain.usecase.CheckBluetoothStateUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothDevicesUseCase
import ru.shtykin.bluetooth.domain.usecase.StartDiscoveryUseCase
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkBluetoothStateUseCase: CheckBluetoothStateUseCase,
    private val getBluetoothDevicesUseCase: GetBluetoothDevicesUseCase,
    private val startDiscoveryUseCase: StartDiscoveryUseCase
) : ViewModel() {

    private val _uiState =
        mutableStateOf<ScreenState>(
            ScreenState.SettingsScreen(
                temp = "temp"
            )
        )

    val uiState: State<ScreenState>
        get() = _uiState

    init {
        Log.e("DEBUG1", "bt -> ${checkBluetoothStateUseCase.execute()}")
    }

    fun getDevices() {
        Log.e("DEBUG1", "devices -> ${getBluetoothDevicesUseCase.execute()}")
    }

    fun startDiscovery() = startDiscoveryUseCase.execute()

}