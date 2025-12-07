import util.Coord
import util.Direction
import util.Direction.LEFT
import util.Direction.RIGHT
import util.Grid
import util.drawGrid

// See puzzle in https://adventofcode.com/2025/day/7

class Day07(inputLines: List<String>) {
    private val grid = Grid.of(inputLines)
    private val startCoord = grid.getCoord('S')

    /**
     * Simulates the beam movement and splitting process.
     * Beams fall downward one row at a time. When a beam hits '^', it splits left and right.
     * Returns a Pair of (number of splits, total timelines reaching the bottom row).
     */
    private fun solve(): Pair<Int, Long> {
        var beams = setOf(startCoord)
        val timelines = mutableMapOf(startCoord to 1L)
        var splitCount = 0
        repeat (grid.lastY()) {
            beams = buildSet {
                beams.forEach { beam ->
                    val newPos = beam.move(Direction.DOWN)
                    val timelineCount = timelines.getOrDefault(beam, 1)
                    when (grid[newPos]) {
                        '^' -> {
                            // Beam splits into two directions
                            listOf(newPos.move(LEFT), newPos.move(RIGHT)).forEach {
                                timelines.merge(it, timelineCount, Long::plus)
                                add(it)
                            }
                            splitCount += 1
                        }
                        else -> {
                            // Beam continues downward
                            timelines.merge(newPos, timelineCount, Long::plus)
                            add(newPos)
                        }
                    }
                }
            }
        }

        val timelinesReachingBottom = timelines
            .filter { (coord, _) -> coord.y == grid.lastY() }
            .values
            .sum()

        return splitCount to timelinesReachingBottom
    }

    fun part1(): Int = solve().first

    fun part2(): Long = solve().second
}