package dev.notsatria.stop_pmo

import android.app.Application
import dev.notsatria.stop_pmo.di.databaseModule
import dev.notsatria.stop_pmo.di.repositoryModule
import dev.notsatria.stop_pmo.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class StopPMOApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.Forest.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@StopPMOApp)

            modules(
                databaseModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}