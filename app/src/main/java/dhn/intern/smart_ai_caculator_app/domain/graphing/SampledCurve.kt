package dhn.intern.smart_ai_caculator_app.domain.graphing

data class SampledCurve(
    val functionId: String,
    val continuousSegments: List<List<GraphPoint>>,
    val specialPoints: List<SpecialPoint>
)
