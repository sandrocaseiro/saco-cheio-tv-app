package dev.sandrocaseiro.sacocheiotv.models.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sandrocaseiro.sacocheiotv.services.AuthService
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val authService = AuthService()

    val authHash = MutableLiveData<String?>()

    fun authenticate(username: String, password: String) {
        viewModelScope.launch {

            authHash.value = authService.login(username, password)
        }
    }
}
