import util.Coord
import util.drawGrid
import kotlin.math.abs
import kotlin.text.compareTo

// See puzzle in https://adventofcode.com/2025/day/9

class Day09(inputLines: List<String>) {
    val points = inputLines.map { Coord.of(it) }

    data class Rectangle(val topLeft: Coord, val bottomRight: Coord) {
        companion object {
            fun of(a: Coord, b: Coord): Rectangle {
                return Rectangle(Coord(minOf(a.x, b.x), minOf(a.y, b.y)),
                    Coord(maxOf(a.x, b.x), maxOf(a.y, b.y)))
            }
        }

        fun area(): Long =
            (abs(topLeft.x - bottomRight.x).toLong() + 1) *
            (abs(topLeft.y - bottomRight.y).toLong() + 1)

        fun innerRectangle(): Rectangle =
            Rectangle(
                Coord(topLeft.x + 1, topLeft.y + 1),
                Coord(bottomRight.x - 1, bottomRight.y - 1)
            )

        fun intersects(other: Rectangle): Boolean =
            topLeft.x <= other.bottomRight.x &&
                    bottomRight.x >= other.topLeft.x &&
                    topLeft.y <= other.bottomRight.y &&
                    bottomRight.y >= other.topLeft.y
    }

    fun noIntersects(rect: Rectangle, edges: List<Rectangle>): Boolean =
        edges.all { !rect.intersects(it) }

    fun getEdges(points: List<Coord>): List<Rectangle> =
        (points + points.first()).windowed(2).map { (a, b) -> Rectangle.of(a, b) }

    fun getRectangles(points: List<Coord>): List<Rectangle> =
        points.flatMapIndexed { i, a -> points.drop(i + 1).map { b -> Rectangle.of(a, b) } }
            .distinct()
            .sortedByDescending { it.area() }


    private fun Pair<Coord, Coord>.area(): Long =
        (abs(this.first.x - this.second.x).toLong() + 1) *
                (abs(this.first.y - this.second.y).toLong() + 1)

    // Part 1: Find the largest rectangle between any two points
    fun part1(): Long = points
        .flatMapIndexed { i, a -> points.drop(i + 1).map { b -> a to b } }
        .maxByOrNull { (a, b) -> a.distanceTo(b) }
        ?.let { (p1, p2) -> Pair(p1, p2).area() }
        ?: 0L

    // Ray-casting point-in-polygon test
    // Returns true if Coord c is inside the polygon
    private fun isInsidePolygon(c: Coord, polygon: List<Coord>): Boolean {
        var crossingsCount = 0
        for (i in polygon.indices) {
            val a = polygon[i]
            val b = polygon[(i + 1) % polygon.size]

            // Check if the horizontal line at c.y crosses edge (a,b)
            if ((a.y > c.y) != (b.y > c.y)) {
                val xCross = a.x + (c.y - a.y) * (b.x - a.x) / (b.y - a.y)
                if (xCross > c.x) crossingsCount++
            }
        }
        // If crosses odd times, the point is inside
        return crossingsCount % 2 == 1
    }

    // Returns all tiles along the path (edges) of the polygon
    private fun getPathTiles(): Set<Coord> {
        val pathPoints = mutableSetOf<Coord>()
        val cornerPoints = points + points.first() // Close the polygon
        for (i in 1 until cornerPoints.size) {
            val p1 = cornerPoints[i - 1]
            val p2 = cornerPoints[i]
            // Collect all coordinates between p1 and p2
            val newCoords = p1.moveCollect(p1.directionTo(p2), p1.distanceTo(p2))
            pathPoints += newCoords
        }
        return pathPoints
    }

    // For each scanline (y), find all x-ranges inside the polygon
    // Returns a map: y -> list of x ranges (IntRange) that are inside
    fun getInsideRanges(path: List<Coord>): Map<Int, List<IntRange>> {
        val edgeTable = mutableMapOf<Int, MutableList<Pair<Coord, Coord>>>()
        // Build edge table: for each edge, add to all scanlines it crosses
        for (i in path.indices) {
            val a = path[i]
            val b = path[(i + 1) % path.size]
            val minY = minOf(a.y, b.y)
            val maxY = maxOf(a.y, b.y)
            for (y in minY until maxY) {
                edgeTable.getOrPut(y) { mutableListOf() }.add(Pair(a, b))
            }
        }
        for ((y, edges) in edgeTable) {
            println("Row $y:")
            for ((a, b) in edges) {
                println("  Edge from (${a.x},${a.y}) to (${b.x},${b.y})")
            }
        }

        drawGrid(points.toSet(), { c ->
            when {
                c in path.toSet() -> '#'
                edgeTable.values.find { it.any { it.first == c || it.second == c } } != null -> 'X'
                else -> ' '
            }
        }
        )


        val ranges = mutableMapOf<Int, List<IntRange>>()
        // For each scanline, compute intersection points and build ranges
        for ((y, edges) in edgeTable) {
            val intersections = mutableListOf<Int>()
            for ((a, b) in edges) {
                // Only consider edges crossing this scanline
                if ((a.y > y) != (b.y > y)) {
                    val xCross = a.x + (y - a.y) * (b.x - a.x) / (b.y - a.y)
                    intersections.add(xCross)
                }
            }
            intersections.sort()

            val rowRanges = mutableListOf<IntRange>()
            // Pair up intersections to form ranges
            for (i in intersections.indices step 2) {
                if (i + 1 < intersections.size) {
                    rowRanges.add(intersections[i]..intersections[i + 1])
                }
            }
            println("Row $y: intersections at x=${intersections.joinToString(",")} => ranges: ${rowRanges.joinToString(",")}")
            if (rowRanges.isNotEmpty()) {
                ranges[y] = rowRanges
            }
        }
        return ranges
    }

    fun part2(): Long {
        val rectangles = getRectangles(points)
        val edges = getEdges(points)
        val result2 = rectangles
            .firstOrNull { rect -> noIntersects(rect.innerRectangle(), edges) }
            ?.area() ?: 0L
        return result2
    }

    // Part 2: Find largest rectangle contained in the polygon
    fun part22(): Long {
        val pathTiles = getPathTiles() // Tiles on the polygon path
        val insideRanges = getInsideRanges(pathTiles.toList()) // Scanline ranges inside polygon
        println("Found inside ranges for ${insideRanges.size} rows")

        var maxArea = 0L
        var maxPair: Pair<Coord, Coord>? = null
        // Try all pairs of points as rectangle corners
        for (i in points.indices) {
            for (j in i + 1 until points.size) {
                val p1 = points[i]
                val p2 = points[j]
                val minX = minOf(p1.x, p2.x)
                val maxX = maxOf(p1.x, p2.x)
                val minY = minOf(p1.y, p2.y)
                val maxY = maxOf(p1.y, p2.y)
                var valid = true
                // For each row in the rectangle, check if all x are inside
                for (y in minY..maxY) {
                    val ranges = insideRanges[y] ?: emptyList()
                    var covered = false
                    // Check if the whole x-range is covered by any inside range
                    for (range in ranges) {
                        if (range.first <= minX && range.last >= maxX) {
                            covered = true
                            break
                        }
                    }
                    // If not covered, check if all x in [minX, maxX] are on the path
                    if (!covered) {
                        for (x in minX..maxX) {
                            val c = Coord(x, y)
                            if (c !in pathTiles) {
                                valid = false
                                break
                            }
                        }
                    }
                    if (!valid) break // Rectangle is not valid
                }
                // If rectangle is valid, check if its area is the largest
                if (valid) {
                    val area = (maxX - minX + 1) * (maxY - minY + 1)
                    if (area > maxArea) {
                        maxArea = area.toLong()
                        maxPair = Pair(p1, p2)
                    }
                }
            }
        }
        println("Max area is $maxArea between $maxPair")
        return maxArea
    }
}
