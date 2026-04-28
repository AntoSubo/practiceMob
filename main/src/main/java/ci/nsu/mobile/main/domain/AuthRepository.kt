package ci.nsu.mobile.main.domain

import ci.nsu.mobile.main.data.models.*
import ci.nsu.mobile.main.data.network.ApiService
import ci.nsu.mobile.main.data.network.PublicApiService
import ci.nsu.mobile.main.data.storage.TokenManager
import ci.nsu.mobile.main.data.network.NetworkModule
class AuthRepository(
    private val apiService: ApiService,
    private val publicApiService: PublicApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(login: String, password: String): Result<Unit> {
        return try {
            val response = publicApiService.login(LoginRequest(login, password))
            tokenManager.saveToken(response.token)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            // логирование
            val jsonString = NetworkModule.json.encodeToString(RegisterRequest.serializer(), request)
            android.util.Log.d("REGISTER_JSON", jsonString)

            val response = publicApiService.register(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Неизвестная ошибка"
                android.util.Log.e("REGISTER", "Ошибка ${response.code()}: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            android.util.Log.e("REGISTER", "Исключение", e)
            Result.failure(e)
        }
    }

    suspend fun getGroups(): Result<List<GroupDto>> {
        return try {
            Result.success(publicApiService.getGroups())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsers(): Result<List<UserDto>> {
        return try {
            Result.success(apiService.getUsers())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}