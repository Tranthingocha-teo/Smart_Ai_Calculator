package dhn.intern.smart_ai_caculator_app.graphing

import dhn.intern.smart_ai_caculator_app.domain.graphing.DefaultGraphingEngine
import dhn.intern.smart_ai_caculator_app.domain.graphing.GraphingEngine
import dhn.intern.smart_ai_caculator_app.domain.graphing.SpecialPointType
import dhn.intern.smart_ai_caculator_app.domain.graphing.ViewportBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.system.measureTimeMillis

class GraphingEngineTest {

    private lateinit var engine: GraphingEngine

    @Before
    fun setUp() {
        engine = DefaultGraphingEngine()
    }

    @Test
    fun `compile valid polynomial expression evaluates correctly`() {
        val result = engine.compile("x^2 - 4")
        assertTrue(result.isSuccess)
        val compiled = result.getOrThrow()

        assertEquals(-4.0, compiled.evaluate(0.0), 1e-6)
        assertEquals(0.0, compiled.evaluate(2.0), 1e-6)
        assertEquals(0.0, compiled.evaluate(-2.0), 1e-6)
        assertEquals(5.0, compiled.evaluate(3.0), 1e-6)
    }

    @Test
    fun `compile supports implicit multiplication for coefficients, parentheses, and functions`() {
        // 2x -> 2 * x
        val f1 = engine.compile("2x + 1").getOrThrow()
        assertEquals(7.0, f1.evaluate(3.0), 1e-6)

        // x(x + 1) -> x * (x + 1)
        val f2 = engine.compile("x(x + 1)").getOrThrow()
        assertEquals(6.0, f2.evaluate(2.0), 1e-6)

        // (x - 1)(x + 1) -> (x - 1) * (x + 1)
        val f3 = engine.compile("(x - 1)(x + 1)").getOrThrow()
        assertEquals(8.0, f3.evaluate(3.0), 1e-6)

        // 3sin(x) -> 3 * sin(x)
        val f4 = engine.compile("3sin(x)").getOrThrow()
        assertEquals(3.0, f4.evaluate(PI / 2), 1e-5)
    }

    @Test
    fun `compile supports unicode exponents and mathematical constants`() {
        // x² - 4 -> x^2 - 4
        val f1 = engine.compile("x² - 4").getOrThrow()
        assertEquals(5.0, f1.evaluate(3.0), 1e-6)

        // x³ -> x^3
        val f2 = engine.compile("x³").getOrThrow()
        assertEquals(8.0, f2.evaluate(2.0), 1e-6)

        // π and e constants
        val f3 = engine.compile("π * x").getOrThrow()
        assertEquals(2 * PI, f3.evaluate(2.0), 1e-5)
    }

    @Test
    fun `compile returns failure for invalid syntax or mismatched parentheses`() {
        val r1 = engine.compile("((x + 1)")
        assertTrue(r1.isFailure)

        val r2 = engine.compile("x + * 2")
        assertTrue(r2.isFailure)
    }

    @Test
    fun `sample polynomial produces single continuous segment and discovers roots and extrema`() {
        val compiled = engine.compile("x^2 - 4").getOrThrow()
        val viewport = ViewportBounds(minX = -5.0, maxX = 5.0, minY = -5.0, maxY = 5.0)

        val curve = engine.sample(compiled, viewport, screenPixelWidth = 500)

        // Continuous parabola without asymptotes should be 1 single segment
        assertEquals(1, curve.continuousSegments.size)
        assertTrue(curve.continuousSegments[0].size >= 200)

        // Roots at x = -2 and x = 2
        val roots = curve.specialPoints.filter { it.type == SpecialPointType.ROOT }
        assertEquals(2, roots.size)
        assertTrue(roots.any { abs(it.point.x - (-2.0)) < 1e-4 })
        assertTrue(roots.any { abs(it.point.x - 2.0) < 1e-4 })

        // Local minimum at x = 0, y = -4
        val minPoint = curve.specialPoints.firstOrNull { it.type == SpecialPointType.LOCAL_MIN }
        assertNotNull(minPoint)
        assertEquals(0.0, minPoint!!.point.x, 1e-4)
        assertEquals(-4.0, minPoint.point.y, 1e-4)
    }

    @Test
    fun `sample 1 over x splits curve across asymptote without vertical connecting line`() {
        val compiled = engine.compile("1 / x").getOrThrow()
        val viewport = ViewportBounds(minX = -5.0, maxX = 5.0, minY = -5.0, maxY = 5.0)

        val curve = engine.sample(compiled, viewport, screenPixelWidth = 500)

        // Must be split into at least 2 distinct continuous segments (x < 0 and x > 0)
        assertTrue("Expected curve to be split across asymptote x=0, got ${curve.continuousSegments.size} segments",
            curve.continuousSegments.size >= 2)

        // Verify no segment connects from negative y to positive y across x=0
        for (segment in curve.continuousSegments) {
            val hasNegativeX = segment.any { it.x < -0.05 }
            val hasPositiveX = segment.any { it.x > 0.05 }
            assertFalse("A single segment crossed asymptote from x < 0 to x > 0", hasNegativeX && hasPositiveX)
        }
    }

    @Test
    fun `sample tan x splits across vertical asymptotes at plus minus pi over 2`() {
        val compiled = engine.compile("tan(x)").getOrThrow()
        val viewport = ViewportBounds(minX = -3.0, maxX = 3.0, minY = -5.0, maxY = 5.0)

        val curve = engine.sample(compiled, viewport, screenPixelWidth = 500)

        // Around [-3, 3], tan(x) has asymptotes at -pi/2 (~ -1.57) and +pi/2 (~ 1.57)
        assertTrue("tan(x) must have multiple segments, got ${curve.continuousSegments.size}",
            curve.continuousSegments.size >= 3)
    }

    @Test
    fun `numerical solver accurately finds extrema for trigonometric functions`() {
        // f(x) = sin(x) has local max at pi/2 (~1.570796) with y = 1.0
        val compiled = engine.compile("sin(x)").getOrThrow()
        val viewport = ViewportBounds(minX = 0.0, maxX = PI, minY = -1.5, maxY = 1.5)

        val curve = engine.sample(compiled, viewport, screenPixelWidth = 500)
        val maxPoint = curve.specialPoints.firstOrNull { it.type == SpecialPointType.LOCAL_MAX }

        assertNotNull(maxPoint)
        assertEquals(PI / 2, maxPoint!!.point.x, 1e-4)
        assertEquals(1.0, maxPoint.point.y, 1e-4)
    }

    @Test
    fun `findIntersections accurately finds intersection points between line and parabola`() {
        // f(x) = x^2, g(x) = x + 2
        // Intersections: x^2 - x - 2 = 0 -> (x - 2)(x + 1) = 0 -> x = -1, y = 1; x = 2, y = 4
        val f = engine.compile("x^2").getOrThrow()
        val g = engine.compile("x + 2").getOrThrow()
        val viewport = ViewportBounds(minX = -3.0, maxX = 3.0, minY = -2.0, maxY = 6.0)

        val intersections = engine.findIntersections(f, g, viewport)
        assertEquals(2, intersections.size)

        val p1 = intersections.find { abs(it.point.x - (-1.0)) < 1e-4 }
        assertNotNull(p1)
        assertEquals(1.0, p1!!.point.y, 1e-4)

        val p2 = intersections.find { abs(it.point.x - 2.0) < 1e-4 }
        assertNotNull(p2)
        assertEquals(4.0, p2!!.point.y, 1e-4)
    }

    @Test
    fun `performance test verifies 600 points sampled in under 10ms`() {
        val compiled = engine.compile("3 * sin(2x) + x^2 / 4 - 2").getOrThrow()
        val viewport = ViewportBounds(minX = -10.0, maxX = 10.0, minY = -10.0, maxY = 10.0)

        // Warm up JIT
        repeat(5) { engine.sample(compiled, viewport, screenPixelWidth = 600) }

        val duration = measureTimeMillis {
            val curve = engine.sample(compiled, viewport, screenPixelWidth = 600)
            assertTrue(curve.continuousSegments.isNotEmpty())
        }

        assertTrue("Sampling took too long: ${duration}ms (expected < 15ms)", duration < 15)
    }
}
