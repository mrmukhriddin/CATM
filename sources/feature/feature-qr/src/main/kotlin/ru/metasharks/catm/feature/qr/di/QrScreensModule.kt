package ru.metasharks.catm.feature.qr.di

import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.core.navigation.screens.QrScannerScreen
import ru.metasharks.catm.feature.qr.ui.QrScannerActivity

@Module
@InstallIn(SingletonComponent::class)
abstract class QrScreensModule private constructor() {

    companion object {

        @Provides
        fun provideQrScannerScreen(): QrScannerScreen = QrScannerScreen {
            ActivityScreen { context ->
                Intent(context, QrScannerActivity::class.java)
            }
        }
    }
}
