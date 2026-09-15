package dhn.intern.smart_ai_caculator_app.ocr

import dhn.intern.smart_ai_caculator_app.domain.ocr.model.BoundingBox
import org.junit.Assert.*
import org.junit.Test

class BoundingBoxTest {

    @Test
    fun testDimensionsAndCenter() {
        val box = BoundingBox(left = 10, top = 20, right = 50, bottom = 80)
        assertEquals(40, box.width)
        assertEquals(60, box.height)
        assertEquals(30.0f, box.centerX, 0.01f)
        assertEquals(50.0f, box.centerY, 0.01f)
        assertEquals(2400, box.area)
        assertEquals(40f / 60f, box.aspectRatio, 0.01f)
    }

    @Test
    fun testContainsAndIntersects() {
        val box1 = BoundingBox(10, 10, 50, 50)
        assertTrue(box1.contains(30, 30))
        assertFalse(box1.contains(60, 60))

        val box2 = BoundingBox(40, 40, 80, 80)
        assertTrue(box1.intersects(box2))

        val box3 = BoundingBox(100, 100, 120, 120)
        assertFalse(box1.intersects(box3))
    }

    @Test
    fun testUnion() {
        val b1 = BoundingBox(10, 20, 30, 40)
        val b2 = BoundingBox(25, 35, 50, 60)
        val u = b1.union(b2)
        assertEquals(10, u.left)
        assertEquals(20, u.top)
        assertEquals(50, u.right)
        assertEquals(60, u.bottom)
    }

    @Test
    fun testHorizontalOverlapRatio() {
        val b1 = BoundingBox(10, 10, 30, 20) // width 20
        val b2 = BoundingBox(15, 30, 35, 40) // width 20, overlap 15..30 (15)
        val ratio = b1.horizontalOverlapRatio(b2)
        assertEquals(15f / 20f, ratio, 0.01f)
    }
}
