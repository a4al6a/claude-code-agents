# Andrea Laforgia's Claude Code Agents

A collection of specialized agents for software development workflows, designed to work together in pipelines for comprehensive problem-solving and code quality improvement. All agents use the `alf-` namespace prefix.

## What's new (April 2026 refactor)

- **alf-system-auditor** refactored to a thin generic engine that produces a shared **evidence bundle** once, then maps facts to each selected framework via per-framework skills (`framework-sox.md`, `framework-gdpr.md`, etc.). Adding a new framework is now a skill addition, not an agent change.
- **Applicability questionnaires** in `alf-system-auditor` and `alf-accessibility-assessor` — users answer data-type and geography questions, and the agent recommends which frameworks/standards actually apply before the audit runs.
- **Shared design-principles skill** (`_shared-skills/design-principles/`) — one canonical reference for SOLID, GRASP, DRY, KISS, YAGNI, Law of Demeter, etc. Consumed by `alf-clean-coder`, `alf-code-smell-detector`, `alf-refactoring-advisor`, `alf-ddd-assessor`.
- **Deepening pass across all 26 agents** — tool integration (semgrep, osv-scanner, mutation testing, OpenTelemetry semantic conventions), temporal coupling analysis, migration-safety audits, language-specific deep dives (Go goroutine leaks, JVM virtual-threads, Python asyncio patterns), SLSA levels, DORA metric readiness, and more.

See individual agent files for their full scope.

## Agents Overview

### Problem Analysis & Planning Agents

#### alf-problem-analyzer
Analyzes and understands complex problems thoroughly before implementation begins. Creates comprehensive problem domain understanding and identifies core requirements.

**Use when:** You have complex requirements that need deep analysis before development starts.

#### alf-user-story-writer
Breaks down problem statements into granular, implementable user stories using techniques like Elephant Carpaccio. Creates small, independent, valuable, and testable stories.

**Use when:** You need to decompose features into actionable user stories with clear acceptance criteria.

#### alf-atdd-developer
Implements user stories using Acceptance Test Driven Development (ATDD) methodology. Follows the Red-Green-Refactor cycle systematically. During the REFACTOR phase, delegates to the **alf-clean-coder** agent for code quality improvements.

**Use when:** You have user stories with acceptance criteria that need systematic implementation using TDD/BDD practices.

### Code Quality Agents

#### alf-clean-coder
Refactors code to improve readability, maintainability, and structure following clean code principles. Applies SOLID, GRASP, and other design principles to transform working code into clean, well-structured code. Based on Robert C. Martin's *"Clean Code"* principles.

**Use when:** You have working code that needs to be cleaned up, or during the REFACTOR phase of TDD to improve code quality while keeping tests green.

#### alf-code-smell-detector
Analyzes code for potential quality issues, design problems, and maintainability concerns. Identifies patterns like God Objects, violation of SOLID principles, and other code smells.

**Use when:** You want to identify problematic patterns in existing code before refactoring.

#### alf-refactoring-advisor
Generates comprehensive refactoring recommendations based on detected code smells. Provides specific techniques and step-by-step guidance for improving code quality. Includes knowledge of both Martin Fowler's *"Refactoring: Improving the Design of Existing Code"* and Joshua Kerievsky's *"Refactoring to Patterns"*.

**Use when:** You have identified code smells and need expert guidance on refactoring approaches.

#### alf-legacy-code-analyzer
Specializes in safely modifying legacy code that lacks tests. Based on Michael Feathers' *"Working Effectively with Legacy Code"*, it applies techniques including seam identification, dependency-breaking, and characterization testing strategies.

**Use when:** You need to modify code that has no tests, break dependencies to enable testability, or introduce tests into existing codebases.

### Compliance & Auditing Agents

#### alf-system-auditor
Audits software systems against regulatory compliance frameworks, producing one separate audit report per selected regulation. Supports 12 frameworks: SOX, SOC 2, GDPR, HIPAA, PCI DSS 4.0, NIST CSF 2.0, ISO 27001:2022, FedRAMP, CCPA/CPRA, DORA, NIS2, and CMMC 2.0.

**Use when:** You need to assess a codebase's compliance posture against one or more regulatory frameworks.

#### alf-accessibility-assessor
Evaluates software system accessibility compliance against WCAG 2.1/2.2, ADA, Section 508, EN 301 549, EAA, AODA, and other international standards.

**Use when:** You need to assess a codebase's accessibility compliance or identify WCAG violations.

### Codebase Assessment Agents

#### alf-cognitive-load-analyzer
Calculates a Cognitive Load Index (CLI) score (0-1000) for a codebase, measuring how much mental effort it demands from developers. Analyzes 8 dimensions: Structural Complexity, Nesting Depth, Volume/Size, Naming Quality, Coupling, Cohesion, Duplication, and Navigability.

**Use when:** You want to assess how complex a codebase is to understand or identify cognitive hotspots.

#### alf-test-design-reviewer
Evaluates test code quality using Dave Farley's 8 Properties of Good Tests, producing a Farley Index score (0-10) with per-property breakdown, signal evidence, worst offenders, and prioritized recommendations.

**Use when:** You want a quantitative assessment of test suite quality or need to identify test smells.

#### alf-system-explorer
Analyzes codebases and produces narrative-driven, slide-based presentations (Marp Markdown) covering design, architecture, code, testing, and infrastructure. Also validates AI-generated code quality with a Health Score (0-10).

**Use when:** You need to onboard new developers, prepare a system walkthrough presentation, or document a system's architecture.

#### alf-ddd-assessor
Evaluates Domain-Driven Design compliance including bounded contexts, pattern maturity, ubiquitous language consistency, and anti-pattern detection.

**Use when:** You want to assess DDD adoption quality or identify architectural anti-patterns in domain modeling.

### Security & Reliability Agents

#### alf-security-assessor
Detects security vulnerabilities and assesses the overall security hygiene of a codebase. Covers OWASP Top 10, hardcoded secrets, input validation boundaries, authentication/authorization patterns, dependency CVEs, and cryptographic usage.

**Use when:** You need a security audit of your codebase or want to identify vulnerability patterns.

#### alf-error-handling-reviewer
Evaluates exception handling consistency, resilience patterns, and failure mode coverage. Analyzes catch-all patterns, swallowed exceptions, retry/circuit-breaker usage, graceful degradation, and missing error paths.

**Use when:** You want to assess how robustly your code handles failures and edge cases.

### Architecture & Design Agents

#### alf-api-design-reviewer
Evaluates API contracts for consistency, usability, and evolution readiness. Covers REST/GraphQL/gRPC contract consistency, versioning strategy, backward compatibility, error response uniformity, and pagination patterns.

**Use when:** You need to audit API design quality or ensure consistency across endpoints.

#### alf-dependency-auditor
Assesses the health, risk, and hygiene of project dependencies. Covers outdated/abandoned packages, license compliance, transitive dependency risk, duplicate functionality, and supply chain signals.

**Use when:** You want to understand dependency risk or audit license compliance.

#### alf-concurrency-analyzer
Detects concurrency issues, performance anti-patterns, and scalability risks. Covers thread safety, race conditions, async/await correctness, N+1 queries, memory leak patterns, and resource cleanup.

**Use when:** You need to identify concurrency bugs, performance bottlenecks, or scalability risks.

### Maintainability & Evolution Agents

#### alf-documentation-reviewer
Assesses documentation coverage, accuracy, and alignment with code complexity. Covers doc coverage vs complexity gap, stale comments, README accuracy, API doc completeness, ADR presence, and onboarding path quality.

**Use when:** You want to evaluate documentation quality or identify underdocumented areas.

#### alf-dead-code-detector
Finds unreachable code, unused exports, orphaned files, and feature drift. Covers dead branches, unused public APIs, zombie dependencies, stale feature flags, commented-out code blocks, and dead routes.

**Use when:** You want to identify code that can be safely removed to reduce maintenance burden.

#### alf-devops-evaluator
Evaluates CI/CD pipeline quality, build reproducibility, and deployment readiness. Covers pipeline stages, secret management, deployment strategy, containerization hygiene, infrastructure as code, and branch protection.

**Use when:** You need to assess DevOps maturity or identify deployment risks.

### Team & Process Agents

#### alf-ownership-analyzer
Analyzes knowledge distribution, bus factor, and collaboration health using git history. Covers single-contributor files, hotspot analysis (high churn + high complexity), knowledge concentration, orphan code, and team coupling.

**Use when:** You want to identify knowledge silos, bus factor risks, or collaboration bottlenecks.

#### alf-consistency-checker
Evaluates adherence to project conventions, naming patterns, and structural uniformity. Covers naming conventions, project structure patterns, import ordering, logging format uniformity, configuration approach consistency, and pattern adherence.

**Use when:** You want to assess codebase consistency or identify convention violations.

### Domain-Specific Agents

#### alf-data-layer-reviewer
Evaluates database access patterns, schema hygiene, and data layer correctness. Covers migration hygiene, ORM misuse, raw SQL injection risk, transaction boundary correctness, index coverage, and connection management.

**Use when:** You need to audit data access patterns or identify database-related risks.

#### alf-observability-assessor
Assesses logging, tracing, metrics, and monitoring instrumentation for production readiness. Covers logging coverage, structured logging adoption, distributed tracing, metric emission, health check completeness, and alert readiness.

**Use when:** You want to evaluate production observability or identify monitoring gaps.

## Agent Pipelines

### Pipeline 1: Feature Development (Problem > Stories > Implementation)

```
Problem Statement > alf-problem-analyzer > alf-user-story-writer > alf-atdd-developer (> alf-clean-coder)
```

### Pipeline 2: Code Quality Improvement (Detection > Analysis > Refactoring)

```
Legacy Code > alf-code-smell-detector > alf-refactoring-advisor > Implementation
```

### Pipeline 3: Safe Legacy Code Modification

```
Untested Code > alf-legacy-code-analyzer > Characterization Tests > Safe Modification
```

### Pipeline 4: Test Quality Assessment

```
Test Suite > alf-test-design-reviewer > Improvement Plan > Refactored Tests
```

### Pipeline 5: System Understanding

```
Codebase > alf-system-explorer > Slide Deck + Analysis Data
```

### Pipeline 6: Compliance Auditing

```
Codebase > alf-system-auditor > Framework Selection > Per-Framework Reports
```

### Pipeline 7: Accessibility Assessment

```
Codebase > alf-accessibility-assessor > Assessment Report > Fix > Verification
```

## Installation

### Quick Install (All Agents)

```bash
git clone https://github.com/a4al6a/claude-code-agents.git
cd claude-code-agents
./install.sh
```

### Installation Options

```bash
./install.sh list                              # List available agents
./install.sh status                            # Check what's installed
./install.sh install                           # Install all agents (interactive)
./install.sh install -f                        # Install all agents (force overwrite)
./install.sh install alf-code-smell-detector   # Install specific agent
./install.sh uninstall alf-code-smell-detector # Uninstall specific agent
./install.sh uninstall                         # Uninstall all agents
```

### Supported Platforms

- **macOS** (zsh/bash)
- **Linux** (bash)
- **Windows** (Git Bash, WSL)

### Manual Installation

Copy agent `.md` files to `~/.claude/agents/`:

```bash
mkdir -p ~/.claude/agents
cp alf-code-smell-detector/alf-code-smell-detector.md ~/.claude/agents/
# ... repeat for other agents
```

Some agents include skills that must be copied to `~/.claude/skills/`:

```bash
mkdir -p ~/.claude/skills
cp -r alf-cognitive-load-analyzer/skills/alf-cognitive-load-analyzer ~/.claude/skills/
cp -r alf-system-explorer/skills/alf-system-explorer ~/.claude/skills/
cp -r alf-accessibility-assessor/skills/alf-accessibility-assessor ~/.claude/skills/
cp -r alf-system-auditor/skills/software-system-auditor ~/.claude/skills/
cp -r alf-test-design-reviewer/skills/alf-test-design-reviewer ~/.claude/skills/
```

Shared skills (referenced by multiple agents — install once, reused by all) must be installed separately:

```bash
cp -r _shared-skills/design-principles ~/.claude/skills/
```

The shared `design-principles` skill is the canonical reference for SOLID / GRASP / DRY / KISS / YAGNI / Law of Demeter and is consumed by `alf-clean-coder`, `alf-code-smell-detector`, `alf-refactoring-advisor`, and `alf-ddd-assessor`. `install.sh` handles this automatically.

After installation, restart Claude Code or start a new session to use the agents.
