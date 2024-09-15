package hr.unipu.musclestore.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import hr.unipu.musclestore.data.AddFromStoreResponse
import hr.unipu.musclestore.data.Exercise
import hr.unipu.musclestore.data.Section
import hr.unipu.musclestore.data.User
import hr.unipu.musclestore.data.WorkoutNotation
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

    // Function to retrieve a workout plan by ID
    fun getWorkoutPlanById(
        context: Context,
        planId: String,
        callback: (WorkoutPlan?, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = getWorkoutPlanByIdRequest(planId, token)
            val workoutPlan = parseWorkoutPlanFromJson(response)
            val success = workoutPlan != null
            callback(workoutPlan, response) // Pass both parameters
        }
    }

    // Sends the request to retrieve a workout plan by ID
    private suspend fun getWorkoutPlanByIdRequest(planId: String, token: String?): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/$planId")
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
                Log.e("WorkoutPlanViewModel", "Exception during get workout plan by ID request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

    // Function to delete a workout plan by ID
    fun deleteWorkoutPlanById(
        context: Context,
        planId: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = deleteWorkoutPlanRequest(planId, token)
            val success = response != null && response.isEmpty()
            callback(success, response)
        }
    }

    // Sends the delete request for a workout plan by ID
    private suspend fun deleteWorkoutPlanRequest(planId: String, token: String?): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/$planId")
                .delete()
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        ""  // Return an empty string on successful deletion
                    } else {
                        "Failed with response code ${response.code}, message: ${response.message}"
                    }
                }
            } catch (e: IOException) {
                Log.e("WorkoutPlanViewModel", "Exception during delete workout plan request: ${e.message}")
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
                    parseWorkoutPlanFromJson(jsonObject.toString())
                }.filterNotNull() // Filter out any null values
            } catch (e: Exception) {
                Log.e("WorkoutPlanViewModel", "Exception during JSON parsing: ${e.message}")
                emptyList()
            }
        }
    }

    // Parses the JSON response to create a WorkoutPlan object
    private fun parseWorkoutPlanFromJson(json: String?): WorkoutPlan? {
        return if (json.isNullOrEmpty()) {
            null
        } else {
            try {
                val jsonObject = gson.fromJson(json, JsonObject::class.java)

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
            } catch (e: Exception) {
                Log.e("WorkoutPlanViewModel", "Exception during JSON parsing: ${e.message}")
                null
            }
        }
    }

    // Function to retrieve the active workout plan
    fun getActiveWorkoutPlan(
        context: Context,
        callback: (WorkoutPlan?, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = getActiveWorkoutRequest(token)
            val activeWorkoutId = parseActiveWorkoutIdFromJson(response)

            if (activeWorkoutId != null) {
                // Fetch the actual workout plan details using the active workout ID
                getWorkoutPlanById(context, activeWorkoutId.toString()) { plan, error ->
                    callback(plan, error)
                }
            } else {
                callback(null, "Failed to parse active workout ID")
            }
        }
    }

    // Sends the request to retrieve the active workout plan
    private suspend fun getActiveWorkoutRequest(token: String?): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/active")
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
                Log.e("WorkoutPlanViewModel", "Exception during get active workout request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

    // Parses the JSON response to extract active workout ID
    private fun parseActiveWorkoutIdFromJson(json: String?): Int? {
        return try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            jsonObject.get("workoutPlanId")?.asInt
        } catch (e: Exception) {
            Log.e("WorkoutPlanViewModel", "Exception during active workout ID parsing: ${e.message}")
            null
        }
    }

    // Function to set an active workout plan
    fun setActiveWorkoutPlan(
        context: Context,
        workoutPlanId: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = setActiveWorkoutRequest(workoutPlanId, token)
            val success = response != null && response.isEmpty()
            callback(success, response)
        }
    }

    // Sends the request to set an active workout plan
    private suspend fun setActiveWorkoutRequest(workoutPlanId: Int, token: String?): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/$workoutPlanId/set-active")
                .post(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), "{}"))
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        ""  // Return an empty string on successful request
                    } else {
                        "Failed with response code ${response.code}, message: ${response.message}"
                    }
                }
            } catch (e: IOException) {
                Log.e("WorkoutPlanViewModel", "Exception during set active workout request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

    // Function to add a workout from the store
    fun addWorkoutFromStore(
        context: Context,
        workoutPlanId: Int,
        callback: (Boolean, String?, Int?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = addWorkoutFromStoreRequest(workoutPlanId, token)
            val success = response?.first ?: false
            val errorMessage = response?.second
            val addedFromStoreId = response?.third
            callback(success, errorMessage, addedFromStoreId)
        }
    }

    // Sends the request to add a workout from the store
    private suspend fun addWorkoutFromStoreRequest(workoutPlanId: Int, token: String?): Triple<Boolean, String?, Int?>? {
        return withContext(Dispatchers.IO) {
            val jsonObject = JsonObject().apply {
                addProperty("workoutPlanId", workoutPlanId)
            }

            val body: RequestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/add-from-store")
                .post(body)
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonResponse = gson.fromJson(responseBody, JsonObject::class.java)
                        val addedFromStoreId = jsonResponse.get("addedFromStoreId").asInt
                        Triple(true, null, addedFromStoreId)
                    } else {
                        Triple(false, "Failed with response code ${response.code}, message: ${response.message}", null)
                    }
                }
            } catch (e: IOException) {
                Log.e("WorkoutPlanViewModel", "Exception during add workout from store request: ${e.message}")
                Triple(false, "Exception: ${e.message}", null)
            }
        }
    }

    // Function to retrieve all records from the add-from-store endpoint
    fun getAllAddedFromStore(
        context: Context,
        callback: (List<AddFromStoreResponse>?, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = getAllAddedFromStoreRequest(token)
            val addedFromStoreRecords = parseAddFromStoreResponse(response)
            val success = addedFromStoreRecords.isNotEmpty()
            callback(addedFromStoreRecords.takeIf { success }, response)
        }
    }

    // Sends the GET request to retrieve all records from add-from-store
    private suspend fun getAllAddedFromStoreRequest(token: String?): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/add-from-store")
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
                Log.e("WorkoutPlanViewModel", "Exception during get all added from store request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

    // Parses the JSON response to create a list of AddFromStoreResponse objects
    private fun parseAddFromStoreResponse(json: String?): List<AddFromStoreResponse> {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                val jsonArray = gson.fromJson(json, JsonArray::class.java)
                jsonArray.map { jsonElement ->
                    gson.fromJson(jsonElement, AddFromStoreResponse::class.java)
                }
            } catch (e: Exception) {
                Log.e("WorkoutPlanViewModel", "Exception during JSON parsing: ${e.message}")
                emptyList()
            }
        }
    }

    // Function to create a workout notation
    fun createWorkoutNotation(
        context: Context,
        timestamp: String?,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = createWorkoutNotationRequest(timestamp, token)
            val success = !response.isNullOrEmpty()
            callback(success, response)
        }
    }

    // Sends the request to create a workout notation
    private suspend fun createWorkoutNotationRequest(
        timestamp: String?,
        token: String?
    ): String? {
        return withContext(Dispatchers.IO) {
            val jsonObject = JsonObject().apply {
                addProperty("timestamp", timestamp)
            }

            val body: RequestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/workout-notations")
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
                Log.e("WorkoutPlanViewModel", "Exception during create workout notation request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }


    // Function to retrieve all workout notations
    fun getAllWorkoutNotations(
        context: Context,
        callback: (List<WorkoutNotation>?, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = getAllWorkoutNotationsRequest(token)
            val workoutNotations = parseWorkoutNotationsFromJson(response)
            val success = workoutNotations.isNotEmpty()
            callback(workoutNotations.takeIf { success }, response)
        }
    }

    // Sends the GET request to retrieve all workout notations
    private suspend fun getAllWorkoutNotationsRequest(token: String?): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/workout-notations")
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
                Log.e("WorkoutPlanViewModel", "Exception during get all workout notations request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

// Parses the JSON response to create a list of WorkoutNotation objects
    private fun parseWorkoutNotationsFromJson(json: String?): List<WorkoutNotation> {
        if (json.isNullOrEmpty()) {
            Log.e("WorkoutPlanViewModel", "Empty or null JSON response")
            return emptyList()
        }

        return try {
            val jsonElement = gson.fromJson(json, JsonElement::class.java)

            // Check if the root element is an array
            if (jsonElement.isJsonArray) {
                val jsonArray = jsonElement.asJsonArray
                jsonArray.map { element ->
                    gson.fromJson(element, WorkoutNotation::class.java)
                }
            } else {
                // Log if it's not an array
                Log.e("WorkoutPlanViewModel", "Expected JSON array but received: $jsonElement")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("WorkoutPlanViewModel", "Exception during JSON parsing: ${e.message}")
            emptyList()
        }
    }

    fun deleteWorkoutNotationsForUser(
        context: Context,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = deleteWorkoutNotationRequest(token)
            val success = response.isEmpty()
            callback(success, response)
        }
    }

    private suspend fun deleteWorkoutNotationRequest(token: String?): String {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/workout-plans/workout-notations")
                .delete()
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        ""  // Return an empty string on successful deletion
                    } else {
                        "Failed with response code ${response.code}, message: ${response.message}"
                    }
                }
            } catch (e: IOException) {
                Log.e("WorkoutPlanViewModel", "Exception during delete workout notation request: ${e.message}")
                "Exception: ${e.message}"
            }
        }
    }

}
