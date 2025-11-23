import util.parseInts
import kotlin.math.abs

// See puzzle in https://adventofcode.com/2024/day/1

class Day01(inputLines: List<String>) {
    private val numberLines: List<List<Int>> = inputLines.map { parseInts(it) }
    private val leftNums = numberLines.map { it.first() }.sorted()
    private val rightNums = numberLines.map { it.last() }.sorted()

    fun part1(): Int = leftNums.indices.sumOf { index ->
        abs(leftNums[index] - rightNums[index])
    }

    fun part2(): Int =
        leftNums.sumOf { left ->
            left * rightNums.count { it == left }
        }
}