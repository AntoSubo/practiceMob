package ci.nsu.mobile.main

import android.app.Application
import ci.nsu.mobile.main.data.local.AppDatabase
import ci.nsu.mobile.main.domain.DepositRepository

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(this)
        DepositRepository.init(database)
    }
}