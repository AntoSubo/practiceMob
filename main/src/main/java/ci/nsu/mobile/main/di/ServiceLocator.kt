package ci.nsu.mobile.main.di

import android.content.Context
import ci.nsu.mobile.main.data.local.AppDatabase
import ci.nsu.mobile.main.data.network.NetworkModule
import ci.nsu.mobile.main.domain.AuthRepository

class ServiceLocator {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl() }
    val depositRepository: DepositRepository by lazy { DepositRepositoryImpl(database.depositDao()) }
    val viewModelFactory: ViewModelFactory by lazy { ViewModelFactory(this) }
}