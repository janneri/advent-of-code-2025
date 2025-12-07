import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day07Test {
    @Test
    fun part1_test() {
        val result = Day07(readInput("Day07_test")).part1()
        assertEquals(21, result)
    }

    @Test
    fun part1_real() {
        val result = Day07(readInput("Day07")).part1()
        assertEquals(1662, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day07(readInput("Day07_test")).part2()
        assertEquals(40, result)
    }

    @Test
    fun part2_real() {
        val result = Day07(readInput("Day07")).part2()
        assertEquals(40941112789504, result)
    }
}