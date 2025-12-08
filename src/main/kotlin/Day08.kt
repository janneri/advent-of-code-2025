// See puzzle in https://adventofcode.com/2025/day/8

class Day08(inputLines: List<String>) {
    data class Point(val x: Int, val y: Int, val z: Int) {
        fun distanceSquared(other: Point): Long {
            val dx = (x - other.x).toLong()
            val dy = (y - other.y).toLong()
            val dz = (z - other.z).toLong()
            return dx * dx + dy * dy + dz * dz
        }
    }

    data class PointPair(val p1: Point, val p2: Point, val distance: Long)

    private val points = inputLines.map {
        val (x, y, z) = it.split(",").map(String::toInt)
        Point(x, y, z)
    }

    // The Union-Find algorithm
    class CircuitBuilder(points: List<Point>) {
        // Initially each point is its own parent
        private val parent = points.associateWith { it }.toMutableMap()
        private val size = points.associateWith { 1L }.toMutableMap()

        // Path compression: flattens the tree for faster future lookups
        fun findParent(p: Point): Point {
            if (parent[p] != p) {
                parent[p] = findParent(parent[p]!!)
            }
            return parent[p]!!
        }

        // Union by Rank/Size: minimizing tree height
        fun connect(p1: Point, p2: Point) {
            val root1 = findParent(p1)
            val root2 = findParent(p2)
            if (root1 == root2) return
            if (size[root1]!! < size[root2]!!) {
                parent[root1] = root2
                size[root2] = size[root2]!! + size[root1]!!
            } else {
                parent[root2] = root1
                size[root1] = size[root1]!! + size[root2]!!
            }
        }

        val pointPairsWithDistance = points.indices.flatMap { i ->
            (i + 1 until points.size).map { j ->
                PointPair(points[i], points[j], points[i].distanceSquared(points[j]))
            }
        }.sortedBy { it.distance }

        fun connected(p1: Point, p2: Point): Boolean =
            findParent(p1) == findParent(p2)

        private fun circuitRoots(): List<Point> =
            parent.keys.map { findParent(it) }.distinct()

        fun topThreeCircuitProduct(): Long =
            circuitRoots()
                .map { root -> size[root]!! }
                .sortedDescending()
                .take(3)
                .reduce(Long::times)

        fun numberOfCircuits(): Int = circuitRoots().size
    }

    fun part1(maxCount: Int = 1000): Long {
        val cb = CircuitBuilder(points)

        for (pair in cb.pointPairsWithDistance.take(maxCount)) {
            if (!cb.connected(pair.p1, pair.p2)) {
                cb.connect(pair.p1, pair.p2)
            }
        }

        return cb.topThreeCircuitProduct()
    }

    fun part2(): Long {
        val cb = CircuitBuilder(points)

        for (pair in cb.pointPairsWithDistance) {
            if (!cb.connected(pair.p1, pair.p2)) {
                cb.connect(pair.p1, pair.p2)
            }
            if (cb.numberOfCircuits() == 1) {
                return pair.p1.x.toLong() * pair.p2.x.toLong()
            }
        }
        error("Could not connect all points into one circuit")
    }
}
