import io.ksmt.KContext
import io.ksmt.expr.KExpr
import io.ksmt.solver.KSolverStatus
import io.ksmt.solver.z3.KZ3Solver
import io.ksmt.sort.KIntSort
import util.parseInts
import kotlin.time.Duration.Companion.seconds

// See puzzle in https://adventofcode.com/2025/day/10

class Day10(inputLines: List<String>) {
    val machines = inputLines.map { Machine.of(it) }

    data class Machine(val lightTarget: Int, val buttons: List<Button>, val joltageRequirements: List<Int>) {
        companion object {
            fun of(line: String): Machine {
                val lights = line.substringAfter('[').substringBefore(']')
                val target = lights.reversed().fold(0) { acc, c -> (acc shl 1) or if (c == '#') 1 else 0 }

                val buttons = Regex("\\((.*?)\\)").findAll(line)
                    .map { Button.of(it.groupValues[1]) }.toList()

                val joltage = parseInts(line.substringAfter('{').substringBefore('}'))

                return Machine(target, buttons, joltage)
            }
        }
    }

    data class Button(val name: String, val mask: Int, val joltageEffect: Set<Int>) {
        fun toggle(state: Int): Int = state xor mask

        companion object {
            fun of(name: String): Button {
                val ints = parseInts(name)
                val mask = ints.fold(0) { acc, i -> acc or (1 shl i) }
                return Button(name, mask, ints.toSet())
            }
        }
    }

    data class Node(val state: Int, val path: List<Button>)

    fun findShortestSequence(machine: Machine): List<Button> {
        val visited = mutableSetOf<Int>()
        val queue = ArrayDeque<Node>()
        queue.add(Node(0, emptyList()))

        while (queue.isNotEmpty()) {
            val (currentState, path) = queue.removeFirst()
            if (currentState == machine.lightTarget) return path
            if (!visited.add(currentState)) continue

            for (button in machine.buttons) {
                val nextState = button.toggle(currentState)
                queue.add(Node(nextState, path + button))
            }
        }
        return emptyList()
    }
    
    fun part1(): Int = machines.sumOf { findShortestSequence(it).size }


    data class SMTSolver(
        val context: KContext,
        val machine: Machine,
    ) {
        val buttonVars = machine.buttons.indices.map { context.mkConst("btn$it", context.intSort) }
        val joltageVars = machine.joltageRequirements.indices.map { context.mkConst("j$it", context.intSort) }

        fun solve(): Int = KZ3Solver(context).use { solver ->
            with(context) {
                // Button counts >= 0 to prevent solver from using negative presses
                buttonVars.forEach { solver.assertAndTrack(it ge 0.expr) }

                // Joltage equations: sum of buttons affecting each joltage index
                machine.joltageRequirements.forEachIndexed { i, req ->
                    val sum = buttonVars.filterIndexed { bi, _ -> i in machine.buttons[bi].joltageEffect }
                        .fold(0.expr as KExpr<KIntSort>) { acc, v -> acc + v }
                    solver.assertAndTrack(joltageVars[i] eq sum)
                    solver.assertAndTrack(joltageVars[i] eq req.expr)
                }

                // Find minimum total button presses
                val total = buttonVars.fold(0.expr as KExpr<KIntSort>) { acc, v -> acc + v }
                (0..Int.MAX_VALUE).first { n ->
                    solver.checkWithAssumptions(listOf(total eq n.expr), 1.seconds) == KSolverStatus.SAT
                }
            }
        }
    }

    fun part2(): Int =
        machines.sumOf { machine -> KContext().use { ctx -> SMTSolver(ctx, machine).solve() } }

}