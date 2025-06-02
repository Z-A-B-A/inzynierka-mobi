package put.inf154030.frog.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SessionManager {
    private const val PREFS_NAME = "FrogAppPrefs"
    private const val KEY_TOKEN = "user_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthToken(token: String) {
        prefs.edit {
            putString(KEY_TOKEN, token)
        }
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUserInfo(userId: String, name: String, email: String) {
        prefs.edit {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
        }
    }

    fun saveUpdatedUserInfo(name: String, email: String) {
        prefs.edit {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
        }
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
}