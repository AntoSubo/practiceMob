package ci.nsu.mobile.main.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavController
import ci.nsu.mobile.main.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepTwoScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val currentRate = viewModel.interestRate.toString()

    Scaffold(topBar = { TopAppBar(title = { Text("Шаг 2: Доп. параметры") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = "$currentRate%",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Доступная процентная ставка") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("$currentRate%") },
                        onClick = { expanded = false }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.monthlyTopUp,
                onValueChange = { viewModel.monthlyTopUp = it },
                label = { Text("Ежемесячное пополнение (необязательно)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Назад")
                }
                Button(onClick = {
                    val validationError = viewModel.validateStepTwo()
                    if (validationError == null) {
                        viewModel.calculateResult()
                        navController.navigate("result")
                    } else {
                        Toast.makeText(context, validationError, Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Рассчитать")
                }
            }
        }
    }
}
