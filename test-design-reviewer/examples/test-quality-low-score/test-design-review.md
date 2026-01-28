# Test Design Review: ExpressionParserTest.java

## Property Scores

| Property | Score | Evidence |
|----------|-------|----------|
| Understandable | 2/10 | Cryptic names (`test1`, `test2`), unclear purpose, magic numbers without explanation |
| Maintainable | 2/10 | Tests implementation details via reflection, tightly coupled to internal state |
| Repeatable | 2/10 | Depends on file system, timing-sensitive assertions, flaky performance tests |
| Atomic | 1/10 | Shared static state, tests depend on execution order, `verifyAllTestsRan` enforces order |
| Necessary | 2/10 | Multiple redundant tests (`testAdditionAgain`, `testAdditionOnceMore`), tests test framework |
| Granular | 2/10 | `testEverything` has 20+ assertions, failures don't pinpoint issues |
| Fast | 3/10 | Contains `Thread.sleep(1000ms)`, file I/O operations |
| First (TDD) | 2/10 | Tests clearly written after implementation, follow implementation structure |

## Farley Score: 2.1/10 (Critical)

**Calculation:**
```
(2×1.5 + 2×1.5 + 2×1.25 + 1×1.0 + 2×1.0 + 2×1.0 + 3×0.75 + 2×1.0) / 9
= (3 + 3 + 2.5 + 1 + 2 + 2 + 2.25 + 2) / 9
= 17.75 / 9
= 1.97 ≈ 2.1
```

## Detailed Analysis

### Understandable (2/10)

The tests fail dramatically at communicating intent:

**Cryptic naming:**
```java
@Test
void test1() { ... }  // What does this test?

@Test
void test2() { ... }  // What behavior is being verified?
```

**Magic numbers without context:**
```java
assertEquals(42.0, parser.parse("6*7"));
assertEquals(3.14159, parser.parse("3.14159"), 0.00001);
// Why these specific numbers? What's being demonstrated?
```

**Tests don't read like specifications** - a developer cannot understand the parser's behavior by reading these tests.

### Maintainable (2/10)

The tests are extremely brittle:

**Testing implementation details:**
```java
// Using reflection to access private fields
var field = ExpressionParser.class.getDeclaredField("position");
field.setAccessible(true);
int pos = (int) field.get(p);
```

Any refactoring of internal state will break tests even if behavior is unchanged.

**Tight coupling to execution environment:**
```java
private static final String LOG_FILE = "/tmp/test_log_" + System.currentTimeMillis() + ".txt";
```

### Repeatable (2/10)

Multiple sources of non-determinism:

**Timing-dependent assertions:**
```java
assertTrue(elapsed < 100_000_000, "Should complete in under 100ms");
// Will fail randomly under system load
```

**File system dependencies:**
```java
try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE))) { ... }
// File operations can fail for many environmental reasons
```

**Time-based identifiers:**
```java
private static final String LOG_FILE = "/tmp/test_log_" + System.currentTimeMillis() + ".txt";
// Different file each run, cleanup may fail
```

### Atomic (1/10)

Severe isolation violations:

**Shared mutable static state:**
```java
private static ExpressionParser parser;
private static List<String> testLog = new ArrayList<>();
private static int testCounter = 0;
private static double lastResult;
```

**Tests depend on execution order:**
```java
@Test
void test2() {
    assertTrue(testLog.contains("test1 passed"), "test1 should have run first");
}
```

**Explicit order enforcement:**
```java
@Test
@Order(Integer.MAX_VALUE)
void verifyAllTestsRan() {
    assertEquals(11, testLog.size(), "All other tests should have run first");
}
```

These tests **cannot be run in parallel** and **cannot be run individually**.

### Necessary (2/10)

Significant redundancy and waste:

**Duplicate tests:**
```java
@Test void testAdditionAgain() { assertEquals(2.0, parser.parse("1+1")); }
@Test void testAdditionOnceMore() { assertEquals(2.0, parser.parse("1+1")); }
// Exact same assertion in multiple tests
```

**Testing the test framework:**
```java
@Test
void testTestFramework() {
    assertTrue(true, "True should be true");
    assertFalse(false, "False should be false");
    // This tests JUnit, not the parser
}
```

### Granular (2/10)

Tests are far too broad:

**Mega-test with 20+ assertions:**
```java
@Test
void testEverything() {
    assertEquals(2.0, parser.parse("1+1"));
    assertEquals(6.0, parser.parse("2*3"));
    // ... 20+ more assertions
    assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
    assertThrows(ArithmeticException.class, () -> parser.parse("1/0"));
}
```

When this test fails, you must investigate all 20+ assertions to find the problem.

### Fast (3/10)

Unnecessary delays and I/O:

**Explicit sleeps:**
```java
@Test
void testWithDelay() throws Exception {
    Thread.sleep(500);  // Why?
    assertEquals(56.0, result);
    Thread.sleep(500);  // Adds 1 second per test run
}
```

**File I/O operations:**
```java
try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
    writer.println("Result: " + result);
}
```

### First/TDD (2/10)

Clear indicators that tests were written after implementation:

- Tests follow the implementation structure (testing internal `position` field)
- No evidence of test-driving design decisions
- Tests feel like "checkbox exercises" to achieve coverage
- `testEverything` is a classic "test-after" mega-test

## Top Recommendations

1. **Eliminate shared state** - Each test should create its own `ExpressionParser` instance. Remove all static fields used to track test state.

2. **Split `testEverything` into focused tests** - Create individual tests like `addition_of_two_numbers_returns_sum()`, `multiplication_has_higher_precedence_than_addition()`, etc.

3. **Remove timing and file dependencies** - Delete `testPerformance`, `testWithFileLogging`, and `testWithDelay`. If performance testing is needed, use a dedicated performance testing framework.

4. **Use descriptive test names** - Rename `test1` to `parsingSimpleAddition_returnsSumOfOperands()` or similar behavior-describing names.

5. **Delete redundant tests** - Remove `testAdditionAgain`, `testAdditionOnceMore`, `testTestFramework`, and `verifyAllTestsRan`.

6. **Remove reflection-based tests** - Delete `testInternalState`. Tests should verify behavior, not implementation details.

## Summary

This test suite is a textbook example of anti-patterns. It provides minimal value as documentation, is unreliable, unmaintainable, and actually makes refactoring the production code *harder* rather than enabling it. The shared state and order dependencies mean the tests cannot be parallelized or run individually, severely impacting developer productivity.

**Recommendation:** This test suite should be completely rewritten following TDD principles before any further development on the parser.

---

## Reference
This review is based on Dave Farley's Properties of Good Tests:
https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/
