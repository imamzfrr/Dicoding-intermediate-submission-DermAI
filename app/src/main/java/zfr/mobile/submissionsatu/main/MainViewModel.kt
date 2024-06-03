package zfr.mobile.submissionsatu.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import zfr.mobile.submissionsatu.SessionManager

class MainViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    fun checkLoginStatus() {
        if (!sessionManager.isLoggedIn()) {
            _logoutEvent.value = true
        }
    }

    fun logout() {
        sessionManager.clearAuthToken()
        _logoutEvent.value = true
    }
}