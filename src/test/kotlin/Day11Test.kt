import util.readInput
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val result = Day11("""
            svr: aaa bbb
            aaa: fft
            fft: ccc
            bbb: tty
            tty: ccc
            ccc: ddd eee
            ddd: hub
            hub: fff
            eee: dac
            dac: fff
            fff: ggg hhh
            ggg: out
            hhh: out
        """.trimIndent().lines()).part2()
        assertEquals(2, result)
    }

    @Test
    fun part2_real() {
        val result = Day11(readInput("Day11")).part2()
        assertEquals(337433554149492, result)
    }
}