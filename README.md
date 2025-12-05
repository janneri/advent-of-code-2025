# Advent of code 2025

## Calendar of puzzles

| Puzzle                                                             | Solution                                    | LOC  | Themes                                             |
|--------------------------------------------------------------------|---------------------------------------------|------|----------------------------------------------------|
| [Day 1 - Secret Entrance](https://adventofcode.com/2025/day/1)     | [Day 1 solution](src/main/kotlin/Day01.kt)  | 26   | Modular arithmetic, circular buffer                |
| [Day 2 - Gift Shop](https://adventofcode.com/2025/day/2)           | [Day 2 solution](src/main/kotlin/Day02.kt)  | 27   | String patterns, repetition                        |
| [Day 3 - Lobby](https://adventofcode.com/2025/day/3)               | [Day 3 solution](src/main/kotlin/Day03.kt)  | 14   | Greedy algorithms, recursion                       |
| [Day 4 - Printing Department](https://adventofcode.com/2025/day/4) | [Day 4 solution](src/main/kotlin/Day04.kt)  | 32   | Grid, coords, simulation                           |
| [Day 5 - Cafeteria](https://adventofcode.com/2025/day/5)           | [Day 5 solution](src/main/kotlin/Day05.kt)  | 25   | Range merging, interval processing, set operations |
| [Day 6 - TODO](https://adventofcode.com/2025/day/6)                | [Day 6 solution](src/main/kotlin/Day06.kt)  | TODO | TODO                                               |
| [Day 7 - TODO](https://adventofcode.com/2025/day/7)                | [Day 7 solution](src/main/kotlin/Day07.kt)  | TODO | TODO                                               |
| [Day 8 - TODO](https://adventofcode.com/2025/day/8)                | [Day 8 solution](src/main/kotlin/Day08.kt)  | TODO | TODO                                               |
| [Day 9 - TODO](https://adventofcode.com/2025/day/9)                | [Day 9 solution](src/main/kotlin/Day09.kt)  | TODO | TODO                                               |
| [Day 10 - TODO](https://adventofcode.com/2025/day/10)              | [Day 10 solution](src/main/kotlin/Day10.kt) | TODO | TODO                                               |
| [Day 11 - TODO](https://adventofcode.com/2025/day/11)              | [Day 11 solution](src/main/kotlin/Day11.kt) | TODO | TODO                                               |
| [Day 12 - TODO](https://adventofcode.com/2025/day/12)              | [Day 12 solution](src/main/kotlin/Day12.kt) | TODO | TODO                                               |

## Blog

### Mod vs Rem in Kotlin

I always forget the difference between % and mod.
I seem to face this same problem each year in the AOC puzzles.

For example:

```kotlin
val a = -3 % 100    // Result: -3, same as in Java
val b = (-3).rem(100) // Result: -3, rem is equivalent to %
val b = (-3).mod(100) // Result: 97
```

The puzzle in Day 1 had a dial 0..99 that could be turned left or right.
The dial wraps around zero. For example, starting at position 5:
- Turning right 10 steps: 15
- Turning left 20 steps: 95

Using % lead to negative positions, which are invalid for the dial.
Using mod ensured the position wraps correctly within 0..99.

For example:
```kotlin
fun turnDial(start: Int, steps: Int): Int = (start + steps).mod(100)

turnDial(5, 10)  // Returns 15
turnDial(5, -10) // Returns 95
```

### The runningFold

Kotlin's `runningFold` function is like fold, but it returns a list of all intermediate accumulation values.
It can be useful when simulating processes over time.
In the dial turning example from Day 1, I could do this:

```kotlin
val moves = listOf(10, -10, 20)
fun turnDial(start: Int, steps: Int): Int = (start + steps).mod(100)
val dialPositions = 
    moves.runningFold(5) { position, move -> turnDial(position, move) }

println(dialPositions) // Output: [5, 15, 5, 25]
```


### generateSequence and filterTo

In day 4, I could have used `generateSequence` to create an infinite sequence of states for the lobby simulation.

Here is a basic `while(true) break` structure that could be replaced with `generateSequence`:
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

The above code can be refactored using `generateSequence` as follows:
```kotlin
fun part2(): Int {
    val removed = mutableSetOf<Coord>()
    // Invokes the function to calculate the next value on each iteration until the function returns null
    generateSequence {
        // Compute next set of coords to remove, or return null to stop
        grid.findCoords('@')
            .filter { it !in removed }
            // use filterTo instead of filter when we want to collect results into a specific collection
            .filterTo(mutableListOf()) { c ->
                c.neighborsIncludingDiagonal()
                    .filter { it in grid }
                    .count { grid[it] == '@' && it !in removed } < 4
            }
            // takeIf returns null if the list is empty, stopping the sequence
            .takeIf { it.isNotEmpty() }
    // removed.addAll(it) is called on each iteration of the generateSequence
    }.forEach { removed.addAll(it) }
    return removed.size
}
```


### buildList

The day 5 puzzle required merging a list of ranges. 
You can use Kotlin's `buildList` for that:

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

Why would you use `buildList` instead of fold?
The `fold` is well known and a general-purpose function for accumulating results.
The intent of building a list is clearer with `buildList`, enhancing code readability.
