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
| Over-specified mock interactions | verify with exact counts, call ordering, verifyNoMoreInteractions (see Mock Anti-Patterns section) | Negative |
| Testing internal details | ArgumentCaptor deep inspection, verify(never()) mirroring branches, type hierarchy assertions, high verify-to-assert ratio (see Mock Anti-Patterns section) | Negative |

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
| Mock tautology | Mock return value configured then asserted on the same mock with no production code in between (see Mock Anti-Patterns section) | Negative |
| No production code exercised | All objects in test are mocks; no real class instantiated or invoked (see Mock Anti-Patterns section) | Negative |

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

## Mock Anti-Patterns

Tests that use mocking frameworks can appear structurally perfect (clean naming, single assertion, no I/O) while providing zero value. These four anti-patterns are collectively known as "test theatre" -- tests that look like tests but don't verify production behaviour. No mainstream static analysis tool (SonarQube, PMD, ESLint, tsDetect, testsmells.org) has rules for these patterns as of 2026.

### AP1 -- Mock Tautology (Severity: Critical)

A test that configures a mock's return value and then asserts that the same mock returns that value, with no production code in between. Logically equivalent to `x = 5; assert x == 5`.

**Affects**: N (Necessary) primary, M (Maintainable) secondary

**Detection heuristic**: The same mock object that has its return value configured is also the object whose return value is asserted, with no real class instantiation between setup and assertion.

| Language | Mock Setup Pattern | Assert Pattern |
|---|---|---|
| Java (Mockito) | `when(\w+\.\w+\(.*?\))\.thenReturn\(\w+\)` | `assertEquals` on same mock's method return |
| Python (unittest.mock) | `(\w+)\.(\w+)\.return_value\s*=\s*(\w+)` | `assert \1.\2(...) == \3` or `assertEqual(\3, \1.\2(...))` |
| Python (pytest-mock) | `mocker\.Mock\(\)` with `.return_value =` | same as unittest.mock |
| JavaScript (Jest) | `jest\.fn\(\)\.mockReturnValue\(` | `expect(mock.method(...)).toEqual(value)` |
| JavaScript (Sinon) | `sinon\.stub\(\)\.returns\(` | `assert.deepEqual(mock.method(...), value)` |
| Go (testify) | `(\w+)\.On\("\w+".*\)\.Return\(\w+` | `assert.Equal(t, value, mock.Method(...))` |
| Go (gomock) | `(\w+)\.EXPECT\(\)\.\w+\(.*\)\.Return\(\w+` | `assert.Equal` on same mock's method return |
| C# (Moq) | `(\w+)\.Setup\(.*\)\.Returns\(\w+\)` | `Assert.*(\w+\.Object\.\w+\()` |
| C# (NSubstitute) | `(\w+)\.\w+\(.*\)\.Returns\(\w+\)` | `Assert.*(\1\.\w+\()` |

**Key discriminator**: If a production class is instantiated between mock setup and assertion (`new RealClass(mock)` or equivalent), it is NOT a tautology.

### AP2 -- No Production Code Exercised (Severity: Critical)

A test where every object is a mock/stub/fake and no real class is instantiated or invoked. The test exercises only mock framework machinery. Superset of AP1: all mock tautologies have no production code, but AP2 also includes verify-only tests.

**Affects**: N (Necessary) primary, M (Maintainable) secondary, T (First) secondary

**Detection heuristic**: Test method contains mock creation and assertions/verifications, but no instantiation of a non-mock class.

| Language | Mock Creation Indicator | Production Code Indicator (absence = flag) |
|---|---|---|
| Java (Mockito) | `mock\(\w+\.class\)`, `@Mock`, `Mockito\.(mock\|spy)\(` | `new [^M]\w+\(` (new non-Mock class) |
| Python | `Mock\(`, `MagicMock\(`, `patch\(`, `mocker\.(Mock\|patch)` | Any class instantiation that is not Mock/MagicMock/patch |
| JavaScript (Jest) | `jest\.fn\(`, `jest\.mock\(`, `jest\.spyOn\(` | `new \w+\(` (new real class) |
| JavaScript (Sinon) | `sinon\.(stub\|spy\|mock)\(` | `new \w+\(` |
| Go (testify) | `new\(Mock\w+\)` | `\w+\{` or `New\w+\(` (non-mock struct literal or constructor) |
| Go (gomock) | `NewMock\w+\(` | non-mock struct literal or constructor |
| C# (Moq) | `new Mock<`, `Mock\.Of<` | `new [^M]\w+\(` |
| C# (NSubstitute) | `Substitute\.For<` | `new \w+\(` |

**False positive mitigation**: Tests where the SUT is injected via `setUp`/`@BeforeEach`/`beforeEach` are not flagged -- check setup methods for production class instantiation before flagging.

### AP3 -- Over-Specified Mock Interactions (Severity: High)

Tests that use verify() with exact call counts, strict argument matchers, and/or call ordering constraints. Tests describe HOW the software works rather than WHAT it achieves. Any behaviour-preserving refactoring breaks them.

**Affects**: M (Maintainable) primary, A (Atomic) secondary

**Severity tiers within AP3**:

| Signal | Severity | Rationale |
|---|---|---|
| Exact call count: `verify(mock, times(N))` | Medium | Couples to implementation call count |
| Call ordering: `InOrder`, `callOrder`, `gomock.InOrder` | High | Ordering is almost always an implementation detail |
| No more interactions: `verifyNoMoreInteractions`, `VerifyNoOtherCalls` | High | Prevents any future internal changes |
| Exact argument matching on >3 fields via argThat/MatchedBy | Medium | Over-constrains the interaction contract |

| Language | Exact Count | Ordering | No More Interactions |
|---|---|---|---|
| Java (Mockito) | `verify\(\w+,\s*times\(\d+\)\)` | `InOrder\s+\w+\s*=\s*inOrder\(` | `verifyNoMoreInteractions\(` |
| Python | `assert_called_once_with\(`, `.call_count\s*==` | `assert_has_calls\(.*any_order\s*=\s*False` | -- |
| JavaScript (Jest) | `toHaveBeenCalledTimes\(\d+\)` | `.mock\.invocationCallOrder` | -- |
| JavaScript (Sinon) | `calledOnce`, `calledTwice`, `callCount` | `sinon\.assert\.callOrder\(` | -- |
| Go (testify) | `.Once\(\)`, `.Times\(\d+\)`, `AssertNumberOfCalls\(` | -- | `AssertExpectations\(` (when all On() use .Once/.Times) |
| Go (gomock) | `.Times\(\d+\)` | `gomock\.InOrder\(` | -- |
| C# (Moq) | `Times\.(Exactly\|Once\|AtMostOnce)\(` | `MockSequence` | `VerifyNoOtherCalls\(\)` |
| C# (NSubstitute) | `.Received\(\d+\)\.` | -- | -- |

**Note**: Simple `verify(mock).method()` without `times()` is NOT flagged -- verifying a side effect occurred is legitimate. Only exact counts, ordering, and exhaustive checks are signals.

### AP4 -- Testing Internal Details (Severity: High)

Tests coupled to implementation through mechanisms subtler than reflection: ArgumentCaptor deep inspection, verify(never()) mirroring control flow branches, type hierarchy assertions, and high verify-to-assert ratios. These tests break on behaviour-preserving refactorings.

**Affects**: M (Maintainable) primary, U (Understandable) secondary

**Composite signals** (flag when 2+ present in a single test method):

| Signal | Weight | Detection |
|---|---|---|
| ArgumentCaptor / call_args deep inspection | 3 | Captor capture + getter chain on captured value |
| Call ordering verification (InOrder) | 3 | Same patterns as AP3 ordering |
| verify(never()) paired with verify() | 2 | `never()` in same test that has other verify() calls |
| Type checking assertions | 2 | `instanceof`, `IsAssignableFrom`, `assertIsInstance`, `toBeInstanceOf` |
| Internal property access on captured args | 2 | Underscore-prefixed properties (`._field`), `.internal.` imports |
| High verify-to-assert ratio (>3:1) | 1 | Count verify/Received/AssertCalled vs assertEquals/assertTrue/expect().toBe |

| Language | ArgumentCaptor / call_args | verify(never()) | Type Check |
|---|---|---|---|
| Java (Mockito) | `ArgumentCaptor<\w+>.*forClass` + `captor\.getValue\(\)\.get` | `verify\(\w+,\s*never\(\)\)` | `instanceof` in assertion |
| Python | `.call_args\[` + property access on captured value | `assert_not_called\(\)` | `assertIsInstance\(`, `assert isinstance\(` |
| JavaScript (Jest) | `.mock\.calls\[\d+\]\[\d+\]\.` (deep property access) | `not\.toHaveBeenCalled\(\)` | `toBeInstanceOf\(` |
| JavaScript (Sinon) | `.args\[\d+\]\[\d+\]\.` | `notCalled` | -- |
| Go (testify) | `mock\.MatchedBy\(func.*\{` with deep field access | `AssertNotCalled\(` | type assertions `\.\(\*?\w+\)` |
| Go (gomock) | -- (gomock lacks argument captors) | -- | type assertions |
| C# (Moq) | `It\.Is<\w+>\(.*=>.*\.` (deep lambda inspection) | `Times\.Never` | `Assert\.IsAssignableFrom<`, `Assert\.IsType<` |
| C# (NSubstitute) | `Arg\.Is<\w+>\(.*=>` | `.DidNotReceive\(\)\.` | `Assert\.IsAssignableFrom<` |

### Mock Anti-Pattern Signal Overlap

| Signal | Properties Affected |
|---|---|
| Mock tautology (AP1) | N (negative), M (negative) |
| No production code exercised (AP2) | N (negative), M (negative), T (negative) |
| Over-specified interactions (AP3) | M (negative), A (negative) |
| Testing internal details (AP4) | M (negative), U (negative) |
| verify(never()) | M (negative) -- AP3 and AP4 overlap |
| Call ordering (InOrder) | M (negative) -- AP3 and AP4 overlap |

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
| Mock creation (Mockito) | `mock\(\w+\.class\)`, `@Mock`, `Mockito\.(mock\|spy)\(` | Mock context (AP1-AP4) |
| Mock tautology (Mockito) | `when\(\w+\.\w+\(.*\)\)\.thenReturn\(\w+\)` then assert on same mock | N, M (negative) |
| Exact count verify | `verify\(\w+,\s*times\(\d+\)\)` | M (negative -- AP3) |
| Call ordering | `InOrder\s+\w+\s*=\s*inOrder\(` or `inOrder\.verify\(` | M (negative -- AP3) |
| No more interactions | `verifyNoMoreInteractions\(` or `verifyNoInteractions\(` | M (negative -- AP3) |
| ArgumentCaptor inspection | `ArgumentCaptor<\w+>` + `captor\.getValue\(\)\.get` | M (negative -- AP4) |
| verify(never()) | `verify\(\w+,\s*never\(\)\)` | M (negative -- AP4) |

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
| Mock creation | `Mock\(`, `MagicMock\(`, `patch\(`, `mocker\.(Mock\|patch\|MagicMock)` | Mock context (AP1-AP4) |
| Mock tautology | `\w+\.\w+\.return_value\s*=\s*\w+` then assert on same mock method | N, M (negative) |
| Exact count assert | `assert_called_once_with\(`, `.call_count\s*==\s*\d+` | M (negative -- AP3) |
| Ordered call assert | `assert_has_calls\(.*any_order\s*=\s*False` | M (negative -- AP3) |
| call_args inspection | `.call_args\[` + property access on captured value | M (negative -- AP4) |
| assert_not_called | `assert_not_called\(\)` | M (negative -- AP4) |
| Type check assertion | `assertIsInstance\(`, `assert\s+isinstance\(` | M (negative -- AP4) |

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
| Mock creation (Jest) | `jest\.fn\(`, `jest\.mock\(`, `jest\.spyOn\(` | Mock context (AP1-AP4) |
| Mock creation (Sinon) | `sinon\.(stub\|spy\|mock)\(` | Mock context (AP1-AP4) |
| Mock tautology (Jest) | `jest\.fn\(\)\.mockReturnValue\(` then `expect(mock.method()).toEqual(value)` | N, M (negative) |
| Mock tautology (Sinon) | `sinon\.stub\(\)\.returns\(` then assert on same stub return | N, M (negative) |
| Exact count (Jest) | `toHaveBeenCalledTimes\(\d+\)` | M (negative -- AP3) |
| Exact count (Sinon) | `calledOnce`, `calledTwice`, `calledThrice`, `callCount` | M (negative -- AP3) |
| Call ordering (Jest) | `.mock\.invocationCallOrder` | M (negative -- AP3) |
| Call ordering (Sinon) | `sinon\.assert\.callOrder\(` | M (negative -- AP3) |
| mock.calls inspection | `.mock\.calls\[\d+\]\[\d+\]\.` (deep property access) | M (negative -- AP4) |
| not.toHaveBeenCalled | `not\.toHaveBeenCalled\(\)` | M (negative -- AP4) |
| Type check | `toBeInstanceOf\(` | M (negative -- AP4) |

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
| Mock creation (testify) | `new\(Mock\w+\)` | Mock context (AP1-AP4) |
| Mock creation (gomock) | `gomock\.NewController`, `NewMock\w+\(` | Mock context (AP1-AP4) |
| Mock tautology (testify) | `\w+\.On\("\w+".*\)\.Return\(\w+` then `assert\.Equal\(t,\s*value,\s*mock\.Method\(` | N, M (negative) |
| Mock tautology (gomock) | `\.EXPECT\(\)\.\w+\(.*\)\.Return\(\w+` then assert on same mock | N, M (negative) |
| Exact count (testify) | `.Once\(\)`, `.Times\(\d+\)`, `AssertNumberOfCalls\(` | M (negative -- AP3) |
| Exact count (gomock) | `.Times\(\d+\)` | M (negative -- AP3) |
| Call ordering (gomock) | `gomock\.InOrder\(` | M (negative -- AP3) |
| AssertExpectations | `AssertExpectations\(` (when all On() use .Once/.Times) | M (negative -- AP3) |
| AssertNotCalled | `AssertNotCalled\(` | M (negative -- AP4) |
| MatchedBy deep inspect | `mock\.MatchedBy\(func.*\{` with deep field access | M (negative -- AP4) |

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
| Mock creation (Moq) | `new\s+Mock<`, `Mock\.Of<` | Mock context (AP1-AP4) |
| Mock creation (NSubstitute) | `Substitute\.For<` | Mock context (AP1-AP4) |
| Mock tautology (Moq) | `\.Setup\(.*\)\.Returns\(\w+\)` then `Assert.*\.Object\.\w+\(` | N, M (negative) |
| Mock tautology (NSubstitute) | `\w+\.\w+\(.*\)\.Returns\(\w+\)` then assert on same substitute | N, M (negative) |
| Exact count (Moq) | `Times\.(Exactly\|Once\|AtMostOnce\|AtLeastOnce)\(` | M (negative -- AP3) |
| Exact count (NSubstitute) | `.Received\(\d+\)\.` | M (negative -- AP3) |
| No other calls (Moq) | `VerifyNoOtherCalls\(\)` | M (negative -- AP3) |
| Strict mock (Moq) | `MockBehavior\.Strict` | M (negative -- AP3) |
| Times.Never (Moq) | `Times\.Never` | M (negative -- AP4) |
| DidNotReceive (NSubstitute) | `.DidNotReceive\(\)\.` | M (negative -- AP4) |
| Type check (C#) | `Assert\.IsAssignableFrom<`, `Assert\.IsType<`, `Assert\.IsInstanceOf<` | M (negative -- AP4) |

## Detection Priorities

When analyzing a test file, collect signals in this order (most impactful first):

1. **Identify language and framework** from imports/annotations (including mocking framework)
2. **Count test methods** (denominators for density calculations)
3. **Scan for high-severity negatives**: sleep, reflection, shared static state, ordering annotations
4. **Scan for mock anti-patterns** (AP1-AP4): mock tautologies, no production code exercised, over-specified interactions, internal detail testing
5. **Count assertions per test method** (G scoring)
6. **Analyze naming patterns** (U, T scoring)
7. **Check for organizational structure** (nested classes, describe blocks)
8. **Scan for I/O patterns** (R, F scoring)
9. **Identify positive patterns** (parameterized tests, builders, parallel markers)

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
| Mock tautology (AP1) | N (negative), M (negative) |
| No production code (AP2) | N (negative), M (negative), T (negative) |
| Over-specified interactions (AP3) | M (negative), A (negative) |
| Testing internal details (AP4) | M (negative), U (negative) |
| verify(never()) / assert_not_called | M (negative) -- AP3 and AP4 overlap |
| Call ordering (InOrder) | M (negative) -- AP3 and AP4 overlap |
