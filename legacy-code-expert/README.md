# Legacy Code Expert Agent

An AI agent specialized in safely refactoring legacy code using Michael Feathers' dependency-breaking techniques from "Working Effectively with Legacy Code".

## Purpose

The Legacy Code Expert agent helps teams safely modify legacy code by:
- Identifying seams and opportunities for testing
- Recommending appropriate dependency-breaking techniques
- Creating safe pathways for introducing tests

## Dependency-Breaking Techniques

The agent has expertise in 25 techniques including:
- Extract Interface / Extract Implementer
- Parameterize Constructor / Method
- Subclass and Override Method
- Break Out Method Object
- Introduce Instance Delegator
- Replace Function with Function Pointer
- And many more...

## When to Use

Use this agent when:
- You need to modify code without tests
- You're trying to introduce testing to legacy systems
- You need to break dependencies for testability
- You want safe strategies for legacy code changes

## Examples

Currently no examples in this folder. The agent works in conjunction with:
- `code-smell-detector/examples/legacy-refactoring/` - Shows the analysis phase
- `refactoring-expert/examples/legacy-refactoring/` - Shows the refactoring plan

## Related Agents

This agent is typically used in a workflow with:
1. **code-smell-detector** - Identify code smells
2. **legacy-code-expert** (this agent) - Break dependencies
3. **refactoring-expert** - Plan the refactoring
4. **test-design-reviewer** - Ensure good test coverage

## Reference

Based on Michael Feathers' "Working Effectively with Legacy Code"

## Agent Definition

The full agent definition is in `.claude/agents/legacy-code-expert.md`
