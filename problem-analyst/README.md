# Problem Analyst Agent

An AI agent specialized in analyzing and understanding complex problems thoroughly before implementation begins.

## Purpose

The Problem Analyst agent helps teams deeply understand the problem space before jumping into solutions. It creates comprehensive problem analyses that serve as the foundation for user story creation and implementation.

## When to Use

Use this agent when:
- You have a complex requirement that needs thorough analysis
- You want to understand the problem domain before development
- You need to identify key stakeholders, constraints, and success criteria
- You want to explore edge cases and potential challenges upfront

## Examples

The `examples/` folder contains:

### tax-calculator/
A complete problem analysis workflow for a UK income tax calculator:
- `01-problem-statement.md` - Initial problem definition
- `02-problem-analysis.md` - Comprehensive problem analysis output

## Related Agents

This agent is typically used in a workflow with:
1. **problem-analyst** (this agent) - Analyze the problem
2. **user-story-writer** - Break down into user stories
3. **atdd-developer** - Implement using ATDD

## Agent Definition

The full agent definition is in `.claude/agents/problem-analyst.md`
