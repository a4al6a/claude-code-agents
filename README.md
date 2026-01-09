# Claude Code Agents

A collection of specialized agents for software development workflows, designed to work together in pipelines for comprehensive problem-solving and code quality improvement.

## Agents Overview

### Problem Analysis & Planning Agents

#### problem-analyst
Analyzes and understands complex problems thoroughly before implementation begins. Creates comprehensive problem domain understanding and identifies core requirements.

**Use when:** You have complex requirements that need deep analysis before development starts.

#### user-story-writer  
Breaks down problem statements into granular, implementable user stories using techniques like Elephant Carpaccio. Creates small, independent, valuable, and testable stories.

**Use when:** You need to decompose features into actionable user stories with clear acceptance criteria.

#### atdd-developer
Implements user stories using Acceptance Test Driven Development (ATDD) methodology. Follows the Red-Green-Refactor cycle systematically.

**Use when:** You have user stories with acceptance criteria that need systematic implementation using TDD/BDD practices.

### Code Quality Agents

#### code-smell-detector
Analyzes code for potential quality issues, design problems, and maintainability concerns. Identifies patterns like God Objects, violation of SOLID principles, and other code smells.

**Use when:** You want to identify problematic patterns in existing code before refactoring.

#### refactoring-expert
Generates comprehensive refactoring recommendations based on detected code smells. Provides specific techniques and step-by-step guidance for improving code quality.

**Use when:** You have identified code smells and need expert guidance on refactoring approaches.

## Agent Pipelines

### Pipeline 1: Feature Development (Problem → Stories → Implementation)

```
Problem Statement → problem-analyst → user-story-writer → atdd-developer
```

**Workflow:**
1. **problem-analyst**: Analyzes the problem domain, identifies core requirements and constraints
2. **user-story-writer**: Applies Elephant Carpaccio technique to create small, implementable user stories  
3. **atdd-developer**: Implements each story using Red-Green-Refactor ATDD methodology

**Best for:** New feature development, complex requirements, systematic implementation

### Pipeline 2: Code Quality Improvement (Detection → Analysis → Refactoring)

```
Legacy Code → code-smell-detector → refactoring-expert → Implementation
```

**Workflow:**
1. **code-smell-detector**: Identifies code smells, design issues, and quality problems
2. **refactoring-expert**: Analyzes findings and creates comprehensive refactoring plan
3. Manual implementation following the refactoring recommendations

**Best for:** Legacy code improvement, technical debt reduction, code quality enhancement

### Pipeline 3: Hybrid Development (Analysis + Quality)

```
Problem Statement → problem-analyst → user-story-writer → atdd-developer → code-smell-detector → refactoring-expert
```

**Workflow:**
1. Full feature development pipeline (1-3)
2. Quality review of implemented code (4-5)
3. Iterative improvement

**Best for:** High-quality feature development with built-in quality assurance

## Examples

### Example 1: Tax Calculator Service
Located in `examples/tax-calculator/` - demonstrates the complete feature development pipeline from problem analysis through ATDD implementation.

### Example 2: Legacy Code Refactoring  
Located in `examples/legacy-refactoring/` - shows how to identify code smells and create systematic refactoring plans.

## Getting Started

1. Choose the appropriate pipeline based on your needs
2. Start with the first agent in the pipeline
3. Use each agent's output as input for the next agent
4. Follow the systematic approach for consistent, high-quality results

Each example folder contains detailed documentation showing how to use these agents effectively in practice.