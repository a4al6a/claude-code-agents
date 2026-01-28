# Code Smell Detector Agent

An AI agent specialized in analyzing code for potential quality issues, design problems, or maintainability concerns.

## Purpose

The Code Smell Detector agent identifies code smells and anti-patterns using Martin Fowler's refactoring catalog and Joshua Kerievsky's "Refactoring to Patterns" as references.

## When to Use

Use this agent when:
- You want to review code for design issues before committing
- You're preparing for a refactoring effort and need to identify problems
- You need a systematic analysis of code quality concerns
- You want to understand what patterns might improve existing code

## Examples

The `examples/` folder contains:

### legacy-refactoring/
- `UserManager.java` - Legacy code with multiple code smells
- `code-smell-detector-report.md` - Detailed analysis report
- `code-smell-detector-summary.md` - Executive summary

### refactoring-to-patterns/
- `OrderProcessingSystem.java` - Code with pattern-related improvement opportunities
- `code-smell-detector-report.md` - Pattern-directed analysis

## Related Agents

This agent is typically used in a workflow with:
1. **code-smell-detector** (this agent) - Identify code smells
2. **refactoring-expert** - Generate refactoring plan
3. **legacy-code-expert** - For dependency-breaking techniques

## Agent Definition

The full agent definition is in `.claude/agents/code-smell-detector.md`
