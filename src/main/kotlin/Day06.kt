import util.parseLongs
import util.transpose
import kotlin.collections.reduce
import kotlin.collections.sum

// See puzzle in https://adventofcode.com/2025/day/6

class Day06(inputLines: List<String>) {
    val numLines = inputLines.dropLast(1)
    val ops = inputLines.last().split(Regex("\\s+"))

    private fun parseColumns(): List<List<Long>> =
        numLines.map { parseLongs(it) }.transpose()

    private fun parseColumnsRightToLeft(): List<List<Long>> {
        val maxWidth = numLines.maxOf { it.length }

        // Collect digits column-wise. Empty column is represented as an empty list.
        val columns: List<List<Int>> = (0 until maxWidth).map { c ->
            numLines.mapNotNull { line ->
                line.getOrNull(c)?.takeIf { it != ' ' }?.digitToInt()
            }
        }

        // Map collected digits into numbers, splitting on empty columns.
        return buildList {
            var current = mutableListOf<Int>()
            columns.forEach { digits ->
                if (digits.isEmpty()) {
                    add(current.map { it.toLong() })
                    current = mutableListOf()
                } else {
                    current.add(digits.joinToString("").toInt())
                }
            }
            // The last column needs to be added
            add(current.map { it.toLong() })
        }
    }


    private fun solve(nums: List<List<Long>>, ops: List<String>): Long =
        nums.zip(ops).sumOf { (nums, ops) ->
            when (ops) {
                "*" -> nums.reduce { acc, i -> acc * i }
                "+" -> nums.sum()
                else -> error("Unknown op $ops")
            }
        }

    fun part1(): Long = solve(parseColumns(), ops)
    fun part2(): Long = solve(parseColumnsRightToLeft(), ops)
}