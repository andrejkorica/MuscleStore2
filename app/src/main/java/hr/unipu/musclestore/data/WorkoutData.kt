package hr.unipu.musclestore.data
data class Exercise(
    val exerciseId: Int = 0,
    val title: String,
    val reps: String
)

data class Section(
    val sectionId: Int = 0,
    val title: String,
    val exercises: List<Exercise>
)

data class WorkoutPlan(
    val planId: Int,
    val title: String,
    val user: User,  // Nested user object
    val sections: List<Section>,
    val timestamp: String?  // Added timestamp field
)