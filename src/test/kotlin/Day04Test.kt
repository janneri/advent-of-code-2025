import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day04Test {
    @Test
    fun part1_test() {
        val result = Day04(readInput("Day04_test")).part1()
        assertEquals(13, result)
    }

    @Test
    fun part1_real() {
        val result = Day04(readInput("Day04")).part1()
        assertEquals(1474, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day04(readInput("Day04_test")).part2()
        assertEquals(43, result)
    }

    @Test
    fun part2_real() {
        val result = Day04(readInput("Day04")).part2()
        assertEquals(8910, result)
    }
}