package hr.unipu.musclestore.data

data class AddFromStoreResponse(
    val id: Int,
    val userId: Int,
    val workoutPlanId: Int,
    val workoutPlan: WorkoutPlan?  // Include full WorkoutPlan details
)
