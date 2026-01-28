# Refactoring Expert Agent

An AI agent specialized in generating comprehensive refactoring recommendations based on code smell analysis.

## Purpose

The Refactoring Expert agent creates detailed, step-by-step refactoring plans using techniques from Martin Fowler's "Refactoring" and Joshua Kerievsky's "Refactoring to Patterns".

## When to Use

Use this agent when:
- You have a code smell report and need actionable refactoring steps
- You want to improve code structure systematically
- You need guidance on which patterns to introduce and when
- You're planning a large-scale refactoring effort

## Examples

The `examples/` folder contains:

### legacy-refactoring/
- `UserManager.java` - The source code being refactored
- `refactoring-plan.md` - Comprehensive refactoring recommendations

### refactoring-to-patterns/
- `OrderProcessingSystem.java` - Code with pattern opportunities
- `refactoring-plan.md` - Pattern-directed refactoring plan including:
  - State pattern for order status
  - Strategy pattern for pricing
  - Command pattern for actions
  - Observer pattern for notifications
  - And more...

## Related Agents

This agent is typically used in a workflow with:
1. **code-smell-detector** - Identify code smells first
2. **refactoring-expert** (this agent) - Generate refactoring plan
3. **legacy-code-expert** - For dependency-breaking in legacy code
4. **test-design-reviewer** - Ensure tests support refactoring

## Agent Definition

The full agent definition is in `.claude/agents/refactoring-expert.md`
