package ci.nsu.mobile.main.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.data.models.PersonDto
import ci.nsu.mobile.main.data.models.RegisterRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf<Int?>(null) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.success) {
        if (state.success) {
            Toast.makeText(context, "Успешно", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
            viewModel.resetSuccess()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Регистрация", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Имя") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Фамилия") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text("Отчество") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = birthDate, onValueChange = { birthDate = it }, label = { Text("Дата рождения") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Пол") }, modifier = Modifier.fillMaxWidth())

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = state.groups.find { it.id == groupId }?.name ?: "",
                onValueChange = {}, readOnly = true,
                label = { Text("Группа") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                state.groups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group.name) },
                        onClick = { groupId = group.id; expanded = false }
                    )
                }
            }
        }

        OutlinedTextField(value = login, onValueChange = { login = it }, label = { Text("Логин") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Телефон") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val person = PersonDto(firstName, lastName, middleName, birthDate, gender, groupId ?: 0)
                viewModel.register(RegisterRequest(login, password, email, phone, person = person))
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && groupId != null
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Зарегистрироваться")
        }

        if (state.isLoadingGroups) CircularProgressIndicator(modifier = Modifier.size(24.dp))
        state.groupsError?.let { Text("Ошибка: $it", color = MaterialTheme.colorScheme.error) }
    }
}