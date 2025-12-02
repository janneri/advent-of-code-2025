import util.readInput
import util.readInputText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day02Test {
    @Test
    fun part1_test() {
        val result = Day02(readInputText("Day02_test")).part1()
        assertEquals(1227775554, result)
    }

    @Test
    fun part1_real() {
        val result = Day02(readInputText("Day02")).part1()
        assertEquals(18893502033, result)
    }
    
    @Test
    fun part2_test() {
        val result = Day02(readInputText("Day02_test")).part2()
        assertEquals(4174379265, result)
    }

    @Test
    fun part2_real() {
        val result = Day02(readInputText("Day02")).part2()
        assertEquals(26202168557, result)
    }
}