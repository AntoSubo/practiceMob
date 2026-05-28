package ci.nsu.mobile.main.di

import android.content.Context
import ci.nsu.mobile.main.data.local.AppDatabase
import ci.nsu.mobile.main.data.network.NetworkModule
import ci.nsu.mobile.main.domain.AuthRepository
import ci.nsu.mobile.main.domain.DepositRepository

class ServiceLocator(context: Context) {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    val authRepository: AuthRepository by lazy {
        AuthRepository(
            NetworkModule.provideApiService(),
            NetworkModule.providePublicApiService()
        )
    }
    val depositRepository: DepositRepository by lazy {
        DepositRepository(database.depositDao())
    }
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }
}