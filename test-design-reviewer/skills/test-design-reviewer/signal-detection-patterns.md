---
name: signal-detection-patterns
description: Static detection heuristics for each Farley property, language-specific patterns for Java, Python, JavaScript, Go, and C#
---

# Signal Detection Patterns

Static analysis detects *structural* test quality. It cannot detect wrong assertions, missing edge cases, or misleading names. The LLM assessment phase addresses those semantic gaps.

## Per-Property Signal Tables

### U -- Understandable

| Signal | Detection Method | Type |
|---|---|---|
| Descriptive test names | Method names contain behavior words: should, when, given, returns, throws, expects | Positive |
| DisplayName/description annotations | @DisplayName, @Description, docstrings on test methods | Positive |
| Nested/grouped organization | @Nested classes, describe/context blocks, test classes per behavior | Positive |
| Arrange-Act-Assert structure | Three distinct blocks: variable declarations, method call, assertion | Positive |
| Comments explaining WHY | Comments preceding assertions or test blocks | Positive |
| Magic numbers in assertions | Numeric literals in assertion arguments without named constants | Negative |
| Cryptic names | test1, test2, testMethod, myTest patterns | Negative |

### M -- Maintainable

| Signal | Detection Method | Type |
|---|---|---|
| Test helper abstractions | Reusable builder/factory patterns in test utilities | Positive |
| Behavior-focused assertions | Asserting on outputs/effects rather than internal state | Positive |
| Reflection usage | Field.setAccessible, getDeclaredField, getDeclaredMethod | Negative |
| Private member access | Testing private/internal methods directly | Negative |
| Implementation coupling | Test imports internal classes rather than public API | Negative |

### R -- Repeatable

| Signal | Detection Method | Type |
|---|---|---|
| Thread.sleep() calls | Literal pattern match: `Thread.sleep`, `time.sleep`, `sleep(`, `await.*delay`, `setTimeout` | Negative |
| File system operations | File, Path, InputStream, OutputStream, open(), fs.read, os.path | Negative |
| Network calls | HTTP client, socket, URL connection, fetch, requests.get, axios | Negative |
| Database access | Connection, Statement, EntityManager, cursor, session.query | Negative |
| System time dependency | System.currentTimeMillis, LocalDateTime.now, Date(), datetime.now(), Date.now() without injection | Negative |
| Random without seed | Random(), Math.random(), random.random() without deterministic seed | Negative |
| Environment variable reads | System.getenv, os.environ, process.env without mocking | Negative |

### A -- Atomic

| Signal | Detection Method | Type |
|---|---|---|
| Fresh instance per test | Each test creates its own objects; no shared references | Positive |
| Parallelizable | No ordering constraints, no shared state; t.Parallel() in Go | Positive |
| Shared mutable static state | Static fields modified across tests; class-level mutable variables | Negative |
| Test execution ordering | @Order, @FixMethodOrder, @TestMethodOrder, ordered=True | Negative |
| Shared instance state | Instance fields without per-test reset (no @BeforeEach reinit) | Negative |
| Complex setUp/tearDown | Extensive setup suggesting shared dependencies; >15 lines in setup | Negative |

### N -- Necessary

| Signal | Detection Method | Type |
|---|---|---|
| Parameterized tests | @ParameterizedTest, @Theory, pytest.mark.parametrize, test.each, table-driven tests | Positive |
| Redundant test methods | Multiple tests with identical assertion patterns on same method | Negative |
| Trivial assertions | assertTrue(true), assertNotNull on constructor, expect(1).toBe(1) | Negative |
| Framework testing | Tests that verify language/framework behavior, not application code | Negative |
| Ignored/disabled tests | @Ignore, @Disabled, @Skip, pytest.mark.skip, it.skip, t.Skip | Negative |
| Empty test bodies | Test methods with no executable statements | Negative |

### G -- Granular

| Signal | Detection Method | Type |
|---|---|---|
| Single logical assertion | One assertion or multiple assertions verifying one outcome | Positive |
| Multiple unrelated assertions | >1 assertion per test verifying different behaviors | Negative |
| Assertion count | Raw count of assert* calls per test method (metric, not direct signal) | Metric |
| Multi-behavior names | Test method name suggests multiple behaviors: And, Also, Then (in middle of name) | Negative |
| Mega-tests | >20 lines in test body or >5 assertions | Negative |

### F -- Fast

| Signal | Detection Method | Type |
|---|---|---|
| Pure computation | No I/O, no external dependencies in test body | Positive |
| Thread.sleep() | Same as Repeatable -- also impacts speed | Negative |
| File I/O | File system access in test body | Negative |
| Network calls | HTTP/socket usage in test body | Negative |
| Database access | JDBC/ORM operations in test body | Negative |
| Large inline test data | Inline datasets >50 lines in test method | Negative |

### T -- First (TDD Evidence)

| Signal | Detection Method | Type |
|---|---|---|
| Behavior-driven naming | Tests named for behaviors: shouldX, whenX_thenY, given_when_then | Positive |
| Test structure drives API | Tests exercise public API, not implementation paths | Positive |
| Arrange-Act-Assert clarity | Clean AAA structure suggests specification-first thinking | Positive |
| Tests mirror implementation | Test class hierarchy mirrors production class hierarchy exactly | Negative |
| Coverage patches | Tests that only cover exception paths or edge cases added later | Negative |

Future enhancement: git history analysis (test files committed before/alongside production files) would strengthen T scoring. Feasible via `git log` within the agent.

## Language-Specific Detection Patterns

### Java (JUnit 5 / JUnit 4)

| Pattern | Regex / Detection | Maps To |
|---|---|---|
| Test method | `@Test` annotation | Test method boundary |
| Assertion | `assert(Equals\|True\|False\|Throws\|NotNull\|Null\|Same\|ArrayEquals)` | G (count), various |
| Nested class | `@Nested` | U (positive) |
| Display name | `@DisplayName` | U (positive) |
| Disabled | `@Disabled` (JUnit 5), `@Ignore` (JUnit 4) | N (negative) |
| Ordered | `@Order`, `@TestMethodOrder`, `@FixMethodOrder` | A (negative) |
| Parameterized | `@ParameterizedTest`, `@ValueSource`, `@CsvSource`, `@MethodSource` | N (positive) |
| Reflection | `setAccessible\|getDeclaredField\|getDeclaredMethod` | M (negative) |
| Sleep | `Thread\.sleep` | R, F (negative) |
| BeforeEach | `@BeforeEach` (JUnit 5), `@Before` (JUnit 4) | A (context) |

### Python (pytest / unittest)

| Pattern | Regex / Detection | Maps To |
|---|---|---|
| Test method | `def test_` or `async def test_` | Test method boundary |
| Assertion (pytest) | `assert `, `pytest.raises`, `pytest.approx` | G (count), various |
| Assertion (unittest) | `self\.assert(Equal\|True\|False\|Raises\|In\|IsNone\|IsNotNone)` | G (count), various |
| Fixture | `@pytest.fixture` | A (context) |
| Skip | `@pytest.mark.skip`, `@unittest.skip` | N (negative) |
| Parametrize | `@pytest.mark.parametrize` | N (positive) |
| Sleep | `time\.sleep` | R, F (negative) |
| File I/O | `open\(`, `Path\(`, `os\.path` | R, F (negative) |
| Env vars | `os\.environ`, `os\.getenv` | R (negative) |
| Datetime | `datetime\.now\(\)`, `date\.today\(\)` | R (negative) |

### JavaScript / TypeScript (Jest / Vitest)

| Pattern | Regex / Detection | Maps To |
|---|---|---|
| Test method | `(it\|test)\s*\(`, `(it\|test)\.only\(` | Test method boundary |
| Assertion | `expect\(.*\)\.(toBe\|toEqual\|toHaveBeenCalled\|toThrow\|toMatch\|toContain)` | G (count), various |
| Describe block | `describe\(` | U (positive, organizational) |
| Nested describe | Nested `describe` blocks | U (positive) |
| Skip | `(describe\|it\|test)\.skip` | N (negative) |
| Each (parameterized) | `(it\|test)\.each`, `describe.each` | N (positive) |
| Sleep | `setTimeout`, `await.*delay`, `jest.advanceTimersByTime` | R, F (context -- timer mocking is acceptable) |
| beforeAll/afterAll | `beforeAll`, `afterAll` | A (context -- shared setup) |
| Fetch/HTTP | `fetch\(`, `axios`, `supertest`, `nock` | R, F (negative unless mocked) |

### Go (testing)

| Pattern | Regex / Detection | Maps To |
|---|---|---|
| Test function | `func Test\w+\(t \*testing\.T\)` | Test method boundary |
| Subtest | `t\.Run\(` | U, G (positive -- organization and granularity) |
| Assertion | `t\.(Error\|Errorf\|Fatal\|Fatalf\|Fail)` | G (count), various |
| Parallel | `t\.Parallel\(\)` | A (positive) |
| Skip | `t\.Skip` | N (negative) |
| Table-driven | `tests := \[\]struct` or `tt := \[\]struct` | N (positive), G (positive) |
| File I/O | `os\.(Open\|Create\|ReadFile\|WriteFile)` | R, F (negative) |
| HTTP | `http\.(Get\|Post\|NewRequest)`, `httptest` | R, F (context -- httptest is acceptable) |
| Sleep | `time\.Sleep` | R, F (negative) |

### C# (NUnit / xUnit)

| Pattern | Regex / Detection | Maps To |
|---|---|---|
| Test method (NUnit) | `\[Test\]`, `\[TestCase\]` | Test method boundary |
| Test method (xUnit) | `\[Fact\]`, `\[Theory\]` | Test method boundary |
| Assertion | `Assert\.(AreEqual\|IsTrue\|IsFalse\|Throws\|IsNull\|IsNotNull)` | G (count), various |
| Ignore | `\[Ignore\]` (NUnit), no direct xUnit equivalent | N (negative) |
| Order | `\[Order\(\d+\)\]` | A (negative) |
| InlineData | `\[InlineData\]`, `\[TestCase\]` | N (positive) |
| Reflection | `GetType\(\)\.GetField\|GetType\(\)\.GetMethod\|BindingFlags` | M (negative) |
| Sleep | `Thread\.Sleep` | R, F (negative) |

## Detection Priorities

When analyzing a test file, collect signals in this order (most impactful first):

1. **Identify language and framework** from imports/annotations
2. **Count test methods** (denominators for density calculations)
3. **Scan for high-severity negatives**: sleep, reflection, shared static state, ordering annotations
4. **Count assertions per test method** (G scoring)
5. **Analyze naming patterns** (U, T scoring)
6. **Check for organizational structure** (nested classes, describe blocks)
7. **Scan for I/O patterns** (R, F scoring)
8. **Identify positive patterns** (parameterized tests, builders, parallel markers)

## Signal Overlap

Some signals affect multiple properties. When a signal is detected, attribute it to all relevant properties:

| Signal | Properties Affected |
|---|---|
| Thread.sleep() | R (negative), F (negative) |
| File I/O | R (negative), F (negative) |
| Network calls | R (negative), F (negative) |
| Behavior-driven naming | U (positive), T (positive) |
| Arrange-Act-Assert | U (positive), T (positive) |
| Shared static state | A (negative), R (negative) |
| Reflection | M (negative) |
| Ordering annotations | A (negative) |
