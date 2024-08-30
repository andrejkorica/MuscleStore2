package hr.unipu.musclestore.data

// Data model for user
data class User(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val profilePicture: String?  // Base64 encoded string
)
