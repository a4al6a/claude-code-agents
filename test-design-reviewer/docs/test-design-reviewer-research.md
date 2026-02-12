# Test Design Reviewer: Research and Design Specification

## 1. Research Summary

This document provides the evidence base for designing a **Test Design Reviewer** agent that analyzes test codebases and produces a quantitative **Farley Index** score (0-10), based on Dave Farley's eight Properties of Good Tests. The research covers the theoretical foundation, comparison with alternative frameworks, static detection heuristics for each property, the scoring formula, and architectural decisions for implementation as a Claude Code agent with deterministic Python-based scoring.

### 1.1 Primary Sources

| Source | Type | Relevance | Independence |
|---|---|---|---|
| Dave Farley, "TDD & The Properties of Good Tests" (LinkedIn, 2024) | Primary theory | Defines the 8 properties: Understandable, Maintainable, Repeatable, Atomic, Necessary, Granular, Fast, First | Author of framework |
| Kent Beck, "Test Desiderata" (Medium, 2019) | Alternative framework | 12 properties including Predictive, Inspiring, Writable, Structure-insensitive — comparison baseline | Independent |
| Gerard Meszaros, *xUnit Test Patterns* (Addison-Wesley, 2007) | Alternative framework | Goals of Test Automation: Tests as Specification, Documentation, Safety Net; 68 patterns, 18 test smells | Independent |
| Dave Farley, *Modern Software Engineering* (Addison-Wesley, 2022) | Book | Broader context: testability as engineering discipline, TDD as design tool | Author of framework |
| Dave Farley, *Continuous Delivery* (co-author, 2010) | Book | Foundation: tests as executable specifications enabling continuous deployment | Author of framework |
| testsmells.org — Test Smell Catalog | Taxonomy | 19 cataloged test smells with detection heuristics mapped to Farley properties | Independent |
| Peruma et al., "tsDetect" (2020) | Academic tool | AST-based test smell detection across 19 smell types | Independent |
| Pontillo et al., "Machine Learning-Based Test Smell Detection" (2024) | Academic | ML approaches to test smell classification | Independent |
| Spadini et al., "Test Smells 20 Years Later" (Springer, 2022) | Empirical study | Validity and reliability of test smell detection; severity thresholds | Independent |
| Reference implementation (test-design-reviewer/) | Prototype | Formula verification (arithmetic correctness) only — see limitations in section 10.1 | Derived from Farley |

### 1.2 Cross-Referenced Findings

**Finding 1: Farley's 8 properties map cleanly to detectable test smells** [Confidence: HIGH]
- Farley's "Atomic" maps to: Shared State (testsmells.org), Eager Test, Constructor Initialization
- Farley's "Repeatable" maps to: Mystery Guest, Sleepy Test, Resource Optimism
- Farley's "Granular" maps to: Assertion Roulette, Duplicate Assert, Eager Test
- Farley's "Understandable" maps to: Magic Number Test, Unknown Test, Default Test
- Cross-referenced across Farley's article, testsmells.org, and tsDetect paper (3 independent sources)

**Finding 2: Static heuristics achieve high recall for most test smells** [Confidence: HIGH]
- Assertion Roulette: counting assertion statements without messages — high precision (tsDetect, testsmells.org, Spadini 2022)
- Sleepy Test: detecting Thread.sleep() calls — near-perfect precision (all three sources agree)
- Mystery Guest: file/database class instantiation — moderate precision, some false positives (Spadini 2022 notes this)
- Eager Test and Assertion Roulette frequently co-occur (both describe tests involving "too much" — Spadini 2022 notes they appeared together in 12 out of their respective 20 and 16 occurrences in the study sample, though this is from a limited sample size)

**Finding 3: Weighting understandability and maintainability higher aligns with practitioner consensus** [Confidence: MEDIUM]
- Farley emphasizes tests as "specifications" and "documentation" — understandability is primary value
- *Modern Software Engineering* positions testability and maintainability as engineering foundations
- Beck's Test Desiderata similarly elevates Readable and Behavioral/Structure-insensitive (the maintainability pair)
- Meszaros's Goals of Test Automation foregrounds "Tests as Documentation" and "Tests as Specification"
- **Limitation**: No empirical study has validated that 1.5x is the optimal weight. The specific multiplier comes from the reference implementation, not from independent evidence. See section 10.1.

---

## 2. Framework Selection: Why Farley's Properties

### 2.1 Comparison of Test Quality Frameworks

Three major frameworks define what constitutes a "good test." This section compares them to justify selecting Farley's as the primary framework while acknowledging what the others contribute.

#### Kent Beck's Test Desiderata (2019)

> Source: [Kent Beck, "Test Desiderata" (Medium)](https://medium.com/@kentbeck_7670/test-desiderata-94150638a4b3), [testdesiderata.com](https://testdesiderata.com/)

Beck defines **12 properties** as "sliders" rather than absolutes:

| Property | Description | Farley Equivalent |
|---|---|---|
| Isolated | Same results regardless of execution order | Atomic |
| Composable | Test dimensions separately, combine results | *No direct equivalent* |
| Deterministic | Unchanged code → unchanged results | Repeatable |
| Fast | Quick execution | Fast |
| Writable | Cheap to write relative to code cost | *No direct equivalent* |
| Readable | Comprehensible, invoking motivation | Understandable |
| Behavioral | Sensitive to behavior changes | Maintainable (partial) |
| Structure-insensitive | Immune to internal refactoring | Maintainable (partial) |
| Automated | No human intervention required | *Assumed baseline* |
| Specific | Failures pinpoint root cause | Granular |
| Predictive | Passing tests → production-ready | *No direct equivalent* |
| Inspiring | Passing tests inspire confidence | *No direct equivalent* |

**Beck properties NOT in Farley**: Composable, Writable, Predictive, Inspiring.
**Farley properties NOT in Beck**: Necessary, First (TDD).

Beck's key insight: "No property should be given up without receiving a property of greater value in return." His framework treats properties as tradeoffs on sliders rather than binary pass/fail, which is more nuanced but harder to score quantitatively.

#### Gerard Meszaros's Goals of Test Automation (2007)

> Source: [Meszaros, *xUnit Test Patterns* (Addison-Wesley)](https://www.amazon.com/xUnit-Test-Patterns-Refactoring-Code/dp/0131495054), [xunitpatterns.com](http://xunitpatterns.com/)

Meszaros defines **goals** (outcomes) rather than properties (characteristics):

| Goal | Description | Farley Mapping |
|---|---|---|
| Tests as Specification | Tests define expected behavior before code | First (TDD) |
| Tests as Documentation | Tests explain what the system does | Understandable |
| Tests as Safety Net | Tests detect regressions | Maintainable |
| Defect Localization | Failures pinpoint the defect | Granular |
| Fully Automated | No manual intervention | *Assumed baseline* |
| Self-Checking | Tests verify their own correctness | *Assumed baseline* |
| Repeatable | Same results across runs | Repeatable |
| Robust | Tests resilient to environmental changes | Repeatable (partial) |

Meszaros also catalogs **18 test smells** with detection patterns — these are a direct input to our static analysis heuristics (section 3).

### 2.2 Selection Rationale

Farley's framework was selected as the primary scoring basis for these reasons:

| Criterion | Farley | Beck | Meszaros |
|---|---|---|---|
| **Quantitative scoring suitability** | 8 discrete properties, each assessable independently | 12 properties as "sliders" — harder to score discretely | Goals rather than properties — harder to measure |
| **Static detection coverage** | 7/8 properties have high-confidence static signals | ~8/12 properties detectable | Goals require runtime or human judgment |
| **Practitioner adoption** | Active YouTube channel, recent articles, TDD training courses | Highly influential practitioner, but Test Desiderata specifically uses a conceptual/philosophical framing without prescribed scoring | Reference book, less active |
| **TDD integration** | Explicitly includes "First" (TDD) as property | Does not prescribe TDD | Includes "Tests as Specification" but not explicitly TDD |
| **Scope clarity** | Focused on individual test method/file quality | Includes system-level concerns (Predictive, Composable) | Includes organizational goals (Fully Automated, Self-Checking) |

**What we take from Beck**: The Predictive and Inspiring properties are worth noting in the Knowledge Gaps section — they represent dimensions that our Farley-based scoring does not capture. Composable is relevant for integration test assessment but out of scope for initial implementation.

**What we take from Meszaros**: His test smell catalog directly feeds our static detection heuristics. His "Tests as Documentation" and "Tests as Specification" goals reinforce the high weighting of Understandable and First.

---

## 3. Dave Farley's Eight Properties of Good Tests

### 3.1 The Properties (from primary source)

> Source: [Dave Farley, "TDD & The Properties of Good Tests"](https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/)

| # | Property | Farley's Definition | Primary Load |
|---|---|---|---|
| U | **Understandable** | Tests should "describe what they are testing so that we can understand the goals of our software," focusing on system behavior rather than implementation details | Documentation value |
| M | **Maintainable** | Tests must "act as a defence of our system, breaking when we want them to," remaining easy to modify as the codebase evolves | Long-term value |
| R | **Repeatable** | Tests should "always pass or fail in the same way for a given version of the software," producing consistent results regardless of when or where they run | Reliability trust |
| A | **Atomic** | Tests need to "be isolated and focus on a single outcome," operating independently with no side-effects | Isolation guarantee |
| N | **Necessary** | Developers should avoid "creating tests for test sake," writing tests that actively guide development decisions | Value justification |
| G | **Granular** | Tests must "be small, simple and focused, and assert a single outcome," providing clear pass/fail with obvious problem indicators | Diagnostic precision |
| F | **Fast** | Since developers "will end up with lots of them," tests need efficiency | Feedback speed |
| T | **First** | In TDD, "write the test before writing code," strengthening all other properties | Design quality |

### 3.2 Why These Properties Matter

Farley's properties form a coherent system where each property reinforces the others:

- **First** (TDD) naturally produces **Understandable** and **Granular** tests because you write the test to express desired behavior before implementation exists
- **Atomic** tests that are isolated are inherently more **Repeatable** (no shared state = no environmental sensitivity)
- **Granular** tests are naturally **Fast** (small scope = quick execution)
- **Maintainable** tests that verify behavior rather than implementation remain **Necessary** longer (they don't become stale)

This interdependence means improvement in one property often cascades to others — and degradation in one often signals degradation in related properties.

---

## 4. Mapping Properties to Detectable Signals

### 4.1 Static Heuristic Catalog

The key design insight: each Farley property can be partially assessed through static analysis of test code patterns. The agent collects raw signal counts, then uses these as evidence for scoring.

**Important caveat**: Static analysis detects *structural* test quality, not *semantic* test quality. A test can have perfect structure (descriptive name, single assertion, no I/O) but verify the wrong thing entirely. The LLM assessment phase (section 7.1 Phase 2) addresses this gap.

#### U — Understandable

| Signal | Detection Method | Positive/Negative |
|---|---|---|
| Descriptive test names | Test method names contain behavior words (should, when, given, returns, throws) | Positive |
| Magic numbers in assertions | Numeric literals in assertion arguments without named constants | Negative |
| Comments explaining WHY | Comments preceding assertions or test blocks | Positive |
| Arrange-Act-Assert structure | Three distinct blocks in test body (variable declarations, method call, assertion) | Positive |
| DisplayName/description annotations | @DisplayName, @Description, docstrings | Positive |
| Nested/grouped organization | @Nested classes, describe/context blocks, test classes per behavior | Positive |
| Cryptic names | test1, test2, testMethod, myTest patterns | Negative |

#### M — Maintainable

| Signal | Detection Method | Positive/Negative |
|---|---|---|
| Reflection usage | Field.setAccessible, getDeclaredField, getDeclaredMethod | Negative |
| Private member access | Testing private/internal methods directly | Negative |
| Implementation coupling | Test imports internal classes rather than public API | Negative |
| Test helper abstractions | Reusable builder/factory patterns in test utilities | Positive |
| Behavior-focused assertions | Asserting on outputs/effects rather than internal state | Positive |

#### R — Repeatable

| Signal | Detection Method | Positive/Negative |
|---|---|---|
| Thread.sleep() calls | Literal pattern match | Negative |
| File system operations | File, Path, InputStream/OutputStream class usage | Negative |
| Network calls | HTTP client, socket, URL connection class usage | Negative |
| Database access | Connection, Statement, EntityManager class usage | Negative |
| System time dependency | System.currentTimeMillis, LocalDateTime.now, Date() without injection | Negative |
| Random without seed | Random(), Math.random() without deterministic seed | Negative |
| Environment variable reads | System.getenv, os.environ without mocking | Negative |

#### A — Atomic

| Signal | Detection Method | Positive/Negative |
|---|---|---|
| Shared mutable static state | static fields modified across tests | Negative |
| Test execution ordering | @Order, @FixMethodOrder, @TestMethodOrder annotations | Negative |
| Shared instance state | Instance fields without per-test reset (no @BeforeEach reinit) | Negative |
| setUp/tearDown complexity | Complex setup suggesting shared dependencies | Negative |
| Fresh instance per test | Each test creates its own objects (no shared references) | Positive |
| Parallelizable | No ordering constraints, no shared state | Positive |

#### N — Necessary

| Signal | Detection Method | Positive/Negative |
|---|---|---|
| Redundant test methods | Multiple tests with identical assertion patterns on same method | Negative |
| Trivial assertions | assertTrue(true), assertNotNull on constructor | Negative |
| Framework testing | Tests that verify language/framework behavior, not application code | Negative |
| Ignored/disabled tests | @Ignore, @Disabled, @Skip annotations | Negative |
| Empty test bodies | Test methods with no executable statements | Negative |
| Parameterized tests | Data-driven tests avoiding duplication | Positive |

#### G — Granular

| Signal | Detection Method | Positive/Negative |
|---|---|---|
| Multiple unrelated assertions | >1 assertion per test verifying different behaviors | Negative |
| Assertion count | Raw count of assert* calls per test method | Metric |
| Multi-behavior tests | Test method name suggests multiple behaviors (And, Also, Then) | Negative |
| Mega-tests | >20 lines in test body or >5 assertions | Negative |
| Single logical assertion | One assertion or multiple assertions verifying one outcome | Positive |

**Note on "single assertion"**: Farley's wording is "assert a single outcome," which is distinct from "single assertion statement." Multiple assertions that together verify one outcome (e.g., checking both status code and body of an HTTP response) are acceptable. The heuristic should detect multiple *unrelated* assertions, not multiple assertions that form a logical group. This aligns with Meszaros's "Single-Condition Test" pattern and the broader practitioner consensus (including Beck's Specific property).

#### F — Fast

| Signal | Detection Method | Positive/Negative |
|---|---|---|
| Thread.sleep() | Literal pattern match (also affects Repeatable) | Negative |
| File I/O | File system access in test body | Negative |
| Network calls | HTTP/socket usage in test body | Negative |
| Database access | JDBC/ORM operations in test body | Negative |
| Large test data | Inline datasets >50 lines | Negative |
| Pure computation | No I/O, no external dependencies | Positive |

#### T — First (TDD Evidence)

| Signal | Detection Method | Positive/Negative |
|---|---|---|
| Behavior-driven naming | Tests named for behaviors rather than methods (shouldX, whenX_thenY) | Positive |
| Test structure drives API | Tests exercise public API, not implementation paths | Positive |
| Tests mirror implementation structure | Test class hierarchy mirrors production class hierarchy exactly | Negative (suggests test-after) |
| Code coverage patches | Tests that only cover exception paths or edge cases added later | Negative |
| Arrange-Act-Assert clarity | Clean AAA suggests specification-first thinking | Positive |

**Future enhancement**: Git history analysis (checking whether test files were committed before or alongside production files) could significantly strengthen T scoring. This is feasible within a CLI agent via `git log` and would provide objective evidence of test-first practice. Deferred to Phase 3 implementation.

### 4.2 Language-Specific Detection Patterns

| Language | Test Framework | Assertion Pattern | Annotation Pattern |
|---|---|---|---|
| Java | JUnit 5 | `assert(Equals\|True\|False\|Throws\|NotNull)` | `@Test`, `@Nested`, `@DisplayName`, `@Disabled`, `@Order` |
| Java | JUnit 4 | `assert*`, `@Rule`, `@RunWith` | `@Test`, `@Ignore`, `@FixMethodOrder` |
| Python | pytest | `assert `, `pytest.raises`, `pytest.approx` | `@pytest.fixture`, `@pytest.mark.skip`, `@pytest.mark.parametrize` |
| Python | unittest | `self.assert*`, `self.setUp`, `self.tearDown` | `@unittest.skip`, `@unittest.expectedFailure` |
| JavaScript | Jest | `expect(...).toBe*`, `describe`, `it`, `test` | `describe.skip`, `it.skip`, `test.skip`, `beforeAll`, `afterAll` |
| TypeScript | Jest/Vitest | Same as JavaScript | Same as JavaScript |
| Go | testing | `t.Error*`, `t.Fatal*`, `t.Run` | `func Test*`, `t.Skip`, `t.Parallel` |
| C# | NUnit | `Assert.*` | `[Test]`, `[TestCase]`, `[Ignore]`, `[Order]` |
| C# | xUnit | `Assert.*` | `[Fact]`, `[Theory]`, `[InlineData]` |

---

## 5. The Farley Index: Scoring Formula

### 5.1 Formula

```
Farley Index = (U*1.5 + M*1.5 + R*1.25 + A*1.0 + N*1.0 + G*1.0 + F*0.75 + T*1.0) / 9.0
```

Where each property score (U, M, R, A, N, G, F, T) ranges from 0.0 to 10.0.

### 5.2 Weight Rationale

| Property | Weight | Rationale | Independent Support |
|---|---|---|---|
| U (Understandable) | 1.5x | Tests as documentation is their primary long-term value. Farley: tests should "describe what they are testing so that we can understand the goals of our software." | Beck's Readable; Meszaros's "Tests as Documentation" goal |
| M (Maintainable) | 1.5x | Test value diminishes to zero if they can't survive refactoring. Implementation-coupled tests become a liability rather than asset. | Beck's Behavioral + Structure-insensitive pair; Meszaros's "Tests as Safety Net" |
| R (Repeatable) | 1.25x | Trust in the test suite depends on determinism. A single flaky test erodes team confidence in all tests. | Beck's Deterministic; Meszaros's Repeatable goal |
| A (Atomic) | 1.0x | Isolation is fundamental but well-understood — most developers achieve baseline isolation. | Beck's Isolated |
| N (Necessary) | 1.0x | Redundant tests waste maintenance effort but are less harmful than other violations. | *Not directly in Beck or Meszaros — Farley-specific* |
| G (Granular) | 1.0x | Single-outcome focus aids debugging but has valid exceptions (logical assertion groups). | Beck's Specific |
| F (Fast) | 0.75x | Speed is optimizable after the fact. Slow but correct tests are preferable to fast but poorly designed ones. | Beck's Fast |
| T (First/TDD) | 1.0x | TDD evidence is the hardest to detect statically — it's often inferred from other properties. Lower confidence in automated detection. | Meszaros's "Tests as Specification" |

**Weight validation status**: The specific multipliers (1.5, 1.25, 1.0, 0.75) come from the reference implementation and reflect practitioner judgment. They have not been validated against empirical data (e.g., correlation between weighted scores and human expert ratings). The relative ordering (U,M > R > A,N,G,T > F) is supported by cross-framework consensus, but the exact magnitudes are subject to calibration.

### 5.3 Rating Scale

| Score | Rating | Interpretation |
|---|---|---|
| 9.0 - 10.0 | Exemplary | Model for the industry; tests serve as living documentation |
| 7.5 - 8.9 | Excellent | High quality with minor improvement opportunities |
| 6.0 - 7.4 | Good | Solid foundation with clear areas for improvement |
| 4.5 - 5.9 | Fair | Functional but needs significant attention to test design |
| 3.0 - 4.4 | Poor | Tests provide limited value; major refactoring needed |
| 0.0 - 2.9 | Critical | Tests may be harmful; consider rewriting from scratch |

### 5.4 Score Interpretation Examples (from reference implementation)

> **Note on evidence status**: The reference implementation examples below were purpose-built to demonstrate the scoring formula — the high-score example was designed to exhibit excellent practices and the low-score example was designed to violate every property. They verify the formula's *arithmetic correctness* (the formula produces expected numbers given known inputs) but do not validate the formula's *predictive accuracy* (whether the weights correlate with actual test quality as judged by practitioners). See section 10.1 for the calibration roadmap.

**Exemplary (9.2/10)**: ExpressionParserTest using JUnit 5 @Nested classes, behavior-driven naming, parameterized tests, fresh instances per test, single assertions, pure computation, Arrange-Act-Assert throughout.

**Critical (2.0/10)**: ExpressionParserTest using shared static state, test ordering dependencies, reflection to access private fields, Thread.sleep(), file I/O, mega-test with 20+ assertions, cryptic names (test1, test2), redundant tests. (Calculation: `(2*1.5 + 2*1.5 + 2*1.25 + 1*1.0 + 2*1.0 + 2*1.0 + 3*0.75 + 2*1.0) / 9 = 17.75 / 9 = 1.97`, rounded to 2.0.)

---

## 6. Per-Property Scoring Rubrics

### 6.1 Understandable (U)

| Score | Criteria |
|---|---|
| 9-10 | Tests read like specifications; behavior crystal clear without reading implementation; descriptive names, display annotations, nested organization |
| 7-8 | Tests clear with minor ambiguities; intent mostly obvious from names |
| 5-6 | Tests require some code inspection to understand purpose |
| 3-4 | Tests cryptic; heavy reliance on implementation details to understand |
| 1-2 | Names like test1/test2; no organization; magic numbers throughout |

**Key static signals**: naming pattern quality, magic number count, comment presence, organizational structure (nested/grouped).

### 6.2 Maintainable (M)

| Score | Criteria |
|---|---|
| 9-10 | Tests use proper abstractions; changes to implementation rarely break tests; verifies behavior not implementation |
| 7-8 | Good separation of concerns; occasional brittleness |
| 5-6 | Some coupling to implementation; moderate refactoring pain |
| 3-4 | Tightly coupled to implementation; tests break with minor changes |
| 1-2 | Uses reflection to access private fields; tests mirror implementation structure exactly |

**Key static signals**: reflection usage, private access, test helper abstractions, behavior vs. implementation assertions.

### 6.3 Repeatable (R)

| Score | Criteria |
|---|---|
| 9-10 | Completely deterministic; no external dependencies; same result every time, anywhere |
| 7-8 | Rarely flaky; minimal environmental dependencies |
| 5-6 | Occasional flakiness; some timing or state dependencies |
| 3-4 | File system, timing, or environment dependencies present |
| 1-2 | Thread.sleep, file I/O, network calls, system time, random without seed |

**Key static signals**: sleep calls, file/network/DB access, system time, environment reads, random usage.

### 6.4 Atomic (A)

| Score | Criteria |
|---|---|
| 9-10 | Completely isolated; no shared state; parallelizable |
| 7-8 | Mostly isolated; minor shared setup that doesn't cause ordering issues |
| 5-6 | Some shared state; test order sometimes matters |
| 3-4 | Heavy interdependencies; tests must run in specific order |
| 1-2 | Shared mutable static state; explicit ordering annotations; tests verify other tests ran |

**Key static signals**: static mutable fields, ordering annotations, shared instance state without reset, setup complexity.

### 6.5 Necessary (N)

| Score | Criteria |
|---|---|
| 9-10 | Every test adds unique value; no redundancy; guides development decisions; parameterized tests for variations |
| 7-8 | Most tests valuable; minor redundancy |
| 5-6 | Some tests feel like checkbox exercises; moderate redundancy |
| 3-4 | Several redundant tests; framework testing; trivial assertions |
| 1-2 | Many tests add no value; assertTrue(true); disabled tests accumulating |

**Key static signals**: duplicate assertion patterns, trivial assertions, ignored/disabled tests, empty test bodies, redundant method names.

### 6.6 Granular (G)

| Score | Criteria |
|---|---|
| 9-10 | Each test verifies a single outcome; failures pinpoint exact issues |
| 7-8 | Tests focused; occasional multiple assertions forming a logical group for one outcome |
| 5-6 | Tests cover multiple behaviors; failure diagnosis takes effort |
| 3-4 | Tests sprawling; multiple unrelated assertions |
| 1-2 | Mega-tests with 20+ assertions; testEverything() methods |

**Key static signals**: assertion count per method, test body line count, assertion diversity (testing multiple behaviors).

### 6.7 Fast (F)

| Score | Criteria |
|---|---|
| 9-10 | Pure computation; no I/O; millisecond execution |
| 7-8 | Tests quick; minor optimization opportunities |
| 5-6 | Some slow tests; suite takes noticeable time |
| 3-4 | File I/O or database calls present |
| 1-2 | Thread.sleep, network calls, heavy setup/teardown |

**Key static signals**: sleep calls, I/O class usage, network client usage, database client usage.

### 6.8 First/TDD (T)

| Score | Criteria |
|---|---|
| 9-10 | Clear evidence of test-first approach; tests drive design; behavior-focused names |
| 7-8 | Likely test-first; good design influence |
| 5-6 | Unclear if test-first; tests may be afterthoughts |
| 3-4 | Test structure mirrors implementation; likely test-after |
| 1-2 | Tests clearly written after code; follow implementation structure; coverage patches |

**Key static signals**: naming patterns (behavior vs. method names), API-level testing vs. implementation testing, AAA clarity.

---

## 7. Architectural Design Recommendations

### 7.1 Two-Phase Assessment

The reviewer should operate in two phases:

**Phase 1: Static Analysis (Python scripts, deterministic)**
- Parse test files, count signals per property
- Compute raw metrics: assertion counts, sleep presence, reflection usage, naming patterns
- Normalize metrics to per-property sub-scores using sigmoid normalization (see section 7.3)
- Produce a "static floor" score that is deterministic and reproducible

**Phase 2: LLM Assessment (agent judgment, controlled non-determinism)**
- Read tests holistically — does the test suite *feel* understandable?
- Assess naming quality semantically (not just pattern matching)
- Evaluate TDD evidence from design patterns that static analysis can't detect
- Adjust static scores up or down based on contextual judgment
- Produce a "judgment delta" for each property

**LLM Reproducibility Protocol** (analogous to cognitive-load-analyzer D4 protocol):
- Temperature: 0 (maximize determinism)
- Sampling: Evaluate all test files if < 50 files; SHA-256 deterministic selection (30%) for larger suites
- Structured rubric: LLM evaluates each property on the 1-10 rubric from section 6, providing specific code evidence
- Per-file evaluation: LLM produces a JSON-structured score per property per file, aggregated by the Python scorer
- Model recording: Record the model identifier in the report for reproducibility tracking

**Final score** = `0.60 * static_score + 0.40 * llm_score`, computed per property, then aggregated via the Farley Index formula. The 60/40 split follows the cognitive-load-analyzer D4 precedent, prioritizing deterministic measurement while incorporating semantic judgment.

### 7.2 Python Calculator Architecture

Following the cognitive-load-analyzer pattern, implement deterministic scoring in Python scripts:

```
skills/test-design-reviewer/lib/
  __init__.py
  core.py              # Sigmoid normalization, aggregation primitives
  signals.py           # Per-property signal extraction from test code
  scoring.py           # Property scoring formulas, Farley Index computation
  language_profiles.py # Language-specific patterns (Java, Python, JS, Go, C#)
  cli_calculator.py    # CLI entry point with JSON I/O
```

The agent invokes:
```bash
python ~/.claude/skills/test-design-reviewer/lib/cli_calculator.py \
  analyze-signals '{"test_code": "...", "language": "java"}'

python ~/.claude/skills/test-design-reviewer/lib/cli_calculator.py \
  compute-farley '{"U": 8, "M": 7, "R": 9, "A": 10, "N": 8, "G": 9, "F": 10, "T": 7}'

python ~/.claude/skills/test-design-reviewer/lib/cli_calculator.py \
  blend-scores '{"static": {"U": 7, ...}, "llm": {"U": 9, ...}}'
```

### 7.3 Signal-to-Score Mapping with Sigmoid Normalization

Following the cognitive-load-analyzer precedent, each property uses sigmoid normalization to map raw signal counts to the 0-10 scale. This provides:
- Smooth transitions around thresholds (no hard cliffs)
- Asymptotic bounds (scores approach but don't reach 0 or 10)
- Calibrated midpoints where "acceptable" transitions to "problematic"

```
raw_negative_density = negative_signal_count / total_test_methods
raw_positive_density = positive_signal_count / total_test_methods

# Negative signals use INVERTED sigmoid: more negatives → lower score
negative_component = (1 - sigmoid(raw_negative_density, midpoint_neg, steepness_neg)) * 10.0

# Positive signals use standard sigmoid: more positives → higher score
positive_component = sigmoid(raw_positive_density, midpoint_pos, steepness_pos) * 10.0

property_score = neg_weight * negative_component + pos_weight * positive_component
```

Where:
- **Sigmoid direction**: Negative signals use inverted sigmoid `(1 - sigmoid(...))` so that higher negative signal density produces a *lower* property score. Positive signals use standard sigmoid so that higher positive signal density produces a *higher* score.
- **Base score** when no signals are detected: **5.0** (conservative — assume mediocre until evidence indicates otherwise; no-signal should not produce a "Good" rating). This is enforced by calibrating sigmoid midpoints such that `sigmoid(0, midpoint, steepness)` yields the values that combine to 5.0 — specifically, when both signal counts are 0, the inverted negative sigmoid produces `(1 - sigmoid(0, mid, steep)) * 10` and the positive sigmoid produces `sigmoid(0, mid, steep) * 10`, and the weighted combination equals 5.0. This constraint is validated during parameter calibration.
- Each property has calibrated sigmoid midpoint and steepness parameters for both its negative and positive signal densities
- Score is bounded in (0.0, 10.0) by sigmoid properties

**Calibration approach**: Sigmoid parameters will be tuned against the reference implementation examples for arithmetic verification, then refined against 5-10 real-world open-source test suites for predictive validation. The parameters are stored in the Python scoring module and can be updated without changing the agent definition.

### 7.4 Aggregation Levels

The reviewer should produce scores at three levels:

1. **Per-test-method**: Individual test quality assessment
2. **Per-test-file**: Aggregated across all test methods in a file
3. **Per-test-suite**: Aggregated across all test files in scope

File-level aggregation uses mean for positive signals and P90 for negative signals (worst offenders should surface, following the cognitive-load-analyzer precedent).

Suite-level aggregation uses LOC-weighted mean across files.

### 7.5 Report Format

```
# Test Design Review

## Farley Index: X.X / 10.0 (Rating)

### Property Breakdown

| Property | Score | Weight | Weighted | Key Evidence |
|---|---|---|---|---|
| Understandable | X/10 | 1.50x | X.XX | ... |
| Maintainable | X/10 | 1.50x | X.XX | ... |
| Repeatable | X/10 | 1.25x | X.XX | ... |
| Atomic | X/10 | 1.00x | X.XX | ... |
| Necessary | X/10 | 1.00x | X.XX | ... |
| Granular | X/10 | 1.00x | X.XX | ... |
| Fast | X/10 | 0.75x | X.XX | ... |
| First (TDD) | X/10 | 1.00x | X.XX | ... |

### Signal Summary

| Signal | Count | Affects | Severity |
|---|---|---|---|
| Thread.sleep() calls | 3 | R, F | High |
| Reflection usage | 2 | M | High |
| Magic numbers | 12 | U | Medium |
| ... | ... | ... | ... |

### Top 5 Worst Offenders
1. file:method - key issues
...

### Recommendations
1. Highest-impact improvement
2. ...
3. ...

### Methodology Notes
- Static/LLM blend: 60/40
- LLM model: {model_id}
- Files analyzed: {count} ({sampling_note})
- Language: {language}

### Reference
Based on Dave Farley's Properties of Good Tests:
https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/
```

---

## 8. Key Design Decisions

### 8.1 Decisions with Strong Evidence

| Decision | Rationale | Sources | Confidence |
|---|---|---|---|
| 8 properties from Farley | Well-defined, practitioner-tested, coherent system; best suited for quantitative scoring among the three frameworks compared | Farley article, MSE book, comparison with Beck and Meszaros | HIGH |
| U and M weighted highest | Documentation and maintainability are tests' primary long-term value; supported by all three frameworks | Farley, Beck (Readable + Behavioral/Structure-insensitive), Meszaros (Tests as Documentation + Safety Net) | HIGH (ordering), MEDIUM (exact 1.5x magnitude) |
| Static detection for R, A, F | Thread.sleep, shared state, I/O access are high-precision detections | testsmells.org, tsDetect, Spadini 2022 all confirm high recall | HIGH |
| Sigmoid normalization | Smooth transitions, asymptotic bounds, calibrated midpoints — consistent with cognitive-load-analyzer precedent | Cognitive-load-analyzer architecture | HIGH |
| P90 for negative signals | Worst offenders must surface, not be masked by averages | Cognitive-load-analyzer precedent, Farley's emphasis on worst-case impact | HIGH |
| 60/40 static/LLM blend | Prioritize deterministic measurement while incorporating semantic judgment | Cognitive-load-analyzer D4 precedent | MEDIUM |

### 8.2 Decisions Requiring Calibration

| Decision | What Needs Calibration | Approach |
|---|---|---|
| Sigmoid parameters per property | Midpoint and steepness for each property's signal density | Calibrate against reference examples, then validate against 5-10 open-source test suites |
| LLM vs. static weight split | Whether 60/40 is optimal | Start at 60/40, collect data on static-vs-LLM divergence, adjust based on which correlates better with expert ratings |
| Language-specific thresholds | Assertion count norms differ by language | Build profiles per language from well-known open-source test suites (e.g., Spring Boot, Django, Express) |
| Base score (no-signal case) | Starting point when no signals detected | **5.0** — conservative; no-signal should yield "Fair" rating, not "Good" |

### 8.3 Open Questions

1. **Should the reviewer assess test coverage data if available?** (e.g., coverage reports from lcov/jacoco). This would strengthen N (Necessary) scoring but adds external dependency.
2. **Should there be a separate "Test Health" metric beyond the Farley Index?** (e.g., flakiness rate from CI history, test execution time trends). This is valuable but requires CI integration.
3. **How should the reviewer handle test utility/helper files?** They contain no test methods but affect M (Maintainable) scoring positively.

---

## 9. Mock Anti-Patterns and Test Theatre

### 9.0.1 Overview

Four mock-specific anti-patterns were identified that produce tests with zero or negative value while appearing structurally sound. These are collectively called "test theatre." Comprehensive research is documented in `docs/mock-anti-patterns-research.md` (22 sources, 9 mocking frameworks across 5 languages).

**Key finding**: As of 2026-02-12, no mainstream static analysis tool (SonarQube, PMD, ESLint, tsDetect, testsmells.org) has rules for any of these patterns. The detection heuristics added to the `signal-detection-patterns` skill represent original contributions.

### 9.0.2 The Four Anti-Patterns

| Anti-Pattern | Severity | Farley Properties | Taxonomy References |
|---|---|---|---|
| **Mock Tautology** (AP1) | Critical | N, M | James Carr "The Mockery," Rainsberger "Tautological TDD," Mockito Wiki |
| **No Production Code Exercised** (AP2) | Critical | N, M, T | James Carr "The Mockery," Khorikov "observable behavior vs implementation" |
| **Over-Specified Interactions** (AP3) | High | M, A | Meszaros "Overspecified Software" / "Fragile Test," Beck "Structure-insensitive" |
| **Testing Internal Details** (AP4) | High | M, U | James Carr "The Inspector," Khorikov "Structural Inspection," Meszaros "Behavior Sensitivity" |

### 9.0.3 Detection Approach

Detection uses both static signals and LLM rubric guidance:

- **Static signals** (60% weight): Language-specific regex patterns detect mock setup/assert on same object (AP1), absence of production class instantiation (AP2), verify with exact counts/ordering (AP3), and ArgumentCaptor deep inspection / verify(never()) patterns (AP4). See `signal-detection-patterns` skill for per-framework patterns.
- **LLM rubric guidance** (40% weight): Explicit instructions added to the N, M, and T property rubrics in `farley-properties-and-scoring` skill, directing the LLM to check for mock tautologies, tests with no SUT, over-specified interactions, and implementation coupling.

### 9.0.4 Detection Confidence

| Anti-Pattern | Static Detection Confidence | LLM Detection Confidence |
|---|---|---|
| AP1 (Mock Tautology) | MEDIUM -- regex catches common forms; misses variable aliasing | HIGH -- LLM can trace data flow semantically |
| AP2 (No Production Code) | MEDIUM -- "no new RealClass" heuristic has false positives for static methods and setUp-injected SUTs | HIGH -- LLM can identify missing SUT |
| AP3 (Over-Specified) | HIGH -- verify/times/InOrder patterns are syntactically distinctive | HIGH -- LLM can assess whether verification is essential vs over-specified |
| AP4 (Internal Details) | MEDIUM -- composite signal (2+ of 6 indicators) reduces false positives | MEDIUM -- requires domain context to distinguish essential from over-specified |

### 9.0.5 Mocking Frameworks Covered

| Language | Frameworks | Status |
|---|---|---|
| Java | Mockito | Full coverage (AP1-AP4) |
| Python | unittest.mock, pytest-mock | Full coverage (AP1-AP4) |
| JavaScript/TypeScript | Jest mocks, Sinon | Full coverage (AP1-AP4) |
| Go | testify/mock, gomock | Full coverage (AP1-AP3); AP4 partial (gomock lacks argument captors) |
| C# | Moq, NSubstitute | Full coverage (AP1-AP4) |

---

## 10. Knowledge Gaps and Limitations

### 10.1 Calibration Evidence Gap [HIGH PRIORITY]

The reference implementation provides formula *verification* (the arithmetic is correct given known inputs), not formula *validation* (the weights produce scores that correlate with actual test quality as judged by practitioners).

The two reference examples (high-score: 9.2, low-score: 2.0) were purpose-built to demonstrate the extremes of the scoring scale. They confirm the formula produces sensible numbers at the boundaries but do not validate its behavior in the middle range (4.0-7.0) where most real codebases fall.

**Mitigation roadmap**:
1. Score 5-10 open-source test suites of varying quality (e.g., Spring Boot, Django, Linux kernel tests, known-flaky suites)
2. Have 2-3 practitioners independently rate the same suites on Farley's 8 properties
3. Compare formula output against practitioner consensus
4. Adjust weights and sigmoid parameters to maximize correlation

### 10.2 Static Analysis Cannot Detect All Semantic Quality Issues

Static analysis detects *structural* test quality — naming patterns, assertion counts, I/O usage, shared state. With the addition of mock anti-pattern detection (section 9), static analysis now also catches some *semantic* quality issues (mock tautologies, no production code exercised, over-specified interactions). However, it still cannot detect:

- **Wrong assertions**: A test that asserts `assertEquals(42, calculate())` has perfect structure but may be verifying the wrong value
- **Missing edge cases**: A test suite can score 10/10 on all structural properties while missing critical boundary conditions
- **Misleading names**: A test named `shouldReturnUserWhenFound` that actually tests deletion would score well structurally
- **Subtle mock tautologies**: Variable aliasing, helper-method indirection, and setUp-injected mocks can mask tautological patterns from regex-based detection
- **Essential vs over-specified verification**: Whether `verify(times(1))` is an idempotency requirement or over-specification requires domain context

The LLM assessment phase addresses these residual gaps with explicit test theatre rubric guidance (see `farley-properties-and-scoring` skill) but introduces its own limitation (see 10.3).

### 10.3 LLM Assessment Introduces Non-Determinism

Even with the reproducibility protocol (temperature 0, structured rubric, deterministic file selection), LLM-based scoring may vary across:
- Different model versions (model updates change judgment)
- Context window variations (different file orderings may influence scores)
- Prompt sensitivity (small rubric changes may shift scores)

The 60/40 static/LLM blend limits the impact: even a 2-point LLM scoring difference on a property only shifts the blended score by 0.8 points. Recording the model identifier in the report enables consumers to account for model-version effects.

### 10.4 Properties Not Captured by the Farley Framework

Beck's Test Desiderata includes properties that Farley does not:

| Beck Property | Description | Why Not in Farley Index |
|---|---|---|
| **Predictive** | Passing tests indicate production readiness | Requires integration/E2E context, not assessable from unit test code alone |
| **Inspiring** | Passing tests inspire confidence | Subjective and emergent — depends on team context |
| **Composable** | Test dimensions separately, combine results | Relevant for integration testing strategy, not individual test quality |
| **Writable** | Tests cheap to write relative to code cost | Requires effort measurement, not assessable from final test code |

These properties are real and valuable but fall outside what a static+LLM code review can assess. They should be noted in reports as "dimensions not measured."

### 10.5 Confidence Distribution

| Confidence | % of Findings | Key Areas |
|---|---|---|
| HIGH | 50% | Property-to-smell mapping (Finding 1), static detection heuristics (Finding 2), framework comparison, sigmoid architecture |
| MEDIUM | 35% | Weight rationale (Finding 3 — ordering supported, magnitudes not validated), 60/40 blend ratio, base score choice |
| LOW | 15% | First/TDD detection accuracy, language-specific threshold values, interaction between properties |

---

## 11. Sources

### Primary Framework Sources
- [Dave Farley, "TDD & The Properties of Good Tests" (LinkedIn)](https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/)
- [Kent Beck, "Test Desiderata" (Medium, 2019)](https://medium.com/@kentbeck_7670/test-desiderata-94150638a4b3)
- [Kent Beck, Test Desiderata Website](https://testdesiderata.com/)
- [Kent Beck, TestDesiderata (GitHub)](https://github.com/KentBeck/TestDesiderata)
- [Gerard Meszaros, *xUnit Test Patterns* (Amazon)](https://www.amazon.com/xUnit-Test-Patterns-Refactoring-Code/dp/0131495054)
- [Meszaros, Goals of Test Automation (xunitpatterns.com)](http://xunitpatterns.com/)

### Farley Background
- [Dave Farley's Weblog — TDD Category](https://www.davefarley.net/?cat=16)
- [Dave Farley, *Modern Software Engineering* (Amazon)](https://www.amazon.com/Modern-Software-Engineering-Discipline-Development/dp/0137314914)
- [Dave Farley, "Test *Driven* Development" (Blog)](https://www.davefarley.net/?p=220)
- [Dave Farley, "The basics of TDD" (Blog)](https://www.davefarley.net/?p=180)
- [TDD as a Design Tool with Dave Farley (VirtualDDD)](https://virtualddd.com/videos/tdd-as-a-design-tool-with-dave-farley/)

### Test Smell Research
- [testsmells.org — Test Smell Catalog](https://testsmells.org/pages/testsmells.html)
- [Spadini et al., "Test Smells 20 Years Later: Detectability, Validity, and Reliability" (Springer, 2022)](https://link.springer.com/article/10.1007/s10664-022-10207-5)
- [Pontillo et al., "Machine Learning-Based Test Smell Detection" (PMC, 2024)](https://pmc.ncbi.nlm.nih.gov/articles/PMC10914901/)
- [Peruma et al., "tsDetect" — AST-based test smell detection (2020)](https://arxiv.org/pdf/2208.07574)
- [Language-Agnostic Test Smell Detection (SBES 2024)](https://sol.sbc.org.br/index.php/sbes/article/download/30413/30219/)
- [Samman Coaching — Test Desiderata Learning Hour](https://sammancoaching.org/learning_hours/test_design/test_desiderata.html)

### Mock Anti-Pattern Sources
- [James Carr, "TDD Anti-Patterns" (2006)](https://web.archive.org/web/20160605001457/http://blog.james-carr.org:80/2006/11/03/tdd-anti-patterns/) — Defines "The Mockery" and "The Inspector"
- [J.B. Rainsberger, "The Curious Case of Tautological TDD" (2013)](https://blog.thecodewhisperer.com/permalink/the-curious-case-of-tautological-tdd) — Mock tautology definition
- [Vladimir Khorikov, *Unit Testing: Principles, Practices, and Patterns* (Manning, 2020)](https://www.manning.com/books/unit-testing) — Observable behavior vs implementation details
- [Khorikov, "Structural Inspection" (Enterprise Craftsmanship)](https://enterprisecraftsmanship.com/posts/structural-inspection) — Testing internal details
- [Mockito Wiki, "How to write good tests"](https://github.com/mockito/mockito/wiki/How-to-write-good-tests) — Tautology avoidance
- [Codepipes, "Software Testing Anti-Patterns" (2018)](https://blog.codepipes.com/testing/software-testing-antipatterns.html) — Testing internal implementation
- Detailed research: `docs/mock-anti-patterns-research.md` *(22 sources, 9 frameworks, code examples in all languages)*

### Reference Implementation
- Reference implementation: `test-design-reviewer/` *(formula verification only — see section 10.1)*
