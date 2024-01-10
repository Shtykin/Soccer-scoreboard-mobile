package ru.shtykin.bluetooth.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.data.mapper.Mapper
import ru.shtykin.bluetooth.data.repository.RepositoryImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideRepository(mapper: Mapper, @ApplicationContext appContext: Context, bluetoothAdapter: BluetoothAdapter): Repository {
        return RepositoryImpl(mapper, appContext, bluetoothAdapter)
    }

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext appContext: Context): BluetoothAdapter {
        return (appContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    @Provides
    fun provideMapper(): Mapper {
        return Mapper()
    }

}