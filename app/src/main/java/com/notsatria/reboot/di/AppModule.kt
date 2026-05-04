package com.notsatria.reboot.di

import com.notsatria.reboot.data.local.AppDatabase
import com.notsatria.reboot.data.local.dao.RelapseDao
import com.notsatria.reboot.data.preference.SettingsDataStore
import com.notsatria.reboot.data.preference.settingsDataStore
import com.notsatria.reboot.data.repository.RelapseRepositoryImpl
import com.notsatria.reboot.domain.repository.RelapseRepository
import com.notsatria.reboot.ui.screen.analytics.AnalyticsViewModel
import com.notsatria.reboot.ui.screen.dashboard.DashboardViewModel
import com.notsatria.reboot.ui.screen.history.HistoryViewModel
import com.notsatria.reboot.ui.screen.onboarding.OnboardingViewModel
import com.notsatria.reboot.ui.screen.settings.SettingsViewModel
import com.notsatria.reboot.worker.StreakCheckWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
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
    viewModelOf(::AnalyticsViewModel)
    viewModelOf(::OnboardingViewModel)

    // Preference
    single<SettingsDataStore> {
        SettingsDataStore(dataStore = androidContext().settingsDataStore)
    }

    // Worker
    worker {
        StreakCheckWorker(get(), get())
    }
}