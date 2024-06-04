package dev.sandrocaseiro.sacocheiotv.services

class AuthService {
    private val apiService = ApiService()

    suspend fun login(username: String, password: String): String? {
        return apiService.authenticate(username, password)
    }
}