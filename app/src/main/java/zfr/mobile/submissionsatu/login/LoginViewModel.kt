package zfr.mobile.submissionsatu.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zfr.mobile.submissionsatu.SessionManager
import zfr.mobile.submissionsatu.api.ApiService
import zfr.mobile.submissionsatu.api.response.login.LoginResponse

class LoginViewModel(
    private val sessionManager: SessionManager,
    private val apiService: ApiService
) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> get() = _loginResult

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _loginError

    fun login(email: String, password: String) {
        apiService.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        sessionManager.saveAuthToken(it.loginResult.token)
                        _loginResult.value = it
                    }
                } else {
                    _loginError.value = "Login failed: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginError.value = "Network error: ${t.message}"
            }
        })
    }
}
