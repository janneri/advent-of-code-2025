import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day11Test {
    @Test
    fun part1_test() {
        val result = Day11(readInput("Day11_test")).part1()
        assertEquals(5, result)
    }

    @Test
    fun part1_real() {
        val result = Day11(readInput("Day11")).part1()
        assertEquals(719, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day11(readInput("Day11_test")).part2()
        assertEquals(2, result)
    }

    @Test
    fun part2_real() {
        val result = Day11(readInput("Day11")).part2()
        assertEquals(2, result)
    }
}