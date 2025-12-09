import util.Coord
import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day09Test {
    @Test
    fun part1_test() {
        val result = Day09(readInput("Day09_test")).part1()
        assertEquals(50, result)
    }

    @Test
    fun part1_real() {
        val result = Day09(readInput("Day09")).part1()
        assertEquals(4767418746, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day09(readInput("Day09_test")).part2()
        assertEquals(24, result)
    }

    @Test
    fun part2_test2() {
        val result = Day09(readInput("Day09_test")).getRectangles(listOf(Coord(0, 0), Coord(0, 4), Coord(4, 4), Coord(4, 0)))
        println(result)
    }

    @Test
    fun part2_real() {
        val result = Day09(readInput("Day09")).part2()
        assertEquals(1461987144, result)
    }
}