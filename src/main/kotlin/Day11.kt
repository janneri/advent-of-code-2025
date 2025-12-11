// See puzzle in https://adventofcode.com/2025/day/11

class Day11(inputLines: List<String>) {
    val nodes = inputLines.associate { line ->
        line.split(":").let { it[0] to it[1].trim().split(" ") }
    }

    private fun solve(
        start: String,
        end: String,
        isValid: (Set<String>) -> Boolean
    ): Long {
        val memo = mutableMapOf<Triple<String, Boolean, Boolean>, Long>()

        fun dfs(
            node: String,
            foundDac: Boolean,
            foundFft: Boolean,
            visited: Set<String>
        ): Long {
            if (node == end) return if (isValid(visited)) 1 else 0
            val key = Triple(node, foundDac, foundFft)
            if (key in memo) return memo[key]!!

            var count = 0L
            val newVisited = visited + node
            for (neighbor in nodes[node]!!) {
                if (neighbor !in newVisited) {
                    count += dfs(
                        neighbor,
                        foundDac || neighbor == "dac",
                        foundFft || neighbor == "fft",
                        newVisited
                    )
                }
            }
            memo[key] = count
            return count
        }

        return dfs(start, false, false, emptySet())
    }

    fun part1() =
        solve("you", "out") { true }

    fun part2(): Long =
        solve("svr", "out") { it.contains("dac") && it.contains("fft") }
}
