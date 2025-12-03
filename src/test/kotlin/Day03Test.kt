import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day03Test {
    @Test
    fun part1_test() {
        val result = Day03(readInput("Day03_test")).part1()
        assertEquals(357, result)
    }

    @Test
    fun part1_real() {
        val result = Day03(readInput("Day03")).part1()
        assertEquals(17113, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day03(readInput("Day03_test")).part2()
        assertEquals(3121910778619, result)
    }

    @Test
    fun part2_real() {
        val result = Day03(readInput("Day03")).part2()
        assertEquals(169709990062889, result)
    }
}