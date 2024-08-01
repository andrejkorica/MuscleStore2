package hr.unipu.musclestore.viewmodels

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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = loginRequest(email, password)
            Log.d("LoginViewModel", response)
        }
    }

    private suspend fun loginRequest(email: String, password: String): String {
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
                        response.body?.string() ?: "Success with empty body"
                    } else {
                        "Failed with response code ${response.code}, message: ${response.message}"
                    }
                }
            } catch (e: IOException) {
                Log.e("LoginViewModel", "Exception during login request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }
}
