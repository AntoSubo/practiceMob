package ci.nsu.mobile.main.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.data.models.UserDto
import ci.nsu.mobile.main.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel(private val serviceLocator: ServiceLocator) : ViewModel() {
    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users: StateFlow<List<UserDto>> = _users

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            val result = serviceLocator.authRepository.getUsers()
            if (result.isSuccess) _users.value = result.getOrDefault(emptyList())
            _isLoading.value = false
        }
    }
}

@Composable
fun UsersTab(serviceLocator: ServiceLocator) {
    val viewModel: UsersViewModel = viewModel(factory = serviceLocator.viewModelFactory)
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(users) { user ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("ID: ${user.id}")
                        Text("Логин: ${user.login}")
                        Text("Email: ${user.email ?: "—"}")
                    }
                }
            }
        }
    }
}