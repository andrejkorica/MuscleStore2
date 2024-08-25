package hr.unipu.musclestore.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.unipu.musclestore.utils.Base64Manager
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

class ProfileViewModel : ViewModel() {
    private val client = OkHttpClient()

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
                return@withContext "Exception: ${e.message}"
            }
        }
    }
}
