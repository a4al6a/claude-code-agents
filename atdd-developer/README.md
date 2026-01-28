# ATDD Developer Agent

An AI agent specialized in implementing user stories using Acceptance Test Driven Development (ATDD) methodology.

## Purpose

The ATDD Developer agent implements features following the Red-Green-Refactor cycle:
1. Write failing acceptance tests based on user story criteria
2. Implement the minimum code to make tests pass
3. Refactor while keeping tests green

## When to Use

Use this agent when:
- You have user stories with clear acceptance criteria
- You want to implement features using TDD/ATDD approach
- You need tests that serve as living documentation
- You're practicing outside-in development

## Examples

The `examples/` folder contains:

### tax-calculator/
- `04-atdd-implementation.md` - Complete ATDD implementation of the tax calculator

## Related Agents

This agent is typically used in a workflow with:
1. **problem-analyst** - Analyze the problem first
2. **user-story-writer** - Create user stories
3. **atdd-developer** (this agent) - Implement using ATDD
4. **test-design-reviewer** - Review test quality

## Agent Definition

The full agent definition is in `.claude/agents/atdd-developer.md`
