
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals

class Day12Test {
    @Test
    fun part1_test() {
        val result = Day12(readInputText("Day12_test")).part1()
        assertEquals(2, result)
    }

    @Test
    fun part1_real() {
        val result = Day12(readInputText("Day12")).part1()
        assertEquals(557, result)
    }

}