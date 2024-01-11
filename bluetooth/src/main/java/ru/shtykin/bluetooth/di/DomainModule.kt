package ru.shtykin.bluetooth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.shtykin.bluetooth.domain.Repository
import ru.shtykin.bluetooth.domain.usecase.BoundBluetoothDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothStateFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.ConnectBtDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.DisconnectBtDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothDeviceFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothStateUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBoundedBluetoothDevicesUseCase
import ru.shtykin.bluetooth.domain.usecase.GetGameFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetGameUseCase
import ru.shtykin.bluetooth.domain.usecase.GetIsBluetoothDiscoveringFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.SaveGameUseCase
import ru.shtykin.bluetooth.domain.usecase.SendMessageUseCase
import ru.shtykin.bluetooth.domain.usecase.StartDiscoveryUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideGetBluetoothStateUseCase(repository: Repository): GetBluetoothStateUseCase =
        GetBluetoothStateUseCase(repository)

    @Provides
    fun provideGetBluetoothStateFlowUseCase(repository: Repository): GetBluetoothStateFlowUseCase =
        GetBluetoothStateFlowUseCase(repository)

    @Provides
    fun provideGetBoundedBluetoothDevicesUseCase(repository: Repository): GetBoundedBluetoothDevicesUseCase =
        GetBoundedBluetoothDevicesUseCase(repository)

    @Provides
    fun provideBoundBluetoothDeviceUseCase(repository: Repository): BoundBluetoothDeviceUseCase =
        BoundBluetoothDeviceUseCase(repository)

    @Provides
    fun provideGetBluetoothDeviceFlowUseCase(repository: Repository): GetBluetoothDeviceFlowUseCase =
        GetBluetoothDeviceFlowUseCase(repository)

    @Provides
    fun provideGetIsBluetoothDiscoveringFlowUseCase(repository: Repository): GetIsBluetoothDiscoveringFlowUseCase =
        GetIsBluetoothDiscoveringFlowUseCase(repository)

    @Provides
    fun provideStartDiscoveryUseCase(repository: Repository): StartDiscoveryUseCase =
        StartDiscoveryUseCase(repository)

    @Provides
    fun provideConnectBtDeviceUseCase(repository: Repository): ConnectBtDeviceUseCase =
        ConnectBtDeviceUseCase(repository)

    @Provides
    fun provideDisconnectBtDeviceUseCase(repository: Repository): DisconnectBtDeviceUseCase =
        DisconnectBtDeviceUseCase(repository)

    @Provides
    fun provideSendMessageUseCase(repository: Repository): SendMessageUseCase =
        SendMessageUseCase(repository)

    @Provides
    fun provideGetGameUseCase(repository: Repository): GetGameUseCase =
        GetGameUseCase(repository)

    @Provides
    fun provideSaveGameUseCase(repository: Repository): SaveGameUseCase =
        SaveGameUseCase(repository)

    @Provides
    fun provideGetGameFlowUseCase(repository: Repository): GetGameFlowUseCase =
        GetGameFlowUseCase(repository)
}