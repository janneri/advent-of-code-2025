import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day08Test {
    @Test
    fun part1_test() {
        val result = Day08(readInput("Day08_test")).part1(10)
        assertEquals(40, result)
    }

    @Test
    fun part1_real() {
        val result = Day08(readInput("Day08")).part1(1000)
        assertEquals(84968, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day08(readInput("Day08_test")).part2()
        assertEquals(25272, result)
    }

    @Test
    fun part2_real() {
        val result = Day08(readInput("Day08")).part2()
        assertEquals(8663467782, result)
    }
}