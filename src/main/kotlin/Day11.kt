// See puzzle in https://adventofcode.com/2025/day/11

class Day11(inputLines: List<String>) {
    val nodes = inputLines.associate { line ->
        line.split(":").let { it[0] to it[1].trim().split(" ") }
    }

    fun part1(): Int {
        val visited = mutableSetOf<String>()
        var pathCount = 0
        val queue = ArrayDeque<String>()
        queue.add("you")

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current == "out") {
                pathCount++
                continue
            }
            //if (!visited.add(current)) continue

            for (neighbors in nodes[current]!!) {
                queue.add(neighbors)
            }
        }

        return pathCount
    }

    fun part2(): Int {
        return 0
    }
}