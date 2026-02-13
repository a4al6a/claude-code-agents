# Test Design Review

## Farley Index: 8.85 / 10.0 (Excellent)

High quality test suite with minor improvement opportunities. Tests read like a specification of the ExpressionParser's behavior, are fully deterministic, and exercise only the public API.

### Property Breakdown

| Property | Static | LLM | Blended | Weight | Weighted | Key Evidence |
|---|---|---|---|---|---|---|
| Understandable | 9.49 | 9.5 | 9.50 | 1.50x | 14.25 | 31/31 behavior-driven names, 10 @Nested classes, 41 @DisplayName annotations, AAA structure throughout |
| Maintainable | 9.36 | 9.0 | 9.22 | 1.50x | 13.83 | Zero mocks, zero reflection, all assertions on public API outputs, no implementation coupling |
| Repeatable | 9.08 | 10.0 | 9.45 | 1.25x | 11.81 | Pure computation, zero I/O, zero sleep, zero time/random/env dependencies |
| Atomic | 9.12 | 9.5 | 9.27 | 1.00x | 9.27 | Fresh ExpressionParser per test, no shared state, no ordering, fully parallelizable |
| Necessary | 4.31 | 8.5 | 5.99 | 1.00x | 5.99 | 2 @ParameterizedTest for variations; each test covers distinct behavior; static score low due to few parameterized markers |
| Granular | 9.01 | 9.0 | 9.00 | 1.00x | 9.00 | 29/31 tests have 1 assertion; 2 tests have 2 assertions forming logical groups; avg 1.06 assertions/test |
| Fast | 8.65 | 10.0 | 9.19 | 0.75x | 6.89 | Pure string parsing and arithmetic; zero I/O; microsecond execution per test |
| First (TDD) | 9.05 | 8.0 | 8.63 | 1.00x | 8.63 | Behavior-organized structure (not implementation-mirrored), public API focus; no git history to confirm test-first |

### Signal Summary

| Signal | Count | Affects | Severity |
|---|---|---|---|
| Behavior-driven method names | 31 | U+, T+ | Positive |
| @DisplayName annotations | 41 | U+ | Positive |
| @Nested class organization | 10 | U+ | Positive |
| Arrange-Act-Assert structure | 31 | U+, T+ | Positive |
| Fresh instance per test | 31 | A+ | Positive |
| Pure computation (no I/O) | 31 | R+, F+ | Positive |
| @ParameterizedTest usage | 2 | N+ | Positive |
| Comments explaining evaluation order | 7 | U+ | Positive |
| Multi-assertion tests (logical groups) | 2 | G- | Low |

Zero negative signals detected for: Thread.sleep, reflection, shared state, ordering, file I/O, network, database, system time, random, environment variables, disabled tests, empty tests, trivial assertions, cryptic names, mock anti-patterns.

### Tautology Theatre Analysis

Tests whose outcome is predetermined by their own setup, independent of production code. The defining test: "Would this test still pass if all production code were deleted?" If yes, it is tautology theatre.

#### Mock Tautologies

None detected.

#### Mock-Only Tests

None detected.

#### Trivial Tautologies

None detected.

#### Framework Tests

None detected.

#### Tautology Theatre Summary

**0** tautology theatre instances across **0** of **31** test methods. No tautology theatre detected.

### Top 5 Worst Offenders

This suite has no significantly weak tests. The following are the lowest-scoring methods, identified only because they have two assertions each (logical assertion groups, not true weaknesses):

1. `ExpressionParserTest.java:220` -- `dividingByZero_throwsArithmeticException` -- 2 assertions (assertThrows + assertEquals on message). Both verify the same error-reporting outcome. Score impact: negligible.
2. `ExpressionParserTest.java:327` -- `missingClosingParenthesis_throwsException` -- 2 assertions (assertThrows + assertTrue on message). Both verify the same error-reporting outcome. Score impact: negligible.
3. `ExpressionParserTest.java:377` -- `rejectsInvalidInput` -- Parameterized test that could be split further to distinguish null vs empty vs whitespace error messages, but combining them is a reasonable design choice.
4. `ExpressionParserTest.java:33` -- `parsingSingleInteger_returnsItsValue` -- Minor: creates a new parser on every test; a @BeforeEach could reduce repetition. However, explicit instantiation improves readability and isolation.
5. `ExpressionParserTest.java:432` -- `complexExpressionsAreEvaluatedCorrectly` -- 6 parameterized cases in one test. All test the same behavior (complex expression evaluation) so this is appropriate use of @CsvSource.

Note: None of these are genuine "offenders." This section is included for completeness. All tests score above 8.0 individually.

### Recommendations

1. **[N - Necessary] Consider parameterizing repetitive single-operation tests.** The Addition, Subtraction, Multiplication, and Division @Nested classes each contain 3 tests that follow the same pattern (basic operation, edge case, chaining/precision). These could use @ParameterizedTest with @CsvSource to reduce structural redundancy while maintaining readability. This would raise the static N score significantly. *Impact: would improve Farley Index by ~0.2 points.*

2. **[M - Maintainable] Extract parser instantiation to reduce duplication.** Every test method contains `ExpressionParser parser = new ExpressionParser();`. While this is explicit and readable, a `@BeforeEach` method or a factory method in the outer class would reduce the 31 repetitions to 1 line. This is a minor maintainability improvement -- the current approach is defensible as it keeps each test fully self-contained. *Impact: marginal.*

3. **[T - First/TDD] Add edge case tests for boundary behaviors.** To strengthen evidence of specification-first design, consider adding tests for: very large numbers (overflow), multiple decimal points in a number (`1.2.3`), consecutive operators (`5++3`), empty parentheses `()`. These boundary cases are the kind a TDD practitioner would discover during red-green-refactor cycles. *Impact: would strengthen both N and T scores.*

### Methodology Notes
- Static/LLM blend: 60/40
- LLM model: claude-opus-4-6
- Files analyzed: 1 (no sampling required -- under 50 test files)
- Test methods analyzed: 31 (including 2 @ParameterizedTest methods)
- Parameterized test cases: 11 total (5 in rejectsInvalidInput, 6 in complexExpressionsAreEvaluatedCorrectly)
- Language: Java
- Framework: JUnit 5 (Jupiter)
- Mocking framework: None detected
- Mock anti-patterns: N/A (no mocks in codebase)
- T (First/TDD) scoring note: Static evidence for TDD is inherently indirect. The LLM assessment carries additional interpretive weight for this property. No git history was analyzed.
- N (Necessary) static/LLM gap: The static score of 4.31 reflects the scorer's limited positive signal vocabulary -- only @ParameterizedTest counts as a positive signal for N. The LLM assessment of 8.5 reflects semantic analysis showing each test covers a genuinely distinct behavior. The blended score of 5.99 is conservatively low for this suite.

### Dimensions Not Measured
Predictive, Inspiring, Composable, Writable (from Beck's Test Desiderata -- require runtime or team context)

### Reference
Based on Dave Farley's Properties of Good Tests:
https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/
