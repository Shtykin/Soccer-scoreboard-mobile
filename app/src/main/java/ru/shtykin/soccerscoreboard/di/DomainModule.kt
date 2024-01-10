package ru.shtykin.soccerscoreboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.shtykin.soccerscoreboard.domain.Repository

import ru.shtykin.soccerscoreboard.domain.usecase.GetGameUseCase
import ru.shtykin.soccerscoreboard.domain.usecase.SaveGameUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideGetGameUseCase(repository: Repository): GetGameUseCase =
        GetGameUseCase(repository)

    @Provides
    fun provideSaveGameUseCase(repository: Repository): SaveGameUseCase =
        SaveGameUseCase(repository)
}