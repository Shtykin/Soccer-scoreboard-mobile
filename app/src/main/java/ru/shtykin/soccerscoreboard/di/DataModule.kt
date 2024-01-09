package ru.shtykin.soccerscoreboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.shtykin.soccerscoreboard.data.mapper.Mapper
import ru.shtykin.soccerscoreboard.data.repository.RepositoryImpl
import ru.shtykin.soccerscoreboard.domain.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideRepository(): Repository {
        return RepositoryImpl()
    }

    @Provides
    fun provideMapper(): Mapper {
        return Mapper()
    }

}