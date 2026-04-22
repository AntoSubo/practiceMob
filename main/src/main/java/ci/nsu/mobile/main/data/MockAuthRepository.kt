package ci.nsu.mobile.main.data

import ci.nsu.mobile.main.data.models.Group
import ci.nsu.mobile.main.data.models.User
import kotlinx.coroutines.delay

class MockAuthRepository {

    private suspend fun wait() { delay(500) }

    suspend fun login(login: String, password: String): Result<String> {
        wait()
        return if (login.isNotBlank() && password.isNotBlank())
            Result.Success("mock_token_123")
        else
            Result.Error("Логин и пароль не могут быть пустыми")
    }

    suspend fun getUsers(): Result<List<User>> {
        wait()
        return Result.Success(listOf(
            User(1, "ivanov", "ivanov@mail.ru", "Иван Иванов"),
            User(2, "petrova", "petrova@mail.ru", "Петрова Анна")
        ))
    }

    suspend fun getGroups(): Result<List<Group>> {
        wait()
        return Result.Success(listOf(
            Group(1, "Android"), Group(2, "iOS"), Group(3, "Backend")
        ))
    }

    suspend fun register(
        login: String,
        password: String,
        email: String,
        phone: String,
        firstName: String,
        lastName: String
    ): Result<Unit> {
        wait()
        return if (login.isNotBlank() && password.length >= 4)
            Result.Success(Unit)
        else
            Result.Error("Проверьте логин и пароль (мин. 4 символа)")
    }

    fun logout() { }
}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}