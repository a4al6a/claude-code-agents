---
name: farley-properties-and-scoring
description: Dave Farley's 8 Properties of Good Tests, scoring rubrics, Farley Index formula, rating scale, and aggregation methodology
---

# Farley Properties and Scoring

> **Implementation note**: The Farley Index formula and sigmoid normalization are specified here for reference. A Python CLI calculator (planned, not yet implemented) will make these calculations deterministic and testable. Until then, the agent computes scores using these specifications directly.

## The 8 Properties of Good Tests

> Source: [Dave Farley, "TDD & The Properties of Good Tests"](https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/)

| Code | Property | Definition | Primary Load |
|---|---|---|---|
| U | Understandable | Tests describe what they are testing so we can understand the goals of our software, focusing on system behavior rather than implementation details | Documentation value |
| M | Maintainable | Tests act as a defence of our system, breaking when we want them to, remaining easy to modify as the codebase evolves | Long-term value |
| R | Repeatable | Tests always pass or fail in the same way for a given version of the software, producing consistent results regardless of when or where they run | Reliability trust |
| A | Atomic | Tests are isolated and focus on a single outcome, operating independently with no side-effects | Isolation guarantee |
| N | Necessary | Avoid creating tests for test sake; write tests that actively guide development decisions | Value justification |
| G | Granular | Tests are small, simple and focused, and assert a single outcome, providing clear pass/fail with obvious problem indicators | Diagnostic precision |
| F | Fast | Since developers will end up with lots of them, tests need execution efficiency | Feedback speed |
| T | First | In TDD, write the test before writing code, strengthening all other properties | Design quality |

Property interdependence: First (TDD) naturally produces Understandable and Granular tests. Atomic tests are inherently more Repeatable. Granular tests are naturally Fast. Maintainable tests remain Necessary longer. Improvement in one property often cascades; degradation often signals related degradation.

## Farley Index Formula

```
Farley Index = (U*1.5 + M*1.5 + R*1.25 + A*1.0 + N*1.0 + G*1.0 + F*0.75 + T*1.0) / 9.0
```

Each property score ranges from 0.0 to 10.0. The divisor 9.0 is the sum of all weights (1.5+1.5+1.25+1.0+1.0+1.0+0.75+1.0 = 9.0), producing a final index on the 0-10 scale.

### Weight Rationale

| Property | Weight | Rationale | Cross-Framework Support |
|---|---|---|---|
| U (Understandable) | 1.5x | Tests as documentation is their primary long-term value | Beck's Readable; Meszaros's "Tests as Documentation" |
| M (Maintainable) | 1.5x | Implementation-coupled tests become a liability rather than asset | Beck's Behavioral + Structure-insensitive; Meszaros's "Tests as Safety Net" |
| R (Repeatable) | 1.25x | A single flaky test erodes team confidence in all tests | Beck's Deterministic; Meszaros's Repeatable |
| A (Atomic) | 1.0x | Isolation is fundamental but well-understood; most developers achieve baseline | Beck's Isolated |
| N (Necessary) | 1.0x | Redundant tests waste maintenance effort but are less harmful than other violations | Farley-specific |
| G (Granular) | 1.0x | Single-outcome focus aids debugging; has valid exceptions (logical assertion groups) | Beck's Specific |
| F (Fast) | 0.75x | Speed is optimizable after the fact; slow but correct tests beat fast but poorly designed | Beck's Fast |
| T (First/TDD) | 1.0x | Hardest to detect statically; often inferred from other properties | Meszaros's "Tests as Specification" |

The relative ordering (U,M > R > A,N,G,T > F) is supported by cross-framework consensus. The exact magnitudes are subject to calibration against practitioner ratings.

## Per-Property Scoring Rubrics (0-10 Scale)

### U -- Understandable

| Score | Criteria |
|---|---|
| 9-10 | Tests read like specifications; behavior crystal clear without reading implementation; descriptive names, display annotations, nested organization |
| 7-8 | Tests clear with minor ambiguities; intent mostly obvious from names |
| 5-6 | Tests require some code inspection to understand purpose |
| 3-4 | Tests cryptic; heavy reliance on implementation details to understand |
| 1-2 | Names like test1/test2; no organization; magic numbers throughout |

### M -- Maintainable

| Score | Criteria |
|---|---|
| 9-10 | Proper abstractions; changes to implementation rarely break tests; verifies behavior not implementation |
| 7-8 | Good separation of concerns; occasional brittleness |
| 5-6 | Some coupling to implementation; moderate refactoring pain |
| 3-4 | Tightly coupled to implementation; tests break with minor changes |
| 1-2 | Reflection to access private fields; tests mirror implementation structure exactly |

### R -- Repeatable

| Score | Criteria |
|---|---|
| 9-10 | Completely deterministic; no external dependencies; same result every time, anywhere |
| 7-8 | Rarely flaky; minimal environmental dependencies |
| 5-6 | Occasional flakiness; some timing or state dependencies |
| 3-4 | File system, timing, or environment dependencies present |
| 1-2 | Thread.sleep, file I/O, network calls, system time, random without seed |

### A -- Atomic

| Score | Criteria |
|---|---|
| 9-10 | Completely isolated; no shared state; parallelizable |
| 7-8 | Mostly isolated; minor shared setup that doesn't cause ordering issues |
| 5-6 | Some shared state; test order sometimes matters |
| 3-4 | Heavy interdependencies; tests must run in specific order |
| 1-2 | Shared mutable static state; explicit ordering annotations; tests verify other tests ran |

### N -- Necessary

| Score | Criteria |
|---|---|
| 9-10 | Every test adds unique value; no redundancy; parameterized tests for variations |
| 7-8 | Most tests valuable; minor redundancy |
| 5-6 | Some tests feel like checkbox exercises; moderate redundancy |
| 3-4 | Several redundant tests; framework testing; trivial assertions |
| 1-2 | Many tests add no value; assertTrue(true); disabled tests accumulating |

### G -- Granular

| Score | Criteria |
|---|---|
| 9-10 | Each test verifies a single outcome; failures pinpoint exact issues |
| 7-8 | Tests focused; occasional multiple assertions forming a logical group for one outcome |
| 5-6 | Tests cover multiple behaviors; failure diagnosis takes effort |
| 3-4 | Tests sprawling; multiple unrelated assertions |
| 1-2 | Mega-tests with 20+ assertions; testEverything() methods |

Note: "single outcome" is distinct from "single assertion statement." Multiple assertions verifying one outcome (e.g., status code and body of an HTTP response) are acceptable. Detect multiple *unrelated* assertions, not logical groups.

### F -- Fast

| Score | Criteria |
|---|---|
| 9-10 | Pure computation; no I/O; millisecond execution |
| 7-8 | Tests quick; minor optimization opportunities |
| 5-6 | Some slow tests; suite takes noticeable time |
| 3-4 | File I/O or database calls present |
| 1-2 | Thread.sleep, network calls, heavy setup/teardown |

### T -- First (TDD Evidence)

| Score | Criteria |
|---|---|
| 9-10 | Clear evidence of test-first approach; tests drive design; behavior-focused names |
| 7-8 | Likely test-first; good design influence |
| 5-6 | Unclear if test-first; tests may be afterthoughts |
| 3-4 | Test structure mirrors implementation; likely test-after |
| 1-2 | Tests clearly written after code; follow implementation structure; coverage patches |

## Two-Phase Assessment

### Phase 1: Static Analysis (deterministic)
- Parse test files, count signals per property (see `signal-detection-patterns` skill)
- Compute raw metrics: assertion counts, sleep presence, reflection usage, naming patterns
- Normalize metrics to per-property sub-scores using sigmoid normalization
- Produce a "static floor" score that is deterministic and reproducible

### Phase 2: LLM Assessment (controlled non-determinism)
- Read tests holistically: does the test suite feel understandable?
- Assess naming quality semantically (beyond pattern matching)
- Evaluate TDD evidence from design patterns static analysis cannot detect
- Adjust static scores up or down based on contextual judgment
- Produce a "judgment delta" for each property

### Blending
```
final_property_score = 0.60 * static_score + 0.40 * llm_score
```
Per property, then aggregated via the Farley Index formula. The 60/40 split prioritizes deterministic measurement while incorporating semantic judgment.

### LLM Reproducibility Protocol
- Evaluate all test files if under 50 files; SHA-256 deterministic selection (30%) for larger suites
- Structured rubric: evaluate each property on the 1-10 rubric above, providing specific code evidence
- Per-file evaluation: produce a score per property per file, then aggregate
- Record the model identifier in the report for reproducibility tracking

## Sigmoid Normalization

Each property uses sigmoid normalization to map raw signal counts to the 0-10 scale:

```
raw_negative_density = negative_signal_count / total_test_methods
raw_positive_density = positive_signal_count / total_test_methods

negative_component = (1 - sigmoid(raw_negative_density, midpoint_neg, steepness_neg)) * 10.0
positive_component = sigmoid(raw_positive_density, midpoint_pos, steepness_pos) * 10.0

property_score = neg_weight * negative_component + pos_weight * positive_component
```

Base score when no signals detected: 5.0 (conservative -- no-signal yields "Fair" rating, not "Good").

## Rating Scale

| Farley Index | Rating | Interpretation |
|---|---|---|
| 9.0 - 10.0 | Exemplary | Model for the industry; tests serve as living documentation |
| 7.5 - 8.9 | Excellent | High quality with minor improvement opportunities |
| 6.0 - 7.4 | Good | Solid foundation with clear areas for improvement |
| 4.5 - 5.9 | Fair | Functional but needs significant attention to test design |
| 3.0 - 4.4 | Poor | Tests provide limited value; major refactoring needed |
| 0.0 - 2.9 | Critical | Tests may be harmful; consider rewriting from scratch |

## Aggregation Levels

Scores are produced at three levels:

1. **Per-test-method**: Individual test quality assessment
2. **Per-test-file**: Aggregated across all test methods in a file (mean for positive signals, P90 for negative signals -- worst offenders must surface)
3. **Per-test-suite**: Aggregated across all test files in scope (LOC-weighted mean across files)

## Properties Not Captured

Beck's Test Desiderata includes properties outside the Farley framework that are noted in reports as "dimensions not measured":

| Property | Why Not Measured |
|---|---|
| Predictive | Requires integration/E2E context, not assessable from test code alone |
| Inspiring | Subjective and emergent; depends on team context |
| Composable | Relevant for integration testing strategy, not individual test quality |
| Writable | Requires effort measurement, not assessable from final test code |
