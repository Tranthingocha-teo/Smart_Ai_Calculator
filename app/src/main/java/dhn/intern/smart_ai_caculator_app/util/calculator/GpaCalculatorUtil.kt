package dhn.intern.smart_ai_caculator_app.util.calculator

data class GpaSubjectInput(
    val credits: Double,
    val grade: Double
)

data class GpaCalculationResult(
    val totalCredits: Double,
    val weightedSum: Double,
    val gpa: Double
)

object GpaCalculatorUtil {

    fun calculateGpa(subjects: List<GpaSubjectInput>): GpaCalculationResult {
        val validSubjects = subjects.filter { it.credits > 0.0 && it.grade >= 0.0 }
        if (validSubjects.isEmpty()) {
            return GpaCalculationResult(
                totalCredits = 0.0,
                weightedSum = 0.0,
                gpa = 0.0
            )
        }

        val totalCredits = validSubjects.sumOf { it.credits }
        val weightedSum = validSubjects.sumOf { it.credits * it.grade }
        val gpa = if (totalCredits > 0.0) weightedSum / totalCredits else 0.0

        return GpaCalculationResult(
            totalCredits = totalCredits,
            weightedSum = weightedSum,
            gpa = gpa
        )
    }
}
