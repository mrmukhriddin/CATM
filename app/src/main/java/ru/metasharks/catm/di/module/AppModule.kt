package ru.metasharks.catm.di.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AppModule private constructor() {

    @Binds
    abstract fun bindApplicationContext(application: Application): Context
}
