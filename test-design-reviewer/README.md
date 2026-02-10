# Test Design Reviewer Agent

An AI agent that evaluates test code quality using Dave Farley's 8 Properties of Good Tests, producing a quantitative Farley Index score (0-10) with per-property breakdown, signal evidence, and actionable recommendations.

## Purpose

The test-design-reviewer analyzes test codebases using a two-phase methodology (static signal detection + LLM semantic assessment) to produce a comprehensive test quality report. It detects test smells, evaluates naming quality, checks for isolation issues, and identifies design patterns that indicate test-first or test-after development.

## When to Use

Use this agent when:
- Assessing the quality of a test suite against established testing principles
- Identifying flaky, brittle, or poorly designed tests
- Auditing test quality across a codebase before a major release
- Onboarding new developers and want to understand test health
- Prioritizing test refactoring efforts based on quantitative scores
- Validating that tests follow TDD best practices

## The 8 Properties of Good Tests

| Property | Weight | What It Measures |
|---|---|---|
| Understandable | 1.50x | Tests read like specifications; behavior-driven naming; clear organization |
| Maintainable | 1.50x | Tests verify behavior not implementation; resilient to refactoring |
| Repeatable | 1.25x | Deterministic results; no external dependencies (time, filesystem, network) |
| Atomic | 1.00x | Isolated; no shared mutable state; parallelizable |
| Necessary | 1.00x | Each test adds unique value; no redundancy or trivial assertions |
| Granular | 1.00x | Single outcome per test; failures pinpoint exact issues |
| Fast | 0.75x | Pure computation; no I/O or delays |
| First (TDD) | 1.00x | Evidence of test-first approach; tests drive design |

## Rating Scale

| Farley Index | Rating |
|---|---|
| 9.0 - 10.0 | Exemplary |
| 7.5 - 8.9 | Excellent |
| 6.0 - 7.4 | Good |
| 4.5 - 5.9 | Fair |
| 3.0 - 4.4 | Poor |
| 0.0 - 2.9 | Critical |

## Skills

This agent includes two skill documents (`skills/test-design-reviewer/`):
- **farley-properties-and-scoring.md** - Property definitions, scoring rubrics (0-10), Farley Index formula, sigmoid normalization, rating scale, and aggregation methodology
- **signal-detection-patterns.md** - Language-specific static detection heuristics for Java, Python, JavaScript/TypeScript, Go, and C#. Per-property signal tables mapping test smells to Farley properties

## Examples

The `examples/` folder contains two contrasting test suites:

### test-quality-high-score/
An exemplary test suite (Farley Score: 9.2/10):
- `ExpressionParser.java` - The implementation
- `ExpressionParserTest.java` - High-quality tests with behavior-driven naming, nested organization, single assertions
- `test-design-review.md` - The review showing what makes these tests excellent

### test-quality-low-score/
A problematic test suite (Farley Score: 2.1/10):
- `ExpressionParser.java` - The implementation
- `ExpressionParserTest.java` - Tests with anti-patterns: cryptic names, shared state, Thread.sleep, reflection, mega-tests
- `test-design-review.md` - The review showing what makes these tests problematic

## Research

The `docs/` folder contains `test-design-reviewer-research.md`, a research document covering:
- Framework comparison: Farley vs Beck's Test Desiderata vs Meszaros's xUnit Patterns
- Mapping of Farley properties to detectable test smells
- Static detection heuristics with precision/recall analysis
- Scoring formula design and weight rationale

## Agent Definition

- `test-design-reviewer.md` - The full agent definition

## Related Agents

This agent works well in combination with:
- **atdd-developer** - Implement features using ATDD methodology
- **code-smell-detector** - Detect quality issues in production code
- **cognitive-load-analyzer** - Assess overall codebase cognitive load

## Reference

Based on Dave Farley's Properties of Good Tests:
https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/

## Attribution

This agent has been created with the agentic framework [nWave](https://nwave.ai).
