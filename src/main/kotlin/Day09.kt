import util.Coord

// See puzzle in https://adventofcode.com/2025/day/9

class Day09(inputLines: List<String>) {
    val vertices = inputLines.map { Coord.of(it) }

    data class Rectangle(val min: Coord, val max: Coord) {
        companion object {
            fun of(a: Coord, b: Coord): Rectangle {
                return Rectangle(
                    Coord(minOf(a.x, b.x), minOf(a.y, b.y)),
                    Coord(maxOf(a.x, b.x), maxOf(a.y, b.y))
                )
            }
        }

        fun area(): Long =
            (max.x - min.x + 1).toLong() * (max.y - min.y + 1).toLong()

        fun inner(): Rectangle =
            Rectangle(
                Coord(min.x + 1, min.y + 1),
                Coord(max.x - 1, max.y - 1)
            )

        fun intersects(other: Rectangle): Boolean =
            min.x <= other.max.x &&
                    max.x >= other.min.x &&
                    min.y <= other.max.y &&
                    max.y >= other.min.y
    }

    fun doesNotIntersectAny(rect: Rectangle, edges: List<Rectangle>): Boolean =
        edges.all { !rect.intersects(it) }

    fun polygonEdges(vertices: List<Coord>): List<Rectangle> =
        (vertices + vertices.first()).windowed(2).map { (a, b) -> Rectangle.of(a, b) }

    fun allRectangles(vertices: List<Coord>): List<Rectangle> =
        vertices.flatMapIndexed { i, a -> vertices.drop(i + 1).map { b -> Rectangle.of(a, b) } }
            .distinct()
            .sortedByDescending { it.area() }

    fun part1(): Long = allRectangles(vertices).first().area()

    fun part2(): Long {
        val rectangles = allRectangles(vertices)
        val edges = polygonEdges(vertices)
        return rectangles
            .firstOrNull { rect -> doesNotIntersectAny(rect.inner(), edges) }
            ?.area() ?: 0L
    }

}
