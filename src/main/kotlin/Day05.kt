import util.mergeRange
import util.overLaps
import util.parseSections

// See puzzle in https://adventofcode.com/2025/day/5

class Day05(input: String) {
    private val inputSections = parseSections(input)

    private val ranges = inputSections[0].map {
        val (a, b) = it.split("-")
        a.toLong() .. b.toLong()
    }.sortedBy { it.first }

    private val ids = inputSections[1].map { it.toLong() }
    
    fun part1(): Int = ids.count { id -> ranges.any { id in it } }

    fun part2(): Long = buildList {
        ranges.forEach { range ->
            val last = lastOrNull()
            when {
                last == null -> add(range)
                range.overLaps(last) -> {
                    removeLast()
                    add(last.mergeRange(range))
                }
                else -> add(range)
            }
        }
    }.sumOf { it.last - it.first + 1 }
}