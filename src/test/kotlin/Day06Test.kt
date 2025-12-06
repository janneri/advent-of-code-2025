import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day06Test {
    @Test
    fun part1_test() {
        val result = Day06(readInput("Day06_test")).part1()
        assertEquals(4277556, result)
    }

    @Test
    fun part1_real() {
        val result = Day06(readInput("Day06")).part1()
        assertEquals(4580995422905, result)
    }

    @Test
    fun part2_test() {
        val result = Day06(readInput("Day06_test")).part2()
        assertEquals(3263827, result)
    }

    @Test
    fun part2_real() {
        val result = Day06(readInput("Day06")).part2()
        assertEquals(10875057285868, result)
    }
}