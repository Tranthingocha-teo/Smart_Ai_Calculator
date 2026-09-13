package dhn.intern.smart_ai_caculator_app.graphing

import dhn.intern.smart_ai_caculator_app.data.repository.GraphPresetRepository
import dhn.intern.smart_ai_caculator_app.data.repository.PresetCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GraphPresetRepositoryTest {

    private lateinit var repository: GraphPresetRepository

    @Before
    fun setUp() {
        repository = GraphPresetRepository()
    }

    @Test
    fun `presets list contains all required mathematical families`() {
        val presets = repository.getPresets()
        assertTrue(presets.isNotEmpty())

        val expressions = presets.map { it.expression }
        assertTrue(expressions.contains("2x + 1"))
        assertTrue(expressions.contains("x^2 - 4"))
        assertTrue(expressions.contains("x^3 - 3x"))
        assertTrue(expressions.contains("sin(x)"))
        assertTrue(expressions.contains("cos(x)"))
        assertTrue(expressions.contains("1/x"))
        assertTrue(expressions.contains("e^x"))
        assertTrue(expressions.contains("ln(x)"))
    }

    @Test
    fun `filter presets by category works accurately`() {
        val polyPresets = repository.getPresetsByCategory(PresetCategory.POLYNOMIAL)
        assertTrue(polyPresets.all { it.category == PresetCategory.POLYNOMIAL })
        assertTrue(polyPresets.any { it.expression == "x^2 - 4" })

        val trigPresets = repository.getPresetsByCategory(PresetCategory.TRIGONOMETRIC)
        assertTrue(trigPresets.all { it.category == PresetCategory.TRIGONOMETRIC })
        assertTrue(trigPresets.any { it.expression == "sin(x)" })

        val rationalPresets = repository.getPresetsByCategory(PresetCategory.RATIONAL)
        assertTrue(rationalPresets.all { it.category == PresetCategory.RATIONAL })
        assertTrue(rationalPresets.any { it.expression == "1/x" })

        val expLogPresets = repository.getPresetsByCategory(PresetCategory.EXPONENTIAL_LOGARITHMIC)
        assertTrue(expLogPresets.all { it.category == PresetCategory.EXPONENTIAL_LOGARITHMIC })
        assertTrue(expLogPresets.any { it.expression == "e^x" })
    }

    @Test
    fun `getPresetById returns correct preset`() {
        val preset = repository.getPresetById("quadratic")
        assertNotNull(preset)
        assertEquals("x^2 - 4", preset?.expression)
        assertEquals(PresetCategory.POLYNOMIAL, preset?.category)
    }
}
