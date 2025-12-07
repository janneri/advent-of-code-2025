package util

import java.nio.file.Path
import java.util.*
import kotlin.math.abs

fun readInput(name: String) = Path.of("src", "test", "kotlin", "resources", "$name.txt").toFile()
    .readLines()

fun readInputText(name: String) = Path.of("src", "test", "kotlin", "resources", "$name.txt").toFile()
    .readText()

fun parseSections(input: String, delimeter: String = "\n\n"): List<List<String>> =
    input.split(delimeter)
        .map { it.split("\n") }

infix fun <E> Set<E>.overlaps(otherSet: Set<E>): Boolean = this.any { otherSet.contains(it) }

fun sumN(n: Long): Long = n * (n + 1) / 2

fun <T> List<T>.middleValue(): T = this[size / 2]

private val numPattern = """-?[0-9]+""".toRegex()
fun parseInts(str: String): List<Int> =
    numPattern.findAll(str).map { it.value.toInt() }.toList()

fun parseUlongs(str: String): List<ULong> =
    numPattern.findAll(str).map { it.value.toULong() }.toList()

fun parseLongs(str: String): List<Long> =
    numPattern.findAll(str).map { it.value.toLong() }.toList()

fun IntRange.intersectRange(other: IntRange) =
    (maxOf(first, other.first)..minOf(last, other.last))

fun LongRange.intersectRange(other: LongRange) =
    (maxOf(first, other.first)..minOf(last, other.last))

fun LongRange.mergeRange(other: LongRange) =
    (minOf(first, other.first)..maxOf(last, other.last))

fun Double.almostEqual(other: Double) = abs(this - other) < 0.0001

fun LongRange.overLaps(other: LongRange) = when {
    this.first <= other.first -> this.last >= other.first
    else -> this.first <= other.last
}

enum class Direction(val dx: Int, val dy: Int, val symbol: Char, val letter: Char) {
    UP(0, -1, '^', 'U') {
        override fun turnLeft() = LEFT
        override fun turnRight() = RIGHT
    },
    RIGHT(1, 0, '>', 'R') {
        override fun turnLeft() = UP
        override fun turnRight() = DOWN
    },
    DOWN(0, 1, 'v', 'D') {
        override fun turnLeft() = RIGHT
        override fun turnRight() = LEFT
    },
    LEFT(-1, 0, '<', 'L') {
        override fun turnLeft() = DOWN
        override fun turnRight() = UP
    };

    abstract fun turnLeft(): Direction
    abstract fun turnRight(): Direction
    fun turnOpposite() = turnLeft().turnLeft()
    fun symbolStr() = symbol.toString()

    companion object {
        fun ofLetter(letter: Char): Direction =
            entries.find { it.letter == letter } ?: error("invalid letter")

        fun ofSymbol(symbol: Char): Direction =
            entries.find { it.symbol == symbol } ?: error("invalid symbol")
    }
}

enum class IDirection(val dx: Int, val dy: Int) {
    NORTH(0, -1),
    NORTHEAST(1, -1),
    EAST(1, 0),
    SOUTHEAST(1, 1),
    SOUTH(0, 1),
    SOUTHWEST(-1, 1),
    WEST(-1, 0),
    NORTHWEST(-1, -1)
}

data class Coord3d(val x: Long, val y: Long, val z: Long) {
    fun move(coord3d: Coord3d): Coord3d =
        Coord3d(x + coord3d.x, y + coord3d.y, z + coord3d.z)

    fun minus(coord3d: Coord3d): Coord3d =
        Coord3d(x - coord3d.x, y - coord3d.y, z - coord3d.z)

    companion object {
        val UP: Coord3d = Coord3d(0, 0, 1)
        val DOWN: Coord3d = Coord3d(0, 0, -1)
    }

    override fun toString() = "($x, $y, $z)"
}

data class LongCoord(val x: Long, val y: Long) {
    fun distanceTo(c: LongCoord): Long = abs(this.x - c.x) + abs(this.y - c.y)

    fun multiply(times: Long) =
        LongCoord(x * times, y * times)

    fun move(coord: LongCoord): LongCoord =
        LongCoord(x + coord.x, y + coord.y)

    fun move(direction: Direction, amount: Long = 1) =
        LongCoord(x + amount * direction.dx, y + amount * direction.dy)

    override fun toString() = "($x, $y)"
}

data class Coord(val x: Int, val y: Int): Comparable<Coord> {
    fun neighbors(): List<Coord> = Direction.entries.map { this.move(it) }
    fun neighborsIncludingDiagonal(): Set<Coord> = IDirection.entries.map { this.move(it) }.toSet()
    fun corners(): List<Coord> = listOf(
        Coord(this.x - 1, this.y - 1),
        Coord(this.x + 1, this.y - 1),
        Coord(this.x - 1, this.y + 1),
        Coord(this.x + 1, this.y + 1)
    )

    fun distanceTo(c: Coord): Int = abs(this.x - c.x) + abs(this.y - c.y)

    fun directionTo(c: Coord): Direction = when {
        x < c.x -> Direction.RIGHT
        x > c.x -> Direction.LEFT
        y < c.y -> Direction.DOWN
        else -> Direction.UP
    }

    fun move(direction: IDirection, amount: Int = 1) =
        Coord(x + amount * direction.dx, y + amount * direction.dy)

    fun move(direction: Direction, amount: Int = 1) =
        Coord(x + amount * direction.dx, y + amount * direction.dy)

    fun move(xDiff: Int, yDiff: Int) =
        Coord(x + xDiff, y + yDiff)

    operator fun plus(other: Coord): Coord =
        Coord(x + other.x, y + other.y)

    operator fun minus(other: Coord): Coord =
        Coord(x - other.x, y - other.y)

    fun moveCollect(direction: Direction, amount: Int = 1): List<Coord> =
        (0 .. amount).map { steps ->
            this.move(direction, steps)
        }

    fun moveCollect(direction: IDirection, amount: Int = 1): List<Coord> =
        (0 .. amount).map { steps ->
            this.move(direction, steps)
        }

    fun moveUntil(direction: Direction, predicate: (Coord) -> Boolean): Coord {
        val maxMoves = 10000
        var newCoord = this

        for (n in 0 until maxMoves) {
            val potentialNewCoord = newCoord.move(direction)
            if (predicate(potentialNewCoord)) {
                return newCoord
            }
            newCoord = potentialNewCoord
        }

        throw IllegalArgumentException("Too many (> $maxMoves) moves!")
    }

    fun teleport(gridWidth: Int, gridHeight: Int): Coord =
        Coord(
            if (x < 0) gridWidth + x else x % gridWidth,
            if (y < 0) gridHeight + y else y % gridHeight
        )

    override fun toString() = "($x, $y)"

    override fun compareTo(other: Coord) = compareValuesBy(this, other,
        { it.y },
        { it.x }
    )

    companion object {
        fun of(input: String): Coord = input.split(",")
            .let { (x, y) -> Coord(x.trim().toInt(), y.trim().toInt()) }
    }
}

data class Line(val start: Coord, val end: Coord) {
    val xDiff: Int get() = end.x - start.x
    val yDiff: Int get() = end.y - start.y
}

typealias Path = List<Coord>

infix fun Any.log(n: Int) {
    if (n == 1) println(this)
}

fun drawGrid(coords: Set<Coord>, tileSymbolAt: (Coord) -> Char) {
    val yRange = coords.minBy { it.y }.y..coords.maxBy { it.y }.y
    val xRange = coords.minBy { it.x }.x..coords.maxBy { it.x }.x

    for (y in yRange) {
        for (x in xRange) {
            val coord = Coord(x, y)
            print(tileSymbolAt(coord))
        }
        println()
    }
}

data class Grid<T>(val width: Int,
                   val height: Int,
                   val tileMap: Map<Coord, T>
) {
    operator fun get(coord: Coord): T? = tileMap[coord]
    operator fun contains(coord: Coord): Boolean = tileMap.containsKey(coord)

    fun lastY(): Int = height - 1
    fun centerCoord() = Coord(width / 2, height / 2)
    fun coords(): Set<Coord> = tileMap.keys
    fun coordsWithout(coord: Coord): Set<Coord> = tileMap.keys.filter { it != coord }.toSet()
    fun findCoords(tile: T): Set<Coord> = tileMap.filterValues { it == tile }.keys.toSet()
    fun getCoord(tile: T): Coord = findCoords(tile).first()

    fun findCoordsByTile(predicate: (T) -> Boolean): Set<Coord> =
        tileMap.filterValues { predicate(it) }.keys.toSet()

    fun findPath(start: Coord, end: Coord, walls: Set<Coord>): util.Path? {
        val seen = mutableSetOf<Coord>()
        val queue = PriorityQueue<util.Path>(compareBy { it.size }).apply { add(listOf(start)) }

        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val currentPos = path.last()

            if (currentPos == end) return path

            if (seen.add(currentPos)) {
                currentPos.neighbors()
                    .filter { it !in walls && it !in seen }
                    .forEach { queue.add(path + it) }
            }
        }
        return null
    }

    fun findAllShortestPaths(start: Coord, end: Coord, walls: Set<Coord>): List<util.Path> {
        val queue = PriorityQueue<util.Path>(compareBy { it.size }).apply { add(listOf(start)) }
        val shortestPaths = mutableListOf<util.Path>()
        var shortestPathLength = Int.MAX_VALUE

        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val currentPos = path.last()

            if (path.size > shortestPathLength) break

            if (currentPos == end) {
                if (path.size < shortestPathLength) {
                    shortestPathLength = path.size
                    shortestPaths.clear()
                }
                shortestPaths.add(path)
            } else {
                currentPos.neighbors()
                    .filter { it !in walls && (it !in path) }
                    .forEach { queue.add(path + it) }
            }
        }

        return shortestPaths
    }

    companion object {
        fun of(lines: List<String>): Grid<Char> {
            val tiles: Map<Coord, Char> = lines.flatMapIndexed { y, line ->
                line.mapIndexed { x, char -> Coord(x, y) to char }
            }.toMap()

            return Grid(lines[0].length, lines.size, tiles)
        }

        fun ofInts(lines: List<String>): Grid<Int> {
            val tiles: Map<Coord, Int> = lines.flatMapIndexed { y, line ->
                line.mapIndexed { x, char -> Coord(x, y) to char.digitToInt() }
            }.toMap()

            return Grid(lines[0].length, lines.size, tiles)
        }

        fun generate(width: Int, height: Int): Grid<Char> {
            return Grid(width, height, (0..<height)
                .flatMap { y ->
                    (0..<width).map { x -> Coord(x, y) to '.'}
                }.toMap())
        }
    }
}

fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = findLCM(result, numbers[i])
    }
    return result
}

fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun <T> List<T>.permutations() =
    this.dropLast(1).flatMapIndexed { i, item1 ->
        this.drop(i + 1).map { item2 ->
            item1 to item2
        }
    }

fun <T> List<T>.combinations(): List<Pair<T, T>> =
    this.flatMap { item1 ->
        this.map { item2 ->
            item1 to item2
        }
    }

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val colCount = this[0].size
    return (0 until colCount).map { colIndex ->
        this.map { row -> row[colIndex] }
    }
}