package dev.notsatria.stop_pmo.di

import dev.notsatria.stop_pmo.data.local.AppDatabase
import dev.notsatria.stop_pmo.data.local.dao.RelapseDao
import dev.notsatria.stop_pmo.data.repository.RelapseRepositoryImpl
import dev.notsatria.stop_pmo.domain.repository.RelapseRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    factory<RelapseDao> {
        get<AppDatabase>().relapseDao()
    }

    single<AppDatabase> {
        AppDatabase.getDatabase(context = androidContext())
    }
}

val repositoryModule = module {
    single<RelapseRepository> {
        RelapseRepositoryImpl(dao = get())
    }
}