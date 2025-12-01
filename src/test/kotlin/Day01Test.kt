import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day01Test {
    @Test
    fun part1_test() {
        val result = Day01(readInput("Day01_test")).part1()
        assertEquals(3, result)
    }

    @Test
    fun part1_test2() {
        val result = Day01("""
            L50
            L1
            R1
            L100
            L100
            R100
        """.trimIndent().lines()).part1()
        assertEquals(5, result)
    }

    @Test
    fun part1_real() {
        val result = Day01(readInput("Day01")).part1()
        assertEquals(1034, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day01(readInput("Day01_test")).part2()
        assertEquals(6, result)
    }

    @Test
    fun part2_test2() {
        val result = Day01("""
            R1000
            L1000
        """.trimIndent().lines()).part2()
        assertEquals(20, result)
    }

    @Test
    fun part2_test3() {
        val result = Day01("""
            L49
            L101
        """.trimIndent().lines()).part2()
        assertEquals(2, result)
    }

    @Test
    fun part2_real() {
        val result = Day01(readInput("Day01")).part2()
        assertEquals(6166, result)
    }
}