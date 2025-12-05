import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day05Test {
    @Test
    fun part1_test() {
        val result = Day05(readInputText("Day05_test")).part1()
        assertEquals(3, result)
    }

    @Test
    fun part1_real() {
        val result = Day05(readInputText("Day05")).part1()
        assertEquals(775, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day05(readInputText("Day05_test")).part2()
        assertEquals(14, result)
    }

    @Test
    fun part2_real() {
        val result = Day05(readInputText("Day05")).part2()
        assertEquals(350684792662845, result)
    }
}