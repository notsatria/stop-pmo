package com.notsatria.reboot

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.notsatria.reboot.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.Forest.plant(Timber.DebugTree())
            Logger.addLogAdapter(AndroidLogAdapter())
        }

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}