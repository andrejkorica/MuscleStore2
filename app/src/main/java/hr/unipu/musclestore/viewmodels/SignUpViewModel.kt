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

class SignUpViewModel : ViewModel() {
    private val client = OkHttpClient()

    fun signUp(firstName: String, lastName: String, email: String, password: String, onTokenReceived: (String?) -> Unit) {
        viewModelScope.launch {
            val response = signUpRequest(firstName, lastName, email, password)
            Log.d("SignUpViewModel", "Sign up response: $response")
            val token = response?.let { JSONObject(it).optString("token") }
            onTokenReceived(token)
        }
    }

    private suspend fun signUpRequest(firstName: String, lastName: String, email: String, password: String): String {
        return withContext(Dispatchers.IO) {
            val json = JSONObject().apply {
                put("firstName", firstName)
                put("lastName", lastName)
                put("email", email)
                put("password", password)
            }
            val body: RequestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/users/register") // Make sure this URL is correct
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
                Log.e("SignUpViewModel", "Exception during sign-up request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }
}
