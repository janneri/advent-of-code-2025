// See puzzle in https://adventofcode.com/2025/day/3

class Day03(val inputLines: List<String>) {

    private fun findMax(line: String, charsLeft: Int): String {
        if (charsLeft == 0) return ""
        val end = line.length - charsLeft
        val maxIdx = (0..end).maxBy { line[it] }
        return line[maxIdx] + findMax(line.substring(maxIdx + 1), charsLeft - 1)
    }

    fun part1(): Long = inputLines.sumOf { findMax(it, 2).toLong() }
    fun part2(): Long = inputLines.sumOf { findMax(it, 12).toLong() }

}