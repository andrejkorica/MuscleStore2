package hr.unipu.musclestore.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonNull
import hr.unipu.musclestore.data.Exercise
import hr.unipu.musclestore.data.Section
import hr.unipu.musclestore.data.User
import hr.unipu.musclestore.data.WorkoutPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class StoreViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val gson = Gson()

    var workoutPlans: List<WorkoutPlan> by mutableStateOf(emptyList())
        private set

    // Fetch all workout plans from the server
    fun fetchAllWorkoutPlans(context: Context) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = getAllWorkoutPlansRequest(token)
            workoutPlans = parseWorkoutPlansFromJson(response)
        }
    }

    // Make network request to fetch all workout plans
    private suspend fun getAllWorkoutPlansRequest(token: String?): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans") // Endpoint to fetch all workout plans
                .get()
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()
                    } else {
                        Log.e("StoreViewModel", "Failed with response code ${response.code}, message: ${response.message}")
                        null
                    }
                }
            } catch (e: IOException) {
                Log.e("StoreViewModel", "Exception during get workout plans request: ${e.message}")
                null
            }
        }
    }

    // Parse JSON response to create a list of WorkoutPlan objects
    private fun parseWorkoutPlansFromJson(json: String?): List<WorkoutPlan> {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                val jsonArray = gson.fromJson(json, JsonArray::class.java)
                jsonArray.map { jsonElement ->
                    val jsonObject = jsonElement.asJsonObject

                    // Parse User
                    val userJson = jsonObject.getAsJsonObject("user")
                    val profilePicture = userJson.get("profilePicture")?.takeIf { it.isJsonPrimitive }?.asString

                    val user = User(
                        userId = userJson.get("userId").asInt,
                        email = userJson.get("email").asString,
                        firstName = userJson.get("firstName").asString,
                        lastName = userJson.get("lastName").asString,
                        password = "",
                        profilePicture = profilePicture // Safely parsed profile picture
                    )

                    // Parse Sections
                    val sections = jsonObject.getAsJsonArray("sections").map { sectionElement ->
                        val sectionObject = sectionElement.asJsonObject
                        val exercises = sectionObject.getAsJsonArray("exercises").map { exerciseElement ->
                            val exerciseObject = exerciseElement.asJsonObject
                            Exercise(
                                exerciseId = exerciseObject.get("exerciseId").asInt,
                                title = exerciseObject.get("title").asString,
                                reps = exerciseObject.get("reps").asString
                            )
                        }

                        Section(
                            sectionId = sectionObject.get("sectionId").asInt,
                            title = sectionObject.get("title").asString,
                            exercises = exercises
                        )
                    }

                    // Parse WorkoutPlan including the timestamp
                    WorkoutPlan(
                        planId = jsonObject.get("planId").asInt,
                        title = jsonObject.get("title").asString,
                        user = user,
                        sections = sections,
                        timestamp = jsonObject.get("timestamp")?.asString // Parse timestamp safely
                    )
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Exception during JSON parsing: ${e.message}")
                emptyList()
            }
        }
    }

}
