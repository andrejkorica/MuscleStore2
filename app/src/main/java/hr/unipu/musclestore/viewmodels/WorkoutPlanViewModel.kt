package hr.unipu.musclestore.viewmodel

import android.content.Context
import android.util.Log
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class WorkoutPlanViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val gson = Gson()

    // Function to send a workout plan to the server
    fun sendWorkoutPlan(
        context: Context,
        title: String,
        sections: List<Section>,
        timestamp: String?,  // Added timestamp parameter
        callback: (Boolean, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = sendWorkoutPlanRequest(title, sections, timestamp, token)
            val success = response != null && response.isNotEmpty()
            callback(success, response)
        }
    }

    // Sends the workout plan request with the provided parameters
    private suspend fun sendWorkoutPlanRequest(
        title: String,
        sections: List<Section>,
        timestamp: String?,  // Added timestamp parameter
        token: String?
    ): String? {
        return withContext(Dispatchers.IO) {
            val jsonObject = JsonObject().apply {
                addProperty("title", title)
                addProperty("timestamp", timestamp)  // Add the timestamp to the JSON
                val sectionsArray = JsonArray()
                sections.forEach { section ->
                    val sectionObject = JsonObject().apply {
                        add("sectionId", JsonNull.INSTANCE) // Using JsonNull for null values
                        addProperty("title", section.title)
                        val exercisesArray = JsonArray()
                        section.exercises.forEach { exercise ->
                            val exerciseObject = JsonObject().apply {
                                add("exerciseId", JsonNull.INSTANCE) // Using JsonNull for null values
                                addProperty("title", exercise.title)
                                addProperty("reps", exercise.reps)
                            }
                            exercisesArray.add(exerciseObject)
                        }
                        add("exercises", exercisesArray)
                    }
                    sectionsArray.add(sectionObject)
                }
                add("sections", sectionsArray)
            }

            val body: RequestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans")
                .post(body)
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()
                    } else {
                        "Failed with response code ${response.code}, message: ${response.message}"
                    }
                }
            } catch (e: IOException) {
                Log.e("WorkoutPlanViewModel", "Exception during send workout plan request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

    // Function to retrieve workout plans for a specific user
    fun getWorkoutPlansForUser(
        context: Context,
        callback: (List<WorkoutPlan>?, User?, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = getWorkoutPlansRequest(token)
            val workoutPlans = parseWorkoutPlansFromJson(response)
            val user = workoutPlans.firstOrNull()?.user // Extract user from the first workout plan
            val success = workoutPlans.isNotEmpty()
            callback(workoutPlans.takeIf { success }, user, response)
        }
    }

    // Sends the request to retrieve workout plans
    private suspend fun getWorkoutPlansRequest(token: String?): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/user")
                .get()
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()
                    } else {
                        "Failed with response code ${response.code}, message: ${response.message}"
                    }
                }
            } catch (e: IOException) {
                Log.e("WorkoutPlanViewModel", "Exception during get workout plans request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

    // Parses the JSON response to create a list of WorkoutPlan objects
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
                        profilePicture = profilePicture // Safe parsing of profile picture
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

                    // Parse WorkoutPlan
                    WorkoutPlan(
                        planId = jsonObject.get("planId").asInt,
                        title = jsonObject.get("title").asString,
                        user = user,
                        sections = sections,
                        timestamp = jsonObject.get("timestamp")?.asString // Added timestamp parsing
                    )
                }
            } catch (e: Exception) {
                Log.e("WorkoutPlanViewModel", "Exception during JSON parsing: ${e.message}")
                emptyList()
            }
        }
    }

}
