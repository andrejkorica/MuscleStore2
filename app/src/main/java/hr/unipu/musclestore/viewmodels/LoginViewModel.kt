import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginViewModel : ViewModel() {
    private val client = OkHttpClient()

    fun login(email: String, password: String, onTokenReceived: (String?) -> Unit) {
        viewModelScope.launch {
            val response = loginRequest(email, password)
            val token = response?.let {
                try {
                    JSONObject(it).optString("token", null) // Return null if no token found
                } catch (e: Exception) {
                    Log.e("loginViewModel", "Failed to parse JSON response: ${e.message}")
                    null
                }
            }
            onTokenReceived(token)
        }
    }

    private suspend fun loginRequest(email: String, password: String): String? {
        return withContext(Dispatchers.IO) {
            val json = JSONObject().apply {
                put("email", email)
                put("password", password)
            }
            val body: RequestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/users/login")
                .post(body)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string() // Successful response, return body
                    } else {
                        Log.e("loginViewModel", "Login failed: ${response.code}, ${response.message}")
                        null // Return null if unsuccessful
                    }
                }
            } catch (e: IOException) {
                Log.e("loginViewModel", "Exception during login request: ${e.message}")
                null // Return null on exception
            }
        }
    }
}
