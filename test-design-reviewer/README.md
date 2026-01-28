# Test Design Reviewer Agent

An AI agent specialized in evaluating test quality using Dave Farley's testing principles.

## Purpose

The Test Design Reviewer agent scores test suites against eight properties of good tests and provides actionable recommendations for improvement.

## The Farley Score

Tests are evaluated on:
- **Understandable** (1.5x weight) - Tests read like specifications
- **Maintainable** (1.5x weight) - Resilient to implementation changes
- **Repeatable** (1.25x weight) - Deterministic, no external dependencies
- **Atomic** (1.0x weight) - Isolated, no shared state
- **Necessary** (1.0x weight) - Each test adds unique value
- **Granular** (1.0x weight) - Single assertion, pinpoint failures
- **Fast** (0.75x weight) - Millisecond execution
- **First/TDD** (1.0x weight) - Evidence of test-first approach

## When to Use

Use this agent when:
- You've written new tests and want quality feedback
- You're experiencing flaky tests in CI
- You want to improve an existing test suite
- You're auditing test quality across a codebase

## Examples

The `examples/` folder contains:

### test-quality-high-score/
An exemplary test suite (Farley Score: 9.2/10):
- `ExpressionParser.java` - The implementation
- `ExpressionParserTest.java` - High-quality tests
- `test-design-review.md` - The review showing what makes these tests excellent

### test-quality-low-score/
A problematic test suite (Farley Score: 2.1/10):
- `ExpressionParser.java` - The implementation
- `ExpressionParserTest.java` - Tests with many anti-patterns
- `test-design-review.md` - The review showing what makes these tests problematic

## Related Agents

This agent is typically used in a workflow with:
1. **atdd-developer** - Implement features with tests
2. **test-design-reviewer** (this agent) - Review test quality
3. **refactoring-expert** - Improve test structure if needed

## Reference

Based on Dave Farley's Properties of Good Tests:
https://www.linkedin.com/pulse/tdd-properties-good-tests-dave-farley-iexge/

## Agent Definition

The full agent definition is in `.claude/agents/test-design-reviewer.md`
