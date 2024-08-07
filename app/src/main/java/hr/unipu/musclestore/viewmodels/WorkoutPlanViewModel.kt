package hr.unipu.musclestore.viewmodel

import android.content.Context
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

data class Exercise(val title: String, val reps: String)
data class Section(val title: String, val exercises: List<Exercise>)

class WorkoutPlanViewModel : ViewModel() {
    private val client = OkHttpClient()

    fun sendWorkoutPlan(
        context: Context,
        userId: Int,
        title: String,
        sections: List<Section>,
        callback: (Boolean, String?) -> Unit
    ) {
        val token = TokenManager.getToken(context)

        viewModelScope.launch {
            val response = sendWorkoutPlanRequest(userId, title, sections, token)
            val success = response != null && response.isNotEmpty()
            callback(success, response)
        }
    }

    private suspend fun sendWorkoutPlanRequest(
        userId: Int,
        title: String,
        sections: List<Section>,
        token: String?
    ): String? {
        return withContext(Dispatchers.IO) {
            val json = JSONObject().apply {
                put("userId", userId)
                put("title", title)
                put("sections", JSONArray().apply {
                    sections.forEach { section ->
                        put(JSONObject().apply {
                            put("sectionId", JSONObject.NULL)
                            put("title", section.title)
                            put("exercises", JSONArray().apply {
                                section.exercises.forEach { exercise ->
                                    put(JSONObject().apply {
                                        put("exerciseId", JSONObject.NULL)
                                        put("title", exercise.title)
                                        put("reps", exercise.reps)
                                    })
                                }
                            })
                        })
                    }
                })
            }
            val body: RequestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

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
}
