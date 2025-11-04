package com.br444n.constructionmaterialtrack.core.security

import android.content.Context
import android.util.Log

/**
 * Utility class for testing security validation
 */
class SecurityTester(private val context: Context) {
    
    private val strings = InputValidator.createStrings(context)
    
    companion object {
        private const val TAG = "SecurityTester"
        
        /**
         * Create SecurityTester with context
         */
        fun create(context: Context): SecurityTester {
            return SecurityTester(context)
        }
    }
    
    /**
     * Test all validation functions with malicious inputs
     */
    fun runSecurityTests(): SecurityTestResults {
        Log.d(TAG, "Starting security validation tests...")
        
        val results = SecurityTestResults()
        
        // Test SQL Injection patterns
        results.sqlInjectionTests = testSqlInjection()
        
        // Test XSS patterns
        results.xssTests = testXssAttacks()
        
        // Test input length limits
        results.lengthTests = testInputLengths()
        
        // Test numeric validation
        results.numericTests = testNumericValidation()
        
        // Test special characters
        results.specialCharTests = testSpecialCharacters()
        
        // Test diacritics support
        results.diacriticsTests = testDiacriticsSupport()
        
        Log.d(TAG, "Security tests completed. Results: $results")
        return results
    }
    
    private fun testSqlInjection(): List<TestResult> {
        val sqlInjectionInputs = listOf(
            "'; DROP TABLE users; --",
            "' OR '1'='1' --",
            "'; DELETE FROM projects; --",
            "' UNION SELECT * FROM users --",
            "'; INSERT INTO users VALUES ('hacker', 'password'); --",
            "' OR 1=1 --",
            "admin'--",
            "' OR 'x'='x",
            "'; EXEC xp_cmdshell('dir'); --"
        )
        
        return sqlInjectionInputs.map { input ->
            val result = InputValidator.validateProjectName(input, strings)
            TestResult(
                input = input,
                expected = false, // Should be blocked
                actual = result.isValid,
                passed = !result.isValid, // Test passes if validation fails (blocks malicious input)
                errorMessage = result.errorMessage
            )
        }
    }
    
    private fun testXssAttacks(): List<TestResult> {
        val xssInputs = listOf(
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "javascript:alert('XSS')",
            "<svg onload=alert('XSS')>",
            "<iframe src=javascript:alert('XSS')></iframe>",
            "<body onload=alert('XSS')>",
            "<div onclick=alert('XSS')>Click me</div>",
            "vbscript:msgbox('XSS')",
            "<script src='http://evil.com/xss.js'></script>"
        )
        
        return xssInputs.map { input ->
            val result = InputValidator.validateProjectName(input, strings)
            TestResult(
                input = input,
                expected = false,
                actual = result.isValid,
                passed = !result.isValid,
                errorMessage = result.errorMessage
            )
        }
    }
    
    private fun testInputLengths(): List<TestResult> {
        val lengthTests = listOf(
            // Valid lengths
            TestCase("Short name", "ValidProject", true),
            TestCase("Max length project", "A".repeat(100), true),
            TestCase("Max length description", "A".repeat(500), true),
            
            // Invalid lengths
            TestCase("Too long project", "A".repeat(101), false),
            TestCase("Too long description", "A".repeat(501), false),
            TestCase("Empty required field", "", false)
        )
        
        return lengthTests.map { test ->
            val result = when (test.description) {
                "Too long description", "Max length description" -> 
                    InputValidator.validateDescription(test.input, strings)
                else -> 
                    InputValidator.validateProjectName(test.input, strings)
            }
            
            TestResult(
                input = test.input,
                expected = test.shouldPass,
                actual = result.isValid,
                passed = result.isValid == test.shouldPass,
                errorMessage = result.errorMessage
            )
        }
    }
    
    private fun testNumericValidation(): List<TestResult> {
        val numericTests = listOf(
            // Valid numbers
            TestCase("Valid price", "123.45", true),
            TestCase("Valid quantity", "100", true),
            TestCase("Zero value", "0", true),
            TestCase("Decimal", "0.99", true),
            
            // Invalid numbers
            TestCase("Negative price", "-10.50", false),
            TestCase("Letters in price", "abc123", false),
            TestCase("Multiple decimals", "12.34.56", false),
            TestCase("Too large", "9999999", false),
            TestCase("Special chars", "12$34", false)
        )
        
        return numericTests.map { test ->
            val result = InputValidator.validatePrice(test.input, strings)
            TestResult(
                input = test.input,
                expected = test.shouldPass,
                actual = result.isValid,
                passed = result.isValid == test.shouldPass,
                errorMessage = result.errorMessage
            )
        }
    }
    
    private fun testSpecialCharacters(): List<TestResult> {
        val specialCharTests = listOf(
            // Valid characters
            TestCase("Alphanumeric", "Project123", true),
            TestCase("With spaces", "My Project", true),
            TestCase("Basic punctuation", "Project-Name_v1.0", true),
            TestCase("Parentheses", "Project (Version 1)", true),
            
            // Invalid characters
            TestCase("HTML chars", "Project<>Name", false),
            TestCase("Quotes", "Project\"Name'Test", false),
            TestCase("Ampersand", "Project&Name", false),
            TestCase("Backslash", "Project\\Name", false),
            TestCase("Pipe", "Project|Name", false)
        )
        
        return specialCharTests.map { test ->
            val result = InputValidator.validateProjectName(test.input, strings)
            TestResult(
                input = test.input,
                expected = test.shouldPass,
                actual = result.isValid,
                passed = result.isValid == test.shouldPass,
                errorMessage = result.errorMessage
            )
        }
    }
    
    private fun testDiacriticsSupport(): List<TestResult> {
        val diacriticsTests = listOf(
            // Spanish diacritics
            TestCase("Spanish accents", "Construcción", true),
            TestCase("Spanish ñ", "Señalización", true),
            TestCase("Spanish mixed", "Niño García", true),
            TestCase("Spanish project", "Edificación Residencial", true),
            TestCase("Spanish materials", "Materiales de Construcción", true),
            
            // French diacritics
            TestCase("French accents", "Matériaux", true),
            TestCase("French circumflex", "Bâtiment", true),
            TestCase("French cedilla", "Français", true),
            TestCase("French mixed", "Hôtel de luxe", true),
            TestCase("French project", "Château de Versailles", true),
            
            // Mixed languages
            TestCase("Mixed Spanish-French", "Café Español", true),
            TestCase("Mixed with English", "Project Français", true),
            
            // Edge cases
            TestCase("All uppercase", "CONSTRUCCIÓN", true),
            TestCase("Mixed case", "Señalización Française", true),
            TestCase("With numbers", "Proyecto 2024", true)
        )
        
        return diacriticsTests.map { test ->
            val result = InputValidator.validateProjectName(test.input, strings)
            TestResult(
                input = test.input,
                expected = test.shouldPass,
                actual = result.isValid,
                passed = result.isValid == test.shouldPass,
                errorMessage = result.errorMessage
            )
        }
    }
    
    /**
     * Print test results to console
     */
    fun printTestResults(results: SecurityTestResults) {
        Log.i(TAG, "=== SECURITY TEST RESULTS ===")
        
        printCategoryResults("SQL Injection Tests", results.sqlInjectionTests)
        printCategoryResults("XSS Attack Tests", results.xssTests)
        printCategoryResults("Length Validation Tests", results.lengthTests)
        printCategoryResults("Numeric Validation Tests", results.numericTests)
        printCategoryResults("Special Character Tests", results.specialCharTests)
        printCategoryResults("Diacritics Support Tests", results.diacriticsTests)
        
        val totalTests = results.getTotalTests()
        val passedTests = results.getPassedTests()
        
        Log.i(TAG, "=== SUMMARY ===")
        Log.i(TAG, "Total Tests: $totalTests")
        Log.i(TAG, "Passed: $passedTests")
        Log.i(TAG, "Failed: ${totalTests - passedTests}")
        Log.i(TAG, "Success Rate: ${(passedTests * 100 / totalTests)}%")
    }
    
    private fun printCategoryResults(category: String, tests: List<TestResult>) {
        Log.i(TAG, "\n--- $category ---")
        tests.forEach { test ->
            val status = if (test.passed) "✅ PASS" else "❌ FAIL"
            Log.i(TAG, "$status: '${test.input.take(30)}${if (test.input.length > 30) "..." else ""}'")
            if (!test.passed) {
                Log.w(TAG, "  Expected: ${test.expected}, Got: ${test.actual}")
                Log.w(TAG, "  Error: ${test.errorMessage}")
            }
        }
    }
    
    // Data classes for test results
    data class TestCase(
        val description: String,
        val input: String,
        val shouldPass: Boolean
    )
    
    data class TestResult(
        val input: String,
        val expected: Boolean,
        val actual: Boolean,
        val passed: Boolean,
        val errorMessage: String
    )
    
    data class SecurityTestResults(
        var sqlInjectionTests: List<TestResult> = emptyList(),
        var xssTests: List<TestResult> = emptyList(),
        var lengthTests: List<TestResult> = emptyList(),
        var numericTests: List<TestResult> = emptyList(),
        var specialCharTests: List<TestResult> = emptyList(),
        var diacriticsTests: List<TestResult> = emptyList()
    ) {
        fun getTotalTests(): Int = 
            sqlInjectionTests.size + xssTests.size + lengthTests.size + 
            numericTests.size + specialCharTests.size + diacriticsTests.size
            
        fun getPassedTests(): Int = 
            sqlInjectionTests.count { it.passed } +
            xssTests.count { it.passed } +
            lengthTests.count { it.passed } +
            numericTests.count { it.passed } +
            specialCharTests.count { it.passed } +
            diacriticsTests.count { it.passed }
    }
}