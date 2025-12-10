import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day10Test {
    @Test
    fun part1_test() {
        val result = Day10(readInput("Day10_test")).part1()
        assertEquals(7, result)
    }

    @Test
    fun part1_real() {
        val result = Day10(readInput("Day10")).part1()
        assertEquals(473, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day10(readInput("Day10_test")).part2()
        assertEquals(33, result)
    }

    @Test
    fun part2_real() {
        val result = Day10(readInput("Day10")).part2()
        assertEquals(18681, result)
    }
}