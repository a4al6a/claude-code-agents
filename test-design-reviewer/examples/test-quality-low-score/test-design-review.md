# Test Design Review

## Farley Index: 1.34 / 10.0 (Critical)

Tests may be harmful; consider rewriting from scratch. This test file violates every one of Dave Farley's eight Properties of Good Tests. It contains shared mutable static state, explicit test ordering, file system I/O, Thread.sleep calls, reflection to access private fields, cryptic naming, mega-tests, redundant tests, framework-testing assertions, and zero behavioral organization. The test suite provides negative value: it creates maintenance burden without meaningfully verifying production behavior.

### Property Breakdown

| Property | Static | LLM | Blended | Weight | Weighted | Key Evidence |
|---|---|---|---|---|---|---|
| Understandable (U) | 0.2 | 1.5 | 0.7 | 1.50x | 1.04 | 10/12 methods have cryptic names (test1, test2, testEverything); zero @Nested, zero @DisplayName; magic numbers throughout |
| Maintainable (M) | 4.2 | 2.0 | 3.3 | 1.50x | 4.94 | Reflection access to private `position` field (line 91-92); shared static parser instance never reset; tests coupled to internal state |
| Repeatable (R) | 0.3 | 1.5 | 0.8 | 1.25x | 0.95 | Thread.sleep (lines 226, 232); file I/O (lines 34, 197-213); System.currentTimeMillis (line 25); System.nanoTime timing assertion (line 116); shared mutable static state (lines 19-22) |
| Atomic (A) | 0.3 | 1.5 | 0.8 | 1.00x | 0.79 | Shared mutable static fields: parser, testLog, testCounter, lastResult (lines 19-22); @Order annotation (line 266); test2 asserts test1 ran first (line 78); verifyAllTestsRan checks all 11 others executed (line 269) |
| Necessary (N) | 0.8 | 2.0 | 1.3 | 1.00x | 1.30 | testAdditionAgain duplicates test1 (line 123-131); testAdditionOnceMore duplicates again (line 135-141); testTestFramework asserts assertTrue(true) (lines 241-244); testEverything re-tests all operations already covered |
| Granular (G) | 0.5 | 2.0 | 1.1 | 1.00x | 1.11 | testEverything has 22 assertions across 7 categories (lines 145-186); test1 has 6 assertions (lines 56-61); testMagicNumbers has 4 assertions with no explanation (lines 254-257); average 5.6 assertions per method |
| Fast (F) | 0.7 | 2.5 | 1.4 | 0.75x | 1.06 | Thread.sleep(500) twice in testWithDelay totaling 1 second (lines 226, 232); file I/O read/write cycles in testWithFileLogging (lines 197-213); file write in @BeforeAll (line 34) |
| First / TDD (T) | 0.4 | 1.5 | 0.9 | 1.00x | 0.86 | Zero behavior-driven names; tests mirror implementation rather than specifying behavior; reflection tests internal `position` field; testEverything is a coverage dump, not specification; no AAA structure |

### Signal Summary

| Signal | Count | Affects | Severity |
|---|---|---|---|
| Cryptic test names (test1, test2, testEverything, etc.) | 10 methods | U, T | High |
| Shared mutable static state (parser, testLog, testCounter, lastResult) | 4 fields, 12 methods affected | A, R | High |
| Thread.sleep() | 2 calls (lines 226, 232) | R, F | High |
| File system I/O (FileWriter, FileReader, PrintWriter) | 3 methods (setupAll, testWithFileLogging, teardownAll) | R, F | High |
| Reflection / private field access (getDeclaredField, setAccessible) | 1 method (line 91-92) | M | High |
| @Order annotation (explicit test ordering) | 1 method (line 266) | A | High |
| Test-order dependency (asserts other tests ran) | 2 methods (test2 line 78, verifyAllTestsRan line 269) | A | High |
| System.currentTimeMillis (log file name) | 1 occurrence (line 25) | R | Medium |
| System.nanoTime timing assertion | 1 method (line 116) | R | Medium |
| Trivial assertions (assertTrue(true), assertFalse(false)) | 4 assertions (lines 241-244) | N | High |
| Redundant tests (duplicate addition testing) | 2 methods (testAdditionAgain, testAdditionOnceMore) | N | Medium |
| Mega-test (>20 assertions) | 1 method (testEverything, 22 assertions) | G | High |
| Multi-assertion tests (>5 assertions) | 3 methods (test1: 6, testEverything: 22, testMagicNumbers: 4) | G | Medium |
| Magic numbers without explanation | 4 assertions (lines 254-257) | U | Medium |
| Zero @Nested / @DisplayName / @ParameterizedTest | 0 positive signals across all 12 methods | U, N, G | Medium |
| Zero behavior-driven naming (should/when/given) | 0 positive signals across all 12 methods | U, T | High |

### Tautology Theatre Analysis

Tests whose outcome is predetermined by their own setup, independent of production code. The defining test: "Would this test still pass if all production code were deleted?" If yes, it is tautology theatre.

#### Mock Tautologies

None detected.

#### Mock-Only Tests

None detected.

#### Trivial Tautologies

Assertions that are always true regardless of any code: `assertTrue(true)`, `assertEquals(1, 1)`, `assertNotNull(new Object())`.

| # | Test Method | Line | Assertion |
|---|---|---|---|
| 1 | testTestFramework | 241 | `assertTrue(true)` |
| 2 | testTestFramework | 242 | `assertFalse(false)` |
| 3 | testTestFramework | 243 | `assertNotNull(new Object())` |
| 4 | testTestFramework | 244 | `assertEquals(1, 1)` |

#### Framework Tests

None detected.

#### Tautology Theatre Summary

**4** tautology theatre instances across **1** of **12** test methods: 0 mock tautologies, 0 mock-only tests, 4 trivial tautologies, 0 framework tests. These tests provide zero verification of production behaviour and create false confidence in test coverage.

### Top 5 Worst Offenders

1. **ExpressionParserTest.java:145 `testEverything()`** -- Farley ~0.5/10 -- Mega-test with 22 assertions covering 7 unrelated behaviors (arithmetic, parentheses, decimals, negatives, complex expressions, nested parentheses, whitespace, error conditions). If any assertion fails, the failure message gives no clue which behavior is broken. Name describes nothing about what is being verified. This single method should be at least 7 separate tests.

2. **ExpressionParserTest.java:267 `verifyAllTestsRan()`** -- Farley ~0.5/10 -- Exists solely to enforce execution order. Uses @Order(Integer.MAX_VALUE) and asserts that testLog.size() == 11 and testCounter >= 11. This is anti-atomic by definition: it cannot pass unless all other tests have already executed in a specific context. It verifies the test harness, not production behavior.

3. **ExpressionParserTest.java:220 `testWithDelay()`** -- Farley ~1.0/10 -- Contains two Thread.sleep(500) calls totaling 1 full second of dead time for a single test that verifies `7*8 == 56`. The sleep serves no purpose. This test is both slow and unreliable under CI load.

4. **ExpressionParserTest.java:191 `testWithFileLogging()`** -- Farley ~1.0/10 -- Writes to /tmp via PrintWriter, reads back via BufferedReader, and searches for "25.0" in the file content. Depends on file system availability, timing of writes, and correctness of the LOG_FILE path which uses System.currentTimeMillis(). The assertion on line 211 (`assertTrue(found, "Result should be logged to file")`) searches for "25.0" but the computed result is actually 25.0 (100/4), so this is a coincidental pass. Contains a subtle bug: line 206 searches for "25.0" in lines that include "Result: 25.0" -- fragile string matching.

5. **ExpressionParserTest.java:237 `testTestFramework()`** -- Farley ~0.5/10 -- Four assertions that test only the JUnit framework itself: `assertTrue(true)`, `assertFalse(false)`, `assertNotNull(new Object())`, `assertEquals(1, 1)`. Zero production code is exercised. This test would pass even if ExpressionParser did not exist. It is the textbook definition of a test that adds no value.

### Recommendations

1. **[HIGH IMPACT -- targets U at 1.5x weight] Rename all test methods to describe behavior using given/when/then or shouldX patterns.** Replace `test1()` with methods like `shouldAddTwoNumbers()`, `shouldMultiplyTwoNumbers()`. Replace `testEverything()` with separate tests per behavior: `shouldRespectOperatorPrecedence()`, `shouldHandleNestedParentheses()`, `shouldRejectMalformedExpressions()`. This alone would transform the suite's documentation value.

2. **[HIGH IMPACT -- targets A at 1.0x and R at 1.25x weight] Eliminate all shared mutable static state.** Change `parser` from a `static` field initialized in `@BeforeAll` to an instance field initialized in `@BeforeEach`. Remove `testLog`, `testCounter`, and `lastResult` entirely. Delete `verifyAllTestsRan()`. Each test must be runnable in isolation and in any order.

3. **[HIGH IMPACT -- targets R at 1.25x weight] Remove all file system I/O and timing dependencies.** Delete `LOG_FILE`, the `@BeforeAll` file write, `testWithFileLogging()`, and the `@AfterAll` cleanup. Replace `testPerformance()` (which uses `System.nanoTime` with a hard threshold) with a JMH benchmark or remove it entirely -- performance tests do not belong in unit test suites.

4. **[MEDIUM IMPACT -- targets N at 1.0x weight] Delete redundant and valueless tests.** Remove `testAdditionAgain()` (duplicates test1), `testAdditionOnceMore()` (duplicates both), and `testTestFramework()` (tests the framework, not production code). Use `@ParameterizedTest` with `@CsvSource` to consolidate arithmetic operation tests into a single parameterized test.

5. **[MEDIUM IMPACT -- targets F at 0.75x weight] Remove Thread.sleep calls and eliminate the reflection test.** The `testWithDelay()` sleeps 1 second for no reason. The `testInternalState()` method uses reflection to access the private `position` field -- testing implementation details that will break on any refactoring. Both should be deleted and replaced with behavior-focused tests.

### Per-Method Signal Detail

| Method | Line | Assertions | Signals |
|---|---|---|---|
| test1() | 47 | 6 | Cryptic name; shared state write (lastResult, testLog); multi-assertion (6); no AAA structure |
| test2() | 69 | 3 | Cryptic name; depends on test1 via testLog assertion; shared state write; order-dependent |
| testInternalState() | 86 | 1 | Reflection (getDeclaredField, setAccessible); tests private field; shared state write |
| testPerformance() | 105 | 1 | System.nanoTime timing assertion; loop-based benchmark; flaky threshold; shared state write |
| testAdditionAgain() | 122 | 3 | Redundant (duplicates test1 addition cases); shared state write |
| testAdditionOnceMore() | 135 | 1 | Redundant (exact duplicate of 1+1 from test1 and testAdditionAgain); shared state write |
| testEverything() | 145 | 22 | Mega-test; 7 behavior categories; cryptic name; shared state write |
| testWithFileLogging() | 191 | 2 | File I/O (write + read); fragile string matching; shared state write |
| testWithDelay() | 220 | 1 | Thread.sleep(500) x2; shared state write |
| testTestFramework() | 237 | 4 | Trivial assertions (assertTrue(true)); zero production code exercised; shared state write |
| testMagicNumbers() | 251 | 4 | Magic numbers without explanation; unclear purpose; shared state write |
| verifyAllTestsRan() | 267 | 2 | @Order(MAX_VALUE); asserts execution order of all other tests; anti-atomic |

### Methodology Notes
- Static/LLM blend: 60/40
- LLM model: claude-opus-4-6
- Files analyzed: 1 (no sampling needed -- single test file)
- Test methods analyzed: 12
- Language: Java
- Framework: JUnit 5 (org.junit.jupiter.api)
- Mocking framework: None detected
- T (First/TDD) note: Static evidence for TDD is necessarily indirect. The LLM assessment carries particular weight for this property. In this case, the complete absence of behavior-driven naming, the presence of implementation-mirroring tests (testInternalState), and the coverage-dump nature of testEverything strongly indicate test-after development.

### Dimensions Not Measured
Predictive, Inspiring, Composable, Writable (from Beck's Test Desiderata -- require runtime or team context)

### Reference
Based on Dave Farley's Properties of Good Tests:
https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/
