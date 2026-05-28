package ci.nsu.mobile.main.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ci.nsu.mobile.main.ui.auth.LoginViewModel
import ci.nsu.mobile.main.ui.auth.RegisterViewModel
import ci.nsu.mobile.main.ui.main.MyCalculationsViewModel
import ci.nsu.mobile.main.ui.main.NewCalculationViewModel
import ci.nsu.mobile.main.ui.main.UsersViewModel

class ViewModelFactory(private val locator: ServiceLocator) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(locator.authRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(locator.authRepository) as T
            modelClass.isAssignableFrom(UsersViewModel::class.java) ->
                UsersViewModel(locator) as T
            modelClass.isAssignableFrom(MyCalculationsViewModel::class.java) ->
                MyCalculationsViewModel(locator) as T
            modelClass.isAssignableFrom(NewCalculationViewModel::class.java) ->
                NewCalculationViewModel(locator) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}