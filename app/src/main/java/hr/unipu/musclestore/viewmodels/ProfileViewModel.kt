package hr.unipu.musclestore.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import hr.unipu.musclestore.utils.Base64Manager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayInputStream
import java.io.IOException

class ProfileViewModel : ViewModel() {
    private val client = OkHttpClient()

    // Fetch user data
    fun fetchUserData(
        context: Context,
        callback: (User?, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)  // Retrieve the token from TokenManager

        viewModelScope.launch {
            val response = fetchUserDataRequest(token)
            if (response != null) {
                callback(response, null)
            } else {
                callback(null, "Failed to fetch user data.")
            }
        }
    }

    private suspend fun fetchUserDataRequest(token: String?): User? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/users/me")
                .get()
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    Log.d("ProfileViewModel", "Response Code: ${response.code}")
                    Log.d("ProfileViewModel", "Response Message: ${response.message}")

                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: return@use null
                        return@use parseUserFromJson(responseBody)
                    } else {
                        null
                    }
                }
            } catch (e: IOException) {
                Log.e("ProfileViewModel", "Exception during fetch user data request: ${e.message}")
                null
            }
        }
    }

    private fun parseUserFromJson(json: String): User? {
        val gson = Gson()

        return try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val firstName = jsonObject.get("firstName")?.asString
            val email = jsonObject.get("email")?.asString
            val profilePicture = jsonObject.get("profilePicture")?.asString

            if (firstName != null && email != null) {
                User(
                    userId = 0, // Placeholder, since userId is not needed here
                    firstName = firstName,
                    lastName = "", // Not needed for this view
                    email = email,
                    password = "", // Not needed for this view
                    profilePicture = profilePicture
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Exception during JSON parsing: ${e.message}")
            null
        }
    }

    // Upload profile picture
    fun uploadProfilePicture(
        context: Context,
        bitmap: Bitmap,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)  // Retrieve the token from TokenManager

        viewModelScope.launch {
            val response = uploadProfilePictureRequest(bitmap, token)
            val success = !response.isNullOrEmpty()
            callback(success, response)
        }
    }

    private suspend fun uploadProfilePictureRequest(
        bitmap: Bitmap,
        token: String?
    ): String? {
        return withContext(Dispatchers.IO) {
            // Encode bitmap to Base64 string
            val base64Image = Base64Manager.encodeBitmapToBase64(bitmap)
            Log.d("ProfileViewModel", "Encoded Base64 Image: $base64Image")

            val body: RequestBody = base64Image.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            // Replace with your actual URL
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/users/profile-picture")
                .post(body)
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    Log.d("ProfileViewModel", "Response Code: ${response.code}")
                    Log.d("ProfileViewModel", "Response Message: ${response.message}")
                    return@use if (response.isSuccessful) {
                        response.body?.string()
                    } else {
                        "Failed with response code ${response.code}, message: ${response.message}"
                    }
                }
            } catch (e: IOException) {
                Log.e("ProfileViewModel", "Exception during upload profile picture request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

    // Decode Base64 image
    fun decodeBase64Image(base64: String): Bitmap? {
        return Base64Manager.decodeBase64ToBitmap(base64)
    }
}

// Data model for user
data class User(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val profilePicture: String?  // Base64 encoded string
)
