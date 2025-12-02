// See puzzle in https://adventofcode.com/2025/day/2

class Day02(input: String) {
    private val ranges = input.split(",")
        .map { it.split("-").let { (a, b) -> a.toLong() to b.toLong() } }

    val doubleRepeatRule: (String) -> Boolean = { str ->
        val half = str.length / 2
        str.length % 2 == 0 && str.take(half) == str.substring(half)
    }

    val p2RepeatRule: (String) -> Boolean = { str ->
        (1..str.length / 2).any { pLen ->
            str.length % pLen == 0 &&
                    str.take(pLen).repeat(str.length / pLen) == str
        }
    }

    fun invalidIds(start: Long, end: Long, rule: (String) -> Boolean): Set<Long> =
        (start..end).filter { rule(it.toString()) }.toSet()

    fun part1(): Long = ranges.flatMap { invalidIds(it.first, it.second, doubleRepeatRule) }.sum()
    fun part2(): Long = ranges.flatMap { invalidIds(it.first, it.second, p2RepeatRule) }.sum()

}