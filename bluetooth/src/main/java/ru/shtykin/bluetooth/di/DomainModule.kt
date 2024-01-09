package ru.shtykin.bluetooth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.domain.usecase.CheckBluetoothStateUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothDevicesUseCase
import ru.shtykin.bluetooth.domain.usecase.StartDiscoveryUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {
    @Provides
    fun provideCheckBluetoothStateUseCase(repository: Repository): CheckBluetoothStateUseCase =
        CheckBluetoothStateUseCase(repository)

    @Provides
    fun provideGetBluetoothDevicesUseCase(repository: Repository): GetBluetoothDevicesUseCase =
        GetBluetoothDevicesUseCase(repository)

    @Provides
    fun provideStartDiscoveryUseCase(repository: Repository): StartDiscoveryUseCase =
        StartDiscoveryUseCase(repository)
}