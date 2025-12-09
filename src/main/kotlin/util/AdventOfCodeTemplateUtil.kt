package util

import java.io.File
import java.net.CookieHandler
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

enum class InputType { GRID, TEXT, SECTIONS, LINES }

object AdventOfCodeTemplateUtil {
    private const val YEAR = 2025
    private const val DAYNUM = 9
    private val inputType = InputType.LINES
    private const val SRC_DIR = "src/main/kotlin" // DayN.kt is created to this directory
    private const val TEST_DIR = "src/test/kotlin" // DayNTest.kt is created to this directory
    private val testResourcesDir = Path.of("src", "test", "kotlin", "resources") // Input is downloaded here
    // Session id for an authenticated download should be placed here:
    private val aocSessionFile = File(System.getProperty("user.home"),".adventofcode.session")

    @JvmStatic
    fun main(args: Array<String>) {
        createDay()
    }

    private fun createDay() {
        val dir = Path.of(SRC_DIR)
        val dayPrefix = String.format("Day%02d", DAYNUM)
        val mainFile = dir.resolve("${dayPrefix}.kt").toFile()

        val inputParam = when (inputType) {
            InputType.TEXT -> "input: String"
            InputType.SECTIONS -> "input: String"
            else -> "inputLines: List<String>"
        }
        val firstVariable = when (inputType) {
            InputType.GRID -> "private val grid = Grid.of(inputLines)"
            InputType.SECTIONS -> "private val inputSections = parseSections(input)"
            else -> ""
        }

        println("Creating ${mainFile.name}")
        Files.writeString(mainFile.toPath(), """
        // See puzzle in https://adventofcode.com/$YEAR/day/$DAYNUM
        
        class $dayPrefix($inputParam) {
            $firstVariable
            
            fun part1(): Int {
                return 0
            }
            
            fun part2(): Int {
                return 0
            }
        }
        """.trimIndent())

        val testSrcDir = Path.of(TEST_DIR)
        val mainTestFile = testSrcDir.resolve("${dayPrefix}Test.kt").toFile()
        println("Creating ${mainTestFile.name}")

        val readFunction = when (inputType) {
            InputType.TEXT -> "readInputText"
            InputType.SECTIONS -> "readInputText"
            else -> "readInput"
        }
        Files.writeString(mainTestFile.toPath(), """
            import util.readInput
            import util.readInputText
            import kotlin.test.Test
            import kotlin.test.assertEquals
            import kotlin.test.assertTrue
            
            class ${dayPrefix}Test {
                @Test
                fun part1_test() {
                    val result = ${dayPrefix}($readFunction("${dayPrefix}_test")).part1()
                    assertEquals(2, result)
                }

                @Test
                fun part1_real() {
                    val result = ${dayPrefix}($readFunction("$dayPrefix")).part1()
                    assertEquals(2, result)
                }
                
                @Test
                fun part2_test() {
                    val result = ${dayPrefix}($readFunction("${dayPrefix}_test")).part2()
                    assertEquals(2, result)
                }

                @Test
                fun part2_real() {
                    val result = ${dayPrefix}($readFunction("$dayPrefix")).part2()
                    assertEquals(2, result)
                }
            }
        """.trimIndent())

        println("Downloading test input")
        Files.createDirectories(testResourcesDir)
        testResourcesDir.resolve("${dayPrefix}_test.txt").toFile().createNewFile()
        val inputTxtFile = testResourcesDir.resolve("$dayPrefix.txt").toFile()
        Files.writeString(inputTxtFile.toPath(), downloadInput(DAYNUM))
        println("Input downloaded to ${inputTxtFile.toPath()}")
    }

    private fun getHttpClient(): HttpClient {
        CookieHandler.setDefault(CookieManager())
        val cookieManager = CookieHandler.getDefault() as CookieManager
        val sessionCookieValue = aocSessionFile.readText().trim()
        val aocSessionCookie = HttpCookie("session", sessionCookieValue).apply {
            path = "/"
            version = 0
        }
        cookieManager.cookieStore.add(URI("https://adventofcode.com"), aocSessionCookie)
        return HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .connectTimeout(Duration.ofSeconds(10))
            .build()
    }

    private fun downloadInput(dayNum: Int): String {
        val client = getHttpClient()
        val req = HttpRequest.newBuilder()
            .uri(URI.create("https://adventofcode.com/$YEAR/day/$dayNum/input"))
            .GET().build()

        return client.send(req, HttpResponse.BodyHandlers.ofString()).body().trim()
    }

}