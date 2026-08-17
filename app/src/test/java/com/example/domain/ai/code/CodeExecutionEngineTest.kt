package com.example.domain.ai.code

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CodeExecutionEngineTest {

    @Test
    fun testExecuteSql() = runBlocking {
        val query = "CREATE TABLE users (id INT, name TEXT); INSERT INTO users VALUES (1, 'Herman'); SELECT * FROM users;"
        val result = CodeExecutionEngine.execute(query, "sql")
        assertTrue(result.isSuccess)
        assertEquals(0, result.exitCode)
        assertTrue(result.output.isNotEmpty())
    }

    @Test
    fun testExecutePython() = runBlocking {
        val pyCode = """
            print("Halo Nusantara")
            print("Inference Active")
        """.trimIndent()

        val result = CodeExecutionEngine.execute(pyCode, "python")
        assertTrue(result.isSuccess)
        assertTrue(result.output.contains("Halo Nusantara"))
        assertTrue(result.output.contains("Inference Active"))
    }

    @Test
    fun testExecuteKotlin() = runBlocking {
        val ktCode = """
            fun main() {
                println("Kotlin Execution Engine Active")
            }
        """.trimIndent()

        val result = CodeExecutionEngine.execute(ktCode, "kotlin")
        assertTrue(result.isSuccess)
        assertTrue(result.output.contains("Kotlin Execution Engine Active"))
    }

    @Test
    fun testExecuteShell() = runBlocking {
        val shCode = """
            echo "Building APK"
            pwd
        """.trimIndent()

        val result = CodeExecutionEngine.execute(shCode, "bash")
        assertTrue(result.isSuccess)
        assertTrue(result.output.contains("Building APK"))
    }

    @Test
    fun testExecuteJson() = runBlocking {
        val jsonCode = "{\"model\": \"Nusantara-AI\", \"version\": 3.5}"
        val result = CodeExecutionEngine.execute(jsonCode, "json")
        assertTrue(result.isSuccess)
        assertTrue(result.output.contains("Nusantara-AI"))
    }
}
