package zfr.mobile.submissionsatu.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zfr.mobile.submissionsatu.api.ApiService
import zfr.mobile.submissionsatu.api.response.register.RegisterResponse

class RegisterViewModel(private val apiService: ApiService) : ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> get() = _registerResult

    private val _registerError = MutableLiveData<String>()
    val registerError: LiveData<String> get() = _registerError

    fun register(name: String, email: String, password: String) {
        apiService.register(name, email, password).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                } else {
                    _registerError.value = "Registration failed: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _registerError.value = "Network error: ${t.message}"
            }
        })
    }
}
