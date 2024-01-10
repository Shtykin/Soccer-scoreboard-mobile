package ru.shtykin.bluetooth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.domain.usecase.BoundBluetoothDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.CheckBluetoothStateUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothDeviceFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBoundedBluetoothDevicesUseCase
import ru.shtykin.bluetooth.domain.usecase.GetIsBluetoothDiscoveringFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.StartDiscoveryUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {
    @Provides
    fun provideCheckBluetoothStateUseCase(repository: Repository): CheckBluetoothStateUseCase =
        CheckBluetoothStateUseCase(repository)

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
}