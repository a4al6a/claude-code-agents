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
Generates comprehensive refactoring recommendations based on detected code smells. Provides specific techniques and step-by-step guidance for improving code quality. Includes knowledge of both Martin Fowler's *"Refactoring: Improving the Design of Existing Code"* and Joshua Kerievsky's *"Refactoring to Patterns"*.

**Use when:** You have identified code smells and need expert guidance on refactoring approaches.

#### legacy-code-expert
Specializes in safely modifying legacy code that lacks tests. Based on Michael Feathers' *"Working Effectively with Legacy Code"*, it applies techniques including seam identification, dependency-breaking, and characterization testing strategies.

**Use when:** You need to modify code that has no tests, break dependencies to enable testability, or introduce tests into existing codebases.

#### test-design-reviewer
Evaluates test quality using Dave Farley's testing principles. Scores test suites against eight properties: Understandable, Maintainable, Repeatable, Atomic, Necessary, Granular, Fast, and First (TDD). Produces a weighted "Farley Score" with detailed analysis and improvement recommendations.

**Use when:** You want to assess the quality of your tests, identify flaky or brittle tests, or ensure your test suite follows TDD best practices.

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

### Pipeline 3: Safe Legacy Code Modification (Analysis → Dependency Breaking → Testing)

```
Untested Code → legacy-code-expert → Characterization Tests → Safe Modification
```

**Workflow:**
1. **legacy-code-expert**: Analyzes code for seams, identifies dependency-breaking techniques
2. Apply recommended techniques to create testability
3. Write characterization tests to document existing behavior
4. Make changes safely with test coverage

**Best for:** Modifying code without tests, introducing testability, breaking problematic dependencies

### Pipeline 4: Hybrid Development (Analysis + Quality)

```
Problem Statement → problem-analyst → user-story-writer → atdd-developer → code-smell-detector → refactoring-expert
```

**Workflow:**
1. Full feature development pipeline (1-3)
2. Quality review of implemented code (4-5)
3. Iterative improvement

**Best for:** High-quality feature development with built-in quality assurance

### Pipeline 5: Test Quality Assessment

```
Test Suite → test-design-reviewer → Improvement Plan → Refactored Tests
```

**Workflow:**
1. **test-design-reviewer**: Analyzes test files against Farley's eight properties
2. Review the Farley Score and detailed property breakdown
3. Apply recommendations to improve test quality

**Best for:** Auditing existing test suites, improving flaky tests, ensuring TDD compliance

## Examples

### Example 1: Tax Calculator Service
Located in `examples/tax-calculator/` - demonstrates the complete feature development pipeline from problem analysis through ATDD implementation.

### Example 2: Legacy Code Refactoring
Located in `examples/legacy-refactoring/` - shows how to identify code smells and create systematic refactoring plans for a UserManager class with multiple responsibilities.

### Example 3: Refactoring to Patterns
Located in `examples/refactoring-to-patterns/` - demonstrates applying Joshua Kerievsky's pattern-directed refactoring techniques to an OrderProcessingSystem, showing how to evolve code toward design patterns through safe, incremental steps.

### Example 4: Test Quality Assessment - Low Score
Located in `examples/test-quality-low-score/` - demonstrates an expression parser with poorly designed tests that violate Farley's testing principles, resulting in a low Farley Score. Shows common anti-patterns to avoid.

### Example 5: Test Quality Assessment - High Score
Located in `examples/test-quality-high-score/` - demonstrates the same expression parser with well-designed tests following TDD principles, achieving a high Farley Score. Shows best practices for test design.

## Installation

### Quick Install (All Agents)

```bash
# Clone the repository
git clone https://github.com/yourusername/claude-code-agents.git
cd claude-code-agents

# Install all agents
./install.sh
```

### Installation Options

```bash
# List available agents
./install.sh list

# Check installation status
./install.sh status

# Install all agents (with overwrite prompt for existing)
./install.sh install

# Install all agents (force overwrite without prompts)
./install.sh install -f

# Install a specific agent
./install.sh install code-smell-detector

# Uninstall a specific agent
./install.sh uninstall code-smell-detector

# Uninstall all agents
./install.sh uninstall
```

### Supported Platforms

The installer works on:
- **macOS** (zsh/bash)
- **Linux** (bash)
- **Windows** (Git Bash, WSL)

### Manual Installation

If you prefer manual installation, copy the agent `.md` files to `~/.claude/agents/`:

```bash
mkdir -p ~/.claude/agents
cp code-smell-detector/code-smell-detector.md ~/.claude/agents/
# ... repeat for other agents
```

After installation, restart Claude Code or start a new session to use the agents.

## Getting Started

1. Choose the appropriate pipeline based on your needs
2. Start with the first agent in the pipeline
3. Use each agent's output as input for the next agent
4. Follow the systematic approach for consistent, high-quality results

Each example folder contains detailed documentation showing how to use these agents effectively in practice.