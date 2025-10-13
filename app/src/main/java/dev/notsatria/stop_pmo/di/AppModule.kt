package dev.notsatria.stop_pmo.di

import dev.notsatria.stop_pmo.data.local.AppDatabase
import dev.notsatria.stop_pmo.data.local.dao.RelapseDao
import dev.notsatria.stop_pmo.data.preference.SettingsDataStore
import dev.notsatria.stop_pmo.data.preference.settingsDataStore
import dev.notsatria.stop_pmo.data.repository.RelapseRepositoryImpl
import dev.notsatria.stop_pmo.domain.repository.RelapseRepository
import dev.notsatria.stop_pmo.ui.screen.dashboard.DashboardViewModel
import dev.notsatria.stop_pmo.ui.screen.history.HistoryViewModel
import dev.notsatria.stop_pmo.ui.screen.settings.SettingsViewModel
import dev.notsatria.stop_pmo.worker.StreakCheckWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    factory<RelapseDao> {
        get<AppDatabase>().relapseDao()
    }

    single<AppDatabase> {
        AppDatabase.getDatabase(context = androidContext())
    }

    single<RelapseRepository> {
        RelapseRepositoryImpl(dao = get())
    }

    viewModelOf(::DashboardViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::SettingsViewModel)

    // Preference
    single<SettingsDataStore> {
        SettingsDataStore(dataStore = androidContext().settingsDataStore)
    }

    // Worker
    worker {
        StreakCheckWorker(get(), get())
    }
}