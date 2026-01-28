# Test Design Review: ExpressionParserTest.java

## Property Scores

| Property | Score | Evidence |
|----------|-------|----------|
| Understandable | 9/10 | Descriptive names, nested classes organize by behavior, tests read like specifications |
| Maintainable | 9/10 | Tests verify behavior not implementation, proper abstractions with @Nested classes |
| Repeatable | 10/10 | No external dependencies, no timing, deterministic assertions |
| Atomic | 10/10 | Each test creates own parser instance, no shared state, fully parallelizable |
| Necessary | 9/10 | Each test documents distinct behavior, parameterized tests avoid duplication |
| Granular | 9/10 | Single assertion per test (or single logical assertion), failures pinpoint exact issues |
| Fast | 10/10 | Pure computation, no I/O, no delays, millisecond execution |
| First (TDD) | 8/10 | Tests clearly specify behavior first, structure suggests test-driven design |

## Farley Score: 9.2/10 (Exemplary)

**Calculation:**
```
(9×1.5 + 9×1.5 + 10×1.25 + 10×1.0 + 9×1.0 + 9×1.0 + 10×0.75 + 8×1.0) / 9
= (13.5 + 13.5 + 12.5 + 10 + 9 + 9 + 7.5 + 8) / 9
= 83.0 / 9
= 9.22 ≈ 9.2
```

## Detailed Analysis

### Understandable (9/10)

The tests excel at communicating intent:

**Descriptive test names that read as specifications:**
```java
@Test
@DisplayName("multiplication has higher precedence than addition")
void multiplicationBeforeAddition() { ... }

@Test
@DisplayName("can produce negative results")
void subtractingLargerFromSmaller_returnsNegative() { ... }
```

**Logical organization with @Nested classes:**
```java
@Nested
@DisplayName("when adding numbers")
class Addition { ... }

@Nested
@DisplayName("operator precedence")
class OperatorPrecedence { ... }
```

**Comments explain WHY, not WHAT:**
```java
// 2 + 3 * 4 = 2 + 12 = 14 (not 5 * 4 = 20)
double result = parser.parse("2+3*4");
```

A developer can understand the parser's complete behavior by reading these tests alone, without looking at implementation.

### Maintainable (9/10)

Tests are resilient to implementation changes:

**Tests verify behavior, not implementation:**
- No reflection to access private fields
- No testing of internal state
- Tests only use the public API

**Proper test structure:**
- Arrange-Act-Assert pattern throughout
- Clear separation of setup, execution, and verification

```java
@Test
void parsingSingleInteger_returnsItsValue() {
    ExpressionParser parser = new ExpressionParser();  // Arrange

    double result = parser.parse("42");                // Act

    assertEquals(42.0, result);                        // Assert
}
```

### Repeatable (10/10)

Tests are completely deterministic:

**No external dependencies:**
- No file system access
- No network calls
- No database connections

**No timing sensitivity:**
- No `Thread.sleep()`
- No time-based assertions
- No performance thresholds

**Pure functional tests:**
- Input → output verification only
- Same result every time, on any machine

### Atomic (10/10)

Perfect isolation between tests:

**Each test creates its own instance:**
```java
@Test
void addingTwoPositiveIntegers_returnsTheirSum() {
    ExpressionParser parser = new ExpressionParser();  // Fresh instance
    ...
}
```

**No shared state:**
- No `static` fields
- No `@BeforeAll` setup
- No `@AfterAll` cleanup
- No test execution order dependencies

**Tests can run:**
- In any order
- In parallel
- Individually
- As a full suite

### Necessary (9/10)

Each test adds distinct value:

**No redundant tests:**
- Each test covers a unique behavior
- No duplicate assertions across tests

**Parameterized tests consolidate similar cases:**
```java
@ParameterizedTest(name = "{0} = {1}")
@CsvSource({
    "1+2*3-4/2, 5.0",
    "(1+2)*(3-1), 6.0",
    ...
})
void complexExpressionsAreEvaluatedCorrectly(String expression, double expected) { ... }
```

**Tests document edge cases:**
- Division by zero
- Missing parentheses
- Invalid characters
- Negative numbers

### Granular (9/10)

Focused tests with clear failure diagnostics:

**Single assertion per test:**
```java
@Test
void multiplyingByZero_returnsZero() {
    ExpressionParser parser = new ExpressionParser();
    double result = parser.parse("999*0");
    assertEquals(0.0, result);  // One assertion, one behavior
}
```

**Exception tests verify specific conditions:**
```java
@Test
void dividingByZero_throwsArithmeticException() {
    ExpressionParser parser = new ExpressionParser();

    ArithmeticException exception = assertThrows(
        ArithmeticException.class,
        () -> parser.parse("5/0")
    );

    assertEquals("Division by zero", exception.getMessage());
}
```

When a test fails, you immediately know exactly what behavior broke.

### Fast (10/10)

Tests execute in milliseconds:

**Pure computation only:**
- No I/O operations
- No network calls
- No sleeps or delays
- No expensive setup

**Entire suite runs instantly:**
- Developer feedback loop is immediate
- Tests can be run continuously during development

### First/TDD (8/10)

Strong indicators of test-first design:

**Tests specify behavior, not implementation:**
- Focus on "what" the parser should do
- No knowledge of internal algorithms

**Logical progression of complexity:**
1. Single numbers
2. Single operations
3. Precedence rules
4. Parentheses
5. Edge cases

**Tests read as executable specification:**
The test suite documents the parser's complete contract, which is characteristic of TDD where tests define requirements before implementation.

Scored 8 instead of 9-10 because there's no accompanying history showing red-green-refactor cycles, though the structure strongly suggests TDD.

## Top Recommendations

1. **Consider adding property-based tests** - For a parser, property-based testing could verify invariants like `parse(x + " + 0") == parse(x)` across random inputs.

2. **Add boundary value tests** - Test very large numbers, very small decimals, and deeply nested parentheses to ensure robustness.

3. **Document the test organization** - A brief comment explaining the @Nested structure would help new developers navigate the test suite.

## Summary

This test suite exemplifies Dave Farley's principles. The tests serve as living documentation, are completely reliable, and provide a solid safety net for refactoring. Each test is focused, fast, and independent, enabling rapid development cycles.

**Key strengths:**
- Tests read like specifications
- Complete isolation enables parallel execution
- No external dependencies ensure reliability
- Focused tests pinpoint failures immediately

This is a model test suite that other projects should emulate.

---

## Reference
This review is based on Dave Farley's Properties of Good Tests:
https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/
