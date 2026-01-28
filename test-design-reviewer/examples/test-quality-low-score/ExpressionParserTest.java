package com.example.parser;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

/**
 * Tests for ExpressionParser
 *
 * WARNING: These tests demonstrate POOR test design practices.
 * They are intentionally written to violate Farley's testing principles
 * and serve as an example of what NOT to do.
 */
public class ExpressionParserTest {

    // Shared mutable state - violates Atomic principle
    private static ExpressionParser parser;
    private static List<String> testLog = new ArrayList<>();
    private static int testCounter = 0;
    private static double lastResult;

    // External dependency - violates Repeatable principle
    private static final String LOG_FILE = "/tmp/test_log_" + System.currentTimeMillis() + ".txt";

    @BeforeAll
    static void setupAll() throws Exception {
        parser = new ExpressionParser();
        testLog.clear();
        testCounter = 0;

        // Write to external file - violates Repeatable
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE))) {
            writer.println("Test run started at: " + new Date());
        }
    }

    @AfterAll
    static void teardownAll() throws Exception {
        // Cleanup that might fail - violates Repeatable
        new File(LOG_FILE).delete();
    }

    // Test 1: Cryptic name, tests too much - violates Understandable and Granular
    @Test
    void test1() {
        testCounter++;
        double r1 = parser.parse("1+1");
        double r2 = parser.parse("2*3");
        double r3 = parser.parse("10/2");
        double r4 = parser.parse("5-3");
        double r5 = parser.parse("1+2+3");
        double r6 = parser.parse("2*3*4");

        assertEquals(2.0, r1);
        assertEquals(6.0, r2);
        assertEquals(5.0, r3);
        assertEquals(2.0, r4);
        assertEquals(6.0, r5);
        assertEquals(24.0, r6);

        lastResult = r6; // Shared state
        testLog.add("test1 passed");
    }

    // Test 2: Depends on test1 having run - violates Atomic
    @Test
    void test2() {
        testCounter++;
        // This test subtly depends on shared state
        assertNotNull(parser, "Parser should exist from previous test");

        double result = parser.parse("24+1");
        assertEquals(25.0, result);

        // Check that we ran after test1 (bad practice)
        assertTrue(testLog.contains("test1 passed"), "test1 should have run first");

        lastResult = result;
        testLog.add("test2 passed");
    }

    // Test 3: Unclear purpose, implementation details exposed - violates Understandable
    @Test
    void testInternalState() throws Exception {
        testCounter++;
        ExpressionParser p = new ExpressionParser();

        // Testing private implementation details using reflection
        var field = ExpressionParser.class.getDeclaredField("position");
        field.setAccessible(true);

        p.parse("1+1");

        // This tests implementation, not behavior
        int pos = (int) field.get(p);
        assertTrue(pos >= 0, "Position should be non-negative after parsing");

        testLog.add("testInternalState passed");
    }

    // Test 4: Flaky test with timing - violates Repeatable
    @Test
    void testPerformance() {
        testCounter++;
        long start = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            parser.parse("1+2*3-4/2");
        }

        long elapsed = System.nanoTime() - start;

        // This assertion may fail randomly based on system load
        assertTrue(elapsed < 100_000_000, "Should complete in under 100ms, took: " + elapsed + "ns");

        testLog.add("testPerformance passed");
    }

    // Test 5: Redundant test - violates Necessary
    @Test
    void testAdditionAgain() {
        testCounter++;
        assertEquals(2.0, parser.parse("1+1"));
        assertEquals(4.0, parser.parse("2+2"));
        assertEquals(6.0, parser.parse("3+3"));
        // This is testing the same thing as test1

        testLog.add("testAdditionAgain passed");
    }

    // Test 6: Another redundant test - violates Necessary
    @Test
    void testAdditionOnceMore() {
        testCounter++;
        assertEquals(2.0, parser.parse("1+1"));
        // Exact duplicate of previous tests

        testLog.add("testAdditionOnceMore passed");
    }

    // Test 7: Massive test with unclear failures - violates Granular
    @Test
    void testEverything() {
        testCounter++;

        // All operations
        assertEquals(2.0, parser.parse("1+1"));
        assertEquals(6.0, parser.parse("2*3"));
        assertEquals(5.0, parser.parse("10/2"));
        assertEquals(2.0, parser.parse("5-3"));

        // Parentheses
        assertEquals(9.0, parser.parse("(1+2)*3"));
        assertEquals(7.0, parser.parse("1+(2*3)"));

        // Decimals
        assertEquals(2.5, parser.parse("1.5+1"));
        assertEquals(3.75, parser.parse("1.5*2.5"));

        // Negatives
        assertEquals(-1.0, parser.parse("-1"));
        assertEquals(0.0, parser.parse("-1+1"));

        // Complex expressions
        assertEquals(14.0, parser.parse("2+3*4"));
        assertEquals(20.0, parser.parse("(2+3)*4"));
        assertEquals(10.0, parser.parse("2*3+4"));
        assertEquals(14.0, parser.parse("2*(3+4)"));

        // Nested parentheses
        assertEquals(21.0, parser.parse("((1+2)*(3+4))"));
        assertEquals(10.0, parser.parse("(2+(3*(1+1)))"));

        // Whitespace (tested implicitly)
        assertEquals(4.0, parser.parse("  2  +  2  "));

        // Errors - testing multiple error conditions
        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(null));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("1+"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("(1+2"));
        assertThrows(ArithmeticException.class, () -> parser.parse("1/0"));

        testLog.add("testEverything passed");
    }

    // Test 8: Depends on external file system - violates Repeatable
    @Test
    void testWithFileLogging() throws Exception {
        testCounter++;

        double result = parser.parse("100/4");

        // Write result to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("Result: " + result);
        }

        // Read back and verify
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("25.0")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Result should be logged to file");
        }

        assertEquals(25.0, result);
        testLog.add("testWithFileLogging passed");
    }

    // Test 9: Slow test with unnecessary sleep - violates Fast
    @Test
    void testWithDelay() throws Exception {
        testCounter++;

        double result = parser.parse("7*8");

        // Unnecessary delay - violates Fast principle
        Thread.sleep(500);

        assertEquals(56.0, result);

        // Another unnecessary delay
        Thread.sleep(500);

        testLog.add("testWithDelay passed");
    }

    // Test 10: Test that tests the test framework - violates Necessary
    @Test
    void testTestFramework() {
        testCounter++;

        assertTrue(true, "True should be true");
        assertFalse(false, "False should be false");
        assertNotNull(new Object(), "New object should not be null");
        assertEquals(1, 1, "1 should equal 1");

        testLog.add("testTestFramework passed");
    }

    // Test 11: Obscure magic numbers - violates Understandable
    @Test
    void testMagicNumbers() {
        testCounter++;

        assertEquals(42.0, parser.parse("6*7"));
        assertEquals(3.14159, parser.parse("3.14159"), 0.00001);
        assertEquals(2.71828, parser.parse("2.71828"), 0.00001);
        assertEquals(1.41421, parser.parse("1.41421"), 0.00001);

        // What are these numbers? Why are we testing them?

        testLog.add("testMagicNumbers passed");
    }

    // Test 12: Order-dependent cleanup verification - violates Atomic
    @Test
    @Order(Integer.MAX_VALUE)
    void verifyAllTestsRan() {
        // This test MUST run last and verifies test execution order
        assertEquals(11, testLog.size(), "All other tests should have run first");
        assertTrue(testCounter >= 11, "Test counter should be at least 11");
    }
}
