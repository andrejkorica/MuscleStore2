package hr.unipu.musclestore.data

data class CalendarInput(
    val day:Int,
    val notes:List<String> = emptyList()
)
