// See puzzle in https://adventofcode.com/2025/day/1

class Day01(inputLines: List<String>) {
    data class Rotation(val direction: Char, val amount: Int)

    val rotations = inputLines.map { Rotation(it[0], it.substring(1).toInt()) }

    fun solve(countCrossings: Boolean): Int {
        var pos = 50
        var count = 0

        rotations.forEach { rotation ->
            if (countCrossings) count += rotation.amount / 100 // count full loops

            repeat(rotation.amount % 100) {
                pos = if (rotation.direction == 'L') (pos + 99) % 100 else (pos + 1) % 100
                if (countCrossings && pos == 0) count++
            }

            if (!countCrossings && pos == 0) count++
        }
        return count
    }

    fun part1(): Int = solve(countCrossings = false)
    fun part2(): Int = solve(countCrossings = true)
}