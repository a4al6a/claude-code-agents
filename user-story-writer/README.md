# User Story Writer Agent

An AI agent specialized in breaking down problem statements or feature requirements into granular, implementable user stories.

## Purpose

The User Story Writer agent transforms problem analyses into small, independent, valuable, and testable user stories using techniques like Elephant Carpaccio.

## When to Use

Use this agent when:
- You have a high-level problem statement that needs decomposition
- You need properly structured user stories with acceptance criteria
- You want stories small enough for iterative development
- You're planning a feature and need INVEST-compliant stories

## Examples

The `examples/` folder contains:

### tax-calculator/
- `03-user-stories.md` - User stories generated from the tax calculator problem analysis

## Related Agents

This agent is typically used in a workflow with:
1. **problem-analyst** - Analyze the problem first
2. **user-story-writer** (this agent) - Create user stories
3. **atdd-developer** - Implement using ATDD

## Agent Definition

The full agent definition is in `.claude/agents/user-story-writer.md`
