# Advent of code 2025

## Calendar of puzzles

| Puzzle                                                             | Solution                                    | SLOC | Themes                                               |
|--------------------------------------------------------------------|---------------------------------------------|------|------------------------------------------------------|
| [Day 1 - Secret Entrance](https://adventofcode.com/2025/day/1)     | [Day 1 solution](src/main/kotlin/Day01.kt)  | 19   | Modular arithmetic, circular buffer                  |
| [Day 2 - Gift Shop](https://adventofcode.com/2025/day/2)           | [Day 2 solution](src/main/kotlin/Day02.kt)  | 18   | String patterns, repetition                          |
| [Day 3 - Lobby](https://adventofcode.com/2025/day/3)               | [Day 3 solution](src/main/kotlin/Day03.kt)  | 10   | Greedy algorithms, recursion                         |
| [Day 4 - Printing Department](https://adventofcode.com/2025/day/4) | [Day 4 solution](src/main/kotlin/Day04.kt)  | 25   | Grid, coords, simulation                             |
| [Day 5 - Cafeteria](https://adventofcode.com/2025/day/5)           | [Day 5 solution](src/main/kotlin/Day05.kt)  | 25   | Range merging, interval processing, set operations   |
| [Day 6 - Trash Compactor](https://adventofcode.com/2025/day/6)     | [Day 6 solution](src/main/kotlin/Day06.kt)  | 40   | Column parsing, transpose, string manipulation       |
| [Day 7 - Laboratories](https://adventofcode.com/2025/day/7)        | [Day 7 solution](src/main/kotlin/Day07.kt)  | 43   | Beam simulation, timeline tracking                   |
| [Day 8 - Playground](https://adventofcode.com/2025/day/8)          | [Day 8 solution](src/main/kotlin/Day08.kt)  | 74   | Union-Find, graph connectivity, distance sorting     |
| [Day 9 - Movie Theater](https://adventofcode.com/2025/day/9)       | [Day 9 solution](src/main/kotlin/Day09.kt)  | 42   | Rectangle geometry, polygon containment, brute-force |
| [Day 10 - TODO](https://adventofcode.com/2025/day/10)              | [Day 10 solution](src/main/kotlin/Day10.kt) | TODO | TODO                                                 |
| [Day 11 - TODO](https://adventofcode.com/2025/day/11)              | [Day 11 solution](src/main/kotlin/Day11.kt) | TODO | TODO                                                 |
| [Day 12 - TODO](https://adventofcode.com/2025/day/12)              | [Day 12 solution](src/main/kotlin/Day12.kt) | TODO | TODO                                                 |

## Blog

### Mod vs Rem in Kotlin

If you come from Java, Python, or C++, you might expect `%` to always yield a non-negative result. In Kotlin (like Java), `%` and `rem` return the remainder, which can be negative if the dividend is negative. The `mod` function, however, always returns a non-negative result, making it ideal for wrapping indices (such as circular buffers or dials).

| Expression         | Result | Notes                                 |
|--------------------|--------|---------------------------------------|
| `-3 % 100`         | -3     | Same as Java's `%`                    |
| `(-3).rem(100)`    | -3     | Equivalent to `%`                     |
| `(-3).mod(100)`    | 97     | Always non-negative, wraps correctly  |

For example, in Day 1, the puzzle involved a dial `0..99` that could be turned left or right and wraps around zero. Using `%` could lead to negative positions, which are invalid for the dial. Using `mod` ensures the position wraps correctly within `0..99`.

```kotlin
fun turnDial(start: Int, steps: Int): Int = (start + steps).mod(100)

turnDial(5, 10)  // Returns 15
turnDial(5, -10) // Returns 95
```

### The runningFold

Kotlin's `runningFold` function is similar to `scan` in languages like Python or Scala: it returns a list of all intermediate accumulation values, not just the final result. This is useful for simulating state changes over time.

For example, simulating a dial's position after each move:

```kotlin
val moves = listOf(10, -10, 20)
fun turnDial(start: Int, steps: Int): Int = (start + steps).mod(100)
val dialPositions = 
    moves.runningFold(5) { position, move -> turnDial(position, move) }

println(dialPositions) // Output: [5, 15, 5, 25]
```

Here, `runningFold` starts at `5` and applies each move, collecting all intermediate positions.

### generateSequence and filterTo

In Day 4, `generateSequence` can be used to express iterative state changes without explicit loops. This is more idiomatic in Kotlin and avoids manual `while(true)` constructs. `filterTo` is used to collect results into a mutable collection, and `takeIf` returns the collection if it's not empty (or `null` to stop the sequence).

Original approach:
```kotlin
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
```

Refactored with `generateSequence`:
```kotlin
fun part2(): Int {
    val removed = mutableSetOf<Coord>()
    generateSequence {
        grid.findCoords('@')
            .filter { it !in removed }
            .filterTo(mutableListOf()) { c ->
                c.neighborsIncludingDiagonal()
                    .filter { it in grid }
                    .count { grid[it] == '@' && it !in removed } < 4
            }
            .takeIf { it.isNotEmpty() }
    }.forEach { removed.addAll(it) }
    return removed.size
}
```

- `generateSequence` repeatedly computes the next set of items to remove, stopping when the list is empty.
- `filterTo` collects results into a mutable list.
- `takeIf` returns `null` to stop the sequence when there are no more items.

### buildList

For merging a list of ranges (as in Day 5), Kotlin's `buildList` provides a clear, idiomatic way to construct lists. Compared to `fold`, `buildList` makes the intent of list-building explicit and avoids manual list management.

```kotlin
buildList {
    ranges.forEach { range ->
        val last = lastOrNull()
        when {
            last == null -> add(range)
            range.overLaps(last) -> {
                removeLast()
                add(last.mergeRange(range))
            }
            else -> add(range)
        }
    }
}
```

Why use `buildList` instead of `fold`? While `fold` is a general-purpose accumulator, `buildList` is designed for constructing lists, making the code's intent clearer and more idiomatic in Kotlin.

### Using mapNotNull and getOrNull for safe access

The standard function for mapping and filtering out nulls is `mapNotNull` (not `mapOrNull`). `getOrNull` safely accesses a list or string by index, returning `null` if out of bounds (instead of throwing an exception). This is especially useful for ragged input (lines of different lengths).

Example from Day 6, parsing columns of digits from input lines:

```kotlin
val inputLines = """
    123 1
     45 12
      6 98
    """.trimIndent().lines()

val maxWidth = inputLines.maxOf { it.length }

// Collect digits column-wise. Empty column is represented as an empty list.
val columns: List<List<Int>> = (0 until maxWidth).map { c ->
    inputLines.mapNotNull { line -> // Transforms and returns only the non-null items
        line.getOrNull(c) // Safely get character at index c, or null if out of bounds
            ?.takeIf { it != ' ' } // Ignore spaces
            ?.digitToInt() // Convert character to integer
    }
}

println(columns) // Output: [[1], [2, 4], [3, 5, 6], [], [1, 1, 9], [2, 8]]
```

- `mapNotNull` applies the transformation and filters out nulls in one step.
- `getOrNull` prevents exceptions from out-of-bounds access.
- `takeIf` is used to skip spaces.

### Use operators for grid utils 

Kotlin allows operator overloading. 
For example, defining `plus` and `minus` operators for a `Coord` class can simplify neighbor calculations.

```kotlin
data class Coord(val x: Int, val y: Int) {
    fun move(dir: Direction) = this + dir.delta
    operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)
}

enum class Direction(val delta: Coord) {
    UP(Coord(0, -1)),
    DOWN(Coord(0, 1)),
    LEFT(Coord(-1, 0)),
    RIGHT(Coord(1, 0))
}

// Example usage:
val start = Coord(0, 3) // 'S' position
val upNeighbor = start.move(Direction.DOWN) // Coord(1, 3))
```

Many puzzles in AOC involve moving around a grid. 
Defining `get` and `contains` operators for a `Grid` class make grid access more intuitive.

```kotlin 
data class Grid<T>(val width: Int,
                   val height: Int,
                   val tileMap: Map<Coord, T>
) {
    operator fun get(coord: Coord): T? = tileMap[coord]
    operator fun contains(coord: Coord): Boolean = tileMap.containsKey(coord)
    
    companion object {
        fun of(lines: List<String>): Grid<Char> {
            val tiles: Map<Coord, Char> = lines.flatMapIndexed { y, line ->
                line.mapIndexed { x, char -> Coord(x, y) to char }
            }.toMap()

            return Grid(lines[0].length, lines.size, tiles)
        }
    }
}

// Example usage:
val gridLines = """
    ...S...
    .......
    ...^...
    .......
    ..^.^..
    """.trimIndent().lines()

val grid = Grid.of(gridLines)
val startCoord = Coord(0, 3)
println(grid[startCoord]) // 'S'
println(startCoord in grid) // true
```

### Summary

These are just the tips and idioms I've found useful while solving the Advent of Code 2025 puzzles in Kotlin.
Happy coding!
