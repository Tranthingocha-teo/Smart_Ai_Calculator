package dhn.intern.smart_ai_caculator_app.domain.graphing

enum class SpecialPointType {
    ROOT,
    LOCAL_MIN,
    LOCAL_MAX,
    INTERSECTION,
    Y_INTERCEPT
}

data class SpecialPoint(
    val point: GraphPoint,
    val type: SpecialPointType,
    val label: String
)
