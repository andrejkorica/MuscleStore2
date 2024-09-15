package hr.unipu.musclestore.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import hr.unipu.musclestore.data.User
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
            try {
                val response = fetchUserDataRequest(token)

                // Log whether response is successful or not
                if (response != null) {
                    callback(response, null)
                } else {
                    callback(null, "Failed to fetch user data.")
                }
            } catch (e: Exception) {
                // Log any exception that occurs during the process
                Log.e("ProfileViewModel", "Exception during fetching user data: ${e.message}")
                callback(null, "Exception during fetching user data: ${e.message}")
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
                Log.d("ProfileViewModel", "Sending request to: ${request.url}")

                client.newCall(request).execute().use { response ->
                    // Log response details
                    Log.d("ProfileViewModel", "Response Code: ${response.code}")
                    Log.d("ProfileViewModel", "Response Message: ${response.message}")

                    // Log response body if not null
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        Log.d("ProfileViewModel", "Response Body: $responseBody")
                    } else {
                        Log.d("ProfileViewModel", "Response Body is null")
                    }

                    // Parse and return user data
                    if (response.isSuccessful) {
                        return@use responseBody?.let { parseUserFromJson(it) }
                    } else {
                        null
                    }
                }
            } catch (e: IOException) {
                Log.e("ProfileViewModel", "Exception during fetch user data request: ${e.message}", e)
                null
            }
        }
    }

    private fun parseUserFromJson(json: String): User? {
        val gson = Gson()

        return try {
            Log.d("ProfileViewModel", "Parsing JSON: $json")

            // Convert JSON string to JsonObject
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            Log.d("ProfileViewModel", "Parsed JSON Object: $jsonObject")

            // Extract fields from the JSON object
            val firstName = jsonObject.get("firstName")?.takeIf { !it.isJsonNull }?.asString
            val email = jsonObject.get("email")?.takeIf { !it.isJsonNull }?.asString
            val profilePicture = jsonObject.get("profilePicture")?.takeIf { !it.isJsonNull }?.asString

            if (firstName != null && email != null) {
                User(
                    userId = 0, // Placeholder, since userId is not needed here
                    firstName = firstName,
                    lastName = "", // Not needed for this view
                    email = email,
                    password = "", // Not needed for this view
                    profilePicture = profilePicture
                ).also {
                    Log.d("ProfileViewModel", "Successfully created User object: $it")
                }
            } else {
                Log.e("ProfileViewModel", "Required fields are missing: firstName or email is null")
                null
            }
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Exception during JSON parsing: ${e.message}", e)
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

    class ProfileViewModel : ViewModel() {
        private val client = OkHttpClient()

        // Fetch user data
        fun fetchUserData(
            context: Context,
            callback: (User?, String?) -> Unit
        ) {
            val token = TokenManager.getToken(context)  // Retrieve the token from TokenManager
            viewModelScope.launch {
                try {
                    val response = fetchUserDataRequest(token)

                    // Log whether response is successful or not
                    if (response != null) {
                        callback(response, null)
                    } else {
                        callback(null, "Failed to fetch user data.")
                    }
                } catch (e: Exception) {
                    // Log any exception that occurs during the process
                    Log.e("ProfileViewModel", "Exception during fetching user data: ${e.message}")
                    callback(null, "Exception during fetching user data: ${e.message}")
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
                    Log.d("ProfileViewModel", "Sending request to: ${request.url}")

                    client.newCall(request).execute().use { response ->
                        // Log response details
                        Log.d("ProfileViewModel", "Response Code: ${response.code}")
                        Log.d("ProfileViewModel", "Response Message: ${response.message}")

                        // Return user data if successful
                        if (response.isSuccessful) {
                            response.body?.string()?.let {
                                parseUserFromJson(it)
                            }
                        } else {
                            null
                        }
                    }
                } catch (e: IOException) {
                    Log.e("ProfileViewModel", "Exception during fetch user data request: ${e.message}", e)
                    null
                }
            }
        }

        private fun parseUserFromJson(json: String): User? {
            val gson = Gson()
            return try {
                Log.d("ProfileViewModel", "Parsing JSON: $json")

                val jsonObject = gson.fromJson(json, JsonObject::class.java)
                Log.d("ProfileViewModel", "Parsed JSON Object: $jsonObject")

                val firstName = jsonObject.get("firstName")?.takeIf { !it.isJsonNull }?.asString
                val email = jsonObject.get("email")?.takeIf { !it.isJsonNull }?.asString
                val profilePicture = jsonObject.get("profilePicture")?.takeIf { !it.isJsonNull }?.asString

                if (firstName != null && email != null) {
                    User(
                        userId = 0, // Placeholder, since userId is not needed here
                        firstName = firstName,
                        lastName = "", // Not needed for this view
                        email = email,
                        password = "", // Not needed for this view
                        profilePicture = profilePicture
                    ).also {
                        Log.d("ProfileViewModel", "Successfully created User object: $it")
                    }
                } else {
                    Log.e("ProfileViewModel", "Required fields are missing: firstName or email is null")
                    null
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception during JSON parsing: ${e.message}", e)
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
                val base64Image = Base64Manager.encodeBitmapToBase64(bitmap)
                Log.d("ProfileViewModel", "Encoded Base64 Image: $base64Image")

                val body: RequestBody = base64Image.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

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

        // Delete user account
        fun deleteUserAccount(
            context: Context,
            callback: (Boolean, String?) -> Unit
        ) {
            val token = TokenManager.getToken(context)  // Retrieve the token from TokenManager

            viewModelScope.launch {
                val response = deleteUserAccountRequest(token)
                val success = response == "Account deleted successfully"
                callback(success, response)
            }
        }

        private suspend fun deleteUserAccountRequest(token: String?): String? {
            return withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/users/account")
                    .delete()
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                try {
                    client.newCall(request).execute().use { response ->
                        Log.d("ProfileViewModel", "Response Code: ${response.code}")
                        Log.d("ProfileViewModel", "Response Message: ${response.message}")
                        return@use when {
                            response.isSuccessful -> "Account deleted successfully"
                            else -> "Failed with response code ${response.code}, message: ${response.message}"
                        }
                    }
                } catch (e: IOException) {
                    Log.e("ProfileViewModel", "Exception during delete user account request: ${e.message}")
                    "Exception: ${e.message}"
                }
            }
        }
    }

}

