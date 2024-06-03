package zfr.mobile.submissionsatu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import zfr.mobile.submissionsatu.welcome.WelcomeActivity

class SessionManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }

    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(KEY_AUTH_TOKEN)
        editor.apply()
        redirectToWelcome()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getString(KEY_AUTH_TOKEN, null) != null
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun redirectToWelcome() {
        val intent = Intent(context, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}
