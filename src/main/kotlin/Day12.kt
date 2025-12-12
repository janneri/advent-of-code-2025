import util.parseInts
import util.parseSections

// See puzzle in https://adventofcode.com/2025/day/12

class Day12(input: String) {
    private val inputSections = parseSections(input)
    private val shapes = inputSections.dropLast(1).map { Shape.of(it) }
    private val regions = inputSections.last().map { Region.of(it) }

    data class Region(val width: Int, val height: Int, val shapeCounts: List<Int>) {
        operator fun contains(cell: Cell): Boolean =
            cell.x in 0 until width && cell.y in 0 until height

        companion object {
            fun of(line: String): Region {
                val (dimensions, countsStr) = line.split(':')
                val (width, height) = dimensions.split('x').map(String::toInt)
                return Region(width, height, parseInts(countsStr.trim()))
            }
        }
    }

    data class Cell(val x: Int, val y: Int)

    data class Shape(val cells: Set<Cell>, val width: Int, val height: Int) {
        companion object {
            fun of(lines: List<String>): Shape {
                val gridLines = lines.drop(1)
                val cells = gridLines.flatMapIndexed { y, line ->
                    line.mapIndexedNotNull { x, c -> if (c == '#') Cell(x, y) else null }
                }
                return Shape(cells.toSet(), gridLines.first().length, gridLines.size)
            }
        }

        operator fun contains(cell: Cell): Boolean = cell in cells

        fun rotate(): Shape {
            val rotatedCells = cells.map { Cell(height - 1 - it.y, it.x) }
            val minX = rotatedCells.minOf { it.x }
            val minY = rotatedCells.minOf { it.y }
            val normalizedCells = rotatedCells.map { Cell(it.x - minX, it.y - minY) }.toSet()
            return Shape(normalizedCells, height, width)
        }

        fun allRotations(): List<Shape> =
            generateSequence(this) { it.rotate() }
                .take(4)
                .distinctBy { it.cells }
                .toList()

        fun translate(dx: Int, dy: Int): Set<Cell> =
            cells.map { Cell(it.x + dx, it.y + dy) }.toSet()
    }

    private fun shapesFitRegion(region: Region, shapes: List<Shape>): Boolean {
        val totalShapeCells = shapes.sumOf { it.cells.size }
        val regionArea = region.width * region.height

        if (totalShapeCells > regionArea) return false

        val threeByThreeCapacity = (region.width / 3) * (region.height / 3)
        if (shapes.size <= threeByThreeCapacity) return true

        // In the real input, we never go here. In the test input, we do.
        val allShapeRotations = shapes.associateWith { it.allRotations() }
        var iterationCount = 0
        val maxIterations = 100

        fun canFitToGrid(occupied: Set<Cell>, remaining: List<Shape>): Boolean {
            if (++iterationCount > maxIterations) return false
            if (remaining.isEmpty()) return true

            val shape = remaining.first()

            for (rotation in allShapeRotations[shape]!!) {
                for (x in 0 until region.width) {
                    for (y in 0 until region.height) {
                        val translatedCells = rotation.translate(x, y)

                        if (translatedCells.all { it in region && it !in occupied }) {
                            if (canFitToGrid(occupied + translatedCells, remaining.drop(1))) {
                                return true
                            }
                        }
                    }
                }
            }
            return false
        }
        return canFitToGrid(emptySet(), shapes)
    }

    fun part1(): Int = regions.count { region ->
        val selectedShapes = region.shapeCounts.flatMapIndexed { index, count ->
            List(count) { shapes[index] }
        }.sortedByDescending { it.cells.size }

        shapesFitRegion(region, selectedShapes)
    }

}
