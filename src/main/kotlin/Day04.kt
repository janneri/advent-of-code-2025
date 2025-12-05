import util.Coord
import util.Grid

// See puzzle in https://adventofcode.com/2025/day/4

class Day04(inputLines: List<String>) {
    private val grid = Grid.of(inputLines)

    fun part1(): Int =
        grid.findCoords('@').count { c ->
            c.neighborsIncludingDiagonal()
                .count { grid.contains(it) && grid[it] == '@' } < 4
        }

    fun part2(): Int {
        val removed = mutableSetOf<Coord>()
        while (true) {
            val newRemoved = grid.findCoords('@')
                .filter { it !in removed }
                .filter { c ->
                    c.neighborsIncludingDiagonal()
                        .count { it in grid && grid[it] == '@' && it !in removed } < 4
                }
                .toSet()

            if (newRemoved.isEmpty()) break
            removed.addAll(newRemoved)
        }
        return removed.size
    }
}